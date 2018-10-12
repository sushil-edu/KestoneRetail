package com.kestone.kestoneretail.DataHolders;



public class SalesData {



    String product_name,author_name,recommended_order,quantity,store_name;

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

    public String getRecommended_order() {
        return recommended_order;
    }

    public void setRecommended_order(String recommended_order) {
        this.recommended_order = recommended_order;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
