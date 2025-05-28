package com.example.takeit;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

// this takes events from the real time firebase database
    // it makes user choose event, then enter a ticket amount to be calculated displayed

public class BookingEventActivity extends AppCompatActivity {

    /* ---------- UI ---------- */
    private Spinner  eventSpinner;
    private EditText ticketCount;
    private TextView totalPrice;
    private TextView eventLocTv, eventDescTv;
    private Button   bookBtn, backBtn;

    // this is firebase setup
    private FirebaseAuth      mAuth;
    private DatabaseReference eventDb, bookingDb;

    // helpers to allow code to work
    private final List<Event> eventList = new ArrayList<>();
    private double  selectedEventPrice = 0.0;
    private String  selectedEventId    = "";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_event);

        //fire base fix using references with ne base_url setup
        mAuth     = FirebaseAuth.getInstance();
        eventDb   = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Events");
        bookingDb = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Bookings");

        // adding the different widgets
        eventSpinner  = findViewById(R.id.eventSpinner);
        ticketCount   = findViewById(R.id.ticketCount);
        totalPrice    = findViewById(R.id.totalPrice);
        eventLocTv    = findViewById(R.id.eventLocation);     // NEW
        eventDescTv   = findViewById(R.id.eventDescription);  // NEW
        bookBtn       = findViewById(R.id.bookBtn);
        backBtn       = findViewById(R.id.backBtn);

        loadEventsIntoSpinner();

        ticketCount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateTotal();
        });

        bookBtn.setOnClickListener(v -> makeBooking());
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    // takes event from database to be placed in spinners, and changes description and location when selected
    private void loadEventsIntoSpinner() {
        eventDb.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                eventList.clear();
                List<String> names = new ArrayList<>();

                for (DataSnapshot s : snap.getChildren()) {
                    Event e = s.getValue(Event.class);
                    eventList.add(e);
                    names.add(e.name);
                }

                ArrayAdapter<String> ad = new ArrayAdapter<>(
                        BookingEventActivity.this,
                        android.R.layout.simple_spinner_item,
                        names);
                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                eventSpinner.setAdapter(ad);

                eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        Event sel          = eventList.get(pos);
                        selectedEventId    = sel.id;
                        selectedEventPrice = sel.price;

                        // this is to show extra event information like location and description
                        eventLocTv.setText("Location: " + sel.location);
                        eventDescTv.setText("Description: " + sel.description);

                        calculateTotal();
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
            }
            @Override public void onCancelled(DatabaseError err) {
                Toast.makeText(BookingEventActivity.this,
                        "Failed to load events: " + err.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    //  to calculate the total according to ticket amount and quantity
    private void calculateTotal() {
        String c = ticketCount.getText().toString();
        if (!c.isEmpty()) {
            int qty = Integer.parseInt(c);
            double tot = qty * selectedEventPrice;
            totalPrice.setText(String.format("Total: %.3f OMR", tot));
        }
    }

    // this to show alert of event addition to be successfull to be added to the realtime firebase database
    private void makeBooking() {
        String qtyStr = ticketCount.getText().toString();
        if (qtyStr.isEmpty()) {
            Toast.makeText(this, "Enter ticket count", Toast.LENGTH_SHORT).show();
            return;
        }

        int    qty  = Integer.parseInt(qtyStr);
        double tot  = qty * selectedEventPrice;
        String uid  = mAuth.getCurrentUser().getUid();
        String bid  = bookingDb.push().getKey();

        Booking b = new Booking(bid, uid, selectedEventId, qty, tot);

        bookingDb.child(bid).setValue(b).addOnCompleteListener(t -> {
            String msg = t.isSuccessful()
                    ? "Booking saved!"
                    : "Booking failed: " + t.getException().getMessage();
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            if (t.isSuccessful()) {
                ticketCount.setText("");
                totalPrice.setText("Total: 0.000 OMR");
            }
        });
    }

    // different classes for events and bookings to be saved for database
    public static class Event {
        public String id, name, location, description;
        public double price;
        public Event() {}
        public Event(String i, String n, String l, String d, double p) {
            id = i; name = n; location = l; description = d; price = p;
        }
    }
    public static class Booking {
        public String id, userId, eventId;
        public int quantity;
        public double totalPrice;
        public Booking() {}
        public Booking(String i, String u, String e, int q, double t) {
            id = i; userId = u; eventId = e; quantity = q; totalPrice = t;
        }
    }
}
