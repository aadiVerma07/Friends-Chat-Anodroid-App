package com.example.tptchatroom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tptchatroom.Model.usermodel;
import com.example.tptchatroom.adapter.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_chat, container, false);
        // Lets bind adapter in recycler view
        ArrayList<usermodel>list=new ArrayList<usermodel>();
        RecyclerView recycle=v.findViewById(R.id.userrecylce);
        UserListAdapter ad= new UserListAdapter(getContext(),list);
        recycle.setLayoutManager(new LinearLayoutManager(getContext()));
        recycle.setAdapter(ad);
        FirebaseAuth auth= FirebaseAuth.getInstance();
        String email=auth.getCurrentUser().getEmail();
        // Lets get all records form Firedatabase user table
        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user:snapshot.getChildren()){
                    usermodel u=new usermodel();
                    u.name=user.child("name").getValue(String.class);
                            u.email=user.child("email").getValue(String.class);
                            u.uid=user.getKey();
                            u.profilepic=user.child("profile").getValue(String.class);
                            if(!(u.email.equals(email)))
                            list.add(u);
                }
             //   Toast.makeText(getContext(), list.size()+"", Toast.LENGTH_SHORT).show();
                ad.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
           Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_SHORT).show();

            }
        });
        return v;
    }
}