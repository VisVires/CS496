package com.androidui.georgew.sqlitegps;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tagmanager.TagManagerApiImpl;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SQLiteDB sqLiteDB;
    Button getWord;
    Cursor sqlCursor;
    SimpleCursorAdapter sqlCursorAdaptor;
    SQLiteDatabase mySQLDB;
    private static final String TAG = "MainActivity";
    private GoogleApiClient googleApiClient;
    private Location myLocation;
    private LocationListener locationListener;
    private LocationRequest locationRequest;
    private TextView latitude;
    private TextView longitude;
    private static final int LOCATION_PERMISSION_RESULT = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        latitude = (TextView) findViewById(R.id.lat_coordinate);
        longitude = (TextView) findViewById(R.id.long_coordinate);
        latitude.setText("Activity Created");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    longitude.setText(String.valueOf(location.getLongitude()));
                    latitude.setText(String.valueOf(location.getLatitude()));
                } else {
                    longitude.setText("No Location Avaliable");
                }
            }
        };

        sqLiteDB = new SQLiteDB(this);
        mySQLDB = sqLiteDB.getWritableDatabase();

        getWord = (Button) findViewById(R.id.getWord);
        getWord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mySQLDB != null){
                    ContentValues vals = new ContentValues();
                    vals.put(DBContract.myTable.COLUMN_NAME_LONGITUDE, String.valueOf(myLocation.getLongitude()));
                    vals.put(DBContract.myTable.COLUMN_NAME_LATITUDE, String.valueOf(myLocation.getLatitude()));
                    vals.put(DBContract.myTable.COLUMN_NAME_WORD, ((EditText)findViewById(R.id.input_word)).getText().toString());
                    mySQLDB.insert(DBContract.myTable.TABLE_NAME, null, vals);
                } else {
                    Log.d(TAG, "Unable to access DB for writing");
                }
            }
        });

        populateTable();
    }

    private void populateTable(){
        if (mySQLDB != null){
            try {
                if(sqlCursorAdaptor != null && sqlCursorAdaptor.getCursor() != null){
                    if(!sqlCursorAdaptor.getCursor().isClosed()){
                        sqlCursorAdaptor.getCursor().close();
                    }
                }
                //sqlCursor = mySQLDB.query(DBContract.myTable.TABLE_NAME,
                  //      new String[]{DBContract.myTable._ID, DBContract.myTable.COLUMN_NAME_LONGITUDE, DBContract.myTable.COLUMN_NAME_LATITUDE, DBContract.myTable.COLUMN_NAME_WORD}, null, null, null);

            } catch (Exception e) {
                Log.d(TAG, "Unable to load data from database");
            }
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        latitude.setText("Started Activity");
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        latitude.setText("onConnect");
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_RESULT);
            longitude.setText("NO PERMISSIONS");
            return;
        }
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0);
        errorDialog.show();
        return;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_RESULT) {
            if (grantResults.length > 0) {
                updateLocation();
            }
        }
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (myLocation != null) {
            longitude.setText(String.valueOf(myLocation.getLongitude()));
            latitude.setText(String.valueOf(myLocation.getLatitude()));
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        }
    }
}

class SQLiteDB extends SQLiteOpenHelper{

    public SQLiteDB(Context context){
        super(context, DBContract.myTable.DB_NAME, null, DBContract.myTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DBContract.myTable.SQL_CREATE_TABLE);
        ContentValues testers = new ContentValues();
        testers.put(DBContract.myTable.COLUMN_NAME_LONGITUDE, -125);
        testers.put(DBContract.myTable.COLUMN_NAME_LATITUDE, 60);
        testers.put(DBContract.myTable.COLUMN_NAME_WORD, "fishyWord");
        db.insert(DBContract.myTable.TABLE_NAME, null, testers);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DBContract.myTable.SQL_DROP_TABLE);
        onCreate(db);
    }

}

final class DBContract{
    private DBContract(){};

    public final class myTable implements BaseColumns{
        public static final String DB_NAME = "Coordinates";
        public static final String TABLE_NAME = "Coordinate_Table";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_WORD = "Word";
        public static final int DB_VERSION = 1;

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
                myTable.TABLE_NAME + "(" + myTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                myTable.COLUMN_NAME_LONGITUDE + "DOUBLE," +
                myTable.COLUMN_NAME_LATITUDE + "DOUBLE," +
                myTable.COLUMN_NAME_WORD + "VARCHAR(255))";

        public static final String SQL_TEST_TABLE_INSERT = "INSERT INTO " + TABLE_NAME + " (" +
                COLUMN_NAME_LONGITUDE + ", " + COLUMN_NAME_LATITUDE + ", " + COLUMN_NAME_WORD + ") VALUES (-145, 60, 'test');";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + myTable.TABLE_NAME;
    }
}