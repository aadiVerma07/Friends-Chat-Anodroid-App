package com.example.tptchatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tptchatroom.Model.messagemodel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
Button btn1;
TextView createaccount;
EditText txtname,txtemail,txtpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            createaccount = findViewById(R.id.newuser);
            txtname = findViewById(R.id.txtname);
            txtemail = findViewById(R.id.txtemail);
            txtpass = findViewById(R.id.txtpass);
            btn1 = findViewById(R.id.btn1);
            // create a progress dialog

            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Please wait");
            progress.setMessage("We are Creating your account");
            // click event of button  start
            btn1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    progress.show();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(txtemail.getText().toString(), txtpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                Toast.makeText(MainActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, SignIn.class);
                                startActivity(intent);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("name", txtname.getText().toString());
                                hashMap.put("email", txtemail.getText().toString());
                                hashMap.put("password", txtpass.getText().toString());
                                FirebaseDatabase.getInstance().getReference().child("user").child(id).setValue(hashMap);
                                progress.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            });
            // btn1 click event end
            createaccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                }
            });
        }
}
