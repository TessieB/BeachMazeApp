package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    private static final int MAX_MAP_SIZE = 10;  //max size that the map can be
    private static final int ROBOT_INITIAL_ENERGY = 3500;  //amount of energy driver starts with
    private String robot;  //sensor configuration chosen by user
    private int mapSize = 5;  //default map size
    private int animationSpeed = 5;  //default animation speed
    private ProgressBar remainingEnergy;  //remaining energy of robot
    private int pathLength = 10;  //path length of robot through maze
    private int shortestPathLength = 8;  //shortest possible path length through the maze
    private String reasonLost = "Broken Robot"; //if the robot lost, tells why


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
        setContentView(R.layout.play_animation_activity);
        Bundle bundle = getIntent().getExtras();
        robot = bundle.getString("Robot");
        if(robot != null) {
            setRobotSensors(robot);
        }
        setSizeOfMap();
        setAnimationSpeed();
        remainingEnergy = (ProgressBar) findViewById(R.id.remainingEnergyProgressBar);
        remainingEnergy.setMax(ROBOT_INITIAL_ENERGY);
        remainingEnergy.setProgress(1500);
        TextView remainingEnergyText = (TextView) findViewById(R.id.remainingEnergyTextView);
        remainingEnergyText.setText("Remaining Energy: " + remainingEnergy.getProgress());
    }

    /**
     * This method sets the size of the map to the
     * size requested by the user.
     */
    private void setSizeOfMap(){
        final SeekBar mapSize1 = (SeekBar) findViewById(R.id.animatedMapSizeSeekBar);
        final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        mapSize1.setProgress(0);
        mapSize1.setMax(MAX_MAP_SIZE);
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
        Log.v(TAG, "Map Size: " + mapSize);
        Toast toast = Toast.makeText(getApplicationContext(), "Map Size: " + mapSize, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * This method sets the speed of the animation to what
     * is requested by the user.
     */
    private void setAnimationSpeed(){
        final SeekBar animationSpeed1 = (SeekBar) findViewById(R.id.animationSpeedSeekBar);
        final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        animationSpeed1.setProgress(0);
        animationSpeed1.setMax(MAX_MAP_SIZE);
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
        Log.v(TAG, "Animation Speed: " + animationSpeed);
        Toast toast = Toast.makeText(getApplicationContext(), "Animation Speed: " + animationSpeed, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * This method makes the app switch
     * to the LosingActivity
     * @param view of the GO2LOSING button
     */
    public void sendLosingMessage(View view){
        Intent intent = new Intent(this, LosingActivity.class);
        Bundle bundle = getIntent().getExtras();
        bundle.putString("Reason Lost", reasonLost);
        bundle.putString("Path Length", pathLength + "");
        bundle.putString("Shortest Path Length", shortestPathLength + "");
        bundle.putString("Energy Consumption", (ROBOT_INITIAL_ENERGY - remainingEnergy.getProgress()) + "");
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
        bundle.putString("Path Length", pathLength + "");
        bundle.putString("Shortest Path Length", shortestPathLength + "");
        bundle.putString("Energy Consumption", (ROBOT_INITIAL_ENERGY - remainingEnergy.getProgress()) + "");
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
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }


    /**
     * This method makes the map appear
     * on the screen if turned on and makes it
     * disappear if turned off
     * @param view of the show map button
     */
    public void showMap(View view){
        if(((ToggleButton)view).isChecked()) {
            Log.v(TAG, "Showing Map: On");
            Toast toast = Toast.makeText(getApplicationContext(), "Showing Map: On", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }
        else{
            Log.v(TAG, "Showing Map: Off");
            Toast toast = Toast.makeText(getApplicationContext(), "Showing Map: Off", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
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
            Log.v(TAG, "Play");
            Toast toast = Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }
        else{
            Log.v(TAG, "Pause");
            Toast toast = Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
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
        if(robot.equals("Premium")){
            setRobotSensorsColors(new boolean[]{true, true, true, true});
        }
        else if(robot.equals("Mediocre")){
            setRobotSensorsColors(new boolean[]{false, false, true, true});
        }
        else if(robot.equals("Soso")){
            setRobotSensorsColors(new boolean[]{true, true, false, false});
        }
        else if(robot.equals("Shaky")){
            setRobotSensorsColors(new boolean[]{false, false, false, false});
        }
    }


    /**
     * This method sets the sensors on the screen to
     * green if they are reliable and red if they
     * are unreliable
     * @param sensorGreen tells which sensors are reliable
     */
    private void setRobotSensorsColors(boolean[] sensorGreen){
        Log.v(TAG, "Robot; " + sensorGreen[0]);
        TextView leftSensorText = (TextView) findViewById(R.id.leftSensorTextView);
        TextView rightSensorText = (TextView) findViewById(R.id.rightSensorTextView);
        TextView frontSensorText = (TextView) findViewById(R.id.frontSensorTextView);
        TextView backSensorText = (TextView) findViewById(R.id.backSensorTextView);
        if(sensorGreen[0]){
            leftSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
        else{
            leftSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
        if(sensorGreen[1]){
            rightSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
        else{
            rightSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
        if(sensorGreen[2]){
            frontSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
        else{
            frontSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
        if(sensorGreen[3]){
            backSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
        else{
            backSensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }

    }
}
