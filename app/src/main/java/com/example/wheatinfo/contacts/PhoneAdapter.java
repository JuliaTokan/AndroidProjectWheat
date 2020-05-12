package com.example.wheatinfo.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class PhoneAdapter extends ArrayAdapter<PhoneContact> {

    public PhoneAdapter(Context context, ArrayList<PhoneContact> phoneContacts) {
        super(context, android.R.layout.simple_list_item_2, phoneContacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhoneContact phoneContact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText(phoneContact.getName());
        ((TextView) convertView.findViewById(android.R.id.text2))
                .setText(phoneContact.getPhone());
        return convertView;
    }
}
