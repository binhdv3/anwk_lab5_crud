package com.example.binhdv35.lab5.models;

public class Product {
    private String id,name, des;
    private int price;

    public Product(String id, String name, String des, int price) {
        this.id = id;
        this.name = name;
        this.des = des;
        this.price = price;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
