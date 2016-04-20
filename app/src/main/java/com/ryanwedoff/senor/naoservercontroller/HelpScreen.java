package com.ryanwedoff.senor.naoservercontroller;
/**
 * HelpScreen is a help screen on how to run the file
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class HelpScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
