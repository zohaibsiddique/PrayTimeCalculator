package com.example.zohaibsiddique.praytimecalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Add extends AppCompatActivity {

    EditText latitudeEditText, longitudeEditText, timezoneEditText, locationEditText;
    Button submitButton;
    final String LOCATION = "location";
    final String LATITUDE = "latitude";
    final String LONGITUDE = "longitude";
    final String TIMEZONE = "timezone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeLayout();

    }

    private void initializeLayout() {
        locationEditText = (EditText) findViewById(R.id.location);
        latitudeEditText = (EditText) findViewById(R.id.latitude);
        longitudeEditText = (EditText) findViewById(R.id.longitutde);
        timezoneEditText = (EditText) findViewById(R.id.timezone);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(latitudeEditText) || isEmpty(longitudeEditText) || isEmpty(timezoneEditText)) {
                    Toast.makeText(Add.this, "Error, Please write values", Toast.LENGTH_LONG).show();
                } else {

                    SessionManager sessionManager = new SessionManager();
                    sessionManager.setPreferences(Add.this, LOCATION, locationEditText.getText().toString());
                    sessionManager.setPreferences(Add.this, LATITUDE, latitudeEditText.getText().toString());
                    sessionManager.setPreferences(Add.this, LONGITUDE, longitudeEditText.getText().toString());
                    sessionManager.setPreferences(Add.this, TIMEZONE, timezoneEditText.getText().toString());
                    Toast.makeText(Add.this, "Location saved!", Toast.LENGTH_LONG).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
