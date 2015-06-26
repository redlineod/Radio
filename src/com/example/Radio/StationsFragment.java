package com.example.Radio;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by SC on 27.05.2015.
 */
public class StationsFragment extends Fragment {
    private static final String RADIO_ROCKS_STAND = "http://online-radioroks2.tavrmedia.ua/RadioROKS";
    private static final String RADIO_NEW_ROCK = "http://online-radioroks2.tavrmedia.ua/RadioROKS_NewRock";
    private static final String RADIO_OCEAN = "http://online-radioroks2.tavrmedia.ua/RadioROKS_OE";
    private static final String RADIO_ROCKS_UA = "http://online-radioroks2.tavrmedia.ua/RadioROKS_Ukr";
    private static final String RADIO_HARD_N_HEAVY = "http://online-radioroks2.tavrmedia.ua/RadioROKS_HardnHeavy";
    private static final String RADIO_BALLADS = "http://online-radioroks2.tavrmedia.ua/RadioROKS_Ballads";
    private static final String RADIO_BEATLES = "http://online-radioroks2.tavrmedia.ua/RadioROKS_Beatles";
    private static final String RADIO_ZAYCEV_NEW_ROCK = "http://radio.zaycev.fm:9002/alternative/ZaycevFM(256)";
    private static final String RADIO_SWISS_JAZZ = "http://stream.srg-ssr.ch/m/rsj/mp3_128";
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.stations, container, false);

        String[] stationsList = getResources().getStringArray(R.array.stations);
        MyArrayAdapter adapter = new MyArrayAdapter(rootView.getContext(), stationsList);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
//                R.array.stations, R.layout.list_stations);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.list_stations, stationsList);

        gestureDetector = new GestureDetector(rootView.getContext(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        listView.setOnTouchListener(gestureListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnSelectedButtonListener listener = (OnSelectedButtonListener) getActivity();
                switch ((int) id) {
                    case 0:
                        listener.onButtonSelected(RADIO_ROCKS_STAND);
                        break;
                    case 1:
                        listener.onButtonSelected(RADIO_NEW_ROCK);
                        break;
                    case 2:
                        listener.onButtonSelected(RADIO_OCEAN);
                        break;
                    case 3:
                        listener.onButtonSelected(RADIO_ROCKS_UA);
                        break;
                    case 4:
                        listener.onButtonSelected(RADIO_HARD_N_HEAVY);
                        break;
                    case 5:
                        listener.onButtonSelected(RADIO_BALLADS);
                        break;
                    case 6:
                        listener.onButtonSelected(RADIO_BEATLES);
                        break;
                    case 7:
                        listener.onButtonSelected(RADIO_ZAYCEV_NEW_ROCK);
                        break;
                    case 8:
                        listener.onButtonSelected(RADIO_SWISS_JAZZ);
                        break;
                }
                MainActivity.selectPlayTab();
            }
        });
        listView.setAdapter(adapter);
        return rootView;
    }
}
