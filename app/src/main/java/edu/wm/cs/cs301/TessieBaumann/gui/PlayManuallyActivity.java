package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.TessieBaumann.R;

/**
 * This class allows the user to play the game manually,
 * using the front, right, and left arrows, and the jump button on
 * the screen. The user can also turn the map, solution, and visible
 * walls on and off, depending on what they want to be able to see when
 * playing the game. In addition to that, the user can change the size of
 * the map that they see if the map is on. Once the user has passed the
 * finish line, the app then goes to WinningActivity. If the user presses
 * the back arrow, then the app will return to AMazeActivity.
 *
 * 	Collaboration: AMazeActivity, GeneratingActivity, WinningActivity
 *
 * @author Tessie Baumann
 */

public class PlayManuallyActivity extends AppCompatActivity {


    private static final String TAG = "Move";  //message string
    private static final int MAX_MAP_SIZE = 10;  //max size that the map can be
    private int pathLength = 3;  //length of the path the user has taken
    private int mapSize = 5;  //default map size
    private int shortestPathLength = 1;  //shortest possible path length, temp value

    /**
     * This method sets the content view to the
     * xml file play_manually_activity.xml and
     * sets the size of the map according to the user input.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_manually_activity);
        setSizeOfMap();

    }


    /**
     * This method sets the size of the map to the
     * size requested by the user.
     */
    private void setSizeOfMap(){
        final SeekBar mapSize1 = (SeekBar) findViewById(R.id.mapSizeSeekBar);
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
     * This method checks to see if the
     * back button has been pressed, and if
     * finds the answer to be true, makes the app return
     * to AMazeActivity.
     */
    @Override
    public void onBackPressed(){
        Log.v(TAG, "back button pressed in PlayManuallyActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }


    /**
     * This method makes the app switch to
     * the WinningActivity state
     * @param view of the short cut button
     */
    public void sendWinningMessage(View view){
        Intent intent = new Intent(this, WinningActivity.class);
        Bundle bundle = getIntent().getExtras();
        bundle.putString("Path Length", pathLength + "");
        bundle.putString("Shortest Path Length", shortestPathLength + "");
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * This method makes the user move one
     * step forwards through the maze
     * @param view of the top arrow
     */
    public void moveForwards(View view){
        pathLength++;
        Log.v(TAG, "Moves forwards one step");
        Toast toast = Toast.makeText(getApplicationContext(), "Moved forwards 1 step", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * This method makes the user turn
     * left in the maze
     * @param view of the top left
     */
    public void turnLeft(View view){
        Log.v(TAG, "Turns left");
        Toast toast = Toast.makeText(getApplicationContext(), "Turned left", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * This method makes the user turn
     * right in the maze
     * @param view of the right arrow
     */
    public void turnRight(View view){
        Log.v(TAG, "Turns right");
        Toast toast = Toast.makeText(getApplicationContext(), "Turned right", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * This method makes the user jump
     * forwards over a wall in the maze
     * @param view of the jump button
     */
    public void jump(View view){
        pathLength++;
        Log.v(TAG, "Jump forwards");
        Toast toast = Toast.makeText(getApplicationContext(), "Jumped forwards", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
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
     * This method makes the solution appear
     * on the screen if turned on and makes it
     * disappear if turned off
     * @param view of the show solution button
     */
    public void showSolution(View view){
        if(((ToggleButton)view).isChecked()) {
            Log.v(TAG, "Showing Solution: On");
            Toast toast = Toast.makeText(getApplicationContext(), "Showing Solution: On", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }
        else{
            Log.v(TAG, "Showing Solution: Off");
            Toast toast = Toast.makeText(getApplicationContext(), "Showing Solution: Off", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    /**
     * This method makes any visible walls appear
     * on the screen if turned on and makes them
     * disappear if turned off
     * @param view of the show visible walls button
     */
    public void showVisibleWalls(View view){
        if(((ToggleButton)view).isChecked()) {
            Log.v(TAG, "Showing Visible Walls: On");
            Toast toast = Toast.makeText(getApplicationContext(), "Showing Visible Walls: On", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }
        else{
            Log.v(TAG, "Showing Visible Walls: Off");
            Toast toast = Toast.makeText(getApplicationContext(), "Showing Visible Walls: Off", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
