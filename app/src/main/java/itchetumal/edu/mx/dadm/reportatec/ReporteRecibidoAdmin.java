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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.ArrayList;

import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemEmpleados;
import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsReportes;

public class ReporteRecibidoAdmin extends AppCompatActivity implements OnClickListener  {
    //para ventana de progreso en caso que se tarde
    ProgressDialog progre;

    ArrayList<ItemEmpleados> listaEmpleados;
    ArrayList<String> NombreEmpleados;

    ImageView CampoImagen;
    String FolioRe;
    //declarar variables
    TextView folio,TipoRep,Problema,Edificio,Salon,Descripcion;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //declarar variables para guardar los datos del archivo de preferencias
    //variable que almacena la ruta
    String rutaBuscada;
    Spinner spinnerEmpleados;

    int IdElegidoEmpleado;
    String Prioridad;

    //url web service 00web
    // final String UrlGlo="https://reportatec.000webhostapp.com/ReportaTec/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listaEmpleados=new ArrayList<>();
        NombreEmpleados = new ArrayList<String>();

        //recuperar la intencion que la llamo
        Intent intencionLlamo=getIntent();
        //recuperar los datos recibidos
        Bundle bundle =intencionLlamo.getExtras();
        FolioRe= bundle.getString("FolioEn");

        setContentView(R.layout.activity_reporte_recibido_admin);

        //agregar el boton regreaar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //crear un sppiner en un fragmento
        final String [] values =
                {"Alto","Medio","Bajo"};
        Spinner SPINNERNivelAdmin = (Spinner) findViewById(R.id.spinnerPrioridadPuestaPorAdmin);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        SPINNERNivelAdmin.setAdapter(myAdapter);

        spinnerEmpleados = (Spinner) findViewById(R.id.spinnerEmpleadoAsignadoPorAdmin);


        reques = Volley.newRequestQueue(ReporteRecibidoAdmin.this);
        //llamar al metodo que trae los datos
        CargarWebServiceDatosReporte();
        //llamar al metodo que trae los datos del emppleado
        CargarWebServiceDatosEmpleado();

        View botonCancelar=findViewById(R.id.btnCancelarReporteRecibidoAdmin);
        botonCancelar.setOnClickListener(this);
        View botonGuardar=findViewById(R.id.btnGuardarReporteRecibidoAdmin);
        botonGuardar.setOnClickListener(this);

        //Asignar a los campos
        folio=(TextView) findViewById(R.id.textViewFolioReporteReporteRecibidoAdmin);
        TipoRep=(TextView) findViewById(R.id.textViewNombreTipoReporteRecibidoAdmin);
        Problema=(TextView) findViewById(R.id.textViewTipoProblemaReporteRecibidoAdmin);
        Edificio=(TextView) findViewById(R.id.textVieweEdificioReporteRecibidoAdmin);
        Salon=(TextView) findViewById(R.id.textViewSalonReporteRecibidoAdmin);
        Descripcion=(TextView) findViewById(R.id.textViewDescripcionReporteRecibidoAdmin);

        CampoImagen=(ImageView) findViewById(R.id.imageViewReporteRecibidoAdmin);

        CampoImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //metodo para abrir la imagen
                CargarImagen();

            }
        });

        spinnerEmpleados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //obtener el item elegido
                    ObtenerId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SPINNERNivelAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //obtener el item elegido
                    Prioridad=values[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        BloquearPantalla();
    }

    private void ObtenerId(int position) {
        IdElegidoEmpleado=listaEmpleados.get(position).getIdEmpleado();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancelarReporteRecibidoAdmin){
            finish();
        }else if (v.getId() == R.id.btnGuardarReporteRecibidoAdmin){
            //Toast.makeText(ReporteRecibidoAdmin.this,String.valueOf(IdElegidoEmpleado).toString()+": "+Prioridad, Toast.LENGTH_LONG).show();
            //metodo para guardar
            CargarWebServiceGuardarDetalles();
        }
    }

    private void CargarWebServiceGuardarDetalles() {
        //barra de progreso
        progre= new ProgressDialog(this);
        progre.setMessage("Guardando por favor espere");
        progre.show();
        String url="http://"+direecionip()+"/ReportaTec/wsJSONRegistroDetallesReporte.php?IdReporte="+FolioRe+
                "&IdEmpleado="+IdElegidoEmpleado+
                "&NivelPrioridad="+Prioridad;
        url= url.replace(" ","%20");

        //enviarcelo a volley para que lo procese
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(ReporteRecibidoAdmin.this, "Reporte asigando satisfactoriamente", Toast.LENGTH_LONG).show();
                CargarWebServiceGuardarHistorialEstatus();
                CargarWebServiceGuardarEstatus();
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mostrar mensaje
                if (exiteConexionInternet()){
                    Toast.makeText(ReporteRecibidoAdmin.this, "Intente mas tarde", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ReporteRecibidoAdmin.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);
    }
    private void CargarWebServiceGuardarEstatus() {
        //barra de progreso
        progre= new ProgressDialog(this);
        progre.setMessage("Guardando por favor espere");
        progre.show();
        String url="http://"+direecionip()+"/ReportaTec/wsJSONRegistroReporteEstado.php?IdReporte="+FolioRe+
                "&TipoEstatus="+"Atendido";

        url= url.replace(" ","%20");

        //enviarcelo a volley para que lo procese
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mostrar mensaje
                Toast.makeText(ReporteRecibidoAdmin.this, "No se puede registar comprube su conexion a internet", Toast.LENGTH_LONG).show();

            }
        });
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);
    }
    private void CargarWebServiceGuardarHistorialEstatus() {
        String url="http://"+direecionip()+"/ReportaTec/wsJSONRegistroHistorialReporteEstatus.php?IdReporte="+FolioRe+
                "&TipoEstatus="+"Atendido";
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
                Toast.makeText(ReporteRecibidoAdmin.this, "No se puede conectar comprube su conexion a internet", Toast.LENGTH_LONG).show();

            }
        });
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);
    }

    public void CargarWebServiceDatosEmpleado(){
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarEmpleadoAdmin.php?";
        url= url.replace(" ","%20");
        //enviarcelo a volley para que lo procese
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ItemEmpleados NuevoItemEmpleado=null;
                JSONArray jsonArregloEmpleados = response.optJSONArray("empleadosAdm");
                try {
                for(int i=0;i<jsonArregloEmpleados.length();i++){
                    NuevoItemEmpleado = new ItemEmpleados();
                    JSONObject jsonObjetoEmpleados = null;
                    jsonObjetoEmpleados = jsonArregloEmpleados.getJSONObject(i);
                    NuevoItemEmpleado.setIdEmpleado(jsonObjetoEmpleados.optInt("IdEmpleado"));
                    NuevoItemEmpleado.setNumControlEmp(jsonObjetoEmpleados.optString("NumControlEmp"));
                    NuevoItemEmpleado.setNombreEmp(jsonObjetoEmpleados.optString("NombreEmp"));
                    NuevoItemEmpleado.setCorreoEmp(jsonObjetoEmpleados.optString("CorreoEmp"));
                    NuevoItemEmpleado.setUsuarioEmp(jsonObjetoEmpleados.optString("UsuarioEmp"));
                    NuevoItemEmpleado.setContraseña(jsonObjetoEmpleados.optString("Contrasena"));
                    NuevoItemEmpleado.setTipoPermiso(jsonObjetoEmpleados.optString("TipoPermiso"));
                    listaEmpleados.add(NuevoItemEmpleado);
                    NombreEmpleados.add(listaEmpleados.get(i).getNombreEmp().toString());

                }
                    //metodo para llenar el sipinner
                    llenarspinner(NombreEmpleados);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReporteRecibidoAdmin.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();

            }
        });
        //para establecer la comunicacion con los metodos de abajo
        reques.add(jsonObjectRequest);

    }

    private void llenarspinner(ArrayList<String> nombreEmpleados) {
        spinnerEmpleados.setAdapter(new ArrayAdapter<String>(ReporteRecibidoAdmin.this, android.R.layout.simple_list_item_1, nombreEmpleados));
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
