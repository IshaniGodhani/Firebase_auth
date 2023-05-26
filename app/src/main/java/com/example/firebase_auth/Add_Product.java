package com.example.firebase_auth;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firebase_auth.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Random;


public class Add_Product extends Fragment {

    EditText et1,et2,et3;
    ImageView imageView;

    Button addpro;
    RecyclerView recyclerView;
    StorageReference bucket;
    StorageReference folder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_add_product, container, false);
        et1=view.findViewById(R.id.pro_name);
        et2=view.findViewById(R.id.pro_price);
        et3=view.findViewById(R.id.pro_des);
        imageView=view.findViewById(R.id.pro_Image);
        addpro=view.findViewById(R.id.btnadd_pro);
        recyclerView=view.findViewById(R.id.recyclerview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .start(getContext(), Add_Product.this);
            }
        });

        addpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TTT", "addproduct: ");
                addproduct();
            }
        });

        return view;
    }

    private void addproduct() {

        bucket=FirebaseStorage.getInstance().getReference();
        String imgName="img"+new Random().nextInt(10000)+"jpg";
        folder = bucket.child("Images/"+imgName);


        folder.getName().equals(folder.getName());    // true
        folder.getPath().equals(folder.getPath());    // false

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = folder.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return folder.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String imgUrl= String.valueOf(downloadUri);
                            Log.d("TTT", "onDownloaded: ");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Products").push();
                            String id=myRef.getKey();

                            DataModel dataModel=new DataModel(id,"DEF","1456",imgUrl);
                            myRef.setValue(dataModel);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });



            }
        });




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}