package msk.android.academy.javatemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import msk.android.academy.javatemplate.documents.DocumentMainActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.activity_settings;

    private TextView actionsItem;
    private TextView documentsItem;

    public static void start(Activity activity){
        final Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

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

    private void setupUi() {
        setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
    }

    private void setupUx() {
        actionsItem.setOnClickListener(v -> {});
        documentsItem.setOnClickListener(v -> DocumentMainActivity.start(this));
    }

    private void unbindUx() {
        actionsItem.setOnClickListener(null);
        documentsItem.setOnClickListener(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void findViews() {
        actionsItem = findViewById(R.id.actions_item);
        documentsItem = findViewById(R.id.documents_item);
    }
}
