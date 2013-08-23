package com.example.museos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class GenerarPDFActivity extends Activity {
	
	private AlertDialog alertDialog;
	
	private Button botonGenerarPDFGuardar, botonGenerarPDFEmail, botonGenerarPDFSesion, botonCerrarSesion, botonAyuda;
	private EditText editNombre, editEmail;
	private CheckBox checkEmail;
	private EditText textEmail;
	
	private String rutaImagenes;
	private String rutaTextos;
	private String id_articulos = "";
	private String url_imagenes = "";
	private String url_textos = "";
	private String url_descripcion = "";
	
	private boolean enviarCorreo = false;
	
	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        setContentView(R.layout.activity_generar_pdf);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
        
        botonGenerarPDFGuardar = (Button) findViewById(R.id.button_generarPDF_guardar);
        botonAyuda = (Button) findViewById(R.id.button_generarPDF_ayuda);
        //botonGenerarPDFEmail = (Button) findViewById(R.id.button_generarPDF_email);
        botonGenerarPDFSesion = (Button) findViewById(R.id.button_expos_cerrar_sesion);
        botonCerrarSesion = (Button) findViewById(R.id.button_generarPDF_cerrar_sesion);
        editNombre = (EditText) findViewById(R.id.editText_generarPDF_nombre);
        checkEmail = (CheckBox) findViewById(R.id.checkBox1);
        textEmail = (EditText) findViewById(R.id.editText1);
        //editEmail = (EditText) findViewById(R.id.editText_generarPDF_email);
        
        Intent intentCaptura = getIntent();
        id_articulos = intentCaptura.getStringExtra("ids_seleccionados");
        //Toast.makeText(this, "id_articulos: " + id_articulos, Toast.LENGTH_LONG).show();
        id_articulos = id_articulos.replace(" ", "%20");
        
        rutaImagenes = getResources().getString(R.string.url_servidor) + getResources().getString(R.string.carpeta_imagenes) + "/";
    	rutaTextos = getResources().getString(R.string.url_servidor) + getResources().getString(R.string.carpeta_texto) + "/";
        url_imagenes = getResources().getString(R.string.url_servidor) + getResources().getString(R.string.nombre_php_imagenes_pdf) + "?id_articulos=" + id_articulos;
        url_textos = getResources().getString(R.string.url_servidor) + getResources().getString(R.string.nombre_php_textos_pdf) + "?id_articulos=" + id_articulos;
        url_descripcion = getResources().getString(R.string.url_servidor) + getResources().getString(R.string.nombre_php_descripcion_pdf) + "?id_articulos=" + id_articulos;
        
        
        //Toast.makeText(this, "url_imagenes: " + url_imagenes, Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "ruta_imagenes: " + rutaImagenes, Toast.LENGTH_LONG).show();
        
        /////////////////////////////////////////////
        //editNombre.setText("test");
        //textEmail.setText("jorgesalch@yahoo.com");
        //checkEmail.setChecked(true);
        /////////////////////////////////////////////
        
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(R.drawable.emo_im_winking);
    	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
    	   }
    	});
        
        botonAyuda.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alertDialog.setTitle(getResources().getString(R.string.titulo_dialogo_ayuda));
				alertDialog.setMessage(getResources().getString(R.string.mensaje_ayuda_PDF));
				alertDialog.show();
			}
		});
        
        botonGenerarPDFGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String nombre = editNombre.getText().toString();
				if(!nombre.isEmpty()){
					if(checkEmail.isChecked()){
						if(textEmail.getText().toString().isEmpty()){
							mostrarAdvertenciaEmail();
						}else{
							enviarCorreo = true;
							generarPDF(nombre);
						}
					}else{
						enviarCorreo = false;
						generarPDF(nombre);
					}
				
				}
				else{
					mostrarAdvertenciaNombrePDF();
				}
			}
		});
        
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cerrarSesion();
			}
		});
        
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
        getMenuInflater().inflate(R.menu.activity_generar_pdf, menu);
        return true;
    }
    
    public void mostrarAdvertenciaNombrePDF(){
    	Toast.makeText(this, getResources().getString(R.string.advertencia_nombre_PDF), Toast.LENGTH_LONG).show();
    }
    
    public void mostrarAdvertenciaEmail(){
    	Toast.makeText(this, getResources().getString(R.string.advertencia_email), Toast.LENGTH_LONG).show();
    }

    public void mostrarAdvertenciaSDCard(){
    	Toast.makeText(this, getResources().getString(R.string.advertencia_sdcard), Toast.LENGTH_LONG).show();
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
    
    @SuppressLint("NewApi")
	private void generarPDF(String nombrePDF){
    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // Es posible leer y escribir en el media
            String nombre = "/"+nombrePDF+".pdf";
            String file = Environment.getExternalStorageDirectory().getPath() + nombre; 
		    Intent servicioPDF = new Intent(this, ServicioIntent.class);
		    servicioPDF.putExtra("file", file);
		    servicioPDF.putExtra("desc", url_descripcion);
		    servicioPDF.putExtra("imgs", url_imagenes);
		    servicioPDF.putExtra("text", url_textos);
		    servicioPDF.putExtra("rutaTextos", rutaTextos);
		    servicioPDF.putExtra("rutaImagenes", rutaImagenes);
		    servicioPDF.putExtra("boolCorreo", enviarCorreo);
		    servicioPDF.putExtra("nombrePDF", nombrePDF);
		    servicioPDF.putExtra("asunto_email", getResources().getString(R.string.email_asunto));
		    servicioPDF.putExtra("cuerpo_email", getResources().getString(R.string.email_cuerpo));
		    servicioPDF.putExtra("correo", textEmail.getText().toString());
		    startService(servicioPDF);
		    alertDialog.setTitle(getResources().getString(R.string.notificacion_titulo));
			alertDialog.setMessage(getResources().getString(R.string.notificacion_contenido));
			alertDialog.show(); 
    	}
    	else{
    		mostrarAdvertenciaSDCard();
    	}
    }
}
