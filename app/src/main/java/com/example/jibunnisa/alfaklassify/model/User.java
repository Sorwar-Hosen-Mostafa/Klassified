package com.example.jibunnisa.alfaklassify.model;

import java.io.Serializable;

/**
 * Created by Alamgir on 05/13/2017.
 */

public class User implements Serializable{
    private String userId;
    private String fullName;
    private String imageUrl;
    private String contactNo;
    private String email;
    private String address;

    public User() {
    }

    public User(String userId, String fullName, String imageUrl,String email, String contactNo, String address) {
        this.userId = userId;
        this.fullName = fullName;
        this.email= email;
        this.imageUrl = imageUrl;
        this.contactNo = contactNo;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrl() {
        if(imageUrl == null){
            imageUrl = "";
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContactNo() {
        if(contactNo == null){
            contactNo = "";
        }
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        if(address == null){
            address = "";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
