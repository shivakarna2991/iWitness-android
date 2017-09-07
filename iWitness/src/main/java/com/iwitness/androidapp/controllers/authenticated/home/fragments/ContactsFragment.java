package com.iwitness.androidapp.controllers.authenticated.home.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.EmergencyContactAdapter;
import com.iwitness.androidapp.controllers.authenticated.contact.ContactDetailController;
import com.iwitness.androidapp.controllers.authenticated.contact.ContactManager;
import com.iwitness.androidapp.controllers.common.FragmentPermissionRequestor;
import com.iwitness.androidapp.controllers.common.PermissionListener;
import com.iwitness.androidapp.controllers.common.PermissionRequestor;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiRequest;
import com.perpcast.lib.utils.FwiIdUtils;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.UUID;

public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener, Animation.AnimationListener, View.OnClickListener, TaskDelegate {
    final private int REQUEST_EDIT_CONTACT = 1;
    final private int REQUEST_ADD_CONTACT = 2;
    final private int REQUEST_ADD_CONTACT_FROM_ADDRESS_BOOK = 4;


    private ListView contactList;
    private LinearLayout optionMenu;
    private Button cancelButton;
    private Button addressButton;
    private Button contactButton;

    private UUID getEmergencyContactsTaskId;
    private FwiJson contacts;
    private ForegroundTask task;


    ImageView actionAddContact;
    private boolean isMenuDisplay;
    private Menu menu;
    LinearLayout ll_addcontact;
    private PermissionRequestor _requestor;
    String fromscreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _requestor = new FragmentPermissionRequestor(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        try {
            fromscreen = this.getArguments().getString("fromscreen");
            if (fromscreen.equalsIgnoreCase("Register")) {
                setHasOptionsMenu(false);
            }
        } catch (Exception e) {

        }
        contactList = (ListView) getView().findViewById(R.id.lvContacts);
        contactList.setOnItemClickListener(this);
        LinearLayout emptyView = (LinearLayout) getView().findViewById(R.id.llEmptyView);
        contactList.setEmptyView(emptyView);

        optionMenu = (LinearLayout) getView().findViewById(R.id.hidden_panel);
        optionMenu.setVisibility(View.INVISIBLE);

        ll_addcontact = (LinearLayout) getView().findViewById(R.id.ll_addcontact);
        actionAddContact = (ImageView) getView().findViewById(R.id.actionAddContact);
        cancelButton = (Button) getView().findViewById(R.id.cancelButton);
        addressButton = (Button) getView().findViewById(R.id.addressButton);
        contactButton = (Button) getView().findViewById(R.id.contactButton);

        if (contacts == null) {
            getContacts();
        }
        ll_addcontact.setOnClickListener(this);
        super.onActivityCreated(savedInstanceState);
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home, menu);
        this.menu = menu;
        MenuItem item = menu.getItem(0);
        UserPreferences userPreferences = UserPreferences.sharedInstance();
        item.setTitle(String.format(getString(R.string.call_now) + " " + userPreferences.getEmergencyNumber()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAddContact: {
                return false;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }*/

    private void getContacts() {
        if (AppDelegate.isNetworkAvailable(getActivity())) {
            FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kGet,
                    Configuration.kService_User_Contact,
                    Configuration.kHostname,
                    UserPreferences.sharedInstance().currentProfileId()
            );

            getEmergencyContactsTaskId = FwiIdUtils.generateUUID();
            task = new ForegroundTask(getActivity(), request, getEmergencyContactsTaskId);
            task.run(this);
        } else {
            showAlert("iWitness", "Could not load emergency contact list at the moment.");
        }
    }

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            if (taskId == getEmergencyContactsTaskId) {
                EmergencyContactAdapter adapter;
                ArrayList<FwiJson> emergencyContacts = new ArrayList<FwiJson>();
                if (response != null) {
                    contacts = ((FwiJson) response).jsonWithPath("_embedded/contact");
                    emergencyContacts = (ArrayList<FwiJson>) contacts.toArray();
                }
                adapter = new EmergencyContactAdapter(getActivity(), emergencyContacts);
                contactList.setAdapter(adapter);
            }
        } else if (statusCode == -1) {
            showAlert("Warning", "Could not load emergency contact list at the moment.");
        } else {
            try {
                contacts = FwiJson.Object();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FwiJson requestedContact = (FwiJson) parent.getItemAtPosition(position);

        Intent intent = new Intent(getActivity(), ContactDetailController.class);
        intent.putExtra("contact", requestedContact);
        intent.putExtra("index", position);
        startActivityForResult(intent, REQUEST_EDIT_CONTACT);

        getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_CONTACT || requestCode == REQUEST_ADD_CONTACT_FROM_ADDRESS_BOOK) {
                FwiJson contact = (FwiJson) data.getSerializableExtra("contact");
                if (contact != null) {
                    contacts.addJsons(contact);
                    EmergencyContactAdapter adapter = new EmergencyContactAdapter(getActivity(), contacts.toArray());
                    contactList.setAdapter(adapter);
                }

            } else if (requestCode == REQUEST_EDIT_CONTACT) {
                if (data.hasExtra("deletedIndex")) {
                    contacts.removeJsonAtIndex(data.getExtras().getInt("deletedIndex"));
                } else {
                    FwiJson contact = (FwiJson) data.getSerializableExtra("contact");
                    contacts.replaceJsonAtIndex(data.getExtras().getInt("index"), contact);
                }
                EmergencyContactAdapter adapter = new EmergencyContactAdapter(getActivity(), contacts.toArray());
                contactList.setAdapter(adapter);
            }
        }
    }


    @Override
    public void onDestroy() {
        if (task != null) {
            task.cancel(true);
        }
        super.onDestroy();
    }


    // Animation.AnimationListener's members
    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (isMenuDisplay) {
            cancelButton.setOnClickListener(this);
            addressButton.setOnClickListener(this);
            contactButton.setOnClickListener(this);
        } else {
            optionMenu.setVisibility(View.INVISIBLE);

            cancelButton.setOnClickListener(null);
            addressButton.setOnClickListener(null);
            contactButton.setOnClickListener(null);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    // View.OnClickListener's members
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton: {
                isMenuDisplay = false;
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
                animation.setAnimationListener(this);
                optionMenu.startAnimation(animation);
                break;
            }

            case R.id.addressButton: {
                int currentAPIVersion = Build.VERSION.SDK_INT;
                if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                    checkPermissionToOpenContacts();
                } else {
                    if (contacts == null || contacts.size() < 6) {
                        Intent intent = new Intent(getActivity(), ContactManager.class);
                        startActivityForResult(intent, REQUEST_ADD_CONTACT_FROM_ADDRESS_BOOK);
                        getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                    } else {
                        showAlert("iWitness", getString(R.string.emergency_contact_limit_reached));
                    }
                    // Dismiss menu
                    onClick(cancelButton);
                }
                break;
            }

            case R.id.contactButton: {
                if (contacts == null || contacts.size() < 6) {
                    Intent intent = new Intent(getActivity(), ContactDetailController.class);
                    startActivityForResult(intent, REQUEST_ADD_CONTACT);
                    getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                } else {
                    showAlert("iWitness", getString(R.string.emergency_contact_limit_reached));
                }
                // Dismiss menu
                this.onClick(cancelButton);
                break;
            }
            case R.id.ll_addcontact: {
                if (contacts == null || contacts.size() < 6) {
                    isMenuDisplay = true;
                    optionMenu.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
                    animation.setAnimationListener(this);
                    optionMenu.startAnimation(animation);
                } else {
                    showAlert("iWitness", getString(R.string.emergency_contact_limit_reached));
                }
                break;
            }
            default: {
            }
        }
    }

    private void checkPermissionToOpenContacts() {
        getPermissionRequestor().request(Manifest.permission.READ_CONTACTS
                , 10
                , "You need to allow access to contacts"
                , new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (contacts == null || contacts.size() < 6) {
                            Intent intent = new Intent(getActivity(), ContactManager.class);
                            startActivityForResult(intent, REQUEST_ADD_CONTACT_FROM_ADDRESS_BOOK);
                            getActivity().overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
                        } else {
                            showAlert("iWitness", getString(R.string.emergency_contact_limit_reached));
                        }

                        // Dismiss menu
                        onClick(cancelButton);
                    }

                    @Override
                    public void onPermissionDenied() {
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

    public void showAlert(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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

}
