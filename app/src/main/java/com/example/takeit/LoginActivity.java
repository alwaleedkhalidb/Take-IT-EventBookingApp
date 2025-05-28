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


public class LoginActivity extends AppCompatActivity {

    EditText email, password; Button loginBtn; TextView registerLink;
    FirebaseAuth mAuth;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email); password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn); registerLink = findViewById(R.id.registerLink);

        loginBtn.setOnClickListener(v -> {
            mAuth.signInWithEmailAndPassword(
                            email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(t -> {
                        if (!t.isSuccessful()) {Toast.makeText(this,"Login failed",Toast.LENGTH_SHORT).show();return;}
                        String uid = mAuth.getCurrentUser().getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                                .getReference("Users").child(uid);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(DataSnapshot snap) {
                                String role = snap.child("role").getValue(String.class);
                                if ("Event Organizer".equals(role)) {
                                    startActivity(new Intent(LoginActivity.this,AdminDashboardActivity.class));
                                } else if ("TakeIT User".equals(role)) {
                                    startActivity(new Intent(LoginActivity.this,UserHomeActivity.class));
                                } else {
                                    Toast.makeText(LoginActivity.this,"Role not set",Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                            @Override public void onCancelled(DatabaseError e) {
                                Toast.makeText(LoginActivity.this,"DB error "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
        });

        registerLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
