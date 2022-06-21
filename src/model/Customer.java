package model;

/**
 * Customer Object model
 * use this to setup a new customer
 */
public class Customer {
    private int ID;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String country;
    private String division;
    private int divisionID;

    public Customer(int ID, String name,String address, String postalCode, String phoneNumber, String country, String division, int divisionID){
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.division = division;
        this.divisionID = divisionID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {this.division = division;}

    public int getDivisionID() {return divisionID;}

    public void setDivisionID(int divisionID) {this.divisionID = divisionID;}
}
