package entities;

public class EsClientConfigData {
    private int port;
    private String scheme;
    private String elasticSearchContainerName;

    public EsClientConfigData() {
    }

    public String getElasticSearchContainerName() {
        return elasticSearchContainerName;
    }

    public int getPort() {
        return port;
    }

    public String getScheme() {
        return scheme;
    }
}
