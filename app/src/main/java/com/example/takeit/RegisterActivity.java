package com.example.takeit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

/**
 *  Lets a new user sign-up and stores their profile under /Users/{uid}
 *  Roles are hard-coded into a Spinner {"TakeIT User", "Event Organizer"}.
 */
public class RegisterActivity extends AppCompatActivity {

    EditText regName, regEmail, regPassword;
    Spinner  roleSpinner;
    Button   registerBtn;

    FirebaseAuth      mAuth;
    DatabaseReference userDb;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);

        mAuth  = FirebaseAuth.getInstance();
        userDb = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Users");

        regName     = findViewById(R.id.regName);
        regEmail    = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerBtn = findViewById(R.id.registerBtn);

        // NOTE: populate spinner with roles
        String[] roles = {"TakeIT User", "Event Organizer"};
        ArrayAdapter<String> roleAd = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        roleAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAd);

        registerBtn.setOnClickListener(v -> {
            String name = regName.getText().toString();
            String email= regEmail.getText().toString();
            String pass = regPassword.getText().toString();
            String role = roleSpinner.getSelectedItem().toString();

            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();
                    User user  = new User(name, email, role);
                    userDb.child(uid).setValue(user);           // write to DB
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this,"Registration failed:\n"+
                            t.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /** Simple POJO stored under /Users */
    public static class User {
        public String name, email, role;
        public User() {}
        public User(String n,String e,String r){name=n;email=e;role=r;}
    }
}
