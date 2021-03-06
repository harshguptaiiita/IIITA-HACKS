package com.betterclever.smartlocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.betterclever.smartlocator.Utils.Item;
import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveNewItem extends AppCompatActivity {

    String mCurrentPhotoPath;
    ImageView itemImageView;
    EditText nameEditText;
    EditText locationEditText;
    static final int REQUEST_TAKE_PHOTO = 1;
    ArrayList<String> list;
    private static final String TAG = SaveNewItem.class.getSimpleName();
    private final ClarifaiClient client = new ClarifaiClient("QwyVMelyYYyB57bUcmAj5pNJ8DFbF2vX_otHSwP1",
            "uTvXRCBEaenYGwAbae56BIcxavMduS6TtJHMnYCZ");

    private File createImageFile() throws IOException {
        // Create an image file name
        Log.d("Smart Locator","I am here");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        Log.d("Photo Path","["+mCurrentPhotoPath+"]");

        if(image != null){
            Log.d("SL","Image is not null");
        }
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
               Log.d("Smart Locator","Unable to create file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d("SL","I am here");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.betterclever.smartlocator.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_new_item);

        itemImageView = (ImageView) findViewById(R.id.item_image);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        locationEditText = (EditText) findViewById(R.id.location_edit_text);

        list = new ArrayList<>();

        dispatchTakePictureIntent();

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = getFilesDir().getPath();
                String databaseName = "myDb";
                String password = "passw0rd";

                WaspDb db = WaspFactory.openOrCreateDatabase(path,databaseName,password);
                WaspHash itemsHash = db.openOrCreateHash("items");

                Item item = new Item();
                String name = nameEditText.getText().toString();
                String location = locationEditText.getText().toString();
                item.setName(name);
                item.setLocation(location);
                item.setLatx("sample");
                item.setLaty("sample");
                item.setImgPath(mCurrentPhotoPath);
                item.setTags(list);

                itemsHash.put(item.getItemName(),item);
                Log.d("Data Stored","Hurray");

                Toast toast = Toast.makeText(getApplicationContext(),"Item Saved Successfully!!",Toast.LENGTH_SHORT);
                toast.show();

                startActivity(new Intent(getBaseContext(),MainActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {

        File imgFile = new File(mCurrentPhotoPath);
        final Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        itemImageView.setImageBitmap(myBitmap);
        itemImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        new AsyncTask<Bitmap, Void, RecognitionResult>() {

            @Override protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                return recognizeBitmap(myBitmap);
            }
            @Override protected void onPostExecute(RecognitionResult result) {

                if(result!=null){
                    if(result.getStatusCode()== RecognitionResult.StatusCode.OK){
                        for(Tag tag : result.getTags()){
                            list.add(tag.getName());
                            Log.d("Recognised Tag",tag.getName() + " " + tag.getProbability());
                        }
                    }
                }
            }
        }.execute(myBitmap);
    }

    private RecognitionResult recognizeBitmap(Bitmap bitmap) {
        try {
            // Scale down the image. This step is optional. However, sending large images over the
            // network is slow and  does not significantly improve recognition performance.
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

            // Compress the image as a JPEG.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] jpeg = out.toByteArray();

            // Send the JPEG to Clarifai and return the result.
            return client.recognize(new RecognitionRequest(jpeg)).get(0);
        } catch (ClarifaiException e) {
            Log.e(TAG, "Clarifai error", e);
            return null;
        }
    }
}
