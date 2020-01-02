package entites;

public class ElasticClientConfigData {
    private int port;
    private String scheme;
    private String elasticSearchContainerName;

    public ElasticClientConfigData() {
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
