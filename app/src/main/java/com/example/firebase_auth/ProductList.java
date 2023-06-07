package com.example.firebase_auth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ProductList extends FirebaseRecyclerAdapter<DataModel,ProductList.ProductViewHolder>
{

    public ProductList(@NonNull FirebaseRecyclerOptions<DataModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductList.ProductViewHolder holder, int position, @NonNull DataModel model) {
        //holder.imageView.setImageURI();
        holder.name.setText(""+model.getProName());
        holder.price.setText(""+model.getProPrice());
        holder.des.setText(""+model.getProDes());
        Glide.with(holder.itemView.getContext()).load(model.getImgUrl()).into(holder.imageView);
    }

    @NonNull
    @Override
    public ProductList.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file,parent,false);
        ProductViewHolder holder=new ProductViewHolder(view);
        return holder;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name,price,des;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.list_img);
            name=itemView.findViewById(R.id.list_proname);
            price=itemView.findViewById(R.id.list_proprice);
            des=itemView.findViewById(R.id.list_prodes);
        }
    }
}
