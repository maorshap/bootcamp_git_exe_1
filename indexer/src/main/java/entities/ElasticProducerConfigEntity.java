package entities;

public class ElasticProducerConfigEntity {
    private String documentType;
    private String indexName;

    public ElasticProducerConfigEntity() {
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getIndexName() {
        return indexName;
    }
}
