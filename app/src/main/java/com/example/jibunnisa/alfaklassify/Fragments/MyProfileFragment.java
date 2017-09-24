package com.example.jibunnisa.alfaklassify.Fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jibunnisa.alfaklassify.Activity.ProfileUpdateActivity;
import com.example.jibunnisa.alfaklassify.MainActivity;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.Utils;
import com.example.jibunnisa.alfaklassify.customAdapter.ProductAdapter;
import com.example.jibunnisa.alfaklassify.databinding.FragmentMyProfileBinding;
import com.example.jibunnisa.alfaklassify.model.PostingProduct;
import com.example.jibunnisa.alfaklassify.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {


    private User updateUser;
    private DatabaseReference dbRef;
    private PostingProduct postingProduct;
    private User user;
    private ArrayList<User> users;
    private FirebaseAuth firebaseAuth;
    private String eventid;
    private String eventName;
    private FragmentMyProfileBinding fragmentMyProfileBinding;
    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMyProfileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_profile,container,false);
        View view= fragmentMyProfileBinding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();
        final String userId  = firebaseAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_USERS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<User>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    user = snapshot.getValue(User.class);
                    if(user.getUserId().equals(userId)){
                        users.add(user);
                        updateUser=user;
                    }



                }
                fragmentMyProfileBinding.nameTV.setText(users.get(0).getFullName().toString().toUpperCase());
                fragmentMyProfileBinding.addressTV.setText(users.get(0).getAddress());
                fragmentMyProfileBinding.phoneTV.setText(users.get(0).getContactNo());
                fragmentMyProfileBinding.emailTV.setText(users.get(0).getEmail());
                if(!users.get(0).getImageUrl().equals(null)){
                   // Picasso.with(getContext()).load(users.get(0).getImageUrl()).into(fragmentMyProfileBinding.profilePicture);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fragmentMyProfileBinding.signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });
        fragmentMyProfileBinding.updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //firebaseAuth.signOut();
                Intent intent=new Intent(getContext(), ProfileUpdateActivity.class);
                intent.putExtra("userObject",updateUser);
                startActivity(intent);

            }
        });

    }
}
