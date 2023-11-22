package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BOILERPLATE: initialize stuff
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView textView = binding.sampleText;

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
        String date = "2023-11-21";
        textView.setText(getScheduleData(date, DEBUG_MODE, cachedHtmlPath));
    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    public native String getScheduleData(String date, boolean debugMode, String cachedHtmlPath);
}