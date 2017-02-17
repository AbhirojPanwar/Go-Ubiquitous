package abhirojpanwar.wear;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

//Add

/**
 * Created by Abhiroj on 2/15/2017.
 */

public class WatchFaceService extends CanvasWatchFaceService {
    @Override
    public Engine onCreateEngine() {
        return new WatchEngine();
    }

    class WatchEngine extends CanvasWatchFaceService.Engine implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

        private static final String TAG="WatchFaceService";
        private static final String KEY_HIGH_TEMP="HIGH";
        private static final String KEY_LOW_TEMP="LOW";
        private static final String KEY_WEATHER_ID="WID";
        private static final String KEY_PATH="/PATH_Watchface";



        GoogleApiClient googleApiClient;
        Context context;

        String hightemp;
        String lowtemp;
        Integer weatherId;


        private final long TICK_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1);
        private Handler timeTick;
        private SimpleWatchFace watchFace;
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            timeTick = new Handler(Looper.myLooper());
            startTimerIfNecessary();
            watchFace = SimpleWatchFace.newInstance(getApplicationContext());
            context=WatchFaceService.this;
            googleApiClient=new GoogleApiClient.Builder(context).addApi(Wearable.API).addOnConnectionFailedListener(this).addConnectionCallbacks(this).build();

        }

        private void releaseGoogleApiClient() {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                Wearable.DataApi.removeListener(googleApiClient, onDataChangedListener);
                googleApiClient.disconnect();
            }
        }

        DataApi.DataListener onDataChangedListener=new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEventBuffer) {


                Log.d(TAG,"On Data Changed Stage 1 --> Count"+ dataEventBuffer.getCount());
                for(DataEvent dataEvent:dataEventBuffer)
                {
                    Log.d(TAG,"On Data Changed Stage 2 --> Event Type"+ dataEvent.getType());
                    if(dataEvent.getType()==DataEvent.TYPE_CHANGED) {
                        DataItem dataItem = dataEvent.getDataItem();
                        Log.d(TAG,"On Data Changed Stage 3 --> Data Event Details"+ dataEvent.toString());
                        processItem(dataItem);
                    }
                }
            dataEventBuffer.release();
                invalidateIfNecessary();
            }
        };

        ResultCallback<DataItemBuffer> OnConnectedResultCallback=new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                Log.d(TAG,"On Connected Result Callback "+dataItems.getCount());
                for(DataItem item:dataItems)
                {
                    processItem(item);

                }
               dataItems.release();
                invalidateIfNecessary();
             }
        };


        public void processItem(DataItem dataItem)
        {
            Log.d(TAG,"processing Item"+"Path fetched is ="+dataItem.getUri().getPath());
            if(KEY_PATH==dataItem.getUri().getPath())
         {
             DataMap map=DataMapItem.fromDataItem(dataItem).getDataMap();
             hightemp=map.getString(KEY_HIGH_TEMP);
             lowtemp=map.getString(KEY_LOW_TEMP);
             weatherId=map.getInt(KEY_WEATHER_ID);
             Log.d(TAG,"Details fetched are "+hightemp+","+lowtemp+","+weatherId);
             invalidateIfNecessary();
         }
        }


        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            watchFace.draw(canvas, bounds,hightemp,lowtemp,weatherId,isInAmbientMode());
        }


        private void startTimerIfNecessary() {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                timeTick.post(timeRunnable);
            }
        }

        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    timeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if(visible)
            {
                googleApiClient.connect();
            }
            else
            {
                releaseGoogleApiClient();
            }
            startTimerIfNecessary();
        }


        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            invalidate();
            startTimerIfNecessary();
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            releaseGoogleApiClient();
            super.onDestroy();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.e(TAG, String.valueOf(connectionResult.getErrorCode()));
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "connected GoogleAPI");
            Wearable.DataApi.addListener(googleApiClient, onDataChangedListener);
            Wearable.DataApi.getDataItems(googleApiClient).setResultCallback(OnConnectedResultCallback);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "Suspended GoogleAPI");
        }

    }
}
