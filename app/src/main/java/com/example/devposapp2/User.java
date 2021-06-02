package com.example.devposapp2;

public class User { int id;
    String status ;
    String firstName ;
    String lastName ;
    String email;
    String mobileNum ;
    String resId;
    String userRole;
    String token ;

    public User(String status, String firstName, String lastName, String email, String mobileNum, String resId, String userRole, String token) {

        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNum = mobileNum;
        this.resId = resId;
        this.userRole = userRole;
        this.token = token;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
