package com.ryanwedoff.senor.naoservercontroller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class RobotName extends AppCompatActivity {
    private RecyclerView.Adapter<RobotNameAdapter.ViewHolder> mAdapter;
    private ArrayList<String> robotNames;
    private SocketService mBoundService;
    private boolean mIsBound;
    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot__name);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.RobotNamesListView);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(false);
        }
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(mLayoutManager);
        }


        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.robot_names);
        String namesObj = sharedPref.getString(getString(R.string.robot_names), defaultValue);
        Gson gson = new Gson();
        String [] rn =  gson.fromJson(namesObj, String[].class);
        robotNames = new ArrayList<>(Arrays.asList(rn));
        //Log.e("Screen 1", robotNames.toString());
        mAdapter = new RobotNameAdapter(robotNames);
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
        }

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            startService(new Intent(RobotName.this, SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.robot_name_layout);
            assert view != null;
            Snackbar.make(view, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.ACTION);
        registerReceiver(myReceiver, intentFilter);

    }


    private ServiceConnection mConnection = new ServiceConnection() {
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
        bindService(new Intent(RobotName.this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
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

    @Override
    protected void onPause(){
        super.onPause();
        doUnbindService();
        unregisterReceiver(myReceiver);
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
            startService(new Intent(RobotName.this, SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.robot_name_layout);
            assert view != null;
            Snackbar.make(view, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.ACTION);
        registerReceiver(myReceiver, intentFilter);
    }



    public void onAddRobot(View view) {
        EditText editText = (EditText) findViewById(R.id.addRobotEdit);
        assert editText != null;
        String name = editText.getText().toString();
        robotNames.add(0, name);
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        assert getCurrentFocus() != null;
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        editText.setText("");
        mAdapter.notifyDataSetChanged();
        updateRobotNamesPref();

        if(mBoundService != null){
            try{
                mBoundService.sendMessage(name + ";" + "Check;");
                Log.i("Sent Check","Send Check");
            } catch (Exception e) {
                Log.e("Socket Connection Error", "Socket Connection Error");
                Snackbar.make(view, "Service Binding Error", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else{
            Snackbar.make(view, "Socket Connection Refused", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public void onTextViewDelete(View view) {
        TextView textView = (TextView) view;
        String name = textView.getText().toString();
        int i = 0;
        while (i < robotNames.size()){
            if (name.equals(robotNames.get(i))) {
                robotNames.remove(i);
                mAdapter.notifyDataSetChanged();
                break;
            } else
                i++;
        }
        updateRobotNamesPref();
    }
    private void updateRobotNamesPref(){
        //Save robotNames into sharedprefs
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(robotNames);
        editor.putString(getString(R.string.robot_names), json);
        editor.apply();

    }

    private Activity getActivity() {
        return this;
    }

    public void onNextBut(View view) {
        if(robotNames.isEmpty()){
            Snackbar.make(view,"No Robots Added", Snackbar.LENGTH_LONG).show();
        } else{
            Intent intent = new Intent(this, ControllerActivity.class);
            startActivity(intent);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }

    public void onGoToMood(View view) {
        if(robotNames.isEmpty()){
            Snackbar.make(view,"No Robots Added", Snackbar.LENGTH_LONG).show();
        } else{
            Intent intent = new Intent(this, MoodActivity.class);
            startActivity(intent);
        }
    }


    public void onGoToFile(View view) {
        if(robotNames.isEmpty()){
            Snackbar.make(view,"No Robots Added", Snackbar.LENGTH_LONG).show();
        } else{
            Intent intent = new Intent(this, FileActivity.class);
            startActivity(intent);
        }
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(SocketService.SERVER_CONNECTION);
            View view = findViewById(R.id.robot_name_layout);
            assert view != null;
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}

