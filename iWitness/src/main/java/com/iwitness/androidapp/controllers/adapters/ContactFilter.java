package com.iwitness.androidapp.controllers.adapters;

import android.widget.*;
import com.iwitness.androidapp.model.*;

import java.util.*;

// Contacts filter
public class ContactFilter extends Filter {

    private ContactAdapter _adapter;
    public ContactFilter(ContactAdapter adapter) {
        super();

        _adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        constraint = constraint.toString().toLowerCase();
        FilterResults result = new FilterResults();
        if (constraint.toString().length() > 0) {
            ArrayList<Contact> filteredItems = new ArrayList<Contact>();

            for (Contact contact : _adapter.getOriginalList()) {
                if (contact.toString().toLowerCase().contains(constraint))
                    filteredItems.add(contact);
            }
            result.count = filteredItems.size();
            result.values = filteredItems;
        } else {
            synchronized (this) {
                result.values =  _adapter.getOriginalList();
                result.count =  _adapter.getOriginalList().size();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        _adapter.setContactList((ArrayList<Contact>) results.values);
        _adapter.notifyDataSetChanged();
        _adapter.clear();
        for (Contact aContactList : _adapter.getContactList()) {
            _adapter.add(aContactList);
        }
        _adapter.notifyDataSetInvalidated();
    }
}
