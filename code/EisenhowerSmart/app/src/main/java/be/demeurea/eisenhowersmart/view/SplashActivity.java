package be.demeurea.eisenhowersmart.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that shows a splash screen during the loading of the app.
 * @author Demeure Arnaud
 * @created on 25-01-21
 */
public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(!isFinishing()){
                startActivity(new Intent(getApplicationContext(),EisenhowerMatrix.class));
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 500);
    }
}