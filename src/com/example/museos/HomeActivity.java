package com.example.museos;

import java.io.BufferedReader;
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
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.museos.model.Acutes;

public class HomeActivity extends Activity {
	
	private Button botonExpos, botonInfoMuseo, botonCerrarSesion, botonMarcarTodo, botonBuscar, botonAyuda;
	private EditText editBuscar;
	private TextView nombreMuseo;
	private JSONArray ja;
	private TableLayout tl;
	private Acutes corrector = new Acutes();
	private Intent intentCaptura;
	private AlertDialog alertDialog;
    
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        
        setContentView(R.layout.activity_home);
        
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
        
        botonExpos = (Button) findViewById(R.id.button_home_expo_del_dia);
        botonInfoMuseo = (Button) findViewById(R.id.button_home_info_museo);
        botonCerrarSesion = (Button) findViewById(R.id.button_home_cerrar_sesion);
        botonMarcarTodo = (Button) findViewById(R.id.button_home_marcar_todo);
        botonBuscar = (Button) findViewById(R.id.button_home_buscar);
        botonAyuda = (Button) findViewById(R.id.button_home_ayuda);
        
        editBuscar = (EditText) findViewById(R.id.editText_home_palabra_clave);
        
        nombreMuseo =  (TextView) findViewById(R.id.home_nombre_museo);
        
        intentCaptura = getIntent();
        
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(R.drawable.emo_im_winking);
    	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
    	   }
    	});
        
        botonAyuda.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDialog.setTitle(getResources().getString(R.string.titulo_dialogo_ayuda));
				alertDialog.setMessage(getResources().getString(R.string.mensaje_ayuda_home));
				alertDialog.show();
			}
		});
        
        botonExpos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				verExposDelDia();
			}
		});
        
        botonInfoMuseo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				verInfoMuseo();
			}
		});
        
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cerrarSesion();
			}
		});
        
        botonMarcarTodo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				marcarDesmarcarCategorias();
			}
		});
        
        botonBuscar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buscar();
			}
		});
        
        obtenerNombreMuseo();
        poblarCategorias();
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
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
    
    
    private void verExposDelDia(){
    	//Redirige a Exposiciones del dia
		Intent expos = new Intent(this, ExposicionesActivity.class);
		startActivity(expos);
    }
    
    private void verInfoMuseo(){
    	//Redirige a Informacion del museo
		Intent info = new Intent(this, InfoMuseoActivity.class);
		startActivity(info);
    }
    
    private void cerrarSesion(){
    	//Redirige a Login
		Intent cerrar = new Intent(this, Login.class);
		cerrar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(cerrar);
		finish();
    }
    
    private void marcarDesmarcarCategorias(){
		if(getNumChecksOn() == ja.length() ){
			for (int i = 0; i < ja.length(); i++) {
				CheckBox cb = (CheckBox) findViewById(i);
				cb.setChecked(false);
			}
		}
		else{
			for (int i = 0; i < ja.length(); i++) {
				CheckBox cb = (CheckBox) findViewById(i);
				cb.setChecked(true);
			}
		}    	
    }
    
    private String obtenerCategorias(int numChecksOn){
    	String categorias = "(";
    	int iterador = 0;
    	int contador = 0;
    	while ( iterador < ja.length() && ( contador < numChecksOn ) ){
    		CheckBox cb = (CheckBox) findViewById(iterador);
			if(cb.isChecked()){
				String temp = cb.getText().toString();
				//Se debe quitar el texto con el numero de elementos
				int pos = temp.lastIndexOf('(') - 1;
				temp = temp.substring(0, pos);
				
				//Se cambian los espacios en blanco por %20
				if (temp.contains(" ")){
					temp = temp.replace(" ", "%20");
				}
				
				categorias += "'" + temp + "',";
				contador++;
			}
			iterador++;
    	}
    	//Quita la ultima coma
    	categorias = categorias.substring(0, categorias.length()-1);
    	categorias += ")";
    	//Toast.makeText(this, categorias, Toast.LENGTH_LONG).show();
    	return categorias;
    }
    
    private void buscar(){
    	
    	String palabraClave = editBuscar.getText().toString();
    	palabraClave = palabraClave.replace(" ", "%20");
    	
    	//Toast.makeText(this, palabraClave, Toast.LENGTH_LONG).show();
    	
    	int numChecksOn = getNumChecksOn();
    	if ( ( palabraClave.equalsIgnoreCase("") ) && ( numChecksOn == 0 ) ){
    		Toast.makeText(this, "Por favor escriba una palabra clave y/o seleccione\nuna de las categorías disponibles", Toast.LENGTH_LONG).show();
    	}
    	else {
    		Intent buscar = new Intent(this, ResultadosActivity.class);
			String categorias = "";
			
			String url = getString(R.string.url_servidor);
			
			
    		if ( ( !palabraClave.equalsIgnoreCase("") ) && ( numChecksOn > 0 ) ){
    			url += getString(R.string.nombre_php_resultados_palabra_clave_y_categorias);
    			url += "?palabraClave=" + palabraClave;
    			
    			//Se agregan las palabras clave de la consulta anterior en caso de que se trate de una
    			//consulta anidada
    			if(Aplicacion.isConsultaAnidada){
    				String palabras_clave_anteriores = intentCaptura.getStringExtra("palabras_clave");
    				if(!palabras_clave_anteriores.equalsIgnoreCase("")){
    					url += " " + palabras_clave_anteriores;
    					
    					//Se actualiza palabraClave para pasar los datos mas nuevos en el Intent
    					palabraClave += " " + palabras_clave_anteriores;
    				}
    			}
    			
    			categorias = obtenerCategorias(numChecksOn);
    			url += "&categorias=" + categorias;
    		}
    		else {
    			if ( !palabraClave.equalsIgnoreCase("") ) {
    				url += getString(R.string.nombre_php_resultados_palabra_clave);
    				url += "?palabraClave=" + palabraClave;
    				
    				//Se agregan las palabras clave de la consulta anterior en caso de que se trate de una
        			//consulta anidada
        			if(Aplicacion.isConsultaAnidada){
        				String palabras_clave_anteriores = intentCaptura.getStringExtra("palabras_clave");
        				if(!palabras_clave_anteriores.equalsIgnoreCase("")){
        					url += " " + palabras_clave_anteriores;
        					
        					//Se actualiza palabraClave para pasar los datos mas nuevos en el Intent
        					palabraClave += " " + palabras_clave_anteriores;
        				}
        			}
    			}
    			else {
    				
    				
    				//Se agregan las palabras clave de la consulta anterior en caso de que se trate de una
        			//consulta anidada
        			if(Aplicacion.isConsultaAnidada){
        				String palabras_clave_anteriores = intentCaptura.getStringExtra("palabras_clave");
        				if(!palabras_clave_anteriores.equalsIgnoreCase("")){
        					url += getString(R.string.nombre_php_resultados_palabra_clave_y_categorias);
        					url += "?palabraClave=" + palabras_clave_anteriores;
        					
        					//Se actualiza palabraClave para pasar los datos mas nuevos en el Intent
        					palabraClave = palabras_clave_anteriores;
        					
        					categorias = obtenerCategorias(numChecksOn);
        	    			url += "&categorias=" + categorias;
        	    			
        				}
        				else {
        					url += getString(R.string.nombre_php_resultados_categorias);
            				categorias = obtenerCategorias(numChecksOn);
                			url += "?categorias=" + categorias;
        				}
        			}
        			else {
        				url += getString(R.string.nombre_php_resultados_categorias);
        				categorias = obtenerCategorias(numChecksOn);
            			url += "?categorias=" + categorias;
        			}
    				
    				
    			}
    		}
    		url = url.replace(" ", "%20");
    		buscar.putExtra("url", url);
    		buscar.putExtra("categorias", categorias);
    		buscar.putExtra("palabraClave", palabraClave);
    		startActivity(buscar);
    	}
    }
    
    private void poblarCategorias(){
    	String url = getString(R.string.url_servidor);
    	
    	if (Aplicacion.isConsultaAnidada){
    		url += getString(R.string.nombre_php_categorias_parciales) + "?categorias=";
    		String categorias = intentCaptura.getStringExtra("categorias_elegidas");
    		if (categorias.equalsIgnoreCase("")){
    			url += "('')";
    		}
    		else {
    			url += categorias;
    		}
    	}
    	else {
    		url += getString(R.string.nombre_php_categorias);
    	}
    	
    	
    	
    	String line;
    	
    	try {
       	    URL conexion = new URL(url);
	        URLConnection tc = conexion.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream(),"UTF-8"));
	        
	        String categoria;
	        
	        while ((line = in.readLine()) != null) {
	        	
	        	// En caso de que no se hayan seleccionado categorias en la busqueda anterior
 	 		    if (line.equalsIgnoreCase("null")){
 	 		    	/*Se agregan un row*/
 	            	TableRow tr = new TableRow(this);
 	            	tr.setBackgroundResource(R.drawable.border_tabla_result);
 	            	
 	            	tl = (TableLayout) findViewById(R.id.tabla_categorias);
 	            	tl.setStretchAllColumns(true);
 	            	tl.addView(tr);
 		            
 		            TextView aviso = new TextView(this);
 		            aviso.setText(getResources().getString(R.string.advertencia_no_se_seleccionaron_categorias));
 		            aviso.setTextColor( getResources().getColor(R.color.azul_claro));
 		            aviso.setBackgroundResource(R.drawable.border_impar);
 		            aviso.setGravity(Gravity.CENTER);
 		            aviso.setMovementMethod(new ScrollingMovementMethod());
 		            aviso.setHorizontallyScrolling(false);
 		            TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
	            	//trlp.span = 2;
 		            tr.addView(aviso, trlp);
 			    }
 	 		    else {
		            ja = new JSONArray(line);
		            for (int i = 0; i < ja.length(); i++) {
		            	
		                JSONObject jo = (JSONObject) ja.get(i);
		                categoria = jo.getString("categoria");
		                categoria = corrector.reemplazar(categoria);
		                categoria+= " ("+jo.getString("cantidad")+")";
		                
		                if (i % 2 == 0){
		                	tl= (TableLayout) findViewById(R.id.tabla_categorias);
		                	tl.setStretchAllColumns(true);
		                	TableRow tr = new TableRow(this);
		                	tr.setId(i+2*ja.length());
		                	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		                	tl.addView(tr, trlp);
		                }
		                
		                RelativeLayout celda = new RelativeLayout(this);
		                celda.setId(i+ja.length());
		                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		                
		                CheckBox nombreCateg = new CheckBox(this);
		                nombreCateg.setText(categoria);
		                nombreCateg.setId(i);
		                nombreCateg.setTextColor( getResources().getColor(R.color.naranja));
		                if(Aplicacion.isConsultaAnidada){
		                	nombreCateg.setChecked(true);
		                }
		                RelativeLayout.LayoutParams lpTexto = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		                lpTexto.addRule(RelativeLayout.BELOW, i);
		                lpTexto.addRule(RelativeLayout.ALIGN_LEFT, i);
		                celda.addView(nombreCateg, lpTexto);
		                
		                int idRow = i;
		                if (idRow % 2 == 0){
		                	idRow = idRow+2*ja.length();
		                }
		                else {
		                	idRow = idRow-1+2*ja.length();
		                }
		                TableRow row = (TableRow) findViewById(idRow);
		                row.addView(celda);
		            }
	        	}
	        }
        }
        catch(Exception e){
    	    Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
        }
    }
    
    private int getNumChecksOn(){
    	int numCheckOn = 0;
    	try {
    		for(int i = 0; i < ja.length(); i++){
    			CheckBox cb = (CheckBox) findViewById(i);
    			if(cb.isChecked()){
    				numCheckOn++;
    			}
    		}
    	}
    	catch (Exception e){
    		Log.e("log_tag", "No se habia seleccionado ningun check en la busqueda anterior: " + e.toString(), e);
    	}
    	
    	return numCheckOn;
    }
    
    private void obtenerNombreMuseo(){
    	
    	String url = getString(R.string.url_servidor);
    	url += getString(R.string.nombre_php_info_museo);
    	
    	String line;
    	
    	try{
            URL conexion = new URL(url);
            URLConnection tc = conexion.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
               
	    while ((line = in.readLine()) != null) {
                JSONArray ja = new JSONArray(line);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    if(jo.getString("nombreMuseo") == null){
                    	nombreMuseo.setText("");
                    }else{
                       nombreMuseo.setText(jo.getString("nombreMuseo"));
                    }
                }
            }
        }
        catch(Exception e){
    	    Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
        }
	}
    
}
