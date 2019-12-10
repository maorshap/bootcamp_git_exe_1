package Dao;

public class ServerConfiguration {
    private String logMessage;
    private int port;
    private String elasticSearchContainerName;


    public ServerConfiguration(){}


    public String getElasticSearchContainerName() {
        return elasticSearchContainerName;
    }


    public int getPort() {
        return port;
    }


    public String getLogMessage() {
        return logMessage;
    }

}
