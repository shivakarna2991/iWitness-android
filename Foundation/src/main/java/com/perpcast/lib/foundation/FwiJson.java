package com.perpcast.lib.foundation;


import android.text.TextUtils;
import com.perpcast.lib.FwiFoundation.*;
import com.perpcast.lib.codec.*;

import java.io.*;
import java.math.*;
import java.util.*;


public class FwiJson implements Serializable {


    // <editor-fold defaultstate="collapsed" desc="Class's static constructors">
	static public FwiJson Null() {
		return new FwiJson(FwiJsonValue.kNull);
	}

	static public FwiJson Boolean() {
        return new FwiJson(FwiJsonValue.kBoolean);
	}
	static public FwiJson Boolean(boolean value) {
		FwiJson o = new FwiJson(FwiJsonValue.kBoolean);
		o.setBoolean(value);
		return o;
	}
	static public FwiJson Boolean(Boolean value) {
        FwiJson o = new FwiJson(FwiJsonValue.kBoolean);
		o.setBoolean(value);
		return o;
	}

	static public FwiJson Double() {
		return new FwiJson(FwiJsonValue.kDouble);
	}
    static public FwiJson Double(float  value) {
        FwiJson o = new FwiJson(FwiJsonValue.kDouble);
        o.setDouble(value);
        return o;
    }
	static public FwiJson Double(double value) {
        FwiJson o = new FwiJson(FwiJsonValue.kDouble);
        o.setDouble(value);
		return o;
	}
	static public FwiJson Double(BigDecimal value) {
        FwiJson o = new FwiJson(FwiJsonValue.kDouble);
		o.setDouble(value);
		return o;
	}

	static public FwiJson Integer() {
		return new FwiJson(FwiJsonValue.kInteger);
	}
    static public FwiJson Integer(short value) {
        FwiJson o = new FwiJson(FwiJsonValue.kInteger);
        o.setInteger(value);
        return o;
    }
    static public FwiJson Integer(int   value) {
        FwiJson o = new FwiJson(FwiJsonValue.kInteger);
        o.setInteger(value);
        return o;
    }
	static public FwiJson Integer(long  value) {
        FwiJson o = new FwiJson(FwiJsonValue.kInteger);
		o.setInteger(value);
		return o;
	}
	static public FwiJson Integer(BigInteger value) {
        FwiJson o = new FwiJson(FwiJsonValue.kInteger);
		o.setInteger(value);
		return o;
	}

	static public FwiJson String() {
		return new FwiJson(FwiJsonValue.kString);
	}
	static public FwiJson String(String input) {
        FwiJson o = new FwiJson(FwiJsonValue.kString);
        o.setString(input);
		return o;
	}

	static public FwiJson Array() {
		return new FwiJson(FwiJsonValue.kArray);
	}
	static public FwiJson Array(FwiJson... objects) {
        FwiJson o = new FwiJson(FwiJsonValue.kArray);
        o.setJsons(objects);
		return o;
	}
	static public FwiJson Array(List<FwiJson> objects) {
        FwiJson o = new FwiJson(FwiJsonValue.kArray);
        o.setJsons(objects);
		return o;
	}

	static public FwiJson Object() {
		return new FwiJson(FwiJsonValue.kObject);
	}
    static public FwiJson Object(Object... objects) {
        FwiJson o = new FwiJson(FwiJsonValue.kObject);
        o.setKeysAndJsons(objects);
		return o;
	}
	static public FwiJson Object(Map<String, FwiJson> objects) {
        FwiJson o = new FwiJson(FwiJsonValue.kObject);
		o.setKeysAndJsons(objects);
		return o;
	}
    // </editor-fold>


	// Global variables
	private FwiJsonValue _jsonType = FwiJsonValue.kNull;

	private Boolean	     _bValue = null;
	private BigDecimal   _dValue = null;
	private BigInteger   _iValue = null;
	private String 	     _sValue = null;

    private List<FwiJson> _array = null;
	private Map<String, FwiJson> _objects = null;

    public List<FwiJson> toArray() {
        return _array;
    }

	// Class's constructors
	public FwiJson(FwiJsonValue jsonType) {
		_jsonType = jsonType;

		if (_jsonType == FwiJsonValue.kArray) {
			_array = new ArrayList<FwiJson>();
		}
		else if (_jsonType == FwiJsonValue.kObject) {
			_objects = new LinkedHashMap<String, FwiJson>();
		}
	}


    // <editor-fold defaultstate="collapsed" desc="Class's Properties">
    public FwiJsonValue jsonType() {
        return _jsonType;
    }
    public Set<String> keySet() {
        return (_objects != null ? _objects.keySet() : null);
    }

    public boolean getBoolean() {
        return (_bValue == null ? false : _bValue);
    }
    public void setBoolean(boolean value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kBoolean) return;
        _bValue = Boolean.valueOf(value);
    }
    public void setBoolean(Boolean value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kBoolean) return;

        /* Condition validation */
        if (value == null) return;
        _bValue = value;
    }

	public BigDecimal getDouble() {
		if (_dValue == null) {
            if (TextUtils.isEmpty(_sValue)) {
                return BigDecimal.ZERO;
            }

            try
            {
                _dValue = BigDecimal.valueOf(Long.parseLong(_sValue));
            } catch(NumberFormatException nfe)
            {
                _dValue = null;
                return BigDecimal.ZERO;
            }
        }

		return _dValue;
	}
    public void setDouble(float  value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kDouble) return;
        _dValue = BigDecimal.valueOf(value);
        _sValue = String.valueOf(value);
    }
    public void setDouble(double value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kDouble) return;
        _dValue = BigDecimal.valueOf(value);
        _sValue = String.valueOf(value);
    }
    public void setDouble(BigDecimal value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kDouble) return;

        /* Condition validation */
        if (value == null) return;
        _dValue = value;
        _sValue = String.valueOf(value);
    }

	public BigInteger getInteger() {
		if (_iValue == null)
        {
            if (TextUtils.isEmpty(_sValue)) {
                return BigInteger.ZERO;
            }

            try
            {
                _iValue = BigInteger.valueOf(Integer.parseInt(_sValue));
            } catch(NumberFormatException nfe)
            {
                _iValue = null;
                return BigInteger.ZERO;
            }
        }
        return _iValue;
	}
    public void setInteger(short value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kInteger) return;
        _iValue = BigInteger.valueOf(value);
        _sValue = String.valueOf(value);
    }
    public void setInteger(int   value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kInteger) return;
        _iValue = BigInteger.valueOf(value);
        _sValue = String.valueOf(value);
    }
    public void setInteger(long  value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kInteger) return;
        _iValue = BigInteger.valueOf(value);
        _sValue = String.valueOf(value);
    }
    public void setInteger(BigInteger value) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kInteger) return;

        /* Condition validation */
        if (value == null) return;
        _iValue = value;
        _sValue = String.valueOf(value);
    }

	public String getString() {
		if (_sValue == null) return "";
        return _sValue;
	}
	public void setString(String value) {
		/* Condition validation */
		if (_jsonType != FwiJsonValue.kString) return;

        /* Condition validation */
        if (value == null) return;
		_sValue = value;
	}
	// </editor-fold>


    // Class's public methods
	@Override
	public boolean equals(Object other) {
        /* Condition validation */
        if (other == null || !(other instanceof FwiJson)) return false;

        // FIX FIX FIX: This use a lot of overhead memory, convert this to for loop
        FwiData d2 = ((FwiJson)other).encode();
        FwiData d1 = this.encode();

        return !(d1 == null || d2 == null) && d1.equals(d2);
	}
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + (this._jsonType != null ? this._jsonType.hashCode() : 0);
		hash = 89 * hash + (this._bValue   != null ? this._bValue.hashCode()   : 0);
		hash = 89 * hash + (this._dValue   != null ? this._dValue.hashCode()   : 0);
		hash = 89 * hash + (this._iValue   != null ? this._iValue.hashCode()   : 0);
		hash = 89 * hash + (this._sValue   != null ? this._sValue.hashCode()   : 0);
		hash = 89 * hash + (this._array    != null ? this._array.hashCode()    : 0);
		hash = 89 * hash + (this._objects  != null ? this._objects.hashCode()  : 0);
		return hash;
	}
    @Override
    public String toString() {
        switch (_jsonType) {
            case kBoolean: return this.getBoolean() ? "true" : "false";
            case kDouble : return this.getDouble().toString();
            case kInteger: return this.getInteger().toString(10);
            case kString : return this.getString();
            case kArray  :
            default      : return "null";
            case kObject : return FwiCodec.convertJsonToString(this, true);
        }
    }
	
	public void sort() {
		/* Condition validation */
		if (_jsonType != FwiJsonValue.kObject) return;
		Map<String, FwiJson> objects = new TreeMap<String, FwiJson>();
		objects.putAll(_objects);
		
		_objects = objects;
	}
	

    /** Validate the structure of 2 Jsons. */
    public boolean isLike(FwiJson object) {
		/* Condition validation */
		if (object == null) return false;

		boolean like = true;
		if (_jsonType != object._jsonType) {
			like = false;
		}
		else {
			if (object.size() > 0) {
                switch (object._jsonType) {
                    case kArray: {
                        for (int i = 0; i < this.size(); i++) {
                            like = object.jsonAtIndex(i).isLike(this.jsonAtIndex(i));
                            if (!like) break;
                        }
                        break;
                    }

                    case kObject: {
                        Map<String, FwiJson> d = object._objects;
                        if (d == null) {
                            like = false;
                        }
                        else {
                            Set<String> keys = d.keySet();
                            for (String key : keys) {
                                FwiJson obj = d.get(key);
                                FwiJson o   = _objects.get(key);

                                if (o != null && obj != null) {
                                    like = o.isLike(obj);
                                } else {
                                    like = (o == null && obj == null);
                                }

                                /* Condition validation */
                                if (!like) break;
                            }
                        }
                        break;
                    }

                    default:
                        break;
                }
			}
		}
		return like;
	}

    /** Convert Json to data. */
	public FwiData encode() {
        return FwiCodec.convertJsonToData(this);
	}
    /** Convert data to base64Data. */
    public FwiData encodeBase64Data() {
        return FwiBase64.encodeBase64Data(this.encode());
    }
    /** Convert base64Data to base64String. */
    public String  encodeBase64String() {
        return FwiCodec.convertDataToString(this.encodeBase64Data());
	}

    /** Get number of child elements if this Json is array or object, otherwise 0. */
	public int size() {
		switch (_jsonType) {
			case kArray : return _array.size();
			case kObject: return _objects.size();
			default     : return 0;
		}
	}
    /** Get Json at index, return null if this Json is not array or object. */
	public FwiJson jsonAtIndex(int index) {
		/* Condition validation */
		if (index < 0 || index >= this.size()) return FwiJson.Null();

		switch (_jsonType) {
			case kArray: {
                return _array.get(index);
			}
			case kObject: {
                String key = (String)_objects.keySet().toArray()[index];
				return _objects.get(key);
			}
			default: return FwiJson.Null();
		}
	}
    /** Get Json with path, return null if this Json is not array or object. */
	public FwiJson jsonWithPath(String path) {
		/* Condition validation */
        if (!(_jsonType == FwiJsonValue.kArray || _jsonType == FwiJsonValue.kObject)) return FwiJson.Null();

        /* Condition validation */
        if (path == null || path.length() == 0) return FwiJson.Null();

		String[] tokens = path.trim().split("/");
		FwiJson o = this;

        for (String token : tokens) {
            if (o._jsonType == FwiJsonValue.kArray) {
                if (token.trim().matches("^\\d+$")) {
                    int index = Integer.parseInt(token);

                    if (0 <= index && index < o._array.size()) {
                        o = o._array.get(index);
                    }
                    else {
                        o = null;
                    }
                }
                else {
                    o = null;
                }
            }
            else if (o._jsonType == FwiJsonValue.kObject) {
                o = o._objects.get(token);
            }
            else {
                o = null;
            }

            /* Condition validation: Object must not be null */
            if (o == null) {
                o = FwiJson.Null();
                break;
            }
        }
		return o;
	}

    /** Replace the current Json with new one. */
	public void setJsons(FwiJson... objects) {
		/* Condition validation */
		if (_jsonType != FwiJsonValue.kArray) return;

        /* Condition validation */
        if (objects == null || objects.length == 0) return;

		_array.clear();
		this.addJsons(objects);
	}
    public void setJsons(List<FwiJson> objects) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return;

        /* Condition validation */
        if (objects == null || objects.size() == 0) return;

        _array.clear();
        this.addJsons(objects);
    }
    /** Append new Json to the current one. */
	public void addJsons(FwiJson... objects) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return;

        /* Condition validation */
        if (objects == null || objects.length == 0) return;

        for (Object object : objects) {
            FwiJson json = FwiCodec.convertObjectToJson(object);

            if (json == null || json._jsonType == FwiJsonValue.kNull) continue;
            _array.add(json);
        }
	}
    public void addJsons(List<FwiJson> objects) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return;

        /* Condition validation */
        if (objects == null || objects.size() == 0) return;

        for (FwiJson object : objects) {
            FwiJson json = FwiCodec.convertObjectToJson(object);

            if (json == null || json._jsonType == FwiJsonValue.kNull) continue;
            _array.add(json);
        }
    }

    public void removeJsonAtIndex(int index) {
        /* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return;

        _array.remove(index);
    }

    public void replaceJsonAtIndex(int index, FwiJson object) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return;

        /* Condition validation */
        if (object == null) return;
        _array.add(index, object);
        _array.remove(index + 1);
    }

    /** Remove Json from current collection. */
	public void removeJsons(FwiJson... objects) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return;

        /* Condition validation */
        if (objects == null || objects.length == 0) return;

        for (FwiJson object : objects) {
            if (_array.contains(object)) _array.remove(object);
        }
	}
    public void removeJsons(List<FwiJson> objects) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return;

        /* Condition validation */
        if (objects == null || objects.size() == 0) return;

        for (FwiJson object : objects) {
            if (_array.contains(object)) _array.remove(object);
        }
    }

    /** Replace the current keys & Json with new one. */
	public void setKeysAndJsons(Object... objects) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kObject) return;

        /* Condition validation */
        if (objects == null || objects.length == 0) return;

		_objects.clear();
		this.addKeysAndJsons(objects);
	}
    public void setKeysAndJsons(Map<String, FwiJson> objects) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kObject) return;

        /* Condition validation */
        if (objects == null || objects.size() == 0) return;

        _objects.clear();
        this.addKeysAndJsons(objects);
    }
	/** Append new keys & Json to the current one. */
    public void addKeysAndJsons(Object... objects) {
        /* Condition validation */
        if (_jsonType != FwiJsonValue.kObject) return;

        /* Condition validation */
        if (objects == null || objects.length == 0) return;

        String  k = null;
		boolean b = true;
        for (Object object : objects) {
            // b = true -> get key for dictionary
            if (b) {
                if (object instanceof String) {
                    k = (String) object;
                } else {
                    k = object.toString();
                }
                b = false;
            }
            // b = false -> get value for dictionary
            else {
                _objects.put(k, FwiCodec.convertObjectToJson(object));
                k = null;
                b = true;
            }
        }

        /* Condition validation: Validate the last object */
		if (k != null) {
			_objects.put(k, FwiJson.Null());
		}
    }
    public void addKeysAndJsons(Map<String, FwiJson> objects) {
        /* Condition validation */
        if (_jsonType != FwiJsonValue.kObject) return;

        /* Condition validation */
        if (objects == null || objects.size() == 0) return;
        _objects.putAll(objects);
    }
	/** Remove keys & Json from current collection. */
    public void removeJsonsWithKeys(String... keys) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kObject) return;

        /* Condition validation */
        if (keys == null || keys.length == 0) return;

        for (String key : keys) {
            if (key == null || key.length() == 0) continue;
            if (_objects.containsKey(key)) _objects.remove(key);
        }
	}
    public void removeJsonsWithKeys(List<String> keys) {
		/* Condition validation */
        if (_jsonType != FwiJsonValue.kObject) return;

        /* Condition validation */
        if (keys == null || keys.size() == 0) return;

        for (String key : keys) {
            if (key == null || key.length() == 0) continue;
            if (_objects.containsKey(key)) _objects.remove(key);
        }
    }

    /** Enumerate array. */
    public Iterator<FwiJson> enumerateJsons() {
        /* Condition validation */
        if (_jsonType != FwiJsonValue.kArray) return null;
        return _array.iterator();
    }

    /** Enumerate objects. */
    public Iterator<String> enumerateKeysAndValues() {
        /* Condition validation */
        if (_jsonType != FwiJsonValue.kObject) return null;
        return _objects.keySet().iterator();
    }
}
