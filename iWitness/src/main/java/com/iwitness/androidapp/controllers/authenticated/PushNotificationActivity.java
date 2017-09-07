
package com.iwitness.androidapp.controllers.authenticated;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.network.TaskDelegate;

import java.util.UUID;

public class PushNotificationActivity extends FragmentActivity implements TaskDelegate {


    private final String Action = "Action";

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_notification_action);

        if (getIntent().hasExtra(Action)) {
            Bundle extras = getIntent().getExtras();
            handleAction(extras.getString(Action));
        }
    }

    private void handleAction(String action) {
        if (action.equals("ProfileUpdated")) {
            updateProfile();    
        }
    }

    private void updateProfile() {
        //TODO: Implement this
//        Toast.makeText(this, "this is a test", Toast.LENGTH_LONG).show();

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
        super.onDestroy();
    }

    // Called after your activity has been stopped, prior to it being started again.
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    // </editor-fold>



    // <editor-fold defaultstate="collapsed" desc="Class's override public methods">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu., menu);

        return super.onCreateOptionsMenu(menu);
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Class's event handlers">
    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {

    }


}