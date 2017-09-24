package com.example.jibunnisa.alfaklassify.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button registerBtn;
    private EditText emailET,passwordET,nameET,addressET,contactNoET;
    private TextView loginTV;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private Toolbar toolbar;
    private String target;
    Uri imageUri;
    private String imageDownlodUrl;
    private static final int GALLERY_REQUEST = 1;
    private ImageButton profileImageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("User_Images");
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_USERS);
       /* if(firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),NearPlaceActivity.class));
        }*/

       target = getIntent().getStringExtra("from");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        profileImageBtn = (ImageButton) findViewById(R.id.profileImage);
        nameET = (EditText) findViewById(R.id.fullNameET);
        addressET = (EditText) findViewById(R.id.addressET);
        contactNoET = (EditText) findViewById(R.id.contactET);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        loginTV = (TextView) findViewById(R.id.signInTV);
        registerBtn.setOnClickListener(this);
        loginTV.setOnClickListener(this);
        profileImageBtn.setOnClickListener(this);
        imageDownlodUrl = "";
    }

    @Override
    public void onClick(View view) {
        if(view == registerBtn){
            registerUser();
        }
        if(view == loginTV){
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
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
    private void registerUser() {
        final String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        final String fullName = nameET.getText().toString().trim();
        final String contactNo = contactNoET.getText().toString().trim();
        final String address = addressET.getText().toString().trim();
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this,"Please enter user name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
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
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(imageUri != null){
                                StorageReference filePath = mStorage.child(imageUri.getLastPathSegment());
                                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imageDownlodUrl = taskSnapshot.getDownloadUrl().toString();
                                        String user_id = firebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference current_user_db = mDatabase.child(user_id);
                                        User user = new User(user_id,fullName,imageDownlodUrl,email,contactNo,address);
                                        current_user_db.setValue(user);
                                        Toast.makeText(RegisterActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                    }
                                });
                            }
                            else {
                                String user_id = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabase.child(user_id);
                                User user = new User(user_id,fullName,imageDownlodUrl,email,contactNo,address);
                                current_user_db.setValue(user);
                                Toast.makeText(RegisterActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                                if(target.equals("myaccount")){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","myaccount"));
                                }
                                else if (target.equals("fab")){
                                    startActivity(new Intent(getApplicationContext(),PostingProductActivity.class));
                                }else if(target.equals("header")){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","allads"));
                                }
                            }

                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"Could not register. Please try again",Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });
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
