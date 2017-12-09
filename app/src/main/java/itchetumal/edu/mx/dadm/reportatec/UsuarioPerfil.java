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
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.view.View.OnClickListener;
import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsUsuarios;

public class UsuarioPerfil extends AppCompatActivity implements OnClickListener{
    //para ventana de progreso en caso que se tarde
    ProgressDialog progre;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //variable para almacenar el id del usuario
    int Idusuario;
    //declarar objetos para referenciarlos
    EditText NumeroControl,Nombres,Correo,Usuario,Contraseña;
    //stringreques para obtener el texto
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_usuario_perfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //se obtiene el id del archivo de preferencias
        SharedPreferences datosUsuario=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
        Idusuario= (datosUsuario.getInt("Id",0));
        //referenciar el objeto reques
        reques = Volley.newRequestQueue(UsuarioPerfil.this);
        //referenciar objetos
        NumeroControl= (EditText) findViewById(R.id.editTextNumCtrlUsuarioPerfil);
        Nombres= (EditText) findViewById(R.id.editTextNombresUsuarioPerfil);
        Correo= (EditText) findViewById(R.id.editTextCorreoUsuarioPerfil);
        Usuario= (EditText) findViewById(R.id.editTextUsuarioUsuarioPerfil);
        Contraseña= (EditText) findViewById(R.id.editTextContraseñaUsuarioPerfil);
        //referenciar boton actualizar
        View botonActualizar=findViewById(R.id.btnActualizarInformacionUsuarioPerfil);
        botonActualizar.setOnClickListener(this);
        //llamar al metodo que carga los datos del usuario
        CargarWebServiceUsua();
        //metodo para bloquear la pantalla
        BloquearPantalla();
    }

    //metodo para cargar el web service con los datos del usuario
    public void CargarWebServiceUsua(){
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarUsuarioPerfil.php?Usuari="+Idusuario;
        //remplazar espacios en blanco
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //crear objeto de la clase itemsusuarios
                ItemsUsuarios NuevoItemUsua = new ItemsUsuarios();
                //obtenemos la respuesta del php del json llamado usuario
                JSONArray jsonArreglo = response.optJSONArray("usuario");
                JSONObject jsonObjeto = null;

                try {
                    //obtenemos los campos de respuesta y se los asignamos al objeto
                    jsonObjeto = jsonArreglo.getJSONObject(0);
                    NuevoItemUsua.setIdUsua(jsonObjeto.optInt("IdUsuario"));
                    //se le asigna los datos a los campos
                    NumeroControl.setText(jsonObjeto.optString("NumControlUsua"));
                    Nombres.setText(jsonObjeto.optString("NombreUsua"));
                    Correo.setText(jsonObjeto.optString("CorreoUsua"));
                    Usuario.setText(jsonObjeto.optString("Usuario"));
                    Contraseña.setText(jsonObjeto.optString("Contrasena"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                //mostrar mensaje
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(UsuarioPerfil.this, "No se puede visualizar en este momento intente mas tarde", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(UsuarioPerfil.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);
    }
    //metodo para actualizar los datos del usuario
    private void webServiceActualizar() {
        //se mostrara un ventana para indicar que se esta intentando conectar
        progre=new ProgressDialog(this);
        //escribimos el mensaje
        progre.setMessage("Cargando...");
        //mostramos la ventana
        progre.show();
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONActualizarUsuario.php?";
        //inicializamos el stringrequest por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo post
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //ocultar ventana
                progre.hide();
                //si se la respuesta es actualiza entra
                if (response.trim().equalsIgnoreCase("actualiza")){

                    //guardar datos en un archivo de preferencias
                    SharedPreferences preferencias=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferencias.edit();
                    editor.putString("TipoUs", "Usuario");
                    editor.putInt("Id",Idusuario );
                    editor.putString("NumControl", NumeroControl.getText().toString());
                    editor.putString("Nombre", Nombres.getText().toString());
                    editor.putString("Correo", Correo.getText().toString());
                    editor.putString("Usuario", Usuario.getText().toString());
                    editor.putString("Contrasena", Contraseña.getText().toString());
                    editor.commit();
                    Toast.makeText(UsuarioPerfil.this,"Se ha Actualizado con exito",Toast.LENGTH_SHORT).show();
                    //finalizar actividad
                    finish();

                }else{
                    //e caso contrario mostrar
                    Toast.makeText(UsuarioPerfil.this,"No se ha actualizado ",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //ocultar ventana
                progre.hide();
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(UsuarioPerfil.this, "No se pudo actualizar", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(UsuarioPerfil.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //declrara e inicilizar los datos que se enviaran al php por medio del metodo post
                //asignarle los datos qu estan en los campos de texto
                String NumControlUsua=NumeroControl.getText().toString();
                String NombreUsua=Nombres.getText().toString();
                String CorreoUsua=Correo.getText().toString();
                String Usuario1=Usuario.getText().toString();
                String Contrasena=Contraseña.getText().toString();
                //enviarle los datos al php con el nombre de los campos
                Map<String,String> parametros=new HashMap<>();
                parametros.put("IdUsuario",String.valueOf(Idusuario));
                parametros.put("NumControlUsua",NumControlUsua);
                parametros.put("NombreUsua",NombreUsua);
                parametros.put("CorreoUsua",CorreoUsua);
                parametros.put("Usuario",Usuario1);
                parametros.put("Contrasena",Contrasena);
                //retornar los datos
                return parametros;

            }
        };
        //para establecer la comunicacion con los metodos
        reques.add(stringRequest);

    }

    //para regresar con el boton atras y par las configuraciones
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if (item.getItemId()==R.id.action_settings) {
            //si se selcciona la opcion se configuracion se inicaia la actividad de configuraciones
            //1 Crear actividad para llamar a la segunda actividad
            Intent Preferencia = new Intent(UsuarioPerfil.this, configuraciones.class);
            //3 Iniciar actividad
            startActivity(Preferencia);

        }
        return super.onOptionsItemSelected(item);
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
    //metodo para obtener la ip ingresada
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        return pref.getString("ip_servidor","");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnActualizarInformacionUsuarioPerfil){
            webServiceActualizar();

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
