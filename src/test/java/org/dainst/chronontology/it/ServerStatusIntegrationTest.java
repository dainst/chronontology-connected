package org.dainst.chronontology.it;

import com.fasterxml.jackson.databind.JsonNode;
import org.dainst.chronontology.Constants;
import org.dainst.chronontology.store.ESServerTestUtil;
import org.dainst.chronontology.handler.model.Results;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.dainst.chronontology.JsonTestUtils.jsonAssertEquals;
import static org.dainst.chronontology.util.JsonUtils.json;
/**
 * @author Daniel M. de Oliviera
 */
public class ServerStatusIntegrationTest extends IntegrationTest {

    private JsonNode dataStoresJson(String status) {
        Results datastores = new Results("datastores");
        datastores.add(json("{ \"role\" : \"main\", \"status\" : \""+ Constants.DATASTORE_STATUS_OK+"\" }"));
        datastores.add(json("{ \"role\" : \"connect\", \"status\" : \""+status+"\" }"));
        return datastores.j();
    }

    @Test
    public void getServerStatus() throws IOException, InterruptedException {

        jsonAssertEquals(
                client.get("/"),
                dataStoresJson(Constants.DATASTORE_STATUS_OK)
        );
    }

    /**
     * Note that that it might cause problems at some point when
     * this test finishes while es is down. Then other tests of this
     * class might not get executed properly.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testESisDown() throws IOException, InterruptedException {

        ESServerTestUtil.stopElasticSearchServer();

        jsonAssertEquals(
                client.get("/"),
                dataStoresJson(Constants.DATASTORE_STATUS_DOWN)
        );

        ESServerTestUtil.startElasticSearchServer();

        jsonAssertEquals(
                client.get("/"),
                dataStoresJson(Constants.DATASTORE_STATUS_OK)
        );
    }
}