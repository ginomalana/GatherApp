package com.gatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Gino on 10/22/2016.
 */

public class PHPPost extends AsyncTask<String, Void, String> {
    private Context context;
    private int byGetOrPost = 0;

    //flag 0 means get and 1 means post.(By default it is get.)
    public PHPPost(Context context, int flag) {
        this.context = context;
        byGetOrPost = flag;
    }

    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {
        if (byGetOrPost == 0) { //means by Get Method
            try {
                String userId = (String)arg0[0];
                String name = (String)arg0[1];
                String gender = (String)arg0[2];
                String email = (String)arg0[3];
                String birthday = (String)arg0[4];
                String location = (String)arg0[5];
                String contactNum = (String)arg0[6];
                String link = "http://myfeu.tech/gatherapp/function/login.php?userId=" + userId
                        + "&name=" + name.replaceAll(" ","%20")
                        + "&gender=" + gender
                        + "&email=" + email
                        + "&birthday=" + birthday
                        + "&location=" + location.replaceAll(" ","%20")
                        + "&contactNum=" + contactNum;

                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
        else {
            try {
                String userId = (String) arg0[0];
                String name = (String) arg0[1];
                String gender = (String) arg0[2];
                String email = (String) arg0[3];
                String birthday = (String) arg0[4];
                String location = (String) arg0[5];
                String contactNum = (String) arg0[6];

                String link = "http://myfeu.tech/gatherapp/function/login.php";
                String data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
                data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");
                data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("birthday", "UTF-8") + "=" + URLEncoder.encode(birthday, "UTF-8");
                data += "&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");
                data += "&" + URLEncoder.encode("contactNum", "UTF-8") + "=" + URLEncoder.encode(contactNum, "UTF-8");


                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.wtf("PHPPost", result);
    }
}
