package org.dainst.chronontology.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.dainst.chronontology.util.Results;
import org.dainst.chronontology.store.Datastore;
import org.dainst.chronontology.store.SearchableDatastore;
import spark.Request;

import java.io.IOException;


/**
 *
 *
 * @author Daniel M. de Oliveira
 */
public class ConnectController extends Controller {

    protected final Datastore mainDatastore;
    protected final SearchableDatastore connectDatastore;

    public ConnectController(Datastore mainDatastore, SearchableDatastore connectDatastore) {
        this.mainDatastore= mainDatastore;
        this.connectDatastore= connectDatastore;
    }

    @Override
    protected JsonNode _get(String bucket, String key) {
        return mainDatastore.get(bucket,key);
    }

    @Override
    protected void _addDatatoreStatus(Results r) throws IOException {
        r.add(makeDataStoreStatus("main",mainDatastore));
        r.add(makeDataStoreStatus("connect",connectDatastore));
    }

    @Override
    protected void _handlePost(String bucket, String key, JsonNode value) {
        mainDatastore.put(bucket,key, value);
        connectDatastore.put(bucket,key, value);
    }

    @Override
    protected void _handlePut(String bucket, String key, JsonNode value) {
        mainDatastore.put(bucket,key, value);
        connectDatastore.put(bucket,key, value);
    }

    @Override
    protected JsonNode _handleGet(String bucket, String key, Request req) {
        JsonNode result= shouldBeDirect(req.queryParams("direct"))
                ? mainDatastore.get(bucket,key)
                : connectDatastore.get(bucket,key);
        return result;
    }

    @Override
    protected JsonNode _handleSearch(String bucket, String query) {
        return connectDatastore.search( bucket, query );
    }

    private boolean shouldBeDirect(final String directParam) {
        return (directParam!=null&&
                directParam.equals("true"));
    }
}