package com.ryanwedoff.senor.naoservercontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

public class ControllerActivity extends AppCompatActivity {

    public String ipAddress;
    public int port;
    public static ArrayList robotNames;
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

    public void onCrouch(View view) {
        Log.e("Crouch","Crouch");
        android.app.FragmentManager fragmentManager = getFragmentManager();

    }

    public void onStand(View view) {
        Log.e("Stand","Stand");
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class RemoteFragment extends Fragment implements View.OnClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NAME = "section_name";
        private static final String ARG_ROBOT_NAME = "robot_name";
        private String rn = "Joe?";
        public RemoteFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        //TODO
        public static RemoteFragment newInstance(String robotName) {
            RemoteFragment fragment = new RemoteFragment();
            Bundle args = new Bundle();
            args.putString(ARG_ROBOT_NAME, robotName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.fragment_remote, container, false);
            //Sets onClick listeners for each fragment, not done in XML to get robot name
            Button mButton = (Button) rl.findViewById(R.id.stand_button);
            mButton.setOnClickListener(this);
            return rl;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = this.getArguments();
            String myName = bundle.getString(ARG_ROBOT_NAME, "Robot Name Error");
            Toast.makeText(getActivity(), myName, Toast.LENGTH_LONG).show();
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
