package com.example.splendor.Fragments;

import android.Manifest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.splendor.Models.MyDialogCreator;
import com.example.splendor.PostActivity;
import com.example.splendor.PostDetailActivity;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class ViewFragment extends Fragment {
    FragmentViewBinding b;
    String contentType;
    String content;
    Uri contentUri;
    Handler uiHandler;
    Runnable uiRunnable;
    File pathAsFile;

    OnProgressListener onProgressListener;

    MyDialogCreator myDialogCreator;
    ProgressBar uploadProgressBar;
    TextView uploadPercentage;
    ImageView uploadSourceImage;
    ImageView cancelLoad;
    TextView uploadingStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentViewBinding.inflate(inflater, container, false);
       // return inflater.inflate(R.layout.fragment_view, container, false);

        try {
            content = getArguments().getString("content");
            contentType = getArguments().getString("contentType");
            contentUri = Uri.parse(content);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        b.downloadContentFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runtimePermission();
            }
        });


        if (contentType != null){
            if (contentType.equals("image")){
                b.viewParentImage.setVisibility(View.VISIBLE);
                b.viewParentVideo.setVisibility(View.GONE);
                b.viewSeekBar.setVisibility(View.GONE);
                Glide.with(getContext()).asBitmap().load(content).placeholder(R.drawable.ic_splendor_placeholder).into(b.viewParentImage);
                b.loadingViewProgress.setVisibility(View.GONE);


            }
            else if (contentType.equals("video")){
                b.viewSeekBar.setEnabled(false);
                b.loadingViewProgress.bringToFront();
                b.viewSeekBar.setProgress(0);
                uiHandler = new Handler();
                uiRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (b.viewParentVideo != null){
                            b.viewSeekBar.setProgress(b.viewParentVideo.getCurrentPosition());
                        }
                        uiHandler.postDelayed(this,100);
                    }
                };
                b.getRoot().post(uiRunnable);
                //getActivity().runOnUiThread(uiRunnable);
                b.viewParentVideo.setVisibility(View.VISIBLE);
                b.viewParentImage.setVisibility(View.GONE);
                b.viewParentVideo.setVideoURI(contentUri);
                b.viewParentVideo.start();
                b.viewParentVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        b.viewSeekBar.setEnabled(true);
                        b.loadingViewProgress.setVisibility(View.GONE);
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                        b.viewSeekBar.setMax(b.viewParentVideo.getDuration());
                        b.viewSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean isSeeked) {
                                if (b.viewParentVideo != null && isSeeked){
                                    b.viewParentVideo.seekTo(i);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                    }
                });

            }

        }
        return b.getRoot();
    }

    public void createFolder(){
        boolean success;
        String path = Environment.getExternalStorageDirectory() + File.separator +"Splendor";
        pathAsFile = new File(path);
        if (!pathAsFile.exists()){
            success = pathAsFile.mkdir();
            System.out.println(success);
        }

        downloadFile();

    }

    private void runtimePermission(){
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                createFolder();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void downloadFile() {
        File localFile = null;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(content);
        

        if (contentType != null){
            if (contentType.equals("video")){
               // localFile = new File(pathAsFile,new UUID(200000, 30000)+".mp4");
                try {
                    localFile = File.createTempFile("Sp-VID-", ".mp4", pathAsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (contentType.equals("image")){
               // localFile = new File(pathAsFile,new UUID(20000, 30000)+".jpg");
                try {
                    localFile = File.createTempFile("Sp-IMG-", ".jpg", pathAsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            myDialogCreator = new MyDialogCreator(getActivity(), R.layout.loading_layout, false, null, null);
            uploadProgressBar = myDialogCreator.findViewById(R.id.llLoadingProgress);
            uploadPercentage = myDialogCreator.findViewById(R.id.lltxtShowProgress);
            uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
            cancelLoad = myDialogCreator.findViewById(R.id.cancelLoading);
            uploadingStatus = myDialogCreator.findViewById(R.id.uploadingStatus);


            cancelLoad.setVisibility(View.GONE);
            myDialogCreator.show();
            Glide.with(getContext()).asBitmap().load(content).placeholder(R.drawable.ic_splendor_placeholder).into(uploadSourceImage);

            uploadingStatus.setText("Downloading");
            onProgressListener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    uploadProgressBar.setProgress((int) progress);
                    uploadPercentage.setText((int) progress + "%");
                }
            };

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    myDialogCreator.dismiss();
                    Toast.makeText(getContext().getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    myDialogCreator.dismiss();
                    Toast.makeText(getContext().getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(onProgressListener);
        }

    }


}