package com.example.splendor.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splendor.Models.CustomProgressDialogue;
import com.example.splendor.Models.MyDialogCreator;
import com.example.splendor.PostActivity;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentCreateCaptionBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

public class CreateCaptionFragment extends Fragment {
    FragmentCreateCaptionBinding b;
    Uri parentUri;
    String parentUriString;
    String contentType;
    StorageTask sendMediaTask;
    OnCompleteListener onSendMediaComplete;
    String downloadUrl;
    String senderRoom;
    String receiverRoom;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    OnProgressListener onSendingProgress;


    MyDialogCreator myDialogCreator;
    ProgressBar uploadProgressBar;
    TextView uploadPercentage;
    ImageView uploadSourceImage;
    ImageView cancelLoad;
    StorageTask uploadTask;
    String reasonForCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentCreateCaptionBinding.inflate(inflater, container, false);
        //return inflater.inflate(R.layout.fragment_create_caption, container, false);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(getContext());

        try {
            parentUriString = getArguments().getString("parentUri");
            contentType = getArguments().getString("contentType");
            senderRoom = getArguments().getString("senderRoom");
            receiverRoom = getArguments().getString("receiverRoom");
            reasonForCall = getArguments().getString("reasonForCall");
            if (parentUriString != null){
                if (contentType.equals("image")){
                    parentUri = Uri.parse(parentUriString);
                    b.parentImage.setImageURI(parentUri);
                }
                else if (contentType.equals("video")){
                    parentUri = Uri.parse(parentUriString);
                }

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }


        if (contentType.equals("image")){
            b.parentImage.setVisibility(View.VISIBLE);
            b.parentVideo.setVisibility(View.GONE);
        }
        else{
            b.parentVideo.setVisibility(View.VISIBLE);
            b.parentImage.setVisibility(View.VISIBLE);
        }


        if (reasonForCall.equals("view")){
            b.linear.setVisibility(View.GONE);
        }
        else if (reasonForCall.equals("upload")){
            b.linear.setVisibility(View.VISIBLE);
            b.coRelSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = b.cpenterMessage.getText().toString().isEmpty()? "null" : b.cpenterMessage.getText().toString();
                    if(parentUri != null){
                        sendMedia(parentUri, mAuth.getCurrentUser().getUid(), new Date().getTime(), message , contentType);
                    }
                    else{
                        //write your code here
                    }
                }
            });
        }



        return b.getRoot();
    }

    public void sendMedia(Uri uri, String senderId, long timeStamp, String message, String contentType){
        myDialogCreator = new MyDialogCreator(getActivity(), R.layout.loading_layout, false, null, null);
        uploadProgressBar = myDialogCreator.findViewById(R.id.llLoadingProgress);
        uploadPercentage = myDialogCreator.findViewById(R.id.lltxtShowProgress);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        cancelLoad = myDialogCreator.findViewById(R.id.cancelLoading);

        myDialogCreator.show();
        onSendingProgress = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                uploadProgressBar.setProgress((int) progress);
                uploadPercentage.setText((int) progress + "%");

            }
        };
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
                        uploadMap.put("timeStamp", timeStamp);
                        uploadMap.put("contentType", contentType);
                        chatsRef.child(chatId).setValue(uploadMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    myDialogCreator.dismiss();
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
                                                pd.dismiss();
                                                System.out.println("message sent on both sides");
                                                getActivity().onBackPressed();
                                            }

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    myDialogCreator.dismiss();
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
        sendMediaTask.addOnProgressListener(onSendingProgress);

    }

    private String getExtention(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(imageUri));
    }
}