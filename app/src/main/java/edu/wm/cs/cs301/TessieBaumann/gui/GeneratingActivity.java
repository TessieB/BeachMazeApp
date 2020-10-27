package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.TessieBaumann.R;

public class GeneratingActivity extends AppCompatActivity {


    private static final String TAG = "Run Thread";  //message key
    private static final String KEY = "my message key";  //message key
    private ProgressBar loadingBar;  //progress bar for loading maze
    private Handler myHandler;  //handler to send messages from background thread to UI thread
    private String driver;  //driver that the maze is going to use
    private boolean backPressed = false;  //tells whether or not the back button has been pressed
    private String robot = "Premium";  //sensor configuration that the user chooses


    /**
     This method sets the content view to the
     xml file generating_activity.xml, puts possible
     robot values into a spinner, updates the app on what
     robot was chosen, and receives messages from the runThread(View view)
     method on when to update the progress of the progress bar
     that tells how much of the maze has been loaded.
     @param savedInstanceState
     */
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
                showStartButton();
            }
        };

    }

    /**
     This method tells when the back
     button has been pressed and then
     goes back to the title screen activity.
     */
    @Override
    public void onBackPressed(){
        Log.v(TAG, "back button pressed in Generating Activity");
        backPressed = true;
        super.onBackPressed();
    }


    /**
     This method creates a background thread that
     updates the progress bar, giving the appearance that
     a maze is being loaded.
     @param view which is the progress bar that loads the maze
     */
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
                        Thread.sleep(500);
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


    /**
     This method sets the driver to be the
     driver that the user clicks on.
     @param view which is the driver radio button
     */
    public void setDriver(View view){
        RadioButton tempDriver = (RadioButton) view;
        driver = tempDriver.getText().toString();
        Log.d(TAG, "Driver: " + driver);
        Toast toast = Toast.makeText(getApplicationContext(), "Driver: " + driver, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
        showStartButton();
    }


    /**
     This method reveals the start button that
     allows the user to start the game once the
     maze has fully loaded (when the progress bar has
     reached 100%) and the user has chosen a driver/
     */
    private void showStartButton(){
        Log.v(TAG, "Loading bar: " + loadingBar.toString());
        if(driver != null && loadingBar.getProgress() == 100) {
            Button startButton = (Button) findViewById(R.id.startGameButton);
            startButton.setVisibility(startButton.VISIBLE);
        }
    }


    /**
     This method makes the app move to the next activity
     when the start playing button has been pressed,
     which is PlayManuallyActivity if the user chose the
     manual driver or is PlayAnimationActivity if the user chose
     the Wall Follower or Wizard driver.
     @param view which is the start button
     */
    public void moveToNextActivity(View view){
        if (driver.equals("Wall Follower")) {
            sendAnimatedMessage(view);
        } else if ( driver.equals("Wizard")) {
            sendAnimatedMessage(view);
        } else if (driver.equals("Manual")) {
            sendManualMessage(view);
        }
    }


    /**
     This method moves the app to the
     PlayAnimationActivity class.
     @param view which is the start button
     */
    public void sendAnimatedMessage(View view){
        Intent intent = new Intent(this, PlayAnimationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Robot", robot);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     This method moves the app to the
     PlayManuallyActivity class.
     @param view which is the start button
     */
    public void sendManualMessage(View view){
        Intent intent = new Intent(this, PlayManuallyActivity.class);
        startActivity(intent);
    }


    /**
     This method checks to see what maze robot
     the user wants to use and tells that to the builder.
     The default value for this method is Premium
     @param str which is the robot specifier
     */
    private void setRobot(String str){
        //Spinner tempBuilder = (Spinner) view;
        robot = str;
        Log.v(TAG, "Setting robot to " + robot);
        Toast toast = Toast.makeText(getApplicationContext(), "Robot: " + robot, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }
}
