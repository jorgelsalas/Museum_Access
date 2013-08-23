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

public class TextoActivity extends Activity {
	
	private int id_articulo;
	private Acutes corrector = new Acutes();

	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        
        
        Intent intentCaptura = getIntent();
        //Toast.makeText(this, "Entro a textos", Toast.LENGTH_LONG).show();
        id_articulo = intentCaptura.getIntExtra("id_articulo", -1);
        //Toast.makeText(this, "Valor de id_articulo: " + id_articulo, Toast.LENGTH_LONG).show();
        
        poblarTexto(id_articulo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_texto, menu);
        return true;
    }
    
    private void poblarTexto(int id_articulo){
    	String url = getString(R.string.url_servidor);
    	url += getString(R.string.nombre_php_obtener_textos);
    	url += "?id_articulo=" + id_articulo;
    	
    	String line;
    	String ext;
    	
    	try{    	
    		URL conexion = new URL(url);
 		    URLConnection tc = conexion.openConnection();
 		    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
 		
 		    int id;
 		    String descripcion;
 		    
 		    while ((line = in.readLine()) != null) {
 		    	
 		    	//En caso de que no se recuperen elementos se indica esto al usuario
 	 		    if (line.equalsIgnoreCase("null")){
 	 		    	/*Se agregan un row*/
 	            	TableLayout tl = (TableLayout) findViewById(R.id.tabla_textos);
 	            	tl.setStretchAllColumns(true);
 	            	TableRow tr = new TableRow(this);
 	            	
 	            	tr.setBackgroundResource(R.drawable.border_tabla_result);
 	            	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
 	            	tl.addView(tr, trlp);
 		            
 		            TextView aviso = new TextView(this);
 		            aviso.setText(getResources().getString(R.string.advertencia_no_hay_textos));
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
		            id = Integer.parseInt(jo.getString("id_texto"));
		            descripcion = corrector.reemplazar(jo.getString("descripcion"));
		            ext = jo.getString("extension");
		            
		            /*Se agregan rows*/
	            	TableLayout tl = (TableLayout) findViewById(R.id.tabla_textos);
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
	        				reproducirTexto(identificador, extension);
	        			}
	        		});
		        }
		    }
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
    }
    
    private void reproducirTexto(int idTexto, String extension){
		
	    String url = getString(R.string.url_servidor) + getString(R.string.carpeta_texto) + "/" + idTexto + "." + extension;
   		Intent reproducirTexto = new Intent(this, ReproduccionTextoActivity.class);
	    reproducirTexto.putExtra("URL_Texto", url);
   		startActivity(reproducirTexto);	
   		
	}
}
