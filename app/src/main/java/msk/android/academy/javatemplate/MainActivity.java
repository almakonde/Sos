package msk.android.academy.javatemplate;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;
    private static final String TAG = MainActivity.class.getCanonicalName();

    private ImageView phoneBlock;
    private TextView addressText;
    private ImageView infoView;


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
}
