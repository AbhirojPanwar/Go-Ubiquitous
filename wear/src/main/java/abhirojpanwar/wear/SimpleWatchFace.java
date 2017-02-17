package abhirojpanwar.wear;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by Abhiroj on 2/15/2017.
 */

public class SimpleWatchFace {

    private static final String TAG="SimpleWatchFaceClass";

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String DATE_FORMAT = "%s %02d, %d";


    Typeface BOLD_TYPEFACE=Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
    Typeface NORMAL_TYPEFACE=Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL);

    Paint mTextTimePaint;
    Paint mTextDatePaint;
    Paint mTextWeatherPaint;

    float TimeOffsetX;
    float TimeOffsetY;
    float DateOffSetX;
    float DateOffsetY;
    float TempOffsetY;
    float DividerOffsetY;
    Resources resources;
    Context context;
    Canvas globalcanvas;


    private final Time time;


    public static SimpleWatchFace newInstance(Context context) {
        return new SimpleWatchFace(context, new Time());
    }

    SimpleWatchFace(Context context, Time time) {
        this.context=context;
        resources=context.getResources();
        Log.i(TAG,(resources==null)?"resource is null":"resource="+resources.toString());
        this.time = time;
    }

    private void initializePaintObjects()
    {
        mTextTimePaint=createTimeObject();
        mTextDatePaint=createDateObject();
        mTextWeatherPaint=createWeatherObject();
        calculateOffsets();
    }

    private void configureForAmbientMode()
    {
        globalcanvas.drawColor(Color.BLACK);
        mTextDatePaint.setColor(Color.GRAY);
        mTextDatePaint.setTypeface(Typeface.DEFAULT);
        mTextTimePaint.setColor(Color.GRAY);
        mTextTimePaint.setTypeface(Typeface.DEFAULT);
        mTextWeatherPaint.setTypeface(Typeface.DEFAULT);
        mTextWeatherPaint.setColor(Color.GRAY);
        setAntiAlias(false); // Watchface is dependent on fewer pixels in Ambient mode, disabling anti-alias.
    }

    private void setConfigsIfVisible()
    {
        globalcanvas.drawColor(Color.parseColor("#42A5F5"));
        setAntiAlias(true); // Watchface is visible, can restore anti-alias for paint objects back to normal
        setColorForDateAndTime(Color.WHITE);
        setColorForWeatherPaint(Color.WHITE);
    }

    private void setColorForWeatherPaint(int color)
    {
        mTextWeatherPaint.setColor(color);
    }

    public void draw(Canvas canvas, Rect bounds,String Hightemp,String Lowtemp,Integer weatherId,boolean checkForAmbient) {
        time.setToNow();
        initializePaintObjects();
        globalcanvas=canvas;
        if(checkForAmbient)
        {
            // If watch is in Ambient Mode, disable Ant-Alias and draw a black background.
            configureForAmbientMode();
        }
        else {
            //Restore configs if visible
            setConfigsIfVisible();
        }
        String timeText = String.format( TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute);
        canvas.drawText(timeText,bounds.centerX()-TimeOffsetX, TimeOffsetY, mTextTimePaint);

        String dateText = String.format(DATE_FORMAT,Utility.getMonth(time.month + 1), time.monthDay, time.year);
        canvas.drawText(dateText,bounds.centerX()-DateOffSetX, DateOffsetY, mTextDatePaint);

        Log.d(TAG,"Current weather conditions="+Hightemp+","+Lowtemp+","+weatherId);
        if(Hightemp!=null && Lowtemp!=null && weatherId!=null)
        {

            drawHighTemp(canvas,Hightemp,bounds,checkForAmbient);
            drawLowTemp(canvas,Lowtemp,bounds,checkForAmbient);
            if(!checkForAmbient) {
                drawLine(canvas, bounds);
                drawWeatherIcon(canvas, weatherId, bounds, Hightemp);
            }
        }
        //For Debug, making a rough layout!!
        else {
            Log.d(TAG, "Drawing Demo");

            drawHighTemp(canvas, "22", bounds, checkForAmbient);
            drawLowTemp(canvas, "18", bounds, checkForAmbient);
            if (!checkForAmbient) {
                drawLine(canvas, bounds);
                drawWeatherIcon(canvas, 900, bounds, "22");
            }
        }
    }

    private void drawLine(Canvas canvas,Rect bounds)
    {
        canvas.drawLine(bounds.centerX()-25,DividerOffsetY,bounds.centerX()+25,DividerOffsetY,mTextWeatherPaint);
    }

    private void drawWeatherIcon(Canvas canvas,Integer weatherId,Rect bounds,String Hightemp)
    {
        float highTextSize =mTextWeatherPaint.measureText(Hightemp);
        Drawable b = context.getResources().getDrawable(Utility.getSmallArtResourceIdForWeatherCondition(weatherId));
        Bitmap icon = ((BitmapDrawable) b).getBitmap();
        float scaledWidth = (mTextWeatherPaint.getTextSize() / icon.getHeight()) * icon.getWidth();
        Bitmap weatherIcon = Bitmap.createScaledBitmap(icon, (int) scaledWidth, (int) mTextWeatherPaint.getTextSize(), true);
        float iconXOffset = bounds.centerX() - ((highTextSize / 2) + weatherIcon.getWidth() + 30);
        canvas.drawBitmap(weatherIcon, iconXOffset, TempOffsetY - weatherIcon.getHeight()+10, null);
    }
    private void drawHighTemp(Canvas canvas,String Hightemp,Rect bounds,boolean ambient)
    {
        float highTextSize =mTextWeatherPaint.measureText(Hightemp);
        float xOffset = bounds.centerX() - (highTextSize / 2);
        if(!ambient)mTextWeatherPaint.setTypeface(BOLD_TYPEFACE);
        canvas.drawText(Hightemp, xOffset, TempOffsetY, mTextWeatherPaint);
    }

    private void drawLowTemp(Canvas canvas,String Lowtemp,Rect bounds,boolean ambient)
    {
        float highTextSize =mTextWeatherPaint.measureText(Lowtemp);
        if(!ambient) mTextWeatherPaint.setTypeface(NORMAL_TYPEFACE);
        canvas.drawText(Lowtemp, bounds.centerX() + (highTextSize / 2) + 20, TempOffsetY, mTextWeatherPaint);
    }


    public void setAntiAlias(boolean antiAlias) {
        mTextDatePaint.setAntiAlias(antiAlias);
        mTextTimePaint.setAntiAlias(antiAlias);
        mTextWeatherPaint.setAntiAlias(antiAlias);
    }


    public void setColorForDateAndTime(int color) {
        mTextDatePaint.setColor(color);
        mTextTimePaint.setColor(color);
    }


    void calculateOffsets(){
        TimeOffsetX=mTextTimePaint.measureText("12:60")/2;
        DateOffSetX=mTextDatePaint.measureText("JUL 14 2015")/2;
        TimeOffsetY=resources.getDimension(R.dimen.timeoffset_y);
        DateOffsetY=resources.getDimension(R.dimen.dateoffset_y);
        TempOffsetY=resources.getDimension(R.dimen.weatheroffset_y);
        DividerOffsetY=resources.getDimension(R.dimen.divideroffset_y);
    }

    Paint createTimeObject()
    {
        Paint paint=new Paint();
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(resources.getDimension(R.dimen.time_size));
        return paint;
    }

    Paint createDateObject()
    {
        Paint paint=new Paint();
        paint.setTypeface(NORMAL_TYPEFACE);
        paint.setTextSize(resources.getDimension(R.dimen.date_size));
        return paint;
    }

    Paint createWeatherObject(){
        Paint paint=new Paint();
        paint.setTextSize(resources.getDimension(R.dimen.weather_size));
        return paint;
    }


}
