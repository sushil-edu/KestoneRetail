package com.kestone.kestoneretail.DataHolders;



public class OrdersData {
    String product_name,author_name,distributer_name,quantity,store_name;

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getDistributer_name() {
        return distributer_name;
    }

    public void setDistributer_name(String distributer_name) {
        this.distributer_name = distributer_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
