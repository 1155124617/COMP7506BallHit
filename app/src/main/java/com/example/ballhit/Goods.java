package com.example.ballhit;

public class Goods {
    private String func;
    private String price;
    private int img_id;

    public Goods(String func, String price, int img_id){
        this.func = func;
        this.price=price;
        this.img_id=img_id;
    }

    public String getFunc(){
        return this.func;
    }

    public String getPrice(){
        return this.price;
    }

    public int getImg_id(){
        return this.img_id;
    }
}
