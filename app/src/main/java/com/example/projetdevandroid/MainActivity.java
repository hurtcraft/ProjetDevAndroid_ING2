package com.example.projetdevandroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.LocaleManagerCompat;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if(id==R.id.action_language){
                showLanguageDialog();
            }
            else if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            }
            else if (id == R.id.nav_geolocate) {
                replaceFragment(new MapsFragment());
            }
            else if (id == R.id.nav_camera) {
                replaceFragment(new CameraFragment());
            } else if (id == R.id.nav_person) {
                replaceFragment(new PeopleFragment());
                
            }
            if (item.getItemId() == R.id.nav_weather) {
                replaceFragment(new WeatherFragment());

            else if (id == R.id.nav_external_bd) {
                replaceFragment(new ExternalBDFragment());
            }
            //else if (id == R.id.nav_settings) {
            //    replaceFragment(new SettingsFragment());
            //}

            drawerLayout.closeDrawers();
            return true;
        });
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    v.getPaddingLeft(),
                    systemBars.top,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );

            return insets;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
    private void showLanguageDialog() {

        String[] languages = {"Français", "English"};

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_language))
                .setItems(languages, (dialog, which) -> {

                    if (which == 0) {
                        setLocale("fr");
                    } else {
                        setLocale("en");
                    }

                })
                .show();
    }
    private void setLocale(String languageCode) {
        AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCode)
        );
    }


}