package msk.android.academy.javatemplate.presentation.useractions.screens;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msk.android.academy.javatemplate.App;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.Utils;
import msk.android.academy.javatemplate.data.Repository;
import msk.android.academy.javatemplate.presentation.useractions.adapter.ActionsAdapter;
import msk.android.academy.javatemplate.presentation.useractions.models.ActionEditingMode;
import msk.android.academy.javatemplate.presentation.useractions.models.ActionModel;
import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction;
import msk.android.academy.javatemplate.presentation.useractions.models.Contact;

public class UserActionsFragment extends Fragment {

    private final Repository actionsRepository = App.getInstance().getDatabase();
    private RecyclerView rvActionsList;
    private Toolbar toolbar;
    private FloatingActionButton fabNewAction;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_user_actions_list, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initAdapter();
        Single.fromCallable(() -> actionsRepository.getActions())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(a -> getAdapter().setData(a));

        Single.fromCallable(() -> actionsRepository.getActive())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(a -> {
                  if (a.isPresent()) {
                      getAdapter().setSelectedItem(a.get().getId());
                  }
              });
//        generateData();
//        Single.zip(actionsObs, activeActionObs, (actions, active) -> {
//            getAdapter().setData(actions);
//            getAdapter().setSelectedItem(active.getId());
//            return actions;
//        }).subscribe();
    }

    private void generateData() {
        List<AlarmAction> alarmActions = new ArrayList<>();
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(Utils.getRandom(), "Мама", " 911"));
        contacts.add(new Contact(Utils.getRandom(), "Папа", " 911"));
        alarmActions.add(new AlarmAction(Utils.getRandom(), "Привет",
                new ActionModel(Utils.getRandom(), ActionModel.Type.SMS,
                        contacts)));
        alarmActions.add(new AlarmAction(Utils.getRandom(), "Пока",
                new ActionModel(Utils.getRandom(), ActionModel.Type.PHONE_CALLING,
                        contacts)));
        getAdapter().setData(alarmActions);
    }

    private void initViews(View view) {
        rvActionsList = view.findViewById(R.id.rvActionsList);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Действия");
        fabNewAction = view.findViewById(R.id.fabNewAction);
        fabNewAction.setOnClickListener(v -> openAction(null, ActionEditingMode.NEW));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void initAdapter() {
        ActionsAdapter actionsAdapter = new ActionsAdapter(
                (action, mode) -> {
                    openAction(action, mode);
                    return null;
                }, (alarmAction, actionEditingMode) -> {
            actionsRepository.setAsActive(alarmAction);
            return null;
        }, (alarmAction -> {
            Completable.fromAction(() -> actionsRepository.deleteAction(alarmAction.getId()))
                       .andThen(Single.fromCallable(() -> actionsRepository.getActions()))
                       .subscribe((a) -> getAdapter().setData(a));
            actionsRepository.deleteAction(alarmAction.getId());
            return null;
        }));
        rvActionsList.setAdapter(actionsAdapter);
        LinearLayoutManager lm =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvActionsList.setLayoutManager(lm);
        rvActionsList.addItemDecoration(
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

    }

    private void openAction(AlarmAction alarmAction, ActionEditingMode mode) {
        ((OnActionClicked) getActivity()).onDetailsClicked(alarmAction, mode);
    }

    private ActionsAdapter getAdapter() {
        return (ActionsAdapter) rvActionsList.getAdapter();
    }
}
