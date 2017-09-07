package com.iwitness.androidapp.libraries.subscription;

/**
 * Created by hoanggia on 8/29/14.
 */
public interface ISubscriptionListener {

    public void onGetSubscriptionFinished(String subscriptionId);
    public void onUpdateSubscriptionFinished();
    public void onCreateSubscriptionFinished(String subscriptionId);
}
