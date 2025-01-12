package com.mohitvirmani.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

import static com.mohitvirmani.quiz.App.CHANNEL_1_ID;

public class AnswerUpload extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText main_ans;
    ImageButton prev_img_qs;
    Button submit_ans;
    TextView prev_ans_txt;
    Button attach_ans_img;
    ImageViewZoom imageView;
    private int answersum = 0;
    private NotificationManagerCompat notificationManager;
    private ImageButton back;

    private Uri mImageUri;
    private LottieAnimationView progress;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main18);

        main_ans = (EditText) findViewById(R.id.main_answers);
        prev_img_qs = (ImageButton) findViewById(R.id.answer_are_question_image);
        submit_ans = (Button) findViewById(R.id.submit_ans);
        prev_ans_txt = (TextView) findViewById(R.id.answer_are_text);
        attach_ans_img = (Button) findViewById(R.id.attach_answer);
        imageView = (ImageViewZoom) findViewById(R.id.verify_image);
        back = (ImageButton) findViewById(R.id.back);
        notificationManager = NotificationManagerCompat.from(this);
        progress = (LottieAnimationView) findViewById(R.id.my_progress);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        attach_ans_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        submit_ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AnswerUpload.this, "Upload in progress", Toast.LENGTH_LONG).show();

                } else {
                    uploadFile();

                    progress.setVisibility(View.VISIBLE);
                    submit_ans.setVisibility(View.INVISIBLE);
//
//
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(imageView);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AnswerUpload.this, AnswerPage.class));
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = firebaseAuth.getCurrentUser();


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 100);


            if (user.getDisplayName() != null) {

                SharedPreferences result = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String txt = result.getString("txt", "1");


                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

                fileReference.putFile(mImageUri).continueWithTask(
                        new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return fileReference.getDownloadUrl();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    final Uri downloadUri = task.getResult();
                                    String username = user.getDisplayName();
                                    Uri pic = user.getPhotoUrl();


                                    FirebaseAuth firebaseAuth;
                                    firebaseAuth = FirebaseAuth.getInstance();


                                    FirebaseUser user2 = firebaseAuth.getCurrentUser();


                                    Ans_Upload ans_upload = new Ans_Upload(main_ans.getText().toString(), downloadUri.toString(), user2.getDisplayName(), "", "");
                                    DatabaseReference solution = FirebaseDatabase.getInstance().getReference("solv");
                                    solution.child(txt).push().setValue(ans_upload);
                                    openImagesActivity();


//                                    Query query = FirebaseDatabase.getInstance().getReference().child("uploads");
//                                    query.orderByChild("mName").equalTo(txt).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
//                                                FirebaseAuth firebaseAuth;
//                                                firebaseAuth = FirebaseAuth.getInstance();
//                                                FirebaseUser user2 = firebaseAuth.getCurrentUser();
//
//                                                String key = foodSnapshot.getKey();
//                                                System.out.println(":::::::::::::" + key);
//                                                DatabaseReference fbdatabase = FirebaseDatabase.getInstance().getReference().child("uploads").child(key);
//                                                Map<String, Object> updates = new HashMap<String, Object>();
//                                                if (main_ans.getText().toString().matches("")) {
//                                                    updates.put("mAnswer", "");
//                                                    updates.put("mAnsImage", downloadUri.toString());
//                                                    updates.put("mAnsDisName", user2.getDisplayName());
//
//                                                } else {
//                                                    updates.put("mAnswer", main_ans.getText().toString());
//                                                    updates.put("mAnsImage", downloadUri.toString());
//                                                    updates.put("mAnsDisName", user2.getDisplayName());
//                                                }
////                                                updates.put("mDisplayImage",user2.getPhotoUrl());
//                                                fbdatabase.updateChildren(updates);
//
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });

                                    Toast.makeText(AnswerUpload.this, "Upload successful", Toast.LENGTH_LONG).

                                            show();

                                } else {
                                    Toast.makeText(AnswerUpload.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AnswerUpload.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } else {

            SharedPreferences result = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String txt = result.getString("txt", "1");

            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();


            FirebaseUser user2 = firebaseAuth.getCurrentUser();

            answersum = answersum + 1;
//            TotalAns count = new TotalAns(String.valueOf(answersum));
//            DatabaseReference rankings = FirebaseDatabase.getInstance().getReference("rankans").child(user2.getUid());
//            rankings.child(main_ans.getText().toString()).push().setValue(count);
//            final AnswerNumber answerNumber = new AnswerNumber(String.valueOf(answersum));
//            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("answercount").child(user2.getUid());
//            dbr.push().setValue(answerNumber);

            Ans_Upload ans_upload = new Ans_Upload(main_ans.getText().toString(), "", user2.getDisplayName(), "", "");
            DatabaseReference solution = FirebaseDatabase.getInstance().getReference("solv");
            solution.child(txt).push().setValue(ans_upload);

            Sum sum = new Sum(String.valueOf(answersum));
            DatabaseReference rank = FirebaseDatabase.getInstance().getReference("rankans").child(user2.getUid());
            rank.child(main_ans.getText().toString()).push().setValue(sum);


//            Query query = FirebaseDatabase.getInstance().getReference().child("uploads");
//            query.orderByChild("mName").equalTo(txt).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
//                        FirebaseAuth firebaseAuth;
//                        firebaseAuth = FirebaseAuth.getInstance();
//
//
//                        FirebaseUser user2 = firebaseAuth.getCurrentUser();
//
// //                        DatabaseReference fbdatabase = FirebaseDatabase.getInstance().getReference().child("uploads").child(key);
////
////                        Map<String, Object> updates = new HashMap<String, Object>();
////                        updates.put("mAnswer", main_ans.getText().toString());
////                        updates.put("mAnsDisName", user2.getDisplayName());
//////                        updates.put("mAnsDisImg",user2.getPhotoUrl());
////
////                        fbdatabase.updateChildren(updates);
////
////                    }
////                }
////
////                @Override
////                public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                }
////            });


            Toast.makeText(AnswerUpload.this, "Upload successful", Toast.LENGTH_LONG).show();
            openImagesActivity();
        }
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, AnswerPage.class);
        startActivity(intent);
    }

    private void notify_person() {
        Notification notification = new NotificationCompat.Builder(AnswerUpload.this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.mohitpic)
                .setContentTitle("New Comment")
                .setContentText("Your question has been answered")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();
//        System.out.println("Dekh Bhai " + result.getString("comm","0"));

        notificationManager.notify(1, notification);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AnswerPage.class));
    }
}