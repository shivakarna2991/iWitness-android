package com.iwitness.androidapp.controllers.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.iwitness.androidapp.R;
import com.iwitness.androidapp.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

// Contact adapter
public class ContactAdapter extends ArrayAdapter<Contact> implements SectionIndexer {

    private ArrayList<Contact> contactList;
    private ArrayList<Contact> originalList;
    HashMap<String, Integer> alphaIndexer;
    String[] sections;

    public ArrayList<Contact> getOriginalList() {
        return originalList;
    }

    public void setContactList(ArrayList<Contact> list) {
        contactList = list;
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }
    private ContactFilter filter;

    public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
        super(context, textViewResourceId, items);

        this.contactList = new ArrayList<Contact>();
        this.originalList = new ArrayList<Contact>();

        this.contactList.addAll(items);
        this.originalList.addAll(items);

        alphaIndexer = new HashMap<String, Integer>();
        int size = contactList.size();

        for (int x = 0; x < size; x++) {
            String s = contactList.get(x).getContactName();

            String ch = s.substring(0, 1);
            ch = ch.toUpperCase();
            alphaIndexer.put(ch, x);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();

// create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

        Collections.sort(sectionList);

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ContactFilter(this);
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.contact_item, null);
        }
        final Contact contact = contactList.get(position);
        if (contact != null) {
            TextView name = (TextView) view.findViewById(R.id.name);
            ImageView thumb = (ImageView) view.findViewById(R.id.thumb);
            TextView number = (TextView) view.findViewById(R.id.number);
            TextView email = (TextView) view.findViewById(R.id.email);

            TextView numberLabel = (TextView) view.findViewById(R.id.numberLabel);
            TextView emailLabel = (TextView) view.findViewById(R.id.emailLabel);

            Uri contactUri = contact.getContactPhotoUri();
            if (contactUri != null) {
                thumb.setImageURI(contactUri);
            }

            if (thumb.getDrawable() == null) {
                thumb.setImageResource(R.drawable.def_contact);
            }

            name.setText(contact.getContactName());

            if (contact.getContactNumber() == null) {
                numberLabel.setText("");
                numberLabel.setVisibility(View.GONE);
                number.setVisibility(View.GONE);
            } else {
                numberLabel.setText(getContext().getString(R.string.short_phone_header));
                numberLabel.setVisibility(View.VISIBLE);
                number.setVisibility(View.VISIBLE);
            }

            number.setText(contact.getNumber());

            if (contact.getContactEmail() == null)
                emailLabel.setText("");
            else
                emailLabel.setText(getContext().getString(R.string.short_email_header));

            email.setText(contact.getContactEmail());

        }

        return view;
    }

    @Override
    public int getPositionForSection(int section) {
        return alphaIndexer.get(sections[section]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }
}