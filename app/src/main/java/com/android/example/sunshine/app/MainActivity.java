package com.android.example.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;


public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    private String mLocation;
    private final String FORECASTFRAGMENT_TAG = "FFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ForecastFragment(),FORECASTFRAGMENT_TAG)
                    .commit();
        }
        Log.v(LOG_TAG, "onCreate");




    }

    @Override
    protected void onDestroy(){
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }

    @Override
    protected void onStart(){
        Log.v(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop(){
        Log.v(LOG_TAG,"onStop");
        super.onStop();
    }

    @Override
    protected void onPause(){
        Log.v(LOG_TAG,"onPause");
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.v(LOG_TAG, "onResume");
        String location = Utility.getPreferredLocation(this);
        if (location!=null&&!location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if (null != ff){
                ff.onLocationChanged();
            }
            mLocation=location;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
       return true;
    }

    private void openPreferredLocationInMap(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String location = pref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        Intent locationIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                                .appendQueryParameter("q", location)
                                .build();
        locationIntent.setData(geoLocation);
        if (locationIntent.resolveActivity(getPackageManager())!=null){
            startActivity(locationIntent);
        } else {
            Log.d(LOG_TAG, "couldn't call " + location + ", no receiving apps installed");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.view_location){
            openPreferredLocationInMap();
            return true;

        }



        return super.onOptionsItemSelected(item);
    }
}
