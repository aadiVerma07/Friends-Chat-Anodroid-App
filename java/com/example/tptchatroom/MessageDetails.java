package com.example.tptchatroom;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tptchatroom.Model.messagemodel;
import com.example.tptchatroom.adapter.MessageListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageDetails extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        getSupportActionBar().hide();
        String s=getIntent().getExtras().getString("name");
        String receiveruid= getIntent().getExtras().getString("uid");
        String senderuid= FirebaseAuth.getInstance().getCurrentUser().getUid();
     //   Toast.makeText(this, receiveruid+"    Hii     "+senderuid, Toast.LENGTH_LONG).show();
        TextView username=findViewById(R.id.username);
        username.setText(s);
        ImageView userprofile=findViewById(R.id.userprofile);
        Picasso.get().load(getIntent().getStringExtra("profile")).placeholder(R.drawable.profile).into(userprofile);
        ImageView btnback=findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MessageDetails.this,Chat.class);
                startActivity(intent);
            }
        });
        // Let's save message in Firebase Database on click of send bttuon
        ImageView sendmsg=findViewById(R.id.sendmsg);
        EditText usermsg=findViewById(R.id.usermsg);
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!usermsg.getText().toString().trim().isEmpty()){
                    messagemodel md=new messagemodel();
                    md.msg=usermsg.getText().toString();
                    SimpleDateFormat format=new SimpleDateFormat("hh:mm aa");
                    Date date=new Date();
                    md.msgtime=String.valueOf(format.format(date));
                    md.senderid=senderuid;
                    FirebaseDatabase.getInstance().getReference().child("message").child(senderuid+receiveruid).push().setValue(md).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("message").child(receiveruid+senderuid).push().setValue(md);
                            usermsg.setText("");
                        }
                    });
                }
                else{
                    Toast.makeText(MessageDetails.this, "Enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Lets bind MessageListAdapter to the Recyclerview
        ArrayList<messagemodel>msglist=new ArrayList<>();
        RecyclerView recyclemsg=findViewById(R.id.recyclemsg);
        MessageListAdapter adapter =new MessageListAdapter(this,msglist,senderuid+receiveruid);
        recyclemsg.setAdapter(adapter);
        recyclemsg.setLayoutManager(new LinearLayoutManager(this));

        //Lets select all message of sender and reciver from database

        FirebaseDatabase.getInstance().getReference().child("message").child(senderuid+receiveruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               //  Log.v("data",snapshot+"");
                msglist.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    messagemodel m=new messagemodel();
                    m.msg=ds.child("msg").getValue(String.class);
                    m.msgtime=ds.child("msgtime").getValue(String.class);
                    m.senderid=ds.child("senderid").getValue(String.class);
                    m.messagekey=ds.getKey();
                    msglist.add(m);

                }
                adapter.notifyDataSetChanged();
                if(msglist.size()!=0)
                    recyclemsg.smoothScrollToPosition(msglist.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}