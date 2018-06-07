package com.headerinteractive.notificationapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibm.mce.sdk.api.MceSdk;
import com.ibm.mce.sdk.api.registration.RegistrationDetails;
import com.ibm.mce.sdk.location.LocationApi;
import com.ibm.mce.sdk.location.LocationManager;
import com.ibm.mce.sdk.location.LocationPreferences;

import java.util.List;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_LOCATION = 0;

    private GoogleMap mMap;
    private Button enableLocations;
    private Button showGeofences;
    private boolean mapEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        enableLocations = (Button) findViewById(R.id.enableLocations);
        showGeofences = (Button) findViewById(R.id.showGeofences);
        boolean locationsEnabled = LocationPreferences.isEnableLocations(getApplicationContext());
        alert("As geofences estão: " + (locationsEnabled ? "Habilitadas" : "Desabilitadas"), findViewById(R.id.drawer_layout));
        enableLocations.setText(locationsEnabled ? getResources().getString(R.string.disable_locations_text) : getResources().getString(R.string.enable_locations_text));
        showGeofences.setEnabled(false);

        enableLocations.setOnClickListener(this);
        showGeofences.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e("Location", "Failed to start map", e);
        }
    }

    @Override
    public void onClick(View view) {
        if (enableLocations == view) {
            boolean locationsEnabled = LocationPreferences.isEnableLocations(getApplicationContext());
            if (locationsEnabled) {
                LocationManager.disableLocationSupport(getApplicationContext());
                enableLocations.setText(getResources().getString(R.string.enable_locations_text));
                showGeofences.setEnabled(false);
                this.alert("Geofences desabilidatas", view);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_LOCATION);
                } else {
                    LocationManager.enableLocationSupport(getApplicationContext());
                    enableLocations.setText(getResources().getString(R.string.disable_locations_text));
                    showGeofences.setEnabled(mapEnabled);
                    this.alert("Geofences habilidatas", view);
                }

            }
        } else if (showGeofences == view) {
            mMap.clear();
            List<LocationApi> locations = LocationManager.getAllLocations(getApplicationContext());
            this.alert("Iniciando as geofences. " + locations.size() + " geofences encontradas.", view);
            for (LocationApi location : locations) {
                LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addCircle(new CircleOptions()
                        .center(center)
                        .radius(location.getRadius())
                        .strokeColor(Color.RED)
                        .strokeWidth(1)
                        .fillColor(0x5500ff00));
                mMap.addMarker(new MarkerOptions().position(center).title(location.getId()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
//        mMap.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getApplicationContext(),"Você não possui permissões para mostrar sua localização atual",Toast.LENGTH_SHORT).show();
        }
        else
        {
            mMap.setMyLocationEnabled(true);
            onClick(showGeofences);
        }
        LatLng center = new LatLng(-25.443804,-49.288262);
        CameraPosition cwb = new CameraPosition(center,11,1,1);



        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cwb));
        mapEnabled = true;
        boolean locationsEnabled = LocationPreferences.isEnableLocations(getApplicationContext());
        showGeofences.setEnabled(locationsEnabled);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e("LM - RequestCode", ""    +requestCode);
        Log.e("LM - Permissions", permissions.toString());
        Log.e("LM - Grants", grantResults.toString());
        if(requestCode == REQUEST_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager.enableLocationSupport(getApplicationContext());
                showGeofences.setEnabled(true);
            }
        }
        alert("As geofences estão "+(showGeofences.isEnabled() ? "habilitadas" : "desabilitadas"),findViewById(R.id.drawer_layout));
    }
    private void alert(final String text,View view)
    {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_inbox)
        {
            // Handle the camera action
        }
        else if (id == R.id.nav_inapp)
        {

        }
        else if (id == R.id.nav_geofences)
        {
            RegistrationDetails registrationDetails = MceSdk.getRegistrationClient().getRegistrationDetails(getApplicationContext());
            if (registrationDetails.getChannelId() == null || registrationDetails.getChannelId().length() == 0) {

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "O SDK Não Foi Registrado.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
//                Toast.makeText(MainActivity.this, "SDK não registrado! ", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LocationActivity.class);
                startActivity(intent);
            }
        }
        else if(id == R.id.nav_beacons)
        {

        }
        else if (id == R.id.nav_registration_details)
        {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), RegistrationDetailsSampleActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_share)
        {

        }
        else if (id == R.id.nav_send)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
