package com.headerinteractive.notificationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.headerinteractive.notificationapp.layout.ResourcesHelper;

import com.ibm.mce.sdk.api.MceSdk;
import com.ibm.mce.sdk.api.OperationCallback;
import com.ibm.mce.sdk.api.OperationResult;
import com.ibm.mce.sdk.api.attribute.Attribute;
import com.ibm.mce.sdk.api.attribute.AttributesOperation;
import com.ibm.mce.sdk.api.attribute.StringAttribute;
import com.ibm.mce.sdk.api.registration.RegistrationDetails;
import com.ibm.mce.sdk.plugin.inapp.InAppManager;
import com.ibm.mce.sdk.plugin.inbox.InboxMessagesClient;
import com.ibm.mce.sdk.plugin.inbox.RichContent;
import com.ibm.mce.sdk.plugin.inbox.RichContentDatabaseHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends ListSampleActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        private final String TAG = "Main";
    @Override
    protected List<String> getListItems() {
        return null;
    }

    @Override
    protected Object[] createUIValues(String[] itemsArray) {return null;}

    @Override
    protected String getMenuName() {
        return null;
    }

    @Override
    protected String getSettingsName() {
        return "action_settings";
    }

    @Override
    protected String getLayoutName() {
        return "activity_main";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                StringAttribute attribute = new StringAttribute("First Name", "Eu me Chamo Nicolas");
                List<Attribute> attributes = new ArrayList<>(1);
                attributes.add(attribute);
                Log.e("Attrs","Atributos : "+attributes);
                List<String> attributeKeys = new ArrayList<String>(1);
                attributeKeys.add(attribute.getKey());
                String action = "Update";
                OperationCallback<AttributesOperation> callback = new OperationCallback<AttributesOperation>() {
                    @Override
                    public void onSuccess(AttributesOperation attributesOperation, OperationResult result) {
                        Log.d(TAG, "Attributes operation was successful: "+attributesOperation.getType());
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, "Atributo enviado com sucesso!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
//                                Toast.makeText(MainActivity.this, "Atributo enviado com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(AttributesOperation attributesOperation, OperationResult result) {
                        Log.d(TAG, "Attributes operation failed: "+attributesOperation.getType()+". "+result.getMessage());
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Falha ao enviar o atributo!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };

                performAttributesAction(attributes, attributeKeys, action, callback);

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(GravityCompat.START);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void performAttributesAction(List<Attribute> attributes, List<String> attributeKeys, String action, OperationCallback<AttributesOperation> callback) {
        if("Update".equals(action)) {
            try
            {
                MceSdk.getQueuedAttributesClient().updateUserAttributes(getApplicationContext(), attributes);
                Snackbar.make(findViewById(R.id.drawer_layout), "Atributo enviado com sucesso!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            catch (JSONException jsone)
            {
                Snackbar.make(findViewById(R.id.drawer_layout), "Ocorreu um erro! "+jsone, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else if("Delete".equals(action)) {
            try
            {
                MceSdk.getQueuedAttributesClient().deleteUserAttributes(getApplicationContext(), attributeKeys);
                Snackbar.make(findViewById(R.id.drawer_layout), "Atributo deletado com sucesso!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            catch (JSONException jsone)
            {
                Snackbar.make(findViewById(R.id.drawer_layout), "Ocorreu um erro! "+jsone, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
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

        }
        else if (id == R.id.nav_inbox)
        {
//            List expiredMessages = new LinkedList();
//            RichContentDatabaseHelper.MessageCursor cursor = RichContentDatabaseHelper.getRichContentDatabaseHelper(getApplicationContext()).getMessages();
//            Date now = new Date(System.currentTimeMillis());
//            while (cursor.moveToNext()) {
//                RichContent rc = cursor.getRichContent();
//                if(rc.getExpirationDate().before(now)) {
//                    expiredMessages.add(rc);
//                }
//            }
//            for(RichContent expiredMessage : expiredMessages) {
//                InboxMessagesClient.deleteMessage(getApplicationContext(), expiredMessage);
//            }
            InboxMessagesClient.showInbox(getApplicationContext());
        }
        else if (id == R.id.nav_inapp)
        {

            showInAppIfExists();
//            InAppManager.show(getApplicationContext(), getSupportFragmentManager(), resourcesHelper.getId("con"));
            inAppShown = true;
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
