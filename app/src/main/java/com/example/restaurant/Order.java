package com.example.restaurant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurant.Adapter.OrderAdapter;
import com.example.restaurant.Email.JavaMailAPI;
import com.example.restaurant.Model.OrderHotelNote;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Order extends AppCompatActivity {
    RecyclerView order_recyclerview;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference restref,useref;
    private Query query;
    private CollectionReference colref;
    private String userid,u_email;
    private OrderAdapter adapter;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentuser=mAuth.getCurrentUser();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        order_recyclerview = findViewById(R.id.order_recyclerview);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
//        Toast.makeText(getApplicationContext(), "userid" + userid, Toast.LENGTH_LONG).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //check Internet Connection
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
        if(null!=infos){
            //show order details in recylerview
            colref = db.collection("hotels").document(userid).collection("orderdetails");
            query = colref.orderBy("timestamp", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<OrderHotelNote> options = new FirestoreRecyclerOptions.Builder<OrderHotelNote>()
                    .setQuery(query, OrderHotelNote.class)
                    .build();

            restref= (CollectionReference) db.collection("hotels").document(userid).collection("orderdetails");
            colref.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                    notification();
                }
            });



            adapter = new OrderAdapter(options);
            order_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            order_recyclerview.setAdapter(adapter);

            //click on cancel for cancel the order
            adapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                }
                //change status after clicking accept button
                @Override
                public void onAccept(DocumentSnapshot documentSnapshot, int position) {
                    OrderHotelNote orderNote = documentSnapshot.toObject(OrderHotelNote.class);
                    String path = documentSnapshot.getReference().getPath();
                    final String user_uid = orderNote.getUserid();
                    u_email=orderNote.getU_email();
                    final String order_id = orderNote.getOrder_id();
                    String sts = orderNote.getStatus();
                    if (sts.equals("Pending")) {
                        //change status in hotel collection
                        db.document(path).update("status", "Accepted");
                        //change staus in user collection
                        db.collection("Users").document(user_uid).collection("orderdetails")
                                .whereEqualTo("order_id", order_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            OrderHotelNote orderHotelNote = documentSnapshot1.toObject(OrderHotelNote.class);
                                            String path2 = documentSnapshot1.getReference().getPath();
                                            db.document(path2).update("status", "Accepted");
                                            Toast.makeText(getApplicationContext(), "Order is accepted", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //sending updated info on user mail
                        String message="Your Order "+order_id+" is accepted";
                        //sendmail function
                        sendmail(message,u_email);
                        //create notification
                        databaseReference= FirebaseDatabase.getInstance().getReference();
                        HashMap<String,String> chatnotification=new HashMap<>();
                        chatnotification.put("from",currentuser.getUid());
                        chatnotification.put("type","Accept Order");
                        databaseReference.child("Notification").child(user_uid).push().setValue(chatnotification);
                    }
                }
                @Override
                public void onCancel(DocumentSnapshot documentSnapshot, int position) {
                    OrderHotelNote orderNote = documentSnapshot.toObject(OrderHotelNote.class);
                    String path = documentSnapshot.getReference().getPath();
                    final String user_uid = orderNote.getUserid();
                    u_email=orderNote.getU_email();

                    final String order_id = orderNote.getOrder_id();
                    String sts = orderNote.getStatus();
                    if (sts.equals("Pending") | sts.equals("Accept")) {
                        //change status in hotel collection
                        db.document(path).update("status", "Cancelled");
                        //change status in user collection
                        db.collection("Users").document(user_uid).collection("orderdetails")
                                .whereEqualTo("order_id", order_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            OrderHotelNote orderHotelNote = documentSnapshot1.toObject(OrderHotelNote.class);
                                            String path2 = documentSnapshot1.getReference().getPath();
                                            db.document(path2).update("status", "Cancelled");
                                            Toast.makeText(getApplicationContext(), "Order is Cancelled", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //sending updated info on user mail
                        String message="Sorry, Your Order "+order_id+" is Cancel because foods are not available";
                        //sendmail function
                        sendmail(message,u_email);
                        //create notification
                        databaseReference= FirebaseDatabase.getInstance().getReference();
                        HashMap<String,String> chatnotification=new HashMap<>();
                        chatnotification.put("from",currentuser.getUid());
                        chatnotification.put("type","Cancel Order");
                        databaseReference.child("Notification").child(user_uid).push().setValue(chatnotification);
                    }
                }

                @Override
                public void onDelivery(DocumentSnapshot documentSnapshot, int position) {
                    OrderHotelNote orderNote = documentSnapshot.toObject(OrderHotelNote.class);
                    String path = documentSnapshot.getReference().getPath();
                    final String user_uid = orderNote.getUserid();
                    u_email=orderNote.getU_email();

                    final String order_id = orderNote.getOrder_id();
                    String sts = orderNote.getStatus();
                    if (sts.equals("Accepted")) {
                        db.document(path).update("status", "Delivered");
                        db.collection("Users").document(user_uid).collection("orderdetails")
                                .whereEqualTo("order_id", order_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                            OrderHotelNote orderHotelNote = documentSnapshot1.toObject(OrderHotelNote.class);
                                            String path2 = documentSnapshot1.getReference().getPath();
                                            db.document(path2).update("status", "Delivered");
                                            Toast.makeText(getApplicationContext(), "Order is delivered", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //sending updated info on user mail
                        String message="Your Order "+order_id+" is delivered successfully\nThank You for Order";
                        //sendmail function
                        sendmail(message,u_email);
                        //create notification
                        databaseReference= FirebaseDatabase.getInstance().getReference();
                        HashMap<String,String> chatnotification=new HashMap<>();
                        chatnotification.put("from",currentuser.getUid());
                        chatnotification.put("type","Deliver Order");
                        databaseReference.child("Notification").child(user_uid).push().setValue(chatnotification);
                    }
                }

            });


        }else{
            Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_SHORT).show();
        }

    }

    private void sendmail(String message, String u_email) {
        String subject = "Order Update Details";

        //Send Mail
            JavaMailAPI javaMailAPI = new JavaMailAPI(this,u_email,subject,message);

            javaMailAPI.execute();
    }

    private void notification(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"n")
                .setContentText("Code Sphere")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentText("New Order is placed");

        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());



    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            adapter.startListening();
            order_recyclerview.smoothScrollToPosition(order_recyclerview.getAdapter().getItemCount());
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
