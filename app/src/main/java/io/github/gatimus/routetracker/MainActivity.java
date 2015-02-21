package io.github.gatimus.routetracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends ActionBarActivity {

    private RouteTrackerFragment mapFragment;
    public ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapFragment = (RouteTrackerFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        toggleButton = (ToggleButton) findViewById(R.id.trackingToggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mapFragment.setTracking(isChecked);
            }
        });
    }

    // create the Activity's menu from a menu resource XML file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.route_tracker_menu, menu);
        return true;
    } // end method onCreateOptionsMenu

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // perform appropriate task based on
        switch (item.getItemId()) {
            case R.id.mapItem: // the user selected "Map"
                mapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.satelliteItem: // the user selected "Satellite"
                mapFragment.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } // end switch
    } // end method onOptionsItemSelected

}
