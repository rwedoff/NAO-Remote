package com.ryanwedoff.senor.naoservercontroller;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class RobotName extends AppCompatActivity {
    private RecyclerView.Adapter<RobotNameAdapter.ViewHolder> mAdapter;
    private ArrayList robotNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot__name);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.RobotNamesListView);
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.robot_names);
        String namesObj = sharedPref.getString(getString(R.string.robot_names), defaultValue);
        Gson gson = new Gson();
        robotNames = gson.fromJson(namesObj, ArrayList.class);
        //Log.e("Screen 1", robotNames.toString());
        if(robotNames == null){
            robotNames = new ArrayList<>();
        }
        mAdapter = new RobotNameAdapter(robotNames);
        mRecyclerView.setAdapter(mAdapter);


    }

    public void onAddRobot(View view) {
        EditText editText = (EditText) findViewById(R.id.addRobotEdit);
        String name = editText.getText().toString();
        robotNames.add(0, name);
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        //noinspection ConstantConditions
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        editText.setText("");
        mAdapter.notifyDataSetChanged();
        updateRobotNamesPref();
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

    public Activity getActivity() {
        return this;
    }

    public void onNextBut(View view) {
        Intent intent = new Intent(this, ControllerActivity.class);
        startActivity(intent);
    }


    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
}

