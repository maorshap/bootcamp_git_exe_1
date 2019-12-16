package entites;

public class ServerConfigEntity {
    private String logMessage;
    private int port;

    public ServerConfigEntity(){}

    public int getPort() {
        return port;
    }

    public String getLogMessage() {
        return logMessage;
    }

}
