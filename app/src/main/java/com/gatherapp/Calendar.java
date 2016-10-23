package com.gatherapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Calendar extends AppCompatActivity {
    //String pageData[];	//Stores the text to swipe.
    ArrayList<ArrayList<String>> pageData;
    LayoutInflater inflater;	//Used to create individual pages
    ViewPager vp;	//Reference to class to swipe views
    HashMap<String,String> persons;
    private static final String TAG_CREATION_DATE = "date_created";
    private static final String TAG_GROUP_NAME = "group_name";

    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        //Get the data to be swiped through
        pageData = new ArrayList<>();



        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        persons = new HashMap<>();
        persons = (HashMap) b.get("STRING_I_NEED");

        getSupportActionBar().setTitle(persons.get(TAG_GROUP_NAME));

        fab = (FloatingActionButton) findViewById(R.id.fabCalendar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Calendar.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialogbox, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Calendar.this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });

        //ArrayList<String> dates = new ArrayList<String>();
        java.util.Calendar calendar = new GregorianCalendar();
        String string = persons.get(TAG_CREATION_DATE);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(string);
        } catch (Exception e) {
            Log.wtf("ASDAS", e.toString());
        }
        Log.wtf("TIME",date.toString());
        Log.wtf("TIME",java.util.Calendar.getInstance().getTime().toString());


        ArrayList<String> content;
        calendar.setTime(date);
        String d[];
        while (calendar.getTime().before(java.util.Calendar.getInstance().getTime()))
        {
            content = new ArrayList<>();
            Date result = calendar.getTime();
            Log.wtf("RES", result.toString());
                d = result.toString().split(" ");
            String in = d[0] + ", " + d[1] + " " + d[2] + ", " + d[5];
            Log.wtf("TANGINA", in);
                content.add(in);
            Log.wtf("PUTA",content.get(0));
                content.add("Matthew 28:19");
                content.add("\"Therefore go and make disciples of all nations, baptizing them in the name of the Father and of the Son and of the Holy Spirit\"");
                content.add("Love");
                pageData.add(content);
            calendar.add(java.util.Calendar.DATE, 1);
        }

        //Log.wtf("ASD",pageData.toString());
        //pageData.add(dates);
        //get an inflater to be used to create single pages
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Reference ViewPager defined in activity
        vp=(ViewPager)findViewById(R.id.viewPager);
        //set the adapter that will create the individual pages
        vp.setAdapter(new MyPagesAdapter());
        vp.setCurrentItem(pageData.size());
    }
    //Implement PagerAdapter Class to handle individual page creation
    class MyPagesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //Return total pages, here one for each data item
            return pageData.size();
        }
        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = inflater.inflate(R.layout.page, null);

            Log.wtf("POS", position + "");
            ((TextView)page.findViewById(R.id.textMessage)).setText(pageData.get(position).get(0));
            ((TextView)page.findViewById(R.id.tvVerse)).setText(pageData.get(position).get(1));
            ((TextView)page.findViewById(R.id.tvVerse2)).setText(pageData.get(position).get(2));
            ((TextView)page.findViewById(R.id.tvVerse3)).setText(pageData.get(position).get(3));
            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);
            return page;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //See if object from instantiateItem is related to the given view
            //required by API
            return arg0==(View)arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object=null;
        }
    }
}