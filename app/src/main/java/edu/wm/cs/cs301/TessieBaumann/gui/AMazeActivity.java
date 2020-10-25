package edu.wm.cs.cs301.TessieBaumann.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import edu.wm.cs.cs301.TessieBaumann.R;

public class AMazeActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amazeactivity);
        Spinner builderSpinner = (Spinner) findViewById(R.id.builderspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builderSpinner.setAdapter(adapter);


    }
}
