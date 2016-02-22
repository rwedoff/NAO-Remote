package com.ryanwedoff.senor.naoservercontroller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;


import java.util.ArrayList;

public class ControllerActivity extends AppCompatActivity implements RemoteFragment.OnSendMessageListener {

    public String ipAddress;
    public int port;
    public static ArrayList robotNames;
    SocketService mBoundService;
    private boolean mIsBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String ipAddress = preferences.getString(getString(R.string.pref_ipAddress_key), getString(R.string.pref_ipAddress_default));
        String serverPort = preferences.getString(getString(R.string.pref_port_key), getString(R.string.pref_port_default));

        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.robot_names);
        String namesObj = sharedPref.getString(getString(R.string.robot_names), defaultValue);
        Gson gson = new Gson();
        robotNames = gson.fromJson(namesObj, ArrayList.class);
        //Log.e("Screen 1", robotNames.toString());
        if(robotNames == null){
            robotNames = new ArrayList<>();
        }


        Log.e("SAVED IP: " , ipAddress);
        Log.e("SAVED PORT: ", serverPort);
        Log.e("SAVED NAMES: ",robotNames.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            startService(new Intent(ControllerActivity.this, SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.root_view);
            Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private ServiceConnection mConnection = new ServiceConnection() {
        //EDITED PART
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((SocketService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }

    };
    private void doBindService() {
        //swipeContainer.setRefreshing(false);
        bindService(new Intent(ControllerActivity.this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        if(mBoundService!=null){
            mBoundService.IsBoundable();
        }
    }
    private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


    //TODO fix menu
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
        //TODO probably the fix is here
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public Context getActivity() {
        return this;
    }

    @Override
    public void onSendMessage(String message) {
        Log.i("SEND A MESSAGE: ", message);
        if(mBoundService != null){
            try{
                mBoundService.sendMessage(message);
            }  catch (Exception  e){
                //TODO Make errors popup in fragment
                Log.e("Socket Connection Error", "Socket Connection Error");
                //Snackbar.make(view, "Socket Connection Error", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        } else{
            Log.e("Socket Connection Error", "Socket Error");

        }
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a RemoteFragment (defined as a static inner class below).
            return RemoteFragment.newInstance(numToRobot(position));
        }

        @Override
        public int getCount() {
            return robotNames.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position < robotNames.size()){
                return (CharSequence) robotNames.get(position);
            } else {
                return null;
            }
        }
    }
    protected static String numToRobot(int position){
        if(position < robotNames.size())
            return (String) robotNames.get(position);
        else
            return null;
    }
}
