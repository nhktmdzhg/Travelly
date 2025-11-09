package com.nhk.travelly;

public class DataHolder {
    private static DataHolder instance = null;
    public UserInfo currentUser = null;

    private DataHolder() {
    }

    public static synchronized DataHolder getInstance() {
        if (instance == null)
            instance = new DataHolder();

        return instance;
    }
}
