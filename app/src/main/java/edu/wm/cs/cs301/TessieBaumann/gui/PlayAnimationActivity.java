package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.wm.cs.cs301.TessieBaumann.gui.Robot.Direction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import edu.wm.cs.cs301.TessieBaumann.R;


/**
 * This class plays the maze with either a Wall Follower or Wizard driver,
 * depending on what the user chose in GeneratingActivity, and goes to
 * the WinningActivity if the driver reaches the end of the maze successfully,
 * or goes to the LosingState if the driver runs out of energy or fails for
 * some other reason. There is also an option to show the map that can be
 * turned on with a button and an option to change the size of the map with a
 * seek bar at the top of the screen. If the user chooses to, they can pause
 * the game and restart it with a button at the bottom, or they can slow down
 * or speed up the driver with a seek bar at the bottom of the screen. Finally,
 * this class also shows how much energy the robot has left at the bottom of the
 * screen with a progress bar. If the user presses the back arrow, then the app
 * will return to AMazeActivity.
 *
 * 	Collaboration: AMazeActivity, GeneratingActivity, WinningActivity, LosingActivity
 *
 * @author Tessie Baumann
 */

public class PlayAnimationActivity extends AppCompatActivity {

    private static final String TAG = "message";  //string message key
    private static final String KEY = "my message key";  //message key
    private static final String FAILURE_KEY = "sensor has failed";  //message key
    private static final int MAX_MAP_SIZE = 80;  //max size that the map can be
    private static final int MAX_ANIMATION_SPEED = 20;  //max animation speed for the robot
    private static final int ROBOT_INITIAL_ENERGY = 3500;  //amount of energy driver starts with

    private String sensorConfig;  //sensor configuration chosen by user
    private int mapSize = 15;  //default map size
    private int animationSpeed = 10;  //default animation speed
    private ProgressBar remainingEnergy;  //remaining energy of robot
    private int shortestPathLength;  //shortest possible path length through the maze
    private String reasonLost = "Ran Out of Energy"; //if the robot lost, tells why
    private StatePlaying statePlaying;  // allows the user to play the game
    private MazePanel panel;  // panel to draw the maze on
    private Robot robot;  // robot that runs through the maze
    private Wizard wizard;  // driver to make robot go through the maze
    private RobotDriver wallFollower;  // driver that follows wallFollower algorithm

    boolean[] reliableSensor;  // tells what sensors are reliable and which are not
    private DistanceSensor leftSensor;  // robot's left sensor
    private DistanceSensor rightSensor;  // robot's right sensor
    private DistanceSensor frontSensor;  // robot's front sensor
    private DistanceSensor backSensor;  // robot's back sensor
    private static final int MEAN_TIME_BETWEEN_FAILURES = 4000;  // average time between sensor failures
    private static final int MEAN_TIME_TO_REPAIR = 2000;  // average time to repair sensor failures
    private boolean isWizard = false;  // tells if the driver is the wizard algorithm
    public static Handler myHandler;  // handler used to send messages between classes


    /**
     * This method sets the content view to the
     * xml file play_animation_activity.xml and
     * sets the size of the map according to the user input,
     * sets the animation speed according to the user input,
     * and sets the progress bar with the robot's remaining energy.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.play_animation_activity);
        setProgressBar();
        AMazeActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.christmas);
        AMazeActivity.mediaPlayer.setLooping(true);
        AMazeActivity.mediaPlayer.start();

        Bundle bundle = getIntent().getExtras();
        sensorConfig = bundle.getString("Robot");
        if(sensorConfig != null) {
            setRobotSensors(sensorConfig);
        }

        statePlaying = new StatePlaying();
        panel = findViewById(R.id.mazePanelViewAnimated);
        int[] startPos = GeneratingActivity.mazeConfig.getStartingPosition();
        shortestPathLength = GeneratingActivity.mazeConfig.getDistanceToExit(startPos[0], startPos[1]);
        statePlaying.setPlayAnimationActivity(this);
        statePlaying.start(panel);

        startDriverPlaying(bundle);
        handleMessage();

        setSizeOfMap();
        setAnimationSpeed();
    }


    /**
     * receives any messages for the class and calls the
     * appropriate methods depending on what message is being received
     */
    private void handleMessage(){
        myHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                int remainingEnergyMessage = bundle.getInt(KEY, -1);
                String[] sensorInfo = bundle.getStringArray(FAILURE_KEY);
                if(remainingEnergyMessage != -1){
                    updateProgressBar(remainingEnergyMessage);
                }
                if(sensorInfo != null){
                    failAndRepairSensors(sensorInfo);
                }
                Log.v(TAG, remainingEnergyMessage + "");
                boolean lostGame = bundle.getBoolean("lost", false);
                if(lostGame){
                    sendLosingMessage(panel);
                }
            }
        };
    }


    /**
     * Takes the sensor information provided that tells what sensor's
     * failure status is being changed and whether or not that sensor
     * is failing or has just been repaired and calls setRobotSensorsColors
     * to set the sensor to green or red on the screen, depending on its status
     * @param sensorInfo
     */
    private void failAndRepairSensors(String[] sensorInfo){
        if(sensorInfo[1].equalsIgnoreCase("true")){
            setRobotSensorsColors(true, sensorInfo[0]);
        }
        else {
            setRobotSensorsColors(false, sensorInfo[0]);
        }
    }


    /**
     * Sets up the progress bar that shows the amount of energy
     * the robot has left
     */
    private void setProgressBar(){
        remainingEnergy = (ProgressBar) findViewById(R.id.remainingEnergyProgressBar);
        remainingEnergy.setMax(ROBOT_INITIAL_ENERGY);
        remainingEnergy.setProgress(ROBOT_INITIAL_ENERGY);
        final TextView remainingEnergyText = (TextView) findViewById(R.id.remainingEnergyTextView);
        remainingEnergyText.setText("Remaining Energy: " + remainingEnergy.getProgress());
    }


    /**
     * Updates the progress bar that tells the user
     * how much energy the robot has left with the current value
     * passed in by the handler
     * @param remainingEnergy of the robot
     */
    private void updateProgressBar(int remainingEnergy){
        final TextView remainingEnergyText = (TextView) findViewById(R.id.remainingEnergyTextView);
        this.remainingEnergy.setProgress(remainingEnergy);
        remainingEnergyText.setText("Remaining Energy: " + this.remainingEnergy.getProgress());
    }


    /**
     * Makes the driver selected by the user begin
     * playing in the maze
     * @param bundle
     */
    private void startDriverPlaying(Bundle bundle){
        String driver = bundle.getString("Driver");
        if(driver.equalsIgnoreCase("Wizard")){
            setWizardPlaying();
        }
        else{
            setWallFollowerPlaying();
        }
    }


    /**
     * Sets up the wizard class and everything
     * that is necessary for the wizard to run
     * so that the maze is solved automatically by
     * the wizard algorithm
     */
    private void setWizardPlaying() {
        Log.v(TAG, "setting Wizard as Animated Driver");
        isWizard = true;
        ReliableRobot robot = new ReliableRobot();
        wizard = new Wizard();
        robot.setStatePlaying(statePlaying);
        robot.setPlayAnimationActivity(this);
        wizard.setRobot(robot);
        wizard.setMaze(GeneratingActivity.mazeConfig);
        try {
            boolean wizard1 = wizard.drive2Exit();
        }
        catch(Exception e) {
            Log.d("exception e", e.getMessage());
            sendLosingMessage(panel);
        }
    }


    /**
     * Sets up the wallFollower class and everything
     * that is necessary for the wallFollower to run
     * so that the maze is solved automatically by
     * the wallFollower algorithm
     */
    private void setWallFollowerPlaying() {
        Log.v(TAG, "setting WallFollower as Animated Driver");
        robot = new UnreliableRobot();
        wallFollower = new WallFollower();
        robot.setStatePlaying(statePlaying);
        robot.setPlayAnimationActivity(this);
        if(reliableSensor != null) {
            for(int i = 0; i < reliableSensor.length; i++) {
                if(reliableSensor[i]) {
                    switch(i) {
                        case 0:
                            setSensor("front", true);
                            break;
                        case 1:
                            setSensor("left", true);
                            break;
                        case 2:
                            setSensor("right", true);
                            break;
                        case 3:
                            setSensor("back", true);
                            break;
                    }
                }
                else {
                    if(i-1 >= 0 && !reliableSensor[i-1] || i-2 >= 0 && !reliableSensor[i-2] || i-3 >= 0 && !reliableSensor[i-3]) {
                        try {
                            Thread.sleep(1300);
                        }
                        catch(Exception e) {
                            System.out.println("wallFollowerPlaying in Controller.java: Error with sleeping");
                        }
                    }

                    switch(i) {
                        case 0:
                            setSensor("front", false);
                            break;
                        case 1:
                            setSensor("left", false);
                            break;
                        case 2:
                            setSensor("right", false);
                            break;
                        case 3:
                            setSensor("back", false);
                            break;
                    }
                }
            }
        }
        else {
            setSensor("front", true);
            setSensor("left", true);
            setSensor("right", true);
            setSensor("back", true);
        }
        wallFollower.setRobot(robot);
        wallFollower.setMaze(GeneratingActivity.mazeConfig);
        try {
            wallFollower.drive2Exit();
        }
        catch(Exception e) {
            sendLosingMessage(panel);
        }
    }


    /**
     * Creates a new reliable or unreliable
     * sensor in the direction passed in by the
     * parameter
     * @param whichSensor that tells what sensor is being set
     * @param reliable that is true if the sensor
     * is supposed to be reliable and false if
     * it is supposed to be unreliable
     */
    public void setSensor(String whichSensor, boolean reliable) {
        if(whichSensor.equalsIgnoreCase("left")) {
            if(reliable) {
                leftSensor = new ReliableSensor();
                leftSensor.setSensorDirection(Direction.LEFT);
                leftSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                leftSensor = new UnreliableSensor();
                leftSensor.setSensorDirection(Direction.LEFT);
                leftSensor.setMaze(GeneratingActivity.mazeConfig);
                leftSensor.setWhichSensor("Left");
                robot.startFailureAndRepairProcess(Direction.LEFT, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("right")) {
            if(reliable) {
                rightSensor = new ReliableSensor();
                rightSensor.setSensorDirection(Direction.RIGHT);
                rightSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                rightSensor = new UnreliableSensor();
                rightSensor.setSensorDirection(Direction.RIGHT);
                rightSensor.setMaze(GeneratingActivity.mazeConfig);
                rightSensor.setWhichSensor("Right");
                robot.startFailureAndRepairProcess(Direction.RIGHT, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("front")) {
            if(reliable) {
                frontSensor = new ReliableSensor();
                frontSensor.setSensorDirection(Direction.FORWARD);
                frontSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                frontSensor = new UnreliableSensor();
                frontSensor.setSensorDirection(Direction.FORWARD);
                frontSensor.setMaze(GeneratingActivity.mazeConfig);
                frontSensor.setWhichSensor("Front");
                robot.startFailureAndRepairProcess(Direction.FORWARD, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("back")) {
            if(reliable) {
                backSensor = new ReliableSensor();
                backSensor.setSensorDirection(Direction.BACKWARD);
                backSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                backSensor = new UnreliableSensor();
                backSensor.setSensorDirection(Direction.BACKWARD);
                backSensor.setMaze(GeneratingActivity.mazeConfig);
                backSensor.setWhichSensor("Back");
                robot.startFailureAndRepairProcess(Direction.BACKWARD, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
    }


    /**
     * This method returns the sensor that corresponds
     * to the direction that is passed in
     * @param direction of the sensor that is requested
     * @return the sensor with the passed in direction
     */
    public DistanceSensor getSensor(Direction direction) {
        switch(direction){
            case FORWARD :
                return frontSensor;
            case BACKWARD :
                return backSensor;
            case LEFT :
                return leftSensor;
            case RIGHT :
                return rightSensor;
        }
        return frontSensor;
    }

    /**
     * This method sets the size of the map to the
     * size requested by the user.
     */
    private void setSizeOfMap(){
        final SeekBar mapSize1 = (SeekBar) findViewById(R.id.animatedMapSizeSeekBar);
        final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        mapSize1.setMin(1);
        mapSize1.setMax(MAX_MAP_SIZE);
        mapSize1.setProgress(mapSize);
        mapSize1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int tempMapSize = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempMapSize = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setMapSize(tempMapSize);
            }
        });
    }


    /**
     * This method sets the map to be
     * the size requested by the user, which
     * was passed down to it through setSizeOfMap().
     * @param size of the map
     */
    private void setMapSize(int size){
        mapSize = size;
        statePlaying.setMapScale(mapSize);
        Log.v(TAG, "Map Size: " + mapSize);
    }

    /**
     * This method sets the speed of the animation to what
     * is requested by the user.
     */
    private void setAnimationSpeed(){
        final SeekBar animationSpeed1 = (SeekBar) findViewById(R.id.animationSpeedSeekBar);
        final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        animationSpeed1.setMax(MAX_ANIMATION_SPEED);
        animationSpeed1.setProgress(animationSpeed);
        animationSpeed1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int tempAnimationSpeed = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempAnimationSpeed = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setTheAnimationSpeed(tempAnimationSpeed);
            }
        });
    }


    /**
     * This method sets the animation to be
     * the speed requested by the user, which
     * was passed down to it through setAnimationSpeed().
     * @param speed of the animation
     */
    private void setTheAnimationSpeed(int speed){
        animationSpeed = speed;
        if(wizard!= null){
            wizard.setAnimationSpeed(animationSpeed);
        }
        else{
            wallFollower.setAnimationSpeed(animationSpeed);
        }
        Log.v(TAG, "Animation Speed: " + animationSpeed);
    }


    /**
     * This method makes the app switch
     * to the LosingActivity
     * @param view of the GO2LOSING button
     */
    public void sendLosingMessage(View view){
        Intent intent = new Intent(this, LosingActivity.class);
        Bundle bundle = getIntent().getExtras();
        int energyConsump = 0;
        int pathLength = 0;
        if(isWizard){
            pathLength = wizard.getPathLength();
            energyConsump = (int)wizard.getEnergyConsumption();
        }
        else{
            pathLength = wallFollower.getPathLength();
            energyConsump = (int)wallFollower.getEnergyConsumption();
        }
        bundle.putString("Reason Lost", reasonLost);
        bundle.putInt("Path Length", pathLength);
        bundle.putInt("Shortest Path Length", shortestPathLength);
        bundle.putInt("Energy Consumption", energyConsump);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * This method makes the app switch
     * to the WinningActivity
     * @param view of the GO2WINNING button
     */
    public void sendWinningMessage(View view){
        Intent intent = new Intent(this, WinningActivity.class);
        Bundle bundle = getIntent().getExtras();
        int energyConsump = 0;
        int pathLength = 0;
        if(isWizard){
            pathLength = wizard.getPathLength();
            energyConsump = (int)wizard.getEnergyConsumption();
        }
        else{
            pathLength = wallFollower.getPathLength();
            energyConsump = (int)wallFollower.getEnergyConsumption();
        }
        bundle.putInt("Path Length", pathLength);
        bundle.putInt("Shortest Path Length", shortestPathLength);
        bundle.putInt("Energy Consumption",  energyConsump);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * This method checks to see if the
     * back button has been pressed, and if
     * finds the answer to be true, makes the app return
     * to AMazeActivity.
     */
    @Override
    public void onBackPressed(){
        Log.v(TAG, "back button pressed in PlayAnimationActivity");
        if(isWizard){
            try {
                wizard.terminateThread();
            }
            catch(Exception e){}
        }
        else{
            try {
                wallFollower.terminateThread();
            }
            catch(Exception e){}
        }
        Intent intent = new Intent(this, AMazeActivity.class);
        Bundle bundle = getIntent().getExtras();
        intent.putExtras(bundle);
        AMazeActivity.mediaPlayer.stop();
        startActivity(intent);
    }


    /**
     * This method makes the map appear
     * on the screen if turned on and makes it
     * disappear if turned off
     * @param view of the show map button
     */
    public void showMap(View view){
        statePlaying.keyDown(Constants.UserInput.ToggleLocalMap, 1);
        statePlaying.keyDown(Constants.UserInput.ToggleSolution, 1);
        statePlaying.keyDown(Constants.UserInput.ToggleFullMap, 1);
        if(((ToggleButton)view).isChecked()) {
            Log.v(TAG, "Showing Map: On");
        }
        else{
            Log.v(TAG, "Showing Map: Off");
        }
    }


    /**
     * This method makes the animation
     * pause if turned on, or continue
     * playing if turned off
     * @param view of the play/pause button
     */
    public void playOrPauseGame(View view){
        if(((ToggleButton)view).isChecked()) {
            if(isWizard){
                wizard.terminateThread();
            }
            else{
                wallFollower.terminateThread();
            }
            Log.v(TAG, "Play");
        }
        else{
            if(isWizard){
                try{
                    wizard.drive2Exit();
                }
                catch(Exception e){
                    sendLosingMessage(panel);
                }
            }
            else{
                try{
                    wallFollower.drive2Exit();
                }
                catch(Exception e){
                    sendLosingMessage(panel);
                }
            }
            Log.v(TAG, "Pause");
        }
    }


    /**
     * This method makes the sensors on the screen
     * show up as green if they are reliable
     * and red if they are unreliable
     * @param robot with specified sensors picked by the user
     */
    private void setRobotSensors(String robot){
        Log.v(TAG, "Robot; " + robot);
        setRobotSensorsColors(true, "Left");
        setRobotSensorsColors(true, "Right");
        setRobotSensorsColors(true, "Front");
        setRobotSensorsColors(true, "Back");
        reliableSensor = new boolean[]{true, true, true, true};
        if(robot.equals("Mediocre")){
            setRobotSensorsColors(false, "Left");
            setRobotSensorsColors(false, "Right");
            reliableSensor[1] = false;
            reliableSensor[2] = false;
        }
        else if(robot.equals("Soso")){
            setRobotSensorsColors(false, "Front");
            setRobotSensorsColors(false, "Back");
            reliableSensor[0] = false;
            reliableSensor[3] = false;
        }
        else if(robot.equals("Shaky")){
            setRobotSensorsColors(false, "Left");
            setRobotSensorsColors(false, "Right");
            setRobotSensorsColors(false, "Front");
            setRobotSensorsColors(false, "Back");
            reliableSensor = new boolean[]{false, false, false, false};
        }
    }


    /**
     * This method sets the sensors on the screen to
     * green if they are reliable and red if they
     * are unreliable
     * @param sensorGreen tells which sensors are reliable
     */
    private void setRobotSensorsColors(boolean sensorGreen, String sensor){
        Log.v(TAG, "Robot; " + sensorGreen);
        TextView sensorText = (TextView) findViewById(R.id.leftSensorTextView);
        switch (sensor){
            case "Left":
                sensorText = (TextView) findViewById(R.id.leftSensorTextView);
                break;
            case "Right":
                sensorText = (TextView) findViewById(R.id.rightSensorTextView);
                break;
            case "Front":
                sensorText = (TextView) findViewById(R.id.frontSensorTextView);
                break;
            case "Back":
                sensorText = (TextView) findViewById(R.id.backSensorTextView);
                break;
        }
        if(sensorGreen){
            sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
        else{
            sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
    }

    /**
     * Pauses the music when the activity is paused
     */
    @Override
    public void onPause(){
        super.onPause();
        AMazeActivity.mediaPlayer.pause();
    }

    /**
     * Plays the music when the activity is resumed
     */
    @Override
    public void onResume(){
        super.onResume();
        AMazeActivity.mediaPlayer.start();
    }

}
