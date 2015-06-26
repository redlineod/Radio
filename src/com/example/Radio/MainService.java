package com.example.Radio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by redlineodessa on 30-May-15.
 */
public class MainService extends Service {
    IcyStreamMeta icyStreamMeta;
    static String url = "";
    String title = "";
    String artist = "";
    Handler handler;
    NotificationManager manager;
    String tmpTitle = "";
    String tmpArtist = "";
    MediaPlayer player;
    AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // resume playback
                    if (player == null) initializePlayer();
                    else if (!player.isPlaying()) player.start();
                    player.setVolume(1.0f, 1.0f);
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    // Lost focus for an unbounded amount of time: stop playback and release media player
//                    if (player.isPlaying()) player.stop();
//                    player.release();
//                    player = null;
                    if (player.isPlaying()) player.pause();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Lost focus for a short time, but we have to stop
                    // playback. We don't release the media player because playback
                    // is likely to resume
                    if (player.isPlaying()) player.pause();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lost focus for a short time, but it's ok to keep playing
                    // at an attenuated level
                    if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
                    break;
            }
        }
    };
    AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("qwe", "onCreate");




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("qwe", "onStart");
        manager.cancelAll();
        if (handler != null){
            handler = null;
        }

            url = intent.getStringExtra("stream_url");
            initializePlayer();
            try {
                icyStreamMeta = new IcyStreamMeta(new URL(url));
                Log.d("qwe", "new IcyStreamMeta");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            handler = new Handler();
            startRepeatingTask();

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // could not get audio focus.
            }

        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    private void initializePlayer() {
        releaseMP();
        player = new MediaPlayer();
        try {
            player.setDataSource(url);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            player.prepareAsync();
            player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
        handler = null;
        manager.cancel(101);
        releaseMP();
        audioManager.abandonAudioFocus(listener);


    }

    private void releaseMP() {
        if (player != null) {
            try {
                player.release();
                player = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected class Broadcast extends AsyncTask<Void, Void, IcyStreamMeta> {
        boolean isNew = false;

        @Override
        protected IcyStreamMeta doInBackground(Void... params) {
            Log.d("qwe", "background");
            try {
                icyStreamMeta.refreshMeta();
//                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(icyStreamMeta.getTitle().getBytes("iso-8859-1")), "UTF-8"));
//                String utfTitle = in.readLine();
//                in.close();
//
//                in = new BufferedReader(new InputStreamReader(new StringBufferInputStream(icyStreamMeta.getArtist()), "UTF-8"));
//                String utfArtist = in.readLine();
//                in.close();
                tmpTitle = icyStreamMeta.getTitle();
                tmpArtist = icyStreamMeta.getArtist();
                Log.d("TMPtit=null", String.valueOf(tmpTitle == null));
                Log.d("TMPartist=null", String.valueOf(tmpArtist == null));
                if (tmpTitle == null || tmpTitle.equals("")){
                    tmpTitle = "Unknown title ^(";
                }
                if (tmpArtist == null || tmpArtist.equals("")){
                    tmpArtist = "Unknown artist";
                }
                if (artist == null || title == null || !artist.equals(tmpArtist) || !title.equals(tmpTitle)) {
                    isNew = true;
                    artist = tmpArtist;
                    title = tmpTitle;
                }

                Log.d("tit=null", String.valueOf(title == null));
                Log.d("artist=null", String.valueOf(artist == null));
                Log.d("qwe", title);
                Log.d("qwe", artist);


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("qwe", "IOEX");

            }
            return icyStreamMeta;

        }

        @Override
        protected void onPostExecute(IcyStreamMeta result) {
            super.onPostExecute(result);
            Log.d("ISNEW", String.valueOf(isNew));
            if (isNew) {

                Intent intent = new Intent("META_UPDATED");
                intent.putExtra("artist", artist);
                intent.putExtra("title", title);
                sendBroadcast(intent);
                String artitle = artist + " - " + title;
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 0);
                Notification notification = new Notification.Builder(getApplicationContext())
                        .setContentText(artitle)
                        .setSmallIcon(R.drawable.rock_sign)
                        .setContentIntent(pendingIntent)
                        .setTicker(artitle)
                        .setOngoing(true)
                        .getNotification();
                manager.notify(101, notification);
            }

        }
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            new Broadcast().execute();
            if (handler != null)
            handler.postDelayed(mHandlerTask, 5000);
        }
    };


    void startRepeatingTask() {
        mHandlerTask.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(mHandlerTask);
    }


}



