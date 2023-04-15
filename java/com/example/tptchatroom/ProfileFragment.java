package com.example.tptchatroom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructo
    }
    ImageView usericon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_profile, container, false);
      usericon=v.findViewById(R.id.userIcon);
      ImageView addusericon=v.findViewById(R.id.addusericon);
      addusericon.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent i=new Intent(Intent.ACTION_GET_CONTENT);
              i.setType("image/*");
              startActivityForResult(i,1);
          }
      });
      // for profile updation
        Button update=v.findViewById(R.id.update);
        EditText edbio=v.findViewById(R.id.edbio);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edbio.getText().toString().isEmpty()){
                   HashMap<String,Object>hupdate=new HashMap<>();
                   hupdate.put("about",edbio.getText().toString());
                   FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).updateChildren(hupdate);
                    Toast.makeText(getContext(), "Bio Updated Successfullly", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Plesase Enter Text !!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ImageView usericon=v.findViewById(R.id.userIcon);
                TextView prouser=v.findViewById(R.id.prouser);
                TextView bio=v.findViewById(R.id.bio);
                TextView email=v.findViewById(R.id.email);
                TextView name=v.findViewById(R.id.name);
                Picasso.get().load(snapshot.child("profile").getValue(String.class)).placeholder(R.drawable.profile).into(usericon);
                prouser.setText(snapshot.child("name").getValue(String.class));
                name.setText(snapshot.child("name").getValue(String.class));
                email.setText(snapshot.child("email").getValue(String.class));
                bio.setText(snapshot.child("about").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // end of updation profile
      return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            Uri userimg=data.getData();
            usericon.setImageURI(userimg);
            StorageReference storage= FirebaseStorage.getInstance().getReference().child("profilepic").child(FirebaseAuth.getInstance().getUid());
            storage.putFile(userimg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(getContext(), "Profile Uploaded", Toast.LENGTH_SHORT).show();
                            HashMap<String,Object>hashMap=new HashMap<>();
                            hashMap.put("profile",uri.toString());
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap);
                        }
                    });
                }
            });

        }
    }
}