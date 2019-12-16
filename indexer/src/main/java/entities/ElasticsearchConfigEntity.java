package entities;

public class ElasticsearchConfigEntity {
    private int port;
    private String scheme;
    private String elasticSearchContainerName;

    public ElasticsearchConfigEntity() {
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
