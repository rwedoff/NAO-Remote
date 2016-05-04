package com.ryanwedoff.senor.naoservercontroller;
/**
 * MainActivity is the home screen of the application.  It contains basic info that is set in the settings.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static String EXTRA_IP = "com.ryanwedoff.senor.naoServerController.IP";
    private final static String EXTRA_PORT = "com.ryanwedoff.senor.naoServerController.Port";
    private String port;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Intent intent = new Intent(this, WelcomeScreen.class);
//        startActivity(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        ipAddress = preferences.getString(getString(R.string.pref_ipAddress_key), getString(R.string.pref_ipAddress_default));
        port = preferences.getString(getString(R.string.pref_port_key), getString(R.string.pref_port_default));

        EditText ipEdit = (EditText) findViewById(R.id.ipQuick);
        EditText portEdit = (EditText) findViewById(R.id.portQuick);
        assert ipEdit != null;
        ipEdit.setText(ipAddress);
        assert portEdit != null;
        portEdit.setText(port);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected) {
            Snackbar.make(drawer, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }


        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();


        TextView textView = (TextView) findViewById(R.id.wifiState);
        String wfState = "Wifi State: " + wifiInfo.getSSID();
        assert textView != null;
        textView.setText(wfState);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.robot_names);
        String namesObj = sharedPref.getString(getString(R.string.robot_names), defaultValue);
        Gson gson = new Gson();
        String [] rn =  gson.fromJson(namesObj, String[].class);
        TextView names = (TextView) findViewById(R.id.quickNames);
        try {
            ArrayList<String> robotNames = new ArrayList<>(Arrays.asList(rn));

            assert names != null;
            String message = "Robot Names: " + robotNames.toString();
            names.setText(message);
        } catch (Exception e) {
            assert names != null;
            names.setText(R.string.NoneAdded);
        }




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send) {
            Intent intent = new Intent(this, SocketSendActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_remote){
            Intent intent = new Intent(this, ControllerActivity.class);
            intent.putExtra(EXTRA_IP, ipAddress);
            intent.putExtra(EXTRA_PORT,port);
            startActivity(intent);
        }else if(id == R.id.nav_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_robot_names){
            Intent intent = new Intent(this, RobotName.class);
            startActivity(intent);
        } else if(id == R.id.nav_mood){
            Intent intent = new Intent(this, MoodActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_file){
            Intent intent = new Intent(this, FileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onInitConnect(View view) {
        EditText ipEdit = (EditText) findViewById(R.id.ipQuick);
        EditText portEdit = (EditText) findViewById(R.id.portQuick);
        assert ipEdit != null;
        String editIp = ipEdit.getText().toString();
        assert portEdit != null;
        String editPort = portEdit.getText().toString();
        if(!editIp.equals(ipAddress)){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.pref_ipAddress_key), editIp);
            editor.apply();
            ipAddress = editIp;
        }
        if(!editPort.equals(port)){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.pref_port_key), editPort);
            editor.apply();
            port = editPort;
        }
        Intent intent = new Intent(this, RobotName.class);
        startActivity(intent);

    }
}
