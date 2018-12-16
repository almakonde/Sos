package msk.android.academy.javatemplate;

import android.app.Application;

import msk.android.academy.javatemplate.data.Repository;

public class App extends Application {

    public static App instance;

    private Repository database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = new Repository(getApplicationContext());
//        database = Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "database").build();
    }

    public static App getInstance() {
        return instance;
    }

    public Repository getDatabase() {
        return database;
    }
}
