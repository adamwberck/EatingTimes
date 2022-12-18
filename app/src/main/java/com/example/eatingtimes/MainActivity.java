package com.example.eatingtimes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    private static final DateTimeFormatter HMA = DateTimeFormatter.ofPattern("h:mma");
    private final Map<Integer, TimeObject> timeObjectMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeObjectMap.put(0,
                new TimeObject(0,findViewById(R.id.text0),findViewById(R.id.switch0)));
        timeObjectMap.put(1,
                new TimeObject(1,findViewById(R.id.text1),findViewById(R.id.switch1)));
        timeObjectMap.put(2,
                new TimeObject(2,findViewById(R.id.text2),findViewById(R.id.switch2)));
        timeObjectMap.put(3,
                new TimeObject(3,findViewById(R.id.text3),findViewById(R.id.switch3)));
        timeObjectMap.get(0).getSwitchCompat().setChecked(true);
        timeObjectMap.get(3).getSwitchCompat().setChecked(true);
        new myTimeSetListener(0).onTimeSet(null, 9, 0);

        for(TimeObject timeObject : timeObjectMap.values()) {
            initSwitches(timeObject);
            timeObject.getTextView().setOnClickListener(
                    new timeOnClickListener(timeObject.getIndex()));

        }
    }

    private void initSwitches(TimeObject timeObject) {
        SwitchCompat s = timeObject.getSwitchCompat();
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            final int index = timeObject.getIndex();
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    if(switchesChecked()>=2) {
                        for (int j = 0; j < index; j++) {
                            timeObjectMap.get(j).getSwitchCompat().setChecked(true);
                        }
                    }
                }
                else{
                    if(switchesChecked()>=2) { // uncheck
                        for (int j = index+1; j < 3; j++) {
                            timeObjectMap.get(j).getSwitchCompat().setChecked(false);
                        }
                    }
                }
                timeObjectMap.get(0).getSwitchCompat().setChecked(true);
                timeObjectMap.get(3).getSwitchCompat().setChecked(true);
                updateTimes(-1);
            }
        });
    }

    private int switchesChecked(){
        int total = 0;
        for(TimeObject timeObject : timeObjectMap.values()){
            total += timeObject.getSwitchCompat().isChecked() ? 1 : 0;
        }
        return total;
    }

    private void updateTimes(int index) {
        int[] timesArray = new int[4];
        List<Integer> nonFillSpots = new ArrayList<>(4);
        List<Integer> fillSpots = new ArrayList<>(4);
        for(int i=0; i<4; i++){
            if( i == index || timeObjectMap.get(i).getSwitchCompat().isChecked()){//don't fill
                LocalTime time = timeObjectMap.get(i).getLocalTime();
                timesArray[i] = time.getHour()*60+time.getMinute();
                if(timesArray[0] > timesArray[i]){
                    timesArray[i]+=24*60;
                }
                nonFillSpots.add(i);
            }
            else{// add to fill spot
                fillSpots.add(i);
            }
        }
        if(nonFillSpots.contains(0) && nonFillSpots.contains(1)){
            nonFillSpots.remove(0);
            timeObjectMap.get(0).setEnabled(false);
        }
        else{
            timeObjectMap.get(0).setEnabled(true);
        }
        if(nonFillSpots.contains(2) && nonFillSpots.contains(3)){
            nonFillSpots.remove(nonFillSpots.size()-1);
            timeObjectMap.get(3).setEnabled(false);
        }
        else{
            timeObjectMap.get(3).setEnabled(true);
        }
        if(fillSpots.size()>0 && nonFillSpots.size() == 2){ // simple
            int nFsG = nonFillSpots.get(0);
            int numPoints = 1 + nonFillSpots.get(1) - nonFillSpots.get(0);
            Stream<Double> stream = linspace(timesArray[nonFillSpots.get(0)],
                    timesArray[nonFillSpots.get(1)], numPoints);
            int[] filling = new int[4];
            int j = nFsG;
            for (Iterator<Double> it = stream.iterator(); it.hasNext(); ) {
                filling[j++] = it.next().intValue();
            }
            for(int i=nFsG; i<4; i++){
                if(fillSpots.contains(i)){ // fill spot
                    LocalTime lt = null;
                    try {
                        lt = LocalTime.of(filling[i] / 60,
                                filling[i] % 60);
                    }
                    catch (DateTimeException dte){
                        lt = LocalTime.of((filling[i]-24) / 60,
                                filling[i] % 60);
                    }
                    timeObjectMap.get(i).setLocalTime(lt);
                    timeObjectMap.get(i).getTextView().setText(lt.format(HMA));
                }
            }
        }
    }

    public static Stream<Double> linspace(double start, double end, int numPoints) {
        return IntStream.range(0, numPoints)
                .boxed()
                .map(i -> start + i * (end - start) / (numPoints - 1));
    }

    private class timeOnClickListener implements View.OnClickListener {
        private final int index;
        public  timeOnClickListener(int index){
            this.index = index;
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View view) {
            System.out.println("df");
            if(!timeObjectMap.get(index).getSwitchCompat().isChecked()
                    || !timeObjectMap.get(index).isEnabled()){
                return;
            }
            LocalTime lt = LocalTime.now();
            new TimePickerDialog(MainActivity.this, new myTimeSetListener(index), lt.getHour(),
                    lt.getMinute(), false).show();
        }
    }

    private class myTimeSetListener implements TimePickerDialog.OnTimeSetListener{
        private final int index;

        public myTimeSetListener(int index){
            this.index = index;
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            LocalTime pickedTime = LocalTime.of(hour, minute);
            MainActivity.this.timeObjectMap.get(index).setLocalTime(pickedTime);
            MainActivity.this.timeObjectMap.get(index).getTextView()
                    .setText(pickedTime.format(HMA));
            updateTimes(index);
        }
    }
}