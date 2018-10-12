package com.kestone.kestoneretail.DatabasePackage;


public class Reporting {
    String category, author, bookname, stock, sales, order, pjpId, bookId, storeId, date, distributor;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    int id, count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Reporting(String pjpId, String category, String author, String bookname, String stock, String sales, String order, String bookId,
                     String storeId, String date, String distributor) {

        this.id = id;
        this.pjpId = pjpId;
        this.category = category;
        this.author = author;
        this.bookname = bookname;
        this.stock = stock;
        this.sales = sales;
        this.order = order;
        this.bookId = bookId;
        this.distributor = distributor;
        this.storeId = storeId;
        this.date = date;

    }


    public Reporting() {

    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPjpId() {
        return pjpId;
    }

    public void setPjpId(String pjpId) {
        this.pjpId = pjpId;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
