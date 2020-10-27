package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.TessieBaumann.R;

/**
 * This class displays a winning message for the user, which includes
 * the length of the shortest path through the maze, the length of the
 * path taken by the user, and the amount of energy consumed by the robot
 * if the maze was played with a driver. There is also a button in the
 * bottom right hand corner that the user can press to play again, which
 * returns the app to AMazeActivity, or they can also press the backspace
 * button to return to AMazeActivity.
 *
 * 	Collaboration: AMazeActivity, PlayManuallyActivity, PlayAnimationActivity
 *
 * @author Tessie Baumann
 */

public class WinningActivity extends AppCompatActivity {


    private static final String TAG = "message";  //string message


    /**
     * This method sets the content view to the
     * xml file winning_activity.xml and
     * sets the screen to show the user's path,
     * the shortest possible path to solve the maze,
     * and the overall energy consumption if the maze
     * was not operated manually.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winning_activity);
        setWinningScreenMessages();
    }


    /**
     * This method sets the screen to show the
     * user's path, the shortest possible path to
     * solve the maze, and the overall energy
     * consumption if the maze was not operated manually.
     */
    private void setWinningScreenMessages(){
        TextView shortPath = (TextView) findViewById(R.id.shortestPathLengthTextView);
        TextView userPath = (TextView) findViewById(R.id.userPathLengthTextView);
        Bundle bundle = getIntent().getExtras();
        String pathLength = bundle.getString("Path Length");
        String shortestPathLength = bundle.getString("Shortest Path Length");
        shortPath.setText("Shortest Possible Path Length: " + shortestPathLength);
        userPath.setText("Your Path Length: " + pathLength);
        if(bundle.getString("Energy Consumption") != null){
            TextView overallEnergyConsumption = (TextView) findViewById(R.id.energyConsumptionTextView);
            overallEnergyConsumption.setVisibility(View.VISIBLE);
            String energyConsumption = bundle.getString("Energy Consumption");
            overallEnergyConsumption.setText("Overall Energy Consumption: " + energyConsumption);
        }
        Log.d("HEY", bundle.getString("Maze Generator"));
    }


    /**
     * This method makes the app
     * switch back to AMazeActivity so the
     * game can be played again
     * @param view play again button
     */
    public void sendTitleStageMessage(View view){
        Intent intent = new Intent(this, AMazeActivity.class);
        Bundle bundle = getIntent().getExtras();
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
        Log.v(TAG, "back button pressed in WinningActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }
}
