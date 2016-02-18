package com.ryanwedoff.senor.naoservercontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class RobotName extends AppCompatActivity {
    private RecyclerView.Adapter<ReceiveSocketAdapter.ViewHolder> mAdapter;
    private ArrayList<String> robotNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot__name);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.RobotNamesListView);
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        robotNames = new ArrayList<>();
        mAdapter = new ReceiveSocketAdapter(robotNames);
        mRecyclerView.setAdapter(mAdapter);


    }

    public void onAddRobot(View view) {
        EditText editText = (EditText) findViewById(R.id.addRobotEdit);
        String name = editText.getText().toString();
        robotNames.add(0, name);
        mAdapter.notifyDataSetChanged();
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
    }
}

