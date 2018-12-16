package msk.android.academy.javatemplate.presentation.useractions.screens;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import msk.android.academy.javatemplate.App;
import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.data.Repository;
import msk.android.academy.javatemplate.presentation.useractions.adapter.ContactAdapter;
import msk.android.academy.javatemplate.presentation.useractions.models.ActionEditingMode;
import msk.android.academy.javatemplate.presentation.useractions.models.ActionModel;
import msk.android.academy.javatemplate.presentation.useractions.models.AddNewActionSharedState;
import msk.android.academy.javatemplate.presentation.useractions.models.AlarmAction;
import msk.android.academy.javatemplate.presentation.useractions.models.Contact;

public class UserActionDetailsFragment extends Fragment implements BackButtonListener {

    private final static String ACTION_KEY = "alarmAction";
    private final static String MODE_KEY = "mode";
    private static final String TAG = UserActionDetailsFragment.class.getSimpleName();
    private final Repository actionsRepository = App.getInstance().getDatabase();
    private AddNewActionSharedState addNewActionSharedState;
    private @Nullable AlarmAction alarmAction;
    private @Nullable Long actionId = null;
    private ActionEditingMode mode;
    private Spinner spinnerActionTypes;
    private Toolbar toolbar;
    private RecyclerView rvContacts;
    private EditText etActionName;
    private MenuItem confirmItem;
    private TextView tvAddNewContact;

    private int spinnerPosition = -1;

    public static UserActionDetailsFragment newInstance(
            @Nullable Long actionId,
            ActionEditingMode mode
    ) {
        Bundle args = new Bundle();
        if (actionId != null) {
            args.putLong(ACTION_KEY, actionId);
        }
        args.putSerializable(MODE_KEY, mode);
        UserActionDetailsFragment fragment = new UserActionDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionId = getArguments().getLong(ACTION_KEY);
        mode = (ActionEditingMode) getArguments().getSerializable(MODE_KEY);
        addNewActionSharedState = new AddNewActionSharedState(actionId);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_action_details, container, false);
        initView(view);
        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        switch (mode) {
            case NEW:
                toolbar.setTitle("Новая запись");

                break;
            case EDITING:
                toolbar.setTitle("Редактирование");
        }

        if (actionId != null) {
            Single.fromCallable(() -> actionsRepository.getActionById(actionId))
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(a -> {
                      int position = 0;
                      if (a.isPresent()) {
                          alarmAction = a.get();
                          ActionModel.Type type = null;
                          if (alarmAction != null) {
                              type = alarmAction.getModel().getType();
                          }
                          if (type == ActionModel.Type.SMS) {
                              position = 0;
                          } else {
                              position = 1;
                          }
//                          if (a.get().getModel().getContacts().isEmpty()) {
                          getAdapter().setData(addNewActionSharedState.getContacts());
//                          } else {
//                              getAdapter().setData(a.get().getModel().getContacts());
//                          }
                          etActionName.setText(a.get().getName());
                      } else {
                          getAdapter().setData(addNewActionSharedState.getContacts());
                      }
                      if (spinnerPosition == -1) {
                          spinnerActionTypes.setSelection(position);
                      } else {
                          spinnerActionTypes.setSelection(spinnerPosition);
                          spinnerPosition = -1;
                      }
                  });
        }
    }

    @Override
    public boolean onBackPressed() {
        AlarmAction newAction = createAction();
        if (newAction.equals(alarmAction) && !newAction.getName().isEmpty()) {
            getActivity().onBackPressed();
        } else {
            showExitDialog();
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        spinnerPosition = spinnerActionTypes.getSelectedItemPosition();
    }

    @Override
    public void onStart() {
        super.onStart();
//        spinnerActionTypes.setSelection(spinnerPosition);
    }

    private AlarmAction createAction() {
        long id = 0;
        switch (mode) {
            case NEW:
                id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
                break;
            case EDITING:
                id = alarmAction.getId();
                break;
        }

        ActionModel newType = null;
        List<Contact> contacts = getAdapter().getData();
        switch (spinnerActionTypes.getSelectedItemPosition()) {
            case 0:
                newType = new ActionModel(id, ActionModel.Type.SMS, contacts);
                break;
            case 1:
                newType = new ActionModel(id, ActionModel.Type.PHONE_CALLING, contacts);
                break;
        }
        Log.d(TAG, "createAction: newType=" + newType);
        String text;
        if (etActionName.getText() == null) {
            text = "";
        } else {
            text = etActionName.getText().toString();
        }
        return new AlarmAction(
                id,
                text,
                newType
        );
    }

    private void showExitDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage("Выйти без сохранения изменений?")
                .setPositiveButton("Ok", (dialog, which) -> {
                    getActivity().onBackPressed();
                })
                .setNegativeButton("Нет", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void initView(View view) {
        spinnerActionTypes = view.findViewById(R.id.spinnerActionTypes);
        ArrayAdapter<CharSequence> stringArrayAdapter =
                ArrayAdapter.createFromResource(getContext(), R.array.action_types,
                        android.R.layout.simple_spinner_item);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActionTypes.setAdapter(stringArrayAdapter);
        spinnerActionTypes.setSelection(0);
        spinnerActionTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == ActionModel.Type.PHONE_CALLING.getCode()) {
                    if (!getAdapter().getData().isEmpty())
                        getAdapter().setData(getAdapter().getData().subList(0, 1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_action_details);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        confirmItem = toolbar.getMenu().findItem(R.id.confirm_edit_check);
        switch (mode) {
            case NEW:
                confirmItem.setOnMenuItemClickListener(menuItem -> {
                    //сохраняем новый объект
                    if (createAction().getName().isEmpty()) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Вы не ввели имя. Продолжить ввод?")
                                .setPositiveButton("Да", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .setNegativeButton("Нет",
                                        (dialog, which) -> getActivity().onBackPressed())
                                .create()
                                .show();
                    } else {
                        Completable.fromCallable(
                                () -> actionsRepository.updateAction(createAction()))
                                   .subscribeOn(Schedulers.io())
                                   .observeOn(AndroidSchedulers.mainThread())
                                   .subscribe(() -> {
                                       getActivity().onBackPressed();
                                   });
                    }
                    return true;
                });
                break;
            case EDITING:
                confirmItem.setOnMenuItemClickListener(menuItem -> {
                    //обновляем объект
                    if (createAction().getName().isEmpty()) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Вы не ввели имя. Отменить изменения?")
                                .setPositiveButton("Да", (dialog, which) -> {
                                    getActivity().onBackPressed();
                                })
                                .setNegativeButton("Нет",
                                        (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    } else {
                        Completable.fromCallable(
                                () -> actionsRepository.updateAction(createAction()))
                                   .subscribeOn(Schedulers.io())
                                   .observeOn(AndroidSchedulers.mainThread())
                                   .subscribe(() -> getActivity().onBackPressed());
                    }
                    return true;
                });
                break;
        }

        tvAddNewContact = view.findViewById(R.id.tvAddContact);
        tvAddNewContact.setOnClickListener(v -> {
            ((OnAddContactListener) getActivity()).onAddContactClicked(addNewActionSharedState);
        });

        rvContacts = view.findViewById(R.id.rvContacts);
        ContactAdapter contactAdapter = new ContactAdapter(contact -> {
            onDeleteContact(contact);
            return null;
        });
        rvContacts.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvContacts.setAdapter(contactAdapter);

        etActionName = view.findViewById(R.id.etActionName);
    }

    private void onDeleteContact(Contact contact) {
        Completable.fromAction(() -> addNewActionSharedState.deleteContact(contact))
                   .andThen(Single.fromCallable(() -> addNewActionSharedState.getContacts()))
                   .subscribe(c -> {
                       getAdapter().setData(c);
                   });
    }

    private ContactAdapter getAdapter() {
        return (ContactAdapter) rvContacts.getAdapter();
    }
}
