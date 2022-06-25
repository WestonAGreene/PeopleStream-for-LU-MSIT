package integrationARetriever.dataModels;

public class IntegrationB {

    String data;

    public IntegrationB() {
    }

    public IntegrationB(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }

}
