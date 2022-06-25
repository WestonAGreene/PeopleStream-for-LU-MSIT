package integrationARetriever.dataModels;

public class IntegrationARetrieval {

    String data;

    public IntegrationARetrieval() {
    }

    public IntegrationARetrieval(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }

}
