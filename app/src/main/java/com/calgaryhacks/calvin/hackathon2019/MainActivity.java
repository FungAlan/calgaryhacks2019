package com.calgaryhacks.calvin.hackathon2019;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private ReportAdapter adapter;
    private double my_lon;
    private double my_lat;
    private FusedLocationProviderClient client;
    private RecyclerView recycler_view;
    private ImageButton help;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view = getSupportActionBar().getCustomView();
        help = view.findViewById(R.id.bar_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog info_dialog = new Dialog(MainActivity.this);
                info_dialog.setContentView(R.layout.dialog);
                Button cancel = info_dialog.findViewById(R.id.dialog_dismiss);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info_dialog.cancel();
                    }
                });
                info_dialog.show();
            }
        });

//        BottomNavigationView navigationView = findViewById(R.id.navigationView);
//        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.navigation_home:
//                        HomeFragment homeFragment = HomeFragment.getInstance();
//                        openFragment(homeFragment);
//                        return true;
//                    case R.id.navigation_reports:
//                        ReportsFragment reportsFragment = ReportsFragment.getInstance();
//                        openFragment(reportsFragment);
//                        return true;
//                    case R.id.navigation_profile:
//                        ProfileFragment profileFragment = ProfileFragment.getInstance();
//                        openFragment(profileFragment);
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successful.";
                        if (!task.isSuccessful()) {
                            msg = "Failed.";
                        }
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        //declare all the necessary variables
        adapter = new ReportAdapter(getApplicationContext());
        client = LocationServices.getFusedLocationProviderClient(this);
        recycler_view = findViewById(R.id.report_recycler);
        adapter = new ReportAdapter(getApplicationContext());
        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view.setAdapter(adapter);

        //set up a timer that fires every 10 seconds and updates user's location -> which triggers an update of the entire list
        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                if(ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(getApplicationContext(), "NOT Getting Location", Toast.LENGTH_SHORT).show();
                    requestPermission();
                    return;
                }
                client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null){
                            my_lat = location.getLatitude();
                            my_lon = location.getLongitude();
                            adapter.updateAdapter(my_lon, my_lat);
                            //Toast.makeText(getApplicationContext(), "MY: "+my_lon+" "+my_lat, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        };
        timer.scheduleAtFixedRate(t,0,10000);
        createNotificationChannels();
    }


    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    /**
     * This method creates a notification channel for phones running Android Oreo and higher (API 26), it
     * will be ignored by all phones running a lower API.
     *
     * @author Robert Fiker
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Message Notification";
            String description = "This channel notifies the user when a new message is received.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NOTIFICATION_CHAT", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

    }


    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        
    }
}
