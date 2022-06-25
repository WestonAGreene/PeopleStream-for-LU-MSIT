package peopleStream.dataModels;

public class IntegrationA {

    String data;

    public IntegrationA() {
    }

    public IntegrationA(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }

}
