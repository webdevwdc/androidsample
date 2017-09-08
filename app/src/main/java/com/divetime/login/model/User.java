package com.divetime.login.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.divetime.utils.TextWatcherAdapter;


/**
 * Created by android on 30/5/17.
 */
public class User extends BaseObservable {

    //cnfPasswordWatcher
    public final ObservableField<String> email =  new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");
    public final ObservableField<String> firstName =  new ObservableField<>("");
    public final ObservableField<String> lastName = new ObservableField<>("");
    public final ObservableField<String> cnfPassword = new ObservableField<>("");

    public TextWatcherAdapter userNameWatcher = new TextWatcherAdapter(email);
    public TextWatcherAdapter passwordWatcher = new TextWatcherAdapter(password);

    public TextWatcherAdapter firstNameWatcher = new TextWatcherAdapter(firstName);
    public TextWatcherAdapter lastNameWatcher = new TextWatcherAdapter(lastName);
    public TextWatcherAdapter cnfPasswordWatcher = new TextWatcherAdapter(cnfPassword);

}
