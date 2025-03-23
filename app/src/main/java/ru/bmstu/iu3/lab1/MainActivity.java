package ru.bmstu.iu3.lab1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import ru.bmstu.iu3.lab1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements TransactionEvents {

    // Used to load the 'lab1' library on application startup.
    static {
        System.loadLibrary("lab1");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;
    private ActivityResultLauncher activityResultLauncher;
    private String pin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    protected void testHttpClient() {
        new Thread(() -> {
            try {
                HttpURLConnection uc = (HttpURLConnection)
                        (new URL("http://10.44.78.161:25575/api/v1/title").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream, "UTF-8");
                String title = getPageTitle(html);
                runOnUiThread(() -> {
                    Toast.makeText(this, title, Toast.LENGTH_LONG).show();
                });
            } catch (Exception ex) {
                Log.e("fapptag", "Http client fails", ex);
            }
        }).start();
    }

    protected String getPageTitle(String html) {
        int pos = html.indexOf("<title");
        String p = "not found";
        if (pos >= 0) {
            int pos2 = html.indexOf("<", pos + 1);
            if (pos2 >= 0)
                p = html.substring(pos + 7, pos2);
        }
        return p;
    }


    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }


    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }

    public native boolean transaction(byte[] trd);

    public static byte[] stringToHex(String s) {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (DecoderException ex) {
            hex = null;
        }
        return hex;
    }

    public void onButtonClick(View v) {
//        Intent it = new Intent(this, PinpadActivity.class);
//        it.putExtra("amount", "100"); // пример значения
//        it.putExtra("ptc", 0);              // пример количества попыток
//        startActivity(it);
//
//        activityResultLauncher.launch(it);
        testHttpClient();
    }


    /**
     * A native method that is implemented by the 'lab1' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public static native int initRng();

    public static native byte[] randomBytes(int no);

    public static native byte[] encrypt(byte[] key, byte[] data);

    public static native byte[] decrypt(byte[] key, byte[] data);

}



