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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pkmmte.view.CircularImageView;

import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.dfapi.ApiException;
import evolveconference.safelive.dfapi.ApiInvoker;
import evolveconference.safelive.dfapi.BaseAsyncRequest;
import evolveconference.safelive.model.Staff;
import evolveconference.safelive.model.StaffList;
import evolveconference.safelive.ui.fragments.AlertFragment;
import evolveconference.safelive.ui.fragments.CircleDashboardFragment;
import evolveconference.safelive.ui.fragments.DashboardFragment;
import evolveconference.safelive.ui.fragments.HomeFragment;
import evolveconference.safelive.ui.fragments.PatientsFragment;
import evolveconference.safelive.ui.fragments.SettingsFragment;
import evolveconference.safelive.ui.fragments.StatisticsFragment;
import evolveconference.safelive.utils.AppConstants;
import evolveconference.safelive.utils.ComponentUtils;
import evolveconference.safelive.utils.PrefUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PARAM_STAFF_ID = "staff_id";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Staff staff;
    private GetStaffInfo getStaffInfo;

    CircularImageView profileImage;
    TextView username;
    TextView role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        if (savedInstanceState == null) {
            showFragment(createFragment(HomeFragment.class));
        }

        setupUI();
        setupData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getStaffInfo != null) {
            getStaffInfo.cancel(true);
        }
    }

    private void setupUI() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setupHeadetviews(navigationView.getHeaderView(0));
        setupToolbar();
        setupDrawerToggle();
    }

    private void setupHeadetviews(View rootView) {
        profileImage = (CircularImageView) rootView.findViewById(R.id.profile_image);
        username = (TextView) rootView.findViewById(R.id.username);
        role = (TextView) rootView.findViewById(R.id.role);
    }

    private void setupData() {
        if (getIntent().hasExtra(PARAM_STAFF_ID)) {
            getStaffInfo = new GetStaffInfo(getIntent().getIntExtra(PARAM_STAFF_ID, 0));
            getStaffInfo.execute();
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupDrawerToggle() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
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
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        }
        else {
            menuItem.setChecked(true);
        }

        drawerLayout.closeDrawers();

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

    private class GetStaffInfo extends BaseAsyncRequest {
        private int staffId;

        public GetStaffInfo(int staffId) {
            this.staffId = staffId;
        }

        @Override
        protected void doSetup() throws ApiException, JSONException {
            callerName = "GetStaffInfo";

            serviceName = AppConstants.DB_SVC;
            endPoint = "staff";
            verb = "GET";

            // filter to only select the contacts in this group
            queryParams = new HashMap<>();
            queryParams.put("filter", "staffid=" + staffId);

            // need to include the API key and session token
            applicationApiKey = AppConstants.API_KEY;
            sessionToken = PrefUtil.getString(getApplicationContext(), AppConstants.SESSION_TOKEN);
        }

        @Override
        protected void processResponse(String response) throws ApiException, JSONException {
            staff = ((StaffList) ApiInvoker.deserialize(response, "", StaffList.class)).record.get(0);
        }

        @Override
        protected void onCompletion(boolean success) {
            if (!isCancelled()) {
                if (success) {
                    populateScreen();
                } else {
                    displayLoginIfNeeded(MainActivity.this, exception);
                }
            }
        }
    }

    private void populateScreen() {
        if (ComponentUtils.checkUIisOK(this)) {
            Glide.with(this)
                    .load(staff.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(profileImage);
            username.setText(getString(R.string.first_and_last_names, staff.firstName, staff.lastName));
            role.setText(staff.position);
        }
    }
}
