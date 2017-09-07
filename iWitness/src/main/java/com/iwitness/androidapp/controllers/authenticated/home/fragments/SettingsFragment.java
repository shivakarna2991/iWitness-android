package com.iwitness.androidapp.controllers.authenticated.home.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.KeyValuePairs;
import com.iwitness.androidapp.controllers.common.FragmentPermissionRequestor;
import com.iwitness.androidapp.controllers.common.PermissionListener;
import com.iwitness.androidapp.controllers.common.PermissionRequestor;
import com.iwitness.androidapp.libraries.ConfigurationFactory;
import com.iwitness.androidapp.libraries.FwiLocation;
import com.iwitness.androidapp.model.UserPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;


public class SettingsFragment extends Fragment
        implements Switch.OnCheckedChangeListener,
        View.OnClickListener,
        DialogInterface.OnClickListener, LocationListener {
    private TextView tvEnableShakeToCall911;
    private Switch enableTorch, swEnableShakeToCall911, swEnableTapToCall911;
    private EditText txtEmergencyNumber;
    private EditText txtRecordTime;
    private UserPreferences userPreferences;
    private KeyValuePairs emergencyNumbers;
    private int currentMinuteIndex = 0, currentSecondIndex = 0;
    private int emergencyNumberIndex = -1;
    private int indexRecordPos = -1;

    private boolean isPopupShown = false;
    private FwiLocation locationManager;
    private Location location;
    Menu OncreateOptionsmenu;
    //    ActionBar actionBar;
    private PermissionRequestor _requestor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _requestor = new FragmentPermissionRequestor(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        enableTorch = (Switch) getView().findViewById(R.id.swEnableTorch);
        enableTorch.setOnCheckedChangeListener(this);
        swEnableShakeToCall911 = (Switch) getView().findViewById(R.id.swEnableShakeToCall911);
        swEnableTapToCall911 = (Switch) getView().findViewById(R.id.swEnableTapToCall911);

        swEnableShakeToCall911.setOnCheckedChangeListener(this);
        swEnableTapToCall911.setOnCheckedChangeListener(this);

        txtEmergencyNumber = (EditText) getView().findViewById(R.id.txtEmergencyNumber);
        txtEmergencyNumber.setOnClickListener(this);

        txtRecordTime = (EditText) getView().findViewById(R.id.txtRecordTime);
        txtRecordTime.setOnClickListener(this);

        ConfigurationFactory configurationFactory = new ConfigurationFactory(getActivity());
        emergencyNumbers = configurationFactory.getEmergencyNumbers();

        userPreferences = UserPreferences.sharedInstance();
        tvEnableShakeToCall911 = (TextView) getView().findViewById(R.id.tvEnabledShakeToCall911);
        tvEnableShakeToCall911.setText(String.format(getString(R.string.enable_shake_to_call_911), userPreferences.getEmergencyNumber()));


        setEmergencyNumberIndex(emergencyNumbers);

        getSettings();
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        OncreateOptionsmenu = menu;
        OncreateOptionsmenu.clear();
        inflater.inflate(R.menu.home, OncreateOptionsmenu);
        MenuItem item = OncreateOptionsmenu.getItem(0);
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        Log.e("N", "TEST" + userPreferences.getEmergencyNumber());
        item.setTitle(String.format(getString(R.string.call_now) + " " + userPreferences.getEmergencyNumber()));
        super.onCreateOptionsMenu(OncreateOptionsmenu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                return false;
            }
//            case R.id.call911Item: {
//                Intent intent = new Intent(getActivity(), Call911Controller.class);
//                intent.setAction(Call911Controller.CALL911_SHOULD_CREATE_CAMERA);
//                startActivity(intent);
//                return false;
//            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (locationManager == null) {
            locationManager = new FwiLocation(getActivity(), this, false);
        }

        tvEnableShakeToCall911.setText(String.format(getString(R.string.enable_shake_to_call_911), userPreferences.getEmergencyNumber()));
    }

    private void setEmergencyNumberIndex(KeyValuePairs emergencyNumbers) {
//        String emergencyLocation = userPreferences.getEmergencyLocation();
        txtEmergencyNumber.setText(userPreferences.getEmergencyNumber());
    }

    private void getSettings() {
        enableTorch.setChecked(userPreferences.enableTorch());
        swEnableShakeToCall911.setChecked(userPreferences.enableShake());
        swEnableTapToCall911.setChecked(userPreferences.enableTap());

        int recordTime = userPreferences.recordTime();
        currentMinuteIndex = recordTime / 60;
        currentSecondIndex = 0;
        if (currentMinuteIndex == 0) {
            currentMinuteIndex = 10;
            indexRecordPos = 0;
        }

        int val = userPreferences.getRecordIndex();
        txtRecordTime.setText(currentMinuteIndex + ":" + "00");
    }

    /**
     * handle on choose record time
     */
   /* private void handleOnChooseRecordTime() {
//        if (isPopupShown) return;
//        isPopupShown = true;

        final SettingsFragment weakThis = this;
        final Context weakContext = getActivity();
        final String[] minuteValues ={"10","20","30"};

      *//*  AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Record");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_single_choice);
        arrayAdapter.add("10");
        arrayAdapter.add("20");
        arrayAdapter.add("30");

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                getActivity());
                        String strName = arrayAdapter.getItem(which);
                        currentMinuteIndex = Integer.parseInt(strName);
                        Log.e("currentMinuteIndex", "TEST.." + currentMinuteIndex);
//                        builderInner.setMessage(strName);
//                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        int time = currentMinuteIndex * 60;
                                        Log.e("time","TEST.."+time);
                                        txtRecordTime.setText(currentMinuteIndex+"");
                                        userPreferences.setRecordTime(time);
                                        userPreferences.save();
//                                        isPopupShown = false;
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
        builderSingle.show();*//*


     View convertView = getActivity().getLayoutInflater().inflate(R.layout.number_picker_2_columns, null);

        NumberPicker npMinutes = (NumberPicker) convertView.findViewById(R.id.np1);
        npMinutes.setMinValue(1);
        npMinutes.setMaxValue(3);
        npMinutes.setDisplayedValues(minuteValues);
        npMinutes.setValue(currentMinuteIndex);

        NumberPicker npSeconds = (NumberPicker) convertView.findViewById(R.id.np2);
        //set min,max Seconds Values
        npSeconds.setMinValue(00);
        npSeconds.setMaxValue(00);
        npSeconds.setValue(currentSecondIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(convertView)
                .setTitle(R.string.record_time)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AlertDialog current = (AlertDialog)dialog;
                        NumberPicker npMinutes = (NumberPicker) current.findViewById(R.id.np1);
                        currentMinuteIndex = npMinutes.getValue();
                        currentMinuteIndex = currentMinuteIndex*10;

                        NumberPicker npSeconds = (NumberPicker) current.findViewById(R.id.np2);
                        currentSecondIndex = npSeconds.getValue();

                        Log.e("builder", "TEST.." + currentMinuteIndex + ":" + currentSecondIndex);
                        txtRecordTime.setText(String.format("%s:%s",
                                String.format("%02d", currentMinuteIndex),
                                String.format("%02d", currentSecondIndex)));

                        int time = currentMinuteIndex * 60 + currentSecondIndex;
                        Log.e("time", "TEST.." + time);

//                        if (time < 30) {
//                            weakThis.getSettings();
//
//                            AlertDialog.Builder ab = new AlertDialog.Builder(weakContext);
//                            ab.setTitle(getString(R.string.record_time))
//                                    .setMessage(getString(R.string.min_recording_time_required))
//                                    .setPositiveButton(weakContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                        }
//                                    })
//                                    .show();
//                        }
//                        else {
                            userPreferences.setRecordTime(time);
                            userPreferences.save();
//                        }

                        isPopupShown = false;
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupShown = false;
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }
*/

    /**
     * handle On Choose emergency number
     */
    private void handleOnChooseEmergencyNumber() {
        if (isPopupShown) return;
        isPopupShown = true;

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice, emergencyNumbers.getKeys());
        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });


        if (emergencyNumberIndex == -1) {
            emergencyNumberIndex = adapter.getPosition(userPreferences.getEmergencyLocation());
        }
        ContextThemeWrapper cw = new ContextThemeWrapper(getActivity(), R.style.AlertDialogTheme);
        AlertDialog dialog = new AlertDialog.Builder(cw)
                .setTitle(R.string.emergency_number_title)
                .setSingleChoiceItems(
                        adapter,
                        emergencyNumberIndex,
                        null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupShown = false;
                        dialog.dismiss();

                        emergencyNumberIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        Log.e("emergencyNumberIndex", "TEST" + emergencyNumberIndex);
                        final KeyValuePairs emergencyNumber = emergencyNumbers.getList(adapter.getItem(emergencyNumberIndex));

                        String number = emergencyNumber.getDisplayName();
                        if (number == null || number.length() == 0) number = "911";

//                        Log.e("number","TEST"+number);
//                        Log.e("getEmergencyNumber","TEST"+userPreferences.getEmergencyNumberLocation());
                        txtEmergencyNumber.setText(number);
//                        ActivityCompat.invalidateOptionsMenu(getActivity());
//                        updateMenuTitles(number);

                        if (!number.equalsIgnoreCase(userPreferences.getEmergencyNumber())) {
                            String message = String.format(getActivity().getString(R.string.emergency_number_changed), number, userPreferences.getEmergencyNumberLocation());
                            final String numberEmergency = number;
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                    .setTitle("Warning")
                                    .setMessage(message)
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                            updateMenuTitles(txtEmergencyNumber.getText().toString(), emergencyNumber.getKey());

                                        }
                                    })
                                    .setPositiveButton(R.string.yes, new Dialog.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Mark this version as read.
                                            dialogInterface.dismiss();
                                            emergencyNumberIndex = userPreferences.getEmergencyIndex();
                                            txtEmergencyNumber.setText(userPreferences.getEmergencyNumberLocation());
                                            updateMenuTitles(txtEmergencyNumber.getText().toString(), userPreferences.getEmergencyLocation());
                                        }

                                    });
                            builder.setCancelable(false);

                            builder.create().show();
                        }
                        tvEnableShakeToCall911.setText(
                                String.format(
                                        getString(R.string.enable_shake_to_call_911),
                                        userPreferences.getEmergencyNumber()
                                )
                        );

//                        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
//                        LatLng currentLocation = locationManager.getCurrentLocation();
//                        List<Address> addresses;
//                        try {
//                            addresses = gcd.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
//                            if (addresses.size() > 0) {
//                                String countryName = addresses.get(0).getCountryName();
//
//                                if (!emergencyNumber.getKey().contains(countryName)) {
//                                    new AlertDialog.Builder(getActivity())
//                                            .setTitle(getString(R.string.info))
//                                            .setMessage(getString(R.string.not_your_location))
//                                            .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            }).show();
//                                }
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupShown = false;
                        dialog.dismiss();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        //Handle the back button
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            isPopupShown = false;
                        }
                        return false;
                    }
                })
                .create();

        dialog.show();
    }

    private void updateMenuTitles(String menuTitle, String emergencyLocation) {
        MenuItem bedMenuItem = OncreateOptionsmenu.findItem(R.id.call911Item);
        userPreferences.setEmergencyNumber(menuTitle);
        userPreferences.setEmergencyLocation(emergencyLocation);
        userPreferences.save();
        bedMenuItem.setTitle(String.format(getString(R.string.call_now) + " " + menuTitle));
    }

    private void setRecordTime() {
        if (isPopupShown) return;
        isPopupShown = true;
        final String[] minuteValues = {"10", "20", "30"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice, minuteValues);

        indexRecordPos = userPreferences.getRecordIndex();

        if (indexRecordPos == -1) {
            indexRecordPos = adapter.getPosition(String.valueOf(userPreferences.getrecordTime_New()));
        }
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.record_time_limit)
                .setSingleChoiceItems(
                        adapter,
                        indexRecordPos,
                        null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupShown = false;
                        dialog.dismiss();

                        indexRecordPos = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        currentMinuteIndex = Integer.valueOf(adapter.getItem(indexRecordPos));
                        if (currentMinuteIndex == 0) {
                            currentMinuteIndex = 10;
                        }

                        currentSecondIndex = 0;
                        txtRecordTime.setText(currentMinuteIndex + ":" + "00");
                        //added for settings sceen
                        userPreferences.setRecordTime_New(currentMinuteIndex);
                        userPreferences.setRecordIndex(indexRecordPos);
                        int time = currentMinuteIndex * 60;
//                        int time = currentMinuteIndex;
                        // use for recording timing
                        userPreferences.setRecordTime(time);
                        userPreferences.save();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isPopupShown = false;
                        dialog.dismiss();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        //Handle the back button
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            isPopupShown = false;
                        }
                        return false;
                    }
                })
                .create();

        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE: {
                dialog.dismiss();
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.swEnableTorch: {
                userPreferences.setEnableTorch(isChecked);
                userPreferences.save();
                break;
            }
            case R.id.swEnableShakeToCall911: {
                userPreferences.setEnableShake(isChecked);
                userPreferences.save();
                break;
            }
            case R.id.swEnableTapToCall911: {
                userPreferences.setEnableTap(isChecked);
                userPreferences.save();
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtRecordTime: {
//                handleOnChooseRecordTime();
                setRecordTime();
                break;
            }
            case R.id.txtEmergencyNumber: {
                int currentAPIVersion = Build.VERSION.SDK_INT;
                if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                    if (!doesUserHaveGPSPermission()) {
                        checkPermissionToOpengps();
                    } else {
                        handleOnChooseEmergencyNumber();
                    }
                } else {
                    handleOnChooseEmergencyNumber();
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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

    private void checkPermissionToOpengps() {
        getPermissionRequestor().request(Manifest.permission.ACCESS_FINE_LOCATION
                , 10
                , "You need to allow access to contacts"
                , new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        LatLng location = locationManager.getCurrentLocation();
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        getCurrentLocationViaJSON(location.latitude, location.longitude);

                    }

                    @Override
                    public void onPermissionDenied() {
//                        _getTelemetry().warn(TAG, "Permission denied by the user to open gallery");
                    }
                });

    }

    public PermissionRequestor getPermissionRequestor() {
        return _requestor;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        _requestor._onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String getCurrentLocationViaJSON(double lat, double lng) {
        JSONObject jsonObj = getLocationInfo(lat, lng);
        Log.i("JSON string =>", jsonObj.toString());
        String currentLocation = "testing";

        try {
            String status = jsonObj.getString("status").toString();
            Log.e("status", status);
            String countryName;
            if (status.equalsIgnoreCase("OK")) {
                JSONArray results = jsonObj.getJSONArray("results");
                int i = 0;
                Log.e("i", i + "," + results.length()); //TODO delete this

                do {
                    JSONObject json = results.getJSONObject(i);
                    JSONArray address_components = json.getJSONArray("address_components");

                    for (int k = 0; k < address_components.length(); k++) {
                        JSONObject jsonObjAddress = address_components.getJSONObject(k);
                        JSONArray typesArray = jsonObjAddress.getJSONArray("types");
                        String types = typesArray.getString(0);
                        Log.e("types...", "TEST" + types);

                        if (types.equalsIgnoreCase("country")) {
                            countryName = jsonObjAddress.optString("long_name");
                            Log.e("countryName...", "TEST" + countryName);
                            ConfigurationFactory configurationFactory = new ConfigurationFactory(getActivity());
                            final KeyValuePairs emergencyNumbers = configurationFactory.getEmergencyNumbers();
                            final UserPreferences userPreferences = UserPreferences.sharedInstance();
                            final KeyValuePairs pair = emergencyNumbers.getList(countryName);
                            userPreferences.setEmergencyNumberLocation(pair.getDisplayName());
                            userPreferences.setEmergencyNumber(pair.getDisplayName());
                            userPreferences.setEmergencyLocation(pair.getKey());
                            userPreferences.save();
                            handleOnChooseEmergencyNumber();
                            break;
                        }
                    }
                    i++;
                } while (i < results.length());
                Log.e("JSON Geo Locatoin =>", currentLocation);
                return currentLocation;
            }
        } catch (JSONException e) {
            Log.e("testing", "Failed to load JSON");
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getLocationInfo(double lat, double lng) {

        String urlString = String.format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=true", lat, lng);
        Log.e("urlString.....", "TEST" + urlString.toString());
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

    private boolean doesUserHaveGPSPermission() {
        int result = getActivity().checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
