package com.example.portable.firebasetests.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DayNumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayNumberFragment extends Fragment {

    private static final String DAY_OF_YEAR = "day";


    private int dayOfYear;


    public DayNumberFragment() {
        // Required empty public constructor
    }


    public static DayNumberFragment newInstance(int dayOfYear) {
        DayNumberFragment fragment = new DayNumberFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_OF_YEAR, dayOfYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayOfYear = getArguments().getInt(DAY_OF_YEAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_number, container, false);
        TextView dayOfWeek = (TextView) v.findViewById(R.id.day_of_week);
        TextView dayOfMonth = (TextView) v.findViewById(R.id.day_of_month);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        dayOfWeek.setText(getDayOfWeekString());
        dayOfMonth.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
        return v;
    }

    private String getDayOfWeekString() {
        return "STR";
    }
}
