package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.layout.listeners.SurveyLocationListener;

public abstract class ADashboardActivityStrategy {
    private final static String TAG = ".DashActivityStrategy";

    public abstract void reloadStockFragment(Activity activity);

    public abstract boolean showStockFragment(Activity activity, boolean isMoveToLeft);

    public abstract void newSurvey(Activity activity);

    public abstract void sendSurvey();

    public abstract boolean beforeExit(boolean isBackPressed);

    public abstract void completeSurvey();

    public abstract boolean isHistoricNewReceiptBalanceFragment(Activity activity);

    public void prepareLocationListener(Activity activity, Survey survey) {

        SurveyLocationListener locationListener = new SurveyLocationListener(survey.getId_survey());

        LocationManager locationManager = null;
        try {
            locationManager =
                    (LocationManager) LocationMemory.getContext().getSystemService(
                            Context.LOCATION_SERVICE);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        if (locationManager == null)
        {
            saveDefaultLocationIfIsNotAvailable(activity, locationListener);
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(TAG, "requestLocationUpdates via NETWORK");
            if (ActivityCompat.checkSelfPermission(activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                saveDefaultLocationIfIsNotAvailable(activity, locationListener);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "requestLocationUpdates via GPS");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListener);
        } else {
            Location lastLocation = locationManager.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER);

            if (lastLocation != null) {
                Log.d(TAG, "location not available via GPS|NETWORK, last know: " + lastLocation);
                locationListener.saveLocation(lastLocation);
            } else {
                saveDefaultLocationIfIsNotAvailable(activity, locationListener);
            }
        }
    }

    private void saveDefaultLocationIfIsNotAvailable(Activity activity,
            SurveyLocationListener locationListener) {
        Location defaultLocation = createDefaultLocation(activity);
        locationListener.saveLocation(defaultLocation);
    }

    @NonNull
    private Location createDefaultLocation(Activity activity) {
        String defaultLatitude = activity.getString(
                R.string.GPS_LATITUDE_DEFAULT);
        String defaultLongitude = activity.getString(
                R.string.GPS_LONGITUDE_DEFAULT);
        Location defaultLocation = new Location(
                activity.getString(R.string.GPS_PROVIDER_DEFAULT));
        defaultLocation.setLatitude(Double.parseDouble(defaultLatitude));
        defaultLocation.setLongitude(Double.parseDouble(defaultLongitude));
        Log.d(TAG, "location not available via GPS|NETWORK, default: " + defaultLocation);
        return defaultLocation;
    }
}
