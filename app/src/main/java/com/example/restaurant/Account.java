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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.restaurant.Restaurant.User_login;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account<imageUri> extends AppCompatActivity {

    // private View account_view;
    CircleImageView profile_image;
    // ImageView imageView;
    TextView holder_name,a_email_id,phone,rname,r_city,r_address;
    TextView logout;
    private FirebaseAuth mAuth;
    private static final int IMAGE_REQUEST=1;
    Uri imageUri;
    private FirebaseUser currentuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private DatabaseReference myRef,tref;
    private StorageReference UserProfileImagesRef;
    private StorageTask uploadTask;
    private ProgressDialog loadingbar;
    //private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profile_image = findViewById(R.id.profile_image);
        logout = findViewById(R.id.logout);
        holder_name = findViewById(R.id.holder_name);
        a_email_id = findViewById(R.id.a_email_id);
        rname = findViewById(R.id.rname);
        phone = findViewById(R.id.phone);
        r_city = findViewById(R.id.r_city);
        r_address = findViewById(R.id.r_address);
        //check Internet Connection
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
        if(null!=infos){
            mAuth = FirebaseAuth.getInstance();
            currentuser = mAuth.getCurrentUser();
            myRef = FirebaseDatabase.getInstance().getReference("Restaurants").child(currentuser.getUid());
            UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("rest profile");
            loadingbar = new ProgressDialog(Account.this);


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))) {
                        String retrivename = dataSnapshot.child("name").getValue().toString();
                        String retriveemail = dataSnapshot.child("username").getValue().toString();
                        String retriveprofileimage = dataSnapshot.child("image").getValue().toString();
                        String retrivephone = dataSnapshot.child("phone").getValue().toString();
                        String retrivername = dataSnapshot.child("rname").getValue().toString();
                        String retrivecity=dataSnapshot.child("city").getValue().toString();
                        String retriveaddress=dataSnapshot.child("address").getValue().toString();
                        holder_name.setText(retrivename);
                        a_email_id.setText(retriveemail);
                        phone.setText(retrivephone);
                        rname.setText(retrivername);
                        r_city.setText(retrivecity);
                        r_address.setText(retriveaddress);

                        Picasso.get().load(retriveprofileimage).into(profile_image);


                    } else if (dataSnapshot.exists() && (dataSnapshot.hasChild("name"))) {
                        String retrivename = dataSnapshot.child("name").getValue().toString();
                        String retriveemail = dataSnapshot.child("username").getValue().toString();
                        String retrivephone = dataSnapshot.child("phone").getValue().toString();
                        String retrivrrname = dataSnapshot.child("rname").getValue().toString();
                        String retrivecity=dataSnapshot.child("city").getValue().toString();
                        String retriveaddress=dataSnapshot.child("address").getValue().toString();
                        holder_name.setText(retrivename);
                        a_email_id.setText(retriveemail);
                        phone.setText(retrivephone);
                        rname.setText(retrivrrname);
                        r_address.setText(retriveaddress);
                        r_city.setText(retrivecity);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery = new Intent();
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(gallery,IMAGE_REQUEST);
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tref=FirebaseDatabase.getInstance().getReference().child("Restaurants").child(currentuser.getUid());
                    tref.child("device_Token").setValue("");
                    mAuth.getInstance().signOut();
                    Intent i=new Intent(getApplicationContext(), User_login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            });


        }else{
            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_SHORT).show();
        }

    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
      loadingbar.setTitle("set profile image");
        loadingbar.setMessage("please wait, your profile image is updating");
       loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        if (imageUri != null) {
            final StorageReference fileReference = UserProfileImagesRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
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
                        Uri downloadUri = task.getResult();
                        final String mUri = downloadUri.toString();
                        myRef.child("image")
                                .setValue(mUri);
                        db.collection("hotels").whereEqualTo("uid",currentuser.getUid()).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                            String path=documentSnapshot.getReference().getPath();
                                            db.document(path).update("image",mUri);
                                        }
                                    }
                                });
                        loadingbar.dismiss();

                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), "upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}

