package com.iwitness.androidapp.controllers.authenticated.home;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.KeyValuePairs;
import com.iwitness.androidapp.controllers.adapters.SlideMenuAdapter;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.ContactsFragment;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.NotificationsListFragment;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.ProfileFragment;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.RecordVideoFragment;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.SettingsFragment;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.ShareFragment;
import com.iwitness.androidapp.controllers.authenticated.home.fragments.VideosFragment;
import com.iwitness.androidapp.controllers.authentication.LoginController;
import com.iwitness.androidapp.controllers.common.Call911Controller;
import com.iwitness.androidapp.controllers.common.listeners.UpdateMenuPosListener;
import com.iwitness.androidapp.controllers.common.listeners.UpdateSideMenuPosFragmentsListener;
import com.iwitness.androidapp.libraries.ConfigurationFactory;
import com.iwitness.androidapp.libraries.FwiLocation;
import com.iwitness.androidapp.libraries.utils.FontUtils;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.SlideMenuItem;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.LogoutAllInterface;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeContainerController extends AppCompatActivity implements LocationListener, UpdateMenuPosListener, UpdateSideMenuPosFragmentsListener, TaskDelegate, LogoutAllInterface {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.signoutButton)
    Button signOutButton;

    @BindView(R.id.slideMenuLayout)
    RelativeLayout slideMenuLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.menuListView)
    ListView menuListView;

    // Global variables
    private FwiLocation locationManager;
    private ActionBarDrawerToggle drawerToggle;
    public int currentPosition = -1;
    public String TAG = "";
    private TextView toolBarTitle;
    private TextView mScreenTitle;

    Integer menu_icons[] = {R.drawable.iw_menu_btn_camera, R.drawable.iw_menu_btn_preferences, R.drawable.iw_menu_btn_contacts,
            R.drawable.iw_menu_btn_videos, R.drawable.iw_menu_btn_tips, R.drawable.iw_menu_btn_demo,
            R.drawable.iw_menu_btn_share, R.drawable.iw_menu_btn_profile};

    private SlideMenuAdapter slideMenuadapter;
    private UUID getLogOutTaskId;
    private ForegroundTask task;
    static AlertDialog dialog = null;
    private Menu menu;
    private ToggleButton mStartRecordingButton;
    private ImageView mSwitchTorch;
    private ImageView mSwitchCamera;
    private LinearLayout mRecordButtonLayout, mToggleFlashLayut, mToggleCameraLayout, mScreenInformationLayout;
    private TextView mTextViewCall911;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.view_home);
        ButterKnife.bind(this);

        setToolbarButtons();

        init();

        final HomeContainerController weakThis = this;

        // Add drawer event listener
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);


        menuListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
//        // Add menu event listener
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                SlideMenuItem menuItem = (SlideMenuItem) parent.getItemAtPosition(position);
                slideMenuadapter.updatePosition(position);
                getSupportActionBar().setTitle(menuItem.getTitle());
                drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        selectItem(position);
                        slideMenuadapter.notifyDataSetChanged();
                    }
                }, 300);
            }
        });


        // Add sign out event listener
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPreferences.sharedInstance().reset();
                Intent intent = new Intent(weakThis, LoginController.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mTextViewCall911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeContainerController.this, Call911Controller.class);
                if(locationManager.getCurrentLocation() != null)  {
                    LatLng pos = locationManager.getCurrentLocation();
                    intent.putExtra("initialLat", pos.latitude);
                    intent.putExtra("initialLong", pos.longitude);
                } else {
                    intent.putExtra("initialLat", "");
                    intent.putExtra("initialLong", "");
                }
                intent.setAction(Call911Controller.CALL911_SHOULD_CREATE_CAMERA);
                startActivity(intent);
            }
        });

    }

    private void setToolbarButtons() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolBarTitle = ((TextView) toolbar.findViewById(R.id.toolbar_title));
        mScreenTitle = (TextView) toolbar.findViewById(R.id.screen_title);
        mStartRecordingButton = (ToggleButton) toolbar.findViewById(R.id.btnRec);
        mSwitchTorch = (ImageView) toolbar.findViewById(R.id.swEnableTorch);
        mSwitchCamera = (ImageView) toolbar.findViewById(R.id.switch_camera);
        mRecordButtonLayout = (LinearLayout) toolbar.findViewById(R.id.recordButtonLayout);
        mToggleFlashLayut = (LinearLayout) toolbar.findViewById(R.id.toggleFlashLayout);
        mToggleCameraLayout = (LinearLayout) toolbar.findViewById(R.id.toggleCameraLayout);
        mScreenInformationLayout = (LinearLayout) toolbar.findViewById(R.id.screenInformationLayout);
        mTextViewCall911 = (TextView) toolbar.findViewById(R.id.textViewCall911);

        mTextViewCall911.setTypeface(FontUtils.getFontFabricGloberBold());
        mScreenTitle.setTypeface(FontUtils.getFontFabricGloberBold());

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void init() {
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        slideMenuadapter = new SlideMenuAdapter(this, R.layout.slide_menu_item_new, getMenuItems());
        menuListView.setAdapter(slideMenuadapter);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();

        // Register notification
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        if (userPreferences.isFirstLogin() || userPreferences.isFirstRegistered()) {
            selectItem(0);
        } else {
            selectItem(currentPosition);
        }
    }

    // Called when the activity is becoming visible to the user.
    @Override
    protected void onStart() {
        super.onStart();
        if (locationManager == null) {
            locationManager = new FwiLocation(this, this, true);
        }
        locationManager.displayLocationSettingsRequest(this);

        /* Condition validation: Ignore if current lat & lng are equal to zero */
        LatLng location = locationManager.getCurrentLocation();
        if (location.latitude == 0.0f && location.longitude == 0.0f) return;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final HomeContainerController weakThis = this;
        getCurrentLocationViaJSON(location.latitude, location.longitude, weakThis);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FwiLocation.ENABLE_LOCATION_REQUEST) {

        } else if (requestCode == FwiLocation.REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (locationManager != null) {
                        locationManager.updateGPSEnabledness();
                        locationManager.requestLocationChanged();
                        locationManager.updateCoordinate();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    public static JSONObject getLocationInfo(double lat, double lng, HomeContainerController weakThis) {

        String urlString = String.format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=true", lat, lng);
        HttpGet httpGet = new HttpGet(urlString);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public String getCurrentLocationViaJSON(double lat, double lng, final HomeContainerController weakThis) {
        JSONObject jsonObj = getLocationInfo(lat, lng, weakThis);
        String currentLocation = "testing";

        try {
            String status = jsonObj.getString("status").toString();
            String countryName;
            if (status.equalsIgnoreCase("OK")) {
                JSONArray results = jsonObj.getJSONArray("results");
                int i = 0;
                do {
                    JSONObject json = results.getJSONObject(i);
                    JSONArray address_components = json.getJSONArray("address_components");

                    for (int k = 0; k < address_components.length(); k++) {
                        JSONObject jsonObjAddress = address_components.getJSONObject(k);
                        JSONArray typesArray = jsonObjAddress.getJSONArray("types");
                        String types = typesArray.getString(0);

                        if (types.equalsIgnoreCase("country")) {
                            countryName = jsonObjAddress.optString("long_name");

                            if (countryName.equalsIgnoreCase("United States of America")) {
                                countryName = "United States";
                            }

                            ConfigurationFactory configurationFactory = new ConfigurationFactory(weakThis);
                            final KeyValuePairs emergencyNumbers = configurationFactory.getEmergencyNumbers();
                            final UserPreferences userPreferences = UserPreferences.sharedInstance();
                            final KeyValuePairs pair = emergencyNumbers.getList(countryName);

                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(weakThis, android.R.layout.select_dialog_singlechoice, emergencyNumbers.getKeys());
                            adapter.sort(new Comparator<String>() {
                                @Override
                                public int compare(String s1, String s2) {
                                    return s1.compareTo(s2);
                                }
                            });
                            if (userPreferences.getEmergencyIndex() == -1) {
                                userPreferences.setEmergencyIndex(adapter.getPosition(pair.getKey()));
                                userPreferences.setEmergencyNumberLocation(pair.getDisplayName());
                                userPreferences.setEmergencyLocation(pair.getKey());
                                userPreferences.setEmergencyNumber(pair.getDisplayName());
                                userPreferences.save();
                            }

                            if (pair != null && pair.getDisplayName() != null && pair.getDisplayName().length() > 0 && !pair.getDisplayName().equalsIgnoreCase(userPreferences.getEmergencyNumber())) {
                                String message = String.format(weakThis.getString(R.string.emergency_number_changed), userPreferences.getEmergencyNumber(), pair.getDisplayName());
                                if (dialog == null || !dialog.isShowing()) {
                                    dialog = new AlertDialog.Builder(weakThis)
                                            .setTitle(R.string.warning)
                                            .setMessage(message)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    dialog.dismiss();

                                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(weakThis, android.R.layout.select_dialog_singlechoice, emergencyNumbers.getKeys());
                                                    adapter.sort(new Comparator<String>() {
                                                        @Override
                                                        public int compare(String s1, String s2) {
                                                            return s1.compareTo(s2);
                                                        }
                                                    });

                                                    userPreferences.setEmergencyIndex(adapter.getPosition(pair.getKey()));
                                                    userPreferences.setEmergencyNumber(pair.getDisplayName());
                                                    userPreferences.setEmergencyLocation(pair.getKey());
                                                    userPreferences.save();
                                                    if (menu != null) {
                                                        MenuItem item = menu.getItem(0);
                                                        UserPreferences userPreferences = UserPreferences.sharedInstance();
                                                        item.setTitle(String.format(getString(R.string.call_now) + " " + userPreferences.getEmergencyNumber()));
                                                    }
                                                    // Send broadcast to update new phone number
                                                    Intent intent = new Intent(UserPreferences.class.getName());
                                                    LocalBroadcastManager.getInstance(AppDelegate.getAppContext()).sendBroadcast(intent);
                                                }
                                            })
                                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                                    dialog.setCancelable(false);

                                    dialog.show();
                                }
                            }
                            break;
                        }
                    }
                    i++;
                } while (i < results.length());
                return currentLocation;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.stopUsingGPS();
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count > 0) {
            VideosFragment fragmentVideos = (VideosFragment) getFragmentManager()
                    .findFragmentByTag("VideosFragment");

            NotificationsListFragment fragmentNotifications = (NotificationsListFragment) getFragmentManager()
                    .findFragmentByTag("NotificationsListFragment");

            if (fragmentVideos != null) {
                currentPosition = 4;
            }
            if (fragmentNotifications != null) {
                currentPosition = 2;
            }
        }

        if (this.currentPosition == 0) {
            this.finish();
        } else {
            getSupportActionBar().setTitle(R.string.iwitness_title);
            selectItem(0);
            slideMenuadapter.updatePosition(0);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return false;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return false;
            }
            case R.id.call911Item: {
                LatLng pos = locationManager.getCurrentLocation();
                Intent intent = new Intent(this, Call911Controller.class);
                intent.putExtra("initialLat", pos.latitude);
                intent.putExtra("initialLong", pos.longitude);
                intent.setAction(Call911Controller.CALL911_SHOULD_CREATE_CAMERA);
                startActivity(intent);
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private ArrayList<SlideMenuItem> getMenuItems() {
        ArrayList<SlideMenuItem> menuItemsList = new ArrayList<>();
        menuItemsList.add(new SlideMenuItem(getString(R.string.home), menu_icons[0]));
        menuItemsList.add(new SlideMenuItem(getString(R.string.preferences), menu_icons[1]));
        menuItemsList.add(new SlideMenuItem(getString(R.string.emergency_contacts), menu_icons[2]));
        menuItemsList.add(new SlideMenuItem(getString(R.string.videos), menu_icons[3]));
        menuItemsList.add(new SlideMenuItem(getString(R.string.tips_tutorials), menu_icons[4]));
        menuItemsList.add(new SlideMenuItem(getString(R.string.demo), menu_icons[5]));
        menuItemsList.add(new SlideMenuItem(getString(R.string.share), menu_icons[6]));
        menuItemsList.add(new SlideMenuItem(getString(R.string.profile), menu_icons[7]));



        // menuItemsList.add(new SlideMenuItem(getString(R.string.notifications_title), menu_icons[2]));


        return menuItemsList;
    }

    /**
     * Handle Navigation Drawer OnItemSelected
     *
     * @param position
     */
    // Class's private methods
    private void selectItem(int position) {
        Bundle args = new Bundle();
        Fragment fragment = null;

        switch (position) {
            case 0: { //Home
                enableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.GONE);
                fragment = new RecordVideoFragment();
                TAG = RecordVideoFragment.class.getSimpleName();
                toolBarTitle.setText("" + (UserPreferences.sharedInstance().recordTime() * 1000 / (60 * 1000) + ":00"));
                toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_background_color));
                break;
            }

            case 1: //Preferences
                disableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.VISIBLE);
                fragment = new SettingsFragment();
                TAG = SettingsFragment.class.getSimpleName();
                mScreenTitle.setText("Preferences");
                toolbar.setBackgroundColor(getResources().getColor(R.color.color_black));
                break;

            case 2: //Emergency Contacts
                disableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.VISIBLE);
                fragment = new ContactsFragment();
                TAG = ContactsFragment.class.getSimpleName();
                mScreenTitle.setText(R.string.emergency_contacts);
                toolbar.setBackgroundColor(getResources().getColor(R.color.color_black));
                break;

            case 3: //Videos
                disableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.VISIBLE);
                fragment = new VideosFragment();
                TAG = VideosFragment.class.getSimpleName();
                mScreenTitle.setText(R.string.videos);
                toolbar.setBackgroundColor(getResources().getColor(R.color.color_black));
                break;


            case 4: //Tips/Tutorials
                disableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.VISIBLE);
                fragment = new NotificationsListFragment();
                TAG = NotificationsListFragment.class.getSimpleName();
                mScreenTitle.setText(R.string.notifications_title);
                toolbar.setBackgroundColor(getResources().getColor(R.color.color_black));
                break;

            case 5: //Demo
                break;

            case 6: // Share the app
                disableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.VISIBLE);
                fragment = new ShareFragment();
                TAG = ShareFragment.class.getSimpleName();
                mScreenTitle.setText(R.string.share);
                toolbar.setBackgroundColor(getResources().getColor(R.color.color_black));
                break;

            case 7: //Profile
                disableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.VISIBLE);
                fragment = new ProfileFragment();
                TAG = ProfileFragment.class.getSimpleName();
//                    setTitle(R.string.profile);
                mScreenTitle.setText("Profile");
                toolbar.setBackgroundColor(getResources().getColor(R.color.color_black));
                break;

         /*   case 8: //Logout

                enableActionbarButtons();
                mScreenInformationLayout.setVisibility(View.GONE);
               // disableActionbarButtons();
                if (AppDelegate.isNetworkAvailable(this)) {
                    getLogOut();
                } else {
                    showAlert("iWitness", "Failed to Logout");
                }
                break;*/





            /*case 1: { //Profile
                disableActionbarButtons();
                fragment = new ProfileFragment();
                TAG = ProfileFragment.class.getSimpleName();
//                    setTitle(R.string.profile);
                toolBarTitle.setText(R.string.profile);
                break;
            }
            case 2: { //Tutorial
                disableActionbarButtons();
                fragment = new NotificationsListFragment();
                TAG = NotificationsListFragment.class.getSimpleName();
                toolBarTitle.setText(R.string.notifications_title);
                break;
            }
            case 3: { //Contact
                disableActionbarButtons();
                fragment = new ContactsFragment();
                TAG = ContactsFragment.class.getSimpleName();
                toolBarTitle.setText(R.string.emergency_contacts);
                break;
            }
            case 4: { //Videos
                disableActionbarButtons();
                fragment = new VideosFragment();
                TAG = VideosFragment.class.getSimpleName();
                toolBarTitle.setText(R.string.videos);
                break;
            }
            case 5: { //Settings,preferences
                disableActionbarButtons();
                fragment = new SettingsFragment();
                TAG = SettingsFragment.class.getSimpleName();
                toolBarTitle.setText(R.string.preferences);
                break;
            }
            case 6: { //Share
                disableActionbarButtons();
                fragment = new ShareFragment();
                TAG = ShareFragment.class.getSimpleName();
                toolBarTitle.setText(R.string.share);
                break;
            }
            case 7: { //Logout
                disableActionbarButtons();
                if (AppDelegate.isNetworkAvailable(this)) {
                    getLogOut();
                } else {
                    showAlert("iWitness", "Failed to Logout");
                }
                break;
            }*/


        }
        if (fragment != null) {
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                    .addToBackStack(null).commit();
        }

        menuListView.setItemChecked(position, true);
        drawerLayout.closeDrawer(slideMenuLayout);
        this.currentPosition = position;
    }

    private void disableActionbarButtons() {
        mToggleCameraLayout.setVisibility(View.GONE);
        mToggleFlashLayut.setVisibility(View.GONE);
        mRecordButtonLayout.setVisibility(View.GONE);
        toolBarTitle.setVisibility(View.VISIBLE);
        mStartRecordingButton.setVisibility(View.GONE);
        mSwitchCamera.setVisibility(View.GONE);
        mSwitchTorch.setVisibility(View.GONE);
    }

    private void enableActionbarButtons() {
        mToggleCameraLayout.setVisibility(View.VISIBLE);
        mToggleFlashLayut.setVisibility(View.VISIBLE);
        mRecordButtonLayout.setVisibility(View.VISIBLE);
        mStartRecordingButton.setVisibility(View.VISIBLE);
        mSwitchCamera.setVisibility(View.VISIBLE);
        mSwitchTorch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (locationManager != null) {
            /* Condition validation: Ignore if current lat & lng are equal to zero */

            if (location.getLatitude() == 0.0f && location.getLongitude() == 0.0f) return;

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            final HomeContainerController weakThis = this;
            getCurrentLocationViaJSON(location.getLatitude(), location.getLongitude(), weakThis);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void updateMenuPos(int position) {
        menuListView.setItemChecked(position, true);
    }

    public void showAlert(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Mark this version as read.
                        dialogInterface.dismiss();
                    }
                });

        builder.create().show();
    }

    @Override
    public void updateSideMenuPos(int position) {
        selectItem(position);
    }


    private void getLogOut() {
        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                Configuration.kService_User_LogOut,
                Configuration.kHostname,
                UserPreferences.sharedInstance().currentProfileId()
        );

        getLogOutTaskId = FwiIdUtils.generateUUID();
        task = new ForegroundTask(this, request, getLogOutTaskId);
        task.run(HomeContainerController.this);
    }

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {
        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            String responseLogout = ((FwiJson) response).toString();
            FwiJson updatedProfile = (FwiJson) response;
            if (responseLogout != null) {
                UserPreferences.sharedInstance().reset();
                HomeContainerController weakThis = this;
                Intent intent = new Intent(weakThis, LoginController.class);
                intent.putExtra("fromLogout", "fromLogout");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        } else if (statusCode == -1) {

        }
    }

    @Override
    public void taskCompleted(Object response) {
        HomeContainerController weakThis = this;
        Intent intent = new Intent(weakThis, LoginController.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
