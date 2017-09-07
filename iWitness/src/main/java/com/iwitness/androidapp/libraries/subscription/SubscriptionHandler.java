package com.iwitness.androidapp.libraries.subscription;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.vending.billing.IInAppBillingService;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.IWPurchasedPackage;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.codec.FwiCodec;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.UUID;

public class SubscriptionHandler implements TaskDelegate {

    private ISubscriptionListener listener;
    private Context context;
    private IInAppBillingService billingService;
    private UUID getSubscriptionTaskId;
    private UUID createSubscriptionTaskId;
    private UUID updateSubscriptionTaskId;
    private IWPurchasedPackage purchasedPackage;
    private boolean updatingSubscription = false;
    public ForegroundTask task;

    public SubscriptionHandler(Context ctx, ISubscriptionListener listener) {
        this.context = ctx;
        this.listener = listener;
    }

    public void updateSubscription(String subscriptionId, IWPurchasedPackage purchasedPackage) {
        this.purchasedPackage = purchasedPackage;
        updatingSubscription = true;

        FwiRequest request = RequestUtils.generateHttpRequest(
                FwiFoundation.FwiHttpMethod.kPatch,
                Configuration.kService_Update_Subscription,
                Configuration.kHostname,
                subscriptionId
        );

        FwiJson requestInfo = FwiJson.Object(
                "packageName", FwiJson.String(
                        String.format(
                                "%s.%s",
                                context.getPackageName(),
                                purchasedPackage.getProductId()
                        )
                ),
                "appName", FwiJson.String(context.getPackageName()),
                "purchasedToken", FwiJson.String(purchasedPackage.getPurchasedToken()),
                "signature", FwiJson.String(purchasedPackage.getSignature()),
                "productId", FwiJson.String(purchasedPackage.getProductId()),
                "receiptId", FwiJson.String(purchasedPackage.getOrderId()),
                "receiptOrderNumber", FwiJson.String(purchasedPackage.getOrderNumber()),
                "receiptDate", FwiJson.String(purchasedPackage.getPurchasedTime()),
                "signedData", FwiJson.String(purchasedPackage.getPurchasedData())
        );

        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();

        if (!TextUtils.isEmpty(mPhoneNumber)) {
            requestInfo.addKeysAndJsons("originalPhone", mPhoneNumber.replace("+", "0"));
        }

        requestInfo.addKeysAndJsons("isRenew", true);
        updateSubscriptionTaskId = FwiIdUtils.generateUUID();
        request.setDataParam(FwiDataParam.paramWithJson(requestInfo));
        task = new ForegroundTask(context, request, updateSubscriptionTaskId);
        task.run(this);
    }

    public void updateSubscription(IWPurchasedPackage purchasedPackage) {
        this.purchasedPackage = purchasedPackage;
        updatingSubscription = true;
        getSubscription(purchasedPackage);
    }

    public SubscriptionHandler setBillingService(IInAppBillingService billingService) {
        this.billingService = billingService;
        return this;
    }

    public IWPurchasedPackage getPurchasedPackage() throws RemoteException {
        return getPurchasedPackage("packageName", context.getPackageName());
    }

    public IWPurchasedPackage getPurchasedPackage(String subscriptionPackage) throws RemoteException {
        return getPurchasedPackage("productId", subscriptionPackage);
    }

    public IWPurchasedPackage getPurchasedPackage(String compareKey, String compareValue) throws RemoteException {

        if (billingService != null) {
            Bundle ownedItems = billingService.getPurchases(Configuration.GOOGLE_PLAY_API_VERSION, context.getPackageName(), "subs", null);

            int response = ownedItems.getInt("RESPONSE_CODE");

            ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

            boolean isPurchased = (response == 0
                    && purchaseDataList != null
                    && signatureList != null
                    && purchaseDataList.size() > 0
                    && signatureList.size() > 0);
            if (isPurchased) {
                int index = 0;

                // Look up data
                for (String data : purchaseDataList) {
                    FwiJson purchaseData = FwiCodec.convertDataToJson(data);
                    if (purchaseData.jsonWithPath(compareKey).getString().compareTo(compareValue) == 0) {
//                        FwiJson data1 = FwiCodec.convertDataToJson(data);
//                        String[] transactions = data1.jsonWithPath("orderId").getString().split("\\.\\.");
//                        Toast.makeText(context, "orderId"+transactions[0], Toast.LENGTH_LONG).show();
                        return new IWPurchasedPackage(data, signatureList.get(index));
                    }
                    index++;
                }
            }
        }
        return null;
    }

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        if (taskId == getSubscriptionTaskId) {
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                FwiJson r = (FwiJson) response;
                String subscriptionId = r.jsonWithPath("_embedded/subscription/0/id").getString();

                boolean isFound = !TextUtils.isEmpty(subscriptionId);

                if (isFound) {
                    if (updatingSubscription) {
                        updatingSubscription = false;
                        updateSubscription(subscriptionId, purchasedPackage);
                    }

                } else if (purchasedPackage != null) {
                    if (updatingSubscription) {
                        updatingSubscription = false;
                        createSubscription(purchasedPackage, true);
                    }
                }
                if (listener != null) {
                    listener.onGetSubscriptionFinished(subscriptionId);
                }
            } else if (statusCode == -1) {

            }
        } else if (taskId == createSubscriptionTaskId) {
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                FwiJson r = (FwiJson) response;
                String subscriptionId = r.jsonWithPath("id").getString();
                if (listener != null) {
                    listener.onCreateSubscriptionFinished(subscriptionId);
                }
            } else if (statusCode == -1) {

            }
        } else if (taskId == updateSubscriptionTaskId) {
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                FwiJson r = (FwiJson) response;
                if (listener != null) {
                    listener.onUpdateSubscriptionFinished();
                }
            } else if (statusCode == -1) {

            }
        }
    }

    public void getSubscription(IWPurchasedPackage purchasedPackage) {
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                Configuration.kService_SubscriptionInfo,
                Configuration.kHostname,
                purchasedPackage.getOrderId()
        );
        getSubscriptionTaskId = FwiIdUtils.generateUUID();
        task = new ForegroundTask(context, request, getSubscriptionTaskId);
        task.run(this);
    }

    /**
     * create subscription
     *
     * @param isRenew
     */
    public void createSubscription(IWPurchasedPackage purchasedPackage, boolean isRenew) {
        FwiRequest request = RequestUtils.generateHttpRequest(
                FwiFoundation.FwiHttpMethod.kPost,
                Configuration.kService_Subscription,
                Configuration.kHostname
        );

        FwiJson requestInfo = FwiJson.Object(
                "packageName", FwiJson.String(
                        String.format(
                                "%s.%s",
                                context.getPackageName(),
                                purchasedPackage.getProductId()
                        )
                ),
                "appName", FwiJson.String(context.getPackageName()),
                "purchasedToken", FwiJson.String(purchasedPackage.getPurchasedToken()),
                "signature", FwiJson.String(purchasedPackage.getSignature()),
                "productId", FwiJson.String(purchasedPackage.getProductId()),
                "receiptId", FwiJson.String(purchasedPackage.getOrderId()),
                "receiptOrderNumber", FwiJson.String(purchasedPackage.getOrderNumber()),
                "receiptDate", FwiJson.String(purchasedPackage.getPurchasedTime()),
                "signedData", FwiJson.String(purchasedPackage.getPurchasedData())
        );

        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();

        if (!TextUtils.isEmpty(mPhoneNumber)) {
            requestInfo.addKeysAndJsons("originalPhone", mPhoneNumber.replace("+", "0"));
        }

        if (isRenew) {
            requestInfo.addKeysAndJsons("isRenew", true);
        }

        createSubscriptionTaskId = FwiIdUtils.generateUUID();
        request.setDataParam(FwiDataParam.paramWithJson(requestInfo));
        task = new ForegroundTask(context, request, createSubscriptionTaskId);
        task.run(this);
    }

    public void cancel(boolean interrupt) {
        if (task != null) {
            task.cancel(interrupt);
        }
    }
}
