package msk.android.academy.javatemplate.documents;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import msk.android.academy.javatemplate.R;


public class DocumentFragment extends Fragment implements DocumentRecyclerAdapter.OnItemClickListener{
    private DocumentCameraDialog documentCameraDialog;
    List<DocumentData> documentDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.document_fragment, container, false);
        //imageView = view.findViewById(R.id.imageView);
        documentCameraDialog = new DocumentCameraDialog();
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentCameraDialog.show(getChildFragmentManager(), "dialog");
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.document_recycler_view);
        recyclerView.setAdapter(new DocumentRecyclerAdapter(getContext(),
                documentDataList, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }


    public void setImage(Bitmap image, String file) {
        //imageView.setImageBitmap(createRotatedBitmap(image, file));
        documentDataList.add(new DocumentData(documentCameraDialog.getInputText(), image, file));
    }

    public static Bitmap createRotatedBitmap(Bitmap realImage, String path) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
            realImage = rotate(realImage, 90);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
            realImage = rotate(realImage, 270);
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
            realImage = rotate(realImage, 180);
        }
        return realImage;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    @Override
    public void onItemClick(String path) {
        DocumentFullPhotoActivity.start(getActivity(), path);
    }
}
