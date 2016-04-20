/**
 * ControllerActivity is the parent activity to Remote Fragment, WalkFragment, and JoyStick Fragment
 *
 * @see com.ryanwedoff.senor.naoservercontroller.WalkFragment
 * @see com.ryanwedoff.senor.naoservercontroller.RemoteFragment
 * @see layout.JoyStickFrag
 */

package com.ryanwedoff.senor.naoservercontroller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ControllerActivity extends AppCompatActivity implements RemoteFragment.OnSendMessageListener {

    private static ArrayList robotNames;
    private SocketService mBoundService;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((SocketService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }

    };
    private boolean mIsBound;

    private static String numToRobot(int position) {
        if (position < robotNames.size())
            return (String) robotNames.get(position);
        else
            return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        Context context = getActivity();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.robot_names);
        String namesObj = sharedPref.getString(getString(R.string.robot_names), defaultValue);
        Gson gson = new Gson();
        robotNames = gson.fromJson(namesObj, ArrayList.class);
        if(robotNames == null){
            robotNames = new ArrayList<>();
        }


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
        assert  mViewPager!= null;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
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
            View view = findViewById(R.id.controller_root_view);
            assert view != null;
            Snackbar.make(view, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void doBindService() {
        bindService(new Intent(ControllerActivity.this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
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

    @Override
    protected void onPause(){
        super.onPause();
        doUnbindService();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            startService(new Intent(ControllerActivity.this, SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.controller_root_view);
            assert view != null;
            Snackbar.make(view, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controller_activity, menu);
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
        } else if(id == R.id.refresh_connection){
            stopService(new Intent(ControllerActivity.this, SocketService.class));
            startService(new Intent(ControllerActivity.this, SocketService.class));
            View view = findViewById(R.id.controller_root_view);
            assert view != null;
            Snackbar.make(view, "Reconnecting...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if(id == R.id.robot_names_menu){
            Intent intent = new Intent(this, RobotName.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Context getActivity() {
        return this;
    }

    @Override
    public void onSendMessage(String message) {
        if(mBoundService != null){
            try{
                mBoundService.sendMessage(message);
            } catch (Exception e) {
                Log.e("Socket Connection Error", "Socket Connection Error");
                View view = findViewById(R.id.controller_root_view);
                assert view != null;
                Snackbar.make(view, "Service Binding Error", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else{
            View view = findViewById(R.id.controller_root_view);
            assert view != null;
            Snackbar.make(view, "Socket Connection Refused", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
}
