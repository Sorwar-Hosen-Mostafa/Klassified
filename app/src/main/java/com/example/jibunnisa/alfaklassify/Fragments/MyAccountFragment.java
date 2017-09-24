package com.example.jibunnisa.alfaklassify.Fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.jibunnisa.alfaklassify.Activity.LoginActivity;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.databinding.FragmentMyAccountBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {


    private FirebaseAuth firebaseAuth;
    private MyProfileFragment myProfileFragment;
    private MyAdsFragment myAdsFragment;
    private Favourites favourites;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    FragmentMyAccountBinding fragmentMyAccountBinding;

    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMyAccountBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false);
        View view = fragmentMyAccountBinding.getRoot();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            startActivity(new Intent(getContext(),LoginActivity.class).putExtra("from","myaccount"));
            getActivity().finish();

        }
        else {
            loadfragment();
        }



        fragmentMyAccountBinding.bottomNevigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.account:
                        myProfileFragment = new MyProfileFragment();
                        fragmentManager = getChildFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.child_fragment_container, myProfileFragment, "myprofilefragment");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.myads:
                        myAdsFragment = new MyAdsFragment();
                        fragmentManager = getChildFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.child_fragment_container, myAdsFragment, "myadsfragment");
                        fragmentTransaction.commit();

                        return true;
                    case R.id.favourits:
                        favourites = new Favourites();
                        fragmentManager = getChildFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.child_fragment_container, favourites, "myfavouritsfragment");
                        fragmentTransaction.commit();
                        return true;

                }
                return false;
            }
        });
    }

    private void loadfragment() {

        myProfileFragment = new MyProfileFragment();
        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.child_fragment_container, myProfileFragment, "myprofilefragment");
        fragmentTransaction.commit();

    }


}
