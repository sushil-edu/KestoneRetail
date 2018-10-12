package com.kestone.kestoneretail.DataHolders;

/**
 * Created by user on 06/05/17.
 */

public class StoreNameData {
    String id,store_name,StoreLongitude,StoreLatitude, StoreCategory;


    public String getId() {
        return id;
    }

    public String getStoreLongitude() {
        return StoreLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        StoreLongitude = storeLongitude;
    }

    public String getStoreLatitude() {
        return StoreLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        StoreLatitude = storeLatitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStoreCategory() {
        return StoreCategory;
    }

    public void setStoreCategory(String storeCategory) {
        StoreCategory = storeCategory;
    }
}
