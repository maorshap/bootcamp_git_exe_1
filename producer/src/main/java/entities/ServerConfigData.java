package entities;

public class ServerConfigData {
    private String logMessage;
    private int port;
    private String kafkaTopicName;

    public String getKafkaTopicName() {
        return kafkaTopicName;
    }

    public ServerConfigData(){}

    public int getPort() {
        return port;
    }

    public String getLogMessage() {
        return logMessage;
    }

}
