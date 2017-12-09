package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class ImagenPantallaCompleta extends AppCompatActivity {
    //para establecer la conexion con el web service
    RequestQueue reques;
    //declarar objeto para referenciarlo
    ImageView ImagenCompleta;
    //variable para almacenar la ruta de la imagen que se le envio
    String rutaEnviada;

    //url web service 00web
    // final String UrlGlo="https://reportatec.000webhostapp.com/ReportaTec/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_pantalla_completa);
        //crear el boton atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //referenciar el objeto reques
        reques = Volley.newRequestQueue(ImagenPantallaCompleta.this);
        //referenciar emage view
        ImagenCompleta=(ImageView) findViewById(R.id.imageViewPantalla);
        //recuperar los datos que le enviaron
        //recuperar la intencion que la llamo
        Intent intencionLlamo=getIntent();
        //recuperar los datos recibidos
        Bundle bundle =intencionLlamo.getExtras();
        rutaEnviada= bundle.getString("EnviarRura");
        //metodo para cargar la imagen 
        CargarWebServiceImagen();
        //metodo para bloquear la pantalla segun la opcion del archivo de preferencias
        BloquearPantalla();
    }

    //metodo que carga la imagen en pantalla completa
    private void CargarWebServiceImagen() {
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/"+rutaEnviada;
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //creamos un objeto de tipo imagerequest para mostrar la imagen desde el servidor
        ImageRequest imagenreques= new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                //si todo sale bien obtenemos la respuesta y se lo asignamos al imagevoew
                ImagenCompleta.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de que ocurra un problema
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(ImagenPantallaCompleta.this, "Error al cargar la imagen", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(ImagenPantallaCompleta.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(imagenreques);
    }

    //para el boton atras
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //metodo para obtener la ip ingresada
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        return pref.getString("ip_servidor","");
    }

    //metodo para bloquear la pnatalla
    public void BloquearPantalla(){
        //llamar al metodo que tiene el valor del archivo de preferencias
        boolean valor =valorpantalla();
        //si es verdadero se bloqueara la pantalla
        if (valor==true){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            //en caso contrario la pantalla se movera conforme al sensor
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
    //metodo para obtener el valor de la orientacion
    public boolean valorpantalla(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        //pref.edit().clear().commit();
        return pref.getBoolean("bloquear",false);
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
