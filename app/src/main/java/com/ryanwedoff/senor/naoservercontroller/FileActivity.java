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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class FileActivity extends AppCompatActivity  {

    private static final int READ_REQUEST_CODE = 1;
    private FileTextAdapter mAdapter;
    private static ArrayList<CharSequence> fileLines;
    private SocketService mBoundService;
    private boolean mIsBound;
    private MyReceiver myReceiver;
    private NaoFileParse fileParse;


    private static int runningPos = 0;
    private static boolean canSend = true;
    private Handler mHandler = new Handler();
    private static boolean isPaused = true;

    private static TextView logTextView;
    private static final String STATE_File_LINES = "file_lines";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fileLines = new ArrayList<>();

        if (savedInstanceState != null) {
            // Restore value of members from saved state
           fileLines = savedInstanceState.getCharSequenceArrayList(STATE_File_LINES);
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.file_list_view);
        assert mRecyclerView != null;
        mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FileTextAdapter(fileLines);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_file);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Context context = getActivity();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.robot_names);
        String namesObj = sharedPref.getString(getString(R.string.robot_names), defaultValue);
        Gson gson = new Gson();

        String [] rn = gson.fromJson(namesObj, String[].class); //Pull in the robot names
        ArrayList<String> robotNames = new ArrayList<>(Arrays.asList(rn));
        String [] moods = getResources().getStringArray(R.array.mood_array);
        fileParse = new NaoFileParse(robotNames,moods);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.run_pause_button);
        assert toggle != null;
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is running
                    isPaused = false;
                    runFile();
                } else {
                    //Is paused
                    logTextView.setText(String.format("Status: Paused\nLine: %d", runningPos));
                    isPaused = true;
                }
            }
        });

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.ACTION);
        registerReceiver(myReceiver, intentFilter);

        logTextView= (TextView) findViewById(R.id.file_run_log);
        runningPos = 0;
        assert logTextView != null;
        logTextView.setText(R.string.LogInit);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putCharSequenceArrayList(STATE_File_LINES, fileLines);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    private void runFile(){

        if(fileLines.size() == 0){
            View view = findViewById(R.id.file_relative_view);
            assert view != null;
            Snackbar.make(view, "No file loaded", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else{
            final String message = (String) fileLines.get(runningPos);
            if(!isPaused){
                String log = "Status: Running\nLine: " + runningPos;
                logTextView.setText(log);
            }

            if(!SocketService.isServiceRunning)
                connectSocketService();

            if(runningPos < fileLines.size() && !isPaused){
                if (canSend) {
                    Log.i("Running", "File");
                    if(!fileParse.checkLine(message,runningPos) && runningPos!=0 && runningPos!=fileLines.size()-1) {
                        View view = findViewById(R.id.file_relative_view);
                        String errorMess = "Syntax Error at line  " + runningPos + " (" + message + ")";
                        assert view != null;
                        Snackbar.make(view, errorMess, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                    mBoundService.sendMessage(message);
                    checkRunDone();
                    canSend = false;
                }
                //Timer, if message doesn't get a response run next line
                mHandler.postDelayed(new Runnable() {
                    int oldPos = runningPos;
                    public void run() {
                        if (oldPos == runningPos && runningPos!=fileLines.size()-1) {
                            View view = findViewById(R.id.file_relative_view);
                            String errorMess = "No response received at line  " + runningPos + " (" + message + ")";
                            assert view != null;
                            Snackbar.make(view, errorMess, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            runningPos++;
                            checkRunDone();
                            canSend = true;
                            runFile();
                        }
                    }
                }, 10000);
            }
        }

    }

    private void checkRunDone(){
        if(runningPos >= fileLines.size()-1){
            Log.i("File", "Done");
            logTextView.setText(R.string.LogEnd);
            restartFile();
        }
    }

    private void restartFile(){
        ToggleButton toggle = (ToggleButton) findViewById(R.id.run_pause_button);
        assert toggle != null;
        toggle.setChecked(false);
        runningPos = 0;
        canSend = true;
        isPaused = true;
        logTextView.setText(R.string.LogInit);
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
        bindService(new Intent(FileActivity.this,SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
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
        if(myReceiver != null)
            unregisterReceiver(myReceiver);

    }


   @Override
    protected void onResume(){
        super.onResume();
       IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction(SocketService.ACTION);
       registerReceiver(myReceiver, intentFilter);
   }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("URI", "Uri: " + uri.toString());
                new GetRobotFile().execute(uri);
            }
        }
    }


    public void onReSend(View view) {
        TextView textView = (TextView) view.findViewById(R.id.file_text_view);
        String message = textView.getText().toString();
        if(mBoundService != null){
            try{
                int lineNum = fileLines.indexOf(message);
                if(fileParse.checkLine(message,lineNum))
                    mBoundService.sendMessage(message);
            } catch (Exception e) {
                Log.e("Socket Connection Error", "Socket Connection Error");
                Snackbar.make(view, "Service Binding Error", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else{
            Snackbar.make(view, "Socket Connection Refused", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_button:
                fileLines.clear();

                runningPos = 0;
                canSend = true;
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.restart_button:
                restartFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetRobotFile extends AsyncTask<Uri, Void, Void> {
        /**
         * This is done under async to ensure that network sources can pull in files
         */
        protected Void doInBackground(Uri... uri){
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri[0]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader br;
            if (inputStream != null) {
                br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        fileLines.add(line);
                    }
                    if(!fileParse.firstCheckLine((String) fileLines.get(0))){
                        View view = findViewById(R.id.file_relative_view);
                        assert view != null;
                        Snackbar.make(view, "'--NAOSTART' needed at line 0", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        canSend = false;
                        Log.i("--NAO-START", "needed at line 0");
                    }

                    if(!fileParse.lastCheckLine((String) fileLines.get(fileLines.size()-1))){
                        View view = findViewById(R.id.file_relative_view);
                        assert view != null;
                        Snackbar.make(view, "'--NAOSTOP' needed at last line", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        Log.i("--NAO-STOP", "needed at last line");
                        canSend = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                Log.e("Null URI Error","Null URI Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            connectSocketService();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mBoundService != null)
                mBoundService.stopSelf();
        }

    }
    private void connectSocketService(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.ACTION);
        registerReceiver(myReceiver, intentFilter);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            startService(new Intent(FileActivity.this,SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.file_relative_view);
            assert view != null;
            Snackbar.make(view, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        mAdapter.notifyDataSetChanged();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String connectionMess = intent.getStringExtra(SocketService.SERVER_CONNECTION);
            String serverResponse = intent.getStringExtra(SocketService.SERVER_RESPONSE);

            if(connectionMess != null){
                View view = findViewById(R.id.file_relative_view);
                assert view != null;
                Snackbar.make(view, connectionMess, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            if (serverResponse != null) {
                //Log.i("Response: ", serverResponse);
                if(serverResponse.contains("@@@@")){
                    Log.e("TRUE","TRUE");
                    canSend = true;
                    runningPos++;
                    checkRunDone();
                    runFile();
                }
            }
        }
    }

    private Context getActivity() {
        return this;
    }

}
