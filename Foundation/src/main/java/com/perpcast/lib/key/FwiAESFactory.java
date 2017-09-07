package com.perpcast.lib.key;


import com.perpcast.lib.FwiFoundation.*;
import com.perpcast.lib.foundation.*;
import com.perpcast.lib.utils.*;

import javax.crypto.*;
import java.security.*;


public final class FwiAESFactory {

    /** Generate AES Key. */
    static public FwiAesKey generateAesKey(FwiAesSize size) {
		return FwiAESFactory.generateAesKey(size, FwiIdUtils.randomTag());
	}
	static public FwiAesKey generateAesKey(FwiAesSize size, String keyTag) {
		FwiAesKey aesKey = null;
		try {
			KeyGenerator aesGenerator = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			aesGenerator.init(size.length, sr);

			SecretKey k = aesGenerator.generateKey();
			aesKey = new FwiAesKeyImpl_Private(keyTag, k.getEncoded());
		}
		catch (Exception ex) {
			aesKey = null;
		}
		finally {
            return aesKey;
		}
	}

	static public FwiAesKey aesKeyWithData(String keyTag, byte[] keyData) {
        /* Condition validation */
		if (keyData == null || keyData.length == 0) return null;

		if (keyTag == null || keyTag.length() == 0) { keyTag = FwiIdUtils.randomTag(); }
		return new FwiAesKeyImpl_Private(keyTag, keyData);
	}
	static public FwiAesKey aesKeyWithData(String keyTag, FwiData keyData) {
        /* Condition validation */
		if (keyData == null || keyData.length() == 0) return null;
		return FwiAESFactory.aesKeyWithData(keyTag, keyData.bytes());
	}
}
