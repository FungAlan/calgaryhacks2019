package com.calgaryhacks.calvin.hackathon2019;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        HomeFragment homeFragment = HomeFragment.getInstance();
                        openFragment(homeFragment);
                        return true;
                    case R.id.navigation_reports:
                        ReportsFragment reportsFragment = ReportsFragment.getInstance();
                        openFragment(reportsFragment);
                        return true;
                    case R.id.navigation_profile:
                        ProfileFragment profileFragment = ProfileFragment.getInstance();
                        openFragment(profileFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        
    }
}
