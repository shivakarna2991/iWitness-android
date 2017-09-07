package com.perpcast.lib.key;


import android.content.*;

import java.io.*;
import java.security.*;


public final class FwiKeystore {
	static private final char[] kAuth_Keystore = "1OQj0,Bp^mg>~ovBIe4r6pO9Q]p+)>yS9(X|bL>Zs!STn I-La#-9RZJPd.DZ+_T".toCharArray();


	static private KeyStore _keystore = null;
	static private File     _ksDir    = null;
	static private File     _ksFile   = null;
	
	
	/** Initialize keystore. */
	static public synchronized void initialize(Context context) {
		// Create keystore directory
		_ksDir = context.getDir("Keystore", Context.MODE_PRIVATE);
		
		// Load keystore
		_ksFile = new File(String.format("%s%sKeystore.uber", _ksDir.getAbsolutePath(), File.separator));
		try {
			_keystore = KeyStore.getInstance("UBER");
			
			if (_ksFile.exists()) {
				InputStream input = new FileInputStream(_ksFile);
				_keystore.load(input, kAuth_Keystore);
				input.close();
			}
			else {
				_keystore.load(null);

				FileOutputStream output = new FileOutputStream(_ksFile);
				_keystore.store(output, kAuth_Keystore);
				output.close();
			}
		}
		catch (Exception ex) {
			_keystore = null;
			_ksFile   = null;
			_ksDir    = null;
		}
	}
	
	/** Load/Save/Delete AES key. */
	static public synchronized boolean deleteAesKey(FwiAesKey aesKey) {
		boolean isSuccess = true;

		// Delete key data
		try {
			_keystore.deleteEntry(aesKey.keyTag());

			// Update keystore
			FileOutputStream output = new FileOutputStream(_ksFile);
			_keystore.store(output, kAuth_Keystore);
			output.close();
		}
		catch (Exception ex) {
			isSuccess = false;
		}
        finally {
            return isSuccess;
        }
	}
	static public synchronized boolean insertAesKey(FwiAesKey aesKey) {
		boolean isSuccess = true;

		// Insert key data
		try {
			KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(aesKey.secretkey());
			_keystore.setEntry(aesKey.keyTag(), entry, new KeyStore.PasswordProtection(kAuth_Keystore));

			// Update keystore
			FileOutputStream output = new FileOutputStream(_ksFile);
			_keystore.store(output, kAuth_Keystore);
			output.close();
		}
		catch (Exception ex) {
			isSuccess = false;
		}
        finally {
            return isSuccess;
        }
	}
	static public FwiAesKey retrieveAesKey(String keyTag) {
		FwiAesKey aesKey = null;

		// Retrieve key data
		try {
			Key entry = _keystore.getKey(keyTag, kAuth_Keystore);

			if (entry != null) {
				aesKey = FwiAESFactory.aesKeyWithData(keyTag, entry.getEncoded());
			}
		}
		catch (Exception ex) {
			aesKey = null;
		}
        finally {
            return aesKey;
        }
	}
}
