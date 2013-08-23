package com.example.museos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

public class Reproduccion_A_V_Activity extends Activity {

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproduccion__a__v_);
        Intent intentCaptura = getIntent();
        //String URL = intentCaptura.getStringExtra(AudioActivity.URL_Multimedia);
        String url = intentCaptura.getStringExtra("URL_Multimedia");
        boolean esVideo = intentCaptura.getBooleanExtra("esVideo", false);
        //Toast.makeText(this, "Reproduciendo: "+url, Toast.LENGTH_LONG).show();
        correrMedia(url);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
        
        if (!esVideo){
        	View v = new ImageView(getBaseContext());
            ImageView image; 
            image = new ImageView(v.getContext()); 
            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_stat_playing2));
            v.setVisibility(0);
            image.setVisibility(0);
            
            RelativeLayout contenedor = (RelativeLayout) findViewById(R.id.contenedor_reproductor_media); 
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rlp.addRule(RelativeLayout.CENTER_VERTICAL);
            contenedor.addView(image, rlp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_reproduccion__a__v_, menu);
        return true;
    }
    
    private void correrMedia(String URL){
        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        videoView.showContextMenu();
        Uri uri = Uri.parse(URL);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

}
