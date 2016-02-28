package org.dainst.chronontology.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel M. de Oliveira
 */
public class SearchHandler {

    private final Controller controller;

    public SearchHandler(Controller controller) {
        this.controller= controller;
    }

    public Object handle(
            final String typeName,
            final Request req,
            final Response res) throws IOException {

        JsonNode searchResults= controller.handleSearch(typeName,req.queryString());

        ArrayNode resultsNode= (ArrayNode) searchResults.get("results");
        removeNodes(resultsNode, indicesToRemove(req, resultsNode));

        return searchResults;
    }

    private void removeNodes(ArrayNode a, List<Integer> indicesToRemove) {
        Collections.reverse(indicesToRemove); // TODO write test
        for (Integer index:indicesToRemove) {
            a.remove(index); // TODO check removal
        }
    }

    private List<Integer> indicesToRemove(Request req, ArrayNode a) {
        List<Integer> indicesToRemove = new ArrayList<Integer>();
        int i=0;
        for (final JsonNode n : a) {
            if (!userPermittedToReadDataset(req,n)) {
                indicesToRemove.add(i);
            }
            i++;
        }
        return indicesToRemove;
    }

    private boolean userPermittedToReadDataset(Request req, JsonNode n) {
        if (n.get("dataset")!=null &&
                !controller.getRightsValidator().hasReaderPermission(req.attribute("user"),
                        n.get("dataset").toString().replaceAll("\"",""))) {
            return false;
        }
        return true;
    }
}
