package com.example.museos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class ReproduccionTextoActivity extends Activity {
	
	private TextView texto;

	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproduccion_texto);
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        Intent intentCaptura = getIntent();
        String URL = intentCaptura.getStringExtra("URL_Texto");
        texto = (TextView) findViewById(R.id.textViewFinal);
        texto.setMovementMethod(new ScrollingMovementMethod());
        texto.setWidth(500);
        texto.setHeight(300);
        desplegarTexto(URL);
        

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_reproduccion_texto, menu);
        return true;
    }

    private void desplegarTexto(String URL){
        texto.setText("");	
        try {
            URL url = new URL(URL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            String resultado = "";
            while ((str = in.readLine()) != null) {
            	resultado += str + "\n";
            }
            in.close();
            texto.setText(resultado);
        	} catch (MalformedURLException e) {
        		texto.setText("mal");
        	} catch (IOException e) {
            	texto.setText("io");
        	}
    }

}
