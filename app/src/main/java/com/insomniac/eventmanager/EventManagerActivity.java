package com.insomniac.eventmanager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class EventManagerActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(R.string.firestore);

        mFragmentManager = getSupportFragmentManager();

        Log.i(EventManagerActivity.class.getSimpleName(),"onCreate");
        addEventFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.event_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_event : addEventFragment();return true;
            case R.id.view_event : viewEventsFragment();return true;
            default : return super.onOptionsItemSelected(item);
        }
    }

    public void addEventFragment(){
        Log.i(EventManagerActivity.class.getSimpleName(),"onCreate1");
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.events_content,new EventManagerAddFragment());
        fragmentTransaction.commit();
    }

    public void viewEventsFragment(){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.events_content, new EventManagerViewFragment());
        ft.commit();
    }
}
