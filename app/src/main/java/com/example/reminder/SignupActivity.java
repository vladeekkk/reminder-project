package com.example.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailEt;
    private EditText passwordEt;
    private Button signUpBtn;

    private ReminderService reminderService;
    private FirebaseDatabase database;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Registration");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reminderService = new ReminderServiceImpl(new ReminderDAOImpl(this));

        emailEt = findViewById(R.id.email_edt_text);
        passwordEt = findViewById(R.id.pass_edt_text);

        signUpBtn = findViewById(R.id.signup_btn);

        signUpBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show();
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show();

                        DatabaseReference ref = database.getReference(auth.getCurrentUser().getUid());
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (auth.getCurrentUser() == null) {
                                    return;
                                }

                                List<String> reminders = (List<String>) snapshot.getValue();
                                if (reminders == null) {
                                    return;
                                }
                                for (String reminder : reminders) {
                                    try {
                                        reminderService.save(new Reminder(reminder));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(SignupActivity.this, "Failed to read value", Toast.LENGTH_LONG).show();
                            }
                        });

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
