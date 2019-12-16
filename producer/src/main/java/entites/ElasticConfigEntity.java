package entites;

public class ElasticConfigEntity {
    private int port;
    private String scheme;
    private String elasticSearchContainerName;

    public ElasticConfigEntity() {
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
