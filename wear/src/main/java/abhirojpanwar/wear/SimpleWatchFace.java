package abhirojpanwar.wear;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;

/**
 * Created by Abhiroj on 2/15/2017.
 */

public class SimpleWatchFace {

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String DATE_FORMAT = "%02d/%02d/%d";


    Typeface NORMAL_TYPEFACE=Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL);

    Paint mTextTimePaint;
    Paint mTextDatePaint;
    Paint mTextTempLowPaint;
    Paint mTextTempHighPaint;

    float TimeOffsetX;
    float TimeOffsetY;
    float DateOffSetX;
    float DateOffsetY;
    float TempOffsetX;
    float TempOffsetY;
    Resources resources;
    Context context;

    private final Time time;

    private boolean shouldShowSeconds = true;

    public static SimpleWatchFace newInstance(Context context) {
        return new SimpleWatchFace(context, new Time());
    }

    SimpleWatchFace(Context context, Time time) {
        mTextTimePaint=createTimeObject();
        mTextDatePaint=createDateObject();
        this.context=context;
        resources=context.getResources();
        this.time = time;
        calculateOffsets();
    }

    public void draw(Canvas canvas, Rect bounds) {
        time.setToNow();
        canvas.drawColor(Color.parseColor("#42A5F5"));

        String timeText = String.format( TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute);
        canvas.drawText(timeText,bounds.centerX()-TimeOffsetX, TimeOffsetY, mTextTimePaint);

        String dateText = String.format(DATE_FORMAT, time.monthDay, (time.month + 1), time.year);
        canvas.drawText(dateText,bounds.centerX()-DateOffSetX, DateOffsetY, mTextDatePaint);
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 10.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        mTextDatePaint.setAntiAlias(antiAlias);
        mTextTimePaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        mTextDatePaint.setColor(color);
        mTextTimePaint.setColor(color);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
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
        return paint;
    }

    Paint createDateObject()
    {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTypeface(NORMAL_TYPEFACE);
        return paint;
    }

    Paint createTempObject(){
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(NORMAL_TYPEFACE);
        paint.setTextSize(R.dimen.temp_size);
        return paint;
    }

}
