package edu.wm.cs.cs301.TessieBaumann.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import edu.wm.cs.cs301.TessieBaumann.R;

/**
 * This class shows the welcome page of the app and allows the user
 * to choose a difficulty level between one and sixteen, a maze generator
 * that is either DFS, Prim, or Eller, and whether or not the maze will have rooms.
 * It also allows the user to either press an explore button, which sends the
 * app to the GeneratingActivity with the selected maze generator, difficulty
 * level, and room values, or a revisit button, that once again sends the app
 * to the GeneratingActivity, but this time with the same maze generator, difficulty
 * level, and room values that were used in the last implementation of the game.
 *
 * 	Collaboration: GeneratingActivity, WinningActivity, LosingActivity
 *
 * @author Tessie Baumann
 */

public class AMazeActivity extends AppCompatActivity  {

    private static final int SKILL_LEVEL_MAX = 15;  //max skill level that can be picked
    private int skillLevelNum = 0;  //tells the maze what skill level to use
    private static final String TAG = "message";
    private String builder = "DFS";  //tells the maze what builder to use
    private boolean wantRooms = false;  //tells whether or not the user wants rooms in their maze
    public static MediaPlayer mediaPlayer;


    /**
     * This method sets the content view to the
     * xml file amazeactivity.xml and puts the builder
     * options into a spinner and the skill level options into
     * a seek bar. It also updates the maze as to what builder to
     * use and what skill level to use whenever the user changes one
     * of these values.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amazeactivity);

        Spinner builderSpinner = (Spinner) findViewById(R.id.builderspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builderSpinner.setAdapter(adapter);
        builderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setBuilder(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final SeekBar skillLevel = (SeekBar) findViewById(R.id.skillLevelSeekBar);
        final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        skillLevel.setProgress(0);
        skillLevel.setMax(SKILL_LEVEL_MAX);
        skillLevelText.setText(skillLevel.getProgress() + " / " + SKILL_LEVEL_MAX);
        skillLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int tempSkillLevel = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempSkillLevel = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                skillLevelText.setText(tempSkillLevel + " / " + SKILL_LEVEL_MAX);
                setSkillLevel(skillLevel);
            }
        });

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ocean);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    /**
     * This method checks to see if the user wants rooms
     * in the maze and sends that information to the maze builder.
     * The default value is no rooms, or a perfect maze
     * @param view of the room check box
     */
    public void onRoomCheckBoxChecked(View view){
        CheckBox rooms = (CheckBox) view;
        wantRooms = rooms.isChecked();
        Log.v(TAG, "User wants rooms: " + wantRooms);
        Toast toast = Toast.makeText(getApplicationContext(), "Rooms: " + wantRooms, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * This method sets the skill/difficulty level
     * that the user wants for the maze.
     * The default value is 0
     * @param view which is the difficulty level seek bar
     */
    private void setSkillLevel(View view){
        ProgressBar tempSkillLevel = (ProgressBar) view;
        skillLevelNum = tempSkillLevel.getProgress();
        Log.v(TAG, "Setting skill level to " + skillLevelNum);
        Toast toast = Toast.makeText(getApplicationContext(), "Difficulty Level: " + skillLevelNum, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * This method checks to see what maze builder
     * the user wants to use and tells that to the builder.
     * The default value for this method is DFS
     * @param str which is the builder specifier
     */
    private void setBuilder(String str){
        builder = str;
        Log.v(TAG, "Setting builder to " + builder);
        Toast toast = Toast.makeText(getApplicationContext(), "Maze Generator: " + builder, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * This method makes the app move to the
     * next activity when the explore
     * button is pressed.
     * @param view of the explore button
     */
    public void sendMessage(View view){
        //GeneratingActivity generatingActivity = new GeneratingActivity(builder, skillLevelNum, wantRooms, 13);
        Intent intent = new Intent(this, GeneratingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("Skill Level", skillLevelNum);
        bundle.putString("Maze Generator", builder);
        bundle.putBoolean("Rooms", wantRooms);
        bundle.putBoolean("Revisit", false);
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
        Log.v(TAG, "back button pressed in PlayManuallyActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }


    /**
     * This method makes the app move to the
     * next activity with the same complexity
     * values of the previously played maze when the revisit
     * button is pressed.
     * @param view of the revisit  button
     */
    public void sendRevisitMessage(View view){
        Intent intent = new Intent(this, GeneratingActivity.class);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            bundle.putString("Energy Consumption", null);
            bundle.putBoolean("Revisit", true);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else{
            sendMessage(view);
        }
    }

}
