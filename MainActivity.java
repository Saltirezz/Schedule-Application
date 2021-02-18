package com.example.timetable;

        import androidx.appcompat.app.AppCompatActivity;

        import android.annotation.SuppressLint;
        import android.os.Bundle;
        import android.os.CountDownTimer;
        import android.widget.TextView;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;

class DateUtils {
    // format 24hre ex. 12:12 , 17:15
    private static String HOUR_FORMAT = "HH:mm";

    private DateUtils() {
    }

    public static String getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfHour = new SimpleDateFormat(HOUR_FORMAT);
        return sdfHour.format(cal.getTime());
    }

    /**
     * @param target hour to check
     * @param start  interval start
     * @param end    interval end
     * @return true    true if the given hour is between
     */
    public static boolean isHourInInterval(String target, String start, String end) {
        return ((target.compareTo(start) >= 0)
                && (target.compareTo(end) <= 0));
    }

    /**
     * @param start interval start
     * @param end   interval end
     * @return true    true if the current hour is between
     */
    public static boolean isNowInInterval(String start, String end) {
        return DateUtils.isHourInInterval
                (DateUtils.getCurrentHour(), start, end);
    }
}
//main app
public class MainActivity extends AppCompatActivity {
    //day times and the classes to save space.
    public String[][][] dayClass(){
        String[][] classes = {{"Graphics","Physics","P.E","Interval","W.A","College","Lunch","W.A","Computing"},{"Graphics","Graphics","Philosophy","Interval","Computing","Computing","Lunch","College","College"},{"Maths","Maths","other W.A","Interval","Physics","Physics","Physics","Graphics","Graphics"},{"Physics","Physics","W.A","Interval","Maths","Maths","Lunch","College","College"},{"Computing","Computing","PSE","Interval","Maths"}};
        String[][] monThurTimes = {{"8:30","9:20"},{"9:20","10:10"},{"10:10","11:10"},{"11:10","11:25"},{"11:25","12:15"},{"12:15","13:05"},{"13:05","13:45"},{"13:45","14:35"},{"14:35","15:25"}};
        String[][] friTimes = {{"8:30","9:20"},{"9:20","10:10"},{"10:10","11:10"},{"11:10","11:30"},{"11:30","23:30"}};
        String[][] daytimes;
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            daytimes = friTimes;
        }
        else{
            daytimes = monThurTimes;
        }
        return new String[][][]{daytimes, classes};
    }
    //getting the current(and next) class you're in
    public String[] pickClass(){
        int[] array1 = {Calendar.MONDAY,Calendar.TUESDAY,Calendar.WEDNESDAY,Calendar.THURSDAY,Calendar.FRIDAY};
        //makes more sense to just create an array instead of running it multiple times when setting variables
        String[][][] dayClassArray = dayClass();
        String[][] classes = dayClassArray[1];
        String[][] daytimes = dayClassArray[0];
        String[] classTimes = {};
        String currentClass = "";
        String nextClass = "";
        int classArrayLocation = 0;
        int currentDay = 0;
        //getting the current day
        for(int i = 0; i < array1.length; i++) {
            if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == array1[i]) {
                currentDay = i;
                break;
            }
        }
        //setting the current class and the next class to whatever it is depending on the time
        for(int i = 0; i < daytimes.length; i++) {
            if (DateUtils.isNowInInterval(daytimes[i][0], daytimes[i][1])) {
                currentClass = classes[currentDay][i];
                classArrayLocation = i;
                classTimes = daytimes[i];
                if(i != daytimes.length-1) {
                    nextClass = classes[currentDay][i + 1];
                    break;
                }
                else{
                    nextClass = "DayEnd";
                    break;
                }

            }

        }
        String[] coolarray = {currentClass,nextClass,Integer.toString(classArrayLocation),Integer.toString(currentDay),classTimes[0],classTimes[1]};
        return coolarray;
    }
    //getting the time until the class ends
    public int timerTime(int classArrayLocation){
        //basically uses the date and time utilities by https://www.rgagnon.com/javadetails/java-0624.html to check if the time is inbetween any of the class times, then updates the textview text accordingly.
        String[][] daytimes = dayClass()[0];
        String strEndTime = daytimes[classArrayLocation][1];
        String currentTime = DateUtils.getCurrentHour();
        //getting x1,y1 and x2,y2

        int y1param1 = 3;
        int y1param2 = 5;
        int y2param1 = 3;
        int y2param2 = 5;
        int y3;

        if(strEndTime.length() == 4){
            y1param1 = 2;
            y1param2 = 4;
        }
        if(currentTime.length() == 4){
            y2param1 = 2;
            y2param2 = 4;
        }
        int y1 = Integer.parseInt(strEndTime.substring(y1param1,y1param2));
        int y2 = Integer.parseInt(currentTime.substring(y2param1,y2param2));
        if(y2 < y1){
            y3 = y1-y2;
        }
        else{
            y3 = (60-y2)+y1;
        }
        return y3;
    }
    //the code for setting the class and other front end info
    @SuppressLint("SetTextI18n")
    public void otherStuff(){
        //variable initialization
        String[] pickClassArray = pickClass();
        String currentClass = pickClassArray[0];
        String nextClass = pickClassArray[1];
        String currentTimings1 = pickClassArray[4];
        String currentTimings2 = pickClassArray[5];
        //getting the textviews
        TextView textView = findViewById(R.id.TextView);
        TextView textView2 = findViewById(R.id.TextView2);
        TextView textView3 = findViewById(R.id.classTimes);
        //setting text to whatever
        textView.setText(currentClass);
        textView3.setText(currentTimings1+" -> "+currentTimings2);
        textView2.setText(">"+nextClass);
        //starting the countdown again
        countdownthing();
    }
    //main timer code
    void countdownthing() {
        int time = timerTime(Integer.parseInt(pickClass()[2]));
        new CountDownTimer(60000*time, 60000) {
            TextView textView3 = (TextView)findViewById(R.id.TextView3);
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                textView3.setText("Minutes Remaining: " + (millisUntilFinished / 60000+1));
            }
            //updating your current and next class in real time
            @SuppressLint("SetTextI18n")
            public void onFinish() {
                //variable initialization
                String[] array = pickClass();
                int currentDay = Integer.parseInt(array[3]);
                int arrayLocation = Integer.parseInt(array[2]);
                String[][] classes = dayClass()[0];
                String[] pickClassArray = pickClass();
                String currentTimings1 = pickClassArray[4];
                String currentTimings2 = pickClassArray[5];
                //text views
                TextView textView3 = findViewById(R.id.classTimes);
                TextView textView = findViewById(R.id.TextView);
                TextView textView2 = findViewById(R.id.TextView2);
                //setting text
                if(arrayLocation != classes[0].length-1) {
                    textView2.setText(classes[currentDay][arrayLocation+2]);
                    textView.setText(classes[currentDay][arrayLocation+1]);
                    textView3.setText(currentTimings1+" -> "+currentTimings2);
                }
                else{
                    textView.setText("Day End");
                    textView2.setText("Day End");
                    textView3.setText("xx:xx -> xx:xx");
                }
                countdownthing();
            }

        }.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        otherStuff();

    }

    public void onResume(){
        super.onResume();
        otherStuff();


    }
}