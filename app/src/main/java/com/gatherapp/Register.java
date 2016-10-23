package com.gatherapp;

import android.app.DatePickerDialog;
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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private ArrayList userInfo;

    private TextInputLayout ilName;
    private TextInputLayout ilEmail;
    private TextInputLayout ilContactNum;
    private TextView tvGenderError;

    private ImageView ivImage;
    private EditText etName;
    private RadioGroup rgGender;
    private EditText etEmail;
    private EditText etBirthday;
    private Spinner spCountry;
    private EditText etContactNum;
    private Button btnDone;
    int year, month, day;

    String userId;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Edit Profile");

        userInfo = new Login().getUserInfo();

        ilName = (TextInputLayout) findViewById(R.id.ilName);
        ilEmail = (TextInputLayout) findViewById(R.id.ilEmail);
        ilContactNum = (TextInputLayout) findViewById(R.id.ilContactNum);

        tvGenderError = (TextView) findViewById(R.id.tvGenderError);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        etName = (EditText) findViewById(R.id.etName);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etBirthday = (EditText) findViewById(R.id.etBirthday);
        spCountry = (Spinner) findViewById(R.id.spCountry);
        etContactNum = (EditText) findViewById(R.id.etContactNum);
        btnDone = (Button) findViewById(R.id.btnDone);

        etName.addTextChangedListener(new MyTextWatcher(etName));
        etEmail.addTextChangedListener(new MyTextWatcher(etEmail));
        etContactNum.addTextChangedListener(new MyTextWatcher(etContactNum));


        //private method of your class


        etBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean b) {
                if (b) {
                    OpenDatePicker(v);
                }
            }
        });
        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDatePicker(v);
            }
        });
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbFemale || i == R.id.rbMale) {
                    tvGenderError.setText("");
                }
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        initialize();

        DatabaseHandler db = new DatabaseHandler(this);
        if(db.isExists(userInfo.get(0).toString())){
            User user1 = db.getContact(userInfo.get(0).toString());
            etContactNum.setText(user1.getContactNum());
            spCountry.setSelection(getIndex(spCountry,user1.getLocation()));
        }
    }

    public void submitForm() {
        if (!validateName() || !validateGender() || !validateEmail() || !validateContactNum()) {
            return;
        }

        String rbGender = ((RadioButton)findViewById(rgGender.getCheckedRadioButtonId())).getText().toString();

        /*
        INSERT TO SQLITE
         */
        DatabaseHandler db = new DatabaseHandler(this);
        if(db.isExists(userInfo.get(0).toString())) {
            db.updateContact(new User(userInfo.get(0).toString(),
                    etName.getText().toString(),
                    rbGender,
                    etEmail.getText().toString(),
                    etBirthday.getText().toString(),
                    spCountry.getSelectedItem().toString(),
                    etContactNum.getText().toString()));
            Log.wtf("DATABASE","UPDATED");
        }
        else{
            db.addContact(new User(userInfo.get(0).toString(),
                    etName.getText().toString(),
                    rbGender,
                    etEmail.getText().toString(),
                    etBirthday.getText().toString(),
                    spCountry.getSelectedItem().toString(),
                    etContactNum.getText().toString()));
            Log.wtf("DATABASE","CRAETED " +userInfo.get(0).toString());
        }
        new PHPPost(this, 1).execute(userInfo.get(0).toString(),
                etName.getText().toString(),
                rbGender,
                etEmail.getText().toString(),
                etBirthday.getText().toString(),
                spCountry.getSelectedItem().toString(),
                etContactNum.getText().toString());

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        userId = sharedpreferences.getString("UserId", new String());

        editor.putString("UserId", Profile.getCurrentProfile().getId());
        editor.commit();
        startActivity(new Intent(Register.this, Home.class));
        finish();
    }

    public void initialize() {
        new getImage(userInfo.get(0).toString()).execute();
        etName.setText(userInfo.get(1).toString() + " " + userInfo.get(2).toString());
        etEmail.setText(userInfo.get(4).toString());
        etBirthday.setText(userInfo.get(5).toString());

        //RadioButton Gender
        switch (userInfo.get(3).toString()) {
            case "male":
                rgGender.check(R.id.rbMale);
                break;
            case "female":
                rgGender.check(R.id.rbFemale);
                break;
        }

        //Location
        initializeSpinner();
    }

    public void initializeSpinner() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        for (Locale loc : locale) {
            String country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country))
                countries.add(country);
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, countries);
        spCountry.setAdapter(adapter);
    }

    public void OpenDatePicker (View v) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Register.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _day) {
                        etBirthday.setText(_day + "/" + (_month + 1) + "/" + _year);
                    }
                }, year, month, day);
        dpd.show();
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
            ivImage.setImageBitmap(getCroppedBitmap(bitmap));
        }
    }

    private boolean validateName() {
        if (etName.getText().toString().trim().isEmpty()) {
            ilName.setError("Enter Name");
            requestFocus(etName);
            return false;
        } else {
            ilName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateGender() {
        if (rgGender.getCheckedRadioButtonId() == -1) {
            tvGenderError.setText("Select Gender");
            return false;
        } else {
            tvGenderError.setText("");
        }
        return true;
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            ilEmail.setError("Enter Valid Email");
            requestFocus(etEmail);
            return false;
        } else {
            ilEmail.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateContactNum() {
        if (etContactNum.getText().toString().trim().isEmpty()) {
            ilContactNum.setError("Enter Contact Number");
            requestFocus(etContactNum);
            return false;
        } else {
            ilContactNum.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etName:
                    validateName();
                    break;
                case R.id.etEmail:
                    validateEmail();
                    break;
                case R.id.etContactNum:
                    validateContactNum();
                    break;
            }
        }
    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
}
