package com.example.gps_tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private TextView hoehenValueTextView, speedValueTextView;
    private TextView latitudeValueTextView, longitudeValueTextView;

    private boolean isDecimalDegrees = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeValueTextView = findViewById(R.id.latitude_value_textview);
        longitudeValueTextView = findViewById(R.id.longitude_value_textview);
        hoehenValueTextView = findViewById(R.id.hoehen_value_textview);
        speedValueTextView = findViewById(R.id.speed_value_textView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Überprüfen, ob die Berechtigung zur Standortabfrage erteilt wurde
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gps_provider_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Standortabfrage wurde erteilt
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        float speed = location.getSpeed();

        if (isDecimalDegrees) {
            latitudeValueTextView.setText(String.format("Lat: %.6f", latitude));
            longitudeValueTextView.setText(String.format("Lng: %.6f", longitude));
        } else {
            latitudeValueTextView.setText(convertToDMS(latitude, true));
            longitudeValueTextView.setText(convertToDMS(longitude, false));
        }
        hoehenValueTextView.setText(String.format("%.2f m", altitude));
        speedValueTextView.setText(String.format("%.2f km/h", speed));
    }

    private String convertToDMS(double coordinate, boolean isLatitude) {
        String direction = isLatitude ? (coordinate >= 0 ? "N" : "S") : (coordinate >= 0 ? "E" : "W");
        coordinate = Math.abs(coordinate);
        int degrees = (int) coordinate;
        coordinate = (coordinate - degrees) * 60;
        int minutes = (int) coordinate;
        coordinate = (coordinate - minutes) * 60;
        double seconds = coordinate;

        return String.format("%d°%d'%.1f\"%s", degrees, minutes, seconds, direction);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_decimal_degrees:
                setCoordinateFormat(true);
                return true;
            case R.id.action_degrees_minutes_seconds:
                setCoordinateFormat(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setCoordinateFormat(boolean decimalDegrees) {
        isDecimalDegrees = decimalDegrees;
        try {
            double latitude = Double.parseDouble(latitudeValueTextView.getText().toString().substring(5));
            double longitude = Double.parseDouble(longitudeValueTextView.getText().toString().substring(5));
            if (decimalDegrees) {
                latitudeValueTextView.setText(String.format("Lat: %.6f", latitude));
                longitudeValueTextView.setText(String.format("Lng: %.6f", longitude));
            } else {
                latitudeValueTextView.setText(convertToDMS(latitude, true));
                longitudeValueTextView.setText(convertToDMS(longitude, false));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Fehler beim Konvertieren der Koordinaten", Toast.LENGTH_SHORT).show();
        }
    }
}
