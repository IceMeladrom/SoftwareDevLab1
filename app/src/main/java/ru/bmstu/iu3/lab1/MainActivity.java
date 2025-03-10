package ru.bmstu.iu3.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import ru.bmstu.iu3.lab1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'lab1' library on application startup.
    static {
        System.loadLibrary("lab1");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int res = initRng();
        byte[] v = randomBytes(10);
        byte[] v2 = randomBytes(10);

        byte[] key = new byte[]{'k', 'a', 'k', '7'};
        byte[] data = new byte[]{'h', 'e', 'l', 'l', 'o'};

        byte[] encrypted = encrypt(key, data);
        byte[] decrypted = decrypt(key, encrypted);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
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