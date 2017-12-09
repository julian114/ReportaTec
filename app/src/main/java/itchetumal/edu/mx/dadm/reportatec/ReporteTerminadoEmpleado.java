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
import android.widget.ImageView;
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
import android.view.View.OnClickListener;

import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsReportes;

public class ReporteTerminadoEmpleado extends AppCompatActivity implements OnClickListener{
    String FolioRe;
    String Prioridad;
    String EstadoRe;
    String ComentariosRe;

    ImageView CampoImagen;
    String ComenRe;
    //declarar variables
    TextView folio,TipoRep,Problema,Edificio,Salon,Descripcion,Prioridadtext,Estadotext,ComentariosTEXT,CalificacionTextUs,ComentarioTEXT;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //declarar variables para guardar los datos del archivo de preferencias
    //variable que almacena la ruta
    String rutaBuscada;
    //url web service 00web
    // final String UrlGlo="https://reportatec.000webhostapp.com/ReportaTec/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //recuperar la intencion que la llamo
        Intent intencionLlamo=getIntent();
        //recuperar los datos recibidos
        Bundle bundle =intencionLlamo.getExtras();
        FolioRe= bundle.getString("FolioEn");
        Prioridad=bundle.getString("PrioridadEn");
        EstadoRe=bundle.getString("EstadoEn");
        ComentariosRe=bundle.getString("ComentariosEn");
        setContentView(R.layout.activity_reporte_terminado_empleado);

        //crear el boton atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reques = Volley.newRequestQueue(ReporteTerminadoEmpleado.this);
        //llamar al metodo que trae los datos
        CargarWebService();

        //Asignar a los campos
        folio=(TextView) findViewById(R.id.textViewFolioReporteReporteRecibidoEmpleado);
        TipoRep=(TextView) findViewById(R.id.textViewNombreTipoReporteRecibidoEmpleado);
        Problema=(TextView) findViewById(R.id.textViewTipoProblemaReporteRecibidoEmpleado);
        Edificio=(TextView) findViewById(R.id.textVieweEdificioReporteRecibidoEmpleado);
        Salon=(TextView) findViewById(R.id.textViewSalonReporteRecibidoEmpleado);
        Descripcion=(TextView) findViewById(R.id.textViewDescripcionReporteRecibidoEmpleado);
        Prioridadtext=(TextView) findViewById(R.id.textViewPrioridadAsignadaRecibidoEmpleado);
        Estadotext=(TextView) findViewById(R.id.textViewEstadoReportePuestoPorEmpleado);
        ComentariosTEXT=(TextView) findViewById(R.id.textViewComentariosHechosPorEmpleado);
        CalificacionTextUs=(TextView) findViewById(R.id.textViewCalificacionRecibidaPorUsuario);
        ComentarioTEXT=(TextView) findViewById(R.id.textViewComentarioReibidoPorUsuario);
        View botonHistorial=findViewById(R.id.btnHistorial);
        botonHistorial.setOnClickListener(this);


        CampoImagen=(ImageView) findViewById(R.id.imageViewReporteRecibidoEmpleado);
        CampoImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //metodo para abrir la imagen
                CargarImagen();

            }
        });
        BloquearPantalla();
    }

    private void CargarImagen() {
        Intent ImagenPantalla = new Intent(this, ImagenPantallaCompleta.class);
        ImagenPantalla.putExtra("EnviarRura",rutaBuscada);
        startActivity(ImagenPantalla);
    }

    public void CargarWebService(){
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarReportes.php?IdRep="+FolioRe;
        url= url.replace(" ","%20");
        //enviarcelo a volley para que lo procese
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ItemsReportes NuevoItemReportes=null;
                JSONArray jsonArregloReportes = response.optJSONArray("reportesUs");

                NuevoItemReportes = new ItemsReportes();
                JSONObject jsonObjetoReportes = null;
                try {
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


                    //asignar a los campos
                    folio.setText(NuevoItemReportes.getIdReporte().toString());
                    TipoRep.setText(NuevoItemReportes.getTipoReporte().toString());
                    Problema.setText(NuevoItemReportes.getProblema().toString());
                    Edificio.setText(NuevoItemReportes.getEdificio().toString());
                    Salon.setText(NuevoItemReportes.getSalon().toString());
                    Descripcion.setText(NuevoItemReportes.getDescripcion().toString());
                    Prioridadtext.setText(Prioridad);
                    Estadotext.setText(EstadoRe);
                    ComentariosTEXT.setText(ComentariosRe);
                    if (jsonObjetoReportes.optString("ComentarioSatis")=="null"){
                        CalificacionTextUs.setText("No hay calificacion");
                        ComentarioTEXT.setText("No hay comentarios");
                    }else {
                        CalificacionTextUs.setText(jsonObjetoReportes.optString("Calificacion"));
                        ComentarioTEXT.setText(jsonObjetoReportes.optString("ComentarioSatis"));}

                    //mostar la imagen si es que hay alguna
                    if (NuevoItemReportes.getFoto()!=null){
                        CampoImagen.setImageBitmap(NuevoItemReportes.getFoto());}
                    else
                    {
                        CampoImagen.setImageResource(R.drawable.itch);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mostrar mensaje
                if (exiteConexionInternet()){
                    Toast.makeText(ReporteTerminadoEmpleado.this, "Intente mas tarde", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ReporteTerminadoEmpleado.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        });
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);
    }
    //para regresar con el boton atras
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
    //metodo universal
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnHistorial){
            Intent Historial = new Intent(this, HistorialEstatus.class);
            Historial.putExtra("Idreporte",FolioRe);
            startActivity(Historial);

        }

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
