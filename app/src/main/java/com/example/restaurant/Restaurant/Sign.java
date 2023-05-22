package com.example.restaurant.Restaurant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign extends AppCompatActivity {

    EditText email,password,name,rname,phone,city,address;
    Button signup;
    TextView alreadylogin;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);
        city=(EditText)findViewById(R.id.city);
        address=(EditText)findViewById(R.id.address);
        password = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.signup);
        rname = (EditText) findViewById(R.id.rname);
        phone = (EditText) findViewById(R.id.phoneno);
        alreadylogin = findViewById(R.id.alreadylogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = email.getText().toString();
                final String pswd = password.getText().toString();
                final String ph = phone.getText().toString();

                if (username.isEmpty()) {
                    email.setError("email is required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    email.setError("please enter the valid email");
                    email.requestFocus();
                    return;
                }
                if (ph.isEmpty()) {
                    phone.setError("Please enter the valid number");
                    phone.requestFocus();
                }
                if (!Patterns.PHONE.matcher(ph).matches()) {
                    phone.setError("Please enter valid phone number");
                    phone.requestFocus();
                    return;
                }
                if(ph.length()!=10){
                    phone.setError("Please enter valid phone number");
                    phone.requestFocus();
                    return;
                }
                if (pswd.isEmpty()) {
                    password.setError("password is required");
                    password.requestFocus();
                    return;
                }
                if (pswd.length() < 6) {
                    password.setError("Minimum length of password should be 6");
                    password.requestFocus();
                    return;
                }
                if(rname.getText().toString().isEmpty()){
                    rname.setError("Restaurant name is required");
                    rname.requestFocus();
                    return;
                }
                if(city.getText().toString().isEmpty()){
                    city.setError("City is required");
                    city.requestFocus();
                    return;
                }
                if(address.getText().toString().isEmpty()){
                    address.setError("Address is required");
                    address.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //check Internet Connection
                ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                if(null!=infos){
                    Intent intent=new Intent(getApplicationContext(),Otp_Verfication.class);
                    intent.putExtra("name",name.getText().toString());
                    intent.putExtra("email",username);
                    intent.putExtra("password",pswd);
                    intent.putExtra("phone",ph);
                    intent.putExtra("rname",rname.getText().toString());
                    intent.putExtra("city",city.getText().toString());
                    intent.putExtra("address",address.getText().toString());
                    progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!", Toast.LENGTH_LONG).show();
                }

            }
        });
        alreadylogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), User_login.class);
                startActivity(intent);
                finish();
            }

        });
    }

}
