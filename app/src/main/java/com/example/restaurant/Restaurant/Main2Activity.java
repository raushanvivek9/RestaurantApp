package com.example.restaurant.Restaurant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.restaurant.About;
import com.example.restaurant.Account;
import com.example.restaurant.ChangeMenu;
import com.example.restaurant.Help;
import com.example.restaurant.Menu;
import com.example.restaurant.Model.UserNote;
import com.example.restaurant.Order;
import com.example.restaurant.R;
import com.example.restaurant.TrainEnq;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Main2Activity extends AppCompatActivity {
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentuser=mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CardView order,menu,change,train,account,about;
    private String userid,username,rest_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        checksetup();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Restaurant");
        setSupportActionBar(toolbar);

   }
    private void checksetup() {
        if(currentuser==null)
        {
            Intent i = new Intent(Main2Activity.this, User_login.class);
            startActivity(i);
            finish();
        }
        else{
            order=findViewById(R.id.orders);
            menu=findViewById(R.id.menu);
            change=findViewById(R.id.change);
            train=findViewById(R.id.train);
            account=findViewById(R.id.account);
            about=findViewById(R.id.about);
            final ProgressDialog progressDialog=new ProgressDialog(Main2Activity.this);
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);

            //check Internet Connection
            ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
            if(null!=infos){

                progressDialog.show();


                db.collection("hotels").whereEqualTo("uid",currentuser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                                    if(documentSnapshot.exists()){
                                        UserNote userNote = documentSnapshot.toObject(UserNote.class);

                                        userid=documentSnapshot.getId();
                                        username=userNote.getUsername();
                                        rest_name=userNote.getRest_name();
                                        progressDialog.dismiss();

                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Error:",Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //check Internet Connection
                        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                        if(null!=infos){
                            Intent intent=new Intent(getApplicationContext(), Order.class);
                            intent.putExtra("userid",userid);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //check Internet Connection
                        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                        if(null!=infos){
                            Intent intent = new Intent(getApplicationContext(), Menu.class);
                            intent.putExtra("userid", userid);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //check Internet Connection
                        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                        if(null!=infos){
                            Intent intent=new Intent(getApplicationContext(), ChangeMenu.class);
                            intent.putExtra("userid",userid);
                            intent.putExtra("rest_name",rest_name);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                        }


                    }
                });
                train.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //check Internet Connection
                        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                        if(null!=infos){
                            Intent intent=new Intent(getApplicationContext(), TrainEnq.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //check Internet Connection
                        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                        if(null!=infos){
                            Intent intent=new Intent(getApplicationContext(), Account.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(), About.class);
                        startActivity(intent);
                    }
                });

            }else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the Menu; this adds items to the action bar if it is present.
        MenuInflater inflater=getMenuInflater();
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.tool_help){
            Intent i=new Intent(getApplicationContext(), Help.class);
            startActivity(i);
//            Toast.makeText(getApplicationContext(),"id"+id,Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
