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
import android.content.Context;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.museos.model.Acutes;

public class ResultadosActivity extends Activity {
	
	private Button botonFiltrarResultados, botonExpos, botonPDF, botonFormatoVisualizacion, botonNuevaBusqueda, botonCerrarSesion, botonAyuda;
	private String url, ids_seleccionados;
	private Acutes corrector = new Acutes();
	private String categorias_elegidas = "";
	private String palabras_clave = "";
	private AlertDialog alertDialog;
	private JSONArray ja;
	private int contador = 0;
	private int lista_articulos[];
	private boolean estado_articulo[];
	private int total_articulos;
	private int contador_id_row = 0;
	boolean bloquearBotonFiltrarResultados = false;
	
	int temp = 0;
	
	/*
	 * Primero asignar valor a contador
	 * 
	 * Luego insertar las flechas antes y despues dependiendo si el valor retornado es mayor que el valor de 
	 * items que se despliegan en una sola pantalla
	 * 
	 * Definir la funcion onClick de cada flecha
	 * 
	 * Actualizar el contador cada vez que se presiona una flecha
	 */

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        setContentView(R.layout.activity_resultados);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
        
        Intent intentCaptura = getIntent();
    	url = intentCaptura.getStringExtra("url");
    	categorias_elegidas = intentCaptura.getStringExtra("categorias");
    	palabras_clave = intentCaptura.getStringExtra("palabraClave");
    	bloquearBotonFiltrarResultados = intentCaptura.getBooleanExtra("bloquearBotonFiltrarResultados", false);
    	
    	//Toast.makeText(this, url, Toast.LENGTH_LONG).show();
        
    	botonFiltrarResultados = (Button) findViewById(R.id.button_result_filtrar);
    	botonExpos = (Button) findViewById(R.id.button_result_expo_del_dia);
    	botonPDF = (Button) findViewById(R.id.button_result_pdf);
    	botonFormatoVisualizacion = (Button) findViewById(R.id.button_result_formato);
    	botonNuevaBusqueda = (Button) findViewById(R.id.button_result_nueva_busqueda);
    	botonCerrarSesion = (Button) findViewById(R.id.button_result_cerrar_sesion);
    	botonAyuda = (Button) findViewById(R.id.button_resultados_ayuda);
    	
    	if (bloquearBotonFiltrarResultados){
    		botonFiltrarResultados.setEnabled(false);
    	}
        
    	alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(R.drawable.emo_im_winking);
    	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
    	   }
    	});
    	
    	botonAyuda.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDialog.setTitle(getResources().getString(R.string.titulo_dialogo_ayuda));
				alertDialog.setMessage(getResources().getString(R.string.mensaje_ayuda_resultados));
				alertDialog.show();
			}
		});
    	
    	botonFiltrarResultados.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Aplicacion.isConsultaAnidada = true;
				filtrarResultados();
				
			}
		});
    	
    	botonExpos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				verExposDelDia();
			}
		});
    	
    	botonPDF.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				activityPDF();
			}
		});
    	
    	botonFormatoVisualizacion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				verFormatosDisponibles();
			}
		});
    	
    	botonNuevaBusqueda.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Aplicacion.isConsultaAnidada = false;
				formularNuevaBusqueda();
			}
		});
    	
    	botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cerrarSesion();
			}
		});
        
        switch(Aplicacion.formatoResultados){
        case(1):
        	poblarResultadosFormatoTabla();
        	break;
        	
        case(2):
        	poblarResultadosScrollHorizontal();
        	break;
        
        case(3):
        	poblarResultadosCuadricula();
        	break;
        	
        default:
        	poblarResultadosFormatoTabla();
        	break;
        }
        
        //Toast.makeText(this, "Resultados recuperados: "+temp, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_resultados, menu);
        return true;
    }
    
    private void inicializar_lista_y_estado_articulos(int cantidad_articulos){
    	lista_articulos = new int[cantidad_articulos];
    	estado_articulo = new boolean[cantidad_articulos];
    	total_articulos = cantidad_articulos;
    	try{
    		for(int i = 0; i < cantidad_articulos; i++){
    			JSONObject jo = (JSONObject) ja.get(i);
        		lista_articulos[i] = Integer.parseInt(jo.getString("ID_Objeto"));
        		estado_articulo[i] = true;
    		}
    	}
    	catch (Exception e){
    		Log.e("log_tag", "Error al inicializar lista dearticulos: "+e.toString(), e);
    	}
    }
    
    private void modificarEstadoArticulo(int id_art, int total_arts){
    	int cont = 0;
    	boolean encontrado = false;
    	while ( (!encontrado) && (cont < total_arts)){
    		if(lista_articulos[cont] == id_art){
    			encontrado = true;
    			estado_articulo[cont] = !estado_articulo[cont];
    		}
    		cont++;
    	}
    }
    
    private boolean obtenerEstadoArticulo(int id_art, int total_arts){
    	int cont = 0;
    	boolean seleccionado = true;
    	boolean encontrado = false;
    	while ( (!encontrado) && (cont < total_arts)){
    		if(lista_articulos[cont] == id_art){
    			encontrado = true;
    			seleccionado = estado_articulo[cont];
    		}
    		cont++;
    	}
    	return seleccionado;
    }
    
    private void obtenerArticulosPDF(){
    	ids_seleccionados = "(";
    	for (int i = 0; i < total_articulos; i++){
    		if (estado_articulo[i]){
    			ids_seleccionados += lista_articulos[i] + ",";
    		}
    	}
    	ids_seleccionados = ids_seleccionados.substring(0, ids_seleccionados.length()-1);
    	ids_seleccionados += ")";
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
    
    private void cerrarSesion(){
    	//Redirige a Login
		Intent cerrar = new Intent(this, Login.class);
		cerrar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(cerrar);
		finish();
    }
    
    private void verExposDelDia(){
    	//Redirige a Exposiciones del dia
		Intent expos = new Intent(this, ExposicionesActivity.class);
		startActivity(expos);
    }
    
    private void verFormatosDisponibles(){
    	Intent formatos = new Intent(this, FormatosActivity.class);
    	formatos.putExtra("url", url);
		startActivity(formatos);
    }
    
    private void activityPDF(){
    	//Redirige a Generar PDF
    	obtenerArticulosPDF();
		Intent pdf = new Intent(this, GenerarPDFActivity.class);
		pdf.putExtra("ids_seleccionados", ids_seleccionados);
		startActivity(pdf);
    }
    
    private void filtrarResultados(){
    	Intent busqueda = new Intent(this, HomeActivity.class);
    	busqueda.putExtra("url", url);
    	busqueda.putExtra("categorias_elegidas", categorias_elegidas);
    	busqueda.putExtra("palabras_clave", palabras_clave);
		startActivity(busqueda);
    }
    
    private void formularNuevaBusqueda(){
    	Intent busqueda = new Intent(this, HomeActivity.class);
		startActivity(busqueda);
    }
    
    private int obtenerTope(){
    	int tope;
    	if(Aplicacion.resultados_por_pantalla <= ja.length()){
        	tope = Aplicacion.resultados_por_pantalla;
        }
        else {
        	tope = ja.length();
        }
    	return tope;
    }
    
    public void poblarResultadosFormatoTabla(){
    	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contenedor_resultados);
		ScrollView sv = new ScrollView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        sv.setBackgroundResource(R.drawable.border);
        //Faltan fading edge, fading edge length, orientation y maxheight 
		rl.addView(sv, lp);
		
		TableLayout tl = new TableLayout (this);
		tl.setStretchAllColumns(true);
		sv.addView(tl);
		
		TableRow fila = new TableRow(this);
		fila.setBackgroundResource(R.drawable.border_tabla_result);
		tl.addView(fila);
		
		TextView tv1 = new TextView(this);
		TextView tv2 = new TextView(this);
		TableRow.LayoutParams fila_lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		fila_lp.leftMargin = 0;
		fila_lp.rightMargin = 1;
		tv1.setBackgroundResource(R.drawable.border_elemento_resultados);
		tv2.setBackgroundResource(R.drawable.border_elemento_resultados);
		tv1.setTextColor( getResources().getColor(R.color.negro));
		tv2.setTextColor( getResources().getColor(R.color.negro));
		tv1.setText(getResources().getString(R.string.resultados_header_imagen));
		tv2.setText(getResources().getString(R.string.resultados_header_descripcion));
		tv1.setGravity(Gravity.CENTER);
		tv2.setGravity(Gravity.CENTER);
		
		fila.addView(tv1, fila_lp);
		fila.addView(tv2, fila_lp);
		
		//Se agregan los resultados
		String line;
    	try{
		    URL conexion = new URL(url);
		    URLConnection tc = conexion.openConnection();
		    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
		    
		    String url2 = getString(R.string.url_servidor);
		    url2 += getString(R.string.carpeta_thumbs)+ "/";
		    String id;
		    String descripcion;
		    int id_articulo;
		    
		    
		    
		    while ((line = in.readLine()) != null) {
		    	
		    	//En caso de que no se recuperen elementos se indica esto al usuario
 	 		    if (line.equalsIgnoreCase("null")){
 	 		    	/*Se agregan un row*/
 	            	TableRow tr = new TableRow(this);
 	            	tr.setBackgroundResource(R.drawable.border_tabla_result);
 	            	
 	            	
 	            	tl.addView(tr);
 		            
 		            TextView aviso = new TextView(this);
 		            aviso.setText(getResources().getString(R.string.advertencia_no_hay_resultados));
 		            aviso.setTextColor( getResources().getColor(R.color.azul_claro));
 		            aviso.setBackgroundResource(R.drawable.border_par);
 		            aviso.setGravity(Gravity.CENTER);
 		            aviso.setMovementMethod(new ScrollingMovementMethod());
 		            aviso.setHorizontallyScrolling(false);
 		            TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
	            	trlp.span = 2;
 		            tr.addView(aviso, trlp);
 		            
 		            //Se deshabilitan botones de filtrar resultados y de generar PDF
 		            botonFiltrarResultados.setEnabled(false);
 		            botonPDF.setEnabled(false);
 		            
 			    }
 	 		    
 	 		    
		        ja = new JSONArray(line);
		        
		        inicializar_lista_y_estado_articulos(ja.length());

		        temp = ja.length();
		        //Toast.makeText(this, "Resultados obtenidos: " + ja.length(), Toast.LENGTH_LONG).show();
		        
		        int tope = obtenerTope();
		        
		        for (int i = 0; i < tope; i++) {
		        	
		            JSONObject jo = (JSONObject) ja.get(i);
		            
		            id = jo.getString("ID_Thumb") + ".png";
		            id_articulo = Integer.parseInt(jo.getString("ID_Objeto"));
		            descripcion = corrector.reemplazar(jo.getString("Descripcion"));
		            
		            
		            
		            View v = new ImageView(getBaseContext());
		            ImageView image; 
		            image = new ImageView(v.getContext()); 
		            
		            
		            image.setImageDrawable(obtenerImagenDeURL(url2+id, "src name"));
		            v.setVisibility(0);
		            image.setVisibility(0);
		            
		            //Toast.makeText(this, url2+id, Toast.LENGTH_LONG).show();
		            
		            //AGREGA ITEMS AL TABLE LAYOUT/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	            	//Se crea un nuevo table row y se agrega a la tabla
	            	
	            	TableRow tr = new TableRow(this);
	            	
	            	tr.setBackgroundResource(R.drawable.border_tabla_result);
	            	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
	            	tl.addView(tr, trlp);

	            	RelativeLayout celda1 = new RelativeLayout(this);
		            
		            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		            lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		            lp1.addRule(RelativeLayout.CENTER_VERTICAL);
		            
		            //Agrega la imagen al table row
		            celda1.addView(image, lp1);
		            
		            
		            
		            //Crea el checkbox del elemento
		            CheckBox cb = new CheckBox (this);
		            cb.setChecked(true);
		            final int total = total_articulos;
		            final int ident = id_articulo;
		            cb.setOnClickListener(new View.OnClickListener() {
		    			public void onClick(View v) {
		    				modificarEstadoArticulo(ident, total);
		    			}
		    		});
		            
		            RelativeLayout.LayoutParams lp_cb = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		            lp_cb.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		            lp_cb.addRule(RelativeLayout.CENTER_VERTICAL);
		            celda1.addView(cb, lp_cb);
		            //Termina de agregar el checkbox
		            
		            
		            
		            
		            tr.addView(celda1);

		            //Se agrega la decripcion del articulo a un textView
		            TextView tv_descripcion = new TextView(this);
		            tv_descripcion.setText(descripcion);
		            tv_descripcion.setTextColor( getResources().getColor(R.color.azul_claro));
		            tv_descripcion.setGravity(Gravity.CENTER);            
		            tv_descripcion.setHorizontallyScrolling(false);
		            tv_descripcion.setMaxWidth(50);

		            tr.addView(tv_descripcion);

	                final int idElemento = id_articulo;
	                tr.setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				pruebaClickResultado(idElemento);
	        				obtenerMultiMedia(idElemento);
	        			}
	        		});
		        }
		        
		        //Se decide si se debe incluir la flecha para desplazar resultados hacia adelante
		        if (ja.length() > Aplicacion.resultados_por_pantalla){
		        	//Se debe colocar la flecha
		        	
		        	//Se crea el imageView Respectivo
		        	View v = new ImageView(getBaseContext());
		            ImageView image; 
		            image = new ImageView(v.getContext()); 
		            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_abajo));
		            v.setVisibility(0);
		            image.setVisibility(0);
		            
		            
		            //Se crea un RL contenedor y se agrega la imagen a este
		            RelativeLayout contenedor_flecha = new RelativeLayout(this);
		            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
		            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
		            contenedor_flecha.addView(image, lp_flecha);
		            
		            TableRow trIconDown = new TableRow(this);
 			        trIconDown.setBackgroundResource(R.drawable.fondo_flecha);
 			    	
 			    	TableRow.LayoutParams trlp_flecha = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
 			    	trlp_flecha.span = 2;
 			    	trIconDown.addView(contenedor_flecha, trlp_flecha);
 			    	tl.addView(trIconDown);
 			    	
		            //Se agrega un onclickListener a la flecha
		            final boolean adelante = true;
		            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
		    			public void onClick(View v) {
		    				cargarNuevosResultadosDefault(adelante);
		    				
		    			}
		    		});
		            
		        }
		    }
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
		
    }
    
    
    
    private void cargarNuevosResultadosDefault(boolean adelante){
    	//Se quitan los resultados previos
    	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contenedor_resultados);
    	rl.removeAllViews();
    	
    	//Se agregan los headers
		ScrollView sv = new ScrollView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        sv.setBackgroundResource(R.drawable.border);
        //Faltan fading edge, fading edge length, orientation y maxheight 
		rl.addView(sv, lp);
		
		TableLayout tl = new TableLayout (this);
		tl.setStretchAllColumns(true);
		sv.addView(tl);
		
		TableRow fila = new TableRow(this);
		fila.setBackgroundResource(R.drawable.border_tabla_result);
		tl.addView(fila);
		
		TextView tv1 = new TextView(this);
		TextView tv2 = new TextView(this);
		TableRow.LayoutParams fila_lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		fila_lp.leftMargin = 0;
		fila_lp.rightMargin = 1;
		tv1.setBackgroundResource(R.drawable.border_elemento_resultados);
		tv2.setBackgroundResource(R.drawable.border_elemento_resultados);
		tv1.setTextColor( getResources().getColor(R.color.negro));
		tv2.setTextColor( getResources().getColor(R.color.negro));
		tv1.setText(getResources().getString(R.string.resultados_header_imagen));
		tv2.setText(getResources().getString(R.string.resultados_header_descripcion));
		tv1.setGravity(Gravity.CENTER);
		tv2.setGravity(Gravity.CENTER);
		
		fila.addView(tv1, fila_lp);
		fila.addView(tv2, fila_lp);
		
    	//Se modifica el contador
    	if(adelante){
    		contador += Aplicacion.resultados_por_pantalla;
    	}
    	else {
    		contador -= Aplicacion.resultados_por_pantalla;
    	}
    	
    	//Se decide si se debe colocar flecha al inicio
    	if (contador >= Aplicacion.resultados_por_pantalla){
    		//Se debe agregar la flecha
    		
        	//Se crea el imageView Respectivo
        	View v = new ImageView(getBaseContext());
            ImageView image; 
            image = new ImageView(v.getContext()); 
            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_arriba));
            v.setVisibility(0);
            image.setVisibility(0);
            
            //Se crea un RL contenedor y se agrega la imagen a este
            RelativeLayout contenedor_flecha = new RelativeLayout(this);
            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
            contenedor_flecha.addView(image, lp_flecha);
            
            TableRow trIconUp = new TableRow(this);
            trIconUp.setBackgroundResource(R.drawable.fondo_flecha);
            
            TableRow.LayoutParams trlp_flecha = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
		    trlp_flecha.span = 2;
		    trIconUp.addView(contenedor_flecha, trlp_flecha);
		    tl.addView(trIconUp);
	    	
            
            //Se agrega un onclickListener a la flecha
            final boolean hacia_adelante = false;
            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				cargarNuevosResultadosDefault(hacia_adelante);
    				
    			}
    		});
    		
    	}
    	
    	
    	//Se agregan todos los elementos que corresponden entre el rango establecido
    	
    	try{
			String url2 = getString(R.string.url_servidor);
			url2 += getString(R.string.carpeta_thumbs)+ "/";
	        String id, descripcion;
	        int id_articulo;
	        
	        for (int i = contador; i < contador + Aplicacion.resultados_por_pantalla; i++) {
	        	
	            JSONObject jo = (JSONObject) ja.get(i);
	            
	            id = jo.getString("ID_Thumb") + ".png";
	            id_articulo = Integer.parseInt(jo.getString("ID_Objeto"));
	            descripcion = corrector.reemplazar(jo.getString("Descripcion"));

	            View v = new ImageView(getBaseContext());
	            ImageView image; 
	            image = new ImageView(v.getContext()); 
	            
	            
	            image.setImageDrawable(obtenerImagenDeURL(url2+id, "src name"));
	            v.setVisibility(0);
	            image.setVisibility(0);
	            
	            //Toast.makeText(this, url2+id, Toast.LENGTH_LONG).show();
	            
	            //AGREGA ITEMS AL TABLE LAYOUT/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            	//Se crea un nuevo table row y se agrega a la tabla
            	
            	TableRow tr = new TableRow(this);
            	
            	tr.setBackgroundResource(R.drawable.border_tabla_result);
            	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            	tl.addView(tr, trlp);

            	RelativeLayout celda1 = new RelativeLayout(this);
	            
	            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	            lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
	            lp1.addRule(RelativeLayout.CENTER_VERTICAL);
	            
	            //Agrega la imagen al table row
	            celda1.addView(image, lp1);
	            
	            
	            //Crea el checkbox del elemento
	            CheckBox cb = new CheckBox (this);
	            if (obtenerEstadoArticulo(id_articulo, total_articulos)){
	            	cb.setChecked(true);
	            }
	            final int total = total_articulos;
	            final int ident = id_articulo;
	            cb.setOnClickListener(new View.OnClickListener() {
	    			public void onClick(View v) {
	    				modificarEstadoArticulo(ident, total);
	    			}
	    		});
	            
	            RelativeLayout.LayoutParams lp_cb = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	            lp_cb.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	            lp_cb.addRule(RelativeLayout.CENTER_VERTICAL);
	            celda1.addView(cb, lp_cb);
	            //Termina de agregar el checkbox
	            
	            tr.addView(celda1);

	            //Se agrega la decripcion del articulo a un textView
	            TextView tv_descripcion = new TextView(this);
	            tv_descripcion.setText(descripcion);
	            tv_descripcion.setTextColor( getResources().getColor(R.color.azul_claro));
	            tv_descripcion.setGravity(Gravity.CENTER);            
	            tv_descripcion.setHorizontallyScrolling(false);
	            tv_descripcion.setMaxWidth(50);

	            tr.addView(tv_descripcion);

                final int idElemento = id_articulo;
                tr.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        				pruebaClickResultado(idElemento);
        				obtenerMultiMedia(idElemento);
        			}
        		});
	        }
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
    	
    	//Se decide si se debe incluir la flecha para desplazar resultados hacia adelante
        if (ja.length() > contador + Aplicacion.resultados_por_pantalla){
        	//Se debe colocar la flecha
        	
        	//Se crea el imageView Respectivo
        	View v = new ImageView(getBaseContext());
            ImageView image; 
            image = new ImageView(v.getContext()); 
            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_abajo));
            v.setVisibility(0);
            image.setVisibility(0);
            
            //Se crea un RL contenedor y se agrega la imagen a este
            RelativeLayout contenedor_flecha = new RelativeLayout(this);
            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
            contenedor_flecha.addView(image, lp_flecha);
            
            TableRow trIconDown = new TableRow(this);
	        trIconDown.setBackgroundResource(R.drawable.fondo_flecha);
	    	
	    	TableRow.LayoutParams trlp_flecha = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
	    	trlp_flecha.span = 2;
	    	trIconDown.addView(contenedor_flecha, trlp_flecha);
	    	tl.addView(trIconDown);
	    	
            //Se agrega un onclickListener a la flecha
            final boolean hacia_adelante = true;
            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				cargarNuevosResultadosDefault(hacia_adelante);
    			}
    		});
        }
    }
    
    public void poblarResultadosScrollHorizontal(){
    	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contenedor_resultados);
    	
    	HorizontalScrollView hsv = new HorizontalScrollView(this);
    	
    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //lp.addRule(RelativeLayout.CENTER_VERTICAL);
        //lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        hsv.setBackgroundResource(R.drawable.border);
        //Faltan fading edge, fading edge length, orientation y maxheight 
		rl.addView(hsv, lp);
		
		LinearLayout ll = new LinearLayout(this);
		hsv.addView(ll);

		/////////////////////////////////////////////
		//Se agregan los resultados

    	String line;
    	try{
		    URL conexion = new URL(url);
		    URLConnection tc = conexion.openConnection();
		    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
		    
		    String url2 = getString(R.string.url_servidor);
		    url2 += getString(R.string.carpeta_thumbs)+ "/";
		    String id;
		    String descripcion;
		    int id_articulo;
		    
		    while ((line = in.readLine()) != null) {
		    	
		    	//En caso de que no se recuperen elementos se indica esto al usuario
 	 		    if (line.equalsIgnoreCase("null")){ 	 		    	
 		            rl.removeAllViews();
 		            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
 		            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
 		            TextView aviso = new TextView(this);
		            aviso.setText(getResources().getString(R.string.advertencia_no_hay_resultados));
		            aviso.setTextColor( getResources().getColor(R.color.azul_claro));
		            aviso.setBackgroundResource(R.drawable.border_par);
		            aviso.setGravity(Gravity.CENTER);
 		            rl.addView(aviso, lp2);
 		            
 		            //Se deshabilitan botones de filtrar resultados y de generar PDF
 		            botonFiltrarResultados.setEnabled(false);
 		            botonPDF.setEnabled(false);
 			    }
		    	
		        ja = new JSONArray(line);
		        
		        inicializar_lista_y_estado_articulos(ja.length());
		        
		        temp = ja.length();
		        //Toast.makeText(this, "Resultados obtenidos: " + ja.length(), Toast.LENGTH_LONG).show();
		        
		        int tope = obtenerTope();
		        
		        for (int i = 0; i < tope; i++) {
		        	
		            JSONObject jo = (JSONObject) ja.get(i);
		            
		            id = jo.getString("ID_Thumb") + ".png";
		            id_articulo = Integer.parseInt(jo.getString("ID_Objeto"));
		            descripcion = corrector.reemplazar(jo.getString("Descripcion"));
		            
		            
		            
		            
		            View v = new ImageView(getBaseContext());
		            ImageView image; 
		            image = new ImageView(v.getContext()); 
		            image.setId(i);
		            
		            image.setImageDrawable(obtenerImagenDeURL(url2+id, "src name"));
		            v.setVisibility(0);
		            image.setVisibility(0);
		            
		            
		            //Toast.makeText(this, url2+id, Toast.LENGTH_LONG).show();
		            
		            //AGREGA ITEMS AL LL DEL SCROLL VIEW/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		            //Se crea un nuevo RL y se agrega al HSV
		            RelativeLayout contenedor = new RelativeLayout(this);
	            	contenedor.setId(i+2*ja.length());
	            	
	            	//Prueba para ver limite de cada RL
	            	if (i%2 == 0) {
	            		contenedor.setBackgroundResource(R.drawable.border_par);
	            	}
	            	else {
	            		contenedor.setBackgroundResource(R.drawable.border_impar);
	            	}
	            	
		            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		            lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		            lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		            
		            //Agrega la imagen al RL
		            contenedor.addView(image, lp1);
		            
		            //Se agrega la decripcion del articulo a un textView
		            TextView tv_descripcion = new TextView(this);
		            tv_descripcion.setText(descripcion);
		            tv_descripcion.setTextColor( getResources().getColor(R.color.azul_claro));
		            tv_descripcion.setBackgroundResource(R.drawable.border);
		            //tv_descripcion.setGravity(Gravity.CENTER);
		            tv_descripcion.setId(i+3*ja.length());
		            //tv_descripcion.setHorizontallyScrolling(false);
		            tv_descripcion.setMaxWidth(300);
		            
		            
		            //Se crea un LP para describir como almacenar la descripcion del articulo
		            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		            
		            lp2.addRule(RelativeLayout.BELOW, i);
		            if (i==0){
		            	lp2.topMargin = obtenerImagenDeURL(url2+id, "src name").getIntrinsicHeight() + 20;
		            }
		            else{
		            	lp2.topMargin = 20;
		            }
		            
		            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		            //lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		            //lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
		            
		            contenedor.addView(tv_descripcion, lp2);
		            
		            //Crea el checkbox del elemento
		            CheckBox cb = new CheckBox (this);
		            cb.setChecked(true);
		            final int total = total_articulos;
		            final int ident = id_articulo;
		            cb.setOnClickListener(new View.OnClickListener() {
		    			public void onClick(View v) {
		    				modificarEstadoArticulo(ident, total);
		    			}
		    		});
		            
		            RelativeLayout.LayoutParams lp_cb = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		            lp_cb.addRule(RelativeLayout.CENTER_HORIZONTAL);
		            lp_cb.addRule(RelativeLayout.CENTER_IN_PARENT);
		            cb.setGravity(Gravity.CENTER);
		            lp_cb.addRule(RelativeLayout.BELOW, i+3*ja.length());
		            contenedor.addView(cb, lp_cb);
		            //Termina de agregar el checkbox
		            
		            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		            
		            Context context = this;
		            WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		            lp3.width = m.getDefaultDisplay().getWidth();
		            
		            ll.addView(contenedor, lp3);
		            //Toast.makeText(this, tv_descripcion.getText(), Toast.LENGTH_LONG).show();
		            
		            
	                final int bla = id_articulo;
	                contenedor.setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				pruebaClickResultado(bla);
	        				obtenerMultiMedia(bla);
	        			}
	        		});
		        }
		        
		        
		        //Se decide si se debe incluir la flecha para desplazar resultados hacia adelante
		        if (ja.length() > Aplicacion.resultados_por_pantalla){
		        	//Se debe colocar la flecha
		        	//Se crea el imageView Respectivo
		        	View v = new ImageView(getBaseContext());
		            ImageView image; 
		            image = new ImageView(v.getContext()); 
		            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_derecha));
		            v.setVisibility(0);
		            image.setVisibility(0);
		            
		            //Se crea un RL contenedor y se agrega la imagen a este
		            RelativeLayout contenedor_flecha = new RelativeLayout(this);
		            contenedor_flecha.setBackgroundResource(R.drawable.fondo_flecha);
		            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
		            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
		            contenedor_flecha.addView(image, lp_flecha);
		            
		            //Se agrega el RL al LL que contiene los demas elementos
		            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		            ll.addView(contenedor_flecha, lp3);
		            
		            //Se agrega un onclickListener a la flecha
		            final boolean adelante = true;
		            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
		    			public void onClick(View v) {
		    				cargarNuevosResultadosHorizontalScroll(adelante);
		    				
		    			}
		    		});
		        }
		    }
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
    }
    
    private void cargarNuevosResultadosHorizontalScroll(boolean adelante){
    	//Se quitan los resultados previos
    	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contenedor_resultados);
    	rl.removeAllViews();
    	HorizontalScrollView hsv = new HorizontalScrollView(this);
    	
    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        hsv.setBackgroundResource(R.drawable.border);
		rl.addView(hsv, lp);
		
		LinearLayout ll = new LinearLayout(this);
		hsv.addView(ll);
    	
    	//Se modifica el contador
    	if(adelante){
    		contador += Aplicacion.resultados_por_pantalla;
    	}
    	else {
    		contador -= Aplicacion.resultados_por_pantalla;
    	}
    	
    	//Se decide si se debe colocar flecha al inicio
    	if (contador >= Aplicacion.resultados_por_pantalla){
    		//Se debe agregar la flecha
    		
        	//Se crea el imageView Respectivo
        	View v = new ImageView(getBaseContext());
            ImageView image; 
            image = new ImageView(v.getContext()); 
            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_izquierda));
            v.setVisibility(0);
            image.setVisibility(0);
            
            //Se crea un RL contenedor y se agrega la imagen a este
            RelativeLayout contenedor_flecha = new RelativeLayout(this);
            contenedor_flecha.setBackgroundResource(R.drawable.fondo_flecha);
            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
            contenedor_flecha.addView(image, lp_flecha);
            
            //Se agrega el RL al LL que contiene los demas elementos
            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            ll.addView(contenedor_flecha, lp3);
            
            //Se agrega un onclickListener a la flecha
            final boolean hacia_adelante = false;
            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				cargarNuevosResultadosHorizontalScroll(hacia_adelante);
    				
    			}
    		});
    		
    	}
    	
    	
    	//Se agregan todos los elementos que corresponden entre el rango establecido
    	
    	try{
			String url2 = getString(R.string.url_servidor);
			url2 += getString(R.string.carpeta_thumbs)+ "/";
	        String id, descripcion;
	        int id_articulo;
	        
	        for (int i = contador; i < contador + Aplicacion.resultados_por_pantalla; i++) {
	        	
	            JSONObject jo = (JSONObject) ja.get(i);
	            
	            id = jo.getString("ID_Thumb") + ".png";
	            id_articulo = Integer.parseInt(jo.getString("ID_Objeto"));
	            descripcion = corrector.reemplazar(jo.getString("Descripcion"));

	            View v = new ImageView(getBaseContext());
	            ImageView image; 
	            image = new ImageView(v.getContext()); 
	            image.setId(i);
	            
	            image.setImageDrawable(obtenerImagenDeURL(url2+id, "src name"));
	            v.setVisibility(0);
	            image.setVisibility(0);
	            
	            
	            //Toast.makeText(this, url2+id, Toast.LENGTH_LONG).show();
	            
	            //AGREGA ITEMS AL LL DEL SCROLL VIEW/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	            //Se crea un nuevo RL y se agrega al HSV
	            RelativeLayout contenedor = new RelativeLayout(this);
            	contenedor.setId(i+2*ja.length());
            	
            	//Prueba para ver limite de cada RL
            	if (i%2 == 0) {
            		contenedor.setBackgroundResource(R.drawable.border_par);
            	}
            	else {
            		contenedor.setBackgroundResource(R.drawable.border_impar);
            	}
            	
	            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	            lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
	            lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	            
	            //Agrega la imagen al RL
	            contenedor.addView(image, lp1);
	            
	            //Se agrega la decripcion del articulo a un textView
	            TextView tv_descripcion = new TextView(this);
	            tv_descripcion.setText(descripcion);
	            tv_descripcion.setTextColor( getResources().getColor(R.color.azul_claro));
	            tv_descripcion.setBackgroundResource(R.drawable.border);
	            //tv_descripcion.setGravity(Gravity.CENTER);
	            tv_descripcion.setId(i+3*ja.length());
	            //tv_descripcion.setHorizontallyScrolling(false);
	            tv_descripcion.setMaxWidth(300);
	            
	            
	            //Se crea un LP para describir como almacenar la descripcion del articulo
	            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	            
	            lp2.addRule(RelativeLayout.BELOW, i);
	            if (i==0){
	            	lp2.topMargin = obtenerImagenDeURL(url2+id, "src name").getIntrinsicHeight() + 20;
	            }
	            else{
	            	lp2.topMargin = 20;
	            }
	            
	            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
	            //lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	            //lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
	            
	            contenedor.addView(tv_descripcion, lp2);
	            
	            //Crea el checkbox del elemento
	            CheckBox cb = new CheckBox (this);
	            if (obtenerEstadoArticulo(id_articulo, total_articulos)){
	            	cb.setChecked(true);
	            }
	            final int total = total_articulos;
	            final int ident = id_articulo;
	            cb.setOnClickListener(new View.OnClickListener() {
	    			public void onClick(View v) {
	    				modificarEstadoArticulo(ident, total);
	    			}
	    		});
	            
	            RelativeLayout.LayoutParams lp_cb = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	            lp_cb.addRule(RelativeLayout.CENTER_HORIZONTAL);
	            lp_cb.addRule(RelativeLayout.CENTER_IN_PARENT);
	            cb.setGravity(Gravity.CENTER);
	            lp_cb.addRule(RelativeLayout.BELOW, i+3*ja.length());
	            contenedor.addView(cb, lp_cb);
	            //Termina de agregar el checkbox
	            
	            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	            
	            Context context = this;
	            WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	            lp3.width = m.getDefaultDisplay().getWidth();
	            
	            ll.addView(contenedor, lp3);
	            //Toast.makeText(this, tv_descripcion.getText(), Toast.LENGTH_LONG).show();
	            
	            
                final int bla = id_articulo;
                contenedor.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        				pruebaClickResultado(bla);
        				obtenerMultiMedia(bla);
        			}
        		});
	        }
	        
	        
	        //Se decide si se debe incluir la flecha para desplazar resultados hacia adelante
	        if (ja.length() > contador + Aplicacion.resultados_por_pantalla){
	        	//Se debe colocar la flecha
	        	//Se crea el imageView Respectivo
	        	View v = new ImageView(getBaseContext());
	            ImageView image; 
	            image = new ImageView(v.getContext()); 
	            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_derecha));
	            v.setVisibility(0);
	            image.setVisibility(0);
	            
	            //Se crea un RL contenedor y se agrega la imagen a este
	            RelativeLayout contenedor_flecha = new RelativeLayout(this);
	            contenedor_flecha.setBackgroundResource(R.drawable.fondo_flecha);
	            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
	            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
	            contenedor_flecha.addView(image, lp_flecha);
	            
	            //Se agrega el RL al LL que contiene los demas elementos
	            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
	            ll.addView(contenedor_flecha, lp3);
	            
	            //Se agrega un onclickListener a la flecha
	            final boolean hacia_adelante = true;
	            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
	    			public void onClick(View v) {
	    				cargarNuevosResultadosHorizontalScroll(hacia_adelante);
	    				
	    			}
	    		});
	            
	        }
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
    }
    
    public void poblarResultadosCuadricula(){
    	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contenedor_resultados);
		ScrollView sv = new ScrollView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        sv.setBackgroundResource(R.drawable.border);
        //Faltan fading edge, fading edge length, orientation y maxheight 
		rl.addView(sv, lp);
		
		TableLayout tl = new TableLayout (this);
		tl.setStretchAllColumns(true);
		sv.addView(tl);

    	String line;
    	
    	try{
		    URL conexion = new URL(url);
		    URLConnection tc = conexion.openConnection();
		    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
		    
		    String url2 = getString(R.string.url_servidor);
		    url2 += getString(R.string.carpeta_thumbs)+ "/";
		    String id;
		    
		    
		    
		    
		    while ((line = in.readLine()) != null) {
		    	
		    	//En caso de que no se recuperen elementos se indica esto al usuario
 	 		    if (line.equalsIgnoreCase("null")){
 	 		    	/*Se agregan un row*/
 	            	TableRow tr = new TableRow(this);
 	            	tr.setBackgroundResource(R.drawable.border_tabla_result);
 	            	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
 	            	tl.addView(tr, trlp);
 		            
 		            TextView aviso = new TextView(this);
 		            aviso.setText(getResources().getString(R.string.advertencia_no_hay_resultados));
 		            aviso.setTextColor( getResources().getColor(R.color.azul_claro));
 		            aviso.setBackgroundResource(R.drawable.border);
 		            aviso.setGravity(Gravity.CENTER);
 		            aviso.setMovementMethod(new ScrollingMovementMethod());
 		            aviso.setHorizontallyScrolling(false);
 		            tr.addView(aviso);
 		            
 		            //Se deshabilitan botones de filtrar resultados y de generar PDF
 		            botonFiltrarResultados.setEnabled(false);
 		            botonPDF.setEnabled(false);
 			    }
 	 		    
	            ja = new JSONArray(line);
	            
	            inicializar_lista_y_estado_articulos(ja.length());
	            
	            temp = ja.length();
	            //Toast.makeText(this, "Resultados obtenidos: " + ja.length(), Toast.LENGTH_LONG).show();
	            
	            int tope = obtenerTope();
	            contador_id_row = 0;
	            
	            for (int i = 0; i < tope; i++) {
	                
	            	JSONObject jo = (JSONObject) ja.get(i); 
	            	
	            	id = jo.getString("ID_Thumb") + ".png";
		            int id_articulo = Integer.parseInt(jo.getString("ID_Objeto"));
		            
		            
		            //Toast.makeText(this, "id_articulo = " + id_articulo, Toast.LENGTH_LONG).show();
					View v = new ImageView(getBaseContext());
	                ImageView image; 
	                image = new ImageView(v.getContext()); 
	                
	                image.setImageDrawable(obtenerImagenDeURL(url2+id, "src name"));
	                v.setVisibility(0);
	                image.setVisibility(0);
	                
	                if (i % 3 == 0){
	                	TableRow tr = new TableRow(this);
	                	tr.setId(contador_id_row);
	                	contador_id_row++;
	                	tr.setBackgroundResource(R.color.negro);
	                	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
	                	tl.addView(tr, trlp);
	                }
	                
	                RelativeLayout celda = new RelativeLayout(this);
	                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	                lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
	                celda.setBackgroundResource(R.drawable.border_tabla_result);
	                celda.addView(image, lp2);
	                
	                //Crea el checkbox del elemento
		            CheckBox cb = new CheckBox (this);
		            cb.setChecked(true);
		            final int total = total_articulos;
		            final int ident = id_articulo;
		            cb.setOnClickListener(new View.OnClickListener() {
		    			public void onClick(View v) {
		    				modificarEstadoArticulo(ident, total);
		    			}
		    		});
		            
		            RelativeLayout.LayoutParams lp_cb = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		            lp_cb.addRule(RelativeLayout.CENTER_VERTICAL);
		            lp_cb.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		            celda.addView(cb, lp_cb);
		            //Termina de agregar el checkbox
	                
	                int idRow = contador_id_row-1;
	                
	                
	                TableRow row = (TableRow) findViewById(idRow);
	                row.addView(celda);
	                
	                final int idElement = id_articulo;  
	                celda.setOnClickListener(new View.OnClickListener() {
	        			public void onClick(View v) {
	        				pruebaClickResultado(idElement);
	        				obtenerMultiMedia(idElement);
	        			}
	        		});
	            }
	            
	            
	            ////////////////////////Relleno de cuadros faltantes
	            int idRow = contador_id_row-1;
                TableRow row = (TableRow) findViewById(idRow);
	            
                int elementos_agregados;
        		if ((contador + Aplicacion.resultados_por_pantalla) > ja.length()){
        			elementos_agregados = ja.length() - contador;
        		}
        		else {
        			elementos_agregados = Aplicacion.resultados_por_pantalla;
        		}
        		
        		if(elementos_agregados % 3 == 2){
        			TextView tv1 = new TextView(this);
        			tv1.setTextColor(getResources().getColor(R.color.azul_claro));
        			row.addView(tv1);
        		} 
        		else if (elementos_agregados % 3 == 1){
        			TextView tv1 = new TextView(this);
        			tv1.setTextColor(getResources().getColor(R.color.azul_claro));
        			row.addView(tv1);
        			TextView tv2 = new TextView(this);
        			tv2.setTextColor(getResources().getColor(R.color.azul_claro));
        			row.addView(tv2);
        		}
                
	            ///////////TERMINA RELLENO////////////////////////////////////////
	        }
		    
		    //Se decide si se debe incluir la flecha para desplazar resultados hacia adelante
	        if (ja.length() > Aplicacion.resultados_por_pantalla){
	        	//Se debe colocar la flecha
	        	//Se crea el imageView Respectivo
	        	View v = new ImageView(getBaseContext());
	            ImageView image; 
	            image = new ImageView(v.getContext()); 
	            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_abajo));
	            v.setVisibility(0);
	            image.setVisibility(0);
	            
	            
	            //Se crea un RL contenedor y se agrega la imagen a este
	            RelativeLayout contenedor_flecha = new RelativeLayout(this);
	            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
	            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
	            contenedor_flecha.addView(image, lp_flecha);
	            
	            TableRow trIconDown = new TableRow(this);
		        trIconDown.setBackgroundResource(R.drawable.fondo_flecha);
		        TableRow.LayoutParams trlp_flecha = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
			    trlp_flecha.span = 3;
		    	trIconDown.addView(contenedor_flecha, trlp_flecha);
		    	tl.addView(trIconDown);
		    	
	            //Se agrega un onclickListener a la flecha
	            final boolean adelante = true;
	            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
	    			public void onClick(View v) {
	    				cargarNuevosResultadosCuadricula(adelante);
	    				
	    			}
	    		});
	            
	        }
    	}
    	catch(Exception e){
    		Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
		}
    	
    }
    
    private void cargarNuevosResultadosCuadricula(boolean adelante){
    	//Se quitan los resultados previos
    	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contenedor_resultados);
    	rl.removeAllViews();
		ScrollView sv = new ScrollView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        sv.setBackgroundResource(R.drawable.border);
        //Faltan fading edge, fading edge length, orientation y maxheight 
		rl.addView(sv, lp);
		
		TableLayout tl = new TableLayout (this);
		tl.setStretchAllColumns(true);
		sv.addView(tl);

    	
    	//Se modifica el contador
    	if(adelante){
    		contador += Aplicacion.resultados_por_pantalla;
    	}
    	else {
    		contador -= Aplicacion.resultados_por_pantalla;
    	}
    	
    	//Se decide si se debe colocar flecha al inicio
    	if (contador >= Aplicacion.resultados_por_pantalla){
    		//Se debe agregar la flecha
    		
        	//Se crea el imageView Respectivo
        	View v = new ImageView(getBaseContext());
            ImageView image; 
            image = new ImageView(v.getContext()); 
            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_arriba));
            v.setVisibility(0);
            image.setVisibility(0);
            
            //Se crea un RL contenedor y se agrega la imagen a este
            RelativeLayout contenedor_flecha = new RelativeLayout(this);
            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
            contenedor_flecha.addView(image, lp_flecha);
            
            TableRow trIconUp = new TableRow(this);
            trIconUp.setBackgroundResource(R.drawable.fondo_flecha);
            TableRow.LayoutParams trlp_flecha = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
		    trlp_flecha.span = 3;
            trIconUp.addView(contenedor_flecha, trlp_flecha);
	    	tl.addView(trIconUp);
               
            //Se agrega un onclickListener a la flecha
            final boolean hacia_adelante = false;
            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				cargarNuevosResultadosCuadricula(hacia_adelante);
    				
    			}
    		});
    		
    	}
    	
    	
    	//Se agregan todos los elementos que corresponden entre el rango establecido
    	
    	try{
			String url2 = getString(R.string.url_servidor);
			url2 += getString(R.string.carpeta_thumbs)+ "/";
	        String id;
	        int id_articulo;
	        contador_id_row = 0;
	        //contador_id_row++;
	        
	        for (int i = contador; i < contador + Aplicacion.resultados_por_pantalla; i++) {
	        	
	            JSONObject jo = (JSONObject) ja.get(i);
	            
	            id = jo.getString("ID_Thumb") + ".png";
	            id_articulo = Integer.parseInt(jo.getString("ID_Objeto"));

	            View v = new ImageView(getBaseContext());
	            ImageView image; 
	            image = new ImageView(v.getContext()); 
	            
	            
	            image.setImageDrawable(obtenerImagenDeURL(url2+id, "src name"));
	            v.setVisibility(0);
	            image.setVisibility(0);
	            
	            if ( (i-contador) % 3 == 0){
                	TableRow tr = new TableRow(this);
                	tr.setId(contador_id_row);
                	contador_id_row++;
                	tr.setBackgroundResource(R.color.negro);
                	TableRow.LayoutParams trlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                	tl.addView(tr, trlp);
                }

               
                RelativeLayout celda = new RelativeLayout(this);
                
                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
                celda.setBackgroundResource(R.drawable.border_tabla_result);
                celda.addView(image, lp2);

                //Crea el checkbox del elemento
	            CheckBox cb = new CheckBox (this);
	            if (obtenerEstadoArticulo(id_articulo, total_articulos)){
	            	cb.setChecked(true);
	            }
	            final int total = total_articulos;
	            final int ident = id_articulo;
	            cb.setOnClickListener(new View.OnClickListener() {
	    			public void onClick(View v) {
	    				modificarEstadoArticulo(ident, total);
	    			}
	    		});
	            
	            RelativeLayout.LayoutParams lp_cb = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
	            lp_cb.addRule(RelativeLayout.CENTER_VERTICAL);
	            lp_cb.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	            celda.addView(cb, lp_cb);
	            //Termina de agregar el checkbox
                
                int idRow = contador_id_row - 1;
                
                
                TableRow row = (TableRow) findViewById(idRow);
                row.addView(celda);
                
                final int idElement = id_articulo;  
                celda.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        				pruebaClickResultado(idElement);
        				obtenerMultiMedia(idElement);
        			}
        		});
	        }
	        
			
    	}
    	catch(Exception e){
    		Log.e("log_tag", "contador_id_row: " + contador_id_row + " Error al pegarse a PHP: "+e.toString(), e);
		}
    	
		////////////////////////Relleno de cuadros faltantes
		        
		int idRow = contador_id_row - 1;
		
		TableRow row = (TableRow) findViewById(idRow);
		
		int elementos_agregados;
		if ((contador + Aplicacion.resultados_por_pantalla) > ja.length()){
			elementos_agregados = ja.length() - contador;
		}
		else {
			elementos_agregados = Aplicacion.resultados_por_pantalla;
		}
		
		if(elementos_agregados % 3 == 2){
			TextView tv1 = new TextView(this);
			tv1.setTextColor(getResources().getColor(R.color.azul_claro));
			row.addView(tv1);
		} 
		else if (elementos_agregados % 3 == 1){
			TextView tv1 = new TextView(this);
			tv1.setTextColor(getResources().getColor(R.color.azul_claro));
			row.addView(tv1);
			TextView tv2 = new TextView(this);
			tv2.setTextColor(getResources().getColor(R.color.azul_claro));
			row.addView(tv2);
		}
    	
    	//Se decide si se debe colocar flecha al final
    	if (ja.length() > contador + Aplicacion.resultados_por_pantalla){
    		//Se debe agregar la flecha
    		
        	//Se crea el imageView Respectivo
        	View v = new ImageView(getBaseContext());
            ImageView image; 
            image = new ImageView(v.getContext()); 
            image.setImageDrawable(getResources().getDrawable(R.drawable.flecha_abajo));
            v.setVisibility(0);
            image.setVisibility(0);
            
            RelativeLayout contenedor_flecha = new RelativeLayout(this);
            RelativeLayout.LayoutParams lp_flecha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_flecha.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp_flecha.addRule(RelativeLayout.CENTER_VERTICAL);
            contenedor_flecha.addView(image, lp_flecha);
            
            TableRow trIconDown = new TableRow(this);
            trIconDown.setBackgroundResource(R.drawable.fondo_flecha);
            TableRow.LayoutParams trlp_flecha = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
		    trlp_flecha.span = 3;
            trIconDown.addView(contenedor_flecha, trlp_flecha);
	    	tl.addView(trIconDown);
            
            
            //Se agrega un onclickListener a la flecha
            final boolean hacia_adelante = true;
            contenedor_flecha.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				cargarNuevosResultadosCuadricula(hacia_adelante);
    				
    			}
    		});
    		
    	}
    	
    }
    
    private void pruebaClickResultado(int id){
    	//Toast.makeText(this, "El id del articulo es: "+ id, Toast.LENGTH_LONG).show();
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
    
    private void obtenerMultiMedia(int id_articulo){
    	Intent resultadosEspecificos = new Intent(this, ResultadosEspecificosActivity.class);
    	resultadosEspecificos.putExtra("id_articulo", id_articulo);
    	//resultadosEspecificos.putExtra(name, value);
		startActivity(resultadosEspecificos);
    }
    
}
