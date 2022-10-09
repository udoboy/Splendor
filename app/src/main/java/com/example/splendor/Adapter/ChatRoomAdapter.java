package com.example.splendor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.splendor.Fragments.InboxFragment;
import com.example.splendor.Models.MessageModel;
import com.example.splendor.R;
import com.example.splendor.databinding.RecieverLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter {

    List<MessageModel> messageList;
    Context context;
    ViewContentListener viewContentListener;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatRoomAdapter(List<MessageModel> messageList, Context context, ViewContentListener viewContentListener) {
        this.messageList = messageList;
        this.context = context;
        this.viewContentListener = viewContentListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_layout, parent, false);
            return new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reciever_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageList.get(position);
        System.out.println("message is "+ messageModel.getMessage());
        if (holder.getClass() == SenderViewHolder.class){
            if (messageModel.getMessage().equals("null")){
                ((SenderViewHolder)holder).senderMessage.setVisibility(View.GONE);
            }
            else{
                ((SenderViewHolder)holder).senderMessage.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).senderMessage.setText(messageModel.getMessage());
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("KK:mm");
            String date = simpleDateFormat.format(messageModel.getTimeStamp());
            ((SenderViewHolder)holder).senderTime.setText(date);
            if (messageModel.getContentType() != null){
                if (messageModel.getContentType().equals("text")){
                    ((SenderViewHolder)holder).contentThumbnail.setVisibility(View.GONE);
                    ((SenderViewHolder)holder).senderMessage.setVisibility(View.VISIBLE);
                    ((SenderViewHolder)holder).slVideoConfirm.setVisibility(View.GONE);
                }
                else if (messageModel.getContentType().equals("image")){
                    Glide.with(context).asBitmap().load(messageModel.getContent()).placeholder(R.drawable.ic_splendor_placeholder)
                            .into(((SenderViewHolder)holder).contentThumbnail);
                    ((SenderViewHolder)holder).slVideoConfirm.setVisibility(View.GONE);
                    ((SenderViewHolder)holder).contentThumbnail.setVisibility(View.VISIBLE);
                    ((SenderViewHolder)holder).senderMessage.setVisibility(View.VISIBLE);
                    ((SenderViewHolder)holder).contentThumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewContentListener.onToViewClicked(messageModel.getContent(), "image");
                        }
                    });
                }
                else if (messageModel.getContentType().equals("video")){
                    Glide.with(context).asBitmap().load(messageModel.getContent()).placeholder(R.drawable.ic_splendor_placeholder)
                            .into(((SenderViewHolder)holder).contentThumbnail);
                    ((SenderViewHolder)holder).slVideoConfirm.bringToFront();
                    ((SenderViewHolder)holder).slVideoConfirm.setVisibility(View.VISIBLE);
                    ((SenderViewHolder)holder).contentThumbnail.setVisibility(View.VISIBLE);
                    ((SenderViewHolder)holder).senderMessage.setVisibility(View.VISIBLE);
                    ((SenderViewHolder)holder).contentThumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewContentListener.onToViewClicked(messageModel.getContent(), "video");
                        }
                    });
                }
            }

        }
        else if (holder.getClass() == ReceiverViewHolder.class){
            if (messageModel.getMessage().equals("null")){
                ((ReceiverViewHolder)holder).receiverMessage.setVisibility(View.GONE);
            }
            else{
                ((ReceiverViewHolder)holder).receiverMessage.setText(messageModel.getMessage());
                ((ReceiverViewHolder)holder).receiverMessage.setVisibility(View.VISIBLE);
            }


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("KK:mm");
            String date = simpleDateFormat.format(messageModel.getTimeStamp());

            ((ReceiverViewHolder)holder).receiverTime.setText(date);
            if (messageModel.getContentType() != null){
                if (messageModel.getContentType().equals("text")){
                    ((ReceiverViewHolder)holder).rlContentThumbnail.setVisibility(View.GONE);
                    ((ReceiverViewHolder)holder).receiverMessage.setVisibility(View.VISIBLE);
                }
                else if (messageModel.getContentType().equals("image")){
                    Glide.with(context).asBitmap().load(messageModel.getContent()).placeholder(R.drawable.ic_splendor_placeholder)
                            .into(((ReceiverViewHolder)holder).rlContentThumbnail);
                    ((ReceiverViewHolder)holder).rlContentThumbnail.setVisibility(View.VISIBLE);
                    ((ReceiverViewHolder)holder).receiverMessage.setVisibility(View.VISIBLE);
                }
                else if (messageModel.getContentType().equals("video")){
                    Glide.with(context).asBitmap().load(messageModel.getContent()).placeholder(R.drawable.ic_splendor_placeholder)
                            .into(((ReceiverViewHolder)holder).rlContentThumbnail);
                    ((ReceiverViewHolder)holder).rlVideoConfirm.bringToFront();
                    ((ReceiverViewHolder)holder).rlVideoConfirm.setVisibility(View.VISIBLE);
                    ((ReceiverViewHolder)holder).rlContentThumbnail.setVisibility(View.VISIBLE);
                    ((ReceiverViewHolder)holder).receiverMessage.setVisibility(View.VISIBLE);
                    ((ReceiverViewHolder)holder).receiverMessage.setPaintFlags(0);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECEIVER_VIEW_TYPE;
        }
        //return super.getItemViewType(position);
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMessage, receiverTime;
        ImageView rlContentThumbnail, rlVideoConfirm;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessage = itemView.findViewById(R.id.rlMessage);
            receiverTime = itemView.findViewById(R.id.rlTimeStamp);
            rlContentThumbnail = itemView.findViewById(R.id.rlContentThumbnail);
            rlVideoConfirm = itemView.findViewById(R.id.rlVideoConfirm);
        }
    }
 public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderTime, senderMessage;
        ImageView contentThumbnail, slVideoConfirm;
     public SenderViewHolder(@NonNull View itemView) {
         super(itemView);
         senderMessage = itemView.findViewById(R.id.slMessage);
         senderTime = itemView.findViewById(R.id.slTimeStamp);
         contentThumbnail = itemView.findViewById(R.id.contentThumbnail);
         slVideoConfirm = itemView.findViewById(R.id.slVideoConfirm);
     }
 }
}
