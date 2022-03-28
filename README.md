This demo-RetailStore API is using CosmosDB as backend database 
The default consistency level for COSMOSDB database is session consistency. 
To demo strong consistency on item write in CosmosDB, service account which creates the database - Retail_Store needs to be configured with strong consistency. 
The host and access secret/key needs to be set up as environment variables for local testing
The host and access secret/key needs to be set up as Global variables need to be set up in Azure Red Hat OpenShift cluster environment 
Environment variable names are COSMOS_HOST and COSMOS_MASTER_KEY