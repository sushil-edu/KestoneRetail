
package com.kestone.kestoneretail.DataHolders;

import com.google.gson.annotations.SerializedName;

public class PopUp {

    @SerializedName("Backlist")
    private String mBacklist;
    @SerializedName("FaceUp")
    private String mFaceUp;
    @SerializedName("NewArrival")
    private String mNewArrival;
    @SerializedName("PJPDate")
    private String mPJPDate;
    @SerializedName("RefStoreID")
    private String mRefStoreID;
    @SerializedName("RefUserID")
    private String mRefUserID;
    @SerializedName("StoreFaceList")
    private String mStoreFaceList;

    @SerializedName("StockReason")
    private String mStockReason;
    @SerializedName("OrderReason")
    private String mOrderReason;

    public String getBacklist() {
        return mBacklist;
    }

    public void setBacklist(String backlist) {
        mBacklist = backlist;
    }

    public String getFaceUp() {
        return mFaceUp;
    }

    public void setFaceUp(String faceUp) {
        mFaceUp = faceUp;
    }

    public String getNewArrival() {
        return mNewArrival;
    }

    public void setNewArrival(String newArrival) {
        mNewArrival = newArrival;
    }

    public String getPJPDate() {
        return mPJPDate;
    }

    public void setPJPDate(String pJPDate) {
        mPJPDate = pJPDate;
    }

    public String getRefStoreID() {
        return mRefStoreID;
    }

    public void setRefStoreID(String refStoreID) {
        mRefStoreID = refStoreID;
    }

    public String getRefUserID() {
        return mRefUserID;
    }

    public void setRefUserID(String refUserID) {
        mRefUserID = refUserID;
    }

    public String getStoreFaceList() {
        return mStoreFaceList;
    }

    public void setStoreFaceList(String storeFaceList) {
        mStoreFaceList = storeFaceList;
    }

    public String getStockReason() {
        return mStockReason;
    }

    public void setStockReason(String mStockReason) {
        this.mStockReason = mStockReason;
    }

    public String getOrderReason() {
        return mOrderReason;
    }

    public void setOrderReason(String mOrderReason) {
        this.mOrderReason = mOrderReason;
    }
}
