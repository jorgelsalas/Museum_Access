package com.example.museos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.museos.model.Acutes;

public class InfoMuseoActivity extends Activity {
	
	private TextView textTelefono, textFax, textEmail, textHorario, textNombreMuseo;
	private Acutes corrector = new Acutes();
	private Button botonCerrarSesion;
	
	
	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        setContentView(R.layout.activity_info_museo);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
        
        textTelefono = (TextView) findViewById(R.id.info_telefono);
        textFax = (TextView) findViewById(R.id.info_fax);
        textEmail = (TextView) findViewById(R.id.info_email);
        textHorario = (TextView) findViewById(R.id.info_horario);
        textNombreMuseo = (TextView) findViewById(R.id.info_nombre_museo);
        
        botonCerrarSesion = (Button) findViewById(R.id.button_info_cerrar_sesion);
        
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cerrarSesion();
			}
		});
        
        cargarInfo();
        
        
        
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
        getMenuInflater().inflate(R.menu.activity_info_museo, menu);
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
    
    private void cargarInfo(){
    	
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
                    
                    textTelefono.setText(jo.getString("telefono"));
                    textFax.setText(jo.getString("fax"));
                    textHorario.setText(corrector.reemplazar(jo.getString("horario")));
                    textEmail.setText(jo.getString("email"));
                    if(jo.getString("nombreMuseo") == null){
                    	textNombreMuseo.setText("");
                                        	
                    }else{
                    	textNombreMuseo.setText(jo.getString("nombreMuseo"));
                    }
                    //jo.getString("horario").format(Locale., format, args)
                }
            }
       }
       catch(Exception e){
       	Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
       }
    	
    }

}
