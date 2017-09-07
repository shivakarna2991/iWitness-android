package com.perpcast.lib.key;


import com.perpcast.lib.foundation.*;

import javax.crypto.*;
import javax.crypto.spec.*;


final class FwiAesKeyImpl_Private implements FwiAesKey {
	static private final long serialVersionUID = 1L;


	// Global variables
    private int	          _keySize = 0;
    private String        _keyTag  = null;
	private SecretKeySpec _aesKey  = null;


    // Class's constructors
    public FwiAesKeyImpl_Private(String keyTag, byte[] keyData) {
        this._keyTag  = keyTag;
        this._keySize = keyData.length * 8;
        this._aesKey  = new SecretKeySpec(keyData, "AES");
    }


    // <editor-fold defaultstate="collapsed" desc="Class's properties">
    @Override
    public int keysize() { return this._keySize; }
    @Override
    public String keyTag() {
        return this._keyTag;
    }
    @Override
    public SecretKey secretkey() {
        return _aesKey;
    }
    // </editor-fold>


	// <editor-fold defaultstate="collapsed" desc="Class's public methods">
	@Override
	public FwiData encode() {
        return (this._aesKey != null ? new FwiData(this._aesKey.getEncoded()) : null);
	}

    @Override
	public FwiData decryptData(FwiData... data) {
		/* Condition validation */
		if (this._aesKey == null) return null;

		// Append all data together
		FwiMutableData d = new FwiMutableData();
		for (int i = 0; i < data.length; i++) {
			d.append(data[i]);
		}
		if (d == null || d.length() == 0) return null;

		// Decrypt data
        FwiData decrypted = null;
		try {
			Cipher cipher = this._getCipher();

			cipher.init(Cipher.DECRYPT_MODE, this._aesKey);
			byte[] decryptData = cipher.doFinal(d.bytes());
			decrypted = new FwiData(decryptData);
		}
		catch (Exception ex) {
            decrypted = null;
		}
        finally {
            return decrypted;
        }
	}
    @Override
	public FwiData encryptData(FwiData... data) {
		/* Condition validation */
        if (this._aesKey == null) return null;

		// Append all data together
		FwiMutableData d = new FwiMutableData();
		for (int i = 0; i < data.length; i++) {
			d.append(data[i]);
		}
        if (d == null || d.length() == 0) return null;

		// Encrypt data
        FwiData encrypted = null;
		try {
			Cipher cipher = this._getCipher();

			cipher.init(Cipher.ENCRYPT_MODE, this._aesKey);
			byte[] encryptData = cipher.doFinal(d.bytes());
			encrypted = new FwiData(encryptData);
		}
		catch (Exception ex) {
            encrypted = null;
		}
        finally {
            return encrypted;
        }
	}
	// </editor-fold>


	// Class's private methods
	private Cipher _getCipher() {
        Cipher cipher = null;
		try {
            cipher = Cipher.getInstance("AES");
		}
		catch (Exception ex) {
            cipher = null;
		}
        finally {
            return cipher;
        }
	}
}
