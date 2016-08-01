package demo.ibartj.coachmachine;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import demo.ibartj.coachmachine.fragments.NewWorkoutFragment;
import demo.ibartj.coachmachine.fragments.WorkoutListFragment;
import demo.ibartj.coachmachine.model.Workout;
import demo.ibartj.coachmachine.util.DoneClickListener;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String ACTION_CONNECTIVITY_CHANGE = "connectivity_change";
    private static final int PICK_ACCOUNT_REQUEST = 1;

    private static IntentFilter intentFilter;
    private boolean lastConnState = false;
    private String username;

    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private DrawerLayout drawerLayout;
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private boolean mUserLearnedDrawer;

    private CharSequence mTitle;
    protected ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupSupportActionBar(getString(R.string.app_name), R.drawable.ic_menu_white_24dp);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupActionBarToggle(drawerLayout, toolbar);

        mTitle = getTitle();

        lastConnState = AppContext.getInstance().getWorkoutService().isNetworkConnected();

        if (savedInstanceState != null && savedInstanceState.containsKey("username")) {
            username = savedInstanceState.getString("username");
        }
        initUsername();
    }

    private void setupActionBarToggle(DrawerLayout drawerLayout, Toolbar toolbar) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                MainActivity.this.supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                MainActivity.this.supportInvalidateOptionsMenu();
            }
        };
        if (!mUserLearnedDrawer) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });
    }

    private void setupSupportActionBar(String title, int iconResourceId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(iconResourceId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_home:
                startWorkoutListFragment();
                return true;
            case R.id.drawer_new:
                startNewWorkoutFragment();
                return true;
        }
        return false;
    }

    public void onSectionAttached(int titleResourceId) {
        mTitle = getResources().getString(titleResourceId);
        invalidateOptionsMenu();
        restoreActionBar();
    }

    public void restoreActionBar() {
        setupSupportActionBar(mTitle.toString(), isNewWorkoutFragmentOpen() ? R.drawable.ic_arrow_back_white_24dp : R.drawable.ic_menu_white_24dp);
    }

    private void checkMenuItem(int position) {
        Menu menu = navigationView.getMenu();
        int len = menu.size();
        for (int idx = 0; idx < len; idx++) {
            navigationView.getMenu().getItem(idx).setChecked(idx == position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            getMenuInflater().inflate(R.menu.main, menu);

            boolean show = isNewWorkoutFragmentOpen();
            menu.getItem(1).setVisible(show);
            menu.getItem(0).setVisible(!show && !AppContext.getInstance().getWorkoutService().isNetworkConnected());
            menu.getItem(0).setEnabled(false);

            restoreActionBar();
            return true;
        }
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (isNewWorkoutFragmentOpen()) {
                    startWorkoutListFragment();
                }
                break;
            case R.id.action_done:
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof DoneClickListener) {
                        ((DoneClickListener) fragment).onDoneButtonClicked();
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showErrorMessage(int titleResourceId, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titleResourceId);
        alertDialogBuilder.setMessage(message)
                .setCancelable(false)
                .setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }

    public void showProgressDialog(int messageResourceId) {
        getProgressDialog();
        if (messageResourceId > 0) {
            progressDialog.setMessage(getResources().getString(messageResourceId));
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    @Override
    public void onBackPressed() {
        if (isNewWorkoutFragmentOpen()) {
            startWorkoutListFragment();
            return;
        }
        super.onBackPressed();
    }

    public Fragment startWorkoutListFragment() {
        return startFragmentInstance(WorkoutListFragment.newInstance(), 0);
    }

    public Fragment startNewWorkoutFragment() {
        return startNewWorkoutFragment(null);
    }

    public Fragment startNewWorkoutFragment(Workout workout) {
        NewWorkoutFragment instance = NewWorkoutFragment.newInstance();
        if (workout != null) {
            instance.setWorkout(workout);
        }
        return startFragmentInstance(instance,1);
    }

    private Fragment startFragmentInstance(Fragment fragmentInstance, int menuPosition) {
        drawerLayout.closeDrawers();
        checkMenuItem(menuPosition);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragmentInstance)
                .commit();
        return fragmentInstance;
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ACTION_CONNECTIVITY_CHANGE)) {
                invalidateOptionsMenu();
                boolean connState = AppContext.getInstance().getWorkoutService().isNetworkConnected();
                if (lastConnState != connState && !isNewWorkoutFragmentOpen()) {
                    startWorkoutListFragment();
                }
                lastConnState = connState;
            }
        }
    };

    private static IntentFilter getBroadcastIntentFilter() {
        if (intentFilter == null) {
            intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_CONNECTIVITY_CHANGE);
        }
        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, getBroadcastIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    private boolean isNewWorkoutFragmentOpen() {
        Menu menu = navigationView.getMenu();
        return (menu!=null && menu.getItem(1)!=null && menu.getItem(1).isChecked());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (username != null) {
            outState.putString("username", username);
        }
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof DoneClickListener) {
                    outState.putBoolean("newWorkout", true);
                    outState.putSerializable("workout", ((NewWorkoutFragment) fragment).getWorkout());
                    break;
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("username")) {
            username = savedInstanceState.getString("username");
        }
        if (savedInstanceState.containsKey("newWorkout")
                && savedInstanceState.getBoolean("newWorkout")
                && savedInstanceState.containsKey("workout")) {
            hideProgressDialog();
            startNewWorkoutFragment((Workout) savedInstanceState.getSerializable("workout"));
        } else {
            startWorkoutListFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    public void initUsername() {
        if (username == null) {
            Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
            startActivityForResult(googlePicker, PICK_ACCOUNT_REQUEST);
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        if (requestCode == PICK_ACCOUNT_REQUEST && resultCode == RESULT_OK) {
            username = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            TextView v = (TextView) findViewById(R.id.header_email);
            if (v != null) {
                v.setText(username);
            }
            startWorkoutListFragment();
        }
    }
}
