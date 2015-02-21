package io.github.gatimus.routetracker;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteTrackerFragment extends MapFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final int MAP_ZOOM = 18; // Google Maps supports 1-21

    private GoogleMap map;
    private ToggleButton toggleButton;
    private GoogleApiClient googleApiClient;
    private Location currentLocation, lastLocation;
    private LocationRequest locationRequest;
    private boolean isTracking = true;


    public static RouteTrackerFragment newInstance() {
        return new RouteTrackerFragment();
    }

    public RouteTrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.moveCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM));
            }
        });
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(RouteTrackerFragment.this)
                .addOnConnectionFailedListener(RouteTrackerFragment.this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setSmallestDisplacement(1);
        locationRequest.setFastestInterval(333);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    // called when Activity becoming visible to the user
    @Override
    public void onStart() {
        super.onStart(); // call super's onStart method
        googleApiClient.connect();
    } // end method onStart

    // called when Activity is no longer visible to the user
    @Override
    public void onStop() {
        super.onStop(); // call the super method
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        googleApiClient.disconnect();
    } // end method onStop

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onLowMemory (){
        locationRequest.setInterval(1000);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(getClass().getSimpleName(), "connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST){
            Log.w(getClass().getSimpleName(), "Network Lost");
            Toast.makeText(getActivity(),"Network Lost",Toast.LENGTH_SHORT).show();
        }
        if(i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED){
            Log.w(getClass().getSimpleName(), "Service Disconnected");
            Toast.makeText(getActivity(),"Service Disconnected",Toast.LENGTH_SHORT).show();
        }
        googleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(getClass().getSimpleName(), connectionResult.toString());
        Toast.makeText(getActivity(),connectionResult.toString(),Toast.LENGTH_SHORT).show();
        googleApiClient.reconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(getClass().getSimpleName(), "LocationChanged");
        if(isTracking){
            currentLocation = location;
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            map.addCircle(new CircleOptions().center(currentLatLng).fillColor(Color.RED).strokeColor(Color.RED).radius(1));
            if(lastLocation!=null){
                LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                map.addPolyline(new PolylineOptions().add(currentLatLng,lastLatLng).color(Color.BLACK).width(1));
            }
            lastLocation = location;
        }
    }

    public void setMapType(int type){
        map.setMapType(type);
    }

    public void setTracking(boolean tracking){
        isTracking = tracking;
    }

}
