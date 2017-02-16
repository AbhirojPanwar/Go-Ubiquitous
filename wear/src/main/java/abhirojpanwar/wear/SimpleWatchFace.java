package abhirojpanwar.wear;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by Abhiroj on 2/15/2017.
 */

public class SimpleWatchFace {

    private static final String TAG="SimpleWatchFaceClass";

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String DATE_FORMAT = "%02d/%02d/%d";


    Typeface NORMAL_TYPEFACE=Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL);

    Paint mTextTimePaint;
    Paint mTextDatePaint;
    Paint mTextWeatherPaint;

    float TimeOffsetX;
    float TimeOffsetY;
    float DateOffSetX;
    float DateOffsetY;
    float TempOffsetX;
    float TempOffsetY;
    Resources resources;
    Context context;
    float timeSize;
    float dateSize;

    String hightemp;
    String lowtemp;
    String weatherid;
    boolean ambient;

    private final Time time;


    public static SimpleWatchFace newInstance(Context context, float timeSize, float dateSize) {
        return new SimpleWatchFace(context, new Time(),timeSize,dateSize);
    }

    SimpleWatchFace(Context context, Time time,float timeSize,float dateSize) {
        mTextTimePaint=createTimeObject();
        mTextDatePaint=createDateObject();
        this.context=context;
        this.timeSize=timeSize;
        this.dateSize=dateSize;
        resources=context.getResources();
        Log.i(TAG,(resources==null)?"resource is null":"resource="+resources.toString());
        this.time = time;
        calculateOffsets();
    }

    public void draw(Canvas canvas, Rect bounds,String Hightemp,String Lowtemp,String weatherId) {
        time.setToNow();
        canvas.drawColor(Color.parseColor("#42A5F5"));

        String timeText = String.format( TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute);
        canvas.drawText(timeSize+"",bounds.centerX()-TimeOffsetX, TimeOffsetY, mTextTimePaint);

        String dateText = String.format(DATE_FORMAT, time.monthDay, (time.month + 1), time.year);
        canvas.drawText(dateSize+"",bounds.centerX()-DateOffSetX, DateOffsetY, mTextDatePaint);

        if(hightemp!=null && lowtemp!=null && weatherid!=null)
        {
            float highTextSize =mTextWeatherPaint.measureText(Hightemp);
            float xOffset = bounds.centerX() - (highTextSize / 2);
            canvas.drawText(Hightemp, xOffset, TempOffsetY, mTextWeatherPaint);
            canvas.drawText(Lowtemp, bounds.centerX() + (highTextSize / 2) + 20, TempOffsetY, mTextWeatherPaint);

        }
    }


    public void setAntiAlias(boolean antiAlias) {
        mTextDatePaint.setAntiAlias(antiAlias);
        mTextTimePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        mTextDatePaint.setColor(color);
        mTextTimePaint.setColor(color);
    }


    void calculateOffsets(){
        TimeOffsetX=mTextTimePaint.measureText("12:60")/2;
        DateOffSetX=mTextDatePaint.measureText("14/02/2017")/2;
        TimeOffsetY=resources.getDimension(R.dimen.timeoffset_y);
        DateOffsetY=resources.getDimension(R.dimen.dateoffset_y);
        TempOffsetY=resources.getDimension(R.dimen.weatheroffset_y);
    }

    Paint createTimeObject()
    {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTypeface(NORMAL_TYPEFACE);
        Log.d(TAG,"time size = "+timeSize);
        return paint;
    }

    Paint createDateObject()
    {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        Log.d(TAG,"date size = "+dateSize);
        paint.setTypeface(NORMAL_TYPEFACE);
        return paint;
    }

    Paint createWeatherObject(){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(NORMAL_TYPEFACE);
        paint.setTextSize(R.dimen.temp_size);
        return paint;
    }

}
