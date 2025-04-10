package com.surendramaran.yolov9tflite.Models;

import com.google.firebase.database.PropertyName;

public class Users {
    String name, email, search, phone, image, cover,role, onlineStatus, typingTo, experience,working ;

    // Annotation để ánh xạ trường uId với "uid" trong Firebase
    @PropertyName("uid")
    String uId;
    String hocvi;

    public Users() {
        // Required empty constructor for Firebase
    }

    public Users(String name, String email, String search, String phone, String image, String cover, String role, String onlineStatus, String typingTo, String experience, String working, String uId, String hocvi) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.role = role;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.experience = experience;
        this.working = working;
        this.uId = uId;
        this.hocvi = hocvi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        this.working = working;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getHocvi() {
        return hocvi;
    }

    public void setHocvi(String hocvi) {
        this.hocvi = hocvi;
    }
}
