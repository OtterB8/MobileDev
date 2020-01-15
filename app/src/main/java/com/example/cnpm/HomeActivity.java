package com.example.cnpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnpm.model.APIClient;
import com.example.cnpm.model.Body;
import com.example.cnpm.model.ResultLogout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout,btnProfile;
    Intent getIntent = getIntent();
    String accesstoken, name;
    TextView txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Intent getIntent = getIntent();
        accesstoken = getIntent.getStringExtra("accesstoken");
        name = getIntent.getStringExtra("username");

        txtName = findViewById(R.id.txt_setName);
        txtName.setText(name);

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnProfile = findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail,pass,birthday,createat,phoneNumber;
                mail = getIntent.getStringExtra("mail");
                pass = getIntent.getStringExtra("pass");
                birthday = getIntent.getStringExtra("birthday");
                createat = getIntent.getStringExtra("createat");
                phoneNumber = getIntent.getStringExtra("phoneNumber");
                Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                intent.putExtra("accesstoken",accesstoken);
                intent.putExtra("username",name);
                intent.putExtra("mail",mail);
                intent.putExtra("pass",pass);
                intent.putExtra("birthday",birthday);
                intent.putExtra("createat",createat);
                intent.putExtra("phoneNumber",phoneNumber);
                startActivity(intent);
                HomeActivity.this.finish();
            }
        });

        Button btnEditImage = findViewById(R.id.btn_edit_image);
        btnEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EditImageActivity.class);
                if (accesstoken != null)
                    Log.i("NOTNULLHOME", accesstoken);
                else {
                    Log.i("NULLHOME", "null");
                }
                intent.putExtra("accesstoken", accesstoken);
                startActivity(intent);
            }
        });
    }
    private void logout() {
                Intent intent = getIntent();
                String accesstoken = intent.getStringExtra("accesstoken");
                Retrofit retrofit = APIClient.getClient();
                HerokuService requestApi = retrofit.create(HerokuService.class);
                Call<ResultLogout> call = requestApi.logout(accesstoken);
                call.enqueue(new Callback<ResultLogout>() {
                    @Override
                    public void onResponse(Call<ResultLogout> call, Response<ResultLogout> response) {
                        try {
                            ResultLogout result = response.body();
                            if (result.getMessage().equals("OK")){
                                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                                HomeActivity.this.finish();
                            }
                            else {
                                Toast.makeText(HomeActivity.this, "something were wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(HomeActivity.this, "something were wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultLogout> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
