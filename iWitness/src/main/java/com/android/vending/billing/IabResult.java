
package com.android.vending.billing;


public final class IabResult {


    private int _response   = -1;
    private String _message = null;


    // Class's constructors
    public IabResult(int response, String message) {
        this._response = response;

        if (message == null || message.trim().length() == 0) {
            this._message = IabHelper.getResponseDesc(response);
        }
        else {
            this._message = message;
        }
    }

    // Class's properties
    public int getResponse() {
        return _response;
    }

    public String getMessage() {
        return _message;
    }


    // Class's public methods
    public boolean isSuccess() {
        return _response == IabHelper.BILLING_RESPONSE_RESULT_OK;
    }


    // Class's override methods
    @Override
    public String toString() {
        return String.format("Result: %s [response: %s]", this._message, IabHelper.getResponseDesc(this._response));
    }
}

