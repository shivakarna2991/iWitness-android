package com.perpcast.lib.key;


import com.perpcast.lib.foundation.*;

import javax.crypto.*;
import java.io.*;


public interface FwiAesKey extends Serializable {

    /** Size of the key. */
    public int keysize();
    /** Name of the key. */
    public String keyTag();
	/** Return Java compatible version. */
	public SecretKey secretkey();

    /** Convert this key to data. */
    public FwiData encode();

    /** Decrypt data. */
	public FwiData decryptData(FwiData... data);
    /** Encrypt data. */
	public FwiData encryptData(FwiData... data);
}