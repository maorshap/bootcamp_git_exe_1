package entities;

public class Account {
    private int id;
    private String name;
    private String token;
    private String esIndexName;

    public Account() {
    }

    public Account(String name, String token, String esIndexName) {
        this.name = name;
        this.token = token;
        this.esIndexName = esIndexName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getEsIndexName() {
        return esIndexName;
    }
}
