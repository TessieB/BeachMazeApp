package edu.wm.cs.cs301.TessieBaumann.gui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.TessieBaumann.R;

public class GeneratingActivity extends AppCompatActivity {


    private static final String TAG = "Run Thread";
    private static final String KEY = "my message key";
    private ProgressBar loadingBar;
    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generating_activity);
        Spinner builderSpinner = (Spinner) findViewById(R.id.sensorspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sensor, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builderSpinner.setAdapter(adapter);

        loadingBar = (ProgressBar) findViewById(R.id.generatingProgressBar);
        loadingBar.setMax(100);
        runThread(loadingBar);
        myHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                String progressBarMessage = bundle.getString(KEY);
                Log.v(TAG, progressBarMessage);
                loadingBar.incrementProgressBy(5);
            }
        };

    }

    public void runThread(View view){
        loadingBar.setProgress(0);
        Log.v(TAG, "Running thread");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 100; i+= 5) {
                    Log.v(TAG, "Running thread inside run method");
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        System.out.println("Thread was interrupted");
                    }
                    Log.v(TAG, "Thread is done running");
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY, "time to increment the progress bar by 5");
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
