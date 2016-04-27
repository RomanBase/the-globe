package com.base.wall.weather;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class CoarseLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private LocationObtainedListener locationListener;

    public CoarseLocation(Context context) {

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void requestLocation() {

        googleApiClient.connect();
    }

    public void setLocationListener(LocationObtainedListener locationListener) {
        this.locationListener = locationListener;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            String lat = String.valueOf(location.getLatitude());
            String lon = String.valueOf(location.getLongitude());

            if (locationListener != null) {
                locationListener.onLocationObtained(lat, lon);
            }

            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (locationListener != null) {
            locationListener.onLocationObtained(null, null);
        }
    }

    public static interface LocationObtainedListener {

        void onLocationObtained(String lat, String lon);
    }
}
