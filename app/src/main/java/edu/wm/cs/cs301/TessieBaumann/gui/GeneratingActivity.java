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
import edu.wm.cs.cs301.TessieBaumann.generation.Factory;
import edu.wm.cs.cs301.TessieBaumann.generation.Maze;
import edu.wm.cs.cs301.TessieBaumann.generation.MazeFactory;
import edu.wm.cs.cs301.TessieBaumann.generation.Order;

/**
 * This class allows the user to pick whether or not they
 * want to play the maze manually or want the maze solved with a
 * Wizard or Wall Follower robot. Additionally, this class allows
 * the user to choose a robot that either has all reliable sensors,
 * all unreliable sensors, or a mixture of reliable and unreliable sensors.
 * While the user is picking these options, the maze is being loaded with
 * a progress bar showing how much of the maze has been loaded at the bottom
 * of the screen. Once the maze has been loaded and the driver has been selected,
 * a button pops up that allows the user to start playing the game. If the user
 * selected to play the game manually, the app then goes to PlayManuallyActivity.
 * Otherwise, the app goes to PlayAnimationActivity. If the user presses the back
 * arrow, then the app will return to AMazeActivity.
 *
 * 	Collaboration: AMazeActivity, PlayManuallyActivity, PlayAnimationActivity
 *
 * @author Tessie Baumann
 */

public class GeneratingActivity extends AppCompatActivity implements Order {


    private static final String TAG = "Run Thread";  //message key
    private static final String KEY = "my message key";  //message key
    private ProgressBar loadingBar;  //progress bar for loading maze
    private Handler myHandler;  //handler to send messages from background thread to UI thread
    private String driver;  //driver that the maze is going to use
    private boolean backPressed = false;  //tells whether or not the back button has been pressed
    private String robot = "Premium";  //sensor configuration that the user chooses
    private Order.Builder builder;
    private int skillLevel;
    private boolean rooms;
    private int seed;
    protected Factory factory;
    private int percentdone = 0;
    public static Maze mazeConfig;


    public GeneratingActivity(){
    }

    /*public GeneratingActivity(Bundle bundle){
        factory = new MazeFactory();
        switch(bundle.getString("Maze Generator")){
            case "Prim":
                builder = Builder.Prim;
                break;
            case "Eller":
                builder = Builder.Eller;
                break;
            case "DFS":
                builder = Builder.DFS;
                break;
        }
        skillLevel = bundle.getInt("Skill Level");
        rooms = bundle.getBoolean("Rooms");
        seed = 13;
    }*/


    private void init(Bundle bundle){
        factory = new MazeFactory();
        switch(bundle.getString("Maze Generator")){
            case "Prim":
                builder = Builder.Prim;
                break;
            case "Eller":
                builder = Builder.Eller;
                break;
            case "DFS":
                builder = Builder.DFS;
                break;
        }
        skillLevel = bundle.getInt("Skill Level");
        rooms = bundle.getBoolean("Rooms");
        seed = 13;
    }


    /**
     * This method sets the content view to the
     * xml file generating_activity.xml, puts possible
     * robot values into a spinner, updates the app on what
     * robot was chosen, and receives messages from the runThread(View view)
     * method on when to update the progress of the progress bar
     * that tells how much of the maze has been loaded.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        Log.d("bundle", bundle.getString("Maze Generator"));
        init(bundle);
        //Log.d("builder", builder.toString());
        factory.order(this);
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
        //runThread(loadingBar);
        /*myHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                String progressBarMessage = bundle.getString(KEY);
                Log.v(TAG, progressBarMessage);
                loadingBar.incrementProgressBy(10);
                showStartButton();
            }
        };*/

    }

    /**
     * Allows external increase to percentage in generating mode.
     * Internal value is only updated if it exceeds the last value and is less or equal 100
     * @param percentage gives the new percentage on a range [0,100]
     * @return true if percentage was updated, false otherwise
     */
    @Override
    public void updateProgress(int percentage) {
        if (this.percentdone < percentage && percentage <= 100) {
            this.percentdone = percentage;
            loadingBar.setProgress(percentdone);
        }
    }

    /**
     * This method tells when the back
     * button has been pressed and then
     * goes back to the title screen activity.
     */
    @Override
    public void onBackPressed(){
        Log.v(TAG, "back button pressed in Generating Activity");
        backPressed = true;
        super.onBackPressed();
    }


    /**
     * This method creates a background thread that
     * updates the progress bar, giving the appearance that
     * a maze is being loaded.
     * @param view which is the progress bar that loads the maze
     */
    /*public void runThread(View view){
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
    }*/


    /**
     * This method sets the driver to be the
     * driver that the user clicks on.
     * @param view which is the driver radio button
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
     * This method reveals the start button that
     * allows the user to start the game once the
     * maze has fully loaded (when the progress bar has
     * reached 100%) and the user has chosen a driver
     */
    private void showStartButton(){
        Log.v(TAG, "Loading bar: " + loadingBar.toString());
        if(driver != null && loadingBar.getProgress() == 100) {
            Button startButton = (Button) findViewById(R.id.startGameButton);
            startButton.setVisibility(startButton.VISIBLE);
        }
    }


    /**
     * This method makes the app move to the next activity
     * when the start playing button has been pressed,
     * which is PlayManuallyActivity if the user chose the
     * manual driver or is PlayAnimationActivity if the user chose
     * the Wall Follower or Wizard driver.
     * @param view which is the start button
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
     * This method moves the app to the
     * PlayAnimationActivity class.
     * @param view which is the start button
     */
    public void sendAnimatedMessage(View view){
        Intent intent = new Intent(this, PlayAnimationActivity.class);
        Bundle bundle = getIntent().getExtras();
        bundle.putString("Driver", driver);
        bundle.putString("Robot", robot);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * This method moves the app to the
     * PlayManuallyActivity class.
     * @param view which is the start button
     */
    public void sendManualMessage(View view){
        Intent intent = new Intent(this, PlayManuallyActivity.class);
        Bundle bundle = getIntent().getExtras();
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * This method checks to see what maze robot
     * the user wants to use and tells that to the builder.
     * The default value for this method is Premium
     * @param str which is the robot specifier
     */
    private void setRobot(String str){
        robot = str;
        Log.v(TAG, "Setting robot to " + robot);
        Toast toast = Toast.makeText(getApplicationContext(), "Robot: " + robot, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return !rooms;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void deliver(Maze mazeConfig) {
        this.mazeConfig = mazeConfig;
    }

}
