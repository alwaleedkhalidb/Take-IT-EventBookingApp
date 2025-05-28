package com.example.takeit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// activity to login for event organizers and users
public class LoginActivity extends AppCompatActivity {

    // declaration of ui things and firebase
    EditText email, password;
    Button loginBtn;
    TextView registerLink;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);  // this is to connect the java with its xml design file

        // to start firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // connect the ui to the xml elemenets
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);

        // login button click action
        loginBtn.setOnClickListener(v -> {
            // try to sign in using the provided email and password
            mAuth.signInWithEmailAndPassword(
                    email.getText().toString(),
                    password.getText().toString()
            ).addOnCompleteListener(t -> {
                if (!t.isSuccessful()) {
                    //  login fails then show error message
                    Toast.makeText(this, "Login failed, Try Again", Toast.LENGTH_SHORT).show();
                    return;
                }

                // log in works, gets the uid of user that logged in
                String uid = mAuth.getCurrentUser().getUid();

                // references thethe user data in real time firebase database
                DatabaseReference userRef = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                        .getReference("Users").child(uid);

                // read user information including role
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snap) {
                        //  the role stored in the database for  user
                        String role = snap.child("role").getValue(String.class);

                        // takes to different pages based on role type
                        if ("Event Organizer".equals(role)) {
                            //  user is  organizer, go to to the admin page/dashboard
                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        } else if ("TakeIT User".equals(role)) {
                            // user is user , go to to the user page/dashboard
                            startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                        } else {
                            // no role / missing
                            Toast.makeText(LoginActivity.this, "Role not set", Toast.LENGTH_SHORT).show();
                        }

                        // this is to stop user from going to login screen
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError e) {
                        // this is to stop firebase problems and errors
                        Toast.makeText(LoginActivity.this, "DB error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // this is to take to register page once clicked
        registerLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
