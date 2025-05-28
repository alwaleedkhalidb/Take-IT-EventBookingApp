package com.example.takeit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// this activity is for users to create new events with info like name, location and price
public class AdminDashboardActivity extends AppCompatActivity {

    // declaration of ui and for database
    EditText eventName, eventLocation, eventDescription, eventPrice;
    Button createEventBtn, backBtn;
    DatabaseReference eventDb;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_dashboard); // to connect java to xml design

        // to add to events in real time firebase database
        eventDb = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Events");

        // connect elements with xml input fields and buttons
        eventName = findViewById(R.id.eventName);
        eventLocation = findViewById(R.id.eventLocation);
        eventDescription = findViewById(R.id.eventDescription);
        eventPrice = findViewById(R.id.eventPrice);
        createEventBtn = findViewById(R.id.createEventBtn);
        backBtn = findViewById(R.id.backBtn);

        // to create new event when button is clicked
        createEventBtn.setOnClickListener(v -> {

            // take user inputted data
            String name = eventName.getText().toString();
            String loc  = eventLocation.getText().toString();
            String desc = eventDescription.getText().toString();

            // convert price from text to double
            double price = Double.parseDouble(eventPrice.getText().toString());

            // create unique id for each event created
            String id = eventDb.push().getKey();

            // create event with inputted data
            Event e = new Event(id, name, loc, desc, price);

            // save events in real time database under /events
            eventDb.child(id).setValue(e).addOnCompleteListener(t -> {
                String msg = t.isSuccessful() ?
                        "Event saved" :
                        "Save failed: " + t.getException().getMessage();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        });

        // to go back to the same screen
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    // for database structure
    public static class Event {
        public String id, name, location, description;
        public double price;

        public Event() {}

        public Event(String i, String n, String l, String d, double p) {
            id = i;
            name = n;
            location = l;
            description = d;
            price = p;
        }
    }
}
