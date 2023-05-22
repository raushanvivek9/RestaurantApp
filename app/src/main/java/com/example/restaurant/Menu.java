package com.example.restaurant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurant.Adapter.MenuAdapter;
import com.example.restaurant.Model.MenuNote;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Menu extends AppCompatActivity {
    private String userid,restPath,rest_name,u_email_id;
    RecyclerView menu_recyclerview;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference restref;
    private Query query;
    MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final Intent data=getIntent();
        userid=data.getStringExtra("userid");
        menu_recyclerview=findViewById(R.id.menu_recyclerview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //check Internet Connection
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
        if(null!=infos){
                restref=db.collection("hotels").document(userid).collection("menu");
                query=restref;
                FirestoreRecyclerOptions<MenuNote> options=new FirestoreRecyclerOptions.Builder<MenuNote>()
                        .setQuery(query,MenuNote.class)
                        .build();
                adapter=new MenuAdapter(options);
                menu_recyclerview.setLayoutManager(new LinearLayoutManager(this));
                menu_recyclerview.setAdapter(adapter);
        }
        else{
            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        }catch (Exception e)
        {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        }catch (Exception e)
        {

        }
    }
}
