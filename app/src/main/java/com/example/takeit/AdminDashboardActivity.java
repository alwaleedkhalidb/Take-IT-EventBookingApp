package com.example.takeit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AdminDashboardActivity extends AppCompatActivity {

    EditText eventName,eventLocation,eventDescription,eventPrice;
    Button createEventBtn,backBtn;
    DatabaseReference eventDb;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_dashboard);

        eventDb = FirebaseDatabase.getInstance(FirebaseHelper.BASE_URL)
                .getReference("Events");

        eventName=findViewById(R.id.eventName); eventLocation=findViewById(R.id.eventLocation);
        eventDescription=findViewById(R.id.eventDescription); eventPrice=findViewById(R.id.eventPrice);
        createEventBtn=findViewById(R.id.createEventBtn); backBtn=findViewById(R.id.backBtn);

        createEventBtn.setOnClickListener(v -> {
            String name=eventName.getText().toString();
            String loc =eventLocation.getText().toString();
            String desc=eventDescription.getText().toString();
            double price=Double.parseDouble(eventPrice.getText().toString());
            String id=eventDb.push().getKey();
            Event e=new Event(id,name,loc,desc,price);

            eventDb.child(id).setValue(e).addOnCompleteListener(t->{
                String msg=t.isSuccessful()?"Event saved":"Save failed: "+t.getException().getMessage();
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
            });
        });

        backBtn.setOnClickListener(v->onBackPressed());
    }

    public static class Event{
        public String id,name,location,description; public double price;
        public Event(){}
        public Event(String i,String n,String l,String d,double p){
            id=i;name=n;location=l;description=d;price=p;
        }
    }
}
