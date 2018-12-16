package msk.android.academy.javatemplate.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction;
import msk.android.academy.javatemplate.presentation.useractions.models.Contact;

//@Database(entities = {AlarmAction.class, Contact.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ActionsRepository actionsDao();

    public abstract ContactRepository carDao();
}
