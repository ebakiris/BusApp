package com.example.busapp.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.R;

public class BusRouteActivity<stToGo> extends AppCompatActivity {

    public TextView stationNameToGo;
    public TextView stationNameToReturn;
    public static final String EXTRA_MESSAGE = "com.example.busapp.ui.gallery.MESSAGE";
    public int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        position = 0;

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra(GalleryFragment.EXTRA_MESSAGE));

        setContentView(R.layout.activity_route);
        //here we define the views for the layout
        stationNameToGo = findViewById(R.id.togo);
        stationNameToReturn = findViewById(R.id.toreturn);
        Button scheduleToReturn = findViewById(R.id.timestoreturn);
        Button stationsGo = findViewById(R.id.stationstogo);
        Button stationsToReturn = findViewById(R.id.stationstoreturn);
        Button scheduleToGo = findViewById(R.id.timetogo);

        //Set the right text for each bus
        setTheText(position);

        stationsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusRouteActivity.this, BusStationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, (position + "togo"));
                startActivity(intent);
            }
        });

        stationsToReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusRouteActivity.this, BusStationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, (position + "toreturn"));
                startActivity(intent);
            }
        });

        scheduleToGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusRouteActivity.this, BusScheduleActivity.class);
                intent.putExtra(EXTRA_MESSAGE,(position + "togo"));
                startActivity(intent);
            }
        });

        scheduleToReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusRouteActivity.this, BusScheduleActivity.class);
                intent.putExtra(EXTRA_MESSAGE,(position + "toreturn"));
                startActivity(intent);
            }
        });

    }

    public void setTheText(int i){

        switch (i){
            case 0:
                stationNameToGo.setText("Άναυρος -> Νέα Ιωνία");
                stationNameToReturn.setText("Νέα Ιωνία -> Άναυρος");
                break;
            case 1:
                stationNameToGo.setText("Αφετηρία -> Αμπελόκηποι");
                stationNameToReturn.setText("Αμπελόκηποι -> Αφετηρία");
                break;
            case 2:
                stationNameToGo.setText("Άναυρος -> Νέα Ιωνία(ΜΕΤΚΑ)");
                stationNameToReturn.setText("Νέα Ιωνία(ΜΕΤΚΑ) -> Άναυρος");
                break;
            case 3:
                stationNameToGo.setText("ΟΑΕΔ -> Αηδονοφωλιές");
                stationNameToReturn.setText("Αηδονοφωλιές -> ΟΑΕΔ");
                break;
            case 4:
                stationNameToGo.setText("Αφετηρία -> Τέρμα Λεχώνια/Πλατανίδια");
                stationNameToReturn.setText("Τέρμα Λεχώνια/Πλατανίδια -> Αφετηρία");
                break;
            case 5:
                stationNameToGo.setText("ΟΑΕΔ -> Αλυκές/Άγιος Στέφανος");
                stationNameToReturn.setText("Αλυκές/Άγιος Στέφανος -> ΟΑΕΔ");
                break;
            case 6:
                stationNameToGo.setText("Άλλη Μεριά");
                stationNameToReturn.setText(" - ");
                break;
            case 7:
                stationNameToGo.setText("ΟΑΕΔ -> Διμήνι");
                stationNameToReturn.setText(" - ");
                break;
            case 8:
                stationNameToGo.setText("Αφετηρία -> Αλιβέρι");
                stationNameToReturn.setText("Αλιβέρι -> Αφετηρία");
                break;
            case 9:
                stationNameToGo.setText("Αφετηρία -> Νέο Κοιμητήριο");
                stationNameToReturn.setText("Νέο Κοιμητήριο -> Αφετηρία");
                break;
            case 10:
                stationNameToGo.setText("Άναυρος -> Παλαιά");
                stationNameToReturn.setText("Παλαιά -> Άναυρος");
                break;
            case 11:
                stationNameToGo.setText("Αφετηρία -> Αγία Παρασκευή");
                stationNameToReturn.setText("Αγία Παρασκευή -> Αφετηρία");
                break;
        }

    }
}
