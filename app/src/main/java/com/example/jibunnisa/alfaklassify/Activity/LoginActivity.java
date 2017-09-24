package com.example.jibunnisa.alfaklassify.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.jibunnisa.alfaklassify.Fragments.MyAccountFragment;
import com.example.jibunnisa.alfaklassify.MainActivity;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button loginBtn;
    private EditText emailET,passwordET;
    private TextView signupTV;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private String target;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        /*if(firebaseAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),NearPlaceActivity.class));
        }*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        target= getIntent().getStringExtra("from");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        signupTV = (TextView) findViewById(R.id.signupTV);
        loginBtn.setOnClickListener(this);
        signupTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == loginBtn){
            userLogin();
        }
        if(view == signupTV){
            finish();
            startActivity(new Intent(this,RegisterActivity.class).putExtra("from",target));
        }
    }
    private void userLogin() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Loging Please Wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            final String user_id = firebaseAuth.getCurrentUser().getUid();
                            Utils.saveToPrefs(getApplicationContext(), Utils.PREF_USER_ID, user_id);

                            if(!TextUtils.isEmpty(target))
                            {
                                if(target.equals("myaccount")){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","myaccount"));
                                }
                                else if (target.equals("fab")){
                                    startActivity(new Intent(getApplicationContext(),PostingProductActivity.class));
                                }else if(target.equals("header")){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","allads"));
                                }
                            }
                            else {
                                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("ab","allads"));
                            }

                        }else {
                            Toast.makeText(LoginActivity.this,"Could not register. Please try again",Toast.LENGTH_SHORT).show();

                        }
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
