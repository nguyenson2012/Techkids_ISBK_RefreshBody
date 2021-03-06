package com.example.asus.refreshbody.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asus.refreshbody.R;
import com.example.asus.refreshbody.RefreshBodyApplication;
import com.example.asus.refreshbody.database.model.CupChooseItem;
import com.example.asus.refreshbody.database.model.DrinkIntakeItem;
import com.example.asus.refreshbody.database.model.TimeDrink;
import com.example.asus.refreshbody.database.model.User;
import com.example.asus.refreshbody.fragment.DrinkLog;
import com.example.asus.refreshbody.fragment.FragmentBackUp;
import com.example.asus.refreshbody.fragment.FragmentChooseCup;
import com.example.asus.refreshbody.fragment.FragmentDrawer;
import com.example.asus.refreshbody.fragment.FragmentDrinkWater;
import com.example.asus.refreshbody.fragment.FragmentReminder;
import com.example.asus.refreshbody.fragment.FragmentReminderPlanDetail;
import com.example.asus.refreshbody.fragment.FragmentSetWeight;
import com.example.asus.refreshbody.fragment.FragmentSetting;
import com.example.asus.refreshbody.fragment.SettingsFragment;
import com.example.asus.refreshbody.intef.FragmentDrawerListener;
import com.example.asus.refreshbody.provider.DefaultDataSqlite;
import com.example.asus.refreshbody.provider.PlanContract;
import com.example.asus.refreshbody.provider.PlanDBHelper;
import com.example.asus.refreshbody.service.AlarmServiceReceiver;
import com.example.asus.refreshbody.utils.Constant;
import com.example.asus.refreshbody.utils.ScreenManager;
import com.example.asus.refreshbody.utils.iLog;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FragmentDrawerListener, FragmentReminder.OnListItemSelectedListener {
    private String TAG = MainActivity.this.getClass().getSimpleName();

    private Toolbar mToolbar;


    private ScreenManager screenManager;

    private FragmentDrinkWater fragmentDrinkWater;
    private FragmentDrawer drawerFragment;
    private FragmentReminder fragmentReminder;
    private FragmentChooseCup fragmentChooseCup;
    private FragmentSetWeight fragmentSetWeight;
    private DrinkLog fragmentDrinkLog;

    private PlanDBHelper planDBHelper;
    private DefaultDataSqlite defaultDataSqlite;

    private SharedPreferences sharedPreferences;

    private boolean isDatabaseAlready;
    private String userId;
    private FragmentSetting fragmentSetting;
    private FragmentBackUp fragmentBackup;

    @Override
    protected void onDestroy() {
        iLog.d(iLog.LogTag.UI, TAG + " onDestroy()");
        getContentResolver().unregisterContentObserver(mContentObserver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences(Constant.MY_PREFERENCE,MODE_PRIVATE);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d("Date",date);
        getContentResolver().registerContentObserver(PlanContract.PlanEntry.CONTENT_URI, true, mContentObserver);
        planDBHelper=PlanDBHelper.getInstance(this);
        isDatabaseAlready=sharedPreferences.getBoolean(Constant.DATABASE_ALREADY,false);
        if(!isDatabaseAlready) {
            defaultDataSqlite = DefaultDataSqlite.getInst(planDBHelper);
            defaultDataSqlite.setDefaultData();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(Constant.DATABASE_ALREADY,true);
            editor.commit();
        }
        setUpView();
        intiliazeFragment();
        if  (sharedPreferences.getBoolean(Constant.PREFERENCE_FIRST_RUNNING, true)) {
            addFragmentSetWeight();
            sharedPreferences.edit().putBoolean(Constant.PREFERENCE_FIRST_RUNNING, false).commit();
        } else addFragmentDrinkWater();
    }

    private void addFragmentSetWeight() {
        screenManager.openFragmentWithAnimation(getSupportFragmentManager(),R.id.frame_container,fragmentSetWeight,false);
    }


    public void addFragmentDrinkWater() {
        screenManager.openFragmentWithAnimation(getSupportFragmentManager(),R.id.frame_container,fragmentDrinkWater,false);
        openNavigationDrawer();
    }

    private void intiliazeFragment() {
        fragmentDrinkWater=new FragmentDrinkWater();
        fragmentChooseCup=new FragmentChooseCup();
        fragmentDrinkLog = new DrinkLog();
        fragmentReminder = new FragmentReminder();
        fragmentSetWeight=new FragmentSetWeight();
        fragmentSetting=new FragmentSetting();
        fragmentBackup=new FragmentBackUp();
    }

    private void setUpView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Menu");

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        //new DrawerBuilder().withActivity(this).build();

        screenManager=ScreenManager.getInst();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position){
            case 0://Drink water
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                screenManager.openFragmentWithAnimation(getSupportFragmentManager(),R.id.frame_container,fragmentDrinkWater,false);
                break;
            case 1://Drink log
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                screenManager.openFragmentWithAnimation(getSupportFragmentManager(),R.id.frame_container,fragmentDrinkLog,false);
                break;
            case 2://Drink record
                Intent intentRecord = new Intent(MainActivity.this, DrinkReportActivity.class);
                startActivity(intentRecord);
                break;
            case 3://Reminder
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                screenManager.openFragmentWithAnimation(getSupportFragmentManager(), R.id.frame_container, fragmentReminder, false);
                break;
            case 4: //Settings
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                screenManager.openFragmentWithAnimation(getSupportFragmentManager(), R.id.frame_container, fragmentSetting, false);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onListItemSelected(Uri uri, long id) {
        FragmentReminderPlanDetail fragment = FragmentReminderPlanDetail.newInstance(id);
        screenManager.openFragmentWithAnimation(getSupportFragmentManager(), R.id.frame_container, fragment, false);
    }

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            iLog.d(iLog.LogTag.UI, TAG + " mContentObserver onChange() selfChange : " + selfChange);

            // Re-trigger AlarmServiceReceiver to start AlarmService to update next alarm
            Intent intent = new Intent(getApplicationContext(), AlarmServiceReceiver.class);
            sendBroadcast(intent);
        }
    };

    public void replaceFragmentCupChoose() {
        screenManager.openFragmentWithAnimation(getSupportFragmentManager(),R.id.frame_container,fragmentChooseCup,true);
    }

    public void addDrinkIntake(CupChooseItem cupChooseItem) {
        DrinkIntakeItem drinkIntakeItem=new DrinkIntakeItem(cupChooseItem.getSymbolPosition(),cupChooseItem.getNameCup(),
                cupChooseItem.getAmountCup());
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        drinkIntakeItem.setTimeDrink(new TimeDrink(year,month,day,hour,minute));
        drinkIntakeItem.setIdDrink(Math.abs(SystemClock.currentThreadTimeMillis())+year+month+day+hour+minute+"");
        drinkIntakeItem.setUpdated(false);
        planDBHelper.insertDrinkIntake(drinkIntakeItem);
        //insertDrinkIntakeToServer(drinkIntakeItem);
        //insertDrinkIntakeHttp(drinkIntakeItem);
    }

    private void insertDrinkIntakeHttp(DrinkIntakeItem drinkIntakeItem) {
        JSONObject params = new JSONObject();
        try {
            params.put(Constant.ID_DRINK_INTAKE,drinkIntakeItem.getIdDrink());
            params.put(Constant.ID_USER,userId);
            params.put(Constant.SYMBOL_POSITION,drinkIntakeItem.getSymbolPosition()+"");
            params.put(Constant.AMOUNT_DRINK,drinkIntakeItem.getAmountDrink()+"");
            params.put(Constant.NAME_DRINK,drinkIntakeItem.getNameDrink());
            params.put(Constant.TIME_DRINK,drinkIntakeItem.getDateString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void getUserId() {
        userId=sharedPreferences.getString(Constant.ID_USER,"");
    }

    public void closeDrawer() {
        drawerFragment.closeNavigationDrawer();
    }

    public void openNavigationDrawer() {
        drawerFragment.openNavigationDrawer();
    }

    public void openFragmentBackUp() {
        screenManager.openFragmentWithAnimation(getSupportFragmentManager(),R.id.frame_container,fragmentBackup,true);
    }
}
