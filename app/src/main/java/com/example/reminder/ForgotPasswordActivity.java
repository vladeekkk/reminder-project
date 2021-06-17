package com.example.reminder;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailEt;
    private Button resetPasswordBtn;
    private Button back;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_activity);
        setTitle("Password recovery");

        auth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.email_edt_text);

        resetPasswordBtn = findViewById(R.id.reset_pass_btn);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(v -> {
            finish();
        });

        resetPasswordBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email id", Toast.LENGTH_LONG).show();
            } else {
                auth.sendPasswordResetEmail(email).
                        addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
