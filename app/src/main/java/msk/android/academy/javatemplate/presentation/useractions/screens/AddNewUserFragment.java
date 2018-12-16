package msk.android.academy.javatemplate.presentation.useractions.screens;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import msk.android.academy.javatemplate.R;
import msk.android.academy.javatemplate.Utils;
import msk.android.academy.javatemplate.presentation.useractions.models.AddNewActionSharedState;
import msk.android.academy.javatemplate.presentation.useractions.models.Contact;

public class AddNewUserFragment extends Fragment {

    private EditText etContactName;
    private EditText etContactTelephone;
    private Button btnSaveContact;
    private Toolbar toolbar;
    private MenuItem confirmItem;

    private AddNewActionSharedState sharedState;

    public static AddNewUserFragment newInstance(AddNewActionSharedState sharedState) {
        Bundle args = new Bundle();
        AddNewUserFragment fragment = new AddNewUserFragment();
        fragment.setArguments(args);
        fragment.sharedState = sharedState;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        etContactName = view.findViewById(R.id.etContactName);
        etContactTelephone = view.findViewById(R.id.etContactTelephone);
//        btnSaveContact = view.findViewById(R.id.btnSaveContact);

//        btnSaveContact.setOnClickListener(v -> {
//            Contact contact = new Contact(Utils.getRandom(), etContactName.getText().toString(),
//                    etContactTelephone.getText().toString());
//            sharedState.addContact(contact);
//            getActivity().onBackPressed();
//        });

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_action_details);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        confirmItem = toolbar.getMenu().findItem(R.id.confirm_edit_check);
        confirmItem.setOnMenuItemClickListener(item -> {
            Contact contact = new Contact(Utils.getRandom(), etContactName.getText().toString(),
                    etContactTelephone.getText().toString());
            sharedState.addContact(contact);
            getActivity().onBackPressed();
            return true;
        });
    }
}
