package com.ryanwedoff.senor.naoservercontroller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class FileActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 1;
    private RecyclerView.Adapter<FileTextAdapter.ViewHolder> mAdapter;
    private ArrayList<String> fileLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fileLines = new ArrayList<>();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.file_list_view);
        mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FileTextAdapter(fileLines);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_file);
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
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onRestart(View view) {
    }

    public void onReSend(View view) {
    }

    public void onClear(View view) {
    }

    private class GetRobotFile extends AsyncTask<Uri, Integer, String> {
        /**
         * This is done under async to ensure that network sources can pull in files
         */
        protected String doInBackground(Uri... uri){
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri[0]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader reader;
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                        fileLines.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("File Text:", stringBuilder.toString());

                return stringBuilder.toString();
            }else{
                Log.e("Null URI Error","Null URI Error");
            }
            return "File Error";
        }
    }



}
