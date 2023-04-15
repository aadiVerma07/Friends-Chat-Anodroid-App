package com.example.tptchatroom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tptchatroom.Model.statusmodel;
import com.example.tptchatroom.adapter.StatusListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class StoriesFragment extends Fragment {

    public StoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_stories, container, false);

        //Lets add story of user to firbase

        LinearLayout layout=v.findViewById(R.id.addstory);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,2);
            }
        });
        //select all stories and bind in recycler view
        ArrayList<statusmodel> statuslist=new ArrayList<statusmodel>();
        RecyclerView statusrecycler = v.findViewById(R.id.userstoryrecycler);
        StatusListAdapter adapter=new StatusListAdapter(getContext(),statuslist);
        statusrecycler.setAdapter(adapter);
        statusrecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseDatabase.getInstance().getReference().child("story").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statuslist.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    statusmodel s=new statusmodel();
                    s.username=ds.child("name").getValue(String.class);
                    s.statusdate=ds.child("date").getValue(String.class);
                    ArrayList<String> status=new ArrayList<>();
                    for(DataSnapshot spic:ds.child("status").getChildren()){
                        status.add(spic.child("pic").getValue(String.class));
                    }
                    s.status=status;
                    statuslist.add(s);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri img=data.getData();
        Random r=new Random();
        int random=r.nextInt();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("story").child(FirebaseAuth.getInstance().getUid()+random);
        ref.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String status = uri.toString();
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String username=snapshot.child("name").getValue(String.class);
                                    //save data of status to the story table
                                    HashMap<String,Object> userdata=new HashMap<>();
                                    userdata.put("name",username);
                                    SimpleDateFormat df = new SimpleDateFormat("DD-MM-yyyy hh:mm aa");
                                    Date d=new Date();
                                    userdata.put("date",df.format(d));

                                    HashMap<String,Object> statusdata=new HashMap<>();
                                    statusdata.put("pic",status);

                                    FirebaseDatabase.getInstance().getReference().child("story").child(FirebaseAuth.getInstance().getUid()).updateChildren(userdata);

                                    FirebaseDatabase.getInstance().getReference().child("story").child(FirebaseAuth.getInstance().getUid()).child("status").push().setValue(statusdata);

                                    Toast.makeText(getContext(), "Story Updated", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });

                }
                catch (Exception ex)
                {
                    Toast.makeText(getContext(), "Error"+ex, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}