package com.example.takeit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;


// this is to show user all the bookings created using information from real time firebase database
public class UserBookingsActivity extends AppCompatActivity {

    // showing ui elements and firebase authentication
    ListView bookingList;
    Button backBtn;
    DatabaseReference bookingDb;
    FirebaseAuth mAuth;

    // to show the adapter in ListView
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookings);

        // created this back button to go back to main user screen for convienience
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> onBackPressed());

        // listview that will show all events booked by user
        bookingList = findViewById(R.id.bookingList);

        // to make firebase authenticiation
        mAuth = FirebaseAuth.getInstance();
        bookingDb = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Bookings");

        // to get uid from firebase database
        String uid = mAuth.getCurrentUser().getUid();

        // to show the bookings where uid matches the uid for the logged in user
        bookingDb.orderByChild("userId").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snap) {
                        // to hold the booking info
                        ArrayList<String> rows = new ArrayList<>();

                        // this is to loop all info from real time database
                        for (DataSnapshot s : snap.getChildren()) {
                            BookingEventActivity.Booking b = s.getValue(BookingEventActivity.Booking.class);
                            rows.add("Event ID: " + b.eventId +
                                    "\nTickets: " + b.quantity +
                                    "\nTotal: " + b.totalPrice + " OMR");
                        }

                        // this is to add bookings to listview
                        adapter = new ArrayAdapter<>(UserBookingsActivity.this,
                                android.R.layout.simple_list_item_1, rows);
                        bookingList.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError e) {
                        // this is to stop database errors
                        Toast.makeText(UserBookingsActivity.this,
                                "Load failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
