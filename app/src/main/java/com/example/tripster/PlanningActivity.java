package com.example.tripster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlanningActivity extends AppCompatActivity {
    private static final String TAG = "EventLog";
    Button btnPsave;
    EditText edtTitle, edtPdate, edtPstarttime, edtPReachedtime, edtPendTime;

    final Calendar myCalendar = Calendar.getInstance();
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);

    private FirebaseAuth firebaseAuth;
    String UserID;
    FirebaseFirestore fStore;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        btnPsave = findViewById(R.id.btnPsave);
        edtTitle = findViewById(R.id.edtTitle);
        edtPdate = findViewById(R.id.edtPdate);
        edtPstarttime = findViewById(R.id.edtPstarttime);
        edtPReachedtime = findViewById(R.id.edtPReachedtime);
        edtPendTime = findViewById(R.id.edtPendTime);

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        edtPstarttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlanningActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                edtPstarttime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        edtPendTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlanningActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                edtPendTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        edtPReachedtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(PlanningActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                edtPReachedtime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        edtPdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =  new DatePickerDialog(PlanningActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        btnPsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtPdate.getText().toString().isEmpty() && edtPendTime.getText().toString().isEmpty() && edtPReachedtime.getText().toString().isEmpty() && edtPstarttime.getText().toString().isEmpty() && edtTitle.getText().toString().isEmpty()){
                    Toast.makeText(PlanningActivity.this, "Please fill in the required context!!", Toast.LENGTH_SHORT).show();
                }else{
                    String temp = " Title of Event: " + edtTitle.getText().toString() + ", Date: " +  edtPdate.getText().toString() + ", Start Time: " + edtPstarttime.getText().toString() + ", Reached Time: " +edtPReachedtime.getText().toString() + ", End Time: " +edtPendTime.getText().toString() ;
                    UserID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Event_Flow").document(UserID);
                    Map<String,Object> user = new HashMap<>();
                    user.put(getDate() + " Event Flow: " + edtTitle.getText().toString(), temp );
//                    user.put("Date",edtBookingDate.getText().toString());
//                    user.put("Time",edtTime.getText().toString());
//                    user.put("Name",edtEventName.getText().toString());
//                    user.put("Venue",edtVenue.getText().toString());

                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: user Profile is created for "+ UserID);
                                            Toast.makeText(PlanningActivity.this, "Successfully Saved to Database!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PlanningActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            Toast.makeText(PlanningActivity.this, "Failed to Save in Database!", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                } else {
                                    Log.d(TAG, "No such document");
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: user Profile is created for "+ UserID);
                                            Toast.makeText(PlanningActivity.this, "Successfully Saved to Database!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PlanningActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            Toast.makeText(PlanningActivity.this, "Failed to Save in Database!", Toast.LENGTH_SHORT).show();                                        }
                                    });
                                }

                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }

                        }
                    });

                }

            }

        });

        setUpToolbar();
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {

                    case R.id.nav_home:{
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        drawerLayout.closeDrawers();
                    }
                    break;

                    case R.id.event:{
                        startActivity(new Intent(getApplicationContext(),EventActivity.class));
                        drawerLayout.closeDrawers();
                    }

                    case R.id.nav_about: {
                        drawerLayout.closeDrawers();
                    }
                    break;

                    case R.id.maps:{
                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        drawerLayout.closeDrawers();
                    }
                    break;

                    case R.id.history:{
                        String uri = "http://maps.google.com/maps?saddr=" + "";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.mapsghdf");
                        try
                        {
                            startActivity(intent);
                        }
                        catch(ActivityNotFoundException ex)
                        {
                            try
                            {
                                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(unrestrictedIntent);
                            }
                            catch(ActivityNotFoundException innerEx)
                            {
                                Toast.makeText(PlanningActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                            }
                        }

                        drawerLayout.closeDrawers();
                    }
                    break;

                    case  R.id.nav_logout:{
                        FirebaseAuth.getInstance().signOut();//logout
                        revokeAccess();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        Toast.makeText(getApplicationContext(), "You have Successfully Logged Out!!", Toast.LENGTH_SHORT).show();
                        finish();
                        drawerLayout.closeDrawers();
                    }
                    break;

                    case R.id.report:{
                        startActivity(new Intent(getApplicationContext(),Report.class));
                        drawerLayout.closeDrawers();
                    }
                    break;

                    case R.id.about_place:{
                        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                        drawerLayout.closeDrawers();
                    }
                    break;

                }
                return false;
            }
        });
    }

    private void revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();
    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtPdate.setText(sdf.format(myCalendar.getTime()));
    }

    private String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }
}