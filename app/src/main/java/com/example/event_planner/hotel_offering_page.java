package com.example.event_planner;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.event_planner.Fragments.Halls_fragment;
import com.example.event_planner.Fragments.foods_fragment;
import com.example.event_planner.Fragments.other_services_fragment;
import com.example.event_planner.Fragments.themes_fragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class hotel_offering_page extends AppCompatActivity {
    ImageView collaspingtoolbarimage;
    CollapsingToolbarLayout ctbl;
    ViewPager vp;
    TabLayout tb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_offering_page);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctbl= findViewById(R.id.collapse_toolbar);
        ctbl.setTitle("Themes");
        collaspingtoolbarimage= findViewById(R.id.bgheader);
        Glide.with(this).load(getIntent().getStringExtra("image_url")).into(collaspingtoolbarimage);
        vp= findViewById(R.id.view);
        tb= findViewById(R.id.tabs);
        setviewpager();
        tb.setupWithViewPager(vp);
        tb.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    ctbl.setTitle("Themes");
                }else if(tab.getPosition()==1){
                    ctbl.setTitle("Food");
                }else if(tab.getPosition()==2){
                    ctbl.setTitle("Other Services");
                }else if(tab.getPosition()==3){
                    ctbl.setTitle("Halls");
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    ctbl.setTitle("Themes");
                }else if(tab.getPosition()==1){
                    ctbl.setTitle("Food");
                }else if(tab.getPosition()==2){
                    ctbl.setTitle("Other Services");
                }else if(tab.getPosition()==3){
                    ctbl.setTitle("Halls");
                }
            }
        });
    }
    private void setviewpager(){
        viewpageradapter vpa=new viewpageradapter(getSupportFragmentManager());
        vpa.addfragment(new themes_fragment(),"Themes");
        vpa.addfragment(new foods_fragment(),"Food");
        vpa.addfragment(new other_services_fragment(),"Other Services");
        vpa.addfragment(new Halls_fragment(),"Halls");
        vp.setAdapter(vpa);
        vp.setOffscreenPageLimit(4);
    }
    class viewpageradapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> listofFragment;
        ArrayList<String>fragmenttitles;

        public viewpageradapter(FragmentManager fm) {
            super(fm);
            listofFragment=new ArrayList<Fragment>();
            fragmenttitles=new ArrayList<String>();
        }
        @Override
        public Fragment getItem(int position) {
            return listofFragment.get(position);
        }

        @Override
        public int getCount() {
            return listofFragment.size();
        }
        public void addfragment(Fragment f,String name){
            listofFragment.add(f);
            fragmenttitles.add(name);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmenttitles.get(position);
        }
    }
}
