package com.example.cnpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.cnpm.model.APIClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EnhancedImageActivity extends AppCompatActivity {
    private final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final int PERMISSION_CODE = 1015;
    public static final String CARTOON_IMAGE_NAME = "CARTOON_IMAGE_NAME";
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private String filePath, accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_image);

        addProgressBar(android.R.attr.progressBarStyleLarge);

        Toolbar toolbar = findViewById(R.id.toolbar_edit_image);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    saveImage();
                }
            }
        });

        Button shareBtn = findViewById(R.id.share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EnhancedImageActivity.this, "Save it and share yourself", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        String imgName = intent.getStringExtra(CARTOON_IMAGE_NAME);
        this.accessToken = intent.getStringExtra("accesstoken");

        this.getCartoonImage(imgName);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        this.accessToken = savedInstanceState.getString(ACCESS_TOKEN);
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ACCESS_TOKEN, this.accessToken);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    private void saveImage() {
        if (EditImageActivity.bitmap != null) {
            MediaStore.Images.Media.insertImage(getContentResolver(), EditImageActivity.bitmap, filePath, filePath);
            Toast.makeText(EnhancedImageActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EnhancedImageActivity.this, "Image no exist", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkAndRequestPermissions() {
        int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList();
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Log.i("BACKBUTTON", "pressed");
                Intent intent = new Intent(this, EditImageActivity.class);
                intent.putExtra("accesstoken", this.accessToken);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
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
        if (requestCode == PERMISSION_CODE) {
            Map<String, Integer> perms = new HashMap();
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; ++i)
                    perms.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "read/write permission granted", Toast.LENGTH_LONG).show();
                    // process the normal flow
                    saveImage();
                } else {//else any one or both the permissions are not granted
                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                    //shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                        Toast.makeText(this, "Go to settings and enable write permissions", Toast.LENGTH_LONG).show();
                        //proceed with logic by disabling the related features or quit the app.
                    }
                }
            }
        }
    }

    private void getCartoonImage(String imgName) {
//        showProgressBar();

        int width, height;
        ImageView image = findViewById(R.id.cartoon_view);
        width = EditImageActivity.bitmap.getWidth();
        height = EditImageActivity.bitmap.getHeight();
        Bitmap bMap2 = Bitmap.createScaledBitmap(EditImageActivity.bitmap, width, height, false);
        image.setImageBitmap(bMap2);

//        Retrofit retrofit = APIClient.getClient();
//        HerokuService requestApi = retrofit.create(HerokuService.class);
//
//        Call<ResponseBody> call = requestApi.downloadImage(this.accessToken, imgName);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                hideProgressBar();
//                try {
//                    boolean fileDownloaded = downloadImage(response.body());
//                    if (fileDownloaded == false) {
//                        Toast.makeText(EnhancedImageActivity.this, "Cound not get the image", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    Log.e("onResponse", "There is an error");
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i("FAILURE", t.getMessage());
//                hideProgressBar();
//                Toast.makeText(EnhancedImageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return "_" + timeStamp + "_.jpg";
    }

    private class DownloadTask extends AsyncTask<ResponseBody, Integer, Void> {
        protected Void doInBackground(ResponseBody... body) {
            EnhancedImageActivity.this.downloadImage(body[0]);
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }

    private boolean downloadImage(ResponseBody body) {
        try {
            InputStream in = null;
            FileOutputStream out = null;
            filePath = getExternalFilesDir(null) + File.separator + createFileName();

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

            int width, height;
            ImageView image = findViewById(R.id.cartoon_view);
            bitmap = BitmapFactory.decodeFile(filePath);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            Bitmap bMap2 = Bitmap.createScaledBitmap(bitmap, width, height, false);
            image.setImageBitmap(bMap2);

            return true;

        } catch (IOException e) {
            Log.d("DownloadIOException",e.toString());
            return false;
        }
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

        hideProgressBar();
    }
}