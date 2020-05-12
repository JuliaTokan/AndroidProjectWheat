package com.example.wheatinfo.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.wheatinfo.R;
import com.example.wheatinfo.db.WheatListActivity;
import com.example.wheatinfo.contacts.ContactActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements View.OnClickListener{

    private int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView latTextView, lonTextView, addrTextView, distTextView, findAddrTextView;
    private EditText latEditText, lonEditText;

    private Button getAddrBtn;
    private Button agronomsBtn;
    private Button wheatBtn;
    private Button mapBtn;

    private double currentLon, currentLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getAddrBtn = (Button) findViewById(R.id.getAddrButton);
        getAddrBtn.setOnClickListener(this);
        wheatBtn = (Button) findViewById(R.id.btnWheat);
        wheatBtn.setOnClickListener(this);
        agronomsBtn = (Button) findViewById(R.id.btnAgronoms);
        agronomsBtn.setOnClickListener(this);
        mapBtn = (Button) findViewById(R.id.btnMap);
        mapBtn.setOnClickListener(this);

        latEditText = (EditText) findViewById(R.id.latEditText);
        lonEditText = (EditText) findViewById(R.id.lonEditText);

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        addrTextView = findViewById(R.id.addrTextView);
        distTextView = findViewById(R.id.distTextView);
        findAddrTextView = findViewById(R.id.addrFindTextView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
                strAdd = "No Address returned!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    currentLat = location.getLatitude();
                                    currentLon = location.getLongitude();
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");
                                    String adrress = getCompleteAddressString(location.getLatitude(), location.getLongitude());
                                    addrTextView.setText(adrress);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    private void findAddress(){
        if(TextUtils.isEmpty(lonEditText.getText().toString())) {
            lonEditText.setError("Empty fiend!");
            return;
        }
        if(TextUtils.isEmpty(latEditText.getText().toString())) {
            latEditText.setError("Empty fiend!");
            return;
        }
        Double lon = Double.parseDouble(lonEditText.getText().toString());
        Double lat = Double.parseDouble(latEditText.getText().toString());

        String adrress = getCompleteAddressString(lat, lon);
        findAddrTextView.setText(adrress);

        float distance = getDistance(lat, lon, currentLat, currentLon);
        distTextView.setText(distance+" meters");
    }

    private float getDistance(double lat1, double lon1, double lat2, double lon2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters;
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
        builder.setTitle("Help map info")
                .setMessage("On this page you can see your current coordinates and address.\n" +
                        "If you want to find some address and distance enter latitude and longitude and then click on GET ADDRESS.\n" +
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getAddrButton:
                findAddress();
                break;
            case R.id.btnMap:
                Intent map_mem = new Intent(this, GoogleMapActivity.class);
                map_mem.putExtra("curLat", currentLat);
                map_mem.putExtra("curLon", currentLon);
                startActivity(map_mem);
                break;
            case R.id.btnWheat:
                Intent wheat_mem = new Intent(this, WheatListActivity.class);
                startActivity(wheat_mem);
                break;
            case R.id.btnAgronoms:
                Intent agro_mem = new Intent(this, ContactActivity.class);
                startActivity(agro_mem);
                break;
        }
    }
}