package com.example.museos;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.museos.model.Acutes;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ServicioIntent extends IntentService {
	
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static Font mediumBold = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD);
	private static Font titulos = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.ITALIC);
	private static Font subtitulos = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC);
	private static Font informacion = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	
	private Acutes corrector = new Acutes();
	
	public ServicioIntent () {
		  super("ServicioIntent");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub

        int contador = 1;
        int idGeneral;
        
        String lineaDescrip = "";
        String lineaImgs = "";
        String lineaTxts = "";
        String descripcion;
    	String rutaTextos = arg0.getStringExtra("rutaTextos");
    	String rutaImagenes = arg0.getStringExtra("rutaImagenes");
        String file = arg0.getStringExtra("file");
	    String nombre = arg0.getStringExtra("nombrePDF");
	    String correo = arg0.getStringExtra("correo");
	    String asunto = arg0.getStringExtra("asunto_email");
	    String cuerpo = arg0.getStringExtra("cuerpo_email");
        
        boolean enviarCorreo = arg0.getBooleanExtra("boolCorreo", false);
        
        try{
        	
    		URL conexionDescripcion = new URL(arg0.getStringExtra("desc"));
		    URLConnection urlDescripcion = conexionDescripcion.openConnection();
		    BufferedReader inDescripcion = new BufferedReader(new InputStreamReader(urlDescripcion.getInputStream()));
		    lineaDescrip = inDescripcion.readLine();
		    JSONArray jaDesc = new JSONArray(lineaDescrip);
	        
    		URL conexionImagenes = new URL(arg0.getStringExtra("imgs"));
		    URLConnection urlImagenes = conexionImagenes.openConnection();
		    BufferedReader inImagenes = new BufferedReader(new InputStreamReader(urlImagenes.getInputStream()));
		    lineaImgs = inImagenes.readLine();
		    JSONArray jaImgs = null;
		    if(!lineaImgs.equals("null")){
		    	jaImgs = new JSONArray(lineaImgs); 
		    }
		    
		    URL conexionTextos = new URL(arg0.getStringExtra("text"));
		    URLConnection urlTextos = conexionTextos.openConnection();
		    BufferedReader inTextos = new BufferedReader(new InputStreamReader(urlTextos.getInputStream()));
		    lineaTxts = inTextos.readLine();
		    JSONArray jaTexts = null;
		    if(!lineaTxts.equals("null")){
		    	jaTexts = new JSONArray(lineaTxts); 
		    }
		    
	        Document document = new Document();
	        PdfWriter.getInstance(document, new FileOutputStream(file));
	        document.open();
	        document = addTitulo(document);
	        
	        for (int i = 0; i < jaDesc.length(); i++) {
	            JSONObject joDesc = (JSONObject) jaDesc.get(i);
	            
	            idGeneral = Integer.parseInt(joDesc.getString("ID_Objeto"));
	            descripcion = corrector.reemplazar(joDesc.getString("Descripcion"));
	            
	            String[] imagenes = null;
	            if(jaImgs != null){
		            List<String> imagenesURL = new ArrayList<String>();
		            for(int j = 0; j < jaImgs.length(); j++){
		            	
		            	JSONObject joImg = (JSONObject) jaImgs.get(j);
		            	int id_articulo = Integer.parseInt(joImg.getString("id_articulo"));
		            	if(id_articulo == idGeneral){
		            		imagenesURL.add(rutaImagenes + joImg.getString("id_imagen") + "." + joImg.getString("extension"));	
		            	}
		            }
		            if(!imagenesURL.isEmpty()){
		            	imagenes = new String[ imagenesURL.size() ];
			            imagenesURL.toArray( imagenes );
		            }
	            }
	            
	            String[] textos = null;
	            if(jaTexts != null){
		            List<String> textosURL = new ArrayList<String>();
		            for(int j = 0; j < jaTexts.length(); j++){
		            	
		            	JSONObject joTxt = (JSONObject) jaTexts.get(j);
		            	int id_articulo = Integer.parseInt(joTxt.getString("id_articulo"));
		            	if(id_articulo == idGeneral){
		            		textosURL.add(rutaTextos + joTxt.getString("id_texto") + ".txt");	
		            	}
		            }
		            
		            if(!textosURL.isEmpty()){
		            	textos = new String[ textosURL.size() ];
			            textosURL.toArray( textos );
		            }
	            }
	            
	            agregarCuerpo(document, textos, imagenes, descripcion, contador);
	            contador++;
	        }
	        
	        document.close();
	        
	        if(enviarCorreo){
	        	enviarPDFCorreo(correo, nombre, asunto, cuerpo);
	        }
	        
        }catch(Exception e){
    		Log.e("log_tag", "Error: "+e.toString(), e);
		}
 
	}
	
	private Document addTitulo(Document document)throws DocumentException, MalformedURLException, IOException {
        Paragraph preface = new Paragraph();
        Paragraph parrafo;
        preface = addEmptyLine(preface, 10);
        parrafo = new Paragraph("Knowledge Viewer", catFont);
        parrafo.setAlignment(Element.ALIGN_CENTER);
        preface.add(parrafo);
        preface = addEmptyLine(preface, 1);
        parrafo = new Paragraph("Fecha de creación: "+ new Date(), smallBold);
        parrafo.setAlignment(Element.ALIGN_CENTER);
        preface.add(parrafo);
        preface = addEmptyLine(preface, 1);
        parrafo = new Paragraph("Información de la Consulta", mediumBold);
        parrafo.setAlignment(Element.ALIGN_CENTER);
        preface.add(parrafo);
        preface = addEmptyLine(preface, 1);
        parrafo = new Paragraph("El siguiente documento contiene información acerca de la consulta emitida por el usuario.", smallBold);
        parrafo.setAlignment(Element.ALIGN_CENTER);
        preface.add(parrafo);
        preface = addEmptyLine(preface, 2);
        document.add(preface);
    	String imageUrl = "http://www.jsalas.codefactorycr.com/imagenes_expo/logo2.png";
        Image image1 = Image.getInstance(new URL(imageUrl));
        image1.setAbsolutePosition(0f, 767f);
        document.add(image1);
        document.newPage();
        return document;
    }

    private Document agregarCuerpo(Document document, String[] textos, String[] imagenes, String descripcion, int contador) throws DocumentException, MalformedURLException, IOException{
    	Paragraph padre = new Paragraph();
    	Anchor anchor = new Anchor(contador+". "+descripcion, titulos);  
    	Paragraph parrafo = new Paragraph(anchor);
    	parrafo = addEmptyLine(parrafo, 2);
    	padre.add(parrafo);
    	if(textos != null){
	    	parrafo = agregarArchivosTexto(textos);
	    	padre.add(parrafo);
    	}
    	if(imagenes != null){
	    	parrafo = new Paragraph("Imágenes", subtitulos);
	    	parrafo = addEmptyLine(parrafo, 1);
	    	padre.add(parrafo);
	    	for(int i=0; i<imagenes.length; i++){
	    		int indentacion = 0; //por si se quisiera
	    		Image image = Image.getInstance(new URL(imagenes[i]));
	    		if (image.getWidth() > (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentacion) ){
	    			float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
	    		    		- document.rightMargin() - indentacion) / image.getWidth()) * 100;
	    			image.scalePercent(scaler);
	    		}
	    		image.setAlignment(Image.MIDDLE);
	    		padre.add(image);
	    	}
    	}
    	document.add(padre);
    	document.newPage();
    	return document;
      }
      
    private Paragraph addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
          paragraph.add(new Paragraph(" "));
        }
        return paragraph;
    }
    
    private Paragraph agregarArchivosTexto(String[] urls){
    	String texto = "";
 
    	for(int i=0; i<urls.length; i++){

            try {
                URL url = new URL(urls[i]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                	texto += str + "\n";
                }
                in.close();
            } catch (MalformedURLException e) {
            	Log.e("log_tag", "URL malformado", e);
            } catch (IOException e) {
            	Log.e("log_tag", "Excepcion de IO", e);
            }
            texto += "\n";
    	}
    	
    	Paragraph parrafo = new Paragraph(texto, informacion);
    	parrafo.setAlignment(Element.ALIGN_JUSTIFIED_ALL);

    	return parrafo;
    }
    
    private void enviarPDFCorreo(String correo, String PDF, String asunto, String cuerpo){
    	
    	String[] mailto = {correo};
    	Uri uri = Uri.parse("file:///sdcard/"+PDF+".pdf");

    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, mailto);
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, asunto);
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, cuerpo);
    	emailIntent.setType("application/pdf");
    	emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
    	emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(emailIntent); 
    	
    }

}