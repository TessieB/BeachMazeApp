package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import edu.wm.cs.cs301.TessieBaumann.R;

public class PlayAnimationActivity extends AppCompatActivity {

    private static final String TAG = "message";
    private String robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_animation_activity);
        Bundle bundle = getIntent().getExtras();
        robot = bundle.getString("Robot");
        if(robot != null) {
            setRobotSensors(robot);
        }
    }

    public void sendLosingMessage(View view){
        Intent intent = new Intent(this, LosingActivity.class);
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

    @Override
    public void onBackPressed(){
        Log.v(TAG, "back button pressed in PlayAnimationActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
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
