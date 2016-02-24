package com.thracecodeinc.shopcalculator;


/**
 * Created by sami on 2/16/16.
 */
public class ModelClass {
    private String kind;
    private String price;
    private String quantity;
    private String total;

    public ModelClass(String kind, String price, String quantity, String total) {
        this.kind = kind;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }

    public ModelClass(){
        this.kind = "";
        this.price = "";
        this.quantity = "";
        this.total = "";
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
