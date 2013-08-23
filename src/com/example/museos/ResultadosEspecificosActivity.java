package com.example.museos;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class ResultadosEspecificosActivity extends TabActivity {
	
	int id_articulo;
	
	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        setContentView(R.layout.activity_resultados_especificos);
        
        Intent intentCaptura = getIntent();
        
        //Toast.makeText(this, "Entro al resultados especificos", Toast.LENGTH_LONG).show();
        id_articulo = intentCaptura.getIntExtra("id_articulo", -1);
        //Toast.makeText(this, "Valor de id_articulo: " + id_articulo, Toast.LENGTH_LONG).show();
    	//id_articulo = Integer.parseInt(intentCaptura.getStringExtra("id_articulo"));
        

        ////////////////////SECCION DE TABS//////////////////////////////////////////////////////
        TabHost tabHost = getTabHost();
		
		//Tab para texto
        TabSpec tabTexto = tabHost.newTabSpec("Texto");
        tabTexto.setIndicator("Texto");
        Intent textIntent = new Intent(this, TextoActivity.class);
        textIntent.putExtra("id_articulo", id_articulo);
		tabTexto.setContent(textIntent);
		
		//Tab para imagenes
        TabSpec tabImagenes = tabHost.newTabSpec("Imagenes");
        tabImagenes.setIndicator("Imagenes");
        Intent imagenesIntent = new Intent(this, ImagenesActivity.class);
        imagenesIntent.putExtra("id_articulo", id_articulo);
        tabImagenes.setContent(imagenesIntent);
        
        //Tab para videos
        TabSpec tabVideos = tabHost.newTabSpec("Videos");
        tabVideos.setIndicator("Videos");
        Intent videosIntent = new Intent(this, VideosActivity.class);
        videosIntent.putExtra("id_articulo", id_articulo);
        tabVideos.setContent(videosIntent);
        
        //Tab para audio
        TabSpec tabAudio = tabHost.newTabSpec("Audio");
        tabAudio.setIndicator("Audio");
        Intent audioIntent = new Intent(this, AudioActivity.class);
        audioIntent.putExtra("id_articulo", id_articulo);
        tabAudio.setContent(audioIntent);
        
        //se agregan los tabs al tabHost
        tabHost.addTab(tabTexto); // se agrega tab para texto
        tabHost.addTab(tabImagenes); // se agrega tab para imagenes
        tabHost.addTab(tabVideos); // se agrega tab para videos
        tabHost.addTab(tabAudio); // se agrega tab para audio
        
        tabHost = getTabHost();
        
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++){
        	TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.blanco));
        } 
        
        ////////////////////TERMINA SECCION DE TABS//////////////////////////////////////////////////////
        
        
    	
        getActionBar().hide();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_resultados_especificos, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
