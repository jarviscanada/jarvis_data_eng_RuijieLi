package ca.jrvs.exercise.jdbc;
// this is an exercise file from Linkedin. I made some modifications to make it easier to use
import ca.jrvs.exercise.jdbc.util.DataTransferObject;

public class Customer implements DataTransferObject{
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    Customer() {}
    Customer(
        String firstName, String lastName, String email, String phone, String address, String city, String state, String zipCode
    ) {
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.phone      = phone;
        this.address    = address;
        this.city       = city;
        this.state      = state;
        this.zipCode    = zipCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String toString() {
        return "{" + '\n' + 
        "   id          : " + this.id           + '\n' +
        "   firstName   : " + this.firstName    + '\n' +
        "   lastName    : " + this.lastName     + '\n' +
        "   email       : " + this.email        + '\n' +
        "   phone       : " + this.phone        + '\n' +
        "   address     : " + this.address      + '\n' +
        "   city        : " + this.city         + '\n' +
        "   state       : " + this.state        + '\n' +
        "   zipCode     : " + this.zipCode      + '\n' +
        "}";
    }
}
