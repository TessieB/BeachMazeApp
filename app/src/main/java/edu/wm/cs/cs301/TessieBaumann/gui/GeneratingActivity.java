package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.TessieBaumann.R;

public class GeneratingActivity extends AppCompatActivity {


    private static final String TAG = "Run Thread";
    private static final String KEY = "my message key";
    private ProgressBar loadingBar;
    private Handler myHandler;
    private String driver;
    private boolean backPressed = false;
    private String robot = "Premium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generating_activity);
        Spinner sensorSpinner = (Spinner) findViewById(R.id.sensorspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sensor, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSpinner.setAdapter(adapter);
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setRobot(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loadingBar = (ProgressBar) findViewById(R.id.generatingProgressBar);
        loadingBar.setMax(100);
        runThread(loadingBar);
        myHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                String progressBarMessage = bundle.getString(KEY);
                Log.v(TAG, progressBarMessage);
                loadingBar.incrementProgressBy(10);
                moveToNextActivity();
            }
        };

    }

    @Override
    public void onBackPressed(){
        Log.v(TAG, "back button pressed");
        backPressed = true;
        super.onBackPressed();
    }

    public void runThread(View view){
        loadingBar.setProgress(0);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 100; i+= 10) {
                    Log.v(TAG, "backPressed value: " + backPressed);
                    if(backPressed){
                        break;
                    }
                    Log.v(TAG, "Running thread inside run method");
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        System.out.println("Thread was interrupted");
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY, "time to increment the progress bar by 5");
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
                Log.v(TAG, "Thread is done running");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void setDriver(View view){
        Log.d("hey there", "hi " + driver);
        RadioButton tempDriver = (RadioButton) view;
        driver = tempDriver.getText().toString();
        Log.d("hey there2", "hi2 " + driver);
        moveToNextActivity();
    }

    private void moveToNextActivity(){
        Log.v(TAG, "Loading bar: " + loadingBar.toString());
        if(driver != null) {
            if (loadingBar.getProgress() == 100 && driver.equals("Wall Follower")) {
                sendAnimatedMessage(loadingBar);
            } else if (loadingBar.getProgress() == 100 && driver.equals("Wizard")) {
                sendAnimatedMessage(loadingBar);
            } else if (loadingBar.getProgress() == 100 && driver.equals("Manual")) {
                sendManualMessage(loadingBar);
            }
        }
    }

    public void sendAnimatedMessage(View view){
        Intent intent = new Intent(this, PlayAnimationActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void sendManualMessage(View view){
        Intent intent = new Intent(this, PlayManuallyActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void setRobot(String str){
        //Spinner tempBuilder = (Spinner) view;
        robot = str;
        Log.v(TAG, "Setting robot to " + robot);
    }
}
