package com.chronix.databaseparsing;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout mContainerFragment;
    private FragmentTransaction fragmentTransaction;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mContainerFragment = (FrameLayout) findViewById(R.id.content_fragment);
        ListFragment listFragment = new ListFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mContainerFragment.getId(), listFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        //mNavigationDrawerFragment.init(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.main_activity_drawer_layout), mToolbar);

        //FrameLayout frameLayout = new FrameLayout(this);


        //link = getIntent().getStringExtra("link");
        //textView = (TextView) findViewById(R.id.content_text);

        //textView.setText(link);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        networkCheck(drawer);
    }

    public void networkCheck(View view) {
        boolean network;
        Database database = new Database(MainActivity.this);
        network = database.isNetworkAvailable();
        if (!network) {
            Snackbar.make(view, "No internet connection. ", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            FilterFragment filterFragment = new FilterFragment();
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(mContainerFragment.getId(), filterFragment);
            fragmentTransaction.addToBackStack("Replacing Main List");
            fragmentTransaction.commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // getActionBar().setDisplayHomeAsUpEnabled(true);
            toggle.setDrawerIndicatorEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_post) {
            PostFragment postFragment = new PostFragment();
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(mContainerFragment.getId(), postFragment);
            fragmentTransaction.addToBackStack("Replacing Main List");
            fragmentTransaction.commit();

            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // getActionBar().setDisplayHomeAsUpEnabled(true);
            toggle.setDrawerIndicatorEnabled(false);
        }
        return true;
    }
}

