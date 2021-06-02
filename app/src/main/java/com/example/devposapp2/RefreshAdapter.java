package com.example.devposapp2;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.devposapp2.ui.reservations.ReservationFragment;
import com.example.devposapp2.ui.reservations.ReservationsAdapter;
import com.example.devposapp2.ui.reservations.ReservationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class RefreshAdapter {
    Context context;
    View view;
    public RefreshAdapter(Context context, View view) {
        this.context = context;
        this.view = view;
    }


    RecyclerView recyclerView;
    ReservationsAdapter reservationsAdapter;
    List<ReservationsViewModel> reservations = new ArrayList<>();
    ReservationFragment reservationFragment;
    public void refresh(){


        reservationsAdapter.notifyDataSetChanged();


    }

}
