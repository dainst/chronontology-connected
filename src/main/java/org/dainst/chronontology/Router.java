package org.dainst.chronontology;

import static spark.Spark.*;
import static org.dainst.chronontology.Constants.*;

import org.apache.log4j.Logger;
import org.dainst.chronontology.controller.*;
import spark.Request;
import spark.Response;


/**
 * @author Daniel M. de Oliveira
 */
public class Router {

    private final static Logger logger = Logger.getLogger(Router.class);
    public static final String ID = ":id";

    private final Dispatcher dispatcher;
    private final SearchHandler searchHandler;
    private final ServerStatusHandler serverStatusHandler;
    private final CrudHandler crudHandler;

    private void setUpTypeRoutes(
            final String typeName
    ) {
        get( "/", (req,res) -> {
            setHeader(res);
            return serverStatusHandler.handle(res);
        });
        get( "/" + typeName + "/", (req,res) -> {
            setHeader(res);
            return searchHandler.handle(typeName,req,res);
        });
        get( "/" + typeName + "/" + ID, (req,res) -> {
            setHeader(req,res);
            return crudHandler.handleGet(typeName,req,res);
        });
        post("/" + typeName + "/", (req, res) ->  {
            setHeader(res);
            return crudHandler.handlePost(typeName,req,res);
        });
        put( "/" + typeName + "/" + ID, (req, res) -> {
            setHeader(req,res);
            return crudHandler.handlePut(typeName,req,res);
        });
    }

    private void setHeader(Response res) {
        res.header(HEADER_CT, HEADER_JSON);
    }

    private void setHeader(Request req, Response res) {
        res.header(HEADER_CT, HEADER_JSON);
        res.header(HEADER_LOC, req.params(ID));
    }

    private void setUpAuthorization(String[] credentials) {

        before("/*", (req, res) -> {

            boolean authenticated = false;
            if(req.headers(HEADER_AUTH) != null
                    && req.headers(HEADER_AUTH).startsWith("Basic"))
            {
                for (String cred:credentials)
                    if(decode(req.headers(HEADER_AUTH)).equals(cred)) {
                        req.attribute("user",cred.split(":")[0]);
                        authenticated = true;
                    }
            }

            if(!authenticated && !req.requestMethod().equals("GET")) {
                    res.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
                    res.status(HTTP_UNAUTHORIZED);
                    halt(HTTP_UNAUTHORIZED);
            }
        });
    }

    /**
     * @param toDecode the value of request header "Authorization".
     * @return
     */
    private String decode(String toDecode) {
        return new String(
                java.util.Base64.getDecoder().decode(
                        toDecode.substring("Basic".length()).trim()));
    }

    public Router(
            final Dispatcher dispatcher,
            final String[] typeNames,
            final String[] credentials,
            final RightsValidator rightsValidator
            ){

        this.dispatcher = dispatcher; // TODO review if still necessary
        this.searchHandler= new SearchHandler(dispatcher,rightsValidator);
        this.serverStatusHandler= new ServerStatusHandler(dispatcher,rightsValidator);
        this.crudHandler= new CrudHandler(dispatcher,rightsValidator);

        for (String typeName:typeNames)
            setUpTypeRoutes(typeName);

        setUpAuthorization(credentials);
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    } // TODO review if getters for handlers would make more sense here
}
