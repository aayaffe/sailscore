/*
 * This activity is the primary screen for entering competitor details.
 * It maintains rows in the entries table in the database.
 */

package com.avimarineinnovations.sailscore.Activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.avimarineinnovations.sailscore.ListAdapters.EntriesSelectListAdapter;
import com.avimarineinnovations.sailscore.ListAdapters.OrcCertsSelectListAdapter;
import com.avimarineinnovations.sailscore.ListAdapters.SailscoreDbAdapter;
import com.avimarineinnovations.sailscore.ORCCertificateType;
import com.avimarineinnovations.sailscore.Objects.CountryObj;
import com.avimarineinnovations.sailscore.Objects.ORCCertObj;
import com.avimarineinnovations.sailscore.Objects.OrcRowObj;
import com.avimarineinnovations.sailscore.Objects.rowObj;
import com.avimarineinnovations.sailscore.R;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OrcImportActivity extends Activity {

  private SailscoreDbAdapter mDbHelper;
  private Button mSaveButton;
  private Long mRowId;
  private static final int RESULT_OK = 1;
  private static final String TAG = "OrcImportActivity";
  private Spinner mCountriesSpinner;
  private ArrayList<OrcRowObj> combinedList;
  private OrcCertsSelectListAdapter mAdapter;
  private Map<String, String> countries;
  private Map<String, ORCCertObj> certs;
  private ListView certListView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDbHelper = new SailscoreDbAdapter(this);
    mDbHelper.open();
    setContentView(R.layout.orc_import);
    combinedList = new ArrayList<>();
    certListView = findViewById(R.id.orc_list);
    mAdapter = new OrcCertsSelectListAdapter(this, combinedList);
    certListView.setAdapter(mAdapter);
    mSaveButton = findViewById(R.id.save_button);
    mCountriesSpinner = findViewById(R.id.countries_spinner);
    mCountriesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String countryCode = countries.get(mCountriesSpinner.getSelectedItem().toString());
        certs = getCountryBoats(countryCode);
        combinedList.clear();
        certListView.setAdapter(mAdapter);
        for (ORCCertObj cert : certs.values()){
          combinedList.add(ORCCertObjToRowObj(cert));
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
    mRowId = savedInstanceState != null
        ? savedInstanceState.getLong(SailscoreDbAdapter.KEY_ROWID)
        : null;
    registerButtonListenersAndSetDefaultText();

    countries = getCountriesAbbrs();
    if (countries!=null && !countries.isEmpty()) {
      String countriesArray[] = Arrays.copyOf(countries.keySet().toArray(),countries.keySet().size(),String[].class);
      Arrays.sort(countriesArray);
      ArrayAdapter adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_spinner_dropdown_item, countriesArray);
      mCountriesSpinner.setAdapter(adapter);
    }

  }

  private OrcRowObj ORCCertObjToRowObj(ORCCertObj cert) {
    if (cert==null)
      return null;
    OrcRowObj ret = new OrcRowObj();
    ret.setBoatClass(cert.getYachtsClass());
    ret.setYachtName(cert.getYachtsName());
    ret.setSail(cert.getSailNo());
    ret.setCdl(cert.getCDL());
    return ret;
  }

  private Map<String, ORCCertObj> getCountryBoats(String countryCode) {
    try {
      Toast.makeText(this,"Downloading boats list",Toast.LENGTH_LONG).show();
      String json = new RetrieveStreamTask().execute("http://data.orc.org/public/WPub.dll?action=DownRMS&CountryId="+countryCode+"&ext=json").get();
//      Log.d(TAG,json);
      Map<String, ORCCertObj> ret = new HashMap<>();
      if (json!=null){
        try {
          JSONObject root = new JSONObject(json);
          JSONArray certsJson = root.getJSONArray("rms");

          for (int i=0; i<certsJson.length(); i++) {
            JSONObject jo = certsJson.getJSONObject(i);
            ORCCertObj cert = convertORCJsonToORCCertObj(jo);
            ret.put(cert.getSailNo(), cert);
          }
          return ret;

        } catch (Exception e) {
          Log.e(TAG,"Error parsing countries XML",e);
        }
      }
    } catch(Exception e){
      return null;
    }
    return null;
  }

  @Nullable
  private ORCCertObj convertORCJsonToORCCertObj(JSONObject jo) {
    if (jo == null)
      return null;
    ORCCertObj ret = new ORCCertObj();
    try{
      ret.setNatAuth(jo.getString("NatAuth"));
      ret.setCertNo(jo.getString("CertNo"));
      ret.setRefNo(jo.getString("RefNo"));
      ret.setSailNo(jo.getString("SailNo"));
      ret.setYachtsName(jo.getString("YachtName"));
      ret.setYachtsClass(jo.getString("Class"));
      ret.setCertType(ORCCertificateType.fromString(jo.getString("C_Type")));
      ret.setDivision(jo.getString("Division"));
      //ret.setIssueDate(Date.valueOf(jo.getString("IssueDate")));
      ret.setAgeYear(jo.getInt("Age_Year"));
      ret.setLOA(jo.getDouble("LOA"));
      ret.setCDL(jo.getDouble("CDL"));
      ret.setTMFInshore(jo.getDouble("TMF_Inshore"));
      ret.setTMFOffshore(jo.getDouble("TMF_Offshore"));
      ret.setILCWA(jo.getDouble("ILCWA"));
      ret.setOSN(jo.getDouble("OSN"));
      ret.setTNInshoreLow(jo.getDouble("TN_Inshore_Low"));
      ret.setTNInshoreMedium(jo.getDouble("TN_Inshore_Medium"));
      ret.setTNInshoreHigh(jo.getDouble("TN_Inshore_High"));
      ret.setTNOffshoreLow(jo.getDouble("TN_Offshore_Low"));
      ret.setTNOffshoreMedium(jo.getDouble("TN_Offshore_Medium"));
      ret.setTNOffshoreHigh(jo.getDouble("TN_Offshore_High"));
      ret.setTNDInshoreLow(jo.getDouble("TND_Inshore_Low"));
      ret.setTNDInshoreMedium(jo.getDouble("TND_Inshore_Medium"));
      ret.setTNDInshoreHigh(jo.getDouble("TND_Inshore_High"));
      ret.setTNDOffshoreLow(jo.getDouble("TND_Offshore_Low"));
      ret.setTNDOffshoreMedium(jo.getDouble("TND_Offshore_Medium"));
      ret.setTNDOffshoreHigh(jo.getDouble("TND_Offshore_High"));


    }catch (Exception e){
      Log.e(TAG,"Error converting json to cert",e);
      return null;
    }

    return ret;
  }

  private Map<String, String> getCountriesAbbrs() {
    try {
      Toast.makeText(this,"Downloading countries list",Toast.LENGTH_LONG).show();
      String xml = new RetrieveStreamTask().execute("http://data.orc.org/public/WPub.dll").get();
      Log.d(TAG,xml);
      Map<String, String> ret = new HashMap<>();
      if (xml!=null){
        try {
          InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
          Document doc = dBuilder.parse(is);

          Element element=doc.getDocumentElement();
          element.normalize();

          NodeList nList = doc.getElementsByTagName("ROW");
          CountryObj co = new CountryObj();
          for (int i=0; i<nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
              Element element2 = (Element) node;
              co.setId(getValue("CountryId",element2));
              co.setName(getValue("CountryName",element2));
//              co.setLastUpdate(Date.valueOf(getValue("LastUpdate",element2))); //TODO add parsing
              co.setCertificatesCount(Integer.valueOf(getValue("CertCount",element2)));
              co.setClubCertCount(Integer.valueOf(getValue("ClubCert",element2)));
            }
            ret.put(co.getName(),co.getId());
          }
          return ret;

        } catch (Exception e) {
          Log.e(TAG,"Error parsing countries XML",e);
        }
      }
    } catch(Exception e){
      return null;
    }
    return null;
  }

  private static String getValue(String tag, Element element) {
    NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
    Node node = nodeList.item(0);
    return node.getNodeValue();
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
        Toast.makeText(OrcImportActivity.this, getString(R.string.entry_saved_message),
            Toast.LENGTH_SHORT).show();
        finish();
      }
    });
  }

  private void saveState() {
    for (OrcRowObj o:combinedList){
      if (o.getCheckState()){
        ORCCertObj cert = certs.get(o.getSail());
        mDbHelper.createEntry(cert.getYachtsName(), "", "", cert.getYachtsClass(), 1000, cert.getTMFInshore(), cert.getTMFOffshore(), cert.getSailNo(), "");
      }
    }
  }
}

class RetrieveStreamTask extends AsyncTask<String, Void, String> {
  private static final String TAG = "RetrieveStreamTask";

  protected String doInBackground(String... urls) {
    try {
      URL url = new URL(urls[0]);
      //create the new connection
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.connect();
      //this will be used in reading the data from the internet
      InputStream inputStream = urlConnection.getInputStream();
      String ret = readFullyAsString(inputStream,"UTF-8");
      urlConnection.disconnect();
      return ret;
      //catch some possible errors...
    } catch (MalformedURLException e) {
      Log.e(TAG,"Error getting countries xml",e);
    } catch (IOException e) {
      Log.e(TAG,"Error getting countries xml",e);
    }
    return null;
  }

  public String readFullyAsString(InputStream inputStream, String encoding)
      throws IOException {
    return readFully(inputStream).toString(encoding);
  }

  public byte[] readFullyAsBytes(InputStream inputStream)
      throws IOException {
    return readFully(inputStream).toByteArray();
  }

  private ByteArrayOutputStream readFully(InputStream inputStream)
      throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int length = 0;
    while ((length = inputStream.read(buffer)) != -1) {
      baos.write(buffer, 0, length);
    }
    return baos;
  }
  protected void onPostExecute(String feed) {

  }
}
