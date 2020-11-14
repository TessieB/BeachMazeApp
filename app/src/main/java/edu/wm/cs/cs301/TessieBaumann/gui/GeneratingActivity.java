package edu.wm.cs.cs301.TessieBaumann.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

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


    private static final String TAG = "GeneratingActivity";  //message key
    private static final String PROGRESS_KEY = "my message key";  //message key
    private ProgressBar loadingBar;  //progress bar for loading maze
    private Handler handler;  //handler to send messages from background thread to UI thread
    private String driver;  //driver that the maze is going to use
    private boolean backPressed = false;  //tells whether or not the back button has been pressed
    private String robot = "Premium";  //sensor configuration that the user chooses
    private Order.Builder builder;  // builder for the maze
    private int skillLevel;  // difficulty level of the maze
    private boolean rooms;  // whether or not the maze has rooms
    private int seed;  // seed to generate random maze
    protected Factory factory;  // factory created to order maze
    private int percentdone;  // gives the percent that the maze has loaded
    public static Maze mazeConfig;  // static variable for the maze configuration
    private boolean deterministic = false;  // tells whether or not the maze is perfect
    private final int mode = Activity.MODE_PRIVATE;  // mode for the preference storage
    private final String MYPREFS = "My Preferences";  // string name for myPreferences
    private SharedPreferences myPreferences;  // storage for maze settings
    private SharedPreferences.Editor myEditor;  // editor for myPreferences
    private final int DEFAULT_SEED = 13;  // default seed value



    public GeneratingActivity(){
        seed = 13;
        percentdone = 0;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Bundle bundle = getIntent().getExtras();
        init(bundle);
        Boolean revist = bundle.getBoolean("Revisit");
        storeMazeSettings(revist);
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
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                int progressBarMessage = bundle.getInt(PROGRESS_KEY);
                showStartButton();
            }
        };

    }

    /**
     * Initializes each of the elements in the order
     * with the values chosen by the user in AMazeActivity
     * @param bundle
     */
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
        if(!deterministic){
            Random rand = new Random();
            seed = rand.nextInt();
        }
    }


    /**
     * Initializes myPreferences and myEditor and calls
     * generatePreviousMaze() if the user wants to revisit
     * their last maze; otherwise, it calls storePreferences()
     * to store the preferences for this maze
     * @param revisit
     */
    private void storeMazeSettings(Boolean revisit){
        myPreferences = getSharedPreferences(MYPREFS, mode);
        myEditor = myPreferences.edit();
        if(revisit){
            generatePreviousMaze();
        }
        else{
            storePreferences();
        }
    }


    /**
     * Puts the maze information in myPreferences with the
     * builder, skill level, and rooms as the key and the seed as the value
     */
    private void generatePreviousMaze(){
        seed = myPreferences.getInt(builder + " " + skillLevel + " " + rooms, DEFAULT_SEED);
    }


    /**
     * stores the key and value pair from this maze in myPreferences
     */
    private void storePreferences(){
        myEditor.clear();
        myEditor.putInt(builder + " " + skillLevel + " " + rooms, seed);
        myEditor.commit();
    }



    /**
     * Allows external increase to percentage in generating mode.
     * Internal value is only updated if it exceeds the last value and is less or equal 100
     * @param percentage gives the new percentage on a range [0,100]
     * @return true if percentage was updated, false otherwise
     */
    @Override
    public void updateProgress(int percentage) {
        Log.v(TAG, "Updating progress loaded to " + percentage);
        if (this.percentdone < percentage && percentage <= 100) {
            this.percentdone = percentage;
            loadingBar.setProgress(percentdone);
        }
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt(PROGRESS_KEY, this.percentdone);
        message.setData(bundle);
        handler.sendMessage(message);
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
     * This method sets the driver to be the
     * driver that the user clicks on.
     * @param view which is the driver radio button
     */
    public void setDriver(View view){
        RadioButton tempDriver = (RadioButton) view;
        driver = tempDriver.getText().toString();
        Log.v(TAG, "Driver: " + driver);
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
    }

    /**
     * Sets deterministic to true in order to generate
     * the same maze over and over again and to false
     * in order to generate random mazes
     * @param deterministic
     */
    public void setDeterministic(boolean deterministic){
        this.deterministic = deterministic;
    }

    /**
     * Returns the difficulty level that the user chooses
     * @return int skill level
     */
    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    /**
     * Returns the builder that the user chooses
     * @return Builder builder
     */
    @Override
    public Builder getBuilder() {
        return builder;
    }

    /**
     * Returns whether or not the user wants rooms
     * @return boolean rooms
     */
    @Override
    public boolean isPerfect() {
        return !rooms;
    }

    /**
     * Returns the random seed
     * @return int seed
     */
    @Override
    public int getSeed() {
        return seed;
    }


    /**
     * Sets the static variable mazeConfig so that
     * every class can access the maze once it has been
     * created
     */
    @Override
    public void deliver(Maze mazeConfig) {
        this.mazeConfig = mazeConfig;
    }

}
