package com.example.museos.model;

public class Acutes {
	
	private String palabra;
	
	public Acutes(){
		palabra = "";
	}
	
	public Acutes(String texto){
		palabra = texto;
	}
	
	public void sustituirAcentos(){
		
		String aTildada = "&aacute;";
		String eTildada = "&eacute;";
		String iTildada = "&iacute;";
		String oTildada = "&oacute;";
		String uTildada = "&uacute;";
		String ntilde = "ntilde;";
		
		if(palabra.toLowerCase().contains(aTildada.toLowerCase())){
			palabra = palabra.replace("&aacute;", "�");
		}
		
		if(palabra.toLowerCase().contains(eTildada.toLowerCase())){
			palabra = palabra.replace("&eacute;", "�");
		}
		
		if(palabra.toLowerCase().contains(iTildada.toLowerCase())){
			palabra = palabra.replace("&iacute;", "�");
		}
		
		if(palabra.toLowerCase().contains(oTildada.toLowerCase())){
			palabra = palabra.replace("&oacute;", "�");
		}
		
		if(palabra.toLowerCase().contains(uTildada.toLowerCase())){
			palabra = palabra.replace("&uacute;", "�");
		}
		
		if(palabra.toLowerCase().contains(ntilde.toLowerCase())){
			palabra = palabra.replace("&ntilde;", "�");
		}
	}
	
	public String getPalabra(){
		return palabra;
		
	}
	
	public void setPalabra(String texto){
		palabra = texto;
		
	}
	
	public String reemplazar(String texto){
		setPalabra(texto);
		sustituirAcentos();
		return getPalabra();
	}
}
