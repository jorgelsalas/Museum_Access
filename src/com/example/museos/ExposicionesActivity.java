package com.example.museos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.museos.model.Acutes;

public class ExposicionesActivity extends Activity {
	
	ImageView logo;
	private Acutes corrector = new Acutes();
	Button botonCerrarSesion, botonAyuda;
	private AlertDialog alertDialog;

	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exposiciones);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        logo = (ImageView) findViewById(R.id.expos_logo);
        
        botonCerrarSesion = (Button) findViewById(R.id.button_expos_cerrar_sesion);
        botonAyuda = (Button) findViewById(R.id.button_expos_ayuda);
        
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(R.drawable.emo_im_winking);
    	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
    	   }
    	});
        
        botonAyuda.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDialog.setTitle(getResources().getString(R.string.titulo_dialogo_ayuda));
				alertDialog.setMessage(getResources().getString(R.string.mensaje_ayuda_expo));
				alertDialog.show();
			}
		});
        
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cerrarSesion();
			}
		});
        
        cargarInfoExpos();
    }
	
	private void cerrarSesion(){
    	//Redirige a Login
		Intent cerrar = new Intent(this, Login.class);
		cerrar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(cerrar);
		finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_exposiciones, menu);
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
    
    private void cargarInfoExpos(){
    	
    	String url = getString(R.string.url_servidor);
    	url += getString(R.string.nombre_php_expos);
    	String line;
    	
    	try{
       	    URL conexion = new URL(url);
	        URLConnection tc = conexion.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
	        
	        String url2 = getString(R.string.url_servidor);
	        url2 += getString(R.string.carpeta_imagenes_expo) + "/";
	        String id;
	        String nombre;
	        
	        TableLayout tl = (TableLayout) findViewById(R.id.tabla_expos);
         	tl.setStretchAllColumns(true);
	        
	        while ((line = in.readLine()) != null) {
	        	
	        	//En caso de que no se recuperen elementos se indica esto al usuario
 	 		    if (line.equalsIgnoreCase("null")){
 	 		    	/*Se agregan un row*/
 	            	TableRow tr = new TableRow(this);
 	            	tl.addView(tr);
 		            
 		            TextView aviso = new TextView(this);
 		            aviso.setText(getResources().getString(R.string.advertencia_no_hay_exposiciones));
 		            aviso.setTextColor( getResources().getColor(R.color.azul_claro));
 		            aviso.setBackgroundResource(R.drawable.border_par);
 		            aviso.setGravity(Gravity.CENTER_HORIZONTAL);
 		            TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
 		            tr.addView(aviso, trlp);
 			    }
 	 		    
	            JSONArray ja = new JSONArray(line);
	            for (int i = 0; i < ja.length(); i++) {
	                
	            	JSONObject jo = (JSONObject) ja.get(i);
	                
	                id = jo.getString("id") + "." + jo.getString("extension_imagen");
	                nombre = corrector.reemplazar(jo.getString("nombre"));
	                final String nombre_php = corrector.reemplazar(jo.getString("nombre_php"));
	                
	                //Se crea el objeto tipo imagen y se obtiene la misma de un URL
	                View v = new ImageView(getBaseContext());
	                ImageView image; 
	                image = new ImageView(v.getContext()); 
	                image.setId(i);
	                image.setImageDrawable(obtenerImagenDeURL(url2+id, "src name"));
	                v.setVisibility(0);
	                image.setVisibility(0);
	                
	                //Toast.makeText(this, url2+id, Toast.LENGTH_LONG).show();
	                
	                //AGREGA ITEMS AL TABLE LAYOUT/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	                if (i % 2 == 0){
	                	//Se crea un nuevo table row y se agrega a la tabla
	                	
	                	TableRow tr = new TableRow(this);
	                	tr.setId(i+2*ja.length());
	                	
	                	
	                	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
	                	
	                	tl.addView(tr, trlp);
	                }
	                
	                //Agrega la imagen al RL de la celda
	                RelativeLayout celda = new RelativeLayout(this);
	                celda.setId(i+ja.length());
	                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
	                celda.addView(image, lp);
	                
	                //Se agrega el titulo de cada exposición al RL de la celda
	                TextView nombreExpo = new TextView(this);
	                nombreExpo.setText(nombre);
	                nombreExpo.setId(i+5*ja.length());
	                nombreExpo.setTextColor( getResources().getColor(R.color.naranja));
	                //nombreExpo.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.general_header3));
	                RelativeLayout.LayoutParams lpTexto = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	                lpTexto.addRule(RelativeLayout.BELOW, i);
	                lpTexto.addRule(RelativeLayout.CENTER_HORIZONTAL);
	                if (i == 0){
	                	nombreExpo.setPadding(0, obtenerImagenDeURL(url2+id, "src name").getIntrinsicHeight(), 0, 0);
	                }
	                celda.addView(nombreExpo, lpTexto);
	                
	                //Se agrega la celda al TableRow
	                int idRow = i;
	                if (idRow % 2 == 0){
	                	idRow = idRow+2*ja.length();
	                }
	                else {
	                	idRow = idRow-1+2*ja.length();
	                }
	                TableRow row = (TableRow) findViewById(idRow);
	                row.addView(celda);
	                
	                final int bla = i;
	                image.setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				pruebaClick(bla);
	        				mostrar_expo(nombre_php);
	        			}
	        		});
	            }
	        }
       }
       catch(Exception e){
       	Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
       }
    	
    }
    
    public Drawable obtenerImagenDeURL(String url, String nombre) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, nombre);
            return d;
        } catch (Exception e) {
            return null;
        }
    }
    
    public void pruebaClick(int id){
    	//Toast.makeText(this, "El on click funciono! id = " + id, Toast.LENGTH_LONG).show();
    }
    
    public void mostrar_expo(String nombre_php){
    	Intent buscar = new Intent(this, ResultadosActivity.class);
		String url = getString(R.string.url_servidor) + nombre_php;
		boolean bloquearBotonFiltrarResultados = true;
		buscar.putExtra("bloquearBotonFiltrarResultados", bloquearBotonFiltrarResultados);
		buscar.putExtra("url", url);
		startActivity(buscar);
    }

}
