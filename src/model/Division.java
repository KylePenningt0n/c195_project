package model;

public class Division {
    private String name;
    private int countryID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public Division(String name, int countryID){
        this.name = name;
        this.countryID = countryID;
    }
}
