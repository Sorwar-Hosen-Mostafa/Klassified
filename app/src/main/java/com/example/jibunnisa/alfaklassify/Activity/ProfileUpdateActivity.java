package com.example.jibunnisa.alfaklassify.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jibunnisa.alfaklassify.MainActivity;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.Utils;
import com.example.jibunnisa.alfaklassify.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backBtn, updateBtn;
    private EditText emailET,passwordET,nameET,addressET,contactNoET;
    private TextView loginTV;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private Toolbar toolbar;
    private String target;
    private User userObj;
    Uri imageUri;
    private String imageDownlodUrl;
    private static final int GALLERY_REQUEST = 1;
    private ImageButton profileImageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("User_Images");
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_USERS);
       /* if(firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),NearPlaceActivity.class));
        }*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile Update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);
        profileImageBtn = (ImageButton) findViewById(R.id.profileImage);
        nameET = (EditText) findViewById(R.id.fullNameET);
        addressET = (EditText) findViewById(R.id.addressET);
        contactNoET = (EditText) findViewById(R.id.contactET);
        backBtn = (Button) findViewById(R.id.backBtn);
        updateBtn = (Button) findViewById(R.id.updateBtn);

        backBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        profileImageBtn.setOnClickListener(this);
        imageDownlodUrl = "";
        userObj=(User)getIntent().getSerializableExtra("userObject");
        populateUserProfile();
    }

    private void populateUserProfile() {
        if(userObj != null &&!TextUtils.isEmpty(userObj.getUserId())){
            nameET.setText(userObj.getFullName());
            addressET.setText(userObj.getAddress());
            contactNoET.setText(userObj.getContactNo());
            imageDownlodUrl = userObj.getImageUrl();
            if(userObj.getImageUrl()!= null && !userObj.getImageUrl().equals("")){
                Picasso.with(getApplicationContext()).load(userObj.getImageUrl()).fit().centerCrop().into(profileImageBtn);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == updateBtn){
            updateUser();
        }

        if(view == backBtn){
            startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","myaccount"));
        }
        if(view == profileImageBtn){
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");

            startActivityForResult(galleryIntent,GALLERY_REQUEST);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
            //profileImageBtn.setImageURI(imageUri);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                profileImageBtn.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void updateUser() {

        final String fullName = nameET.getText().toString().trim();
        final String contactNo = contactNoET.getText().toString().trim();
        final String address = addressET.getText().toString().trim();
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this,"Please enter user name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this,"Please enter user name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(contactNo)){
            Toast.makeText(this,"Please enter contact no",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Updating Profile User...");
        progressDialog.show();
        if(imageUri != null){
            StorageReference filePath = mStorage.child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageDownlodUrl = taskSnapshot.getDownloadUrl().toString();
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = mDatabase.child(user_id);
                    User user = new User(user_id,fullName,imageDownlodUrl,userObj.getEmail(),contactNo,address);
                    current_user_db.setValue(user);
                    progressDialog.dismiss();
                    Toast.makeText(ProfileUpdateActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","myaccount"));
                }
            });
        }
        else {
            String user_id = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference current_user_db = mDatabase.child(user_id);
            User user = new User(user_id,fullName,imageDownlodUrl,userObj.getEmail(),contactNo,address);
            current_user_db.setValue(user);
            progressDialog.dismiss();
            Toast.makeText(ProfileUpdateActivity.this,"Updated Successfully",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","myaccount"));
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
