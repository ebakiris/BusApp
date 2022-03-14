package com.example.busapp.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busapp.R;

import java.util.ArrayList;
import java.util.List;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class BusScheduleActivity extends AppCompatActivity {

    ListView listView;
    int[] help = {0,2,4,6,8,10,12,14,16,18,20,22};
    String[] days = {"Δευτέρα-Παρασκευή", "Σάββατο", "Κυριακή"};
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String message;
        String[] busestogo = {"no1MonFri", "no1Sat", "no1sungo","no2MonFrigo", "no2Satgo",
                "no2Sungo","no3MonFrigo", "no3Satgo", "no3Sungo",
                "no4MonFrigo", "no4Satgo", "no4Sungo","no5MonFrigo", "no5Satgo",
                "no5Sungo","no6MonFrigo", "no6Satgo", "no6Sungo",
                "no7MonFrigo", "no7Satgo", "no7Sungo","no8MonFrigo", "no8Satgo",
                "no8Sungo","no9MonFrigo", "no9Satgo", "no9Sungo",
                "no11MonFrigo", "no11Satgo", "no11Sungo","no15MonFrigo", "no15Satgo",
                "no15Sungo","no49MonFrigo", "no49Satgo", "no49Sungo"};
        String[] busestoreturn = {"no1MonFrireturn", "no1Satreturn", "no1Sunreturn","no2MonFrireturn", "no2Satreturn",
                "no2Sunreturn","no3MonFrireturn", "no3Satreturn", "no3Sunreturn",
                "no4MonFrireturn", "no4Satreturn", "no4Sunreturn","no5MonFrireturn", "no5Satreturn",
                "no5Sunreturn","no6MonFrireturn", "no6Satreturn", "no6Sunreturn",
                "no7MonFrireturn", "no7Satreturn", "no7Sunreturn","no8MonFrireturn", "no8Satreturn",
                "no8Sunreturn","no9MonFrireturn", "no9Satreturn", "no9Sunreturn",
                "no11MonFrireturn", "no11Satreturn", "no11Sunreturn","no15MonFrireturn", "no15Satreturn",
                "no15Sunreturn","no49MonFrireturn", "no49Satreturn", "no49Sunreturn"};

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        message = intent.getStringExtra(BusRouteActivity.EXTRA_MESSAGE);

        position = Integer.parseInt(String.valueOf(message.charAt(0)));
        setContentView(R.layout.activity_busschedule);

        if(message.contains("togo")){
            ConnectToRedis(busestogo);
        }
        else{
            ConnectToRedis(busestoreturn);
        }

    }

    public void ConnectToRedis(String[] bus){

        List schedule;
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayListSat = new ArrayList<>();
        ArrayList<String> arrayListSun = new ArrayList<>();

        int count = 0, countdays = 0;

        RedisClient redisClient = RedisClient.create("redis://@192.168.1.10:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        //MONDAY TO FRIDAY LIST

        schedule = syncCommands.lrange(bus[position + help[position]],0,87);
        listView = (ListView) findViewById(R.id.listviewschedule);

        arrayList.add(days[countdays]);

        while(count < schedule.size() ){
            arrayList.add(schedule.get(count).toString());
            count++;
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        listView.setAdapter(arrayAdapter);

        countdays++;

        //SATURDAY LIST
        count = 0;

        schedule = syncCommands.lrange(bus[position + help[position] + 1],0,87);
        listView = (ListView) findViewById(R.id.listviewschedulesaturday);

        arrayListSat.add(days[countdays]);

        while(count < schedule.size() ){
            arrayListSat.add(schedule.get(count).toString());
            count++;
        }

        ArrayAdapter arrayAdapterSat = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayListSat);
        listView.setAdapter(arrayAdapterSat);

        countdays++;

        //SUNDAY LIST
        count = 0;

        schedule = syncCommands.lrange(bus[position + help[position] + 2],0,87);
        listView = (ListView) findViewById(R.id.listviewschedulesun);

        arrayListSun.add(days[countdays]);

        while(count < schedule.size() ){
            arrayListSun.add(schedule.get(count).toString());
            count++;
        }

        ArrayAdapter arrayAdapterSun = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayListSun);
        listView.setAdapter(arrayAdapterSun);

        connection.close();
        redisClient.shutdown();
    }
}
