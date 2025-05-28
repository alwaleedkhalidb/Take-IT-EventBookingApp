package com.example.takeit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UserHomeActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_user_home);

        Button book = findViewById(R.id.btnCreateBooking);
        Button view = findViewById(R.id.btnViewBooking);

        book.setOnClickListener(v->startActivity(new Intent(this,BookingEventActivity.class)));
        view.setOnClickListener(v->startActivity(new Intent(this,UserBookingsActivity.class)));
    }
}
