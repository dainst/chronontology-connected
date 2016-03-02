package org.dainst.chronontology.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.dainst.chronontology.JsonTestUtils.jsonAssertEquals;
import static org.dainst.chronontology.util.JsonUtils.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Daniel M. de Oliveira
 */
public class DocumentModelTest {

    public static final String ADMIN = "admin";

    /**
     * Produces a node whose modified dates array
     * is a merge of both the arguments nodes created dates.
     * @param old
     * @param niew
     * @return
     */
    private JsonNode nodeWithModifiedDates(JsonNode old, JsonNode niew) {
        ObjectNode example= (ObjectNode) json();
        ArrayNode a = example.putArray(DocumentModel.MODIFIED);
        a.add(old.get(DocumentModel.MODIFIED).get(0));
        a.add(niew.get(DocumentModel.MODIFIED).get(0));
        return example;
    }

    private JsonNode makeNodeWithVersion(int version) {
        JsonNode example= json();
        ((ObjectNode)example).put(DocumentModel.VERSION,version);
        return example;
    }

    @Test
    public void createdDateStaysSame() throws IOException, InterruptedException {
        DocumentModel old=
                new DocumentModel("1",json(), ADMIN);
        Thread.sleep(10);
        DocumentModel dm=
                new DocumentModel("1",json(), ADMIN);

        jsonAssertEquals(
                dm.merge(old).j().get(DocumentModel.CREATED),
                old.j().get(DocumentModel.CREATED));
    }

    @Test
    public void modifiedDatesMerge() throws IOException, InterruptedException, JSONException {
        DocumentModel old=
                new DocumentModel("1",json(), ADMIN);
        Thread.sleep(10);
        DocumentModel dm=
                new DocumentModel("1",json(), ADMIN);

        JsonNode nodeWithDates = nodeWithModifiedDates(old.j(), dm.j());

        jsonAssertEquals(
                dm.merge(old).j(),
                nodeWithDates);
    }

    @Test
    public void setVersionOnCreate() throws IOException, InterruptedException, JSONException {

        jsonAssertEquals(
                new DocumentModel("1",json(), ADMIN).j(),
                makeNodeWithVersion(1));
    }

    @Test
    public void setCreateUserOnCreate() throws IOException {
        jsonAssertEquals(
                new DocumentModel("1",json(), ADMIN).j()
                        .get(DocumentModel.CREATED),
                json("{ \"user\" : \""+ADMIN+"\" }"));
    }

    @Test
    public void setModifiedUserOnCreate() throws IOException {
        jsonAssertEquals(
                new DocumentModel("1",json(), ADMIN).j().
                        get(DocumentModel.MODIFIED).get(0),
                json("{\"user\":\""+ADMIN+"\"}"));
    }

    @Test
    public void differentUserOnModify() throws IOException {
        DocumentModel old=
                new DocumentModel("1",json(), ADMIN);
        DocumentModel dm=
                new DocumentModel("1",json(), "ove");

        jsonAssertEquals(
                dm.merge(old).j().
                        get(DocumentModel.MODIFIED).get(1),
                json("{\"user\":\"ove\"}"));
    }


    @Test
    public void countVersions() throws IOException, InterruptedException, JSONException {
        DocumentModel old=
                new DocumentModel("1",json(), ADMIN);
        DocumentModel dm=
                new DocumentModel("1",json(), ADMIN);

        jsonAssertEquals(dm.merge(old).j(), makeNodeWithVersion(2));
    }

    @Test
    public void createFromOld() {
        JsonNode n= json();
        ((ObjectNode)n).put(DocumentModel.ID,"1");
        ((ObjectNode)n).put(DocumentModel.CREATED,json("{\"date\":\"today\"}"));

        DocumentModel dm= DocumentModel.from(n);
        assertEquals(dm.getId(),"1");
        assertEquals(dm.j().get(DocumentModel.CREATED),json("{\"date\":\"today\"}"));
    }

    @Test
    public void createFromOldNull() {
        assertEquals(DocumentModel.from(null),null);
    }

    @Test
    public void getDataset() {
        JsonNode n= json();
        ((ObjectNode)n).put(DocumentModel.ID,"1");
        ((ObjectNode)n).put(DocumentModel.DATASET,"1");

        DocumentModel dm= DocumentModel.from(n);
        assertEquals(dm.getDataset(),"1");
    }

    @Test
    public void noDataset() {
        JsonNode n= json();
        ((ObjectNode)n).put(DocumentModel.ID,"1");

        DocumentModel dm= DocumentModel.from(n);
        assertEquals(dm.getDataset(),null);
    }
}
