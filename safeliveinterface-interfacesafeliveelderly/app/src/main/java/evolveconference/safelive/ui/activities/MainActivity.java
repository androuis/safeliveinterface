package evolveconference.safelive.ui.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Date;

import evolveconference.safelive.R;
import evolveconference.safelive.ui.fragments.AlertFragment;
import evolveconference.safelive.ui.fragments.CircleDashboardFragment;
import evolveconference.safelive.ui.fragments.DashboardFragment;
import evolveconference.safelive.ui.fragments.HomeFragment;
import evolveconference.safelive.ui.fragments.PatientsFragment;
import evolveconference.safelive.ui.fragments.SettingsFragment;
import evolveconference.safelive.ui.fragments.StatisticsFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        if (savedInstanceState == null) {
            showFragment(createFragment(HomeFragment.class));
        }

        // Initializing Toolbar and setting it as the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.home:
                        showFragment(createFragment(HomeFragment.class));
                        return true;
                    case R.id.alerts:
                        showFragment(createFragment(AlertFragment.class));
                        return true;
                    case R.id.patients:
                        showFragment(createFragment(PatientsFragment.class));
                        return true;
                    case R.id.settings:
                        showFragment(createFragment(SettingsFragment.class));
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public <T extends Fragment> void showFragment(T newFragment) {
        if (newFragment == null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.content_container);
        if (currentFragment == null || !(currentFragment.getClass() == newFragment.getClass())) {
            FragmentTransaction ft = fm.beginTransaction();
            //ft.setCustomAnimations(R.anim.fade_in_quick_frag, R.anim.fade_out_quick_frag, R.anim.fade_in_quick_frag, R.anim.fade_out_quick_frag);
            ft.addToBackStack("tag");
            ft.replace(R.id.frame, newFragment);
            ft.commit();
        }
        fm.executePendingTransactions();
    }

    private <T extends Fragment> Fragment createFragment(Class<T> clazz) {
        if (DashboardFragment.class == clazz) {
            return new DashboardFragment();
        }

        if (StatisticsFragment.class == clazz) {
            return StatisticsFragment.newInstance(new Date());
        }

        if (CircleDashboardFragment.class == clazz) {
            return new CircleDashboardFragment();
        }

        if (PatientsFragment.class == clazz) {
            return new PatientsFragment();
        }

        if (SettingsFragment.class == clazz) {
            return new SettingsFragment();
        }

        if (HomeFragment.class == clazz) {
            return new HomeFragment();
        }

        if (AlertFragment.class == clazz) {
            return new AlertFragment();
        }

        return new HomeFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }
}
