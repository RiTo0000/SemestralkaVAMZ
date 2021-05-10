package com.example.covidpass20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.covidpass20.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    //String DB_FULL_PATH = "Userdatabase.db";
    String DB_FULL_PATH = "lol3.db";
    Helper help;

    public DBHelper(Context context) {

        super(context, "lol3.db", null, 1);
        help = new Helper();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(!checkDataBase())
        {
            db.execSQL("create Table UserInfo(meno TEXT, priezvisko TEXT, rodcislo TEXT primary key, mesto TEXT, adresa TEXT)");
            db.execSQL("create Table Testy(id_test INTEGER primary key, datum TEXT, vysledok TEXT, typ TEXT, rodcislo TEXT )");
            db.execSQL("create Table Ockovanie(datum1 TEXT, datum2 TEXT, typ TEXT, pocetDavok INTEGER, rodcislo TEXT primary key, kodCentra TEXT )");
            db.execSQL("create Table Karantena(id_karantena INTEGER primary key, datum TEXT, doba TEXT, rodcislo TEXT )");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists UserInfo");
        db.execSQL("drop Table if exists Testy");
        db.execSQL("drop Table if exists Ockovanie");
        db.execSQL("drop Table if exists Karantena");
    }

    public boolean insertUser(String meno, String priezvisko, String rodcislo, String mesto, String adresa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("meno", meno);
        contentValues.put("priezvisko", priezvisko);
        contentValues.put("rodcislo", rodcislo);
        contentValues.put("mesto", mesto);
        contentValues.put("adresa", adresa);
        long result=db.insert("UserInfo",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean updateUser(String meno, String priezvisko, String rodcislo, String mesto, String adresa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("meno", meno);
        contentValues.put("priezvisko", priezvisko);
        contentValues.put("mesto", mesto);
        contentValues.put("adresa", adresa);
        Cursor cursor = db.rawQuery("select * from UserInfo where rodcislo=?", new String[] {rodcislo} );
        if (cursor.getCount() > 0)
        {
            long result=db.update("UserInfo",contentValues, "rodcislo=?", new String[] {rodcislo} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    public boolean deleteUser(String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from UserInfo where rodcislo=?", new String[] {rodcislo} );
        if (cursor.getCount() > 0)
        {
            long result=db.delete("UserInfo", "rodcislo=?", new String[] {rodcislo} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    public Cursor getData(String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table, null);
        return cursor;
    }

    public boolean insertTest(int id, String datum, String vysledok, String typ, String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_test", id);
        contentValues.put("datum", datum);
        contentValues.put("vysledok", vysledok);
        contentValues.put("typ", typ);
        contentValues.put("rodcislo", rodcislo);
        long result=db.insert("Testy",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean deleteTest(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Testy where id_test=?", new String[] {id} );
        if (cursor.getCount() > 0)
        {
            long result=db.delete("Testy", "id_test=?", new String[] {id} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }



    public boolean insertOckovanie(String datum1, String datum2, String typ , int pocetDavok, String rodcislo, String kodCentra) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("datum1", datum1);
        contentValues.put("datum2", datum2);
        contentValues.put("typ", typ);
        contentValues.put("pocetDavok", pocetDavok);
        contentValues.put("rodcislo", rodcislo);
        contentValues.put("kodCentra", kodCentra);
        long result=db.insert("Ockovanie",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean updateOckovanie(String datum2, String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("datum2", datum2);
        Cursor cursor = db.rawQuery("select * from Ockovanie where rodcislo=?", new String[] {rodcislo} );
        if (cursor.getCount() > 0)
        {
            long result=db.update("Ockovanie",contentValues, "rodcislo=?", new String[] {rodcislo} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    public boolean deleteOckovanie(String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from UserInfo where rodcislo=?", new String[] {rodcislo} );
        if (cursor.getCount() > 0)
        {
            long result=db.delete("UserInfo", "rodcislo=?", new String[] {rodcislo} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    public boolean insertKarantenu(int id, String datum, String doba, String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_karantena", id);
        contentValues.put("datum", datum);
        contentValues.put("doba", doba);
        contentValues.put("rodcislo", rodcislo);
        long result=db.insert("Karantena",null,contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean deleteKarantenu(String rodcislo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Karantena where rodcislo=?", new String[] {rodcislo} );
        if (cursor.getCount() > 0)
        {
            long result=db.delete("Karantena", "rodcislo=?", new String[] {rodcislo} );
            if (result == -1)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }

    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
                    SQLiteDatabase.OPEN_READWRITE);
            checkDB.close();
            System.out.println("databaza existuje");
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            System.out.println("databaza neexistuje");
        }
        System.out.println("databaza neexistuje");
        return checkDB != null;
    }

    public boolean jeVtabulke(String rodCislo, String tabulka, int indexRC) {
        Cursor res = getData(tabulka);
        if (res.getCount() == 0)
        {
            return false;
        }
        String rc;
        while (res.moveToNext()) {
            rc = res.getString(indexRC);
            if (rc.equals(rodCislo))
                return true;
        }
        return false;
    }


}
