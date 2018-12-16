package msk.android.academy.javatemplate;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity  implements FetchADressTask.OnTaskComplite {

    private Button mLocationButton;
    private TextView mLocationTextView;
    private ImageView mAndroidImageView;
    private FusedLocationProviderClient mFusedLocationClient;
    private AnimatorSet mRotateAnim;
    private boolean mTrackingLocation = false;
    private LocationCallback mLocationCalback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mRotateAnim = (AnimatorSet) AnimatorInflater.
                loadAnimator(this, R.animator.rotate) ;

        mLocationButton = (Button) findViewById(R.id.button_location);
        mLocationTextView = (TextView) findViewById(R.id.textview_location);
        mAndroidImageView = (ImageView) findViewById(R.id.imageview_android);

        mRotateAnim.setTarget(mAndroidImageView);

        mLocationCalback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                new FetchADressTask(MainActivity.this,
                        MainActivity.this)
                        .execute(locationResult.getLastLocation());
            }
        };

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTrackingLocation){
                    startTrackingLocation();
                } else {
                    stopTrackingLocation();
                }
            }
        });

    }

    private void startTrackingLocation() {
        mRotateAnim.start();
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
        mRotateAnim.cancel();
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
