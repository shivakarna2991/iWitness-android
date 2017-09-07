package com.iwitness.androidapp.controllers.authentication;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Purchase;
import com.crittercism.app.Crittercism;
import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.libraries.subscription.ISubscriptionListener;
import com.iwitness.androidapp.libraries.subscription.SubscriptionHandler;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.IWPurchasedPackage;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.codec.FwiCodec;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SubscriptionController extends AppCompatActivity implements TaskDelegate {

    @BindView(R.id.textAppThatSaveYourLife)
    TextView mTextViewAppInfoParent;

    @BindView(R.id.textSubscribeText)
    TextView mTextViewAppInfoChild;

    @BindView(R.id.btnMonthlySubscription)
    Button mButtonMonthlySubscription;

    @BindView(R.id.btnAnnualSubscription)
    Button mButtonAnnualSubscription;

    @BindView(R.id.tv_learnmore)
    TextView mTextViewLearnMore;


    // Global constants
    static private final String MONTHLY_SUBSCRIPTION = "monthly_sub_2.99";
    static private final String YEARLY_SUBSCRIPTION = "yearly_sub_29.99";
    static private final int REGISTER_PROFILE_CODE = 1;
    static private final int REQUEST_IN_APP_PURCHASE = 2;
    private String TAG = "SubscriptionController";
    // Global variables
    private IabHelper iabHelper;
    private ServiceConnection connection;
    private IInAppBillingService billingService;
    private IabHelper.OnIabPurchaseFinishedListener purchasedHandler;
    private IWPurchasedPackage purchasedPackage;
    private String subscriptionPackage;

    private UUID retrieveSubscriptionTaskId, retrieveUserTaskId;
    private boolean isRenew = false;
    private SubscriptionHandler subscriptionHandler;
    private ForegroundTask task;
    private TextView toolBarTitle;
    private IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener;
    //private RadioButton monthlyRadioButton, yearlyRadioButton;


    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_subscription);

        ButterKnife.bind(this);

        setFont();

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle = ((TextView) toolbar.findViewById(R.id.toolbar_title));
*/
       /* getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/

        AppDelegate.isLoginScreen = true;
       // toolBarTitle.setText("Subscription");

       // monthlyRadioButton = (RadioButton) findViewById(R.id.monthlyRadioButton);
        // yearlyRadioButton = (RadioButton) findViewById(R.id.yearlyRadioButton);

        // monthlyRadioButton.setChecked(true);





        subscriptionPackage = MONTHLY_SUBSCRIPTION;
        isRenew = getIntent().hasExtra("isRenew") && getIntent().getBooleanExtra("isRenew", false);
        // Initialize in-app billing
        final SubscriptionController weakThis = this;
        subscriptionHandler = new SubscriptionHandler(this, new ISubscriptionListener() {
            @Override
            public void onGetSubscriptionFinished(String subscriptionId) {
            }

            @Override
            public void onUpdateSubscriptionFinished() {
                weakThis.finish();
                overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
            }

            @Override
            public void onCreateSubscriptionFinished(String subscriptionId) {
                continueRegisterProfile(subscriptionId, false);
            }
        });

        final List<String> additionalSkuList = new ArrayList();
        additionalSkuList.add(MONTHLY_SUBSCRIPTION);
        additionalSkuList.add(YEARLY_SUBSCRIPTION);

        iabHelper = new IabHelper(this, Configuration.publicKey);
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            public void onIabSetupFinished(IabResult result) {
                /* Condition validation: Validate setup result */
                if (!result.isSuccess()) {
                    Toast.makeText(weakThis, weakThis.getText(R.string.google_play_service_not_available), Toast.LENGTH_LONG).show();

                }
//                iabHelper.queryInventoryAsync(true, additionalSkuList,
//                        mQueryFinishedListener);
            }
        });

        // In-app billing purchase handler
        purchasedHandler = new IabHelper.OnIabPurchaseFinishedListener() {

            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                // This left un-handle as Google does not return anything at purchased time. We need
                // to query the inventory again.
                Toast.makeText(weakThis, "onIabPurchaseFinished", Toast.LENGTH_LONG).show();

            }
        };

        // Bind in-app billing service
        connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                billingService = IInAppBillingService.Stub.asInterface(service);
                subscriptionHandler.setBillingService(billingService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                billingService = null;
            }
        };

        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        // This is the key line that fixed everything for me
        intent.setPackage("com.android.vending");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void setFont(){
        mTextViewAppInfoParent.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewAppInfoChild.setTypeface(FontUtils.getFontFabricGloberBold());
        mButtonMonthlySubscription.setTypeface(FontUtils.getFontFabricGloberBold());
        mButtonAnnualSubscription.setTypeface(FontUtils.getFontFabricGloberBold());
        mTextViewLearnMore.setTypeface(FontUtils.getFontFabricGloberBold());

    }

    @OnClick(R.id.tv_learnmore)
    void onClickOfLearnMore(){
        Intent intent = new Intent(SubscriptionController.this, SubscriptionGuidelinesController.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }

    @OnClick(R.id.btnAnnualSubscription)
    void annualSubscription(){
        Intent in = new Intent(SubscriptionController.this, NumberAuthenticationActivity.class);
        startActivity(in);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }


    // Called when the activity is becoming visible to the user.
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Called when the activity will startRecording interacting with the user.
    @Override
    protected void onResume() {
        super.onResume();
    }

    // Called when the system is about to startRecording resuming a previous activity.
    @Override
    protected void onPause() {
        super.onPause();
    }

    // Called when the activity is no longer visible to the user, because another activity has been resumed and is covering this one.
    @Override
    protected void onStop() {
        super.onStop();
    }

    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        unbindService(connection);
        if (task != null) {
            task.cancel(true);
        }

        if (subscriptionHandler != null) {
            subscriptionHandler.cancel(true);
        }
        super.onDestroy();
    }

    // Called after your activity has been stopped, prior to it being started again.
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    // <editor-fold defaultstate="collapsed" desc="Class's override public methods">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REGISTER_PROFILE_CODE: {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
                break;
            }
            case REQUEST_IN_APP_PURCHASE: {
                if (iabHelper == null || data == null) return;

                iabHelper.handleActivityResult(requestCode, resultCode, data);
                int responseCode = data.getIntExtra("RESPONSE_CODE", 1);
                if (resultCode == RESULT_OK && responseCode == 0) {
                    try {
                        if (isPurchased()) {
                            subscriptionHandler.createSubscription(purchasedPackage, isRenew);
                        }
                    } catch (Exception ex) {
                        Crittercism.logHandledException(ex);
                    }
                }

                break;
            }

            case 1001: {
//            default:
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                try {
                    if (isPurchased()) {
                        subscriptionHandler.createSubscription(purchasedPackage, isRenew);
                    }
                } catch (Exception ex) {
                    Crittercism.logHandledException(ex);
                }
                break;
            }
        }
    }

    public void handlePurchaseSubscription(View v) {

        //TODO this is temporary code change by sam

        String subscriptionId = "da473d63-2c2b-445b-837e-dd649e624591";

        Intent intent = new Intent(this, RegisterProfileController.class);
        intent.putExtra("subscriptionUuid", subscriptionId);
        intent.putExtra("receiptId", purchasedPackage.getOrderId());
        startActivityForResult(intent, REGISTER_PROFILE_CODE);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);


     /*   try {
            if (billingService != null) {
                //Already purchased and retriving data from server flow
                if (this.isPurchased()) {
                    this.retrieveSubscriptionFromServer();
                } else {
                    //new purchase flow
                    iabHelper.launchSubscriptionPurchaseFlow(this,
                            subscriptionPackage,
                            REQUEST_IN_APP_PURCHASE,
                            purchasedHandler, "");
                }
            } else {
                Toast.makeText(this, getString(R.string.google_play_service_not_available), Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Crittercism.logHandledException(ex);
        }*/
    }

    //selected subscription
    public void handleSubscriptionSelected(View v) {
        switch (v.getId()) {
           /* case R.id.monthlyRadioButton:
                subscriptionPackage = MONTHLY_SUBSCRIPTION;
                break;
            case R.id.yearlyRadioButton:
                subscriptionPackage = YEARLY_SUBSCRIPTION;
                break;*/
        }
    }

    // Class's private methods
    private boolean isPurchased() throws RemoteException {
        purchasedPackage = subscriptionHandler.getPurchasedPackage(subscriptionPackage);
        return purchasedPackage != null;
    }

    //user goes to create profile
    private void continueRegisterProfile(String subscriptionId, boolean subscriptionExists) {
        try {
            FwiJson purchasedData = FwiCodec.convertDataToJson(purchasedPackage.getPurchasedData());
            final String token = purchasedData.jsonWithPath("purchaseToken").getString();
            billingService.consumePurchase(Configuration.GOOGLE_PLAY_API_VERSION, getPackageName(), token);
        } catch (Exception ex) {
            Crittercism.logHandledException(ex);
        } finally {
            if (isRenew) {
                if (subscriptionExists) {
                    subscriptionHandler.updateSubscription(subscriptionId, purchasedPackage);
                } else { //created & extend User Subscription successful
                    this.finish();
                    overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
                }
            } else {
                Intent intent = new Intent(this, RegisterProfileController.class);
                intent.putExtra("subscriptionUuid", subscriptionId);
                intent.putExtra("receiptId", purchasedPackage.getOrderId());
                startActivityForResult(intent, REGISTER_PROFILE_CODE);
                overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
            }
        }
    }

    //Retrive subscriptionId from server
    private void retrieveSubscriptionFromServer() {
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                Configuration.kService_SubscriptionInfo,
                Configuration.kHostname,
                purchasedPackage.getOrderId()
        );

        retrieveSubscriptionTaskId = FwiIdUtils.generateUUID();
        task = new ForegroundTask(this, request, retrieveSubscriptionTaskId);

        task.run(this);
    }

    //Retrive userId from server
    private void retrieveUserIdFromServer(String subscriptionId) {
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                Configuration.kService_GET_Userid,
                Configuration.kHostname,
                subscriptionId
        );


        retrieveUserTaskId = FwiIdUtils.generateUUID();
        task = new ForegroundTask(this, request, retrieveUserTaskId);
        task.run(this);
    }

    //Retrive subscriptionId from server
    private void handleRetrieveSubscriptionFromServerDidFinish(int statusCode, FwiJson response) {
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            String subscriptionId = response.jsonWithPath("_embedded/subscription/0/id").getString();
            boolean isFound = !TextUtils.isEmpty(subscriptionId);
            if (isFound) {
                this.retrieveUserIdFromServer(subscriptionId);
//                this.continueRegisterProfile(subscriptionId, true);
            } else {
                subscriptionHandler.createSubscription(purchasedPackage, isRenew);
            }
        }
    }

    //Retrive userId from server based on subscriptionId
    private void handleRetrieveUserIdFromServerDidFinish(int statusCode, FwiJson response) {
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            try {
                String userId = response.jsonWithPath("user_id").getString();
                boolean isUserIdFound = !TextUtils.isEmpty(userId);
                String subscriptionId = response.jsonWithPath("subscription_id").getString();
                boolean isSubscriptionIdFound = !TextUtils.isEmpty(subscriptionId);
                if (!isUserIdFound && !isSubscriptionIdFound) {
                    this.continueRegisterProfile(subscriptionId, true);
                } else {
                    subscriptionHandler.createSubscription(purchasedPackage, isRenew);
                }
            } catch (Exception e) {
                subscriptionHandler.createSubscription(purchasedPackage, isRenew);
            }

        }
    }

    // TaskDelegate's members
    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        FwiJson r = (FwiJson) response;
        if (statusCode == -1) {

        } else if (taskId == retrieveSubscriptionTaskId) {
            this.handleRetrieveSubscriptionFromServerDidFinish(statusCode, r);
        } else if (taskId == retrieveUserTaskId) {
            this.handleRetrieveUserIdFromServerDidFinish(statusCode, r);

        }
    }
}