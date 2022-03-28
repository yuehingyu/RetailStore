/*
 * CosmosDB Host & Access Key are defined as environment variables
 * -- The secrets 
 */
package demo.azure.cosmo.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

@Configuration
public class CosmosClientFactory {

	//@Value("${azure.datasource.host}")
	
	@Value("#{environment.COSMOS_HOST}")
	private String HOST;

	//@Value("${azure.datasource.master_key}")
	
	@Value("#{environment.COSMOS_MASTER_KEY}")
	private String MASTER_KEY;

	@Bean(name = "myCosmoClient")
	@Primary
	public CosmosClient myCosmosClient() {

		
		CosmosClient cosmosClient = new CosmosClientBuilder().endpoint(HOST).key(MASTER_KEY)
				.consistencyLevel(ConsistencyLevel.STRONG).buildClient();

		return cosmosClient;
	}

}
