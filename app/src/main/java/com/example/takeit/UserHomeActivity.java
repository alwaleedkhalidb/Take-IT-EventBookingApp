package com.example.takeit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

//this is home screen for the normal users to give them two options of making new booking or viewing old bookings
public class UserHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_user_home); // to connect java to xml design

        // buttons to go to the different pages
        Button book = findViewById(R.id.btnCreateBooking);   // create booking
        Button view = findViewById(R.id.btnViewBooking);     // view booking

        // when create booking is clicked, it takes them to bookingeventactivity.java
        book.setOnClickListener(v ->
                startActivity(new Intent(this, BookingEventActivity.class))
        );

        // 🔹when view booking is clicked, it takes them to usersbookingactivity.java
        view.setOnClickListener(v ->
                startActivity(new Intent(this, UserBookingsActivity.class))
        );
    }
}
