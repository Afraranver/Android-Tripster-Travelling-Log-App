package com.example.tripster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Report extends AppCompatActivity {
    private static final String ARG_NAME = "Report";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;

    View view;
    ListView listView;
    TextView textView;
    List<String> Flows;
    List<String> Flows2;
    List<String> Events;
    List<String> Events2;
    ArrayAdapter<String> adapter;
    private String UserID;

    Button btnViewReport, btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        firebaseAuth = FirebaseAuth.getInstance();
        listView = (ListView) findViewById(R.id.reportview);
        textView = (TextView) findViewById(R.id.txtMyList);
        view = findViewById(R.id.view);

        btnDownload  =findViewById(R.id.btnDownload);
        btnViewReport = findViewById(R.id.btnGenerateView);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdfGenerator.getBuilder()
                        .setContext(Report.this)
                        .fromViewSource()
                        .fromView(listView)
                        .setPageSize(PdfGenerator.PageSize.A4)
                        .setFileName("Tripster-Report")
                        .setFolderName("Tripster-folder")
                        .openPDFafterGeneration(true)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                                Toast.makeText(Report.this, "Failed to Save in Storage.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void showLog(String log) {
                                super.showLog(log);
                            }

                            @Override
                            public void onStartPDFGeneration() {
                                /*When PDF generation begins to start*/
                            }

                            @Override
                            public void onFinishPDFGeneration() {
                                /*When PDF generation is finished*/
                            }

                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
                                Toast.makeText(Report.this, "Successfully Saved in Storage.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserID = firebaseAuth.getCurrentUser().getUid();
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                rootRef.collection("Event_Flow").orderBy("random", Query.Direction.ASCENDING);
                DocumentReference codesRef1 = rootRef.collection("Event_Flow").document(UserID);

                codesRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Flows = new ArrayList<>();
                            Map<String, Object> map = document.getData();
                            if (map != null) {
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    Flows.add(entry.getValue().toString());
                                }
                            }

                            Flows2 = new ArrayList<>();
                            for(String s: Flows){
                                if(s.contains(getDate())){
                                    Flows2.add(s);
                                }
                            }

                            Events2.addAll(Flows2);


                            String formattedString = Events2.toString()
                                    .replace(",", "\n")  //remove the commas
                                    .replace("[", "")  //remove the right bracket
                                    .replace("]", "")  //remove the left bracket
                                    .replace("Date", "\nDate")
                                    .replace("Title of Event", "\nTitle of Event")

                                    .trim();

                            //So what you need to do with your list
                            for (String s : Events2) {
                                adapter = new ArrayAdapter<String>(Report.this, android.R.layout.simple_list_item_1, android.R.id.text1, Collections.singletonList(formattedString));
                                listView.setAdapter(adapter);
                                Log.d("TAG", s);
                            }
                        }

                    }


                });
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
                    Events = new ArrayList<>();
                    Map<String, Object> map = document.getData();
                    if (map != null) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            Events.add(entry.getValue().toString());
                        }
                    }

                    Events2 = new ArrayList<>();
                    for(String s: Events){
                        if(s.contains(getDate())){
                            Events2.add(s);
                        }else{

                        }
                    }
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
                                Toast.makeText(Report.this, "Please install a maps application", Toast.LENGTH_LONG).show();
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

    private String getDate(){
        DateFormat dfDate = new SimpleDateFormat("MM/dd/yy");
        String date=dfDate.format(Calendar.getInstance().getTime());
        return date;
    }

}
