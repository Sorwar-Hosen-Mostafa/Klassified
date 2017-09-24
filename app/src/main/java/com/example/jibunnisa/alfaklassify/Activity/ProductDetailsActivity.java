package com.example.jibunnisa.alfaklassify.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jibunnisa.alfaklassify.Fragments.Favourites;
import com.example.jibunnisa.alfaklassify.Fragments.MyAdsFragment;
import com.example.jibunnisa.alfaklassify.Fragments.MyProfileFragment;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.Utils;
import com.example.jibunnisa.alfaklassify.customAdapter.ProductAdapter;
import com.example.jibunnisa.alfaklassify.databinding.ActivityProductDetailsBinding;
import com.example.jibunnisa.alfaklassify.model.PostingProduct;
import com.example.jibunnisa.alfaklassify.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailsActivity extends AppCompatActivity {

    private DatabaseReference dbRef;

    private ArrayList<User> users;
    private User user;
    private String poster_name;
    private String poster_phone;
    private String poster_email;
    private String title;
    private Toolbar toolbar;
    ActivityProductDetailsBinding activityProductDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityProductDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent i = getIntent();
        Double Price = i.getDoubleExtra("price",0);
        String Category = i.getStringExtra("category");
        String Condition = i.getStringExtra("condition");
        String Brand = i.getStringExtra("brand");
        String Date = i.getStringExtra("date");
        String Time = i.getStringExtra("time");
        String Description = i.getStringExtra("description");
        final String Url = i.getStringExtra("url");
        String Location = i.getStringExtra("location");
        String Model = i.getStringExtra("model");
        String Type = i.getStringExtra("type");
        final String User = i.getStringExtra("user");
        title = Brand + " " + Model + " " + Condition;


        activityProductDetailsBinding.ProductTitleTV.setText(title);
        activityProductDetailsBinding.ProductPriceTV.setText("TK: " + Price);
        activityProductDetailsBinding.ProductBrandTV.setText(Brand);
        activityProductDetailsBinding.ProductCategoryTV.setText(Category);
        activityProductDetailsBinding.ProductLocationTV.setText(Location);
        activityProductDetailsBinding.ProductPostedTV.setText(Date + " " + Time);
        activityProductDetailsBinding.ProductDescriptionTV.setText(Description);
        activityProductDetailsBinding.ProductModelTV.setText(Model);
        activityProductDetailsBinding.ProductTypeTV.setText(Type);

        Picasso.with(ProductDetailsActivity.this).load(Url).into(activityProductDetailsBinding.productImageThumb);


        if(!getIntent().getStringExtra("from").equals(null)){
            if(getIntent().getStringExtra("from").equals("myads")){
                activityProductDetailsBinding.bottomNevigationView.inflateMenu(R.menu.bottom_menu_myproduct_desc);
            }
            else if(getIntent().getStringExtra("from").equals("allads")){
                activityProductDetailsBinding.bottomNevigationView.inflateMenu(R.menu.bottom_menu_product_desc);
            }
        }


        activityProductDetailsBinding.productImageThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ProductDetailsActivity.this, FullScreenImageActivity.class).putExtra("url", Url));
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_USERS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<User>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);

                    if (user.getUserId().equals(User)) {
                        users.add(user);
                    }


                }
                poster_name = users.get(0).getFullName();
                poster_email = users.get(0).getEmail();
                poster_phone = users.get(0).getContactNo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        activityProductDetailsBinding.bottomNevigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.Call:

                        AlertDialog.Builder alert = new AlertDialog.Builder(ProductDetailsActivity.this);
                        alert.setTitle("Call " + poster_name + "??");
                        alert.setMessage("Are you sure you want to call this number: " + poster_phone + " ??");
                        alert.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callPhoneNumber(poster_phone);
                            }
                        });
                        alert.setNegativeButton("Cancel",null);
                        alert.show();



                        return true;
                    case R.id.Mail:

                        AlertDialog.Builder alert2 = new AlertDialog.Builder(ProductDetailsActivity.this);
                        alert2.setTitle("Mail " + poster_name + "??");
                        alert2.setMessage("Are you sure you want to mail to this email: " + poster_email + " ??");
                        alert2.setPositiveButton("Mail", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               sendEmail(poster_email,title);
                            }
                        });
                        alert2.setNegativeButton("Cancel",null);
                        alert2.show();

                        return true;
                    case R.id.Update:

                        Toast.makeText(ProductDetailsActivity.this, "update", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.Delete:
                        Toast.makeText(ProductDetailsActivity.this, "delete", Toast.LENGTH_SHORT).show();
                        break;


                }
                return false;
            }
        });




    }
    public void callPhoneNumber(String phone)
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(ProductDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void sendEmail(String mail,String title) {

        String[] TO = {mail};
        String subject=title;
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I want to buy your "+subject);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ProductDetailsActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
