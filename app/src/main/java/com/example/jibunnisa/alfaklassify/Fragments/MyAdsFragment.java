package com.example.jibunnisa.alfaklassify.Fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jibunnisa.alfaklassify.Activity.ProductDetailsActivity;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.Utils;
import com.example.jibunnisa.alfaklassify.customAdapter.ProductAdapter;
import com.example.jibunnisa.alfaklassify.databinding.FragmentMyAccountBinding;
import com.example.jibunnisa.alfaklassify.databinding.FragmentMyAdsBinding;
import com.example.jibunnisa.alfaklassify.model.PostingProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAdsFragment extends Fragment {


    private FragmentMyAdsBinding fragmentMyAdsBinding;
    private ArrayList<PostingProduct> products;
    private ProductAdapter adapter;
    private RecyclerView productListRV;
    private DatabaseReference dbRef;
    private PostingProduct postingProduct;
    private String eventid;
    private String eventName;
    private FirebaseAuth firebaseAuth;

    public MyAdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMyAdsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_ads,container,false);
        View view = fragmentMyAdsBinding.getRoot();
        return  view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();
        final String userId  = firebaseAuth.getCurrentUser().getUid();
        fragmentMyAdsBinding.myproductrecview.setHasFixedSize(true);
        fragmentMyAdsBinding.myproductrecview.setLayoutManager(new LinearLayoutManager(getContext()));


        dbRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_PRODUCTS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products = new ArrayList<PostingProduct>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    postingProduct = snapshot.getValue(PostingProduct.class);

                    if(postingProduct.getUserId().equals(userId)){
                        products.add(postingProduct);
                    }
                  
                    adapter = new ProductAdapter(getContext(),products);
                    fragmentMyAdsBinding.myproductrecview.setAdapter(adapter);
                    adapter.setListener(new ProductAdapter.Listener() {
                        @Override
                        public void onClick(int position) {
                            postingProduct = products.get(position);
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
                                    .putExtra("from","myads")
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
