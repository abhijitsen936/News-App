package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mContactNumberEditText;
    private EditText mPasswordEditText;
    private CheckBox mTermsAndConditionsCheckBox;
    private Button mRegisterButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mNameEditText = findViewById(R.id.name);
        mEmailEditText = findViewById(R.id.email);
        mContactNumberEditText = findViewById(R.id.contact_number);
        mPasswordEditText = findViewById(R.id.password);
        mTermsAndConditionsCheckBox = findViewById(R.id.terms_and_conditions);
        mRegisterButton = findViewById(R.id.register_button);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTermsAndConditionsCheckBox.isChecked()) {
                    String name = mNameEditText.getText().toString().trim();
                    String email = mEmailEditText.getText().toString().trim();
                    String contactNumber = mContactNumberEditText.getText().toString().trim();
                    String password = mPasswordEditText.getText().toString().trim();

                    if(!name.isEmpty() && !email.isEmpty() && !contactNumber.isEmpty() && !password.isEmpty()) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Signup.this, new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()) {
                                            String userId = mAuth.getCurrentUser().getUid();

                                            Map<String, Object> user = new HashMap<>();
                                            user.put("name", name);
                                            user.put("email", email);
                                            user.put("contact_number", contactNumber);

                                            mDatabaseReference.child("users").child(userId).setValue(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(Signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(Signup.this, MainActivity.class));

                                                                finish();
                                                            } else {
                                                                Toast.makeText(Signup.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(Signup.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(Signup.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Signup.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
