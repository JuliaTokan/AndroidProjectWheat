package com.example.wheatinfo.contacts;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wheatinfo.R;
import com.example.wheatinfo.db.WheatListActivity;
import com.example.wheatinfo.map.MapActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_READ_CONTACTS = 49;
    ListView list;
    ArrayList<PhoneContact> mobileArray;

    private Button devBtn;
    private Button infoBtn;

    private Button wheatBtn;
    private Button mapBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        devBtn = (Button) findViewById(R.id.btn_dev);
        devBtn.setOnClickListener(this);

        infoBtn = (Button) findViewById(R.id.btn_info);
        infoBtn.setOnClickListener(this);

        mapBtn = (Button) findViewById(R.id.btnMap);
        mapBtn.setOnClickListener(this);

        wheatBtn = (Button) findViewById(R.id.btnWheat);
        wheatBtn.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllContacts();
        } else {
            requestPermission();
        }
        list = findViewById(R.id.list);
        PhoneAdapter phoneAdapter = new PhoneAdapter(this, mobileArray);
        list.setAdapter(phoneAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.help) {

            buttonHelpOnClick();

        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private ArrayList<PhoneContact> getAllContacts() {
        Set<PhoneContact> phoneContacts = new LinkedHashSet<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        PhoneContact phoneContact = new PhoneContact(name, phoneNo);
                        phoneContacts.add(phoneContact);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return new ArrayList<>(phoneContacts);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dev:
                buttonDeveloperOnClick();
                break;
            case R.id.btn_info:
                buttonInfo();
                break;
            case R.id.btnWheat:
                Intent wheat_mem = new Intent(this, WheatListActivity.class);
                startActivity(wheat_mem);
                break;
            case R.id.btnMap:
                Intent map_mem = new Intent(this, MapActivity.class);
                startActivity(map_mem);
                break;
        }
    }

    private void buttonDeveloperOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewImg = factory.inflate(R.layout.developer_info, null);

        builder.setTitle("About developer")
                .setView(viewImg)
                .setMessage("Name: Yulia Tokan \nGroup: TTP-3 \nCourse: 3")
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void buttonHelpOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        builder.setTitle("Help contact agronomes")
                .setMessage("On this page you can see all information about agronomes.\n" +
                        "If you want to find numbers start from +38066 click NUMBER START FROM +38066.\n" +
                        "Use green buttons for navigation.")
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void buttonInfo() {
        ArrayList<PhoneContact> infoList = getNumberContacts();

        String result = "";
        for(PhoneContact phoneContact: infoList){
            result+=phoneContact.getName();
            result+="\n     "+phoneContact.getPhone();
            result+="\n\n";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contacts with phone starts +38066")
                .setMessage(result.equals("")?"No contact found":result)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private ArrayList<PhoneContact> getNumberContacts() {
        Set<PhoneContact> phoneContacts = new LinkedHashSet<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id+ " AND "+
                            ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '+38066%'",
                            null, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        PhoneContact phoneContact = new PhoneContact(name, phoneNo);
                        phoneContacts.add(phoneContact);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        return new ArrayList<>(phoneContacts);
    }

}
