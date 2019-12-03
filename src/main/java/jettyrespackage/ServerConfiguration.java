package jettyrespackage;

public class ServerConfiguration {
    private String logMessage;
    private int port;

    public ServerConfiguration(){}

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}
