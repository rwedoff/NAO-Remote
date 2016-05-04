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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
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
    private String mood;
    private String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);


        Context context = getActivity();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.robot_names);
        String namesObj = sharedPref.getString(getString(R.string.robot_names), defaultValue);
        Gson gson = new Gson();
        ArrayList robotNames = gson.fromJson(namesObj, ArrayList.class);
        if(robotNames == null){
            robotNames = new ArrayList<>();
            Intent intent = new Intent(this, RobotName.class);
            startActivity(intent);
        }

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            startService(new Intent(MoodActivity.this, SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.controller_root_view);
            assert view != null;
            Snackbar.make(view, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Spinner robotNameSpinner = (Spinner) findViewById(R.id.robot_name_spinner);
        String[] robotNamesArray = (String[]) robotNames.toArray(new String[robotNames.size()]);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, robotNamesArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assert robotNameSpinner != null;
        robotNameSpinner.setAdapter(spinnerArrayAdapter);
        robotNameSpinner.setOnItemSelectedListener(this);

        Spinner moodSpinner = (Spinner) findViewById(R.id.mood_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mood_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        assert moodSpinner != null;
        moodSpinner.setAdapter(adapter);
        moodSpinner.setOnItemSelectedListener(this);
    }

    private void doBindService() {
        bindService(new Intent(MoodActivity.this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
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
            startService(new Intent(MoodActivity.this, SocketService.class));
            doBindService();
        } else {
            View view = findViewById(R.id.controller_root_view);
            assert view != null;
            Snackbar.make(view, "No network connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }


    private Context getActivity() {
        return this;
    }

    public void onSend(View view) {
        EditText editText = (EditText) findViewById(R.id.sendMessageEdit);
        assert editText != null;
        String message = editText.getText().toString();
        message = message.replace(';',':');
        //Sends the message
        if(mBoundService != null){
            try{
                String currentMess = currentName + ";" + "Mood;" + mood + ";" + message + ";";
                mBoundService.sendMessage(currentMess);
            }  catch (Exception  e){
                Snackbar.make(view, "Socket Connection Error", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else{
            Log.e("Socket Connection Error", "Socket Error");

        }
        InputMethodManager inputManager =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert getCurrentFocus() != null;
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        editText.setText("");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       setSpinnerStrings(parent.getId(), (String) parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        setSpinnerStrings(parent.getId(), (String) parent.getItemAtPosition(0));
    }

    private void setSpinnerStrings(int parentId, String string){
        if(parentId == R.id.mood_spinner){
            Log.i("Mood Spinner",  string);
            mood = string;
        } else{
            Log.i("Robot Spinner", string);
            currentName = string;
        }
    }
}
