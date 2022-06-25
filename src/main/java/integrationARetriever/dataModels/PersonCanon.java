package integrationARetriever.dataModels;

public class PersonCanon {

    String data;

    public PersonCanon() {
    }

    public PersonCanon(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }

}
