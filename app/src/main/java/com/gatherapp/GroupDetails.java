package com.gatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetails extends AppCompatActivity {

    TextView textView;
    TextView textView2;
    TextView textView3;
    ListView lvMembers;
    Button btnJoin;

    HashMap<String,String> persons;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_GROUP_ID = "group_id";
    private static final String TAG_GROUP_NAME = "group_name";
    private static final String TAG_CATEGORY ="group_category";
    private static final String TAG_CREATED_BY = "created_by";
    private static final String TAG_GROUP_LOCATION = "group_location";
    private static final String TAG_MEMBERS = "members";

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        lvMembers = (ListView) findViewById(R.id.lvMembers);
        btnJoin = (Button) findViewById(R.id.btnJoin);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        persons = new HashMap<>();
        persons = (HashMap) b.get("STRING_I_NEED");


        textView.setText((String) persons.get(TAG_GROUP_NAME));
        textView2.setText((String) persons.get(TAG_GROUP_LOCATION));
        textView3.setText((String) persons.get(TAG_CATEGORY));

        Log.wtf("MEMBERS", persons.get(TAG_MEMBERS).toString());

        String memb[] = persons.get(TAG_MEMBERS).toString().split("\\, ");
        List<String> memb2 = Arrays.asList(memb);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                memb2 );
        lvMembers.setAdapter(arrayAdapter);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        final String userId = sharedpreferences.getString("UserId", new String());

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Joined " +persons.get(TAG_GROUP_ID), Toast.LENGTH_LONG).show();
                new JoinGroupParser(getApplicationContext()).execute(userId, persons.get(TAG_GROUP_ID));
                finish();
            }
        });

    }
}
