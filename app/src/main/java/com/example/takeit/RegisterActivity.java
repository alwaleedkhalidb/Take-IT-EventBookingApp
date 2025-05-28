package com.example.takeit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;


// this class is for users to submit name and email and then choose their role using a spinner
// all data gets stored into the real time firebase database

public class RegisterActivity extends AppCompatActivity {

    // showing ui elements and firebase authentication
    EditText regName, regEmail, regPassword;
    Spinner  roleSpinner;
    Button   registerBtn;
    FirebaseAuth mAuth;
    DatabaseReference userDb;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register); // connects the java to the xml design file for registeration

        // allow firebase authentication to work to add to users in the real time firebase database
        mAuth = FirebaseAuth.getInstance();
        userDb = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Users");

        // link components to xml layout
        regName     = findViewById(R.id.regName);
        regEmail    = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerBtn = findViewById(R.id.registerBtn);

        // adding user and organizer roles to spinner
        String[] roles = {"TakeIT User", "Event Organizer"};
        ArrayAdapter<String> roleAd = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        roleAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAd);

        // this is for registeration button click
        registerBtn.setOnClickListener(v -> {
            // grab the inputted values from the fields
            String name  = regName.getText().toString();
            String email = regEmail.getText().toString();
            String pass  = regPassword.getText().toString();
            String role  = roleSpinner.getSelectedItem().toString(); // take the selected role

            // use all grabbed information and add it to firebase real time database
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    // store data under /users and create new uid
                    String uid = mAuth.getCurrentUser().getUid();
                    User user = new User(name, email, role);
                    userDb.child(uid).setValue(user);  // this is to save user info to database

                    // this is to re-direct user to login page after account is created
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    // if registeration fails, this message shows up
                    Toast.makeText(this, "Registration failed:\n" +
                            t.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }


    // created objects to organize for the database structure
    public static class User {
        public String name, email, role;

        public User() {}

        public User(String n, String e, String r) {
            name = n;
            email = e;
            role = r;
        }
    }
}
