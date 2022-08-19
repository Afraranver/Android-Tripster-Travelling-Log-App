package com.example.tripster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String ARG_NAME = "username";
    private static final String TAG = "Delete" ;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;

    //alarm
    TextView textAlarmPrompt;
    TimePickerDialog timePickerDialog;
    final static int RQS_1 = 1;

    ListView listView;
    ListView listView2;
    TextView textView;
    List<String> list;
    List<String> list2;
    List<String> listtoday;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        listView=(ListView)findViewById(R.id.listView);
        listView2=(ListView)findViewById(R.id.listView2);

        textView=(TextView)findViewById(R.id.txtMyList);


        setUpToolbar();
        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_home:{
                        Toast.makeText(MainActivity.this, "You are in Home!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                    case R.id.maps:{
                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        drawerLayout.closeDrawers();
                    }
                    break;

                    case R.id.event:{
                        startActivity(new Intent(getApplicationContext(),EventActivity.class));
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
                                Toast.makeText(MainActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
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
                        revokeAccess();
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


        UserID = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("Add_Event").orderBy("random", Query.Direction.ASCENDING);
        DocumentReference codesRef = rootRef.collection("Add_Event").document(UserID);

        codesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    List<String> listdet = new ArrayList<>();
                    list = new ArrayList<>();
                    Map<String, Object> map = document.getData();
                    if (map != null) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            list.add(entry.getKey());
                            listdet.add(entry.getValue().toString());
                        }

                        listtoday = new ArrayList<>();
                        for(String s: list){
                            if(s.contains(getDate())){
                                listtoday.add(s);
                            }
                        }

                        list2 = new ArrayList<>();
                        for(String s: list){
                            for(int i = 1; i<11; i++){
                                if(s.contains(getyesDate(i))){
                                    list2.add(s);
                                }
                            }

                        }
                    }


                    //So what you need to do with your list
                    for (String s : listtoday) {
                        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, listtoday);
                        listView.setAdapter(adapter);
                        Log.d("TAG", s);
                    }

                    for (String s : list2) {
                        adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, list2);
                        listView2.setAdapter(adapter2);
                        Log.d("TAG", s);
                    }



                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            // TODO Auto-generated method stub
                            String value=adapter.getItem(position);
                            String temp = "";
                            Map<String, Object> map = document.getData();
                            if (map != null) {
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    if(value.equals(entry.getKey())){
                                        temp= entry.getValue().toString();
                                    }

//                                list.add(entry.getKey());
                                }
                                String formattedString = temp.toString()
                                        .replace(",", "\n")  //remove the commas
                                        .replace("[", "")  //remove the right bracket
                                        .replace("]", "")  //remove the left bracket

                                        .trim();
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Upcoming Event\n")
                                        .setMessage(formattedString)

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                StringBuilder csvList = new StringBuilder();

                                            }
                                        })

                                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                DocumentReference documentReference = fStore.collection("Add_Event").document(UserID);
                                                Map<String,Object> user = new HashMap<>();
                                                user.put(value, FieldValue.delete());


                                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "onSuccess: user Profile is created for "+ UserID);
                                                                        Toast.makeText(MainActivity.this, "Successfully Deleted in Database!", Toast.LENGTH_SHORT).show();
                                                                        recreate();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d(TAG, "onFailure: " + e.toString());
                                                                        Toast.makeText(MainActivity.this, "Failed to Delete in Database!", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });
                                                            }

                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }

                                                    }
                                                });
                                            }
                                        })
                                        .show();
                            }
                        }
                    });

                    listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            // TODO Auto-generated method stub
                            String value=adapter2.getItem(position);
                            String temp = "";
                            Map<String, Object> map = document.getData();
                            if (map != null) {
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    if(value.equals(entry.getKey())){
                                        temp= entry.getValue().toString();
                                    }

//                                list.add(entry.getKey());
                                }
                                String formattedString = temp.toString()
                                        .replace(",", "\n")  //remove the commas
                                        .replace("[", "")  //remove the right bracket
                                        .replace("]", "")  //remove the left bracket

                                        .trim();
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Past Event\n")
                                        .setMessage(formattedString)

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                StringBuilder csvList = new StringBuilder();

                                            }
                                        })

                                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                UserID = firebaseAuth.getCurrentUser().getUid();
                                                DocumentReference documentReference = fStore.collection("Event_Flow").document(UserID);

                                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "onSuccess: user Profile is created for "+ UserID);
                                                                        Toast.makeText(MainActivity.this, "Successfully Deleted in Database!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d(TAG, "onFailure: " + e.toString());
                                                                        Toast.makeText(MainActivity.this, "Failed to Delete in Database!", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });
                                                            }

                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }

                                                    }
                                                });
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                }

            }


        });
    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    public static void startActivity(Context context, String username) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ARG_NAME, username);
        context.startActivity(intent);
    }

    private void revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();
    }


    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(MainActivity.this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
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

            if (calSet.compareTo(calNow) <= 0) {
                // Today Set time passed, count to tomorrow
                calSet.add(Calendar.DATE, 1);
            }

            setAlarm(calSet);
        }
    };

    private void setAlarm(Calendar targetCal) {

        textAlarmPrompt.setText("\n\n***\n" + "Alarm is set "
                + targetCal.getTime() + "\n" + "***\n");

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);

    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String getyesDate(int i){
        Calendar cal = Calendar.getInstance();
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.DATE, -i);
        String date=dfDate.format(cal.getTime());
        return date;
    }

    private String getDate(){
        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date=dfDate.format(Calendar.getInstance().getTime());
        return date;
    }


}