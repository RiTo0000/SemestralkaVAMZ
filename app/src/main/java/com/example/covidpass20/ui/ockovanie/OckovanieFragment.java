package com.example.covidpass20.ui.ockovanie;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.covidpass20.R;

import java.util.Calendar;

public class OckovanieFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OckovanieViewModel ockovanieViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ockovanieViewModel =
                new ViewModelProvider(this).get(OckovanieViewModel.class);
        View root = inflater.inflate(R.layout.ockovanie_fragment, container, false);

        TextView datum1 = root.findViewById(R.id.datum1Vstup);
        TextView datum2 = root.findViewById(R.id.datum2Vstup);
        TextView typVstup = root.findViewById(R.id.typVakcinyVstup);
        TextView druhaDavkaVstup = root.findViewById(R.id.druhaDavkaVstup);
        TextView rcVstup = root.findViewById(R.id.rcOckovanieVstup);
        TextView kodOCVstup = root.findViewById(R.id.kodOCVstup);
        TextView rcVypisVstup = root.findViewById(R.id.rcOckovanieVypisVstup);

        CheckBox druhaDavka = root.findViewById(R.id.druhaDavka);
        EditText rc = root.findViewById(R.id.rcOckovanie);
        EditText kodOC = root.findViewById(R.id.kodOC);
        EditText rcVypis = root.findViewById(R.id.rcOckovanieVypis);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int stageWidth = display.getWidth();

        datum1.setWidth(stageWidth/2);
        datum2.setWidth(stageWidth/2);
        rcVstup.setWidth(stageWidth/2);
        typVstup.setWidth(stageWidth/2);
        druhaDavkaVstup.setWidth(stageWidth/2);
        kodOCVstup.setWidth(stageWidth/2);
        rcVypisVstup.setWidth(stageWidth/2);

        druhaDavka.setWidth(stageWidth/2);
        rc.setWidth(stageWidth/2);
        kodOC.setWidth(stageWidth/2);
        rcVypis.setWidth(stageWidth/2);

        EditText datum1Pole, datum2Pole;
        datum1Pole = root.findViewById(R.id.datum1);
        datum2Pole = root.findViewById(R.id.datum2);
        datum1Pole.setWidth(stageWidth/2);
        datum2Pole.setWidth(stageWidth/2);

        //kontrola spravneho formatu datumu 1
        TextWatcher tw1 = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    datum1Pole.setText(current);
                    datum1Pole.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        datum1Pole.addTextChangedListener(tw1);

        //kontrola spravneho formatu datumu 2
        TextWatcher tw2 = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    datum2Pole.setText(current);
                    datum2Pole.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        datum2Pole.addTextChangedListener(tw2);


        //spinner
        Spinner typVakciny = root.findViewById(R.id.typVakciny);
        ArrayAdapter<CharSequence> typVakcinyAdapter = ArrayAdapter.createFromResource(getContext(), R.array.vakciny, android.R.layout.simple_spinner_item);
        typVakcinyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typVakciny.setAdapter(typVakcinyAdapter);
        typVakciny.setOnItemSelectedListener(this);

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}