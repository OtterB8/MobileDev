package com.example.cnpm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.cnpm.model.APIClient;
import com.example.cnpm.model.ResultUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditImageActivity extends AppCompatActivity {
    private final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public final int GALLERY_REQUEST_CODE = 1301;
    public final int MY_CAMERA_PERMISSION_CODE = 1310;
    public final int CAMERA_REQUEST_CODE = 1031;
//    public static ResponseBody responseBody;
    public static Bitmap bitmap;

    private ImageView imageView;
    private LinearLayout toCartoonWrapper;
    private Uri selectedImage;
    private String mCameraFileName;
    private ProgressBar progressBar;
    private String fileName;
    private File file;
    private String style = "mosaic";
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        this.imageView = findViewById(R.id.image_view);
        this.toCartoonWrapper = findViewById(R.id.to_cartoon_wrapper);

        addProgressBar(android.R.attr.progressBarStyleLarge);
        hideProgressBar();

        Toolbar toolbar = findViewById(R.id.toolbar_edit_image);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button pickGalleryBtn = findViewById(R.id.pick_gallery);
        pickGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        Button takePhotoBtn = findViewById(R.id.take_photo);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    takePhoto();
                }
            }
        });

        Button doMagicBtn = findViewById(R.id.to_cartoon);
        doMagicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enhancement_style, menu);
        return true;
    }

    private void changeStyle(String style) {
        this.style = style;
        Toast.makeText(this, "Enhancement style: " + style, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("RESTORE", savedInstanceState.getString(ACCESS_TOKEN));
        this.accessToken = savedInstanceState.getString(ACCESS_TOKEN);
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ACCESS_TOKEN, this.accessToken);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Log.i("BACKBUTTON", "pressed");
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("accesstoken", this.accessToken);
                startActivity(intent);
                finish();
                break;
            case R.id.action_mosaic:
                this.changeStyle("mosaic");
                break;
            case R.id.action_candy:
                this.changeStyle("candy");
                break;
            case R.id.action_rain_princess:
                this.changeStyle("rain_princess");
                break;
            case R.id.action_udnie:
                this.changeStyle("udnie");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Intent intent = getIntent();
        this.accessToken = intent.getStringExtra("accesstoken");
        if (this.accessToken != null) {
            Log.i("NOTNULL", this.accessToken);
        } else {
            Log.i("NULL", "NULL");
        }
    }

    private boolean checkAndRequestPermissions() {
        int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_CAMERA_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void addProgressBar(int type) {
        progressBar = new ProgressBar(this, null, type);

        RelativeLayout layout = findViewById(R.id.display);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
    }

    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        return "_" + timeStamp + "_.png";
    }

    private MultipartBody.Part createFormData(File file) {
        RequestBody requestFile =
                RequestBody.create(file, MediaType.parse("image/*"));

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("Image", file.getName(), requestFile);

        return body;
    }

    private void processImage() {
        if (this.accessToken != null)
            Log.i("TOKEN", this.accessToken);
        showProgressBar();

        Retrofit retrofit = APIClient.getClient();
        HerokuService requestApi = retrofit.create(HerokuService.class);

        MultipartBody.Part formData = createFormData(file);
        fileName = createFileName();
        Call<ResponseBody> call = requestApi.enhanceImage(this.accessToken, fileName, this.style, formData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    boolean fileDownloaded = downloadImage(response.body());
                    hideProgressBar();
                    if (!fileDownloaded) {
                        Toast.makeText(EditImageActivity.this, "Cound not process the image", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(EditImageActivity.this, EnhancedImageActivity.class);
                        intent.putExtra("accesstoken", EditImageActivity.this.accessToken);
                        startActivity(intent);
                    }
//                    if (result == null) {
//                        Toast.makeText(EditImageActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                    } else
//                    if (result.getMessage().equals("created")){
//                        Toast.makeText(EditImageActivity.this, "success", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(EditImageActivity.this, EnhancedImageActivity.class);
//                        intent.putExtra(EnhancedImageActivity.CARTOON_IMAGE_NAME, fileName);
//                        intent.putExtra("accesstoken", EditImageActivity.this.accessToken);
//                        startActivity(intent);
//                    }
//                    else {
//                        Toast.makeText(EditImageActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
                }
                catch (Exception e){
                    Toast.makeText(EditImageActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("FAILURE", t.getMessage());
                hideProgressBar();
                Toast.makeText(EditImageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean downloadImage(ResponseBody body) {
        try {
            InputStream in = null;
            FileOutputStream out = null;
            String filePath = getExternalFilesDir(null) + File.separator + createFileName();

            try {
                in = body.byteStream();
                out = new FileOutputStream(filePath);
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            catch (IOException e) {
                Log.e("DownloadIOException",e.toString());
                return false;
            }
            finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            bitmap = BitmapFactory.decodeFile(filePath);

            return true;

        } catch (IOException e) {
            Log.d("DownloadIOException",e.toString());
            return false;
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            Map<String, Integer> perms = new HashMap();
            // Initialize the map with both permissions
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; ++i)
                    perms.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera and read/write permission granted", Toast.LENGTH_LONG).show();
                    // process the normal flow
                    takePhoto();
                } else {//else any one or both the permissions are not granted
                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                    //shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialogOK("Camera and Read/Write Permission required for this feature",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                break;
                                        }
                                    }
                                });
                    }
                    //permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        Toast.makeText(this, "Go to settings and enable camera and write permissions", Toast.LENGTH_LONG).show();
                        //proceed with logic by disabling the related features or quit the app.
                    }
                }
            }
        }
    }

    private void takePhoto() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String newPicFile = createFileName();

        String outPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + newPicFile;
        File outFile = new File(outPath);

        this.mCameraFileName = outFile.toString();
        Uri outuri = Uri.fromFile(outFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    if (data != null) {
                        selectedImage = data.getData();
                        imageView.setImageURI(selectedImage);
                        toCartoonWrapper.setVisibility(View.VISIBLE);
                    } else {
                        return;
                    }
                    try {
                        this.file = new File(getCacheDir(), "cacheFileAppeal.srl");
                        InputStream input = getContentResolver().openInputStream(selectedImage);
                        try (OutputStream output = new FileOutputStream(this.file)){
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;
                            while ((read = input.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }

                            output.flush();
                        } catch (FileNotFoundException e) {
                            Log.e("FileNotFoundException", e.getMessage());
                        } catch (IOException e) {
                            Log.e("IOException", e.getMessage());
                        }
                    } catch (FileNotFoundException e) {
                        Log.e("FileNotFoundException", e.getMessage());
                    }
                    break;
                case CAMERA_REQUEST_CODE:
                    selectedImage = null;
                    if (data != null) {
                        selectedImage = data.getData();
                        imageView.setImageURI(selectedImage);
                        toCartoonWrapper.setVisibility(View.VISIBLE);
                    }
                    if (selectedImage == null && mCameraFileName != null) {
                        selectedImage = Uri.fromFile(new File(mCameraFileName));
                        imageView.setImageURI(selectedImage);
                        toCartoonWrapper.setVisibility(View.VISIBLE);
                    }
                    try {
                        this.file = new File(getCacheDir(), "cacheFileAppeal.srl");
                        InputStream input = getContentResolver().openInputStream(selectedImage);
                        try (OutputStream output = new FileOutputStream(this.file)){
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;
                            while ((read = input.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }

                            output.flush();
                        } catch (FileNotFoundException e) {
                            Log.e("FileNotFoundException", e.getMessage());
                        } catch (IOException e) {
                            Log.e("IOException", e.getMessage());
                        }
                    } catch (FileNotFoundException e) {
                        Log.e("FileNotFoundException", e.getMessage());
                    }
                    break;
            }
    }
}
