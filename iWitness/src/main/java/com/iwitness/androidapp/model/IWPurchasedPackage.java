package com.iwitness.androidapp.model;

import com.perpcast.lib.codec.FwiCodec;
import com.perpcast.lib.foundation.FwiJson;

public class IWPurchasedPackage {

    private String purchasedData;
    private String signature;
    private String purchasedTime;
    private String productId;
    private String orderId;
    private String orderNumber;
    private String purchasedToken;

    public String getPurchasedData() {
        return this.purchasedData;
    }

    public String getPurchasedToken() { return this.purchasedToken; }

    public String getSignature() {
        return this.signature;
    }

    public String getProductId() {
        return this.productId;
    }

    public String getPurchasedTime() {
        return this.purchasedTime;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public IWPurchasedPackage(String purchasedData, String signature) {
        FwiJson data = FwiCodec.convertDataToJson(purchasedData);
        String[] transactions = data.jsonWithPath("orderId").getString().split("\\.\\.");

        this.purchasedData = purchasedData;
        this.productId = data.jsonWithPath("productId").getString();
        this.purchasedToken = data.jsonWithPath("purchaseToken").getString();
        this.purchasedTime = data.jsonWithPath("purchaseTime").getString().substring(0, 10);
        this.orderId = transactions[0];
        this.orderNumber = transactions.length > 1 ? transactions[1] : "0";
        this.signature = signature;
    }
}
