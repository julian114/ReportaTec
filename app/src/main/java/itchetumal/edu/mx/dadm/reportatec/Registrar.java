package itchetumal.edu.mx.dadm.reportatec;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.service.voice.VoiceInteractionSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//necesario para acceder al web service
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Registrar extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener{
   //declarar variables para acceder a los componentes
    Button botonEnviar, botonCancelar;
    EditText NumeroConUsu, NombreUsu, CorreoUsu,Usuario,Contrase;
    //para ventana de progreso en caso que se tarde
    ProgressDialog progre;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;

    //url web service 00web
    // final String UrlGlo="https://reportatec.000webhostapp.com/ReportaTec/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        //orientacion de pantalla bloqueada
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //referenciar boton enviar
        botonEnviar = (Button) findViewById(R.id.btnEnviar);
        //referenciar boton ancelar
        botonCancelar = (Button) findViewById(R.id.btncan);
        //referenciar campos
        NumeroConUsu = (EditText) findViewById(R.id.numcontrol);
        NombreUsu = (EditText) findViewById(R.id.nomUsu);
        CorreoUsu = (EditText) findViewById(R.id.correo);
        Usuario = (EditText) findViewById(R.id.usuario);
        Contrase = (EditText) findViewById(R.id.cont);
         //referenciar el objeto reques
        reques = Volley.newRequestQueue(Registrar.this);
        //metdo para saber si se oprime el boton, clase anonima
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //llamar al metodo para cargar el web service
                CargarWebService();
            }
        });
        //metodo para saber si oprime el boton cancelar, clase anonima
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se regresa al login
                Intent intentoRegresar = new Intent(Registrar.this, Login.class);
                startActivity(intentoRegresar);
                //se finaliza la actividad
                finish();
            }
        });
    }

    //metodo para realizar el registro
    public void CargarWebService(){
        //crear ventana de progreso
        progre= new ProgressDialog(this);
        //mensaje
        progre.setMessage("...cargando");
        //mostrar ventana
        progre.show();
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url= "http://"+direecionip()+"/ReportaTec/wsJSONRegistro.php?NumControlUsu="+NumeroConUsu.getText().toString()
                +"&NombreUsu="+NombreUsu.getText().toString()
                +"&CorreoUsu="+CorreoUsu.getText().toString()
                +"&Usuari="+Usuario.getText().toString()
                +"&Contrase="+Contrase.getText().toString();
         //eliminamos los espacios por %20
        url= url.replace(" ","%20");

        //enviarcelo a volley para que lo procese
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);


    }

    //si hay un error entra aqui
    @Override
    public void onErrorResponse(VolleyError error) {
        //ocultar barra
        progre.hide();
        //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
        if (exiteConexionInternet()){
            //si exite conexion a internet mostar
            Toast.makeText(Registrar.this, "Error al registrar", Toast.LENGTH_LONG).show();
        }else{
            //en caso de que no exista conexion a internet
            Toast.makeText(Registrar.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
        }

    }

    //si todo esta bien entra aqui

    @Override
    public void onResponse(JSONObject response) {
        //mostrar mensaje
        Toast.makeText(this,"Usuario registrado, Ahora puedes Iniciar sesion ",Toast.LENGTH_SHORT).show();
        //ocultar barra
        progre.hide();
        //limpiar campos
        NumeroConUsu.setText("");
        NombreUsu.setText("");
        CorreoUsu.setText("");
        Usuario.setText("");
        Contrase.setText("");

    }
    //metodo para obtener la ip ingresada
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        return pref.getString("ip_servidor","");
    }
    //metodo para sber el estado del internet
    public boolean exiteConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}

