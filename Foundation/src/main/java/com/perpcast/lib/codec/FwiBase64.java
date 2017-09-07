package com.perpcast.lib.codec;


import com.perpcast.lib.foundation.*;

import org.apache.commons.codec.binary.Base64;

public final class FwiBase64 {
    
    
    /** Validate base64. */
    static public boolean isBase64(FwiData base64Data) {
        /* Condition validation */
        if (base64Data == null || base64Data.length() == 0) return false;
        else return FwiBase64.isBase64(base64Data.bytes());
    }
	static public boolean isBase64(String  base64Data) {
        /* Condition validation */
		if (base64Data == null || base64Data.length() == 0) return false;
        return FwiBase64.isBase64(FwiCodec.convertStringToData(base64Data));
	}
    static public boolean isBase64(byte[]  base64Data) {
        /* Condition validation */
        if (base64Data == null || base64Data.length == 0 || (base64Data.length % 4) != 0) return false;
        return Base64.isArrayByteBase64(base64Data);
    }
    
    /** Decode base64. */
    static public FwiData decodeBase64Data(FwiData base64Data) {
        /* Condition validation */
		if (base64Data == null || base64Data.length() == 0) return null;
        return FwiBase64.decodeBase64Data(base64Data.bytes());
    }
    static public FwiData decodeBase64Data(String  base64Data) {
		/* Condition validation */
		if (base64Data == null || base64Data.length() == 0) return null;
        return FwiBase64.decodeBase64Data(FwiCodec.convertStringToData(base64Data));
	}
    static public FwiData decodeBase64Data(byte[]  base64Data) {
        /* Condition validation */
        if (!FwiBase64.isBase64(base64Data)) return null;
        return new FwiData(Base64.decodeBase64(base64Data));
    }
    static public String decodeBase64String(FwiData base64Data) {
        return FwiCodec.convertDataToString(FwiBase64.decodeBase64Data(base64Data));
    }
    static public String decodeBase64String(String  base64Data) {
		return FwiCodec.convertDataToString(FwiBase64.decodeBase64Data(base64Data));
	}
    static public String decodeBase64String(byte[]  base64Data) {
        return FwiCodec.convertDataToString(FwiBase64.decodeBase64Data(base64Data));
    }
    
    /** Encode base64. */
    static public FwiData encodeBase64Data(FwiData data) {
        /* Condition validation */
        if (data == null || data.length() == 0) return null;
        return FwiBase64.encodeBase64Data(data.bytes());
    }
	static public FwiData encodeBase64Data(String  data) {
        /* Condition validation */
        if (data == null || data.length() == 0) return null;
        return FwiBase64.encodeBase64Data(FwiCodec.convertStringToData(data));
    }
    static public FwiData encodeBase64Data(byte[]  data) {
        /* Condition validation */
        if (data == null || data.length == 0) return null;
        return new FwiData(Base64.encodeBase64(data));
    }

    static public String encodeBase64String(FwiData data) {
        return FwiBase64.encodeBase64String(data.bytes());
    }
    static public String encodeBase64String(String  data) {
        return FwiBase64.encodeBase64String(FwiCodec.convertStringToData(data));
    }
    static public String encodeBase64String(byte[]  data) {
        return FwiCodec.convertDataToString(FwiBase64.encodeBase64Data(data));
    }
}
