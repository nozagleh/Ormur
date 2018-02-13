package com.nozagleh.ormur;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static String ACTIVITY_TAG = "MainActivity";

    private TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLocation = (TextView) findViewById(R.id.txtLocation);

        checkPermissions();
    }

    private void checkPermissions() {
        if (!Permissions.has_gps(this)) {
            Permissions.ask_gps(this);
        } else {
            Locator.startListening(this);
            Location location = Locator.getLocation(this);

            Log.d(ACTIVITY_TAG, location.toString());
            String loc = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
            txtLocation.setText(loc);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(ACTIVITY_TAG, grantResults.toString());
    }
}
