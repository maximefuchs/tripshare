package com.example.maxime.tripshare.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.maxime.tripshare.pageDetails.Picture;
import com.example.maxime.tripshare.pageSteps.Step;

import java.util.ArrayList;
import java.util.List;

public class DataBasePics extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;



    public static final String NOM_TABLE = "Pics";

    public static final String COL_ID_PIC = "ID";
    public static final String COL_PICTURE = "PICTURE";
    public static final String COL_IDSTEP = "IDSTEP";

    public DataBasePics(Context context) {
        super(context, NOM_TABLE, null, DATABASE_VERSION);
    }

    public static final String CREATE_TABLES = "CREATE TABLE " + NOM_TABLE + " ("
            + COL_ID_PIC + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PICTURE + " TEXT NOT NULL, "
            + COL_IDSTEP + "INTERGER NOT NULL)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOM_TABLE);
        onCreate(db);
    }

    public long addPic(Picture pic){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PICTURE, pic.getPicture());
        values.put(COL_IDSTEP, pic.getIdStep());

        long id = db.insert(NOM_TABLE, null, values);
        db.close();
        return id;
    }

    public void addPicS(List<Picture> pics){
        SQLiteDatabase db = this.getWritableDatabase();
        for (Picture pic : pics) {
            ContentValues values = new ContentValues();
            values.put(COL_PICTURE, pic.getPicture());
            values.put(COL_IDSTEP, pic.getIdStep());

            db.insert(NOM_TABLE, null, values);
        }
        db.close();
    }

    public List<Picture> getPicsByStepId(int idStep){
        List<Picture> pics = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + NOM_TABLE + " WHERE " + COL_IDSTEP + " = " + idStep;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        while(cursor.moveToNext()){
            Picture p = new Picture();
            p.setId(cursor.getInt(0));
            p.setPicture(cursor.getString(1));
            p.setIdStep(idStep);
            pics.add(p);
        }
        db.close();
        return pics;
    }

}

