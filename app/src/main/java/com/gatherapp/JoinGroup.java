package com.gatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinGroup extends AppCompatActivity {

    ListView lvJoinGroup;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_GROUP_ID = "group_id";
    private static final String TAG_GROUP_NAME = "group_name";
    private static final String TAG_CATEGORY ="group_category";
    private static final String TAG_CREATED_BY = "created_by";
    private static final String TAG_GROUP_LOCATION = "group_location";
    private static final String TAG_MEMBERS = "members";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        lvJoinGroup = (ListView) findViewById(R.id.lvJoinGroup);

        String result = "";
        try {
            result = new GetGroups(getApplicationContext()).execute().get();
        }catch (Exception e) {
            Log.wtf("EXCEPTION", e.toString());
        }
        //String result = JSONParser.getResult();
        Log.wtf("RESULT", result);

        personList = new ArrayList<>();

        ArrayList<String> listdata = new ArrayList<String>();

        try {
            //JSONObject json = new JSONObject(result);
            JSONObject jsonObj = new JSONObject(result);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String group_id = c.getString(TAG_GROUP_ID);
                String group_name = c.getString(TAG_GROUP_NAME);
                String group_category = c.getString(TAG_CATEGORY);
                String group_created = c.getString(TAG_CREATED_BY);
                String group_location = c.getString(TAG_GROUP_LOCATION);
                String members = c.getString(TAG_MEMBERS);

                HashMap<String,String> persons = new HashMap<>();

                persons.put(TAG_GROUP_ID, group_id);
                persons.put(TAG_GROUP_NAME,group_name);
                persons.put(TAG_CATEGORY,group_category);
                persons.put(TAG_CREATED_BY, group_created);
                persons.put(TAG_GROUP_LOCATION,group_location);
                persons.put(TAG_MEMBERS,members);



                personList.add(persons);
                Log.wtf("PUTA",personList.toString());
            }

            ListAdapter adapter = new SimpleAdapter(
                    JoinGroup.this, personList, R.layout.list_item,
                    new String[]{TAG_GROUP_NAME,TAG_CATEGORY,TAG_GROUP_LOCATION},
                    new int[]{R.id.id, R.id.name, R.id.address}
            );
            lvJoinGroup.setAdapter(adapter);
            lvJoinGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // PUT EXTRA
                    Intent i = new Intent(JoinGroup.this, GroupDetails.class);
                    i.putExtra("STRING_I_NEED", personList.get(position));
                    startActivity(i);
                }
            });
        } catch (Exception e) {
            Log.wtf("JSONOBject", e.toString());
        }

        for (int i = 0; i < listdata.size(); i++) {
            Log.wtf("SADSAD", listdata.get(i));
        }

    }
}
