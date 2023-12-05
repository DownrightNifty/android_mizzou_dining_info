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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

// these classes are one-to-one mapping to the C++ versions

class TimeBlock {
    String label;
    int start;
    int end;

//    TimeBlock(String _label, int _start, int _end) {
//        label = _label; start = _start; end = _end;
//    }
}

class Location {
    String name;
    double latitude;
    double longitude;
    String strHours;
    boolean favorite;
    boolean open;
    ArrayList<TimeBlock> hours;

//    Location(String _name, double _latitude, double _longitude, String _strHours, boolean _favorite, boolean _open, ArrayList<TimeBlock> _hours) {
//        name = _name; latitude = _latitude; longitude = _longitude; strHours = _strHours; favorite = _favorite; open = _open; hours = _hours;
//    }
}

class Util {
    static boolean strToBool(String s) { return s.equals("1"); }

    static ArrayList<Location> deserializeLocations(String serializedLocations) {
        ArrayList<Location> locations = new ArrayList<>();

        String[] serLocationObjs = serializedLocations.split("\\|\\|\\|\\|");
        for (String serLocObj : serLocationObjs) {
            Location l = new Location();
            String[] serLocProps = serLocObj.split("\\|\\|\\|");
            l.name = serLocProps[0];
            l.latitude = Double.parseDouble(serLocProps[1]);
            l.longitude = Double.parseDouble(serLocProps[2]);
            l.strHours = serLocProps[3];
            l.favorite = strToBool(serLocProps[4]);
            l.open = strToBool(serLocProps[5]);

            // extract location hours
            ArrayList<TimeBlock> timeBlocks = new ArrayList<>();
            String[] serTimeBlocks = serLocProps[6].split("\\|\\|");
            for (String serTimeBlock : serTimeBlocks) {
                TimeBlock tb = new TimeBlock();
                String[] serTBProps = serTimeBlock.split("\\|");
                tb.label = serTBProps[0];
                tb.start = Integer.parseInt(serTBProps[1]);
                tb.end = Integer.parseInt(serTBProps[2]);
                timeBlocks.add(tb);
            }
            l.hours = timeBlocks;

            locations.add(l);
        }

        return locations;
    }
}

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

        // call C++ getScheduleData() function
        // TODO: use today's date instead of hardcoded value
        String date = "2023-12-05";
        String serializedLocations = getScheduleData(date, DEBUG_MODE, cachedHtmlPath);

        ArrayList<Location> locations = Util.deserializeLocations(serializedLocations);
        System.out.println("Locations:");
        for (Location l : locations) {
            System.out.println(l.name);
            System.out.println(l.strHours);
        }
    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    public native String getScheduleData(String date, boolean debugMode, String cachedHtmlPath);
}
