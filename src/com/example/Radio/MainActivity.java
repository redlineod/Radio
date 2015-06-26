package com.example.Radio;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity implements OnSelectedButtonListener {
    /**
     * Called when the activity is first created.
     */
    final static String API_KEY = "88ecf0b81edd5dd87465f7e5668727fb";
    private PlayFragment playFragment;
    private StationsFragment stationsFragment;
    private static ActionBar.Tab playTab;
    private static ActionBar.Tab stationsTab;
    private static String url = "";
    private RelativeLayout relativeLayout;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    private BroadcastReceiver uiUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String artist = intent.getExtras().getString("artist");
            String title = intent.getExtras().getString("title");
            Log.d("BROADCAST", artist + title);
            playFragment.setTitle(title);
            playFragment.setArtist(artist);

            StringBuilder stringBuilder = new StringBuilder("http://ws.audioscrobbler.com/2.0/");
            stringBuilder.append("?method=track.getInfo");
            stringBuilder.append("&api_key=");
            stringBuilder.append(API_KEY);
            stringBuilder.append("&artist=" + artist.replaceAll(" ", "+"));
            stringBuilder.append("&track=" + title.toString().replaceAll(" ", "+"));

            String url = "";
            try {
                url = new RetrieveFeedTask().execute(stringBuilder.toString()).get();
//            Log.d("qwe", url);
            } catch (InterruptedException e) {
                url = "";
                e.printStackTrace();
            } catch (ExecutionException e) {
                url = "";
                e.printStackTrace();
            }
            if (url != null && !url.equals(""))
                new DownloadImageTask().execute(url);
            else playFragment.setAlbumArt(null);

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = getTabListener();

        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        relativeLayout = (RelativeLayout) findViewById(R.id.container);
        relativeLayout.setOnTouchListener(gestureListener);
//        playFragment = new PlayFragment();
//        stationsFragment = new StationsFragment();

        playTab = actionBar.newTab()
                .setText("Playing")
                .setTag("play_tab");

        stationsTab = actionBar.newTab()
                .setText("Stations")
                .setTag("stations_tab");

        actionBar.addTab(playTab
                .setTabListener(tabListener));
        actionBar.addTab(stationsTab
                .setTabListener(tabListener));

        registerReceiver(uiUpdated, new IntentFilter("META_UPDATED"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.d("qwe", "onMenuItem");
        switch (item.getItemId()) {
            case R.id.menu_exit:
                Log.d("qwe", "exit");
                stopService(new Intent(this, MainService.class));
                finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(uiUpdated);
//        stopService(new Intent(this, MainService.class));


    }

    public ActionBar.TabListener getTabListener() {
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                switch (tab.getPosition()) {
                    case 0:
                        if (playFragment == null) {
                            playFragment = new PlayFragment();
                            ft.add(R.id.container, playFragment);
                        } else ft.show(playFragment);

                        break;
                    case 1:
                        if (stationsFragment == null) {
                            stationsFragment = new StationsFragment();
                            ft.add(R.id.container, stationsFragment);
                        } else ft.show(stationsFragment);

                        break;
                }
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                switch (tab.getPosition()) {

                    case 0:
                        if (playFragment == null) {
//                            playFragment = new PlayFragment();
//                            ft.add(R.id.container, playFragment);
                        } else ft.hide(playFragment);
                        break;
                    case 1:
                        if (stationsFragment == null) {
//                            stationsFragment = new StationsFragment();
//                            ft.add(R.id.container, stationsFragment);
                        } else ft.hide(stationsFragment);
                        break;
                }

            }


            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };
        return tabListener;
    }

    @Override
    public void onButtonSelected(int btnId) {
        Log.d("qwe", "onbtnslctd");
        Log.d("qwe", String.valueOf(btnId));
        if (url == null || url.equals("")) {
            url = "http://online-radioroks2.tavrmedia.ua/RadioROKS";
        }


        switch (btnId) {
            case R.id.button_play:
                Log.d("qwe", "btnPl");
//                stopService(new Intent(this, MainService.class));
                playFragment.setButtonStopVisible();
                Intent intent = new Intent(this, MainService.class);
                intent.putExtra("stream_url", this.url);
                startService(intent);
                break;
            case R.id.button_stop:
                Log.d("qwe", "btnStp");
                playFragment.setButtonPlayVisible();
                stopService(new Intent(this, MainService.class));
                playFragment.setTitle("Title");
                playFragment.setArtist("Artist");
                playFragment.setAlbumArt(null);
                break;
        }

    }

    @Override
    public void onButtonSelected(String url) {
        if (!this.url.equals(url)) {
            this.url = url;
            onButtonSelected(R.id.button_play);
        }

    }

    public static void selectPlayTab() {
        playTab.select();
    }

    public static void selectStationTab() {
        stationsTab.select();
    }

    public class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            String albumArtUrl = null;
            Log.d("async", "doInBg");
            try {

                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(urls[0]); // getting XML from URL
                Document doc = parser.getDomElement(xml);

                NodeList nl = doc.getElementsByTagName("album");

                Log.d("async", "in parser");
                Log.d("qwe", String.valueOf(nl.getLength()));
                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);
                    Log.d("qwe", parser.getElementValue(e));

                    NodeList nl2 = e.getElementsByTagName("image");
                    for (int j = 0; j < nl2.getLength(); j++) {
                        Element el = (Element) nl2.item(j);
                        Log.d("qwe", "Size = " + el.getAttribute("size") + " = " + parser.getElementValue(el));
                        if (el.getAttribute("size").contentEquals("extralarge")) {
                            Log.d("async", "in if");
                            albumArtUrl = parser.getElementValue(el);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (albumArtUrl != null)
                Log.d("async", albumArtUrl);
            return albumArtUrl;

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            playFragment.setAlbumArt(result);
        }
    }
}
