package com.ryanwedoff.senor.naoservercontroller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class SocketSendActivity extends AppCompatActivity {

    SocketService mBoundService;
    private boolean mIsBound;
    MyReceiver myReceiver;
    private SwipeRefreshLayout swipeContainer;

    private RecyclerView.Adapter mAdapter;
    private List<String> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDataset = new ArrayList<>();
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ReceiveSocketAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBoundService.recvMess();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            startService(new Intent(SocketSendActivity.this, SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.root_view);
            Snackbar.make( view,"No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send, menu);
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
        } else if(id == R.id.RefreshConnection){
            unregisterReceiver(myReceiver);
            stopService(new Intent(SocketSendActivity.this, SocketService.class));
            startService(new Intent(SocketSendActivity.this, SocketService.class));
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SocketService.MY_ACTION);
            registerReceiver(myReceiver, intentFilter);

            myDataset.clear();
            mAdapter.notifyDataSetChanged();
            mBoundService.recvMess();
        } else {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // app icon in action bar clicked; goto parent activity.
                    this.finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
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
        bindService(new Intent(SocketSendActivity.this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
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

    public void onSend(View view){
        EditText editText = (EditText) findViewById(R.id.sendMessageEdit);
        String message = editText.getText().toString();
        //Sends the message
        if(mBoundService != null){
            try{
                mBoundService.sendMessage(message);
            }  catch (Exception  e){
                Snackbar.make(view, "Socket Connection Error", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            /*try{
                mBoundService.recvMess();
            }catch (Exception e2){
                Snackbar.make(view, "Socket Connection Error", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }*/

        } else{
            Log.e("Socket Connection Error", "Socket Error");

        }
        InputMethodManager inputManager =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //noinspection ConstantConditions
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        editText.setText("");

    }



    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(SocketService.EXTRA_MESSAGE);
            mBoundService.recvMess();
            myDataset.add(0, message);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startService(new Intent(SocketSendActivity.this, SocketService.class));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
        stopService(new Intent(SocketSendActivity.this, SocketService.class));
    }

}