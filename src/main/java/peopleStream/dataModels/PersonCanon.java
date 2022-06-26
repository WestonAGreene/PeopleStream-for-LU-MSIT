package peopleStream.dataModels;

public class PersonCanon {

    String data;
    Boolean diffValueFromTable;

    public PersonCanon() {
    }

    public PersonCanon(String data) {
        this.data = data;
    }

    public PersonCanon(String data, Boolean diffValueFromTable) {
        this.data = data;
        this.diffValueFromTable = diffValueFromTable;
    }

    public String getData() {
        return data;
    }

    public Boolean getDiffValueFromTable() {
        return diffValueFromTable;
    }

    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }

}
