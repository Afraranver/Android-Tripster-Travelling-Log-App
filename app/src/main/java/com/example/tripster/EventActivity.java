package com.example.tripster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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

public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventLog";
    EditText edtEventName, edtVenue, edtBookingDate, edtTime, edtReminder;
    final Calendar myCalendar = Calendar.getInstance();
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    Button btnSaveEvent, btnTest;

    private FirebaseAuth firebaseAuth;
    String UserID;
    FirebaseFirestore fStore;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    TimePicker myTimePicker;
    TextView textAlarmPrompt;
    TimePickerDialog timePickerDialog;
    final static int RQS_1 = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        edtBookingDate = findViewById(R.id.edtBookingDate);
        edtEventName = findViewById(R.id.edtEventName);
        edtVenue = findViewById(R.id.edtVenue);
        edtTime = findViewById(R.id.edtTime);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnTest = findViewById(R.id.btntest);
        edtReminder = findViewById(R.id.edtReminder);

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        btnTest.setVisibility(View.INVISIBLE);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openTimePickerDialog(false);
            }
        });

        edtBookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog =  new DatePickerDialog(EventActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtVenue.getText().toString().isEmpty() && edtEventName.getText().toString().isEmpty() && edtTime.getText().toString().isEmpty() && edtBookingDate.getText().toString().isEmpty()){
                    Toast.makeText(EventActivity.this, "Please fill in the required context!!", Toast.LENGTH_SHORT).show();
                }else{
                    String temp = " Date: " + edtBookingDate.getText().toString() + ", Time: " +  edtTime.getText().toString() + ", Name: " + edtEventName.getText().toString() + ", Venue: " +edtVenue.getText().toString()+", Reminder: " + edtReminder.getText().toString() ;
                    UserID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Add_Event").document(UserID);
                    Map<String,Object> user = new HashMap<>();
                    user.put(getDate() + " Event: " + edtEventName.getText().toString(), temp );

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
                                            Toast.makeText(EventActivity.this, "Successfully Saved to Database!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EventActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            Toast.makeText(EventActivity.this, "Failed to Save in Database!", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                } else {
                                    Log.d(TAG, "No such document");
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: user Profile is created for "+ UserID);
                                            Toast.makeText(EventActivity.this, "Successfully Saved to Database!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EventActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            Toast.makeText(EventActivity.this, "Failed to Save in Database!", Toast.LENGTH_SHORT).show();                                        }
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

                    case R.id.event:{
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
                                Toast.makeText(EventActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
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

                    case R.id.nav_about:{

                        startActivity(new Intent(getApplicationContext(),PlanningActivity.class));
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

        textAlarmPrompt = (TextView) findViewById(R.id.alarmprompt);
        textAlarmPrompt.setVisibility(View.INVISIBLE);

    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(EventActivity.this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Set Alarm Time");
        timePickerDialog.show();

    }


    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);


            calSet.set(Calendar.YEAR, calNow.get(myCalendar.YEAR));
            calSet.set(Calendar.MONTH, calNow.get(myCalendar.MONTH));
            calSet.set(Calendar.DAY_OF_MONTH, calNow.get(myCalendar.DAY_OF_MONTH));

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            edtTime.setText(hourOfDay + ":" + minute);
            setAlarm(calSet);
//            setAlarmformessage(calSet);
        }
    };

    private void setAlarm(Calendar targetCal) {

//        textAlarmPrompt.setText("\n\n***\n" + "Alarm is set "
//                + targetCal.getTime() + "\n" + "***\n");
        Toast.makeText(this, "Alarm is set " + targetCal.getTime(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        Log.d("setAlarm:", targetCal.getTime().toString());

    }

    private void setAlarmfordate(Calendar targetCal) {

//        textAlarmPrompt.setText("\n\n***\n" + "Alarm is set + targetCal.getTime() + "\n" + "***\n");
        Toast.makeText(this, "Alarm is set " + targetCal.getTime(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), AlarmDateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        Log.d("setAlarmfordate:", targetCal.getTime().toString());
    }

    private void setAlarmformessage(Calendar targetCal) {

//        textAlarmPrompt.setText("\n\n***\n" + "Alarm is set + targetCal.getTime() + "\n" + "***\n");
        Toast.makeText(this, "Message Sent!! ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getBaseContext(), AlarmDateReceiverMessage.class);
        intent.putExtra("UserID", edtReminder.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        startService(intent);
        Log.d("setAlarmformessage:", targetCal.getTime().toString());
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
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();
            calSet.set(Calendar.YEAR, year);
            calSet.set(Calendar.MONTH, monthOfYear);
            calSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
            setAlarmfordate(calSet);
            setAlarmformessage(calSet);

        }
    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtBookingDate.setText(sdf.format(myCalendar.getTime()));
    }

    private String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return date + " " + time;
    }
}