package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsReportes;

import static itchetumal.edu.mx.dadm.reportatec.R.id.imageView;

public class ReporteEnviadoUsuario extends AppCompatActivity implements View.OnClickListener {
    //declrar variables para almacenar los datos obtenidos
    ImageView CampoImagen;
    String FolioRe;
    String EstadoRe;
    String ComenRe;
    Button btnCalificar;
    //declarar variables para refernciarlos
    TextView folio,TipoRep,Problema,Edificio,Salon,Descripcion,Estado,comentariosDepto;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //declarar variables para guardar los datos
    //variable que almacena la ruta
    String rutaBuscada;
    //url web service 00web
    // final String UrlGlo="https://reportatec.000webhostapp.com/ReportaTec/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //recuperar el datos que se le envio
        //recuperar la intencion que la llamo
        Intent intencionLlamo=getIntent();
        //recuperar los datos recibidos
        Bundle bundle =intencionLlamo.getExtras();
        FolioRe= bundle.getString("FolioEn");
        EstadoRe= bundle.getString("EstadoEn");
        ComenRe=bundle.getString("ComentariosEn");

        setContentView(R.layout.activity_reporte_enviado_usuario);
        //crear el boton atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //refernciar el boton
        View btnCali = findViewById(R.id.btnCalificarReporteEnviadoUsuario);
        btnCali.setOnClickListener((View.OnClickListener) this);
       //refernciar el boton calificar
        btnCalificar = (Button) findViewById(R.id.btnCalificarReporteEnviadoUsuario);
        btnCalificar.setVisibility(View.INVISIBLE);
        //referenciar el objeto reques
        reques = Volley.newRequestQueue(ReporteEnviadoUsuario.this);
        //llamar al metodo que trae los datos
        CargarWebService();

        //referenciar los campos
        folio=(TextView) findViewById(R.id.textViewFolioReporteEnviadoUsuario);
        TipoRep=(TextView) findViewById(R.id.textViewNombreTipoReporte);
        Problema=(TextView) findViewById(R.id.textViewTipoProblemaReporte);
        Edificio=(TextView) findViewById(R.id.textVieweEdificioReporte);
        Salon=(TextView) findViewById(R.id.textViewSalonReporte);
        Descripcion=(TextView) findViewById(R.id.textviewDescipcionEnviadoUsuario);
        Estado=(TextView) findViewById(R.id.textViewEstadoDelReporte);
        comentariosDepto=(TextView) findViewById(R.id.textViewComentariosDelDepartamento);
        CampoImagen=(ImageView) findViewById(R.id.imageViewReporteEnviadoUsuario);
        //metodo para saber si se presiono la imagen, clase anonima
        CampoImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //metodo para abrir la imagen
                CargarImagen();

            }
        });
        //metodo para bloquear la pantalla
        BloquearPantalla();
    }

    //metodo para cargar la imagen
    private void CargarImagen() {
        //se intenta abrir la actividad imagenpantallacompleta
        Intent ImagenPantalla = new Intent(this, ImagenPantallaCompleta.class);
        //se le envia la ruta obtenida
        ImagenPantalla.putExtra("EnviarRura",rutaBuscada);
        startActivity(ImagenPantalla);
    }
    //metodo que carga los datos del reporte especificado del servidor
    public void CargarWebService(){
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarReportes.php?IdRep="+FolioRe;
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                 //crear un objeto de itemsreportes
                ItemsReportes NuevoItemReportes=null;
                //obtenemos la respuesta del json llamado reportesUs y se lo asignamos al arreglo json
                JSONArray jsonArregloReportes = response.optJSONArray("reportesUs");
                //crear un objeto de tipo itesreportes para guardar los datso de cada reporte
                NuevoItemReportes = new ItemsReportes();
                //inicilizar el jsonobjet con null
                JSONObject jsonObjetoReportes = null;
                //intentamos acceder a los datos que envio el json
                try {
                    //obtenemos los campos de respuesta y se los asignamos al objeto
                    jsonObjetoReportes = jsonArregloReportes.getJSONObject(0);
                    NuevoItemReportes.setIdReporte(jsonObjetoReportes.optInt("IdReporte"));
                    NuevoItemReportes.setTipoReporte(jsonObjetoReportes.optString("TipoReporte"));
                    NuevoItemReportes.setProblema(jsonObjetoReportes.optString("Problema"));
                    NuevoItemReportes.setEdificio(jsonObjetoReportes.optString("Edificio"));
                    NuevoItemReportes.setSalon(jsonObjetoReportes.optString("Salon"));
                    NuevoItemReportes.setDescripcion(jsonObjetoReportes.optString("Descripcion"));
                    NuevoItemReportes.setFecha(jsonObjetoReportes.optString("Fecha"));
                    NuevoItemReportes.setDato(jsonObjetoReportes.optString("Foto"));
                    NuevoItemReportes.setIdUsuario(jsonObjetoReportes.optInt("IdUsuario"));
                    //obtener la ruta
                    rutaBuscada=jsonObjetoReportes.optString("Ruta");
                    //preguntar si el comentario es null para ocultar el mostra el boton calificar
                    if (jsonObjetoReportes.optString("ComentarioSatis")!="null"){
                        btnCalificar.setVisibility(View.VISIBLE);
                    }

                    //asignar a los campos los datos obtenido
                    folio.setText(NuevoItemReportes.getIdReporte().toString());
                    TipoRep.setText(NuevoItemReportes.getTipoReporte().toString());
                    Problema.setText(NuevoItemReportes.getProblema().toString());
                    Edificio.setText(NuevoItemReportes.getEdificio().toString());
                    Salon.setText(NuevoItemReportes.getSalon().toString());
                    Descripcion.setText(NuevoItemReportes.getDescripcion().toString());
                    Estado.setText(EstadoRe.toString());
                    comentariosDepto.setText(ComenRe);

                    //mostar la imagen si es que hay alguna
                    if (NuevoItemReportes.getFoto()!=null){
                        CampoImagen.setImageBitmap(NuevoItemReportes.getFoto());}
                    else
                    {
                        //en caso de que no tenga imagen se le asigna el logo del tecnologico
                        CampoImagen.setImageResource(R.drawable.itch);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(ReporteEnviadoUsuario.this, "Intente mas tarde", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(ReporteEnviadoUsuario.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);
    }

    //metodo para el boton atras
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //metodo para saber si se presiona el boton calificar en caso de estar visibles
    @Override
    public void onClick(View v) {
        //si se presiona el boton entras
        if (v.getId() == R.id.btnCalificarReporteEnviadoUsuario){
            //se abre la intencion para calificar
            Intent cali = new Intent(this, CalificarReporteUsuario.class);
            //se le envian los datos del reporte
            cali.putExtra("FolioEn",FolioRe);
            cali.putExtra("TipoRepEn",TipoRep.getText().toString());
           startActivity(cali);
            //se finaliza esta actividad
            finish();
        }
    }
    //metodo para obtener la ip ingresada
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        return pref.getString("ip_servidor","");
    }
    //metodo para bloquear la pantalla
    public void BloquearPantalla(){
        //posicion de la pantalla
        boolean valor =valorpantalla();
        if (valor==true){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
    //metodo para obtener el valor de la orientacion
    public boolean valorpantalla(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
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
