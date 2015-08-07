package com.android.example.sunshine.app;

/**
 * Created by John on 8/5/2015.
 */

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID

    };
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_WEATHER_CONDITION_ID=9;

    private String forecastStr;
    private ShareActionProvider mShareActionProvider;

    private final int LOADER_ID = 77;

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView headerView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView humidView;
        public final TextView windView;
        public final TextView pressureView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            headerView = (TextView) view.findViewById(R.id.detail_day_textview);
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
            humidView = (TextView) view.findViewById(R.id.detail_humidity_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
            pressureView=(TextView) view.findViewById(R.id.detail_pressure_textview);
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent==null) return null;
        return new CursorLoader(getActivity(),intent.getData(),FORECAST_COLUMNS, null, null, null);

    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data){
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }
        String friendlyDateString = Utility.getFriendlyDayString(getActivity(),data.getLong(COL_WEATHER_DATE));
        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String weatherDescription = data.getString(COL_WEATHER_DESC);
        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity(),data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity(),data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        ViewHolder vh = (ViewHolder) getView().getTag();
        vh.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        vh.dateView.setText(dateString);
        vh.headerView.setText(friendlyDateString);
        vh.descriptionView.setText(weatherDescription);
        vh.highTempView.setText(high);
        vh.lowTempView.setText(low);
        String wind = Utility.getFormattedWind(getActivity(),data.getFloat(COL_WEATHER_WIND_SPEED),data.getFloat(COL_WEATHER_DEGREES));
        vh.windView.setText(wind);
        String humidity = getActivity().getString(R.string.format_humidity,data.getFloat(COL_WEATHER_HUMIDITY));
        String pressure = getActivity().getString(R.string.format_pressure,data.getFloat(COL_WEATHER_PRESSURE));
        vh.humidView.setText(humidity);
        vh.pressureView.setText(pressure);
        forecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(setShareIntent());
        }
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, Bundle.EMPTY, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider != null){
            mShareActionProvider.setShareIntent(setShareIntent());
        }
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ViewHolder viewHolder= new ViewHolder(rootView);
        rootView.setTag(viewHolder);

        return rootView;
    }

    private Intent setShareIntent(){
        Intent shareIntent =  new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String message=forecastStr;
        message= message + " #SunshineAPP";
        shareIntent.putExtra(Intent.EXTRA_TEXT,message);
        return shareIntent;
    }


}
