package ca.ulaval.ima.projet_session;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//import android.app.Notification;
import android.app.NotificationManager;
//import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ServiceGPS extends Service
{
    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_SET_INT_VALUE = 3;
    static final int MSG_SET_STRING_VALUE = 4;

    private NotificationManager notificationManager;
    private Timer timer = new Timer();
    private int counter = 0, incrementby = 1;
    private static boolean isRunning = false;
    ArrayList<Messenger> messengers = new ArrayList<>();
    final Messenger toServiceMessenger = new Messenger(new ServiceHandler());

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
                Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
                msg.setData(bundle);
                messengers.get(i).send(msg);
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
        Log.i("MyService", "Service Started.");
        //showNotification();
        timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 0, 100L);
        isRunning = true;

        //TEST

    }

//    private void showNotification()
//    {
//        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        // In this sample, we'll use the same text for the ticker and the expanded notification
//        CharSequence text = getText(R.string.service_started);
//        // Set the icon, scrolling text and timestamp
//        Notification notification = new Notification(R.drawable.icon, text, System.currentTimeMillis());
//        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
//        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.service_label), text, contentIntent);
//        // Send the notification.
//        // We use a layout id because it is a unique number.  We use it later to cancel.
//        notificationManager.notify(R.string.service_started, notification);
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //Run until stopped
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
        //notificationManager.cancel(R.string.service_started); // Cancel the persistent notification.
        isRunning = false;
    }
}