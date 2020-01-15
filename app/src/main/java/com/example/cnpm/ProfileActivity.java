package com.example.cnpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnpm.model.APIClient;
import com.example.cnpm.model.ResultLogout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {
    EditText editFirstname,editLastname,editMail,editPhone,editBirthday;
    TextView editCreatAt;
    Button btnUpdateProfile,btnBack;
    String accesstoken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        editBirthday = findViewById(R.id.edt_birthdate);
        editFirstname = findViewById(R.id.edt_firstname);
        editLastname= findViewById(R.id.edt_lastname);
        editPhone = findViewById(R.id.edt_phonenumber);
        editMail = findViewById(R.id.edt_email);
        btnBack = findViewById(R.id.btn_back);

        editCreatAt = findViewById(R.id.edt_createAt);
        Intent getIntent = getIntent();
        accesstoken = getIntent.getStringExtra("accesstoken");
        String name = getIntent.getStringExtra("username");
        final Register register = new Register(getIntent.getStringExtra("mail"),
                getIntent.getStringExtra("pass"),
                getIntent.getStringExtra("username"),
                getIntent.getStringExtra("birthday"),
                getIntent.getStringExtra("phoneNumber"),
                name.substring(0,name.lastIndexOf(" ")),
                name.substring(name.lastIndexOf(" ")),
                false,getIntent.getStringExtra("createat"),false);
        editCreatAt.setText(register.getCreateat());
        editMail.setText(register.getMail());
        editPhone.setText(register.getPhoneNumber());
        editBirthday.setText(register.getBirthday());
        editFirstname.setText(register.getFirstname());
        editLastname.setText(register.getLastname());
        btnUpdateProfile = (Button)findViewById(R.id.btn_update_profile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String U_fname,U_lname,U_mail,U_phone,U_birth;
                U_birth = editBirthday.getText().toString().trim();
                U_fname = editFirstname.getText().toString().trim();
                U_lname = editLastname.getText().toString().trim();
                U_phone = editPhone.getText().toString().trim();
                U_mail = editMail.getText().toString().trim();
                if ((U_birth != register.getBirthday()) || (U_fname !=register.getFirstname()) || (U_lname!=register.getLastname()) || (U_mail!=register.getMail())||(U_phone!=register.getPhoneNumber())) {
                    Register register1  = new Register(U_mail,register.getPass(),U_fname+" "+U_lname,U_birth,U_phone,U_fname,U_lname,false,register.getCreateat(),false);
                    Update(register1);
                }
                else {

                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                intent.putExtra("accesstoken",accesstoken);
                intent.putExtra("username",register.getUsername());
                intent.putExtra("mail",register.getMail());
                intent.putExtra("pass",register.getPass());
                intent.putExtra("birthday",register.getBirthday());
                intent.putExtra("createat",register.getCreateat());
                intent.putExtra("phoneNumber",register.getPhoneNumber());
                startActivity(intent);
                ProfileActivity.this.finish();
            }
        });
    }

    private void Update(final Register register1) {
        Retrofit retrofit = APIClient.getClient();
        HerokuService requestApi = retrofit.create(HerokuService.class);
        Call<ResultLogout> call = requestApi.update(accesstoken,register1);
        call.enqueue(new Callback<ResultLogout>() {
            @Override
            public void onResponse(Call<ResultLogout> call, Response<ResultLogout> response) {
                try {
                    ResultLogout result = response.body();
                    if (result.getMessage().equals("created")) {
                        Toast.makeText(ProfileActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                        intent.putExtra("accesstoken",accesstoken);
                        intent.putExtra("username",register1.getUsername());
                        intent.putExtra("mail",register1.getMail());
                        intent.putExtra("pass",register1.getPass());
                        intent.putExtra("birthday",register1.getBirthday());
                        intent.putExtra("createat",register1.getCreateat());
                        intent.putExtra("phoneNumber",register1.getPhoneNumber());
                        startActivity(intent);
                        ProfileActivity.this.finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "something were wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResultLogout> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
