package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.TessieBaumann.R;

public class PlayManuallyActivity extends AppCompatActivity {


    private static final String TAG = "Move";
    private int pathLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_manually_activity);


    }

    @Override
    public void onBackPressed(){
        Log.v(TAG, "back button pressed in PlayManuallyActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void sendWinningMessage(View view){
        Intent intent = new Intent(this, WinningActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void moveForwards(View view){
        pathLength++;
        Log.v(TAG, "Moves forwards one step");
        Toast toast = Toast.makeText(getApplicationContext(), "Moved forwards 1 step", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void turnLeft(View view){
        Log.v(TAG, "Turns left");
        Toast toast = Toast.makeText(getApplicationContext(), "Turned left", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void turnRight(View view){
        Log.v(TAG, "Turns right");
        Toast toast = Toast.makeText(getApplicationContext(), "Turned right", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void jump(View view){
        pathLength++;
        Log.v(TAG, "Jump forwards");
        Toast toast = Toast.makeText(getApplicationContext(), "Jumped forwards", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        toast.show();
    }

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
