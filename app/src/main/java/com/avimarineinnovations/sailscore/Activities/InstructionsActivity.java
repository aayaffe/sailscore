package com.avimarineinnovations.sailscore.Activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import com.avimarineinnovations.sailscore.R;

public class InstructionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.instructions, menu);
		return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

}
