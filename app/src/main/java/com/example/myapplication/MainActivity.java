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

    // Used to load the 'myapplication' library on application startup.
    static {
        System.loadLibrary("myapplication");
    }

    private ActivityMainBinding binding;

    // relative to app/src/main/assets/
    final static String EXAMPLE_DOC_FN = "locations.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BOILERPLATE: initialize stuff
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView textView = binding.sampleText;

        // appDataDir will be something like "/data/user/0/com.example.myapplication/files"
        String appDataDir = this.getApplicationContext().getFilesDir().toString();
        String htmlPath = appDataDir + "/" + EXAMPLE_DOC_FN;

        // BOILERPLATE: copy from assets/ to <appDataDir>/
        AssetManager am = this.getAssets();
        InputStream is; try { is = am.open(EXAMPLE_DOC_FN); } catch (IOException e) { throw new RuntimeException(e); }
        Scanner scanner = new Scanner(is).useDelimiter("\\A"); String contents = scanner.hasNext() ? scanner.next() : "";
        PrintWriter pw; try { pw = new PrintWriter(htmlPath); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
        pw.print(contents); pw.close();

        // set the TextView's text to the output of the C++ parseXML() function
        textView.setText(parseHTML(htmlPath));
    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    public native String parseHTML(String htmlPath);
}