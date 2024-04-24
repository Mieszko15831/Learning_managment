package com.example.learning_activity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.learning_activity.Fragments.StatsFragment;
import com.google.firebase.messaging.RemoteMessage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.messaging.FirebaseMessaging;

import com.example.learning_activity.Fragments.AccountFragment;
import com.example.learning_activity.Fragments.CalendarFragment;
import com.example.learning_activity.Fragments.TasksFragment;
import com.example.learning_activity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout mainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getFCMToken();
        mainFrame = findViewById(R.id.content_main);
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_home){
                    setFragment(new TasksFragment());
                    return true;
                }
                else if(item.getItemId() == R.id.nav_add){
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.nav_calendar){
                    setFragment(new CalendarFragment());
                    return true;
                }
                else if (item.getItemId() == R.id.nav_stats) {
                    setFragment(new StatsFragment());
                    return true;
                }
                else if(item.getItemId() == R.id.nav_account){
                    setFragment(new AccountFragment());
                    return true;
                }
                return false;
            }
        });
        setFragment(new TasksFragment());
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainFrame.getId(), fragment);
        transaction.commit();
    }

    private void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    Log.i("mytoken",token);
                }
            }
        });
    }
}