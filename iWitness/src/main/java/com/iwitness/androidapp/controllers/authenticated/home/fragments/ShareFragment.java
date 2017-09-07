package com.iwitness.androidapp.controllers.authenticated.home.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.iwitness.androidapp.Configuration;
import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.RecipientAdapter;
import com.iwitness.androidapp.libraries.utils.RequestUtils;
import com.iwitness.androidapp.model.Recipient;
import com.iwitness.androidapp.model.UserPreferences;
import com.iwitness.androidapp.network.ForegroundTask;
import com.iwitness.androidapp.network.TaskDelegate;
import com.perpcast.lib.FwiFoundation;
import com.perpcast.lib.foundation.FwiJson;
import com.perpcast.lib.services.request.FwiDataParam;
import com.perpcast.lib.services.request.FwiRequest;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.UUID;

public class ShareFragment extends Fragment implements View.OnClickListener {


    // View's controls
    private ListView recipientListView;

    // Global variables
    private RecipientAdapter adapter;
    Button actionShare;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        recipientListView = (ListView) getView().findViewById(R.id.recipientlistView);
        actionShare = (Button) getView().findViewById(R.id.actionShare);

        actionShare.setOnClickListener(this);
        ArrayList<Recipient> recipients = new ArrayList<Recipient>();
        recipients.add(new Recipient());

        adapter = new RecipientAdapter(getActivity(), R.layout.cell_recipient, recipients);
        recipientListView.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.share_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAdd: {
                adapter.add(new Recipient());
                adapter.setNotifyOnChange(true);
                adapter.notifyDataSetChanged();
                break;
            }
            case R.id.actionShare: {
                EditText currentFocus = adapter.getCurrentFocus();
                if (currentFocus != null) currentFocus.clearFocus();

                onShareApp();
                return true;
            }
        }

        return false;
    }

    public void onShareApp() {
        String EMAIL_PATTERN = getString(R.string.MultipleEmailPattern);

        ArrayList<Recipient> validRecipients = new ArrayList<Recipient>(adapter.getCount());
        ArrayList<Recipient> emptyEmails = new ArrayList<Recipient>(adapter.getCount());
        ArrayList<Recipient> emptyName = new ArrayList<Recipient>(adapter.getCount());
        StringBuilder invalidEmails = new StringBuilder(100);

        for (int i = 0; i < adapter.getCount(); i++) {

            Recipient recipient = adapter.getItem(i);
            String name = recipient.getRecipient();
            String email = recipient.getEmail();

            if (name != null && name.length() == 0 && email != null && email.length() == 0) {
                emptyEmails.add(recipient);
                emptyName.add(recipient);
            } else if (name != null && name.length() > 0 && email != null && email.length() == 0) {
                emptyEmails.add(recipient);
            } else if (name != null && name.length() == 0 && email != null && email.length() > 0) {
                emptyName.add(recipient);
            }

            //&& email.isValidEmail
            if (name != null && name.length() > 0 && email != null && email.length() > 0) {

                if (!(email.matches(EMAIL_PATTERN))) {
                    invalidEmails.append(email);
                    invalidEmails.append(", ");
                } else {
                    validRecipients.add(recipient);
                }

            }

        }

        // Condition validation: At least 1 valid recipients /
        if (validRecipients.size() < 1 && invalidEmails.length() == 0) {
            if (emptyName.size() > 0 && emptyEmails.size() > 0) {
                showAlert("iWitness", "Please enter the recipient's name and email address before sending.");
                return;
            } else if (emptyName.size() > 0) {
                showAlert("iWitness", "Please enter the recipient's name before sending.");
                return;
            } else if (emptyEmails.size() > 0) {
                showAlert("iWitness", "Please enter the recipient's email address before sending.");
                return;
            }
        }

        /* Condition validation: All input recipients' emails must be valid */
        if (invalidEmails.length() > 0) {
            invalidEmails.replace(invalidEmails.length() - 2, invalidEmails.length(), "");

            String[] tokens = invalidEmails.toString().split(", ");

            if (tokens.length < 2) {
                showAlert("iWitness", String.format("The email address \"%s\" is not valid.", invalidEmails));
            } else {
                showAlert("iWitness", String.format("The emails addresses \"%s\" are not valid.", invalidEmails));
            }
            return;
        }


        FwiRequest request = RequestUtils.generateHttpRequest(FwiFoundation.FwiHttpMethod.kPost,
                Configuration.kService_Invitation,
                Configuration.kHostname
        );

        FwiJson recipients = FwiJson.Array();
        for (int i = 0; i < validRecipients.size(); i++) {
            Recipient recipient = validRecipients.get(i);
            recipients.addJsons(
                    FwiJson.Object(
                            "name", FwiJson.String(recipient.getRecipient()),
                            "email", FwiJson.String(recipient.getEmail())
                    )
            );
        }

        UserPreferences userPreferences = UserPreferences.sharedInstance();
        try{
            FwiJson profile = userPreferences.userProfile();

            FwiJson requestInfo = FwiJson.Object(
                    "firstName", profile.jsonWithPath("firstName"),
                    "lastName", profile.jsonWithPath("lastName"),
                    "email", profile.jsonWithPath("email"),
                    "subject", FwiJson.String(getString(R.string.check_out_iwitness)),
                    "message", FwiJson.String(getString(R.string.InvitationMessage)),
                    "friends", recipients
            );

        request.setDataParam(FwiDataParam.paramWithJson(requestInfo));
        }
        catch (Exception e){

        }
        ForegroundTask task = new ForegroundTask(getActivity(), request);
        task.run(new TaskDelegate() {
            @Override
            public void taskDidFinish(UUID taskId, int statusCode, Object response) {
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                    showAlert("iWitness", getString(R.string.send_invitaion_success));
                    // Toast.makeText(getActivity(), getString(R.string.send_invitaion_success), Toast.LENGTH_LONG).show();
                } else if (statusCode == -1) {
                    showAlert("Warning", "Could not connect to server at the moment");
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionShare: {
                EditText currentFocus = adapter.getCurrentFocus();
                if (currentFocus != null) currentFocus.clearFocus();
                onShareApp();

            }
        }
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
