package com.example.firebase_auth;

public class DataModel
{
    String id;
    String proName;
    String proPrice;
    String proDes;
    String imgUrl;

    public DataModel(String id, String proName, String proPrice,String proDes, String imgUrl) {
        this.id = id;
        this.proName = proName;
        this.proPrice = proPrice;
        this.proDes=proDes;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public DataModel() {
    }

    public String getProDes() {
        return proDes;
    }

    public void setProDes(String proDes) {
        this.proDes = proDes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProPrice() {
        return proPrice;
    }

    public void setProPrice(String proPrice) {
        this.proPrice = proPrice;
    }
}
