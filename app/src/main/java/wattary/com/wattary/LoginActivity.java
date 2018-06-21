package wattary.com.wattary;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jackandphantom.circularprogressbar.CircleProgressbar;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static maes.tech.intentanim.CustomIntent.customType;

/**
 * Created by mosabahmed55 on 03/03/2018.
 */
public class LoginActivity extends AppCompatActivity {
    private Button CaptureLoginBu;
    Button GaleryBu;
    private Uri mImageUri;
    private CircleProgressbar mProgressBar;


    private static final int PICK_IMAGE_REQUEST = 2;

    private static final String TAG = "";

    private CircleImageView Login_ImageView;


    private Uri mCropImageUri;


    public Uri file;
    private String generatedFilePath;



    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        builder.detectFileUriExposure();

        CaptureLoginBu = (Button) findViewById(R.id.CaptureLoginBu);
        firebaseDatabase = FirebaseDatabase.getInstance();
        GaleryBu=(Button)findViewById(R.id.Galery) ;
        mProgressBar = findViewById(R.id.progress_bar);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        ref = firebaseDatabase.getReference("ProfileInfo");
        StorageReference mStorageRef;

        Login_ImageView = findViewById(R.id.CropImageView);
          //animation
        customType(LoginActivity.this,"left-to-right");

        CaptureLoginBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                   // mProgressBar.setVisibility(View.VISIBLE);
                   // Login_ImageView.setVisibility(View.INVISIBLE);
                    //mProgressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    if (mUploadTask.isComplete())
                    {
                        mUploadTask=null;
                        mCropImageUri=null;
                        mImageUri=null;
                    }
        }
        else  {
                   // CropImage.activity()
                          //  .setGuidelines(CropImageView.Guidelines.ON)
                          //  .setMinCropResultSize(512,512)
                          //  .setAspectRatio(1, 1)
                           // .start(LoginActivity.this);
                //    BringImageCapture();
                    startActivityForResult(getPickImageChooserIntent(), 200);


      }

            }
        });
        GaleryBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                   // mProgressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    if (mUploadTask.isComplete())
                    {
                        mUploadTask=null;
                        mCropImageUri=null;
                        mImageUri=null;
                    }
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                  openFileChooser();


                }
            }
        });

    }


   public void LoginCheck(View view)
    {
        Intent intent =new Intent(LoginActivity.this,VoiceActivity.class);
        startActivity(intent);
    }


    public void onCropImage()
    {
        if (mImageUri!=null) {
            Uri uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(mImageUri, uri).asSquare().start(LoginActivity.this);
            mImageUri = uri;
        }
        else if(mCropImageUri!=null)
        {
            Uri uri2 = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(mCropImageUri, uri2).asSquare().start(LoginActivity.this);
            mCropImageUri = uri2;
        }
    }



    public Intent getPickImageChooserIntent() {

// Determine Uri of camera image to  save.
        Uri outputFileUri =  getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager =  getPackageManager();

// collect all camera intents
        Intent captureIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam =  packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new  Intent(captureIntent);
            intent.setComponent(new  ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

// collect all gallery intents


// the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent =  allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if  (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity"))  {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

// Create a chooser from the main  intent
        Intent chooserIntent =  Intent.createChooser(mainIntent, "Select source");

// Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,  allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new  File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;

    }

    /**
     * Get the URI of the selected image from  {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera  and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ?  getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }




    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void submit(){

        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(String.valueOf(System.currentTimeMillis())
                     //"." + getFileExtension(mImageUri)
            );

            mUploadTask= fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    mProgressBar.setProgress(0);

                                }
                            }, 500);


                            Toast.makeText(LoginActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            //Getting the Url Of the Image
                            Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                            generatedFilePath = downloadUri.toString()+".jpg";
                            if(generatedFilePath!=null)
                            { Log.d("file",generatedFilePath);
                                Send();
                            }else
                            {
                                Toast.makeText(LoginActivity.this,"Try Agin",Toast.LENGTH_SHORT);
                            }
                          //  Toast.makeText(LoginActivity.this,generatedFilePath,Toast.LENGTH_LONG).show();
                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });

        }
        else if (mImageUri ==null){



      StorageReference storageReference = mStorageRef.child(String.valueOf(System.currentTimeMillis())
              //+".jpg"
              );

        //StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
           mUploadTask=  storageReference.putFile(getCaptureImageOutputUri())
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setProgress(0);
                }
            }, 500);

            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            Toast.makeText(LoginActivity.this, "uploaded", Toast.LENGTH_SHORT).show();


            //Getting the Url Of the Image


            Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

            generatedFilePath = downloadUri.toString()+".jpg";
            Log.d("file",generatedFilePath);
            if(generatedFilePath!=null)
            {
                Send();
            }else
            {
                Toast.makeText(LoginActivity.this,"Try Agin",Toast.LENGTH_SHORT).show();
            }
           // Toast.makeText(LoginActivity.this,generatedFilePath,Toast.LENGTH_LONG).show();
        }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,"failed",Toast.LENGTH_LONG).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            })
            ;
        }
         else {
        Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
    }}




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST&&requestCode!=Crop.REQUEST_CROP && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri  = data.getData();
            onCropImage();

           // Picasso.with(this).load(mImageUri).into(Login_ImageView);
           // submit();
        }
        else if (resultCode==RESULT_OK&requestCode==Crop.REQUEST_CROP)
        {
            outputCroppedImage(resultCode,data);
        }
        else if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = getPickImageResultUri(data);
                // For API >= 23 we need to check specifically that we have permissions to read external storage,
                // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
                boolean requirePermissions = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        isUriRequiresPermissions(imageUri)) {

                    // request permissions and handle the result in onRequestPermissionsResult()
                    requirePermissions = true;

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }

                if (!requirePermissions&&requestCode!=Crop.REQUEST_CROP)
                {
                    //Login_ImageView.setImageUriAsync(imageUri);
                   // Login_ImageView.setImageURI(imageUri);
                   // submit();
                    mCropImageUri = imageUri;
                    onCropImage();
                }
                else if (resultCode==RESULT_OK&requestCode==Crop.REQUEST_CROP)
                {
                    outputCroppedImage(resultCode,data);
                }

            }
        else {
            return;
        }
        }

    public void  outputCroppedImage(int code, Intent result) {
        if (code == RESULT_OK) {
            Login_ImageView.setImageURI(Crop.getOutput(result));
            submit();
        }

    }


    public void Send()
    {
        mUploadTask=null;
        mCropImageUri=null;
        mImageUri=null;
        String ServerUrl="http://35.228.93.235:5000/signin";

        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("PhotoUrl", generatedFilePath);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,ServerUrl , new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast.makeText(LoginActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                        Log.v("respo",response.toString());
                        //Toast.makeText(LoginActivity.this,"is Done ",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG,error.toString());
                Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("PhotoUrl", "application/json; charset=utf-8");
                return headers;
            }



        };

        jsonObjReq.setTag(TAG);

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);


        // Adding request to request queue
        queue.add(jsonObjReq);

        // Cancelling request
    /* if (queue!= null) {
    queue.cancelAll(TAG);
    } */

    }
}






