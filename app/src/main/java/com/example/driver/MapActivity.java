package com.example.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    double[] lats, longs;
    String[] area_names;
    int PERMISSION_ID = 44;
    int p;
    private FusedLocationProviderClient fusedLocationClient ;
    private double driver_lats, driver_longs;
    private GoogleMap mMap;
    Button button_reached;
    Location location;
    String area_name;
    FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Button button_to_gmaps = findViewById(R.id.button_toGMaps);
        button_reached = findViewById(R.id.button_reached);
        button_reached.setEnabled(false);
        // Setting the Map Fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // onclick for the Maps open button
        button_to_gmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving");
                String url = "https://www.google.com/maps/dir/?api=1&destination="+lats[0]+","+longs[0];
                for(int k = 1 ; k< lats.length;k++){
                    if(k==1){
                        url += "&waypoints="+lats[k]+","+longs[k];
                    }
                    else {
                    url += "|"+lats[k]+","+longs[k];
                    Log.d("k",Integer.toString(k));
                    }

                }
                url += "&travelmode=driving";
                Uri gmmIntentUri = Uri.parse(url);
                Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        Intent i = getIntent();
        lats = i.getDoubleArrayExtra("lats");
        longs = i.getDoubleArrayExtra("longs");
        area_names = i.getStringArrayExtra("names");
        for(int j=0;j<lats.length;j++){
            map.addMarker(new MarkerOptions().position(new LatLng(lats[j], longs[j])).title(area_names[j]));
        }
        CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(lats[0],longs[0]));
        map.setMinZoomPreference(16);
        map.animateCamera(cu);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
}
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    mMap.setMyLocationEnabled(true);
                                    button_reached.setEnabled(false);
                                    double lat1 = location.getLatitude();
                                    double long1 = location.getLongitude();
                                    for(int p = 0;p<lats.length;p++){
                                        double k = distance(lat1, long1, lats[p],longs[p]);
                                        Log.d("k",Double.toString(k));


                                        if(k<5){
                                            button_reached.setEnabled(true);
                                            area_name = area_names[p];
                                            Log.d("reach",area_name);
                                        }


                                    }

//                                    Toast.makeText(getApplicationContext(),Double.toString(k),Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(),Double.toString(location.getLatitude())+ " " + Double.toString(location.getLongitude()),Toast.LENGTH_SHORT).show();
//                                    latTextView.setText(location.getLatitude()+"");
//                                    lonTextView.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        //mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Toast.makeText(getApplicationContext(),Double.toString(mLastLocation.getLatitude())+ " " + Double.toString(mLastLocation.getLongitude()),Toast.LENGTH_SHORT).show();

            button_reached.setEnabled(false);
            double lat1 = location.getLatitude();
            double long1 = location.getLongitude();
            for(p = 0;p<lats.length;p++){
                double k = distance(lat1, long1, lats[p],longs[p]);
                Log.d("area","2255");
                area_name = area_names[p];
                Log.d("k",Double.toString(k));
                if(k<5){
                    button_reached.setEnabled(true);
                    Log.d("aass",area_name);}

            }
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    //distance between two lats and longitudes
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515*1000;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getLastLocation();
            button_reached.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getResponse(area_name);
                }
            });

        } }

    private void getResponse (String area) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(NotifyResidents.base_url).addConverterFactory(GsonConverterFactory.create()).build();

        NotifyResidents api = retrofit.create(NotifyResidents.class);
        Call<List<NotifyResponse>> call = api.getResponse(area);

        call.enqueue(new Callback<List<NotifyResponse>>() {
            @Override
            public void onResponse(Call<List<NotifyResponse>> call, Response<List<NotifyResponse>> response) {
                List<NotifyResponse> areasList = response.body();


                }

            @Override
            public void onFailure(Call<List<NotifyResponse>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}
