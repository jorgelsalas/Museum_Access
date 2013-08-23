package com.example.museos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

public class FormatosActivity extends Activity {
	
	RadioButton radio1, radio2, radio3;
	ImageView formato1, formato2, formato3;
	Button botonElegir, botonCerrarSesion, botonAyuda;
	private AlertDialog alertDialog;
	
	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formatos);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
        
        
        radio1 = (RadioButton) findViewById(R.id.radio_formatos_1);
        radio2 = (RadioButton) findViewById(R.id.radio_formatos_2);
        radio3 = (RadioButton) findViewById(R.id.radio_formatos_3);
        
        formato1 = (ImageView) findViewById(R.id.imageView_formato1);
        formato2 = (ImageView) findViewById(R.id.imageView_formato2);
        formato3 = (ImageView) findViewById(R.id.imageView_formato3);
        
        botonElegir = (Button) findViewById(R.id.button_formatos_elegir);
        botonCerrarSesion = (Button) findViewById(R.id.button_formatos_cerrar_sesion);
        botonAyuda = (Button) findViewById(R.id.button_formatos_ayuda);
        
        if (Aplicacion.formatoResultados == 1){
        	radio1.setChecked(true);
		}
        else if (Aplicacion.formatoResultados == 2){
        	radio2.setChecked(true);
		}
        else if (Aplicacion.formatoResultados == 3){
        	radio3.setChecked(true);
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
				alertDialog.setMessage(getResources().getString(R.string.mensaje_ayuda_formatos));
				alertDialog.show();
			}
		});
		
        
        radio1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (radio2.isChecked()){
					radio2.setChecked(false);
				}
				if (radio3.isChecked()){
					radio3.setChecked(false);
				}
			}
		});
        
        radio2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (radio1.isChecked()){
					radio1.setChecked(false);
				}
				if (radio3.isChecked()){
					radio3.setChecked(false);
				}
			}
		});
        
        radio3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (radio1.isChecked()){
					radio1.setChecked(false);
				}
				if (radio2.isChecked()){
					radio2.setChecked(false);
				}
			}
		});
        
        formato1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				radio1.setChecked(true);
				radio1.callOnClick();
			}
		});
        
        formato2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				radio2.setChecked(true);
				radio2.callOnClick();
			}
		});
        
        formato3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				radio3.setChecked(true);
				radio3.callOnClick();
			}
		});
        
        botonElegir.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (radio1.isChecked()){
					Aplicacion.formatoResultados = 1;
				}
				else if (radio2.isChecked()){
					Aplicacion.formatoResultados = 2;
				}
				else if (radio3.isChecked()){
					Aplicacion.formatoResultados = 3;
				}
				verResultados();
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
        getMenuInflater().inflate(R.menu.activity_formatos, menu);
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
    
    private void verResultados(){
    	Intent resultados = new Intent(this, ResultadosActivity.class);
    	Intent intentCaptura = getIntent();
    	String url = intentCaptura.getStringExtra("url");
    	resultados.putExtra("url", url);
		startActivity(resultados);
    }

}
