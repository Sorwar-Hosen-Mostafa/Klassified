package com.example.jibunnisa.alfaklassify.Fragments;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jibunnisa.alfaklassify.Activity.ProductDetailsActivity;
import com.example.jibunnisa.alfaklassify.MainActivity;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.Utils;
import com.example.jibunnisa.alfaklassify.customAdapter.ProductAdapter;
import com.example.jibunnisa.alfaklassify.databinding.FragmentAllAdsBinding;
import com.example.jibunnisa.alfaklassify.model.PostingProduct;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllAdsFragment extends Fragment {


    private RelativeLayout parentLayout;
    private RecyclerView recyclerView;
    private ArrayList<PostingProduct> products;
    private ProductAdapter adapter;
    private DatabaseReference dbRef;
    private PostingProduct postingProduct;
    String eventid;
    String eventName;
    private FragmentAllAdsBinding fragmentAllAdsBinding;

    public AllAdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentAllAdsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_ads,container,false);
        View view = fragmentAllAdsBinding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentAllAdsBinding.itemrecview.setHasFixedSize(true);
        fragmentAllAdsBinding.itemrecview.setLayoutManager(new LinearLayoutManager(getContext()));

        parentLayout = (RelativeLayout) getActivity().findViewById(R.id.parentlayout);


        action();




    }

    private void action() {
        if(checkConectivity()){

            load();
        }
        else {

            Snackbar.make(parentLayout,"No Internet Connection",Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            load();
                            action();
                        }
                    }).show();

        }

    }

    public boolean checkConectivity() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else{
            connected = false;
        }


        return connected;
    }

    public void load(){
        dbRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_PRODUCTS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products = new ArrayList<PostingProduct>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    postingProduct = snapshot.getValue(PostingProduct.class);
                    products.add(postingProduct);
                   /* if(postingProduct.getEventId().equals(eventid)) {
                        products.add(postingProduct);
                    };*/
                    adapter = new ProductAdapter(getContext(),products);
                    fragmentAllAdsBinding.itemrecview.setAdapter(adapter);
                    adapter.setListener(new ProductAdapter.Listener() {
                        @Override
                        public void onClick(int position) {
                            postingProduct = products.get(position);
                            startActivity(new Intent(getContext(), ProductDetailsActivity.class)
                                    .putExtra("price", postingProduct.getPrice())
                                    .putExtra("brand", postingProduct.getBrandName())
                                    .putExtra("category", postingProduct.getCategoryName())
                                    .putExtra("condition", postingProduct.getCondition())
                                    .putExtra("date", postingProduct.getDate())
                                    .putExtra("time", postingProduct.getTime())
                                    .putExtra("description", postingProduct.getDescription())
                                    .putExtra("url", postingProduct.getImageUrl())
                                    .putExtra("location", postingProduct.getLocationName())
                                    .putExtra("model", postingProduct.getModelName())
                                    .putExtra("type", postingProduct.getTypeName())
                                    .putExtra("user",postingProduct.getUserId())
                                    .putExtra("from","allads")
                            );
                            //showUpdateDeleteDialog();
                            /*Intent momentIntent = new Intent(getApplicationContext(),AddMomentActivity.class);
                            momentIntent.putExtra("moment",moment);
                            startActivity(momentIntent);*/
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
