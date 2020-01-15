package com.example.cnpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cnpm.model.APIClient;
import com.example.cnpm.model.ResultLogout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    EditText editLastName,editFirstName,editMail,editPass,editConfirm,editPhonenumber,editDay,editMonth,editYear;
    Button btnReg,btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editLastName = (EditText)findViewById(R.id.edt_lastname);
        editFirstName = (EditText)findViewById(R.id.edt_firstname);
        editMail = (EditText)findViewById(R.id.edt_email);
        editDay = (EditText) findViewById(R.id.edt_day);
        editMonth = (EditText) findViewById(R.id.edt_month);
        editYear = (EditText) findViewById(R.id.edt_year);
        editPass = (EditText)findViewById(R.id.edt_password);
        editConfirm = (EditText)findViewById(R.id.edt_confirm);
        editPhonenumber = (EditText)findViewById(R.id.edt_phonenumber);
        btnReg = (Button) findViewById(R.id.btn_register);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                RegisterActivity.this.finish();
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register() {
        String lastName,firstName,mail,pass,confirm,phone;
        lastName = editLastName.getText().toString().trim();
        firstName = editFirstName.getText().toString().trim();
        mail = editMail.getText().toString().trim();
        pass = editPass.getText().toString().trim();
        phone = editPhonenumber.getText().toString().trim();
        confirm = editConfirm.getText().toString().trim();
        int day,month,year;

        if ((lastName.isEmpty() || firstName.isEmpty() || mail.isEmpty() || pass.isEmpty()|| confirm.isEmpty()|| phone.isEmpty()||editDay.getText().toString().trim().isEmpty()||editMonth.getText().toString().trim().isEmpty()||editYear.getText().toString().trim().isEmpty())){
            Toast.makeText(RegisterActivity.this, "Please enter enough information", Toast.LENGTH_SHORT).show();
        }
        else if (!pass.equals(confirm)){
            Toast.makeText(RegisterActivity.this, "Confirm Password is wrong", Toast.LENGTH_SHORT).show();
        }
        else {
            day = Integer.parseInt(editDay.getText().toString().trim());
            month = Integer.parseInt(editMonth.getText().toString().trim());
            year = Integer.parseInt(editYear.getText().toString().trim());
            if (day <= 0 || month <= 0 || year <= 0 || day > 31 || month > 12) {
                Toast.makeText(this, "Error birthday", Toast.LENGTH_SHORT).show();
            } else {
                Retrofit retrofit = APIClient.getClient();
                HerokuService requestApi = retrofit.create(HerokuService.class);
                String birthday = year + "/" + month + "/" + day;
                Register register = new Register(mail, pass, firstName + " " + lastName, birthday, phone, firstName, lastName, false, "", false);
                Call<ResultLogout> call = requestApi.signup(register);
                call.enqueue(new Callback<ResultLogout>() {
                    @Override
                    public void onResponse(Call<ResultLogout> call, Response<ResultLogout> response) {
                        try {
                            String result = response.message();
                            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            RegisterActivity.this.finish();

//                            if (result.getMessage().equals("created")) {
//                                Toast.makeText(RegisterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//                                RegisterActivity.this.finish();
//                            } else {
//                                Toast.makeText(RegisterActivity.this, "email is used by another user", Toast.LENGTH_SHORT).show();
//                            }
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "something were wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultLogout> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
