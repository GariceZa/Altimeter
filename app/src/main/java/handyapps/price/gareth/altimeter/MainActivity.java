package handyapps.price.gareth.altimeter;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends ActionBarActivity implements LocationListener {

    private TextView height,heightUnit;
    private LocationManager locMan;
    private String provider;
    private AdView mAdView;
    private int accuracy;
    private ImageView gpsAccuracy;
    private int altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define the color of the action bar
        setActionBarColor();

        // Set font of text views
        setTypeFace();

        // sets the preference default values the first time the app runs
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    @Override
    protected void onResume() {

        super.onResume();
        // If the devices location services are disabled
        if(!locationServiceEnabled()){
            // Notify user
            locationServiceDisabledAlert();
        }
        else {
            // Start retrieving location updates
            startLocationUpdates();
            // Start admob
            startAds();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        // Stop retrieving location updates
        stopLocationUpdates();
        // Pause admob
        pauseAds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this,ShowUserPreferences.class));
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

        // Executes an async task to update the speed every time there is a location update
        new RetrieveHeight().execute(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("--onStatusChanged", "Status Changed " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("--onProviderEnabled","Provider Enabled " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("--onProviderDisabled","Provider Disabled " + provider);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Save the current altitude, altitude unit & location accuracy
        outState.putString("STATE_HEIGHT",height.getText().toString());
        outState.putString("STATE_UNIT",heightUnit.getText().toString());
        outState.putInt("accuracy",accuracy);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore from saved instance
        height.setText(savedInstanceState.getString("STATE_HEIGHT"));
        heightUnit.setText(savedInstanceState.getString("STATE_UNIT"));
        accuracy = savedInstanceState.getInt("accuracy");
        setGpsAccuracy();
    }

    // Starts new ad requests from admob
    private void startAds(){

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    // Pauses admob requests
    private void pauseAds(){

        if(mAdView != null){
            mAdView.pause();
        }
    }

    // Sets the color of the actionbar
    private void setActionBarColor(){

        ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_background)));
    }

    // Determines if location services are enabled
    private boolean locationServiceEnabled() {

        LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Displays an alert dialog allowing the user to turn location services on
    private void locationServiceDisabledAlert(){

        //https://github.com/afollestad/material-dialogs
        new MaterialDialog.Builder(this)
                .title(R.string.enable_location_service_title)
                .content(R.string.enable_location_service)
                .positiveText(R.string.enable)
                .negativeText(R.string.exit)
                .cancelable(false)
                .iconRes(R.drawable.ic_alert_white_36dp)
                .theme(Theme.DARK)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //opens the location service settings
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        // closes the speedometer application
                        finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        // closes the application
                        finish();
                    }
                })
                .show();
    }

    // Sets the text to the digital typeface stored in the assets folder
    private void setTypeFace(){

        Typeface digitalFont = Typeface.createFromAsset(getAssets(),"fonts/digital.ttf");
        height = (TextView)findViewById(R.id.tvHeight);
        height.setTypeface(digitalFont);

        heightUnit = (TextView)findViewById(R.id.tvHeightUnit);
        heightUnit.setTypeface(digitalFont);
    }

    // Sets the unit value to display
    private void setUnit(){

        Preferences preferences = new Preferences(MainActivity.this);

        if(preferences.getHeightUnit() == 1){

            heightUnit.setText(R.string.meters);
        }
        else if(preferences.getHeightUnit() == 3.28084){
            heightUnit.setText(R.string.feet);
        }
    }

    // Starts location updates
    private void startLocationUpdates(){

        locMan      = (LocationManager)getSystemService(LOCATION_SERVICE);
        provider    = LocationManager.GPS_PROVIDER;
        locMan.requestLocationUpdates(provider,1000,0, this);
    }

    // Stops location updates
    private void stopLocationUpdates(){

        if(locMan != null) {
            locMan.removeUpdates(this);
        }
    }

    // Sets the satellite icon depending on the location accuracy
    private void setGpsAccuracy(){

        gpsAccuracy = (ImageView)findViewById(R.id.ivGPSAccuracy);
        if(accuracy < 10){
            gpsAccuracy.setBackgroundResource(R.drawable.gps_high);
        }
        else if( accuracy < 20){
            gpsAccuracy.setBackgroundResource(R.drawable.gps_med);
        }
        else{
            gpsAccuracy.setBackgroundResource(R.drawable.gps_low);
        }
    }

    // Update height on background thread
    private class RetrieveHeight extends AsyncTask<Location,Void,Integer>{

        @Override
        protected Integer doInBackground(Location... params) {

            // Instantiates a new instance of the LocationInfo class
            LocationInfo locationInfo = new LocationInfo(params[0],getApplicationContext());

            altitude = locationInfo.getHeight();
            accuracy = locationInfo.getAccuracy();

            // Returns the current altitude
            return altitude;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            super.onPostExecute(integer);
            // Sets the text view to the current speed
            height.setText(String.valueOf(integer));

            // Sets the speed unit text view
            setUnit();

            // sets accuracy icon
            setGpsAccuracy();
        }
    }
}
