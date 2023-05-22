package com.example.restaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.restaurant.Model.MenuNote;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChangeMenu extends AppCompatActivity {
    Button add,edit;
    String userid,foodName,rest_name;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //check Internet Connection
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
        if(null!=infos){
            final Intent data=getIntent();
            userid=data.getStringExtra("userid");
            rest_name=data.getStringExtra("rest_name");
            add = findViewById(R.id.addmenu);
            edit = findViewById(R.id.editmenu);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext(),AddMenu.class);
                    i.putExtra("userid",userid);
                    i.putExtra("rest_name",rest_name);
                    startActivity(i);
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create view for AlertBox
                    View view= LayoutInflater.from(ChangeMenu.this).inflate(R.layout.edit_price,null);
                    final EditText new_price=view.findViewById(R.id.new_price);
                    final Spinner menu_list=view.findViewById(R.id.menu_list);
                    final ArrayList<String> items = new ArrayList<String>();
                    items.add("select food name");
                    //fetch menu for firestore database
                    db.collection("hotels").document(userid).collection("menu")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        MenuNote note = documentSnapshot.toObject(MenuNote.class);
                                        String f_name=note.getMenu_name();
                                        items.add(f_name);
                                    }
                                    //set menu into spinner
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, items);
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    menu_list.setAdapter(arrayAdapter);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                                }
                            });
                    //get selected menu item from spinner
                    menu_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            foodName = menu_list.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangeMenu.this);
                    builder.setTitle("Change/Edit Food Price")
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Integer price=Integer.parseInt(new_price.getText().toString());
                                    if(new_price.getText().toString().isEmpty())
                                    {
                                        Toast.makeText(getApplicationContext(), "New Price Empty,Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (foodName.equals("select food name")){
                                        Toast.makeText(getApplicationContext(), "Please select food", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        //fetch path for selected food name
                                        db.collection("hotels").document(userid).collection("menu")
                                                .whereEqualTo("menu_name",foodName).get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                            String path=documentSnapshot.getReference().getPath();
                                                            //Update selected food  Price
                                                            db.document(path).update("price",price);
                                                            Toast.makeText(getApplicationContext(),"Menu Update Successfully",Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Please select food", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            })
                            .setNegativeButton("no",null);
                    AlertDialog alert=builder.create();
                    alert.show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_SHORT).show();
        }

    }
}
