package msk.android.academy.javatemplate.documents;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

import msk.android.academy.javatemplate.R;


public class DocumentFullPhotoActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_full_photo_fragment);
        imageView = findViewById(R.id.document_full_image_view);
        String path = getIntent().getStringExtra("qwer");
        File imgFile = new File(path);

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(DocumentFragment.createRotatedBitmap(myBitmap, path));
        }
    }


    public static void start(Activity activity, String path) {
        final Intent intent = new Intent(activity, DocumentFullPhotoActivity.class);
        intent.putExtra("qwer", path);
        activity.startActivity(intent);
    }
}
