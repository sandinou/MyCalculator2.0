package com.mycalculator20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;

public class ThemeActivity extends AppCompatActivity {

    private RadioGroup themeGroup;
    private AppPreferences preferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = AppPreferences.getInstance(this);
        setTheme(preferences.getStringPreference(AppPreferences.APP_THEME));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting toolbar style manually
        setToolBarStyle(preferences.getStringPreference(AppPreferences.APP_THEME));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        themeGroup = findViewById(R.id.rg_theme_group);
        checkSelectedTheme();

        themeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_theme_turquoise:
                        changeTheme("turquoise");
                        break;
                    case R.id.rb_theme_orange:
                        changeTheme("orange");
                        break;
                    case R.id.rb_theme_blue:
                        changeTheme("blue");
                        break;
                    case R.id.rb_theme_green:
                        changeTheme("green");
                        break;
                    case R.id.rb_theme_pink:
                        changeTheme("pink");
                        break;
                    case R.id.rb_theme_default:
                        changeTheme("default");
                }
            }
        });
    }

    private void changeTheme(String themeName) {
        Intent[] intent = new Intent[3];
        switch (themeName){
            case "turquoise":
                preferences.setStringPreference(AppPreferences.APP_THEME, "turquoise");
                intent[2] = new Intent(this, ThemeActivity.class);
                intent[1] = new Intent(this, SettingsActivity.class);
                intent[0] = new Intent(this, MainActivity.class);

                startActivities(intent);
                finish();
                break;
            case "orange":
                preferences.setStringPreference(AppPreferences.APP_THEME, "orange");
                intent[2] = new Intent(this, ThemeActivity.class);
                intent[1] = new Intent(this, SettingsActivity.class);
                intent[0] = new Intent(this, MainActivity.class);
                startActivities(intent);
                finish();
                break;
            case "blue":
                preferences.setStringPreference(AppPreferences.APP_THEME, "blue");
                intent[2] = new Intent(this, ThemeActivity.class);
                intent[1] = new Intent(this, SettingsActivity.class);
                intent[0] = new Intent(this, MainActivity.class);
                startActivities(intent);
                finish();
                break;
            case "green":
                preferences.setStringPreference(AppPreferences.APP_THEME, "green");
                intent[2] = new Intent(this, ThemeActivity.class);
                intent[1] = new Intent(this, SettingsActivity.class);
                intent[0] = new Intent(this, MainActivity.class);
                startActivities(intent);
                finish();
                break;
            case "pink":
                preferences.setStringPreference(AppPreferences.APP_THEME, "pink");
                intent[2] = new Intent(this, ThemeActivity.class);
                intent[1] = new Intent(this, SettingsActivity.class);
                intent[0] = new Intent(this, MainActivity.class);
                startActivities(intent);
                finish();
                break;
            case "default":
                preferences.setStringPreference(AppPreferences.APP_THEME, "default");
                intent[2] = new Intent(this, ThemeActivity.class);
                intent[1] = new Intent(this, SettingsActivity.class);
                intent[0] = new Intent(this, MainActivity.class);
                startActivities(intent);
                finish();
                break;
        }
    }

    private void setTheme(String themeName) {

        switch (themeName){
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
        }
    }

    private void checkSelectedTheme() {
        String themeName = preferences.getStringPreference(AppPreferences.APP_THEME);

        switch (themeName){
            case "turquoise":
                themeGroup.check(R.id.rb_theme_turquoise);
                break;
            case "orange":
                themeGroup.check(R.id.rb_theme_orange);
                break;
            case "blue":
                themeGroup.check(R.id.rb_theme_blue);
                break;
            case "green":
                themeGroup.check(R.id.rb_theme_green);
                break;
            case "pink":
                themeGroup.check(R.id.rb_theme_pink);
                break;
            case "default":
            case "":
                themeGroup.check(R.id.rb_theme_default);
                break;
        }
    }

    private void setToolBarStyle(String themeName) {

        switch (themeName){
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
}
