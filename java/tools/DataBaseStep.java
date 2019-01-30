package com.example.maxime.tripshare.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.maxime.tripshare.pageSteps.Step;
import com.example.maxime.tripshare.pageTrips.Trip;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class DataBaseStep extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;



    public static final String NOM_TABLE = "Step";

    public static final String COL_ID_STEP = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_LATITUDE = "LATITUDE";
    public static final String COL_LONGITUDE = "LONGITUDE";
    public static final String COL_PICFILE = "PICTURE";
    public static final String COL_STORY = "STORY";
    public static final String COL_ID_TRIP = "ID_TRIP";

    public DataBaseStep(Context context) {
        super(context, NOM_TABLE, null, DATABASE_VERSION);
    }

    public static final String CREATE_TABLES = "CREATE TABLE " + NOM_TABLE + " ("
            + COL_ID_STEP + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_LATITUDE + " FLOAT NOT NULL, "
            + COL_LONGITUDE + " FLOAT NOT NULL, "
            + COL_PICFILE + " TEXT NOT NULL, "
            + COL_STORY + "TEXT NOT NULL, "
            + COL_ID_TRIP + "INTERGER NOT NULL)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOM_TABLE);
        onCreate(db);
    }

    public long addStep(Step step){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, step.getName());
        values.put(COL_LATITUDE,step.getGeo().getLatitude());
        values.put(COL_LONGITUDE,step.getGeo().getLongitude());
        values.put(COL_PICFILE,step.getCoverPic());
        values.put(COL_STORY, step.getStory());
        values.put(COL_ID_TRIP, step.getIdTrip());

        long id = db.insert(NOM_TABLE, null, values);
        db.close();
        return id;
    }

    public void addStepS(List<Step> steps){
        SQLiteDatabase db = this.getWritableDatabase();
        for (Step step : steps) {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, step.getName());
            values.put(COL_LATITUDE,step.getGeo().getLatitude());
            values.put(COL_LONGITUDE,step.getGeo().getLongitude());
            values.put(COL_PICFILE,step.getCoverPic());
            values.put(COL_STORY, step.getStory());
            values.put(COL_ID_TRIP, step.getIdTrip());

            db.insert(NOM_TABLE, null, values);
        }
        db.close();
    }

    public List<Step> getStepsByTripId(int idTrip){
        List<Step> steps = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + NOM_TABLE + " WHERE " + COL_ID_TRIP + " = " + idTrip;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        while(cursor.moveToNext()){
            Step s = new Step();
            s.setId(cursor.getInt(0));
            s.setName(cursor.getString(1));
            float lat = cursor.getFloat(2);
            float lon = cursor.getFloat(3);
            s.setGeo(new GeoPoint(lat,lon));
            s.setCoverPic(cursor.getString(4));
            s.setStory(cursor.getString(5));
            s.setIdTrip(idTrip);
            steps.add(s);
        }
        db.close();
        return steps;
    }
}

