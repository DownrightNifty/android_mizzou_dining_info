package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.databinding.FragmentSearchBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.DynamicColors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

// NOTE: as is, the TextView contents will be cleared when the user switches between fragments
// (home, locations, and search), this will be fixed by observing the switch and resetting the
// fragment as needed, don't worry for now

public class MainActivity extends AppCompatActivity {

    // If debug mode is on, the cached locations.html will be used instead of live data.
    final static boolean DEBUG_MODE = true;

    // relative to app/src/main/assets/
    final static String CACHED_HTML_FN = "locations.html";

    // Used to load the 'myapplication' library on application startup.
    static {
        System.loadLibrary("myapplication");
    }
    private ActivityMainBinding binding;
    private FragmentSearchBinding binding2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BOILERPLATE: initialize stuff
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_locations, R.id.navigation_home, R.id.navigation_search)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // appDataDir will be something like "/data/user/0/com.example.myapplication/files"
        String appDataDir = this.getApplicationContext().getFilesDir().toString();
        String cachedHtmlPath = appDataDir + "/" + CACHED_HTML_FN;

        // BOILERPLATE: copy from assets/ to <appDataDir>/
        AssetManager am = this.getAssets();
        InputStream is; try { is = am.open(CACHED_HTML_FN); } catch (IOException e) { throw new RuntimeException(e); }
        Scanner scanner = new Scanner(is).useDelimiter("\\A"); String contents = scanner.hasNext() ? scanner.next() : "";
        PrintWriter pw; try { pw = new PrintWriter(cachedHtmlPath); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
        pw.print(contents); pw.close();

        // set the TextView's text to the output of the C++ getScheduleData() function
        // TODO: use today's date instead of hardcoded value
        String date = "2023-12-05";
        System.out.println(getScheduleData(date, DEBUG_MODE, cachedHtmlPath));
    }


    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    public native String getScheduleData(String date, boolean debugMode, String cachedHtmlPath);
}
