package msk.android.academy.javatemplate.presentation.useractions.models;

import android.arch.persistence.room.TypeConverter;

public class ActionTypeConverter {

    @TypeConverter
    public static ActionModel.Type toStatus(int status) {
        if (status == ActionModel.Type.SMS.getCode()) {
            return ActionModel.Type.SMS;
        } else if (status == ActionModel.Type.PHONE_CALLING.getCode()) {
            return ActionModel.Type.PHONE_CALLING;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static Integer toInteger(ActionModel.Type status) {
        return status.getCode();
    }
}
