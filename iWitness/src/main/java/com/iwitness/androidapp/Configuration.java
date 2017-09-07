package com.iwitness.androidapp;


public final class Configuration {

    static private final String kHostProtocol = "https";
    static public final String kHostname = "api.iwitness.com";

    // http://api.balukiran.com/
//    static public final String kHostname ="api.balukiran.com";
//    static private final String kHostProtocol = "http";

    static public final int GOOGLE_PLAY_API_VERSION = 3;
    static public final double US_Latitude = 41.850033;
    static public final double US_Logitude = -87.6500523;
    static public final String senderId = "325547727416";
    static public final String model = "Android";
    public static final String regId = "regId";
    // static public final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnQEMIgugnsfl6jdb05FBKE//2rCc2wgUnq26S8pMV6LrJKT+IP2Sv1t/4IfvGu2VxSdK2Kzqe9FYNstfY/ovyQ9oKt1gddOTDq1TEuZ0Pv3TTDXGGMRjQcbC2rPrsloekMPt8SKRHC8XaU9XmLEvZUOmJlI2rkTPTe6pAevSfOGRO4LADiCFjmfpP69HGGPZjc6VyxbsbmWkzF2Xg8PdRox4/KnlB59Wr7vZ5j9/g18izWQCFM3cHTX169Am5uAY8YG5EjGS/kUtjV2vUOsq9r0OFQ96hZ2Nk6nOWIAHtVieByx/rbgm1JGuMhNvgNqhxCdpCy+an8QPMVhoVdzaAQIDAQAB";
    static public final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo2pUlfPZuU/xNi8+wb35PsOjNbCsZnJDdWInYzoLKHiQx7yQtNjjxk344c3NpkjAmDYaC6mEX3Ibgfalc5/yVPKiR5qIJEo6XCfj/pWr5TmC8ZphLpI0LUdd3rMQm7atHFomfV0/WE1pr2deSFgrO+ehaGLNa8USxdpcSWHfbLpN52q3W07zU1HnoToOaSN0fGHuKKxZaNvUuziLxprQVTqylwHW8v8thEuamNhpvtLqDyHrFLgoukEflCEiw25IU7ZlOaHrqwyKhSuOdoYs7EyPdcCcOr7eHpVRbnTyTPud1GxfATxF6ibVwvR9WF4MEVnPiAGlUQ7Fv2wX2S89lwIDAQAB";

    public static final int ENABLE_GPS = 1;

    static public final String kService_Asset = kHostProtocol + "://%s/asset";
    static public final String kService_Authorization = kHostProtocol + "://%s/oauth";
    static public final String kService_Contact = kHostProtocol + "://%s/contact";
    static public final String kService_User_Contact = kHostProtocol + "://%s/user/%s/contact";
    static public final String kService_Event = kHostProtocol + "://%s/event";
    static public final String kService_Event_Item = kHostProtocol + "://%s/event/%s";
    static public final String kService_User_Event_Paging = kHostProtocol + "://%s/event?user_id=%s&page=%s&size=%s&sort=-created";
    static public final String kService_Invitation = kHostProtocol + "://%s/invitation";
    static public final String kService_Register = kHostProtocol + "://%s/user";
    static public final String kService_Subscription = kHostProtocol + "://%s/subscription";
    static public final String kService_Update_Subscription = kHostProtocol + "://%s/subscription/%s";
    static public final String kService_SubscriptionInfo = kHostProtocol + "://%s/subscription?receiptId=%s";
    static public final String kService_Update_Contact = kHostProtocol + "://%s/contact/%s";
    static public final String kService_UploadPhoto = kHostProtocol + "://%s/user/%s/upload";
    static public final String kService_ResetPassword = kHostProtocol + "://%s/user/forgot-password/%s";
    static public final String kService_User = kHostProtocol + "://%s/user/%s";
    static public final String kService_UserInfo = kHostProtocol + "://%s/user?phone=%s";
    static public final String kService_Device = kHostProtocol + "://%s/device";
    public static final String kService_Emergency = kHostProtocol + "://%s/emergency";

    //Logout
    static public final String kService_User_LogOut = kHostProtocol + "://%s/user/logout/%s";
    static public final String kService_User_LogOutAll = kHostProtocol + "://%s/user/logoutall/%s";

    static public final String kService_GET_Userid = kHostProtocol + "://%s/subscription/checkuser/%s";
    public static final int VIDEO_CHUNK_DURATION = 3; //3 Seconds
    public static final int VIDEO_RECORD_DURATION = 10; // In munits

}

