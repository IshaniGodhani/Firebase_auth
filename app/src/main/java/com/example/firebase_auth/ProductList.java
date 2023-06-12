package com.example.firebase_auth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.menu);
                popupMenu.getMenuInflater().inflate(R.menu.edit_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId()==R.id.update)
                        {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query applesQuery = ref.child("Products").orderByChild("id").equalTo(model.id);
                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot list : snapshot.getChildren()) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance(); // initializing object of database
                                        DatabaseReference myRef = database.getReference("Products").push(); // Creating main (parent) reference
                                        String id = myRef.getKey();

                                        DataModel dataModel = new DataModel(id,"CPU","12345","RRR","https://play-lh.googleusercontent.com/8W4Ai-J22SM8Xzwy75_wXqnYPm04zbc0QpXm6dfKiS-VNDgPjeS3rtu_yymuGWmGP4w=s48-rw");

                                        list.getRef().setValue(dataModel);
                                        //list.getRef().removeValue();
                                        notifyDataSetChanged();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });


}                        if (menuItem.getItemId()==R.id.delete)
                        {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query applesQuery = ref.child("Products").orderByChild("id").equalTo(model.id);
                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot list : snapshot.getChildren()) {
//                                        FirebaseDatabase database = FirebaseDatabase.getInstance(); // initializing object of database
//                                        DatabaseReference myRef = database.getReference("Products").push(); // Creating main (parent) reference
//                                        String id = myRef.getKey();

                                        //DataModel dataModel = new DataModel(id,"CPU","12345","RRR","https://play-lh.googleusercontent.com/8W4Ai-J22SM8Xzwy75_wXqnYPm04zbc0QpXm6dfKiS-VNDgPjeS3rtu_yymuGWmGP4w=s48-rw");

                                        //list.getRef().setValue(dataModel);
                                        list.getRef().removeValue();
                                        notifyDataSetChanged();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        return true;

                    }

                });
                popupMenu.show();
            }
        });
    }

    @NonNull
    @Override
    public ProductList.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file,parent,false);
        ProductViewHolder holder=new ProductViewHolder(view);
        return holder;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,menu;
        TextView name,price,des;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.list_img);
            name=itemView.findViewById(R.id.list_proname);
            price=itemView.findViewById(R.id.list_proprice);
            des=itemView.findViewById(R.id.list_prodes);
            menu=itemView.findViewById(R.id.menu);
        }
    }
}
