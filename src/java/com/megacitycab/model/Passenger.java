package com.megacitycab.model;

public class Passenger {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String nic;
    private String address;

<<<<<<< HEAD
 
=======
    // Constructor, Getters, and Setters
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607
    public Passenger(String fullName, String email, String phone, String password, String nic, String address) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.nic = nic;
        this.address = address;
    }

<<<<<<< HEAD

=======
    // Getters and Setters
>>>>>>> 72ae542dfd6364ed1a2fd6a2eb44d3e556980607

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
}
