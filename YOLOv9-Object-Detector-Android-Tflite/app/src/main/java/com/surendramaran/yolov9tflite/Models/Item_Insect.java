package com.surendramaran.yolov9tflite.Models;

public class Item_Insect {
    int stt;
    String tenvietnam;
    String url;

    public Item_Insect(String url) {
        this.stt=stt;
        //this.tenvietnam = tenvietnam;
        this.url = url;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public String getTenvietnam() {
        return tenvietnam;
    }

    public void setTenvietnam(String tenvietnam) {
        this.tenvietnam = tenvietnam;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
