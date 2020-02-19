package com.dku.dogukankarayilanoglu_mad4124_fp.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dku.dogukankarayilanoglu_mad4124_fp.Database.CategoryDatabase;
import com.dku.dogukankarayilanoglu_mad4124_fp.Database.ItemDatabase;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Category;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Item;
import com.dku.dogukankarayilanoglu_mad4124_fp.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;

public class NoteDetailAndUpdateController extends AppCompatActivity {

    Button addNote;
    Button record;
    Button playRecord;
    Button camera;
    Button library;
    Button location;

    EditText title;
    EditText content;

    Spinner categorySelector;

    String pathSave = "";
    String camPathSave ="";

    ImageView imageView;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    boolean isMediaRecorderRecording = false ;
    boolean isMediaPlayerPlaying = false ;

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSION_CODE_AUDIO = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 3;

    ArrayList<Category> categories;
    final CategoryDatabase categoryDatabase = new CategoryDatabase(this);
    final ItemDatabase itemDatabase = new ItemDatabase(this);

    private static String RECORDED_FILE;

    Category category;
    ArrayList<Item> arrayList;
    boolean isCategorySelected = true ;

    int incomingItemId;
    int incomingCategoryId;
    int incomingCategoryIndex;

    Category initialCategory ;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail_and_update_controller);

        location = findViewById(R.id.btn_locationD);

        incomingCategoryId = getIntent().getExtras().getInt("incomingCategoryId");
        initialCategory = categoryDatabase.getCategory(incomingCategoryId);

        incomingItemId = getIntent().getExtras().getInt("incomingItemId");
        item = itemDatabase.getItem(incomingItemId);

        incomingCategoryIndex=getIntent().getExtras().getInt("incomingCategoryIndex");


        categorySelector = findViewById(R.id.spinner_category_selectorD);

        //Add fake category to show...
        categories = categoryDatabase.loadCategories();

        Category categoryFake = new Category();
        categoryFake.setCategoryName("Select Category");
        categories.add(0,categoryFake);

        ArrayAdapter categoryAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,categories);


        categorySelector.setAdapter(categoryAdapter);
        categorySelector.setSelection(incomingCategoryIndex+1,false);

        categorySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0 ){
                    isCategorySelected=false;
                }

                else{

                    category = categories.get(i);
                    arrayList = category.getArrayList();
                    isCategorySelected = true;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {



            }
        });

        //incoming item id will assigned
        camPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + item.getId()+".jpeg";

        addNote = findViewById(R.id.btn_add_noteD);
        record = findViewById(R.id.btn_recordD);
        playRecord = findViewById(R.id.btn_playD);
        camera = findViewById(R.id.btn_cameraD);
        library = findViewById(R.id.btn_libraryD);

        title = findViewById(R.id.txt_titleD);
        content = findViewById(R.id.txtContentD);


        imageView = findViewById(R.id.img_photoD);

        //To retrieve image from storage...
            File file = new File(camPathSave);
            Bitmap photo = BitmapFactory.decodeFile(String.valueOf(file));
            if(photo != null){
                imageView.setImageBitmap(photo);
            }



         title.setText(item.getTitle());
         content.setText(item.getContent());


        //Audio...
        RECORDED_FILE = "/"+item.getId()+".3gp";
        pathSave = getExternalStorageDirectory().getAbsolutePath() + RECORDED_FILE;
        audioManager =(AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume (AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);

        if(!checkAudioPermissionDevice()){
            requestAudioPermission();
        }

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkAudioPermissionDevice()){

                    //Record if it is not recording...
                    if(isMediaRecorderRecording == false){

                        if(isMediaPlayerPlaying){
                            mediaPlayer.stop();
                            playRecord.setBackgroundResource(R.drawable.recordplay);
                        }

                        pathSave = getExternalStorageDirectory().getAbsolutePath() + RECORDED_FILE;
                        setUpMediaRecorder();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        isMediaRecorderRecording = true;
                        record.setBackgroundResource(R.drawable.recordstop);

                    }

                    //Stop recording
                    else{
                        mediaRecorder.stop();
                        isMediaRecorderRecording = false ;

                        record.setBackgroundResource(R.drawable.recordstart);
                    }



                }

                else {
                    requestAudioPermission();
                }

            }
        });

        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if media recorder is recording
                if(isMediaRecorderRecording){
                    mediaRecorder.stop();
                    isMediaRecorderRecording = false ;

                    record.setBackgroundResource(R.drawable.recordstart);
                }

                //if media player not playing , then play the media
                if(isMediaPlayerPlaying == false){
                    mediaPlayer = new MediaPlayer();

                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                        playRecord.setBackgroundResource(R.drawable.recordstopplaying);
                        isMediaPlayerPlaying=true;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            playRecord.setBackgroundResource(R.drawable.recordplay);
                            isMediaPlayerPlaying=false;
                        }
                    });

                    mediaPlayer.start();

                }

                //if it is playing , stop the media

                else {
                    mediaPlayer.stop();
                    playRecord.setBackgroundResource(R.drawable.recordplay);
                    isMediaPlayerPlaying=false;

                }


            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isAndroidEmulator()){
                    Toast.makeText(NoteDetailAndUpdateController.this,"Camera is not working while running on emulator",Toast.LENGTH_SHORT).show();
                }

                else {

                    if(checkCameraPermissionDevice()){
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

                        //Save Location
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, camPathSave);
                        startActivityForResult(intent, REQUEST_PERMISSION_CAMERA);



                    }

                    else{
                        requestCameraPermission();
                    }
                }

            }
        });

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 0);

            }
        });



        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("tag", "onClickselected: "+isCategorySelected);

                if(!title.getText().toString().equals("") && isCategorySelected == true){
                    //Add note

                    item.setTitle(title.getText().toString());

                    if(content.getText() != null){
                        item.setContent(content.getText().toString());
                    }
                    else {
                        item.setContent("");
                    }


                    itemDatabase.updateItem(item);


                    //if saved category and selected category are not equal
                    //Then update the categories...
                    if(category != null  && !initialCategory.getCategoryName().equals(category.getCategoryName())){
                        ArrayList<Item> items = initialCategory.getArrayList();
                        items.remove(item);

                        if (items.size() == 1) {
                            items.remove(0);
                        }

                        else{
                            for(int i = 0 ; i<items.size(); i++){
                                if(items.get(i).getId() == item.getId()){
                                    items.remove(i);
                                }
                            }
                        }




                        Gson gson = new Gson();
                        String inputUpdate = gson.toJson(items);
                        initialCategory.setArrayOfItems(inputUpdate);
                        categoryDatabase.updateCategory(initialCategory);

                        arrayList.add(item);


                        String input = gson.toJson(arrayList);
                        category.setArrayOfItems(input);

                        categoryDatabase.updateCategory(category);
                    }

                    else{

                        ArrayList<Item> items = initialCategory.getArrayList();
                        for(int i = 0 ; i < items.size() ; i++){
                            if(items.get(i).getId() == item.getId()){
                                items.remove(i);
                            }

                            items.add(item);
                            Gson gson = new Gson();
                            String inputUpdate = gson.toJson(items);
                            initialCategory.setArrayOfItems(inputUpdate);
                            categoryDatabase.updateCategory(initialCategory);

                        }

                    }




                    Toast.makeText(NoteDetailAndUpdateController.this,"Note updated"  ,Toast.LENGTH_SHORT).show();

                    finish();

                }


            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteDetailAndUpdateController.this,ShowOnMapsActivityController.class);
                intent.putExtra("lat",item.getLatitude());
                intent.putExtra("lng",item.getLongitude());
                startActivity(intent);
            }
        });


    }



    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);

    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE_AUDIO);
    }

    private boolean checkAudioPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, REQUEST_PERMISSION_CAMERA);
    }

    private boolean checkCameraPermissionDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera_result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                camera_result == PackageManager.PERMISSION_GRANTED;
    }

    //Cam result...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Camera
        if (requestCode == REQUEST_PERMISSION_CAMERA && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            File outFile = new File(Environment.getExternalStorageDirectory(), "/"+item.getId()+".jpeg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                imageView.setImageBitmap(photo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }




        }

        //Library...
        if(requestCode == 0 && resultCode == RESULT_OK){

            Uri uri = data.getData();
            Bitmap photo = null;
            try {
                photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            File outFile = new File(Environment.getExternalStorageDirectory(), "/"+item.getId()+".jpeg");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                //imageView.setImageBitmap(photo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(photo);


        }
    }

    public static boolean isAndroidEmulator() {
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        boolean isEmulator = false;

        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
        }

        return isEmulator;
    }
}
