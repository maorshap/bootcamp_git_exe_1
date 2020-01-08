package entities;

public class KafkaProducerConfigData {
    private String host;
    private String id;
    private int port;

    public KafkaProducerConfigData() {
    }

    public String getHost() {
        return host;
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }
}
