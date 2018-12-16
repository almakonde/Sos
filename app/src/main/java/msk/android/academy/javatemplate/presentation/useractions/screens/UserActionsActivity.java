package msk.android.academy.javatemplate.presentation.useractions.screens;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction;
import msk.android.academy.javatemplate.presentation.useractions.models.ActionEditingMode;
import msk.android.academy.javatemplate.presentation.useractions.models.AddNewActionSharedState;

public class UserActionsActivity extends AppCompatActivity
        implements OnActionClicked, OnAddContactListener {

    private static final String TAG = UserActionsActivity.class.getSimpleName();
    @IdRes private int container = R.id.fragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        getSupportFragmentManager().beginTransaction()
                                   .add(container, new UserActionsFragment())
                                   .commit();
    }

    @Override
    public void onDetailsClicked(@Nullable AlarmAction alarmAction, ActionEditingMode mode) {
        Log.d(TAG, "onDetailsClicked: ");
        Long id;
        if (alarmAction != null) {
            id = alarmAction.getId();
        } else {
            id = null;
        }
        getSupportFragmentManager().beginTransaction()
                                   .replace(container,
                                           UserActionDetailsFragment.newInstance(id, mode))
                                   .addToBackStack(null)
                                   .commit();
    }

    @Override
    public void onAddContactClicked(AddNewActionSharedState sharedState) {
        getSupportFragmentManager().beginTransaction()
                                   .replace(container,
                                           AddNewUserFragment.newInstance(sharedState))
                                   .addToBackStack(null)
                                   .commit();
    }
}
