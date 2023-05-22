package com.example.restaurant.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurant.Model.OrderHotelNote;
import com.example.restaurant.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class OrderAdapter extends FirestoreRecyclerAdapter<OrderHotelNote,OrderAdapter.OrderViewHolder> {
    private OrderAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options,option2;
     */
    public OrderAdapter(@NonNull FirestoreRecyclerOptions<OrderHotelNote> options) {
        super(options);
    }



    @Override
    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull OrderHotelNote model) {
//        if(model.getStatus().equals("Pending")){
            holder.user_name.setText(model.getCust_name());
            holder.bill_name.setText("Bill Name: "+model.getCust_name());
            holder.orderid.setText("Order Id: "+model.getOrder_id());
            holder.pay_mtd.setText("Payment Method: "+model.getPay_method());
            holder.order_del.setText("Order: "+model.getOrder());
            holder.total_price.setText("Total Price: "+model.getTotal());
            holder.phone_no.setText("Phone no: "+model.getPhone_no());
            holder.train_no.setText("Train no: "+model.getTrain_no());
            holder.pnr_no.setText("PNR No :"+model.getPnr_no());
            holder.status.setText("Status:" +model.getStatus());
            holder.time.setText("Time "+model.getTimestamp());
//        }
//        else{


            if(model.getStatus().equals("Delivered")){
                holder.accept.setEnabled(false);
                holder.cancel.setEnabled(false);
                holder.delivery.setEnabled(false);
            }
            else if(model.getStatus().equals("Accepted")){
                holder.accept.setEnabled(false);
                holder.cancel.setEnabled(false);
            }
            else if(model.getStatus().equals("Cancelled")){
                holder.accept.setEnabled(false);
                holder.cancel.setEnabled(false);
                holder.delivery.setEnabled(false);
            }
            else {
                holder.accept.setEnabled(true);
                holder.cancel.setEnabled(true);
                holder.delivery.setEnabled(true);
            }
//        }


        if(model.getStatus().equals("Delivered")){
            holder.accept.setEnabled(false);
            holder.cancel.setEnabled(false);
            holder.delivery.setEnabled(false);
        }
        else if(model.getStatus().equals("Accepted")){
            holder.accept.setEnabled(false);
            holder.cancel.setEnabled(false);
        }
        else if(model.getStatus().equals("Cancelled")){
            holder.accept.setEnabled(false);
            holder.cancel.setEnabled(false);
            holder.delivery.setEnabled(false);
        }
        else {
            holder.accept.setEnabled(true);
            holder.cancel.setEnabled(true);
            holder.delivery.setEnabled(true);
        }



    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderview,parent,false);
        return new OrderViewHolder(view);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        Button accept,delivery,cancel;
        TextView user_name,bill_name,order_del,total_price,phone_no,train_no,pnr_no,orderid,pay_mtd,status,time;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name=itemView.findViewById(R.id.user_name);
            bill_name=itemView.findViewById(R.id.bill_name);
            order_del=itemView.findViewById(R.id.order_del);
            total_price=itemView.findViewById(R.id.total_price);
            phone_no=itemView.findViewById(R.id.phone_no);
            train_no=itemView.findViewById(R.id.train_no);
            pnr_no=itemView.findViewById(R.id.pnr_no);
            orderid=itemView.findViewById(R.id.orderid);
            pay_mtd=itemView.findViewById(R.id.pay_mtd);
            status=itemView.findViewById(R.id.status);
            time=itemView.findViewById(R.id.time);
            accept=itemView.findViewById(R.id.accept);
            delivery=itemView.findViewById(R.id.delivery);
            cancel=itemView.findViewById(R.id.cancel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getAdapterPosition();
                    if(listener!=null) {
                        listener.onAccept(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getAdapterPosition();
                    if(listener!=null){
                        listener.onCancel(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
            delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getAdapterPosition();
                    if(listener!=null){
                        listener.onDelivery(getSnapshots().getSnapshot(position),position);
                    }
                }
            });



        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onAccept(DocumentSnapshot documentSnapshot,int position);
        void onCancel(DocumentSnapshot snapshot, int position);
        void onDelivery(DocumentSnapshot snapshot, int position);
    }
    public  void setOnItemClickListener(OrderAdapter.OnItemClickListener listener){
        this.listener=listener;
    }
}



