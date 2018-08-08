package com.mycalculator20;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> settingsList;
    private Intent intent;
    private AppPreferences preferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setToolbarStyle(preferences.getStringPreference(AppPreferences.APP_THEME));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listView = findViewById(R.id.listview);

        settingsList = new ArrayList<>();
        settingsList.add(getResources().getString(R.string.themes));
        settingsList.add(getResources().getString(R.string.answerPrecision));

        arrayAdapter = new ArrayAdapter<>(this,R.layout.setting_list_layout, settingsList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        intent = new Intent(SettingsActivity.this, ThemeActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(SettingsActivity.this, Precision.class);
                        startActivity(intent);
                }
            }
        });
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setToolbarStyle(String theme) {

        switch (theme){
            case "turquoise":
                toolbar.setBackground(getDrawable(R.color.colorTurquoiseDark));
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                break;
            case "orange":
                toolbar.setBackground(getDrawable(R.color.colorPrimaryDark));
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                break;
            case "blue":
                toolbar.setBackground(getDrawable(R.color.colorBlueDark));
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                break;
            case "green":
                toolbar.setBackground(getDrawable(R.color.colorLightGreenDark));
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                break;
            case "pink":
                toolbar.setBackground(getDrawable(R.color.colorPinkDark));
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                break;
            case "default":
            case "":
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                toolbar.setBackground(getDrawable(R.color.darkGray));
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

                break;

        }
    }

    private void setTheme(String theme) {
        switch (theme){
            case "turquoise":
                setTheme(R.style.TurquoiseAppTheme);
                break;
            case "orange":
                setTheme(R.style.AppTheme);
                break;
            case "blue":
                setTheme(R.style.BlueAppTheme);
                break;
            case "green":
                setTheme(R.style.GreenAppTheme);
                break;
            case "pink":
                setTheme(R.style.PinkAppTheme);
                break;
            case "default":
                setTheme(R.style.DefAppTheme);
                break;
            case "":
                setTheme(R.style.DefAppTheme);
                preferences.setStringPreference(AppPreferences.APP_THEME,"default");
                break;
        }
    }
}
