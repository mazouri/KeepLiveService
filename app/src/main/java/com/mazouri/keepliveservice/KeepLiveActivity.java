package com.mazouri.keepliveservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class KeepLiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeepLiveManager.getInstance().initKeepLiveActivity(this);
    }
}
