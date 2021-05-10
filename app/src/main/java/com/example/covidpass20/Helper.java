package com.example.covidpass20;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {
    public Helper() {

    }

    public boolean nemozeVon(int id, String tabulka, int dobaZakazu, DBHelper db) { //metoda ktora vrati true ak ososba je bud pozitivna alebo v karantene podla toho aku tabulku zadame do vstupneho parametru tabulka
        Cursor res = db.getData(tabulka);
        Date datum = new Date();
        while (res.moveToNext()) {
            int idvypis = Integer.parseInt(res.getString(0));
            if (idvypis == id) {
                String datumS = res.getString(1);
                try {
                    datum = new SimpleDateFormat("dd/MM/yyyy").parse(datumS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return !porovnajDatumyPoPridaniXdni(datum, new Date(),dobaZakazu);
            }
        }
        return false;
    }

    public boolean porovnajDatumyPoPridaniXdni(Date mensiDatum, Date vacsiDatum, int x) //vrati true ak vacsi datum je po mensom datume po pripocitani x dni
    {
        Calendar c = Calendar.getInstance();
        c.setTime(mensiDatum);

        // manipulate date
        c.add(Calendar.DATE, x);

        // convert calendar to date
        Date navysenyDen = c.getTime();

        if (vacsiDatum.after(navysenyDen)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean porovnajCiSaRovnajuDvaDatumyPoPridaniXdni(Date datum1, Date datum2, int x) //vrati true ak sa dane datumy rovnaju po pridani x dni k datum1
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(datum1);

        // manipulate date
        cal1.add(Calendar.DATE, x);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(datum2);
        boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

        return sameDay;
    }

    public Date dateFromString(String pDatum) {
        Date datum = new Date();
        try {
            datum = new SimpleDateFormat("dd/MM/yyyy").parse(pDatum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datum;
    }
}
