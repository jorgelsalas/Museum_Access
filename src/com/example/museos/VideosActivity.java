package com.example.museos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.museos.model.Acutes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class VideosActivity extends Activity {
	
	private int id_articulo;
	private Acutes corrector = new Acutes();

	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        
        Intent intentCaptura = getIntent();
        //Toast.makeText(this, "Entro a videos", Toast.LENGTH_LONG).show();
        id_articulo = intentCaptura.getIntExtra("id_articulo", -1);
        //Toast.makeText(this, "Valor de id_articulo: " + id_articulo, Toast.LENGTH_LONG).show();
        
        poblarVideos(id_articulo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_videos, menu);
        return true;
    }
    
    private void poblarVideos(int id_articulo){
    	String url = getString(R.string.url_servidor);
    	url += getString(R.string.nombre_php_obtener_videos);
    	url += "?id_articulo=" + id_articulo;
    	
    	String line;
    	
    	try{    	
    		URL conexion = new URL(url);
 		    URLConnection tc = conexion.openConnection();
 		    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
 		
 		    int id;
 		    String descripcion;
 		    
 		    int cont = 0;
 		   String ext;
 		    
 		    
 		    while ((line = in.readLine()) != null) {
 		    	//Toast.makeText(this, line + " cont: " + cont , Toast.LENGTH_LONG).show();
 		    	cont++;
 		    	
 		    	//En caso de que no se recuperen elementos se indica esto al usuario
 	 		    if (line.equalsIgnoreCase("null")){
 	 		    	/*Se agregan un row*/
 	            	TableLayout tl = (TableLayout) findViewById(R.id.tabla_videos);
 	            	tl.setStretchAllColumns(true);
 	            	TableRow tr = new TableRow(this);
 	            	
 	            	tr.setBackgroundResource(R.drawable.border_tabla_result);
 	            	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
 	            	tl.addView(tr, trlp);
 		            
 		            TextView aviso = new TextView(this);
 		            aviso.setText(getResources().getString(R.string.advertencia_no_hay_videos));
 		            aviso.setTextColor( getResources().getColor(R.color.azul_claro));
 		            aviso.setBackgroundResource(R.drawable.border);
 		            aviso.setGravity(Gravity.CENTER);
 		            aviso.setMovementMethod(new ScrollingMovementMethod());
 		            aviso.setHorizontallyScrolling(false);
 		            tr.addView(aviso);
 			    }
 		    	
 			    JSONArray ja = new JSONArray(line);
		        
		        for (int i = 0; i < ja.length(); i++) {
		        	
		            JSONObject jo = (JSONObject) ja.get(i);
		            id = Integer.parseInt(jo.getString("id_video"));
		            descripcion = corrector.reemplazar(jo.getString("descripcion"));
		            ext = jo.getString("extension");
		            
		            /*Se agregan rows*/
	            	TableLayout tl = (TableLayout) findViewById(R.id.tabla_videos);
	            	tl.setStretchAllColumns(true);
	            	TableRow tr = new TableRow(this);
	            	tr.setId(i+2*ja.length());
	            	tr.setBackgroundResource(R.drawable.border_tabla_result);
	            	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
	            	tl.addView(tr, trlp);
		            
		            TextView tv_descripcion = new TextView(this);
		            tv_descripcion.setText(descripcion);
		            tv_descripcion.setTextColor( getResources().getColor(R.color.azul_claro));
		            tv_descripcion.setBackgroundResource(R.drawable.border);
		            tv_descripcion.setGravity(Gravity.CENTER);
		            tv_descripcion.setMovementMethod(new ScrollingMovementMethod());
		            tv_descripcion.setHorizontallyScrolling(false);
		            tr.addView(tv_descripcion);
		 
	                final int identificador = id;
	                final String extension = ext;
	                tv_descripcion.setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				reproducirVideo(identificador, extension);
	        			}
	        		});
		        }
		    }
 		    
 		    
 		    
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
    }
    
    private void reproducirVideo(int idVideo, String extension){
		
	    String url = getString(R.string.url_servidor) + getString(R.string.carpeta_video) + "/" + idVideo + "." + extension;
   		Intent reproducirVideo = new Intent(this, Reproduccion_A_V_Activity.class);
   		reproducirVideo.putExtra("URL_Multimedia", url);
   		reproducirVideo.putExtra("esVideo", true);
   		startActivity(reproducirVideo);	
   		
	}
}
