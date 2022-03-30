package demo.azure.cosmo.sale.crud;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.implementation.Utils;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import demo.azure.cosmo.common.keys;
import demo.azure.cosmo.entity.SalesOrder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("demo.azure.cosmo.api")
public class SaleDocDbDao implements SalesDao {

	@Autowired
	CosmosClient cosmosClient;

	@Value("${azure.cosmo.database.sales}")
	private String DATABASE_ID;

	@Value("${azure.cosmo.database.sales.container}")
	private String CONTAINER_ID;

	// The Cosmos DB database
	private static CosmosDatabase cosmosDatabase = null;

	// The Cosmos DB container
	private static CosmosContainer cosmosContainer = null;

	private static final ObjectMapper OBJECT_MAPPER = Utils.getSimpleObjectMapper();

	@Override
	public SalesOrder createSaleItem(SalesOrder saleItem) {

		CosmosItemResponse<SalesOrder> item = null;

		String uuid = UUID.randomUUID().toString().toUpperCase();

		saleItem.setId(uuid);
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		
		saleItem.setOrder_date(dateFormat.format(date));
		
		
		saleItem.setPurchase_order_number("P"+uuid);		

		try {

			CosmosContainer container = getContainerCreateResourcesIfNotExist();

			CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();

			// set to true to get response payload, default is null

			cosmosItemRequestOptions.setContentResponseOnWriteEnabled(true);

			// item = container.createItem(saleItem, cosmosItemRequestOptions);

			item = container.upsertItem(saleItem, cosmosItemRequestOptions);

		} catch (CosmosException e) {
			System.out.println("Error creating Sale item.\n");
			e.printStackTrace();
			return null;
		}

		return item.getItem();

	}

	@Override
	public SalesOrder readSaleItem(String id) {
		// Retrieve the document by id using our helper method.
		JsonNode saleItemJson = getDocumentById(id);

		if (saleItemJson != null) {
			// De-serialize the document in to a saleItem.
			try {
				return OBJECT_MAPPER.treeToValue(saleItemJson, SalesOrder.class);
			} catch (JsonProcessingException e) {
				System.out.println("Error deserializing read Sale item.\n");
				e.printStackTrace();

				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public List<SalesOrder> readSaleItems(String pkey, Enum<?> key_type) {

		List<SalesOrder> saleItems = new ArrayList<SalesOrder>();
		String sql = null;

		if (key_type == keys.ACCOUNTID) {
			sql = "SELECT * FROM root r WHERE r.accountid ='" + pkey + "'";
		} else {
			sql = "SELECT * FROM root r WHERE r.partyid ='" + pkey + "'";
		}

		int maxItemCount = 1000;
		int maxDegreeOfParallelism = 1000;
		int maxBufferedItemCount = 100;

		CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
		options.setMaxBufferedItemCount(maxBufferedItemCount);
		options.setMaxDegreeOfParallelism(maxDegreeOfParallelism);
		options.setQueryMetricsEnabled(false);

		int error_count = 0;
		int error_limit = 10;

		String continuationToken = null;
		do {

			for (FeedResponse<JsonNode> pageResponse : getContainerCreateResourcesIfNotExist()
					.queryItems(sql, options, JsonNode.class).iterableByPage(continuationToken, maxItemCount)) {

				continuationToken = pageResponse.getContinuationToken();

				for (JsonNode item : pageResponse.getElements()) {

					try {
						saleItems.add(OBJECT_MAPPER.treeToValue(item, SalesOrder.class));
					} catch (JsonProcessingException e) {
						if (error_count < error_limit) {
							error_count++;
							if (error_count >= error_limit) {
								System.out.println("\n...reached max error count.\n");
							} else {
								System.out.println(
										"Error deserializing Sale item JsonNode. " + "This item will not be returned.");
								e.printStackTrace();
							}
						}
					}

				}
			}

		} while (continuationToken != null);

		return saleItems;
	}

	@Override
	public SalesOrder updateSaleItem(SalesOrder saleItem) {
		// Retrieve the document from the database

		JsonNode saleItemJson = OBJECT_MAPPER.valueToTree(saleItem);

		String id = saleItem.getId();
		String key = saleItem.getAccountid();

		try {
			// Persist/replace the updated document.

			// CosmosItemRequestOptions options= new CosmosItemRequestOptions();
			// options.setIfMatchETag(key)
			saleItemJson = getContainerCreateResourcesIfNotExist()
					.replaceItem(saleItemJson, id, new PartitionKey(key), new CosmosItemRequestOptions()).getItem();
		} catch (CosmosException e) {
			System.out.println("Error updating Order item.\n");
			// e.printStackTrace();
			return null;
		}

		// De-serialize the document in to a saleItem.
		try {
			return OBJECT_MAPPER.treeToValue(saleItemJson, SalesOrder.class);
		} catch (JsonProcessingException e) {
			System.out.println("Error deserializing updated item.\n");
			// e.printStackTrace();

			return null;
		}
	}

	@Override
	public boolean deleteSaleItem(String id) {
		// CosmosDB refers to documents by self link rather than id.

		// Query for the document to retrieve the self link.

		SalesOrder saleItem = readSaleItem(id);

		if (saleItem == null)
			return false;

		try {

			// Delete the document by self link.

			CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();

			// set to true to get response payload, default is null

			cosmosItemRequestOptions.setContentResponseOnWriteEnabled(true);

			CosmosContainer container = getContainerCreateResourcesIfNotExist(); // need

			container.deleteItem(saleItem, cosmosItemRequestOptions);

		} catch (CosmosException e) {
			System.out.println("Error deleting Order item.\n");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private CosmosContainer getContainerCreateResourcesIfNotExist() {

		try {

			if (cosmosDatabase == null) {
				CosmosDatabaseResponse cosmosDatabaseResponse = cosmosClient.createDatabaseIfNotExists(DATABASE_ID);
				cosmosDatabase = cosmosClient.getDatabase(cosmosDatabaseResponse.getProperties().getId());
			}

		} catch (CosmosException e) {
			// TODO: Something has gone terribly wrong - the app wasn't
			// able to query or create the collection.
			// Verify your connection, endpoint, and key.
			System.out
					.println("Something has gone terribly wrong - " + "the app wasn't able to create the Database.\n");
			e.printStackTrace();
		}

		try {

			if (cosmosContainer == null) {
				CosmosContainerProperties properties = new CosmosContainerProperties(CONTAINER_ID, "/id");
				CosmosContainerResponse cosmosContainerResponse = cosmosDatabase.createContainerIfNotExists(properties);
				cosmosContainer = cosmosDatabase.getContainer(cosmosContainerResponse.getProperties().getId());
			}

		} catch (CosmosException e) {
			// TODO: Something has gone terribly wrong - the app wasn't
			// able to query or create the collection.
			// Verify your connection, endpoint, and key.
			System.out
					.println("Something has gone terribly wrong - " + "the app wasn't able to create the Container.\n");
			e.printStackTrace();
		}

		return cosmosContainer;
	}

	private JsonNode getDocumentById(String id) {

		String sql = "SELECT * FROM root r WHERE r.id='" + id + "'";
		int maxItemCount = 1000;
		int maxDegreeOfParallelism = 1000;
		int maxBufferedItemCount = 100;

		CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
		options.setMaxBufferedItemCount(maxBufferedItemCount);
		options.setMaxDegreeOfParallelism(maxDegreeOfParallelism);
		options.setQueryMetricsEnabled(false);

		List<JsonNode> itemList = new ArrayList();

		String continuationToken = null;
		do {
			for (FeedResponse<JsonNode> pageResponse : getContainerCreateResourcesIfNotExist()
					.queryItems(sql, options, JsonNode.class).iterableByPage(continuationToken, maxItemCount)) {

				continuationToken = pageResponse.getContinuationToken();

				for (JsonNode item : pageResponse.getElements()) {
					itemList.add(item);
				}
			}

		} while (continuationToken != null);

		if (itemList.size() > 0) {
			return itemList.get(0);
		} else {
			return null;
		}
	}

}