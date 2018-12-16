package msk.android.academy.javatemplate;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements FetchADressTask.OnTaskComplite {

    private static final int LAYOUT = R.layout.activity_main;
    private static final String TAG = MainActivity.class.getCanonicalName();

    private ImageView phoneBlock;
    private TextView addressText;
    private ImageView infoView;

    private TextView mLocationTextView;
    private FusedLocationProviderClient mFusedLocationClient;
    private AnimatorSet mRotateAnim;
    private boolean mTrackingLocation = false;
    private LocationCallback mLocationCalback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        MainFragment messageFragment = new MainFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_frame, messageFragment)
                .commit();

        setupUi();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationTextView = (TextView) findViewById(R.id.address_text);
        mLocationCalback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                new FetchADressTask(MainActivity.this,
                        MainActivity.this)
                        .execute(locationResult.getLastLocation());
            }
        };
        if (!mTrackingLocation){
            startTrackingLocation();
        } else {
            stopTrackingLocation();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupUx();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindUx();
    }

    private void unbindUx() {
        phoneBlock.setOnClickListener(null);
    }


    private void setupUi() {
        findViews();
    }

    private void setupUx() {
        phoneBlock.setOnClickListener(v -> callPhone("103"));
        infoView.setOnClickListener(v -> SettingsActivity.start(this));
    }

    private void callPhone(String phone){
        final Intent intent = new Intent(Intent.ACTION_DIAL)
                .setData(Uri.parse(String.format("tel: %s", phone)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
        }
    }

    private void findViews() {
        phoneBlock = findViewById(R.id.phone_block);
        addressText = findViewById(R.id.address_text);
        infoView = findViewById(R.id.infoView);
    }


    private void startTrackingLocation() {
//        mRotateAnim.start();
        mTrackingLocation = true;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(),
                    mLocationCalback,
                    null);
        }
    }
    private void stopTrackingLocation() {
        mTrackingLocation = false;
        mFusedLocationClient.removeLocationUpdates(mLocationCalback);
    }
    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    public static final int REQUEST_LOCATION_PERMISSION = 123;

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                new FetchADressTask(MainActivity.this,
                                        MainActivity.this).execute(location);
                                mLocationTextView.setText(getString(R.string.location_text,
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        location.getTime()));
                            } else {
                                mLocationTextView.setText("Нет последней известной позиции");
                            }
                        }
                    });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                } else {
                    Toast.makeText(this,
                            "Нет разрешения на испольщование Location",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    @Override
    public void onTaskComplite(String result) {
        stopTrackingLocation();
        mLocationTextView.setText(result);
    }
}
