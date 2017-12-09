package itchetumal.edu.mx.dadm.reportatec;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.view.View.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsReportes;

public class ReporteRecibidoEmpleado extends AppCompatActivity implements OnClickListener {
    //para ventana de progreso en caso que se tarde
    ProgressDialog progre;
    ImageView CampoImagen;
    String FolioRe;
    String Prioridad;
    String EstadoReporte;
    //declarar variables
    TextView folio,TipoRep,Problema,Edificio,Salon,Descripcion, Prioridadtext;
    EditText DescripcionAdmin;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //variable que almacena la ruta
    String rutaBuscada;
    StringRequest stringRequest;

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
        //crear el boton atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_reporte_recibido_empleado);
        final String [] values2 =
                {"En revision","En espera","Resuelto"};
        Spinner SPINNERESTADOPUESTOPOREMPLE = (Spinner) findViewById(R.id.spinnerEstadoReportePuestoPorEmpleado);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values2);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        SPINNERESTADOPUESTOPOREMPLE.setAdapter(myAdapter2);

        reques = Volley.newRequestQueue(ReporteRecibidoEmpleado.this);
        CargarWebServiceDatosReporte();

        View botonCancelar=findViewById(R.id.btnCancelarReporteRecibidoEmpleado);
        botonCancelar.setOnClickListener(this);
        View botonGuardar=findViewById(R.id.btnGuardarReporteRecibidoEmpleado);
        botonGuardar.setOnClickListener(this);
        View botonHistorial=findViewById(R.id.btnHistorial);
        botonHistorial.setOnClickListener(this);

        //Asignar a los campos
        folio=(TextView) findViewById(R.id.textViewFolioReporteReporteRecibidoEmpleado);
        TipoRep=(TextView) findViewById(R.id.textViewNombreTipoReporteRecibidoEmpleado);
        Problema=(TextView) findViewById(R.id.textViewTipoProblemaReporteRecibidoEmpleado);
        Edificio=(TextView) findViewById(R.id.textVieweEdificioReporteRecibidoEmpleado);
        Salon=(TextView) findViewById(R.id.textViewSalonReporteRecibidoEmpleado);
        Descripcion=(TextView) findViewById(R.id.textViewDescripcionReporteRecibidoEmpleado);
        CampoImagen=(ImageView) findViewById(R.id.imageViewReporteRecibidoEmpleado);
        Prioridadtext=(TextView) findViewById(R.id.textViewPrioridadAsignadaRecibidoEmpleado);
        DescripcionAdmin=(EditText)findViewById(R.id.editTextComentarioDelEmpleadoSobreReporte);


        CampoImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //metodo para abrir la imagen
                CargarImagen();

            }
        });

        SPINNERESTADOPUESTOPOREMPLE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //obtener el item elegido
                EstadoReporte =values2[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        BloquearPantalla();
    }

    private void CargarImagen() {
        Intent ImagenPantalla = new Intent(this, ImagenPantallaCompleta.class);
        ImagenPantalla.putExtra("EnviarRura",rutaBuscada);
        startActivity(ImagenPantalla);
    }

    //para regresar con el boton atras
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void CargarWebServiceDatosReporte(){
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
                Toast.makeText(ReporteRecibidoEmpleado.this, "No se puede conectar comprube su conexion a internet", Toast.LENGTH_LONG).show();

            }
        });
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancelarReporteRecibidoEmpleado){
            finish();
        }else if (v.getId() == R.id.btnGuardarReporteRecibidoEmpleado){
            CargarWebServiceGuardarHistorialEstatus();
            webServiceActualizar();
        }else if (v.getId() == R.id.btnHistorial){
            Intent Historial = new Intent(this, HistorialEstatus.class);
            Historial.putExtra("Idreporte",FolioRe);
            startActivity(Historial);

        }

    }
    private void webServiceActualizar() {
        progre=new ProgressDialog(this);
        progre.setMessage("Cargando...");
        progre.show();
        String url="http://"+direecionip()+"/ReportaTec/wsJSONActualizarEstatus.php?";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progre.hide();

                if (response.trim().equalsIgnoreCase("actualiza")){

                    Toast.makeText(ReporteRecibidoEmpleado.this,"Se ha Guardado con exito",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ReporteRecibidoEmpleado.this,"No se ha Guardado ",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progre.hide();
                if (exiteConexionInternet()){
                    Toast.makeText(ReporteRecibidoEmpleado.this, "Intente mas tarde", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ReporteRecibidoEmpleado.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String IdReporte=folio.getText().toString();
                String TipoEstatus=EstadoReporte;
                String Comentario=DescripcionAdmin.getText().toString();


                Map<String,String> parametros=new HashMap<>();
                parametros.put("IdReporte",IdReporte);
                parametros.put("TipoEstatus",TipoEstatus);
                parametros.put("Comentario",Comentario);

                return parametros;

            }
        };
        reques.add(stringRequest);

    }
    private void CargarWebServiceGuardarHistorialEstatus() {
        String url="http://"+direecionip()+"/ReportaTec/wsJSONRegistroHistorialReporteEstatus.php?IdReporte="+folio.getText().toString()+
                "&TipoEstatus="+EstadoReporte;
        url= url.replace(" ","%20");
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);
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
