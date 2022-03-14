package com.example.busapp.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.R;

import java.util.ArrayList;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class BusStationActivity extends AppCompatActivity {

    public String position;
    public ArrayList<String> arrayList;
    public ArrayAdapter arrayAdapter;
    public  ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] stationstogo = {"no1gostations","no2gostations","no3gostations","no4gostations",
                "no5gostations","no6gostations","no7gostations","no8gostations",
                "no9gostations","no11gostations","no15gostations","no49gostations",};

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        position = intent.getStringExtra(BusRouteActivity.EXTRA_MESSAGE);

        setContentView(R.layout.activity_busstations);

        listView = findViewById(R.id.liststations);

        arrayList = new ArrayList<>();

        addToArrayStations(stationstogo);

    }

    public void addToArrayStations(String[] stations){

        connectToRedis(stations);
        arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,arrayList);
        listView.setAdapter(arrayAdapter);

    }

    public void connectToRedis(String[] stations){

        int i;

        RedisClient redisClient = RedisClient.create("redis://@192.168.1.10:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        if(position.contains("togo")) {
            i = 0;
            while (i < syncCommands.llen(stations[Integer.parseInt(String.valueOf(position.charAt(0)))])) {
                arrayList.add(syncCommands.lindex(stations[Integer.parseInt(String.valueOf(position.charAt(0)))], i));
                i++;
            }
        }
        else{
            i = 0;
            while (i < syncCommands.llen(stations[Integer.parseInt(String.valueOf(position.charAt(0)))])) {
                arrayList.add(syncCommands.lindex(stations[Integer.parseInt((String.valueOf(position.charAt(0))))], i));
                i++;
            }
        }

        connection.close();
        redisClient.shutdown();
    }
}
