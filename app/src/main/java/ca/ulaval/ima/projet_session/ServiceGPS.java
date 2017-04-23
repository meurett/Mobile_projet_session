package ca.ulaval.ima.projet_session;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.location.LocationManager;
import android.content.Context;
import android.location.Location;
import android.location.Criteria;

public class ServiceGPS extends Service
{
    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_SET_INT_VALUE = 3;
    static final int MSG_SET_STRING_VALUE = 4;
    static final int MSG_SET_DISTANCE_VALUE = 5;
    static final int NOTIFICATION_ID = 1;

    private Timer timer = new Timer();
    private int counter = 0, incrementby = 1;
    private static boolean isRunning = false;
    ArrayList<Messenger> messengers = new ArrayList<>();
    final Messenger toServiceMessenger = new Messenger(new ServiceHandler());

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener();

    @Override
    public IBinder onBind(Intent intent)
    {
        return toServiceMessenger.getBinder();
    }

    private class ServiceHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case MSG_REGISTER_CLIENT:
                    messengers.add(message.replyTo);
                    locationListener.initialize();
                    break;
                case MSG_UNREGISTER_CLIENT:
                    messengers.remove(message.replyTo);
                    break;
                case MSG_SET_INT_VALUE:
                    incrementby = message.arg1;
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    private void sendMessageToUI(int intvaluetosend)
    {
        //Reverse order to allow removal while iterating
        for (int i = messengers.size() - 1; i >= 0; i--)
        {
            try
            {
                messengers.get(i).send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));
                Bundle bundle = new Bundle();
                bundle.putString("str1", "ab" + intvaluetosend + "cd");
                Message message = Message.obtain(null, MSG_SET_STRING_VALUE);
                message.setData(bundle);
                messengers.get(i).send(message);
            }
            catch (RemoteException e)
            {
                messengers.remove(i);
            }
        }
    }

    private void sendMessageToUI2(float distance)
    {
        //Reverse order to allow removal while iterating
        for (int i = messengers.size() - 1; i >= 0; i--)
        {
            try
            {
                Bundle bundle = new Bundle();
                bundle.putFloat("distance", distance);
                Message message = Message.obtain(null, MSG_SET_DISTANCE_VALUE);
                message.setData(bundle);
                messengers.get(i).send(message);
            }
            catch (RemoteException e)
            {
                messengers.remove(i);
            }
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        showNotification();
        timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 0, 100L);
        isRunning = true;

        //TEST
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try
        {
            List<String> test = locationManager.getAllProviders();
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);

        }
        catch (java.lang.SecurityException e) {}
        catch (IllegalArgumentException e)
        {
            Exception exception = e;
        }
    }

    private class LocationListener implements android.location.LocationListener
    {
        Location lastLocation = null;
        float distanceTraveled = 0;

        public void initialize()
        {
            sendMessageToUI2(distanceTraveled);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            if (lastLocation != null)
            {
                distanceTraveled += lastLocation.distanceTo(location);
                sendMessageToUI2(distanceTraveled);
            }
            else
            {
                distanceTraveled += 1;
                sendMessageToUI2(distanceTraveled);
            }
            lastLocation = location;
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    }

    private void showNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.icon_gps_tracking)
            .setContentTitle("My Notification Title")
            .setContentText("Something interesting happened")
            .setOngoing(true);
        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    public static boolean isRunning()
    {
        return isRunning;
    }

    private void onTimerTick()
    {
        try
        {
            counter += incrementby;
            sendMessageToUI(counter);

        }
        catch (Throwable t) { Log.e("TimerTick", "Timer Tick Failed.", t); }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (timer != null) {timer.cancel();}
        counter=0;
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
        isRunning = false;
    }
}