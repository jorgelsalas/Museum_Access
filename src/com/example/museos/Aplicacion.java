package com.example.museos;

import android.app.Application;

public class Aplicacion extends Application {
	
	private static Aplicacion aplicacion;
	public static int formatoResultados = 1;
	public static boolean isConsultaAnidada = false;
	public static int resultados_por_pantalla = 18;
	
	
	public static Aplicacion getAplicacion(){
		return aplicacion;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		aplicacion = this;
	}
}
