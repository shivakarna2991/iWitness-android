package com.perpcast.lib.services;


import com.crittercism.app.*;
import com.perpcast.lib.services.request.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.*;
import org.apache.http.params.*;
import org.apache.http.protocol.*;


public abstract class FwiService {

	
	// Global static variables
	static private boolean _isInitialized = false;
	static private ThreadSafeClientConnManager _clientManager = null;
	// Global variables
	protected FwiRequest   _req = null;
	protected HttpClient   _con = null;
    protected HttpResponse _res = null;
	
    
	// Class's constructors
	public FwiService(FwiRequest request) {
		if (!_isInitialized) {
            PlainSocketFactory plainSocket = PlainSocketFactory.getSocketFactory();

            SSLSocketFactory sslSocket = SSLSocketFactory.getSocketFactory();
            sslSocket.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http" , plainSocket, 80));
            schemeRegistry.register(new Scheme("https", sslSocket  , 443));

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

            _clientManager = new ThreadSafeClientConnManager(params, schemeRegistry);
			_isInitialized = true;
		}
        _req = request;
		_con = new DefaultHttpClient(_clientManager, new BasicHttpParams());
	}
	
	
    // Class's properties
    public int status() {
        return (_res != null ? _res.getStatusLine().getStatusCode() : -1);
    }


    // Class's public abstract methods
    public abstract Object getResource();


	// Class's protected methods
	protected void execute() {
		/* Condition validation */
		if (this._req == null) return;
        
		try {
            _req.prepare();
			_res = _con.execute(_req);
		}
		catch (Exception ex) {
            Crittercism.logHandledException(ex);
			_req.abort();
		}
	}
}
