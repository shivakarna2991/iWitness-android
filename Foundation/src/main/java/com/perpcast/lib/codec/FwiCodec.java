package com.perpcast.lib.codec;


import com.perpcast.lib.foundation.*;
import org.codehaus.jackson.map.*;

import java.math.*;
import java.net.*;
import java.text.*;
import java.util.*;


public final class FwiCodec {


    /** Convert human readable string to Json. */
    static public FwiJson convertBase64ToJson(FwiData base64Data) {
        /* Condition validation */
        if (base64Data == null || base64Data.length() == 0) return null;
		return FwiCodec.convertDataToJson(FwiBase64.decodeBase64String(base64Data));
    }
    static public FwiJson convertBase64ToJson(String  base64Data) {
        /* Condition validation */
        if (base64Data == null || base64Data.length() == 0) return null;
		return FwiCodec.convertDataToJson(FwiBase64.decodeBase64String(base64Data));
	}
    static public FwiJson convertBase64ToJson(byte[]  base64Data) {
        /* Condition validation */
        if (base64Data == null || base64Data.length == 0) return null;
		return FwiCodec.convertDataToJson(FwiBase64.decodeBase64String(base64Data));
    }
    static public FwiJson convertDataToJson(FwiData jsonData) {
		/* Condition validation */
        if (jsonData == null || jsonData.length() == 0) return null;
		return FwiCodec.convertDataToJson(FwiCodec.convertDataToString(jsonData));
	}
    static public FwiJson convertDataToJson(String  jsonData) {
        /* Condition validation */
        if (jsonData == null || jsonData.length() == 0) return null;

		FwiJson result = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS);

			Object objectMapper = mapper.readValue(jsonData, Object.class);
			result = FwiCodec.convertObjectToJson(objectMapper);
		}
		catch (Exception ex) {
			result = null;
		}
		finally {
			return result;
		}
	}
    static public FwiJson convertDataToJson(byte[]  jsonData) {
		/* Condition validation */
        if (jsonData == null || jsonData.length == 0) return null;
		return FwiCodec.convertDataToJson(FwiCodec.convertDataToString(jsonData));
	}
    /** Convert Json to human readable string. */
    static public FwiData convertJsonToData(FwiJson   json) {
		if (json == null) return null;
		return FwiCodec.convertStringToData(FwiCodec.convertJsonToString(json));
	}
    static public String  convertJsonToString(FwiJson json) {
        if (json == null) return null;
        return FwiCodec.convertJsonToString(json, false);
	}
    static public String  convertJsonToString(FwiJson json, boolean isFormat) {
        String jsonString = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = (isFormat ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer());

            Object o = FwiCodec.convertJsonToObject(json);
            jsonString = writer.writeValueAsString(o);
        }
        catch (Exception ex) {
            jsonString = null;
        }
        finally {
            return jsonString;
        }
    }

    /** Convert Json to object and vise versa. */
    static public Object  convertJsonToObject(FwiJson object) {
		/* Condition validation */
        if (object == null) return null;

        switch (object.jsonType()) {
            case kBoolean: return object.getBoolean();
            case kDouble : return object.getDouble();
            case kInteger: return object.getInteger();
            case kString : return object.getString();
            case kArray  : {
                ArrayList<Object> a = new ArrayList<Object>();
                for (int i = 0; i < object.size(); i++) {
                    Object o = FwiCodec.convertJsonToObject(object.jsonAtIndex(i));

                    if (o == null) {
                        continue;
                    }
                    a.add(o);
                }
                return a;
            }
            case kObject : {
                LinkedHashMap<String, Object> d = new LinkedHashMap<String, Object>();
                Set<String> keys = object.keySet();
                Iterator<String> i = keys.iterator();
                while(i.hasNext()) {
                    String key = i.next();
                    FwiJson value = object.jsonWithPath(key);
                    d.put(key, FwiCodec.convertJsonToObject(value));
                }
                return d;
            }
            default: return null;
        }
    }
    static public FwiJson convertObjectToJson(Object  object) {
		/* Condition validation */
        if (object == null) { return FwiJson.Null(); }

        if (object instanceof FwiJson)         { return (FwiJson) object; }
        else if (object instanceof Boolean)    { return FwiJson.Boolean((Boolean) object); }
        else if (object instanceof Double)     { return FwiJson.Double(new BigDecimal(((Double) object).doubleValue())); }
        else if (object instanceof BigDecimal) { return FwiJson.Double((BigDecimal) object); }
        else if (object instanceof Integer)	   { return FwiJson.Integer(new BigInteger(object.toString())); }
        else if (object instanceof BigInteger) { return FwiJson.Integer((BigInteger) object); }
        else if (object instanceof String) 	   { return FwiJson.String((String) object); }
        else if (object instanceof URL)        { return FwiJson.String(object.toString()); }
        else if (object instanceof FwiData)    { return FwiJson.String(FwiCodec.convertDataToString(FwiBase64.encodeBase64Data(((FwiData) object)))); }
        else if (object instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return FwiJson.String(dateFormat.format((Date) object));
        }
        else if (object instanceof ArrayList) {
            List<FwiJson> a = new ArrayList<FwiJson>();
            List src = (List) object;
            for (Object aSrc : src) {
                a.add(FwiCodec.convertObjectToJson(aSrc));
            }
            FwiJson j = FwiJson.Array();
            j.setJsons(a);
            return j;
        }
        else if (object instanceof LinkedHashMap) {
            Map<String, Object> src = (Map<String, Object>) object;
            Map<String, FwiJson> d = new LinkedHashMap<String, FwiJson>();
            Set<String> keys = src.keySet();
            for (String key : keys) {
                Object value = src.get(key);
                d.put(key, FwiCodec.convertObjectToJson(value));
            }

            FwiJson j = FwiJson.Object();
            j.setKeysAndJsons(d);
            return j;
        }
        else {
            return FwiJson.Null();
        }
    }

    /** Convert human readable string to bytes array. */
    static public FwiData convertStringToData(String utf8String) {
        /* Condition validation */
		if (utf8String == null || utf8String.length() == 0) return null;

        FwiData data = null;
		try {
			data = new FwiData(utf8String.getBytes("UTF-8"));
		}
		catch (Exception ex) {
            data = null;
		}
		finally {
            return data;
        }
    }
    /** Convert bytes array to human readable hex string. */
    static public String convertDataToString(FwiData data) {
        if (data == null || data.length() == 0) return null;
        else return FwiCodec.convertDataToString(data.bytes());
    }
    static public String convertDataToString(byte[]  data) {
        /* Condition validation */
		if (data == null || data.length == 0) return null;

		String text = null;
		try {
			text = new String(data, "UTF-8");
		}
		catch (Exception ex) {
			text = null;
		}
        finally {
            return text;
        }
    }
}
