package org.dainst.chronontology.config;

import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Daniel M. de Oliveira
 */
public class ElasticsearchServerConfig extends Config {

    private static final String MSG_PORT_NAN = "Embedded Elasticsearch server port must be a number, but is: ";

    private String port= null;
    private String dataPath= null;
    private String clusterName= null;

    public ElasticsearchServerConfig() {
        this.prefix= "esServer.";
    }

    @Override
    public boolean validate(Properties props) {
        return (
            _validate(props,"port", ConfigConstants.EMBEDDED_ES_PORT) &&
            _validate(props,"dataPath", ConfigConstants.ES_SERVER_DATA_PATH) &&
            _validate(props,"clusterName", ConfigConstants.ES_SERVER_CLUSTER_NAME)
        );
    }

    @Override
    public ArrayList<String> getConstraintViolations() {
        return constraintViolations;
    }

    public String getPort() {
        return port;
    }

    void setPort(String port) {
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new ConfigValidationException(MSG_PORT_NAN+"\""+port+"\".");
        }
        this.port= port;
    }

    void setDataPath(String dataPath) {
        this.dataPath= dataPath;
    }

    void setClusterName(String clusterName) {
        this.clusterName= clusterName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getDataPath() {
        return dataPath;
    }
}
