package com.example.splendor.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.splendor.Adapter.ChatRoomAdapter;
import com.example.splendor.Adapter.ViewContentListener;
import com.example.splendor.Models.MessageModel;
import com.example.splendor.Models.MyDialogCreator;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentChatRoomBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatRoomFragment extends Fragment implements ViewContentListener {

    FragmentChatRoomBinding b;
    String id;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    String senderRoom, downloadUrl;
    String receiverRoom;
    FirebaseUser firebaseUser;
    List<MessageModel> messageList;
    ChatRoomAdapter chatRoomAdapter;
    MyDialogCreator myDialogCreator;
    RelativeLayout pText, pVideo, pImage, pCancel;

    DatabaseReference chatRef;
    ValueEventListener chatsEventListener;

    DatabaseReference usersRef;
    ValueEventListener usersEventListener;

    StorageTask sendMediaTask;
    OnCompleteListener onSendMediaComplete;
    LinearLayoutManager linearLayoutManager;



    @Override
    public void onDestroyView() {
        chatRef.removeEventListener(chatsEventListener);
        usersRef.removeEventListener(usersEventListener);
        b.recyclerVeiwMessages.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentChatRoomBinding.inflate(inflater, container,false);
        //return inflater.inflate(R.layout.fragment_chat_room, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        messageList = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(messageList,getContext(), this);
         linearLayoutManager = new LinearLayoutManager(getContext());
        b.recyclerVeiwMessages.setLayoutManager(linearLayoutManager);
        b.recyclerVeiwMessages.setAdapter(chatRoomAdapter);


        try{
           id = getArguments().getString("userId");
            getUser();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        chatRef = firebaseDatabase.getReference().child("Chats").child(senderRoom);
        chatsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageList.add(messageModel);
                }
                chatRoomAdapter.notifyDataSetChanged();
                linearLayoutManager.scrollToPositionWithOffset(messageList.size()-1,0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        chatRef.addValueEventListener(chatsEventListener);

        b.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.chatSpaceContainer, new InboxFragment()).commit();
                getActivity().onBackPressed();
            }
        });

        b.cvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = b.enterMessage.getText().toString();
                if (message.isEmpty()){

                }
                else{
                    sendMessage(firebaseUser.getUid(),message,"text", new Date().getTime());
                }
            }
        });

        b.imgAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialogCreator = new MyDialogCreator(getContext(), R.layout.choose_post_dialog, true,null, "Choose post");
                pText = myDialogCreator.findViewById(R.id.pText);
                pText.setVisibility(View.GONE);
                pCancel = myDialogCreator.findViewById(R.id.cancel);
                pCancel.setVisibility(View.GONE);
                pImage = myDialogCreator.findViewById(R.id.pImage);
                pVideo = myDialogCreator.findViewById(R.id.pVideo);
                myDialogCreator.show();
                initDialogClicks();
            }
        });
        return b.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode== 2 && resultCode == RESULT_OK ){
            //sendMedia(data.getData(), mAuth.getCurrentUser().getUid(), new Date().getTime(), "testing", "image");
            CreateCaptionFragment createCaptionFragment = new CreateCaptionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("parentUri", data.getData().toString() );
            bundle.putString("contentType", "image");
            bundle.putString("senderRoom", senderRoom);
            bundle.putString("receiverRoom", receiverRoom);
            bundle.putString("reasonForCall", "upload");
            createCaptionFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.chatSpaceContainer, createCaptionFragment).addToBackStack(null).commit();
        }
        else if(requestCode ==1 && resultCode == RESULT_OK){
            CreateCaptionFragment createCaptionFragment = new CreateCaptionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("parentUri", data.getData().toString() );
            bundle.putString("contentType", "video");
            bundle.putString("senderRoom", senderRoom);
            bundle.putString("receiverRoom", receiverRoom);
            bundle.putString("reasonForCall", "upload");
            createCaptionFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.chatSpaceContainer, createCaptionFragment).addToBackStack(null).commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initDialogClicks(){
        if( pVideo!= null && pImage != null){
            pVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openVideo = new Intent();
                    openVideo.setType("video/*");
                    openVideo.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(openVideo, 1);
                    myDialogCreator.dismiss();
                }
            });

            pImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openVideo = new Intent();
                    openVideo.setType("image/*");
                    openVideo.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(openVideo, 2);
                    myDialogCreator.dismiss();
                }
            });
        }
    }

    public void sendMessage(String senderId, String message, String content, long timeStamp){
        firebaseDatabase.getReference().child("Chats");
        HashMap<String, Object> map = new HashMap<>();
        map.put("senderId", senderId);
        map.put("message", message);
        map.put("content", content);
        map.put("timeStamp", timeStamp);
        map.put("contentType", "text");

        firebaseDatabase.getReference().child("Chats").child(senderRoom).push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    firebaseDatabase.getReference().child("Chats").child(receiverRoom).push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //message has been sent
                            Log.d(TAG, "onSuccess: message sent");
                            b.enterMessage.setText("");
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendMedia(Uri uri, String senderId, long timeStamp, String message, String contentType){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("ChatsMedia").child(System.currentTimeMillis() + getExtention(uri));
        sendMediaTask = ref.putFile(uri);
        onSendMediaComplete = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (!sendMediaTask.isCanceled()){
                    if (task.getResult() != null){
                        Uri downloadUri = task.getResult();
                         downloadUrl = downloadUri.toString();
                        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(senderRoom);
                        String chatId= chatsRef.push().getKey();
                        HashMap<String, Object> uploadMap = new HashMap<>();
                        uploadMap.put("senderId", senderId);
                        uploadMap.put("message",message);
                        uploadMap.put("content", downloadUrl);
                        uploadMap.put("timestamp", timeStamp);
                        uploadMap.put("contentType", contentType);
                        chatsRef.child(chatId).setValue(uploadMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    System.out.println("message sent on the sender side");
                                    DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(receiverRoom);
                                    String recChatId = receiverRef.push().getKey();
                                    HashMap<String, Object> uploadMap1 = new HashMap<>();
                                    uploadMap1.put("senderId", senderId);
                                    uploadMap1.put("message", message);
                                    uploadMap1.put("timestamp", timeStamp);
                                    uploadMap1.put("content", downloadUrl);
                                    uploadMap1.put("contentType", contentType);
                                    receiverRef.child(recChatId).setValue(uploadMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                System.out.println("message sent on both sides");
                                            }

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        };
        sendMediaTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(onSendMediaComplete);

    }

    private String getExtention(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(imageUri));
    }



    public void getUser(){
        senderRoom = mAuth.getCurrentUser().getUid() + id;
        receiverRoom = id + mAuth.getCurrentUser().getUid();

        usersRef = firebaseDatabase.getReference().child("Users").child(id);
        usersEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                b.cdUsername.setText(users.getUsername());
                Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(b.cdProfileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.addValueEventListener(usersEventListener);
    }


    @Override
    public void onToViewClicked(String url, String contentType) {
        ViewFragment viewFragment = new ViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("contentType", contentType);
        bundle.putString("content", url);
        viewFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.chatSpaceContainer, viewFragment).addToBackStack(null).commit();
    }
}