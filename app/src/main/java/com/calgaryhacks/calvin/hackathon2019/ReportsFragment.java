package com.calgaryhacks.calvin.hackathon2019;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReportsFragment extends Fragment {

    private static ReportsFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    public static ReportsFragment getInstance() {
        if (instance == null) {
            instance = new ReportsFragment();
        }
        return instance;
    }
}
