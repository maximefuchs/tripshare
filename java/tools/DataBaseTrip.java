package com.example.maxime.tripshare.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.maxime.tripshare.User;
import com.example.maxime.tripshare.homePage.HomePageActivity;
import com.example.maxime.tripshare.pageTrips.Trip;

import java.util.ArrayList;
import java.util.List;

public class DataBaseTrip extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;



    public static final String NOM_TABLE = "Trip";

    public static final String COL_ID = "ID";
    public static final String COL_ID_TRIP = "IDTRIP";
    public static final String COL_CITY = "CITY";
    public static final String COL_COUNTRY = "COUNTRY";
    public static final String COL_START = "STARTDATE";
    public static final String COL_END = "ENDDATE";
    public static final String COL_ENDCITY = "ENDCITY";
    public static final String COL_ENDCOUNTRY = "ENDCOUNTRY";
    public static final String COL_PICFILE = "PICTURE";
    public static final String COL_ID_USER = "IDUSER";
    public static final String COL_CREATOR = "CREATOR";
    public static final String COL_PIC_CREATOR = "PICCREATOR";
    public static final String COL_ID_FAV_USER = "ID_FAV_USER";

    public DataBaseTrip(Context context) {
        super(context, NOM_TABLE, null, DATABASE_VERSION);
    }

    public static final String CREATE_TABLES = "CREATE TABLE " + NOM_TABLE + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COL_ID_TRIP + " INTEGER NOT NULL, "
            + COL_ID_USER + " INTEGER NOT NULL, "
            + COL_CITY + " TEXT NOT NULL, "
            + COL_COUNTRY + " TEXT NOT NULL, "
            + COL_START + " TEXT NOT NULL, "
            + COL_END + " TEXT NOT NULL, "
            + COL_ENDCITY + " TEXT, "
            + COL_ENDCOUNTRY + " TEXT, "
            + COL_PICFILE + " TEXT NOT NULL, "
            + COL_CREATOR + " TEXT NOT NULL, "
            + COL_PIC_CREATOR + " TEXT NOT NULL, "
            + COL_ID_FAV_USER + " INT NOT NULL) ";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOM_TABLE);
        onCreate(db);
    }

    public long addTrip(Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID_TRIP, trip.getId());
        values.put(COL_CITY,trip.getCity());
        values.put(COL_COUNTRY,trip.getCountry());
        values.put(COL_START,trip.getDate());
        values.put(COL_END,trip.getEndDate());
        values.put(COL_ENDCITY,trip.getEndCity());
        values.put(COL_ENDCOUNTRY,trip.getEndCountry());
        values.put(COL_PICFILE, trip.getPic());
        values.put(COL_ID_USER, trip.getUserId());
        values.put(COL_CREATOR, trip.getCreator());
        values.put(COL_PIC_CREATOR, trip.getPicCreator());
        values.put(COL_ID_FAV_USER, HomePageActivity.currentIdUser);

        long id = db.insert(NOM_TABLE, null, values);
        db.close();
        return id;
    }

    public void removeTrip(int idTrip){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NOM_TABLE,COL_ID_TRIP + " = " + idTrip + " AND " + COL_ID_USER + " = " + HomePageActivity.currentIdUser,null);
        db.close();
    }

    public void addTripS(List<Trip> trips){
        SQLiteDatabase db = this.getWritableDatabase();
        for (Trip trip : trips) {
            ContentValues values = new ContentValues();
            values.put(COL_ID_TRIP, trip.getId());
            values.put(COL_CITY,trip.getCity());
            values.put(COL_COUNTRY,trip.getCountry());
            values.put(COL_START,trip.getDate());
            values.put(COL_END,trip.getEndDate());
            values.put(COL_ENDCITY,trip.getEndCity());
            values.put(COL_ENDCOUNTRY,trip.getEndCountry());
            values.put(COL_PICFILE, trip.getPic());
            values.put(COL_ID_USER,trip.getUserId());
            values.put(COL_CREATOR, trip.getCreator());
            values.put(COL_PIC_CREATOR, trip.getPicCreator());
            values.put(COL_ID_FAV_USER, HomePageActivity.currentIdUser);

            db.insert(NOM_TABLE, null, values);
        }
        db.close();
    }

    public List<Trip> getTripsByUserId(int idUser){
        List<Trip> trips = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + NOM_TABLE + " WHERE " + COL_ID_FAV_USER + " = " + idUser;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        while(cursor.moveToNext()){
            Trip t = new Trip();
            t.setId(cursor.getInt(1));
            t.setUserId(cursor.getInt(2));
            t.setCity(cursor.getString(3));
            t.setCountry(cursor.getString(4));
            t.setDate(cursor.getString(5));
            t.setEndDate(cursor.getString(6));
            t.setEndCity(cursor.getString(7));
            t.setEndCountry(cursor.getString(8));
            t.setPic(cursor.getString(9));
            t.setCreator(cursor.getString(10));
            t.setPicCreator(cursor.getString(11));
            trips.add(t);
        }
        db.close();
        return trips;
    }

    public List<Integer> getIdTripsByUserId(int idUser){
        List<Integer> idTrips = new ArrayList<>();

        String selectQuery = "SELECT "+ COL_ID_TRIP +" FROM " + NOM_TABLE + " WHERE " + COL_ID_FAV_USER + " = " + idUser;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        while(cursor.moveToNext()){
            idTrips.add(cursor.getInt(0));
        }
        db.close();
        return idTrips;
    }




}

