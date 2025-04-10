package com.surendramaran.yolov9tflite.Models;

public class Insect {
    private int insects_id;
    private String name;
    private String ename;
    private String characteristic;
    private String distribution;
    private String behavior;
    private String protection_method;
    private String thumbnail;

    public Insect(int insects_id, String name, String ename, String characteristic, String distribution, String behavior, String protection_method, String thumbnail) {
        this.insects_id = insects_id;
        this.name = name;
        this.ename = ename;
        this.characteristic = characteristic;
        this.distribution = distribution;
        this.behavior = behavior;
        this.protection_method = protection_method;
        this.thumbnail = thumbnail;
    }

    public int getInsects_id() {
        return insects_id;
    }

    public void setInsects_id(int insects_id) {
        this.insects_id = insects_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public String getProtection_method() {
        return protection_method;
    }

    public void setProtection_method(String protection_method) {
        this.protection_method = protection_method;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
