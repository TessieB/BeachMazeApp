package edu.wm.cs.cs301.TessieBaumann.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import edu.wm.cs.cs301.TessieBaumann.R;

public class AMazeActivity extends AppCompatActivity  {

    private static final int SKILL_LEVEL_MAX = 16;
    private int skillLevelNum = 0;
    private static final String TAG = "message";
    private String builder = "DFS";
    private boolean wantRooms = false;

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

    }

    public void onRoomCheckBoxChecked(View view){
        CheckBox rooms = (CheckBox) view;
        wantRooms = rooms.isChecked();
        Log.v(TAG, "User wants rooms: " + wantRooms);
    }

    private void setSkillLevel(View view){
        ProgressBar tempSkillLevel = (ProgressBar) view;
        skillLevelNum = tempSkillLevel.getProgress();
        Log.v(TAG, "Setting skill level to " + skillLevelNum);
    }

    private void setBuilder(String str){
        //Spinner tempBuilder = (Spinner) view;
        builder = str;
        Log.v(TAG, "Setting builder to " + builder);
    }


    public void sendMessage(View view){
        Intent intent = new Intent(this, GeneratingActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
