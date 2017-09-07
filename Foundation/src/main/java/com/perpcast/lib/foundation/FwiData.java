package com.perpcast.lib.foundation;


import java.io.*;
import java.util.*;


public class FwiData implements Serializable {


	// Global variables
	private byte[] _bytes = null;


	// Class's constructors
	public FwiData() {
		this._bytes = null;
	}
	public FwiData(FwiData data) {
		if (data != null && data.length() > 0) {
			this._bytes = new byte[data.bytes().length];
			System.arraycopy(data.bytes(), 0, this._bytes, 0, data._bytes.length);
		}
	}
    public FwiData(byte[]  data) {
		if (data != null && data.length > 0) {
            this._bytes = data;
        }
	}


	// <editor-fold defaultstate="collapsed" desc="Class's Properties">
    public int length() {
		if (this._bytes == null || this._bytes.length == 0) {
            return 0;
        }
		else {
            return this._bytes.length;
        }
	}
	public byte[] bytes() {
		if (this._bytes == null || this._bytes.length == 0) {
            return null;
        }
		else {
            return this._bytes;
        }
	}
    // </editor-fold>


    // Class's override methods
	@Override
	public boolean equals(Object o) {
		/* Condition validation */
		if (o == null) return false;

		/* Condition validation: Must be instance of FwiData or FwiMutableData */
		if (!(o instanceof FwiData || o instanceof FwiMutableData)) { return false; }

		return Arrays.equals(this._bytes, ((FwiData) o)._bytes);
	}
    
    @Override
    public int hashCode() {
        int hash = 3;

        hash = 67 * hash + Arrays.hashCode(this._bytes);
        return hash;
    }
	@Override
	public String toString() {
		/* Condition validation */
		if (this._bytes == null || this._bytes.length == 0) return "<>";
        
        char[] _hexTable = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

        // Calculate length
        int c = this._bytes.length * 2;
        int r = this._bytes.length % 4;
        int a = (this._bytes.length - r) >> 2;
		StringBuilder builder = new StringBuilder(c + a + 2);

        builder.append('<');
		for (int i = 0; i < this._bytes.length; i++) {
            int b = this._bytes[i] & 0xff;
            builder.append(_hexTable[(b >> 4)]);
            builder.append(_hexTable[(b & 0x0f)]);

			if (i > 0 && i < (this._bytes.length - 1) && ((i + 1) % 4) == 0) {
                builder.append(' ');
            }
		}
		builder.append('>');
		return builder.toString();
	}
    
    
	// Class's public methods
	public void clean() {
		/* Condition validation */
		if (_bytes == null || _bytes.length == 0) return;
		for (int i = 0; i < _bytes.length; i++) _bytes[i] = (byte)0x00;
	}
}
