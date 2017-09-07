package com.android.vending.billing;


import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetails {

    // Class's static controller
    static public SkuDetails skuWithInfo(String skuInfo) {
        return SkuDetails.detailsWithInfo(skuInfo, IabHelper.ITEM_TYPE_SUBS);
    }
    static public SkuDetails detailsWithInfo(String skuInfo, String itemType) {
        SkuDetails skuDetail = new SkuDetails();
        JSONObject jresponse = null;
        try {
            jresponse = new JSONObject(skuInfo);
            String productId = jresponse.getString("productId");
            String title = jresponse.getString("title");
            String price = jresponse.getString("price");
            String description = jresponse.getString("description");

            skuDetail.productId = productId;
            skuDetail.price = price;
            skuDetail.title = title;
            skuDetail.description = description;

            skuDetail.setSkuDetails(skuInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return skuDetail;
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            skuDetail = mapper.readValue(skuInfo, SkuDetails.class);
//
//            if (itemType != null && itemType.length() > 0) {
//                skuDetail.setItemType(itemType);
//            }
//
//            skuDetail.setSkuDetails(skuInfo);
//        }
//        catch (Exception ex) {
//            Log.e("exce..................",ex.toString());
//            skuDetail = null;
//        }
//        finally {
//            return skuDetail;
//        }
    }

    // Global variables
    private String productId;
    private String type;
    private String price;
    private String title;
    private String description;

    private String itemType;
    private String skuDetails;


    // Class's constructors
    public SkuDetails() {
    }


    // Class's properties
    public String getProductId() { return productId; }
    public String getType() { return type; }
    public String getPrice() { return price; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }


    // Class's private methods
    private void setItemType(String itemType) {
        this.itemType = itemType;
    }
    private void setSkuDetails(String skuDetails) {
        this.skuDetails = skuDetails;
    }
}
