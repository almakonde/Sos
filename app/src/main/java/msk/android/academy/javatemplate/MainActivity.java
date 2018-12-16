package msk.android.academy.javatemplate;

import android.Manifest;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
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

import msk.android.academy.javatemplate.presentation.useractions.models.ActionModel;
import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction;
import msk.android.academy.javatemplate.presentation.useractions.models.Contact;

public class MainActivity extends AppCompatActivity implements FetchADressTask.OnTaskComplite {

    public static final int REQUEST_LOCATION_PERMISSION = 123;
    private static final int LAYOUT = R.layout.activity_main;
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final int PERMISSION_SEND_SMS = 124;
    String phone;
    String message;
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
        mLocationCalback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                new FetchADressTask(MainActivity.this,
                        MainActivity.this)
                        .execute(locationResult.getLastLocation());
            }
        };
        if (!mTrackingLocation) {
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

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                            "Нет разрешения на испольщование Location",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSms(phone, message);
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    @Override
    public void onTaskComplite(String result) {
        stopTrackingLocation();
        mLocationTextView.setText(result);
    }

    public void sendSms(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(App.getInstance().getApplicationContext(), "SMS Sent!",
                Toast.LENGTH_LONG).show();
    }

    private void unbindUx() {
        phoneBlock.setOnClickListener(null);
    }

    private void setupUi() {
        findViews();
    }

    private void setupUx() {
        phoneBlock.setOnClickListener(v -> {
            Optional<AlarmAction> alarmAction = App.getInstance().getDatabase().getActive();

            if (alarmAction.isPresent()) {
                ActionModel act = alarmAction.get().getModel();
                if (act.getType() == ActionModel.Type.SMS) {
                    for (Contact contact : act.getContacts()) {
                        initPhoneandSms();
                        phone = contact.getPhoneNumber();
                        requestSmsPermission("+7" + phone, message);
                    }
                } else {
                    callPhone("+7" + (alarmAction.get().getModel().getContacts().get(
                            0).getPhoneNumber()));
                }
            }
        });
        infoView.setOnClickListener(v -> SettingsActivity.start(this));
    }

    private void callPhone(String phone) {
        final Intent intent = new Intent(Intent.ACTION_DIAL)
                .setData(Uri.parse(String.format("tel: %s", phone)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
        }
    }

    private void requestSmsPermission(String phone, String message) {
        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
            sendSms(phone, message);
        }
    }

    private void initPhoneandSms() {
//        phone = "+79998440758";
        message = "Я в опасносности по адресу " + mLocationTextView.getText();
    }

    private void startTrackingLocation() {
        mTrackingLocation = true;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
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

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if (location != null) {
                                            new FetchADressTask(MainActivity.this,
                                                    MainActivity.this).execute(location);
                                            mLocationTextView.setText(
                                                    getString(R.string.location_text,
                                                            location.getLatitude(),
                                                            location.getLongitude(),
                                                            location.getTime()));
                                        } else {
                                            mLocationTextView.setText(
                                                    "Нет последней известной позиции");
                                        }
                                    }
                                });
        }
    }

    private void findViews() {
        phoneBlock = findViewById(R.id.phone_block);
        addressText = findViewById(R.id.address_text);
        infoView = findViewById(R.id.infoView);
    }
}
