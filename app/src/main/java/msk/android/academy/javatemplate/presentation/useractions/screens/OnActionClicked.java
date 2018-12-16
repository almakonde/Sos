package msk.android.academy.javatemplate.presentation.useractions.screens;

import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction;
import msk.android.academy.javatemplate.presentation.useractions.models.ActionEditingMode;
public interface OnActionClicked {

    void onDetailsClicked(AlarmAction alarmAction, ActionEditingMode mode);
}
