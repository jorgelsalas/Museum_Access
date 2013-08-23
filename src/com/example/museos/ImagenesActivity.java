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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class ImagenesActivity extends Activity {
	
	private int id_articulo;

	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagenes);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        
        
        Intent intentCaptura = getIntent();
        //Toast.makeText(this, "Entro a imagenes", Toast.LENGTH_LONG).show();
        id_articulo = intentCaptura.getIntExtra("id_articulo", -1);
        //Toast.makeText(this, "Valor de id_articulo: " + id_articulo, Toast.LENGTH_LONG).show();
        
        poblarImagenes(id_articulo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_imagenes, menu);
        return true;
    }
    
    private void poblarImagenes(int id_articulo){
    	String url = getString(R.string.url_servidor);
    	url += getString(R.string.nombre_php_obtener_imagenes);
    	url += "?id_articulo=" + id_articulo;
    	
    	String line;
    	//Obliga a inicializar el array direcciones
    	String [] direcciones = new String[1];
    	
    	try {
		    URL conexion = new URL(url);
		    URLConnection tc = conexion.openConnection();
		    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
		    
		    //Toast.makeText(this, "url1: " + url, Toast.LENGTH_LONG).show();
		    
		    String url2 = getString(R.string.url_servidor);
		    url2 += getString(R.string.carpeta_imagenes)+ "/";
		    
		    //Toast.makeText(this, "url2: " + url2, Toast.LENGTH_LONG).show();
		    
		    while ((line = in.readLine()) != null) {
		    	//Toast.makeText(this, "line: " + line, Toast.LENGTH_LONG).show();
		        JSONArray ja = new JSONArray(line);
		        direcciones = new String[ja.length()];
		        
		        for (int i = 0; i < ja.length(); i++) {
		            JSONObject jo = (JSONObject) ja.get(i);
		            direcciones[i] = url2 + jo.getString("id_imagen") + "." + jo.getString("extension");
		            //Toast.makeText(this, "id imagen: " + jo.getString("id_imagen") + "." + jo.getString("extension"), Toast.LENGTH_LONG).show();
		        }
		        
		    }
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
    	
    	((Gallery) findViewById(R.id.gallery)).setAdapter(new ImageAdapter(this, direcciones));
    }
    
    public class ImageAdapter extends BaseAdapter {
		/* The parent context */
		private Context myContext;
		
		/* URL-Strings to some remote images. */
		private String[] myRemoteImages = {
				"http://www.anddev.org/images/tiny_tutheaders/weather_forecast.png",
				"http://www.anddev.org/images/tiny_tutheaders/cellidtogeo.png",
				"http://www.anddev.org/images/tiny_tutheaders/droiddraw.png"};
		
		public void setURLS(String[] nuevosURLS){
			myRemoteImages = nuevosURLS;
		}
		
		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c, String[] direcciones) { this.myContext = c; myRemoteImages = direcciones;}
		
		/** Returns the amount of images we have defined. */
		public int getCount() { return this.myRemoteImages.length; }
		
		/* Use the array-Positions as unique IDs */
		public Object getItem(int position) { return position; }
		public long getItemId(int position) { return position; }
		
		/** Returns a new ImageView to
         * be displayed, depending on
         * the position passed. */
		
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(this.myContext);
			try {
				/* Open a new URL and get the InputStream to load data from it. */
				URL aURL = new URL(myRemoteImages[position]);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                /* Buffered is always good for a performance plus. */
                BufferedInputStream bis = new BufferedInputStream(is);
                
                /* Decode url-data to a bitmap. */
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
                /* Apply the Bitmap to the ImageView that will be returned. */
                i.setImageBitmap(bm);
			}
			catch (IOException e) {
                i.setImageResource(R.drawable.ic_action_search);
                Log.e("DEBUGTAG", "Remote Image Exception", e);
			}
			
			/* Image should be scaled as width/height are set. */
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            
            /* Set the Width/Height of the ImageView. */
            i.setLayoutParams(new Gallery.LayoutParams(500, 500));
            return i;
		}
		
		/* Returns the size (0.0f to 1.0f) of the views
         * depending on the 'offset' to the center. */
        public float getScale(boolean focused, int offset) {
            /* Formula: 1 / (2 ^ offset) */
            return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
        }
	}
}
