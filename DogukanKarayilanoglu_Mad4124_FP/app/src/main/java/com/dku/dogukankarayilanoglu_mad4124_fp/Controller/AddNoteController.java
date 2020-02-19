package com.dku.dogukankarayilanoglu_mad4124_fp.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import com.dku.dogukankarayilanoglu_mad4124_fp.Database.IdDatabase;
import com.dku.dogukankarayilanoglu_mad4124_fp.Database.ItemDatabase;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Category;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Id;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Item;
import com.dku.dogukankarayilanoglu_mad4124_fp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;

public class AddNoteController extends AppCompatActivity {

    Button addNote;
    Button record;
    Button playRecord;
    Button camera;
    Button library;

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
    final IdDatabase idDatabase = new IdDatabase(this);
    ArrayList<Id> idArrayList ;
    final ItemDatabase itemDatabase = new ItemDatabase(this);
    private int itemId;
    private Id idObject;


    private static String RECORDED_FILE;

    //USER LOCATION
    private FusedLocationProviderClient mFusedLocationClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    public final int RADIUS = 1200;
    double latitude ;
    double longitude;


    Category category;
    ArrayList<Item> arrayList;
    boolean isCategorySelected = false ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note_controller);


        idArrayList=idDatabase.loadIds();


        categorySelector = findViewById(R.id.spinner_category_selector);




        //Add fake category to show...
        categories = categoryDatabase.loadCategories();
        Category categoryFake = new Category();
        categoryFake.setCategoryName("Select Category");
        categories.add(0,categoryFake);

        ArrayAdapter categoryAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,categories);

        categorySelector.setAdapter(categoryAdapter);

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

        //If first id initialized before , get it and use
        //Else , initialize the new one.
        //Increment while saving item and update id.
        Log.d("tag", "onCreateidarraylistsize: "+idArrayList.size());
        if(idArrayList.size()>0){
           idObject= idArrayList.get(0);

            Log.d("tag", "onCreateidarraylgetid: "+idArrayList.get(0).getId());
            itemId = idObject.getId() ;
        }

        else {
            idObject = new Id();
            idObject.setId(0);

            idDatabase.addId(idObject);
            itemId=idObject.getId();
        }



        camPathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + itemId+".jpeg";

        addNote = findViewById(R.id.btn_add_note);
        record = findViewById(R.id.btn_record);
        playRecord = findViewById(R.id.btn_play);
        camera = findViewById(R.id.btn_camera);
        library = findViewById(R.id.btn_library);

        title = findViewById(R.id.txt_title);
        content = findViewById(R.id.txtContent);


        imageView = findViewById(R.id.img_photo);

        //Location permissions and or get
        getUserLocation();
        if (!checkPermissions())
            requestPermissions();

        else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        //Audio...
        RECORDED_FILE = "/"+itemId+".3gp";
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
                        Log.d("tagpath", "onClick: "+pathSave);
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
                    Toast.makeText(AddNoteController.this,"Camera is not working while running on emulator",Toast.LENGTH_SHORT).show();
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

                    Item item = new Item();

                    item.setTitle(title.getText().toString());

                    if(content.getText() != null){
                        item.setContent(content.getText().toString());
                    }
                    else {
                        item.setContent("");
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date date = new Date();
                    item.setDateAdded(formatter.format(date));

                    item.setLatitude(String.valueOf(latitude));
                    item.setLongitude(String.valueOf(longitude));

                    Log.d("tag", "onCreateitemidaddnotecontroller: "+itemId);

                    item.setId(itemId);

                    itemDatabase.addItem(item);

                    //increment id by one
                    idObject.setId(itemId+1);
                    Log.d("tag", "onCreateitemidaddnotidobjectgetid: "+idObject.getId());
                    //update id
                    idDatabase.updateId(itemId,idObject);

                    //Add to category
                    arrayList.add(item);

                    Gson gson = new Gson();
                    String input = gson.toJson(arrayList);
                    category.setArrayOfItems(input);

                    categoryDatabase.updateCategory(category);


                    Toast.makeText(AddNoteController.this,"New note added to " + category.getCategoryName() ,Toast.LENGTH_SHORT).show();

                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                    mFusedLocationClient=null;

                    finish();

                }


            }
        });
    }
    private void getUserLocation() {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }
            }


        };

    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE : {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    getUserLocation();
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            }
        }
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

    //to stop location services if item is not saved and pushes only back button
    @Override
    public void onBackPressed() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        mFusedLocationClient=null;
        finish();
    }

    //Cam result...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Camera
        if (requestCode == REQUEST_PERMISSION_CAMERA && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            File outFile = new File(Environment.getExternalStorageDirectory(), "/"+itemId+".jpeg");
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


            File outFile = new File(Environment.getExternalStorageDirectory(), "/"+itemId+".jpeg");

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
