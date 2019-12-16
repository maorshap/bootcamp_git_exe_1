package entities;

public class ConsumerConfigEntity {
    private String host;
    private int port;
    private String groupId;
    private String topic;

    public ConsumerConfigEntity() {
    }

    public String getHost() {
        return host;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTopic() {
        return topic;
    }

    public int getPort() {
        return port;
    }
}
