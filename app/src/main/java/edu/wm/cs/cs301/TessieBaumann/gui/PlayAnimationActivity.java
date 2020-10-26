package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.wm.cs.cs301.TessieBaumann.R;

public class PlayAnimationActivity extends AppCompatActivity {

    private static final String TAG = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_animation_activity);
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
}
