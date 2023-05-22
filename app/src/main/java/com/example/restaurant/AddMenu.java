package com.example.restaurant;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.restaurant.Model.MenuNote;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddMenu extends AppCompatActivity {
    private EditText menu, price;
    private ImageView food_image;
    private Button add, select_image;
    String userid,rest_name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference addref;
    private Query query;
    private static final int IMAGE_REQUEST = 1;
    private Uri ImageUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private String mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        menu = findViewById(R.id.menuname);
        price = findViewById(R.id.addprice);
        add = findViewById(R.id.button);
        select_image = findViewById(R.id.select_image);
        food_image = findViewById(R.id.food_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //check Internet Connection
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
        if(null!=infos){
            final Intent data = getIntent();
            userid = data.getStringExtra("userid");
            rest_name=data.getStringExtra("rest_name");
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference().child("menu image/"+rest_name);
            addref = db.collection("hotels").document(userid).collection("menu");

            //selecting image from file
            select_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageChooser();
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (menu.getText().toString().isEmpty()) {
                        menu.setError("Please Add Food Name");
                        menu.requestFocus();
                        return;
                    } else if (price.getText().toString().isEmpty()) {
                        price.setError("Please Enter Price");
                        price.requestFocus();
                        return;
                    } else if (ImageUri == null) {
                        Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadImage();

                        Toast.makeText(getApplicationContext(), "Menu added successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }


            });

        }else{
            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMenu.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        final StorageReference fileReference = storageReference.child(menu.getText().toString()+"." + getFileExtension(ImageUri));
        uploadTask = fileReference.putFile(ImageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
//                        progressDialog.setCanceledOnTouchOutside(false);
                    Uri downloadUri = task.getResult();
                    mUri = downloadUri.toString();
                    String foodName = menu.getText().toString();
                    String foodPrice = price.getText().toString();
                    Integer fprice = Integer.parseInt(foodPrice);
                    MenuNote menuNote = new MenuNote(foodName,fprice,mUri);
                    addref.add(menuNote);

                } else {
                    String message = task.getException().toString();
                    Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imageChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    //call after chooses image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode == RESULT_OK && data!=null &&data.getData()!=null){
            ImageUri=data.getData();
            Picasso.get().load(ImageUri).into(food_image);
            select_image.setText("Change Image");
        }
    }
}
