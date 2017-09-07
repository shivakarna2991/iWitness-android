package com.iwitness.androidapp.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.iwitness.androidapp.AppDelegate;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.FwiService;
import com.perpcast.lib.services.request.FwiRequest;

import org.apache.http.HttpStatus;

import java.util.Iterator;
import java.util.UUID;


public class ForegroundTask extends BackgroundTask {


    // Global variables
    private Context context;
    private ProgressDialog dialog;
    private boolean enableDialog;

    // Class's constructors
    public ForegroundTask(Context context, FwiRequest request) {
        super(request);
        init(context, true);
    }

    public ForegroundTask(Context context, FwiService service) {
        super(service, UUID.randomUUID());
        init(context, true);
    }

    public ForegroundTask(Context context, FwiRequest request, UUID taskId) {
        super(request, taskId);
        init(context, true);
    }

    public ForegroundTask(Context context, FwiService service, UUID taskId) {
        super(service, taskId);
        init(context, true);
    }

    public ForegroundTask(Context context, boolean enableDialog, FwiRequest request) {
        super(request);
        init(context, enableDialog);
    }

    public ForegroundTask(Context context, boolean enableDialog, FwiService service) {
        super(service, UUID.randomUUID());
        init(context, enableDialog);
    }

    public ForegroundTask(Context context, boolean enableDialog, FwiRequest request, UUID taskId) {
        super(request, taskId);
        init(context, enableDialog);
    }

    public ForegroundTask(Context context, boolean enableDialog, FwiService service, UUID taskId) {
        super(service, taskId);
        init(context, enableDialog);
    }


    // Class's override methods
    @Override
    protected void onCancelled() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (enableDialog) {
            dialog = new ProgressDialog(context);

            dialog.setCancelable(false);
            dialog.setMessage("Loading ...");
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        }
    }

    @Override
    protected void onPostExecute(Object response) {
        if (dialog != null) {
            dialog.dismiss();
        }

        // Should display error message or not
        int statusCode = service.status();

        if (HttpStatus.SC_OK <= statusCode && statusCode < HttpStatus.SC_MULTIPLE_CHOICES) {
            FwiJson message = (FwiJson) response;

            if (message != null && !message.isLike(FwiJson.Null())) {
//                AppDelegate.sharedInstance().presentHttpError(statusCode, (FwiJson) response);
            } else {
                AppDelegate.showAlertWithTitle(context, null, "Cancel", "Warning", "Could not connect to server at the moment.", "");
//                Toast.makeText(this.context, "Could not connect to server at the moment.", Toast.LENGTH_SHORT).show();
            }
        } else {
            String title = null;
            String detail = null;
            String message = null;
            FwiJson errorMessage;
//             Log.e("statusCode",statusCode+"");
            switch (statusCode) {
                case 400: {
                    title = "Warning";
                    detail = "Unable to process your request.";
                    errorMessage = (FwiJson) response;
                    if (errorMessage != null) {
                        detail = errorMessage.jsonWithPath("detail").getString();
                        title = errorMessage.jsonWithPath("title").getString();
                    }
                    //prameela
                    if (title != null && title.equalsIgnoreCase("Already Loggedin")) {

                    } else {
                        AppDelegate.showAlertWithTitle(context, "Ok", "Cancel", title, detail, "");
                    }
//                    Toast.makeText(this.context, detail, Toast.LENGTH_SHORT).show();
                    break;
                }
                case 401: {
                    errorMessage = (FwiJson) response;
                    if (errorMessage != null) {
                        message = errorMessage.jsonWithPath("detail").getString();
                        title = errorMessage.jsonWithPath("title").getString();
                        if (errorMessage.jsonWithPath("title").getString().toLowerCase().contains("expired")) {
                            title = errorMessage.jsonWithPath("title").getString();
                            AppDelegate.showAlertWithTitle(context, "Ok", null, title, message, "");
                            //prameela
                            //Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
//                            handleAccountExpired(message);
                        } else if (message.equalsIgnoreCase("User not logged in")) {
                            message = "Your iWitness account has been logged in on different device, if you did not authorize this change please go to www.iWitness.com and reset your password and log-in on your device using your updated credentials.";
                            title = errorMessage.jsonWithPath("title").getString();
                            AppDelegate.showAlertWithLogOut(context, "Ok", null, "iWitness", message, "");

                        } else {
                            AppDelegate.showAlertWithTitle(context, "Ok", null, title, message, "");
                            //prameela
                            //Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AppDelegate.showAlertWithTitle(context, "Ok", null, "iWitness", "Unauthorized access.", "");
                        //prameela
                        //Toast.makeText(this.context, "Unauthorized access.", Toast.LENGTH_SHORT).show();
                    }

                    break;

                }
                case 402: {
                    errorMessage = (FwiJson) response;
                    if (errorMessage != null) {
                        detail = errorMessage.jsonWithPath("detail").getString();
                        title = errorMessage.jsonWithPath("title").getString();
                        //prameela
                        AppDelegate.showAlertWithTitle(context, "Ok", null, title, message, "");
                    }

//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Payment Required: %@", detail]];
                    break;
                }
                case 403: {
//                    AppDelegate.showAlertWithTitle("Ok",null,title,message,"");
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Forbidden: %@", detail]];
                    break;
                }
                case 404: {
                    title = "Warning";
                    detail = "Resource is not available.";
                    errorMessage = (FwiJson) response;
                    if (errorMessage != null) {
                        detail = errorMessage.jsonWithPath("detail").getString();
                        AppDelegate.showAlertWithTitle(context, "Ok", null, title, detail, "");
                        //Toast.makeText(this.context, detail, Toast.LENGTH_SHORT).show();
                    } else {
                        AppDelegate.showAlertWithTitle(context, "Ok", null, title, detail, "");
                        //Toast.makeText(this.context, detail, Toast.LENGTH_SHORT).show();
                    }
//
                    break;
                }
                case 405: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Method Not Allowed: %@", detail]];
                    break;
                }
                case 406: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Not Acceptable: %@", detail]];
                    break;
                }
                case 407: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Proxy Authentication Required: %@", detail]];
                    break;
                }
                case 408: {
                    AppDelegate.showAlertWithTitle(context, "Ok", null, "Warning", "Server is busy at the moment.", "");
                    //Toast.makeText(this.context, "Server is busy at the moment.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 409: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Conflict: %@", detail]];
                    break;
                }
                case 410: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Gone: %@", detail]];
                    break;
                }
                case 411: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Length Required: %@", detail]];
                    break;
                }
                case 412: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Precondition Failed: %@", detail]];
                    break;
                }
                case 413: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Request Entity Too Large: %@", detail]];
                    break;
                }
                case 414: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Request-URI Too Large: %@", detail]];
                    break;
                }
                case 415: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Unsupported Media Type: %@", detail]];
                    break;
                }
                case 416: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Requested range not satisfiable: %@", detail]];
                    break;
                }
                case 417: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Expectation Failed: %@", detail]];
                    break;
                }
                case 418: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"I'm a teapot: %@", detail]];
                    break;
                }
                case 422: {
                    errorMessage = (FwiJson) response;
//                    Log.e("errorMessage","TEST"+errorMessage);
//                    Toast.makeText(this.context, errorMessage.toString(), Toast.LENGTH_SHORT).show();
                    if (errorMessage != null) {
                        message = errorMessage.jsonWithPath("detail").getString();
                        if (!errorMessage.jsonWithPath("validation_s").isLike(FwiJson.Null())) {
//                            Log.e("iffff","TEST"+"iffff");
                            FwiJson validation = errorMessage.jsonWithPath("validation_messages");
                            Iterator<String> i = validation.enumerateKeysAndValues();

                            title = i.next();
                            detail = validation.jsonWithPath(title).getString();

//                            Log.e("detail","TEST"+detail);
                            if (TextUtils.isEmpty(detail)) {
                                validation = validation.jsonWithPath(title);
                                i = validation.enumerateKeysAndValues();
                                title = i.next();
                                detail = validation.jsonWithPath(title).getString();
                            } else {
//                                Log.e("elseee","TEST"+"elseee");
                                message = errorMessage.jsonWithPath("detail").getString();
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(detail) && !TextUtils.isEmpty(message)) {
                        Toast.makeText(this.context, detail + message, Toast.LENGTH_SHORT).show();
                    } else if (!TextUtils.isEmpty(detail)) {
                        //commented below line as they are handling it in EditPasswordController.
//                        AppDelegate.showAlertWithTitle(context, "Ok", null, "iWitness", detail, "");
                        //prameela
                        //Toast.makeText(this.context, detail, Toast.LENGTH_SHORT).show();
                    } else if (!TextUtils.isEmpty(message)) {
                        //commented below line as they are handling it in EditPasswordController.
//                        AppDelegate.showAlertWithTitle(context, "Ok", null, "iWitness", message, "");
                        //prameela
                        //Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        //commented below line as they are handling it in EditPasswordController.
//                        AppDelegate.showAlertWithTitle(context, "Ok", null, "iWitness", "Validation failed. Please check your inputs", "");
                        //prameela
                        //Toast.makeText(this.context, "Validation failed. Please check your inputs", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case 423: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Locked: %@", detail]];
                    break;
                }
                case 424: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Failed Dependency: %@", detail]];
                    break;
                }
                case 425: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Unordered Collection: %@", detail]];
                    break;
                }
                case 426: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Upgrade Required: %@", detail]];
                    break;
                }
                case 428: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Precondition Required: %@", detail]];
                    break;
                }
                case 429: {
                    AppDelegate.showAlertWithTitle(context, "Ok", null, "Warning", "Server is busy at the moment.", "");
                    //prameela
                    // Toast.makeText(this.context, "Server is busy at the moment.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 431: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Request Header Fields Too Large: %@", detail]];
                    break;
                }
                case 500: {
                    AppDelegate.showAlertWithTitle(context, "Ok", null, "Warning", "Server is busy at the moment.", "");
                    //prameela
                    //Toast.makeText(this.context, "Server is busy at the moment.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 501: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Not Implemented: %@", detail]];
                    break;
                }
                case 502: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Bad Gateway: %@", detail]];
                    break;
                }
                case 503: {
                    AppDelegate.showAlertWithTitle(context, "Ok", null, "Warning", "This service is not available at the moment.", "");
                    //pameela
                    //Toast.makeText(this.context, "This service is not available at the moment.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 504: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Gateway Time-out: %@", detail]];
                    break;
                }
                case 505: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"HTTP Version not supported: %@", detail]];
                    break;
                }
                case 506: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Variant Also Negotiates: %@", detail]];
                    break;
                }
                case 507: {
                    AppDelegate.showAlertWithTitle(context, "Ok", null, "Warning", "Uploaded file had been rejected by server.", "");
                    //prameela
                    //Toast.makeText(this.context, "Uploaded file had been rejected by server.", Toast.LENGTH_SHORT).show();
                    break;
                }
                case 508: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Loop Detected: %@", detail]];
                    break;
                }
                case 511: {
//                    [kAppDelegate presentAlertWithTitle:title message:[NSString stringWithFormat:@"Network Authentication Required: %@", detail]];
                    break;
                }
                case -1: {
                    //commented below line, as the ios app has been showing different texts for internet not being connected, which depends on the module currently opened.
//                        AppDelegate.showAlertWithTitle(context, "Cancel", null, "iWitness", "Network is not available!", "");

                    break;
                }
                default:
                    //prmeela
                    AppDelegate.showAlertWithTitle(context, "Ok", null, "Warning", "Uploaded file had been rejected by server.", "");
                    //Toast.makeText(this.context, "Could not connect to server at the moment.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

//        this.context.unbindService(connection);
        super.onPostExecute(response);
    }


//    /**
//     * Handle event when account expired
//     *
//     * @param message
//     */
//    private void handleAccountExpired(String message) {
//
//        UserPreferences userPreferences = UserPreferences.sharedInstance();
//        if (userPreferences.userProfile() != null
//                && billingService != null) {
//            try {
//                IWPurchasedPackage purchasedPackage = subscriptionHandler.getPurchasedPackage();
//
//                /**
//                 * Renew automatically.
//                 * This is only happened when user finish trial (only once)
//                 */
//                if (purchasedPackage != null) {
//                    extendSubscriptionAfterTrial(purchasedPackage);
//                } else { //renew manually
//                    renewSubscription();
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
//        }
//    }

//    /**
//     * Renew Subscription manually
//     */
//    private void renewSubscription() {
//        new AlertDialog.Builder(context)
//                .setTitle(context.getString(R.string.subscription_expired))
//                .setMessage(context.getString(R.string.renew_subscription))
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.dismiss();
//                        Intent i = new Intent(context, SubscriptionController.class);
//                        i.putExtra("isRenew", true);
//                        context.startActivity(i);
//                        if (context instanceof Activity) {
//                            ((Activity) context).overridePendingTransition(
//                                    R.anim.enter_slide_to_left,
//                                    R.anim.exit_slide_to_left);
//                        }
//                    }
//                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // Do nothing.
//                dialog.dismiss();
//            }
//        }).show();
//    }

//    /**
//     * Extend subscription after trial
//     *
//     * @param purchasedPackage
//     */
//    private void extendSubscriptionAfterTrial(IWPurchasedPackage purchasedPackage) {
//        subscriptionHandler.updateSubscription(purchasedPackage);
//    }


    // Class's private methods
    private void init(Context context, boolean enableDialog) {
        this.context = context;
        this.enableDialog = enableDialog;

//        // Bind in-app billing service
//        subscriptionHandler = new SubscriptionHandler(context, new ISubscriptionListener() {
//
//            @Override
//            public void onGetSubscriptionFinished(String subscriptionId) {
//            }
//            @Override
//            public void onUpdateSubscriptionFinished() {
//            }
//            @Override
//            public void onCreateSubscriptionFinished(String subscriptionId) {
//            }
//        });
//        connection = new ServiceConnection() {
//
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                billingService = IInAppBillingService.Stub.asInterface(service);
//                subscriptionHandler.setBillingService(billingService);
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                billingService = null;
//            }
//        };
//        this.context.bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), connection, Context.BIND_AUTO_CREATE);
    }
}
