package com.example.restaurant.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurant.Model.MenuNote;
import com.example.restaurant.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class MenuAdapter extends FirestoreRecyclerAdapter<MenuNote,MenuAdapter.MenuViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MenuAdapter(@NonNull FirestoreRecyclerOptions<MenuNote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull MenuNote model) {
        holder.m_name.setText(model.getMenu_name());
        holder.m_price.setText(""+model.getPrice());
        Picasso.get().load(model.getImage()).into(holder.m_image);

    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_view,parent,false);
        return new MenuViewHolder(view);
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView m_name,m_price;
        ImageView m_image;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            m_name=itemView.findViewById(R.id.m_name);
            m_price=itemView.findViewById(R.id.m_price);
            m_image=itemView.findViewById(R.id.m_image);
        }
    }
}
