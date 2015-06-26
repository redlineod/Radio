package com.example.Radio;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SC on 27.05.2015.
 */
public class PlayFragment extends Fragment implements View.OnClickListener {
    private Button buttonPlay;
    private Button buttonStop;
    private TextView artist;
    private TextView title;
    private ImageView albumArt;

    final static String API_KEY = "88ecf0b81edd5dd87465f7e5668727fb";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.play, container, false);
        buttonPlay = (Button) rootView.findViewById(R.id.button_play);
        buttonStop = (Button) rootView.findViewById(R.id.button_stop);

        buttonPlay.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonStop.setVisibility(View.INVISIBLE);

        albumArt = (ImageView)rootView.findViewById(R.id.albumArt);

        artist = (TextView)rootView.findViewById(R.id.artist);
        title = (TextView)rootView.findViewById(R.id.title);
        if (savedInstanceState != null){
            artist.setText(savedInstanceState.getString("artist"));
            title.setText(savedInstanceState.getString("title"));
        }



        return rootView;
    }
    @Override
    public void onClick(View v) {
        OnSelectedButtonListener listener = (OnSelectedButtonListener)getActivity();
        Log.d("qwe", "onclick");

        listener.onButtonSelected(v.getId());
    }

    public void setArtist(String artist) {
        this.artist.setText(artist);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }
    public void setAlbumArt(Bitmap image){
        albumArt.setImageBitmap(image);
    }
    public void setButtonPlayVisible(){
        buttonStop.setVisibility(View.INVISIBLE);
        buttonPlay.setVisibility(View.VISIBLE);
    }
    public void setButtonStopVisible(){
        buttonPlay.setVisibility(View.INVISIBLE);
        buttonStop.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("artist", artist.getText().toString());
//        outState.putString("title", title.getText().toString());
//    }

}
