package com.debugcc.myliferpg.Activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.debugcc.myliferpg.R;
import com.debugcc.myliferpg.databinding.ActivityNewMissionBinding;

public class NewMissionActivity extends AppCompatActivity {

    private ActivityNewMissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_mission);
    }
}
