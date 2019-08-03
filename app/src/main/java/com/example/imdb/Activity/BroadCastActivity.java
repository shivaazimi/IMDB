package com.example.imdb.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.imdb.R;

import java.util.ArrayList;
import java.util.List;

public class BroadCastActivity extends AppCompatActivity {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);

        final List<String> categories = new ArrayList<String>();
        categories.add("Click here     ⌵");
        categories.add("davari");
        categories.add("azimi");
        categories.add("alidost");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(dataAdapter);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

                final Intent intent;
                switch (position) {
                    case 1:
                        intent = new Intent(BroadCastActivity.this, azimi.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(BroadCastActivity.this, azimi.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(BroadCastActivity.this, azimi.class);
                        startActivity(intent);
                        break;

                }


            }


            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });


    }
}
