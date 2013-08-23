package com.example.museos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.museos.model.City;

public class Login extends Activity {
	
	
	JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb = null;
	
	
	private Button botonEntrar, botonSalir, botonCreditos;
	private EditText editTextToken;
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //StrictMode.enableDefaults();
        
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        setContentView(R.layout.activity_login);
        
        getActionBar().hide();
        
        botonEntrar = (Button) findViewById(R.id.button_login_entrar);
        botonSalir = (Button) findViewById(R.id.button_login_salir);
        botonCreditos = (Button) findViewById(R.id.button_login_creditos);
        
        editTextToken = (EditText) findViewById(R.id.editText_login_token);
        editTextToken.setText("");
        
        botonEntrar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ingresarAlSistema();
			}
		});
        
        botonSalir.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
        
        botonCreditos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				desplegarCreditos();
			}
		});
        
        /*
        ArrayList<City> cities = new ArrayList<City>();
        
        try{
        	 URL prueba = new URL(
                    "http://www.jsalas.codefactorycr.com/city.php");
             URLConnection tc = prueba.openConnection();
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     tc.getInputStream()));
             String line;
             
             while ((line = in.readLine()) != null) {
                 JSONArray ja = new JSONArray(line);
                 for (int i = 0; i < ja.length(); i++) {
                     JSONObject jo = (JSONObject) ja.get(i);
                     String nombre = jo.getString("CITY_NAME");
                     int id = Integer.parseInt(jo.getString("CITY_ID"));
                     cities.add(new City(id, nombre));
                 }
             }
        }
        catch(Exception e){
        	Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
        }
        
        for(City city : cities){
        	Log.d("log_tag", "Ciudad: " + city.toString());
        }
        */
        
        /*ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();
			//HttpPost httppost = new HttpPost("http://10.0.2.2/pruebaAndroid/city.php");
			HttpPost httppost = new HttpPost("http://www.jsalas.codefactorycr.com/city.php");
			//HttpPost httppost = new HttpPost("http://192.168.0.101/pruebaAndroid/city.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection"+e.toString());
		}
		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			
			String line="0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString(), e);
		}
		//paring data
		int ct_id;
		String ct_name;
		try{
			jArray = new JSONArray("[{\"CITY_ID\":\"1\",\"CITY_NAME\":\"Paducah\"},{\"CITY_ID\":\"2\",\"CITY_NAME\":\"Oro Valley\"},{\"CITY_ID\":\"3\",\"CITY_NAME\":\"Elmira\"},{\"CITY_ID\":\"4\",\"CITY_NAME\":\"Dallas\"},{\"CITY_ID\":\"5\",\"CITY_NAME\":\"Champaign\"},{\"CITY_ID\":\"6\",\"CITY_NAME\":\"Hidden Hills\"},{\"CITY_ID\":\"7\",\"CITY_NAME\":\"York\"},{\"CITY_ID\":\"8\",\"CITY_NAME\":\"San Rafael\"},{\"CITY_ID\":\"9\",\"CITY_NAME\":\"Boston\"},{\"CITY_ID\":\"10\",\"CITY_NAME\":\"Glendale\"},{\"CITY_ID\":\"11\",\"CITY_NAME\":\"South Gate\"},{\"CITY_ID\":\"12\",\"CITY_NAME\":\"Westfield\"},{\"CITY_ID\":\"13\",\"CITY_NAME\":\"Gainesville\"},{\"CITY_ID\":\"14\",\"CITY_NAME\":\"Christiansted\"},{\"CITY_ID\":\"15\",\"CITY_NAME\":\"Decatur\"},{\"CITY_ID\":\"16\",\"CITY_NAME\":\"Sandy\"},{\"CITY_ID\":\"17\",\"CITY_NAME\":\"Plantation\"},{\"CITY_ID\":\"18\",\"CITY_NAME\":\"Aguadilla\"},{\"CITY_ID\":\"19\",\"CITY_NAME\":\"Bellflower\"},{\"CITY_ID\":\"20\",\"CITY_NAME\":\"Sheridan\"},{\"CITY_ID\":\"21\",\"CITY_NAME\":\"Easthampton\"},{\"CITY_ID\":\"22\",\"CITY_NAME\":\"Saint Paul\"},{\"CITY_ID\":\"23\",\"CITY_NAME\":\"Concord\"},{\"CITY_ID\":\"24\",\"CITY_NAME\":\"Areceibo\"},{\"CITY_ID\":\"25\",\"CITY_NAME\":\"Medford\"},{\"CITY_ID\":\"26\",\"CITY_NAME\":\"Brunswick\"},{\"CITY_ID\":\"27\",\"CITY_NAME\":\"Troy\"},{\"CITY_ID\":\"28\",\"CITY_NAME\":\"Areceibo\"},{\"CITY_ID\":\"29\",\"CITY_NAME\":\"Fayetteville\"},{\"CITY_ID\":\"30\",\"CITY_NAME\":\"Wichita Falls\"},{\"CITY_ID\":\"31\",\"CITY_NAME\":\"Pocatello\"},{\"CITY_ID\":\"32\",\"CITY_NAME\":\"Weymouth\"},{\"CITY_ID\":\"33\",\"CITY_NAME\":\"North Chicago\"},{\"CITY_ID\":\"34\",\"CITY_NAME\":\"Winooski\"},{\"CITY_ID\":\"35\",\"CITY_NAME\":\"Clovis\"},{\"CITY_ID\":\"36\",\"CITY_NAME\":\"San Dimas\"},{\"CITY_ID\":\"37\",\"CITY_NAME\":\"Hampton\"},{\"CITY_ID\":\"38\",\"CITY_NAME\":\"Clarksburg\"},{\"CITY_ID\":\"39\",\"CITY_NAME\":\"Morgan City\"},{\"CITY_ID\":\"40\",\"CITY_NAME\":\"La Mirada\"},{\"CITY_ID\":\"41\",\"CITY_NAME\":\"Loudon\"},{\"CITY_ID\":\"42\",\"CITY_NAME\":\"Jonesboro\"},{\"CITY_ID\":\"43\",\"CITY_NAME\":\"Jefferson City\"},{\"CITY_ID\":\"44\",\"CITY_NAME\":\"Sherrill\"},{\"CITY_ID\":\"45\",\"CITY_NAME\":\"Nampa\"},{\"CITY_ID\":\"46\",\"CITY_NAME\":\"Odessa\"},{\"CITY_ID\":\"47\",\"CITY_NAME\":\"Nichols Hills\"},{\"CITY_ID\":\"48\",\"CITY_NAME\":\"Fountain Valley\"},{\"CITY_ID\":\"49\",\"CITY_NAME\":\"Olympia\"},{\"CITY_ID\":\"50\",\"CITY_NAME\":\"Plainfield\"},{\"CITY_ID\":\"51\",\"CITY_NAME\":\"Carson City\"},{\"CITY_ID\":\"52\",\"CITY_NAME\":\"Union City\"},{\"CITY_ID\":\"53\",\"CITY_NAME\":\"Laramie\"},{\"CITY_ID\":\"54\",\"CITY_NAME\":\"Georgetown\"},{\"CITY_ID\":\"55\",\"CITY_NAME\":\"Waterloo\"},{\"CITY_ID\":\"56\",\"CITY_NAME\":\"Appleton\"},{\"CITY_ID\":\"57\",\"CITY_NAME\":\"Kenosha\"},{\"CITY_ID\":\"58\",\"CITY_NAME\":\"Somerville\"},{\"CITY_ID\":\"59\",\"CITY_NAME\":\"Bartlesville\"},{\"CITY_ID\":\"60\",\"CITY_NAME\":\"Reedsport\"},{\"CITY_ID\":\"61\",\"CITY_NAME\":\"Schaumburg\"},{\"CITY_ID\":\"62\",\"CITY_NAME\":\"Ruston\"},{\"CITY_ID\":\"63\",\"CITY_NAME\":\"Pocatello\"},{\"CITY_ID\":\"64\",\"CITY_NAME\":\"Klamath Falls\"},{\"CITY_ID\":\"65\",\"CITY_NAME\":\"Rhinelander\"},{\"CITY_ID\":\"66\",\"CITY_NAME\":\"Thibodaux\"},{\"CITY_ID\":\"67\",\"CITY_NAME\":\"Duluth\"},{\"CITY_ID\":\"68\",\"CITY_NAME\":\"Brookings\"},{\"CITY_ID\":\"69\",\"CITY_NAME\":\"Baytown\"},{\"CITY_ID\":\"70\",\"CITY_NAME\":\"Shelton\"},{\"CITY_ID\":\"71\",\"CITY_NAME\":\"Monrovia\"},{\"CITY_ID\":\"72\",\"CITY_NAME\":\"Monrovia\"},{\"CITY_ID\":\"73\",\"CITY_NAME\":\"El Paso\"},{\"CITY_ID\":\"74\",\"CITY_NAME\":\"Agawam\"},{\"CITY_ID\":\"75\",\"CITY_NAME\":\"Idaho Springs\"},{\"CITY_ID\":\"76\",\"CITY_NAME\":\"Stevens Point\"},{\"CITY_ID\":\"77\",\"CITY_NAME\":\"Oklahoma City\"},{\"CITY_ID\":\"78\",\"CITY_NAME\":\"Atlantic City\"},{\"CITY_ID\":\"79\",\"CITY_NAME\":\"Claremont\"},{\"CITY_ID\":\"80\",\"CITY_NAME\":\"Fremont\"},{\"CITY_ID\":\"81\",\"CITY_NAME\":\"Rensselaer\"},{\"CITY_ID\":\"82\",\"CITY_NAME\":\"Fremont\"},{\"CITY_ID\":\"83\",\"CITY_NAME\":\"DeKalb\"},{\"CITY_ID\":\"84\",\"CITY_NAME\":\"Delta Junction\"},{\"CITY_ID\":\"85\",\"CITY_NAME\":\"Norton\"},{\"CITY_ID\":\"86\",\"CITY_NAME\":\"Chula Vista\"},{\"CITY_ID\":\"87\",\"CITY_NAME\":\"Eatontown\"},{\"CITY_ID\":\"88\",\"CITY_NAME\":\"Boise\"},{\"CITY_ID\":\"89\",\"CITY_NAME\":\"Alexandria\"},{\"CITY_ID\":\"90\",\"CITY_NAME\":\"Fremont\"},{\"CITY_ID\":\"91\",\"CITY_NAME\":\"Sioux Falls\"},{\"CITY_ID\":\"92\",\"CITY_NAME\":\"Sharon\"},{\"CITY_ID\":\"93\",\"CITY_NAME\":\"Bessemer\"},{\"CITY_ID\":\"94\",\"CITY_NAME\":\"Menomonee Falls\"},{\"CITY_ID\":\"95\",\"CITY_NAME\":\"Fairfax\"},{\"CITY_ID\":\"96\",\"CITY_NAME\":\"Shelton\"},{\"CITY_ID\":\"97\",\"CITY_NAME\":\"Kahului\"},{\"CITY_ID\":\"98\",\"CITY_NAME\":\"Manassas Park\"},{\"CITY_ID\":\"99\",\"CITY_NAME\":\"Watertown\"},{\"CITY_ID\":\"100\",\"CITY_NAME\":\"Lockport\"}]");
			JSONObject json_data=null;
			for(int i=0;i<jArray.length();i++){
				json_data = jArray.getJSONObject(i);
				ct_id=json_data.getInt("CITY_ID");
				ct_name=json_data.getString("CITY_NAME");
			}
		}
		catch(JSONException e1){
			Toast.makeText(getBaseContext(), "No City Found" ,Toast.LENGTH_LONG).show();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}*/
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    
    private boolean validarToken(){
    	//Valida el token contra base de datos
		
        
		String url = getString(R.string.url_servidor);
		url += "token.php";
		url += "?token=";
		url += editTextToken.getText().toString();
		//Toast.makeText(this, url, Toast.LENGTH_LONG).show();
		
		String line;
		String token = "";
        int id = 0;
		
        try{
        	 URL conexion = new URL(url);
             URLConnection tc = conexion.openConnection();
             BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
             
             
             
             while ((line = in.readLine()) != null) {
                 JSONArray ja = new JSONArray(line);
                 for (int i = 0; i < ja.length(); i++) {
                     JSONObject jo = (JSONObject) ja.get(i);
                     token = jo.getString("token");
                     id = Integer.parseInt(jo.getString("valido"));
                     
                 }
             }
        }
        catch(Exception e){
        	Log.e("log_tag", "Error al pegarse a PHP: "+e.toString(), e);
        }
        
        
        
    	boolean valido = false;
    	/*logica de validacion*/
    	String clave = editTextToken.getText().toString();
    	if( token.equalsIgnoreCase(clave) &&  !clave.equalsIgnoreCase("")){
    		valido = true;
    	}
    	return valido;
    }
    
    private void ingresarAlSistema(){
    	boolean esTokenValido = validarToken();
    	if (esTokenValido){
    		//Redirige a Home
    		Intent entrar = new Intent(this, HomeActivity.class);
    		startActivity(entrar);
    	}
    	else{
    		//Indica que el token es invalido
    		String mensaje = getString(R.string.login_token_erroneo);
    		Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    	}
    }
    
    private void desplegarCreditos(){
    	Intent creditos = new Intent(this, CreditosApp.class);
    	startActivity(creditos);
    }
    
    
    
    
}
