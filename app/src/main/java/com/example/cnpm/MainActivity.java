package com.example.cnpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnpm.model.APIClient;
import com.example.cnpm.model.ResultLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    EditText editEmail,editPass;
    Button btnLogin;
    TextView txtReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = (EditText)findViewById(R.id.edt_email);
        editPass = (EditText)findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        txtReg = (TextView) findViewById(R.id.txt_register);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                MainActivity.this.finish();
            }
        });
    }
    private void Login(){
        String mail = editEmail.getText().toString().trim();
        String pass = editPass.getText().toString().trim();
        final User user = new User(mail,pass);
        if (mail.isEmpty() && pass.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please enter enough information", Toast.LENGTH_SHORT).show();
        }
        else {
            Retrofit retrofit = APIClient.getClient();
            HerokuService requestApi = retrofit.create(HerokuService.class);
            Call<ResultLogin> call = requestApi.login(user);
            call.enqueue(new Callback<ResultLogin>() {
                @Override
                public void onResponse(Call<ResultLogin> call, Response<ResultLogin> response) {
                    try {
                        ResultLogin result = response.body();
                        Log.i("response", response.message());
                        Log.i("resultmessage", result.getMessage());
                        if (result.getBody() != null) {
                            Log.i("resultbody", result.getBody().toString());
                            if (result.getBody().getTokens() != null) {
                                Log.i("responsetoken", result.getBody().getTokens());
                            } else {
                                Log.i("responsetokennull", "tokennull");
                            }
                        } else {
                            Log.i("resultbodynull", "result");
                        }

                        if (result.getMessage().equals("OK")){
                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            intent.putExtra("accesstoken",result.getBody().getTokens());
                            intent.putExtra("username",result.getBody().getUsername());
                            intent.putExtra("mail",user.getMail());
                            intent.putExtra("pass",user.getPass());
                            intent.putExtra("birthday",result.getBody().getBirthday());
                            intent.putExtra("createat",result.getBody().getCreateat());
                            intent.putExtra("phoneNumber",result.getBody().getPhoneNumber());
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Mail or Passwork is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e){
                        Toast.makeText(MainActivity.this, "something is wrong", Toast.LENGTH_SHORT).show();
                        Log.i("Exeption", e.getMessage());
                    }
                    //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(Call<ResultLogin> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Can not connect to server", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}