package com.example.zohaibsiddique.praytimecalculator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    File exportDir = Environment.getExternalStoragePublicDirectory("Salah Time 2016");
    double latitude = 0;
    double longitude = 0;
    int timezone = 0;
    TextView dateTextView, fajrTimeTxtView,  sunriseTimeTxtView,
             zuharTimeTxtView,  asarTimeTxtView,  sunsetTimeTxtView,
             magribTimeTxtView,  ishaTimeTxtView, locationTxtView;
    PrayTime prayers;
    final String LOCATION = "location";
    final String LATITUDE = "latitude";
    final String LONGITUDE = "longitude";
    final String TIMEZONE = "timezone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        SessionManager sessionManager = new SessionManager();
        String location = sessionManager.getPreferences(MainActivity.this, LOCATION);

        if(location.length() < 1) {
            setPreferences("Karachi", "24.861462", "67.009939", "5");
            locationTxtView.setText("Karachi");
            prayers = new PrayTime();
            calculatePrayTime(1, prayers.Time12, prayers.Karachi, prayers.Hanafi);

        } else if(location.length() > 0){
            locationTxtView.setText(sessionManager.getPreferences(MainActivity.this, LOCATION));
            prayers = new PrayTime();
            calculatePrayTime(1, prayers.Time12, prayers.Karachi, prayers.Hanafi);
        }


        Button oneDayExportButton = (Button) findViewById(R.id.oneDayExportButton);
        oneDayExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportTxtFileDialog(0,latitude, longitude, timezone);
            }
        });

        Button monthExportButton = (Button) findViewById(R.id.monthExportButton);
        monthExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportTxtFileDialog(30,latitude, longitude, timezone);
            }
        });

        Button yearExportButton = (Button) findViewById(R.id.yearExportButton);
        yearExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportTxtFileDialog(365,latitude, longitude, timezone);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Add.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            SessionManager sessionManager = new SessionManager();
            locationTxtView.setText(sessionManager.getPreferences(MainActivity.this, LOCATION));
            prayers = new PrayTime();
            calculatePrayTime(0, prayers.Time12, prayers.Karachi, prayers.Hanafi);
        }

    }

    private void initializeViews() {
        dateTextView = (TextView) findViewById(R.id.date);
        fajrTimeTxtView = (TextView) findViewById(R.id.time_fajr);
        sunriseTimeTxtView = (TextView) findViewById(R.id.time_sunrise);
        zuharTimeTxtView = (TextView) findViewById(R.id.time_zuhar);
        asarTimeTxtView = (TextView) findViewById(R.id.time_asar);
        sunsetTimeTxtView = (TextView) findViewById(R.id.time_sunset);
        magribTimeTxtView = (TextView) findViewById(R.id.time_magrib);
        ishaTimeTxtView = (TextView) findViewById(R.id.time_isha);
        locationTxtView = (TextView) findViewById(R.id.location);
        locationTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation();
            }
        });
    }

    void selectLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final String[] array = getResources().getStringArray(R.array.location);
        builder.setTitle("Select a Location")
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String location = array[which];

                        if(location.equals("Karachi")) {
                            setPreferences("Karachi", "24.861462", "67.009939", "5");
                            calculatePrayTimeByDialog();
                        } else if(location.equals("Lahore")) {
                            setPreferences("Lahore", "31.554606", "74.357158", "5");
                            calculatePrayTimeByDialog();
                        } else if(location.equals("Islamabad")) {
                            setPreferences("Islamabad", "33.729388", "73.093146", "5");
                            calculatePrayTimeByDialog();
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    void calculatePrayTimeByDialog() {
        SessionManager sessionManager = new SessionManager();
        locationTxtView.setText(sessionManager.getPreferences(MainActivity.this, LOCATION));
        prayers = new PrayTime();
        calculatePrayTime(1, prayers.Time12, prayers.Karachi, prayers.Hanafi);
    }

    void setPreferences(String location, String lat, String longi, String timezone) {
        SessionManager sessionManager = new SessionManager();
        sessionManager.setPreferences(MainActivity.this, LOCATION, location);
        sessionManager.setPreferences(MainActivity.this, LATITUDE, lat);
        sessionManager.setPreferences(MainActivity.this, LONGITUDE, longi);
        sessionManager.setPreferences(MainActivity.this, TIMEZONE, timezone);
    }

    void getPreferences() {
        SessionManager sessionManager = new SessionManager();
        latitude = Double.valueOf(sessionManager.getPreferences(MainActivity.this, LATITUDE));
        longitude = Double.valueOf(sessionManager.getPreferences(MainActivity.this, LONGITUDE));
        timezone = Integer.valueOf(sessionManager.getPreferences(MainActivity.this, TIMEZONE));
    }

    private void calculatePrayTime(int duration, int timeFormat, int calcMethod, int asrJuristic) {
        getPreferences();
        prayers.setTimeFormat(timeFormat);
        prayers.setCalcMethod(calcMethod);
        prayers.setAsrJuristic(asrJuristic);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        dateTextView.setText(String.valueOf(cal.getTime()));

        for(int j = 0; j<=duration; j++) {
            ArrayList<String> prayerTimes = prayers.getPrayerTimes(addDays(now, j), latitude, longitude, timezone);
//            ArrayList<String> prayerNames = prayers.getTimeNames();
            for (int i = 0; i<prayerTimes.size(); i++) {
                fajrTimeTxtView.setText(prayerTimes.get(0));
                sunriseTimeTxtView.setText(prayerTimes.get(1));
                zuharTimeTxtView.setText(prayerTimes.get(2));
                asarTimeTxtView.setText(prayerTimes.get(3));
                sunsetTimeTxtView.setText(prayerTimes.get(4));
                magribTimeTxtView.setText(prayerTimes.get(5));
                ishaTimeTxtView.setText(prayerTimes.get(6));
            }
        }
    }

    public Calendar addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal;
    }

    public Date getDate(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    // input dialog to Export txt file
    protected void exportTxtFileDialog(final int duration, final double latitude, final double longitude, final int timezone) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.export_txt_file_dialog, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText export_txt_file = (EditText) promptView.findViewById(R.id.export_txt_file);
        final TextView export_directory_label = (TextView) promptView.findViewById(R.id.export_directory_label);
        export_directory_label.setText("Save Directory: " + exportDir.toString());

        // setup a dialog window
        alertDialogBuilder.setCancelable(true)
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("EXPORT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String fileName = export_txt_file.getText().toString();
                                exportList(fileName, duration, latitude, longitude, timezone);
                            }
                        });
        // create an alert dialog
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    // Export txt file
    public boolean exportList(String fileName, int duration, double latitude, double longitude, int timezone) {
        /**First of all we check if the external storage of the device is available for writing.
         * Remember that the external storage is not necessarily the sd card. Very often it is
         * the device storage.
         */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return false;
        } else {
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file;
            PrintWriter printWriter = null;
            try {
                file = new File(exportDir, fileName);
                file.createNewFile();

                printWriter = new PrintWriter(new FileWriter(file));

                /**Let's read the first table of the database.
                 * getFirstTable() is a method in our DBCOurDatabaseConnector class which retrieves a Cursor
                 * containing all records of the table (all fields).
                 * The code of this class is omitted for brevity.
                 */

                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                for(int j = 0; j<=duration; j++) {
                    printWriter.println(getDate(now, j));
                    ArrayList<String> prayerTimes = prayers.getPrayerTimes(addDays(now, j), latitude, longitude, timezone);
                    ArrayList<String> prayerNames = prayers.getTimeNames();
                    for (int i = 0; i < prayerTimes.size(); i++) {
                        printWriter.println(prayerNames.get(i) + " - " + prayerTimes.get(i) + "\n");
                    }
                }
                Util.shortToast(MainActivity.this, "File saved in directory: "+exportDir);
            } catch (Exception exc) {
                //if there are any exceptions, return false
                return false;
            } finally {
                if (printWriter != null) printWriter.close();
            }
            //If there are no errors, return true.
            return true;
        }
    }
















































    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
