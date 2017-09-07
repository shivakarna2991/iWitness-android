package com.android.vending.billing;


import org.codehaus.jackson.map.*;

import java.io.*;


public class Purchase implements Serializable {


    // Class's static controller
    static public Purchase purchaseWithInfo(String purchaseInfo, String itemType, String signature) {
        Purchase purchase = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            purchase = (Purchase) mapper.readValue(purchaseInfo, Purchase.class);

            purchase.setItemType(itemType);
            purchase.setSignature(signature);
            purchase.setPurchaseInfo(purchaseInfo);
        }
        catch (Exception ex) {
            purchase = null;
        }
        finally {
            return purchase;
        }
    }


    // Global variables
    private String orderId;
    private String productId;
    private String packageName;

    private long   purchaseTime;
    private int    purchaseState;
    private String purchaseToken;
    private String developerPayload;

    private String itemType;
    private String signature;
    private String purchaseInfo;


    // Class's constructors
    private Purchase() {
    }


    // Class's properties
    public String getOrderId() {
        return orderId;
    }
    public String getProductId() {
        return productId;
    }
    public String getPackageName() {
        return packageName;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }
    public int getPurchaseState() {
        return purchaseState;
    }
    public String getPurchaseToken() {
        return purchaseToken;
    }
    public String getDeveloperPayload() {
        return developerPayload;
    }

    public String getItemType() {
        return itemType;
    }

    public String getSignature() {
        return signature;
    }


    // Class's private methods
    private void setItemType(String itemType) {
        this.itemType = itemType;
    }
    private void setSignature(String signature) {
        this.signature = signature;
    }
    private void setPurchaseInfo(String purchaseInfo) {
        this.purchaseInfo = purchaseInfo;
    }
}
