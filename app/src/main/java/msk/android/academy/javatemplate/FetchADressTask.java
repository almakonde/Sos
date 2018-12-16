package msk.android.academy.javatemplate;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FetchADressTask extends AsyncTask<Location, Void, String> {

    private Context mContext;
    private OnTaskComplite listener;

    public FetchADressTask(Context mContext, OnTaskComplite listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Location... params) {

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        Location location = params[0];
        List<Address> addresses = null;
        String resultMessage = "";

        try{
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(),
                    1);

            if (addresses == null || addresses.size() == 0){
                resultMessage = "Мы себя не нашли";
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressPart = new ArrayList<>();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                    addressPart.add(address.getAddressLine(i));
                }

                resultMessage = TextUtils.join("\n", addressPart);

            }
        } catch (IOException ioException){
            resultMessage = "Сервис не досутпен";
        }

        return resultMessage;
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onTaskComplite(s);
        super.onPostExecute(s);
    }

    interface  OnTaskComplite {
        void onTaskComplite(String result);
    }
}
