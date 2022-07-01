package com.example.foody.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foody.R;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

public class LoginActivity extends AppCompatActivity {
    EditText txtEmail;
    EditText txtPassword;
    Button btnLogin;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.putExtra("UserId", user.getUid());
            startActivity(main);
            finish();
        }


        getView();
        listenClick();
        getData();

    }

    void getView() {
        txtEmail = findViewById(R.id.editTextEmailAddress);
        txtPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnGoToRegister);
    }

    void getData() {
        Intent result = getIntent();
        if (result.hasExtra("Pass")) {
            txtPassword.setText(result.getStringExtra("Pass"));
        }
        if (result.hasExtra("Email")) {
            txtEmail.setText(result.getStringExtra("Email"));
        }
    }

    void listenClick() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this, Register.class);
                register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(register);
                finish();

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean validate = true;
                String email = txtEmail.getText().toString().trim(), password = txtPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    txtEmail.setError("Bạn chưa nhập email");
                    validate = false;
                } else txtEmail.setError(null);
                if (password.isEmpty()) {
                    txtPassword.setError("Bạn chưa nhập mật khẩu");
                    validate = false;
                } else txtPassword.setError(null);
                if (validate) {
                    login(txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim());
                }
            }
        });
    }

    public void login(String email, String password) {
        AGConnectAuthCredential credential = EmailAuthProvider.credentialWithPassword(email, password);

        AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        // Obtain sign-in information.
                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        main.putExtra("UserId", signInResult.getUser().getUid());
                        startActivity(main);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}