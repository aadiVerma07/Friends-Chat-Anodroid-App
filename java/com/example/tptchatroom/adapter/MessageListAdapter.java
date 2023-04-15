package com.example.tptchatroom.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tptchatroom.Model.messagemodel;
import com.example.tptchatroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<messagemodel> msglist;
    String senderreciever;

    public MessageListAdapter(Context context, ArrayList<messagemodel> msglist, String senderreciever) {
        this.context = context;
        this.msglist = msglist;
        this.senderreciever=senderreciever;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1){
            View v=LayoutInflater.from(context).inflate(R.layout.sendermessagesimpledesign,parent,false);
            return new SenderViewHolder(v);
        }
        else{
            View v=LayoutInflater.from(context).inflate(R.layout.recievermessagesimledesign,parent,false);
            return new ReciverViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder) holder).sendermsg.setText(msglist.get(position).msg);
            ((SenderViewHolder) holder).sendermsgtime.setText(msglist.get(position).msgtime);
        }
        else{
            ((ReciverViewHolder) holder).recivermsg.setText(msglist.get(position).msg);
            ((ReciverViewHolder) holder).recivermsgtime.setText(msglist.get(position).msgtime);

        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alert=new AlertDialog.Builder(context);
                alert.setTitle("Delete Message");
                alert.setMessage("Do you really want to delete this message");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("message").child(senderreciever).child(msglist.get(position).messagekey).setValue(null);

                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
                             return false;
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        if(msglist.get(position).senderid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            return 1;
        else
            return 2;
    }

    @Override
    public int getItemCount() {
        return msglist.size();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView sendermsg,sendermsgtime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendermsg=itemView.findViewById(R.id.sendermsg);
            sendermsgtime=itemView.findViewById(R.id.sendermessagetime);
        }
    }
    class ReciverViewHolder extends RecyclerView.ViewHolder{
        TextView recivermsg,recivermsgtime;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            recivermsg=itemView.findViewById(R.id.recievermsg);
            recivermsgtime=itemView.findViewById(R.id.recivermsgtime);
        }
    }
}