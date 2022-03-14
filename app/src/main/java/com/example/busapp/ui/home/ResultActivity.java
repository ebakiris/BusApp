package com.example.busapp.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.busapp.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import io.lettuce.core.GeoArgs;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class ResultActivity extends AppCompatActivity{

    String[] keysBusStationsGeoGo = {"no1GeoGo","no2GeoGo","no3GeoGo","no4GeoGo","no5GeoGo","no6GeoGo","no7GeoGo",
            "no8GeoGo","no9GeoGo","no11GeoGo","no15GeoGo","no49GeoGo"};
    String[] keysBusStationsGeoReturn = {"no1GeoReturn","no2GeoReturn","no3GeoReturn","no4GeoReturn","no5GeoReturn",
            "no6GeoReturn","no7GeoReturn","no8GeoReturn","no9GeoReturn","no11GeoReturn","no15GeoReturn","no49GeoReturn"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        //This message contains the geospatial (long,lat) of
        //the start point and the end point
        String message = intent.getStringExtra(HomeFragment.EXTRA_MESSAGE);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_result);


        //because is separated with  " " (space)
        String[] geo = message.split("\\s+");


        ConnectAndFindBestRoute(geo);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ConnectAndFindBestRoute(String[] longlat){
        Set toGo, toGoDest;
        int dayofweek,timeForBusToArriveInt;
        int totalTimeInt;
        String totalTime;
        String timeForBusToArrive;
        //retrieve the right text views for the layout
        TextView stationStart = findViewById(R.id.startStation);
        TextView stationEnd = findViewById(R.id.endStation);
        TextView noofbusstart = findViewById(R.id.busnumberstart);
        TextView noofbusend = findViewById(R.id.busnumberend);
        TextView timeToArrive = findViewById(R.id.time);
        TextView totalTimeForTheRoute = findViewById(R.id.totalTime);


        //here we connect the client to the server which is our database
        RedisClient redisClient = RedisClient.create("redis://@192.168.1.10:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        //find the closest stations in a distance radius 500m for the specific start point(long,lat)
        toGo = syncCommands.georadius(keysBusStationsGeoGo[2], Double.parseDouble(longlat[0]),Double.parseDouble(longlat[1]),500, GeoArgs.Unit.m );
        //find the closest stations in a distance radius 500m for the specific end point(long,lat)
        toGoDest = syncCommands.georadius(keysBusStationsGeoGo[2], Double.parseDouble(longlat[2]),Double.parseDouble(longlat[3]),500, GeoArgs.Unit.m );

        //we set the text for text views
        stationStart.setText(String.valueOf(toGo.toArray()[0]));
        stationEnd.setText(String.valueOf(toGoDest.toArray()[0]));
        noofbusstart.setText("3");
        noofbusend.setText("3");


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();
        String dayandtime = dateFormat.format(date);

        //number 1 for sunday, 2-3-4-5-6 MondaytoFriday and 7 for Saturday
        dayofweek = findDayOfWeek(date);
        //The findTime function returns how many minutes needed for a bus to arrive
        timeForBusToArriveInt = Integer.parseInt(findTimeForBusToArrive(dayofweek,syncCommands,dayandtime));
        timeForBusToArrive = timeForBusToArriveInt + "'";
        timeToArrive.setText(timeForBusToArrive);


        //we add in the database the longitude,latitude of start and end point in order to find the distance between them
        syncCommands.geoadd("no3GeoGo",Double.parseDouble(longlat[0]),Double.parseDouble(longlat[1]),"startPoint");
        syncCommands.geoadd("no3GeoGo",Double.parseDouble(longlat[2]),Double.parseDouble(longlat[3]),"endPoint");
        //Simple calculation, average speed 30km/h or 8m/s
        // plus 10minutes(10*60seconds) for walking + 15*8 = 2minutes waiting in average 8 stations plus the the estimation time
        //for the bus to arrive
        totalTimeInt = (int) (syncCommands.geodist("no3GeoGo","startPoint","endPoint", GeoArgs.Unit.m)/8 + 10*60 + 2*60 + timeForBusToArriveInt*60)/60;
        totalTime = String.valueOf(totalTimeInt) + "'";

        //this is the total duration time for the passenger to reach his destination
        totalTimeForTheRoute.setText(totalTime);

        //here we calculate and set in the appropriate textview the expected time in
        //which the passenger will reach in his destination
        timeToArrive(totalTimeInt,timeForBusToArriveInt);

        connection.close();
        redisClient.shutdown();

    }

    public int findDayOfWeek(Date date){

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);

    }

    public String findTimeForBusToArrive(int day,RedisCommands<String,String> syncCommands,String givenDateTime){
        String[] given = givenDateTime.split("\\s+");
        String givenTime = given[1];
        String[] givenHourandMinutes = givenTime.split(":");
        int givenHour = Integer.parseInt(givenHourandMinutes[0]);
        int givenMinutes = Integer.parseInt(givenHourandMinutes[1]);
        int min;

        if(day == 1){
            //Sunday schedule
            List listSundTime = syncCommands.lrange("no3Sungo",0,syncCommands.llen("no3Sungo"));
            min = findMinutesForBusToArrive(listSundTime,givenHour,givenMinutes);
        }
        else if (day == 6){
            //Saturday schedule
            List listSatTime = syncCommands.lrange("no3Satgo",0,syncCommands.llen("no3Sungo"));
            min = findMinutesForBusToArrive(listSatTime,givenHour,givenMinutes);
        }
        else{
            //MontoFriday schedule
            List listMontoFriTime = syncCommands.lrange("no3MonFrigo",0,syncCommands.llen("no3Sungo"));
            min = findMinutesForBusToArrive(listMontoFriTime,givenHour,givenMinutes);
        }

        return String.valueOf(min);

    }


    public int findMinutesForBusToArrive(List list,int givenHour,int givenMinutes){
        int minimum = 61;
        String time;
        String[] hourAndMinutes ;

        for (int i = 0; i < list.size(); i++) {

            time = (String) list.get(i);
            hourAndMinutes = time.split(":");

            int hour = Integer.parseInt(hourAndMinutes[0]);
            int minutes = Integer.parseInt(hourAndMinutes[1]);
            if (givenHour == hour || (givenHour == hour + 1)) {
                int diffMinutes = Math.abs(minutes - givenMinutes);
                if (diffMinutes < minimum) {
                    minimum = diffMinutes;
                }
            }
        }

        return minimum;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void timeToArrive(int totalTime,int timeForBusToArrive){
        //Calculate the time in which the person will arrive at the destination
        LocalTime myObj = LocalTime.now();
        String[] currentTime = (String.valueOf(myObj)).split(":");
        String timeOfArrivalContent;
        TextView timeOfArrival = findViewById(R.id.timeReach);

        int currentTimeInt = Integer.parseInt(currentTime[1]) + totalTime + timeForBusToArrive;

        if(currentTimeInt >= 60){
            currentTimeInt %= 60;
            if(currentTimeInt <= 10){
                currentTime[1] = String.valueOf("0" + currentTimeInt);
            }
            else{
                currentTime[1] = String.valueOf(currentTimeInt );
            }
            currentTime[0] = String.valueOf(Integer.parseInt(currentTime[0]) + 1);
        }
        else{
            currentTime[1] = String.valueOf(currentTimeInt );
        }

        timeOfArrivalContent = currentTime[0] + ":" + currentTime[1];
        timeOfArrival.setText(timeOfArrivalContent);
    }
}
