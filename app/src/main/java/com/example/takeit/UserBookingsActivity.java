package com.example.takeit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
/**
 *  Displays every booking for the logged-in user in a simple ListView.
 */
public class UserBookingsActivity extends AppCompatActivity {

    ListView bookingList;
    DatabaseReference bookingDb;
    FirebaseAuth mAuth;
    ArrayAdapter<String> adapter;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookings);
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> onBackPressed());
        bookingList = findViewById(R.id.bookingList);
        mAuth      = FirebaseAuth.getInstance();
        bookingDb  = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Bookings");

        String uid = mAuth.getCurrentUser().getUid();

        bookingDb.orderByChild("userId").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        ArrayAdapter<String> ad;
                        ArrayList<String> rows = new ArrayList<>();

                        for (DataSnapshot s : snap.getChildren()) {
                            BookingEventActivity.Booking b = s.getValue(BookingEventActivity.Booking.class);
                            rows.add("Event ID: " + b.eventId + "\nTickets: "
                                    + b.quantity + "\nTotal: " + b.totalPrice + " OMR");
                        }

                        ad = new ArrayAdapter<>(UserBookingsActivity.this,
                                android.R.layout.simple_list_item_1, rows);
                        bookingList.setAdapter(ad);
                    }

                    @Override public void onCancelled(DatabaseError e) {
                        Toast.makeText(UserBookingsActivity.this,"Load failed: "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }


}
