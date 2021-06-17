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

public class UpdatePassword extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText passwordEt;
    private Button changePasswordBtn;
    private Button back;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        setTitle("Password update");


        auth = FirebaseAuth.getInstance();

        passwordEt = findViewById(R.id.password_edt_text);

        changePasswordBtn = findViewById(R.id.reset_pass_btn);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(v -> {
            finish();
        });

        changePasswordBtn.setOnClickListener(v -> {
            String password = passwordEt.getText().toString();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            } else {
                auth.getCurrentUser().updatePassword(password).
                        addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Password changes successfully", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "password not changed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
