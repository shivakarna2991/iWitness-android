package com.iwitness.androidapp.controllers.common;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by prameela on 1/26/2016.
 */
public class FragmentPermissionRequestor implements PermissionRequestor{

    private static final String TAG = FragmentPermissionRequestor.class.getSimpleName();
    public PermissionListener mCallback;
    private Fragment fragment;

    public FragmentPermissionRequestor(Fragment fragment) {
        this.fragment = fragment;
    }
    public void request(final String permission
            , final int requestCode
            , String message
            , PermissionListener callback) {
        Log.e(TAG, "called request method with request code " + requestCode + " for permission " + permission);
        mCallback = callback;
        int hasPermission = ContextCompat.checkSelfPermission(fragment.getContext(), permission);
        Log.e("hasPermission..", "TEST" + hasPermission);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                Log.i(TAG, "shouldShowRequestPermissionRationale returned true, we should show an explanation");
                //There is no penalty for the permission being denied the first time
                /**
                 * shouldShowRequestPermissionRationale() method to check
                 * whether the application has previously requested the specified permission and been denied.
                 * This will allow error states to be correctly displayed on the screen before requesting the permission,
                 * allowing the explanation of why the permission is needed before requesting access again.
                 */

                /**
                 * The next time that you request the same permission after it being denied for the first time,
                 * the user will be granted the option for this request not to occur again.
                 */

//                showMessageOKCancel(fragment, message,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
                                _makeRequest(fragment, permission, requestCode);
//                            }
//                        });
                return;
            } else {
                // No explanation needed, we can request the permission.
                _makeRequest(fragment, permission, requestCode);
                /**
                 * a dialog will be displayed to the user prompting their response to our request.
                 * Once responded, the onRequestPermissionsResult() method will be called
                 */
                Log.i(TAG, "shouldShowRequestPermissionRationale returned false, No explanation needed, we can request the permission.");
            }
        } else {
            //we already have permission!
            //_makeCall();
            Log.i(TAG, "we already have permission!");
            callback.onPermissionGranted();
            callback = null;
        }
    }

    private void _makeRequest(Fragment thisActivity, String permission, int requestCode) {
       /* ActivityCompat.requestPermissions(thisActivity,
                new String[]{permission},
                requestCode);*/
        thisActivity.requestPermissions(new String[]{permission}, requestCode);
    }

    private void showMessageOKCancel(Fragment thisActivity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(thisActivity.getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void _onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult called!");
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was grantedDo the related task you need to do.
            mCallback.onPermissionGranted();
        } else {
            // permission denied! Disable the functionality that depends on this permission.
            mCallback.onPermissionDenied();
        }
    }

}
