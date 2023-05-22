package com.example.restaurant.Model;

public class UserNote {
    private String username;
    private String uid;
    private String phone;
    private String rest_name;
    private String city;
    private String Address;

    public UserNote() {
    }

    public UserNote(String username, String uid, String phone, String rest_name, String city, String address) {
        this.username = username;
        this.uid = uid;
        this.phone = phone;
        this.rest_name = rest_name;
        this.city = city;
        Address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
