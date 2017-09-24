package com.example.jibunnisa.alfaklassify;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jibunnisa.alfaklassify.Activity.LoginActivity;
import com.example.jibunnisa.alfaklassify.Activity.PostingProductActivity;
import com.example.jibunnisa.alfaklassify.Fragments.AllAdsFragment;
import com.example.jibunnisa.alfaklassify.Fragments.MyAccountFragment;
import com.example.jibunnisa.alfaklassify.customAdapter.ProductAdapter;
import com.example.jibunnisa.alfaklassify.model.PostingProduct;
import com.example.jibunnisa.alfaklassify.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;

    private RelativeLayout parentLayout;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    private User user;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AllAdsFragment allAdsFragment;
    private FragmentManager fragmentManager;
    private RelativeLayout relativeLayout;
    private ArrayList<User> users;
    private DatabaseReference dbRef;
    private Button login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Advertisements");


        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerLayout = navigationView.getHeaderView(0);
        login = (Button) headerLayout.findViewById(R.id.login_button_nav_head);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

         fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,PostingProductActivity.class).putExtra("from","fab");
                startActivity(i);
            }
        });


        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if(getIntent().getStringExtra("ab")!=null){
            String ab = getIntent().getStringExtra("ab");
            if(ab.equals("myaccount")){
                fab.hide();
                MyAccountFragment myAccountFragment= new MyAccountFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,myAccountFragment,"myaccountfragment");
                fragmentTransaction.commit();
            }
            else if(ab.equals("allads")){
                fab.show();
                allAdsFragment= new AllAdsFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,allAdsFragment,"alladsfragment");
                fragmentTransaction.commit();
            }

        }
        else {
            allAdsFragment= new AllAdsFragment();
            fragmentTransaction.add(R.id.fragment_container,allAdsFragment,"alladsfragment");
            fragmentTransaction.commit();
        }



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class).putExtra("from","header"));
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){

            login.setText("LOG IN");
            login.setEnabled(true);

        }
        else {
            final String uid=firebaseAuth.getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_USERS);
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    users = new ArrayList<User>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        user = snapshot.getValue(User.class);

                        if(user.getUserId().equals(uid)){
                            users.add(user);
                        }


                    }
                    login.setText(users.get(0).getFullName());
                    login.setEnabled(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.all_ads) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(fragment instanceof AllAdsFragment){

            }
            else {
                getSupportActionBar().setTitle("All Advertisements");
                allAdsFragment= new AllAdsFragment();
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,allAdsFragment,"alladsfragment");
                fragmentTransaction.commit();
                fab.show();
            }


        } else if (id == R.id.my_account) {

            fab.hide();

            getSupportActionBar().setTitle("My Account");
            MyAccountFragment myAccountFragment= new MyAccountFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,myAccountFragment,"myaccountfragment");
            fragmentTransaction.commit();

        } else if (id == R.id.settings) {
            getSupportActionBar().setTitle("Settings");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
