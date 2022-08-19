package com.example.tripster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AboutActivity extends AppCompatActivity {
    Button btnSearch;
    EditText edtSearch;
    WebView webView;
    Boolean isUrl;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());

        firebaseAuth = FirebaseAuth.getInstance();
        String text = edtSearch.getText().toString();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edtSearch.getText().toString();
                isUrl= URLUtil.isValidUrl(url);

                if(isUrl){
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.loadUrl(url);
                    Log.d("Url: ", url);
                } else{
                    //this url well open in web view as google search
                    url = "https://www.google.com/search?q="+url.replace(" ", "%20");
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.loadUrl(url);
                    Log.d("Url: ", url);
                }

            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String url = edtSearch.getText().toString();

                isUrl= URLUtil.isValidUrl(url);

                if(isUrl)
                    url = text;
                else{
                    //this url well open in web view as google search
                    url = "https://www.google.com/search?q="+url.replace(" ", "%20");
                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.loadUrl(url);
                    Log.d("Url: ", url);
                }
                return false;
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
                                Toast.makeText(AboutActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
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


    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();
    }

}