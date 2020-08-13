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

public class update_other_services_fragment extends Fragment {
    RecyclerView list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.themes_and_food_list_layout,container,false);
        list=v.findViewById(R.id.themes_and_food_list);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        Firebase_Operations.get_services_of_hotel(getActivity().getIntent().getStringExtra("hotel_id"),getActivity(),list,null,"Other Services Update",this);
        return v;
    }
}
