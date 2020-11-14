package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
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
    private static final int MAX_MAP_SIZE = 80;  //max size that the map can be
    private static final int MIN_MAP_SIZE = 1;  //min size that the map can be
    private int pathLength = 0;  //length of the path the user has taken
    private int mapSize = 15;  //default map size
    private int shortestPathLength = 0;  //shortest possible path length, temp value
    private MazePanel panel;  // panel used to draw maze
    StatePlaying statePlaying;  // class used to operate maze

    /**
     * This method sets the content view to the
     * xml file play_manually_activity.xml and
     * sets the size of the map according to the user input.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.play_manually_activity);
        AMazeActivity.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.christmas);
        AMazeActivity.mediaPlayer.setLooping(true);
        AMazeActivity.mediaPlayer.start();
        statePlaying = new StatePlaying();
        panel = findViewById(R.id.mazePanelView);
        int[] startPos = GeneratingActivity.mazeConfig.getStartingPosition();
        shortestPathLength = GeneratingActivity.mazeConfig.getDistanceToExit(startPos[0], startPos[1]);
        statePlaying.setPlayManuallyActivity(this);
        statePlaying.start(panel);
        setSizeOfMap();
    }


    /**
     * This method sets the size of the map to the
     * size requested by the user.
     */
    private void setSizeOfMap(){
        final SeekBar mapSize1 = (SeekBar) findViewById(R.id.mapSizeSeekBar);
        final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        mapSize1.setMin(MIN_MAP_SIZE);
        mapSize1.setProgress(15);
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
        statePlaying.setMapScale(mapSize);
        Log.v(TAG, "Map Size: " + mapSize);
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
        Bundle bundle = getIntent().getExtras();
        intent.putExtras(bundle);
        AMazeActivity.mediaPlayer.stop();
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
        bundle.putInt("Path Length", pathLength);
        bundle.putInt("Shortest Path Length", shortestPathLength);
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
        statePlaying.keyDown(Constants.UserInput.Up, 1);
        Log.v(TAG, "Moves forwards one step");
    }


    /**
     * This method makes the user turn
     * left in the maze
     * @param view of the top left
     */
    public void turnLeft(View view){
        statePlaying.keyDown(Constants.UserInput.Left, 1);
        Log.v(TAG, "Turns left");
    }


    /**
     * This method makes the user turn
     * right in the maze
     * @param view of the right arrow
     */
    public void turnRight(View view){
        statePlaying.keyDown(Constants.UserInput.Right, 1);
        Log.v(TAG, "Turns right");
    }


    /**
     * This method makes the user jump
     * forwards over a wall in the maze
     * @param view of the jump button
     */
    public void jump(View view){
        statePlaying.keyDown(Constants.UserInput.Jump, 1);
        pathLength++;
        Log.v(TAG, "Jump forwards");
    }


    /**
     * This method makes the map appear
     * on the screen if turned on and makes it
     * disappear if turned off
     * @param view of the show map button
     */
    public void showMap(View view){
        statePlaying.keyDown(Constants.UserInput.ToggleLocalMap, 1);
        if(((ToggleButton)view).isChecked()) {
            Log.v(TAG, "Showing Map: On");
        }
        else{
            Log.v(TAG, "Showing Map: Off");
        }
    }


    /**
     * This method makes the solution appear
     * on the screen if turned on and makes it
     * disappear if turned off
     * @param view of the show solution button
     */
    public void showSolution(View view){
        statePlaying.keyDown(Constants.UserInput.ToggleSolution, 1);
        if(((ToggleButton)view).isChecked()) {
            Log.v(TAG, "Showing Solution: On");
        }
        else{
            Log.v(TAG, "Showing Solution: Off");
        }
    }


    /**
     * This method makes any visible walls appear
     * on the screen if turned on and makes them
     * disappear if turned off
     * @param view of the show visible walls button
     */
    public void showVisibleWalls(View view){
        statePlaying.keyDown(Constants.UserInput.ToggleFullMap, 1);
        if(((ToggleButton)view).isChecked()) {
            Log.v(TAG, "Showing Visible Walls: Off");
        }
        else{
            Log.v(TAG, "Showing Visible Walls: On");
        }
    }
}
