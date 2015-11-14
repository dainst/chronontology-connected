# chronontology-connected

## Using the server

Execution of the main method starts an embedded 
jetty server.

The application uses two datastores. The main datastore is
file system based. The connected datastore is an elastisearch instance.

At startup, the application reads the properties from 

  config.properties

Make sure you revise the settings before startup!

A type mapping for every type used is needed, so make sure 
you didn't forget to add the mapping to the period type!
 
  type mapping : src/main/resources/mapping.json

### POST /:typeName/

Post json to store a a document of type :typeName.
After posting, the location header will contain the id of 
the created element, if successful. The id will be a string like

  /period/TAvlBuaAasWM
  
That means, it will contain both the type name as well as a 
base64 encoded random part.

The response body will contain the json in the form it got send
to the stores. This means that it will be enriched by id an date information.

  Status codes: 
    201 if created successfully.

### GET /:typeName/:id

Get json stored for type with name :typeName and id :id.

### GET /:typeName/:id?direct=true

Get json stored for type with name :typeName and id :id. the json is retrieved from 
the main storage this time, not from the connected storage.

### GET /:typeName/:elasticsearchSearchString

Performs a search specified by :elasticsearchSearchString 
over the documents of the type named :typeName.

Gets a json object with a top level array field named results which
contains the json for the search hits. 

The elasticsearchSearchString should be a valid search string for elasticsearch
and should not include the "_search?" prefix but everything after it.

## Testing

For the Component and Integration Tests to run, you need an elastic search 
instance on localhost up and running. The index named "jeremy_test" 
is used. Make sure that the index has the type mapping from

  src/test/resources/mapping.json
