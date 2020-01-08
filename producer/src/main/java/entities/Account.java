package entities;

public class Account {
    private int id;
    private String name;
    private String token;
    private String esIndexName;

    public Account() {
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
