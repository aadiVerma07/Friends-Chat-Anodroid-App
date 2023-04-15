package com.example.tptchatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
TextView newuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if(!isConnected()){
            Intent i=new Intent(SignIn.this,network.class);
            startActivity(i);
        }
            EditText txtemail = findViewById(R.id.txtemail);
            EditText txtpass = findViewById(R.id.txtpass);
            Button btn1 = findViewById(R.id.btn1);
            newuser = findViewById(R.id.newuser);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(txtemail.getText().toString(), txtpass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignIn.this, "Welcome to TPTChat", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignIn.this, Chat.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignIn.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            });
            newuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intent);
                }
            });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            Intent i=new Intent(this,Chat.class);
            startActivity(i);
        }
    }
    boolean isConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
          if(networkInfo.isConnected())
              return true;
          else
              return false;
        }
        else
            return false;
    }
}