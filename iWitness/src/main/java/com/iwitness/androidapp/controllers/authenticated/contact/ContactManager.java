package com.iwitness.androidapp.controllers.authenticated.contact;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.controllers.adapters.ContactAdapter;
import com.iwitness.androidapp.model.Contact;
import com.iwitness.androidapp.network.TaskDelegate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public final class ContactManager extends AppCompatActivity implements TaskDelegate, AdapterView.OnItemClickListener, TextWatcher {

    private ArrayList<Contact> contacts = null;
    private LinkedHashMap<String, Contact> allContacts = new LinkedHashMap<String, Contact>();
    private ContactAdapter contactAdapter = null;
    private ListView lv;
    private RelativeLayout progressLayout;
    private EditText myFilter;
    private AsyncLoadContacts task;
    private final int REQUEST_ADD_CONTACT = 1;
    // Indexing fo the list
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    TextView toolBarTitle;
    EditText searchText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarTitle  =((TextView)toolbar.findViewById(R.id.toolbar_title));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        toolBarTitle.setVisibility(View.VISIBLE);
        toolBarTitle.setText("Contacts");
        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
        searchText = (EditText) findViewById(R.id.search_txt);

        progressLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayout.setVisibility(View.GONE); // by default progress view to GONE

        lv = (ListView) findViewById(R.id.contactList);
        lv.setOnItemClickListener(this);

        myFilter = (EditText) findViewById(R.id.search_txt);
        myFilter.addTextChangedListener(this);

//        if(isReadStorageAllowed()){
            if (contacts == null || contacts.size() == 0) {
                contacts = new ArrayList<Contact>();
                task = new AsyncLoadContacts();
                task.execute();
            } else {
                contactAdapter = new ContactAdapter(this, R.id.contactList, contacts);
                lv.setAdapter(contactAdapter);
                contactAdapter.getFilter().filter(myFilter.getText().toString());
            }
//        }
//        requestContactsPermission();


        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // do something, e.g. set your TextView here via .setText()
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addressbook_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return false;
            }
            case R.id.cancel_action: {
                finish();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
//    private boolean isReadStorageAllowed() {
//        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
//        if (result == PackageManager.PERMISSION_GRANTED)
//            return true;
//        //If permission is not granted returning false
//        return false;
//    }
//
//    //Requesting permission
//    private void requestContactsPermission(){
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)){
//        }
//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSIONS_REQUEST_READ_CONTACTS);
//    }

    //This method will be called when the user will tap on allow or deny
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //Checking the request code of our request
//        if(requestCode == PERMISSIONS_REQUEST_READ_CONTACTS){
//            Log.e("result..","TEST"+requestCode);
//            //If permission is granted
//            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
//            }
//        }
//    }
    // Also on back pressed set the selected list, if nothing selected set Intent result to canceled
    @Override
    public void onBackPressed() {

        ArrayList<Contact> selectedList = new ArrayList<Contact>();

        Intent intent = new Intent();

        ArrayList<Contact> contactList = contactAdapter.getOriginalList();
        for (Contact contact : contactList) {
            if (contact.isSelected()) {
                selectedList.add(contact);
            }
            if (selectedList.size() > 0) {
                intent.putParcelableArrayListExtra("SELECTED_CONTACTS", selectedList);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
        }

        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
    }

    @Override
    protected void onDestroy() {
        if (task != null) {
            task.cancel(true);
        }
        super.onDestroy();
    }

    @SuppressLint("InlinedApi")
    private void getContacts() {

        ContentResolver cr = getContentResolver();

        Cursor cur = cr.query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Contacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME
                },
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME);

        Contact contact;
        int givenNameIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
        int middleNameIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
        int familyNameIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);

        Log.e("cur.getCount()","TEST"+cur.getCount());
        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {

                Log.e("while","TEST"+"while");
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Data.CONTACT_ID));

                String mimeType = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE));

                if (allContacts.containsKey(id)) {
                    contact = allContacts.get(id);
                } else {
                    contact = new Contact();
                    allContacts.put(id, contact);
                    contact.setContactPhotoUri(getContactPhotoUri(Long.parseLong(id)));
                }

                if (mimeType.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                    String firstName = cur.getString(givenNameIndex);
                    String middleName = cur.getString(middleNameIndex);
                    String lastName = cur.getString(familyNameIndex);
                    contact.setContactLastName(
                            String.format(
                                    "%s%s",
                                    middleName != null ? String.format("%s ", middleName) : "",
                                    lastName != null ? lastName : ""
                            )
                    );
                    contact.setContactFirstName(firstName);
                }

                //set contact and email address in priority
//                home, work, iCloud, others,  custom----display of email series.
//                mobile, home,  work, custom, //iPhone,  main---display of phone number

//                if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
//                    contact.setContactNumber(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                }
//
//                if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
//                    contact.setContactEmail(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
//                }


                String mobilePhone = null,homePhone = null,workPhone =null,mainPhone=null,
                workFaxPhone =null,homefaxHome =null,pagerPhone=null,otherPhone=null,customPhone=null;

                // Getting Phone numbers
                if(mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    switch (cur.getInt(cur.getColumnIndex("data2"))){
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                            homePhone = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                            mobilePhone = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                            workPhone = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN :
                            mainPhone = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK :
                            workFaxPhone = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME :
                            homefaxHome = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                            pagerPhone = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                            otherPhone= cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                            customPhone= cur.getString(cur.getColumnIndex("data1"));
                            break;

                    }
                }

//                try{
                    if(contact!=null && contact.getNumber()== null){
                        if(mobilePhone!=null && !(mobilePhone.equalsIgnoreCase(""))){
                            contact.setSelectedNumber(mobilePhone);
                        }
                        else if(homePhone!=null && !(homePhone.equalsIgnoreCase(""))){
                            Log.e("else if","TEST"+homePhone);
                            contact.setSelectedNumber(homePhone);
                        }
                        else if(workPhone!=null && !(workPhone.equalsIgnoreCase(""))){
                            Log.e("else if workPhone","TEST"+workPhone);
                            contact.setSelectedNumber(workPhone);
                        }
                        else if(mainPhone!=null && !(mainPhone.equalsIgnoreCase("")))
                            contact.setSelectedNumber(mainPhone);

                        else if(workFaxPhone!=null && !(workFaxPhone.equalsIgnoreCase("")))
                            contact.setSelectedNumber(workFaxPhone);

                        else if(homefaxHome!=null && !(homefaxHome.equalsIgnoreCase("")))
                            contact.setSelectedNumber(homefaxHome);

                        else if(pagerPhone!=null && !(pagerPhone.equalsIgnoreCase("")))
                            contact.setSelectedNumber(pagerPhone);

                        else if(otherPhone!=null && !(otherPhone.equalsIgnoreCase("")))
                            contact.setSelectedNumber(otherPhone);

                        else if(customPhone!=null && !(customPhone.equalsIgnoreCase("")))
                            contact.setSelectedNumber(customPhone);
                    }

//                }
//                catch (Exception e){
//
//                }

                String homeEmail=null,workEmail=null,customEmail=null,otherEmail=null;
                // Getting EMails
                if(mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    switch (cur.getInt(cur.getColumnIndex("data2"))){
                        case ContactsContract.CommonDataKinds.Email.TYPE_HOME :
                            homeEmail = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Email.TYPE_WORK :
                            workEmail = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM :
                            customEmail = cur.getString(cur.getColumnIndex("data1"));
                            break;
                        case ContactsContract.CommonDataKinds.Email.TYPE_OTHER :
                            otherEmail = cur.getString(cur.getColumnIndex("data1"));
                            break;
                    }
                }


                    try{
                        if(contact!=null && contact.getContactEmail()==null)
                        {
                            if(homeEmail!=null && !(homeEmail.equalsIgnoreCase("")))
                                contact.setContactEmail(homeEmail);

                            else if(workEmail!=null && !(workEmail.equalsIgnoreCase("")))
                                contact.setContactEmail(workEmail);

                            else if(customEmail!=null && !(customEmail.equalsIgnoreCase("")))
                                contact.setContactEmail(customEmail);

                            else if(otherEmail!=null && !(otherEmail.equalsIgnoreCase("")))
                                contact.setContactEmail(otherEmail);
                        }
                    }
                    catch (Exception e)
                    {

                    }





            }
        }

        cur.close();

        refreshContactList();
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

    public void refreshContactList() {
        contacts.clear();
        contacts.addAll(allContacts.values());

        for (Contact _contact : contacts) {

            if (_contact.getContactName() == null && _contact.getContactNumber() == null
                    && _contact.getContactEmail() == null) {
                contacts.remove(_contact);
                break;
            }

        }

        contactAdapter = new ContactAdapter(this, R.id.contactList, contacts);
        contactAdapter.notifyDataSetChanged();
    }

    // Get contact photo URI for contactId
    public Uri getContactPhotoUri(long contactId) {
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        photoUri = Uri.withAppendedPath(photoUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cur = null;
        try{
                 cur = getContentResolver()
                        .query(
                                ContactsContract.Data.CONTENT_URI,
                                null,
                                String.format("%s=%d AND %s='%s'",
                                        ContactsContract.Data.CONTACT_ID,
                                        contactId,
                                        ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE),
                                null,
                                null);

                if (cur != null) {
                    if (!cur.moveToFirst()) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            catch (Exception e){

            }
        finally {
            cur.close();
            }
        return photoUri;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Contact item = (Contact) parent.getItemAtPosition(position);

        Log.e("item.getNumber()","TEST"+item.getNumber());
        Log.e("item.getContactNumber()","TEST"+item.getContactNumber());


        Intent intent = new Intent(this, ContactDetailController.class);
        intent.putExtra("title","No");
        intent.putExtra("firstName", item.getContactFirstName());
        intent.putExtra("lastName", item.getContactLastName());
        intent.putExtra("email", item.getContactEmail());
        intent.putExtra("phone", item.getNumber());
//        intent.putExtra("phone", item.getContactNumber());
        startActivityForResult(intent, REQUEST_ADD_CONTACT);
        overridePendingTransition(R.anim.enter_slide_to_left, R.anim.exit_slide_to_left);
    }

    @Override
    public void taskDidFinish(UUID taskId, int statusCode, Object response) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (contactAdapter != null) {
            contactAdapter.getFilter().filter(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class AsyncLoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            getContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lv.setAdapter(contactAdapter);
            contactAdapter.getFilter().filter(myFilter.getText().toString());
            progressLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_CONTACT) {
                setResult(RESULT_OK, data);
                finish();
                overridePendingTransition(R.anim.enter_slide_to_right, R.anim.exit_slide_to_right);
            }
        }
    }
}
