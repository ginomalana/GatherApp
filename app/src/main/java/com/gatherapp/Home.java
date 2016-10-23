package com.gatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;

public class Home extends AppCompatActivity {

    private ImageView ivUserImage;
    private Button btnUserLogout;
    private Button btnUserEditProfile;
    private TextView tvUserName;
    private ListView lvGroups;
    private LinearLayout llItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    String userId;

    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";

    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_GROUP_NAME = "group_name";
    private static final String TAG_CATEGORY ="group_category";
    private static final String TAG_GROUP_LOCATION = "group_location";
    private static final String TAG_CREATION_DATE = "date_created";

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, JoinGroup.class);
                startActivity(intent);

            }
        });

        DatabaseHandler dbHandler = new DatabaseHandler(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
        btnUserLogout = (Button) findViewById(R.id.btnUserLogout);
        btnUserEditProfile = (Button) findViewById(R.id.btnUserEditProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        lvGroups = (ListView) findViewById(R.id.lvGroups);
        llItem = (LinearLayout) findViewById(R.id.llItem);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initialize();
            }
        });

        btnUserLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Login().onLogoutClicked();
                finish();
                startActivity(new Intent(Home.this, MainActivity.class));
            }
        });

        btnUserEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(Home.this, Register.class));
            }
        });

        if (dbHandler.getContactsCount() > 0)
            initialize();

    }

    public void initialize() {
        userId = sharedpreferences.getString("UserId", new String());
        User user = new DatabaseHandler(getApplicationContext()).getContact(userId);

        tvUserName.setText(user.getName());
        try {
            new getImage(userId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Log.wtf("GETIMAGE", e.toString());
        }
        String result = "";
        try {
            result = new JSONParser(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userId).get();
        } catch (Exception e) {
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
            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String group_name = c.getString(TAG_GROUP_NAME);
                String group_category = c.getString(TAG_CATEGORY);
                String group_location = c.getString(TAG_GROUP_LOCATION);
                String creation_date = c.getString(TAG_CREATION_DATE);

                HashMap<String, String> persons = new HashMap<>();

                persons.put(TAG_GROUP_NAME, group_name);
                persons.put(TAG_CATEGORY, group_category);
                persons.put(TAG_GROUP_LOCATION, group_location);
                persons.put(TAG_CREATION_DATE, creation_date);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    Home.this, personList, R.layout.list_item,
                    new String[]{TAG_GROUP_NAME, TAG_CATEGORY, TAG_GROUP_LOCATION},
                    new int[]{R.id.id, R.id.name, R.id.address}
            );
            lvGroups.setAdapter(adapter);
            lvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // PUT EXTRA
                    Intent i = new Intent(Home.this, Calendar.class);
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
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    class getImage extends AsyncTask<String, String, String> {

        private Bitmap bitmap;
        String userId;

        public getImage(String userID) {
            this.userId = userID;
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                URL imageURL = new URL("https://graph.facebook.com/" + userId + "/picture?type=normal");
                Log.wtf("URL", "https://graph.facebook.com/" + userId + "/picture?type=normal");
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            } catch (Exception ex) {
                Log.wtf("URL Exception", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            ivUserImage.setImageBitmap(getCroppedBitmap(bitmap));
        }
    }
}