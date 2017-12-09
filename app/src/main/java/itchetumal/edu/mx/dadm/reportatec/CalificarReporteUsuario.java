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
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CalificarReporteUsuario extends AppCompatActivity implements View.OnClickListener{
    //para ventana de progreso en caso que se tarde
    ProgressDialog progre;
    //declara un objeto de tipo stringrequest para que nos retorne el texto
    StringRequest stringRequest;
    //para establecer la conexion con el web service
    RequestQueue reques;
    //declara componentes  para referenciarlos
    TextView folio,tipoReporte;
    EditText comentariosSatis;
    String calificacionSatis;
    //variables para obtener los datos que se le envian
    String FolioRe;
    String TipoRe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //recuperar la intencion que la llamo
        Intent intencionLlamo=getIntent();
        //recuperar los datos recibidos
        Bundle bundle =intencionLlamo.getExtras();
        FolioRe= bundle.getString("FolioEn");
        TipoRe= bundle.getString("TipoRepEn");

        setContentView(R.layout.activity_calificar_reporte_usuario);

        //crear el boton atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //referenciar el objeto reques
        reques = Volley.newRequestQueue(CalificarReporteUsuario.this);
        //declarar y referenciar el boton cancelar
        View btnCance = findViewById(R.id.BtnCancelarReporteInfraestructuraElectricaYElectronica);
        btnCance.setOnClickListener((View.OnClickListener) this);
        //declarar y referenciar el boton enviar
        View btnEnvi = findViewById(R.id.btnEnviarReporteInfraestructuraElectricaYElectronica);
        btnEnvi.setOnClickListener((View.OnClickListener) this);
        //refreenciar los campos para modificarlos depues
        folio=(TextView) findViewById(R.id.textViewFolioIDReporteInfraestructuraElectricaYElectronica);
        tipoReporte=(TextView) findViewById(R.id.textViewNombreTipoReporteInfraestructuraElectricaYElectronica);
        comentariosSatis=(EditText)findViewById(R.id.editTextComentariosServicio);
        //declarar y referenciar el ratingbar para mostar la calificacion en forma de estrellas
        RatingBar calificar =(RatingBar) findViewById(R.id.calificacion);
        //mostrar solo 5 estrellas
        calificar.setNumStars(5);
        //asignar un metodo que escuche
        calificar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //almacenar la calificacion
                calificacionSatis =String.valueOf(rating);
            }
        });
        //mostar los campos obtenido en los campos
        folio.setText(FolioRe);
        tipoReporte.setText(TipoRe);
        //llamar al metodo que bloqueara la pantalla segun la opcion del archivo de preferencias
        BloquearPantalla();
    }

    //para regresar con el boton atras
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //al precionar el boton finalizara esta actividad
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //metodo para saber cual boton se esta presionando
    @Override
    public void onClick(View v) {
        //si es el boton cancelar se finalizara la actividad
        if (v.getId() == R.id.BtnCancelarReporteInfraestructuraElectricaYElectronica){
            finish();
        }else if (v.getId() == R.id.btnEnviarReporteInfraestructuraElectricaYElectronica){
            //si es el boton enviar se llamara al metod que actualizara el reporte para calificarlo
            webServiceActualizar();
        }

    }

    //metodo par actualizar los campos del reporte que no han sido llenados
    private void webServiceActualizar() {
        //se mostrara un ventana para indicar que se esta intentando conectar
        progre=new ProgressDialog(this);
        //escribimos el mensaje
        progre.setMessage("Cargando...");
        //mostramos la ventana
        progre.show();
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONCalificaReporte.php?";
        //inicializamos el stringrequest por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo post
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //si es satisfactorio se oculta la ventana
                progre.hide();
                //si la respuesta de tipo string es actualiza entra
                if (response.trim().equalsIgnoreCase("actualiza")){
                    //mostramos el mensaje
                    Toast.makeText(CalificarReporteUsuario.this,"Se ha Guardado con exito",Toast.LENGTH_SHORT).show();
                    //se finaliza la actividad
                    finish();
                }else{
                    //en caso de no se resivir el mensaje mostar el mensaje
                    Toast.makeText(CalificarReporteUsuario.this,"No se ha Guardado ",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de un error ocultar la ventana
                progre.hide();
                //llamar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(CalificarReporteUsuario.this, "No se puede calificar en este momento", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(CalificarReporteUsuario.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //declrara e inicilizar los datos que se enviaran al php por medio del metodo post
                //asignarle los datos qu estan en los campos
                String IdReporte=folio.getText().toString();
                String Calificacion=calificacionSatis;
                String ComentarioSatis=comentariosSatis.getText().toString();
                //eenviarle los datos al php con el nombre de los campos
                Map<String,String> parametros=new HashMap<>();
                parametros.put("IdReporte",IdReporte);
                parametros.put("Calificacion",Calificacion);
                parametros.put("ComentarioSatis",ComentarioSatis);
                //retornar los datos
                return parametros;

            }
        };
        //para establecer la comunicacion con los metodos
        reques.add(stringRequest);

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
    //metodo para obtener la ip ingresada
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        return pref.getString("ip_servidor","");
    }
    //metodo para saber el estado del internet
    public boolean exiteConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
