This demo API is using CosmosDB as backend database
To demo strong consistency on item write, the account that is used to set up CosmosDB database - Retail_Store is configured using strong consistency. The default consistency level for COSMOSDB database is session consistency.
The host and access secret/key needs to be set up as environment variables for local testing and Global environment variables when App is pushed, built and deployed in Azure Red Hat OpenShift environment
Two environment variables name are COSMOS_HOST and COSMOS_MASTER_KEY
