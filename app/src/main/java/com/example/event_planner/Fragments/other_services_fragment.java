package com.example.event_planner.Fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.R;

public class other_services_fragment extends Fragment {
    RecyclerView other_services_recyclerview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.themes_and_food_list_layout,container,false);
        other_services_recyclerview=v.findViewById(R.id.themes_and_food_list);
        other_services_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        Firebase_Operations.get_services_of_hotel(getActivity().getIntent().getStringExtra("hotel_id"),getActivity(),other_services_recyclerview,null,"Other Services Offering",null);
        return v;
    }

}
