package com.example.covidpass20.ui.test;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.covidpass20.R;

import java.util.Calendar;

public class TestFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TestViewModel testViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        testViewModel =
                new ViewModelProvider(this).get(TestViewModel.class);
        View root = inflater.inflate(R.layout.test_fragment, container, false);

        TextView datumVstup = root.findViewById(R.id.datumVstup);
        TextView vysledokVstup = root.findViewById(R.id.vysledokVstup);
        TextView typVstup = root.findViewById(R.id.typVstup);
        TextView rcVstup = root.findViewById(R.id.rcVstup);
        TextView rcVypisVstup = root.findViewById(R.id.rcVypisVstup);

        EditText datum = root.findViewById(R.id.datum);
        CheckBox vysledok = root.findViewById(R.id.vysledok);
        EditText rc = root.findViewById(R.id.rc);
        EditText rcVypis = root.findViewById(R.id.rcVypis);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int stageWidth = display.getWidth();

        datumVstup.setWidth(stageWidth/2);
        vysledokVstup.setWidth(stageWidth/2);
        rcVstup.setWidth(stageWidth/2);
        typVstup.setWidth(stageWidth/2);
        rcVypisVstup.setWidth(stageWidth/2);

        datum.setWidth(stageWidth/2);
        vysledok.setWidth(stageWidth/2);
        rc.setWidth(stageWidth/2);
        rcVypis.setWidth(stageWidth/2);



        TextWatcher tw = new TextWatcher() {
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
                    datum.setText(current);
                    datum.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        datum.addTextChangedListener(tw);

        //spinner
        Spinner typTestu = root.findViewById(R.id.typ);
        ArrayAdapter<CharSequence> typTestuAdapter = ArrayAdapter.createFromResource(getContext(), R.array.test, android.R.layout.simple_spinner_item);
        typTestuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typTestu.setAdapter(typTestuAdapter);
        typTestu.setOnItemSelectedListener(this);

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