/*
 * This activity is the primary screen for entering competitor details.
 * It maintains rows in the entries table in the database.
 */

package com.avimarineinnovations.sailscore.Activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avimarineinnovations.sailscore.ListAdapters.SailscoreDbAdapter;
import com.avimarineinnovations.sailscore.R;

public class EntryEditActivity extends Activity{
	private SailscoreDbAdapter mDbHelper;
	private EditText mHelmText;
	private EditText mYachtNameText;
	private EditText mCrewText;
	private EditText mClassText;
	private EditText mPyText;
	private EditText mOrcTotInshoreText;
	private EditText mOrcTotOffshoreText;
	private EditText mSailNumberText;
	private EditText mClubText;
	private Button mSaveButton;
	private Button mNextButton;
	private Long mRowId;
	private static final int RESULT_OK = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new SailscoreDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.entry_edit);
        //actionBar.setDisplayHomeAsUpEnabled(true);
		mYachtNameText = findViewById(R.id.yacht_name);
		mHelmText = (EditText) findViewById(R.id.helm);
		mCrewText = (EditText) findViewById(R.id.crew);
		mClassText = (EditText) findViewById(R.id.boat_class);
		mPyText = (EditText) findViewById(R.id.py);
		mOrcTotInshoreText = (EditText) findViewById(R.id.orcTotInshore);
		mOrcTotOffshoreText = (EditText) findViewById(R.id.orcTotOffshore);
		mSailNumberText = (EditText) findViewById(R.id.sail_number);
		mClubText = (EditText) findViewById(R.id.club);
		mSaveButton = (Button) findViewById(R.id.save_button);
		mNextButton = (Button) findViewById(R.id.next_button);  
		mRowId = savedInstanceState != null
				? savedInstanceState.getLong(SailscoreDbAdapter.KEY_ROWID)
				: null;
		registerButtonListenersAndSetDefaultText();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	

	private void setRowIdFromIntent() {
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null
					? extras.getLong(SailscoreDbAdapter.KEY_ROWID)
					: null;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mDbHelper.close();
		setResult(RESULT_OK);
	}

	/* The onResume method has to deal with situations when the activity
	 * is returned to either when a new entry is to be created but also
	 * when the activity is restarted without it coming from the intent
	 * via the list activity.(non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
		setRowIdFromIntent();
		populateFields();
	}
	
	private void populateFields() {
		if (mRowId != null && mRowId != 0) {
			Cursor entry = mDbHelper.fetchEntry(mRowId);
			mYachtNameText.setText(entry.getString(
					entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_NAME)));
			mHelmText.setText(entry.getString(
					entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_HELM)));
			mCrewText.setText(entry.getString(
					entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_CREW)));
			mClassText.setText(entry.getString(
					entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_CLASS)));
			mPyText.setText(entry.getString(
					entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_PY)));
      mOrcTotInshoreText.setText(entry.getString(
          entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_ORC_INSHORE)));
      mOrcTotOffshoreText.setText(entry.getString(
          entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_ORC_OFFSHORE)));
			mSailNumberText.setText(entry.getString(
					entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_SAILNO)));
			mClubText.setText(entry.getString(
					entry.getColumnIndexOrThrow(SailscoreDbAdapter.KEY_CLUB)));
			entry.close();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mRowId != null) {
			outState.putLong(SailscoreDbAdapter.KEY_ROWID, mRowId);
		}
	}

	private void registerButtonListenersAndSetDefaultText() {
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveState();
				//setResult(RESULT_OK);
				Toast.makeText(EntryEditActivity.this, getString(R.string.entry_saved_message), Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		
		/* When this button is pressed the previous entries are all cleared and
		 * focus transfered to the top edit text view to allow a new helm to 
		 * be entered. 
		 */
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveState();
				//setResult(RESULT_OK);
				Toast.makeText(EntryEditActivity.this, getString(R.string.entry_saved_message), Toast.LENGTH_SHORT).show();
				mYachtNameText.setText(null);
				mHelmText.setText(null);
				mCrewText.setText(null);
				mClassText.setText(null);
				mPyText.setText(null);
        mOrcTotInshoreText.setText(null);
        mOrcTotOffshoreText.setText(null);
				mSailNumberText.setText(null);
				mClubText.setText(null);
				mYachtNameText.requestFocus();
				mRowId = null; // to trigger a new entry
			}
		});
	}

	private void saveState() {
		String yachtName = mYachtNameText.getText().toString();
		String helm = mHelmText.getText().toString();
		String crew = mCrewText.getText().toString();
		String boatclass = mClassText.getText().toString();
		int py;
		try {
			py = Integer.parseInt(mPyText.getText().toString());
		} catch (NumberFormatException e) {
			py = 1000;
		}
    double orcTotInshore;
    try {
      orcTotInshore = Double.parseDouble(mOrcTotInshoreText.getText().toString());
    } catch (NumberFormatException e) {
      orcTotInshore = 1.000;
    }
    double orcTotOffshore;
    try {
      orcTotOffshore = Double.parseDouble(mOrcTotOffshoreText.getText().toString());
    } catch (NumberFormatException e) {
      orcTotOffshore = 1.000;
    }
		String sailnumber = mSailNumberText.getText().toString();
		String club = mClubText.getText().toString();
		if (mRowId == null) {
			long id = mDbHelper.createEntry(yachtName, helm, crew, boatclass, py, orcTotInshore, orcTotOffshore, sailnumber, club);
			if (id > 0) {
				mRowId = id;
			}
		}else {
				mDbHelper.updateEntry(mRowId, yachtName, helm, crew, boatclass, py,orcTotInshore, orcTotOffshore, sailnumber, club);
			}
	}
	
}
