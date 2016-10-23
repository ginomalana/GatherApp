package com.gatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gino on 10/22/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_USER = "user";

    // Contacts Table Columns names
    private static final String KEY_USER_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_CONTACT_NUM = "contactnum";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_GENDER + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_BIRTHDAY + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_CONTACT_NUM + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getId()); // Contact Name
        values.put(KEY_NAME, user.getName()); // Contact Phone
        values.put(KEY_GENDER, user.getGender());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_BIRTHDAY, user.getBirthday());
        values.put(KEY_LOCATION, user.getLocation());
        values.put(KEY_CONTACT_NUM, user.getContactNum());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    User getContact(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[] { KEY_USER_ID,
                        KEY_NAME, KEY_GENDER, KEY_EMAIL, KEY_BIRTHDAY,
                        KEY_LOCATION, KEY_CONTACT_NUM}, KEY_USER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User uer = new User(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6));
        // return contact
        return uer;
    }

    // Getting All Contacts
    public List<User> getAllContacts() {
        List<User> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setGender(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setBirthday(cursor.getString(4));
                user.setLocation(cursor.getString(5));
                user.setContactNum(cursor.getString(6));
                // Adding contact to list
                contactList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return contactList;
    }

    // Updating single user
    public int updateContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getId()); // Contact Name
        values.put(KEY_NAME, user.getName()); // Contact Phone
        values.put(KEY_GENDER, user.getGender());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_BIRTHDAY, user.getBirthday());
        values.put(KEY_LOCATION, user.getLocation());
        values.put(KEY_CONTACT_NUM, user.getContactNum());

        // updating row
        return db.update(TABLE_USER, values, KEY_USER_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    // Deleting single user
    public void deleteContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_USER_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }

    public boolean isExists(String searchItem) {

        String[] columns = { KEY_USER_ID };
        String selection = KEY_USER_ID + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}