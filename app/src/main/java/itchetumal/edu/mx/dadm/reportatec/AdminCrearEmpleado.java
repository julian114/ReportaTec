package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemEmpleados;
import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsUsuarios;

public class AdminCrearEmpleado extends AppCompatActivity implements OnCheckedChangeListener,OnClickListener {
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //declara componentes  para referenciarlos
    EditText IdIngre,NumConIngre,NombreIngre,CorreoIngre,UsuarioIngre,ContraseIngre;
    //variable donde se almacenara el tipo de permiso
    String TipoPermiso;
    //declara un objeto de tipo stringrequest para que nos retorne el texto
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crear_empleado);
        //para mostar el boton regresar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //referenciar el objeto reques
        reques = Volley.newRequestQueue(AdminCrearEmpleado.this);
        //declarar y refrenciar el radio grouppara acceder a el
        RadioGroup RgTipoEmp= (RadioGroup) findViewById(R.id.radioGr);
        RgTipoEmp.setOnCheckedChangeListener(this);
        //declarar y referenciar el boton buscar
        View botonBuscar=findViewById(R.id.btnBuscarAdminCrearEmpleados);
        botonBuscar.setOnClickListener(this);
        //declarar y referenciar el boton atualizar
        View botonActualizar=findViewById(R.id.btnActualizarAdminCrearEmpleados);
        botonActualizar.setOnClickListener(this);
        //declarar y referenciar el boton eliminar
        View botonElimnar=findViewById(R.id.btnEliminarAdminCrearEmpleados);
        botonElimnar.setOnClickListener(this);
        //declarar y referenciar el boton crear
        View botonCrear=findViewById(R.id.btnEnviarReporte0Limpieza);
        botonCrear.setOnClickListener(this);
        //referenciar los componenetes
        IdIngre= (EditText) findViewById(R.id.editTextIdAdminCrearEmpleados);
        NumConIngre= (EditText) findViewById(R.id.editTextNumCtrlAdminCrearEmpleados);
        NombreIngre= (EditText) findViewById(R.id.editTextNombresAdminCrearEmpleados);
        CorreoIngre= (EditText) findViewById(R.id.editTextCorreoAdminCrearEmpleados);
        UsuarioIngre=(EditText) findViewById(R.id.editTextUsuarioAdminCrearEmpleados);
        ContraseIngre=(EditText) findViewById(R.id.editTextContraseñaAdminCrearEmpleados);
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
    //metodo para obtener el valor de la orientacion del archivo de preferencias
    public boolean valorpantalla(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        return pref.getBoolean("bloquear",false);
    }
    //metodo para saber que radio button fue seleccionado
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        //refernciar el radiogroup
        RadioGroup RgTipoEmp= (RadioGroup) findViewById(R.id.radioGr);
        //si se selecciona el radio group entra
        if (group==RgTipoEmp){
            //si se selecciono la opcion jefe entra
            if (checkedId==R.id.radioButtonJefeAdminCrearEmpleados){
                //la variable tipopermiso obtiene el valor de jefe de departamento
                TipoPermiso="Jefe Departamento";

            }else if (checkedId==R.id.radioButtonSubjefeAdminCrearEmpleados){
                //si se selecciono la opcion subjefe entra
                //la variable tipopermiso obtiene el valor de subjefe
                TipoPermiso="SubJefe";
            }else if (checkedId==R.id.radioButtonEmpleadoAdminCrearEmpleados){
                //si se selecciono la opcion empleado entra
                //la variable tipopermiso obtiene el valor de empleado normal
                TipoPermiso="Empleado normal";
            }

        }

    }
    //metodo para saber cual boton se esta presionando
    @Override
    public void onClick(View v) {
        //si es buscar se llama al metodo cargar datos del empleado
        if (v.getId() == R.id.btnBuscarAdminCrearEmpleados){
            CargarWebServiceDatosEmpleado();
        }else if (v.getId() == R.id.btnActualizarAdminCrearEmpleados){
            //si es actualizar se llama al metodo que actualizara los datos del empleado
            webServiceActualizarEmpleado();
        }else if (v.getId() == R.id.btnEliminarAdminCrearEmpleados){
            //si es eliminar se llama al metodo que primero preguntara si se desea eliminar ese empleado
            Preguntar();
        }else if (v.getId() == R.id.btnEnviarReporte0Limpieza){
            //si es enviar se llama al metodo guardar ese empleado
            CargarWebServiceEnviarDatosEmpleado();
        }

    }
    //metodo para preguntar se si desea eliminar ese empleado
    private void Preguntar() {
        //arreglo de opciones
        final CharSequence[] opciones={"si","no"};
        //craer dialogo de alerta
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(AdminCrearEmpleado.this);
        //mensaje que se deasea mostrar
        alertOpciones.setTitle("¿Realmente desea eliminar al empleado :"+NombreIngre.getText().toString()+" ?");
        //mostar mensaje
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //si se selecciona la opcion si llamar al metodo que eliminara al empleado
                if (opciones[i].equals("si")){
                    CargarWebServiceEliminarsEmpleado();
                }else{
                    //si es no se mostrar el mensaje se empleado eliminado
                    Toast.makeText(AdminCrearEmpleado.this,"empleado no eliminado",Toast.LENGTH_SHORT).show();
                    //Se cierra el dialogo
                    dialogInterface.dismiss();
                }
            }
        });
        //para mostar el dialogo
        alertOpciones.show();
    }

    //metodo para actualizar los datos del empleado
    private void webServiceActualizarEmpleado() {
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONActualizarEmpleadosAdminCrud.php?";
        //inicializamos el stringrequest por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo post
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //si la respuesta de tipo string es actualiza entra
                if (response.trim().equalsIgnoreCase("actualiza")){
                    //si el php devuelve la palabra actualiza indica que si se actualizo satisfactoriamente
                    //mostrar el mensaje
                    Toast.makeText(AdminCrearEmpleado.this,"Actualizado con exito ",Toast.LENGTH_SHORT).show();
                    //limpiar campos
                    IdIngre.setText("");
                    NumConIngre.setText("");
                    NombreIngre.setText("");
                    CorreoIngre.setText("");
                    UsuarioIngre.setText("");
                    ContraseIngre.setText("");
                    TipoPermiso="";
                }else{
                    //en caso que no sea actualiza mostrar el mensaje que no se actualizo
                    Toast.makeText(AdminCrearEmpleado.this,"No se ha actualizado ",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede actualizar en este momento", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //declrara e inicilizar los datos que se enviaran al php por medio del metodo post
                //asignarle los datos qu estan en los campos de texto
                String IdEmpleado=IdIngre.getText().toString();
                String NumControlEmp=NumConIngre.getText().toString();
                String NombreEmp=NombreIngre.getText().toString();
                String CorreoEmp=CorreoIngre.getText().toString();
                String UsuarioEmp=UsuarioIngre.getText().toString();
                String Contrasena=ContraseIngre.getText().toString();
                //enviarle los datos al php con el nombre de los campos
                Map<String,String> parametros=new HashMap<>();
                parametros.put("IdEmpleado",IdEmpleado);
                parametros.put("NumControlEmp",NumControlEmp);
                parametros.put("NombreEmp",NombreEmp);
                parametros.put("CorreoEmp",CorreoEmp);
                parametros.put("UsuarioEmp",UsuarioEmp);
                parametros.put("Contrasena",Contrasena);
                parametros.put("TipoPermiso",TipoPermiso);
                //retornar los datos
                return parametros;

            }
        };
        //para establecer la comunicacion con los metodos
        reques.add(stringRequest);

    }

    //metodo para eliminar al empleado
    public void CargarWebServiceEliminarsEmpleado(){
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONEliminarEmpleadosAdminCrud.php?IdEmpleado="+IdIngre.getText().toString();
        //inicializamos el stringrequest por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo post
        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equalsIgnoreCase("elimina")){
                    //si la respuesta de tipo string es elimina entra
                    //mostrar el mensaje que se elimino correctamente
                    Toast.makeText(AdminCrearEmpleado.this, "Empleado eliminado", Toast.LENGTH_LONG).show();
                    //limpiar campos
                    NumConIngre.setText("");
                    NombreIngre.setText("");
                    CorreoIngre.setText("");
                    UsuarioIngre.setText("");
                    ContraseIngre.setText("");
                    TipoPermiso="";

                }else if (response.trim().equalsIgnoreCase("noExiste")){
                    //si regresa no existe es que no se encontro ese registro
                    Toast.makeText(AdminCrearEmpleado.this, "No existe un empleado con ese id", Toast.LENGTH_LONG).show();

                }else if(response.trim().equalsIgnoreCase("noElimina")){
                    //si regresa noElimina es que no se pudo eliminar
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede eliminar", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede eliminar en este momento", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        reques.add(stringRequest);

    }

    //metodo que guarda los datos del empleado
    public void CargarWebServiceEnviarDatosEmpleado(){
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONRegistroEmpleadosAdminCrud.php?NumControlEmp="+NumConIngre.getText().toString()
                +"&NombreEmp="+NombreIngre.getText().toString()
                +"&CorreoEmp="+CorreoIngre.getText().toString()
                +"&UsuarioEmp="+UsuarioIngre.getText().toString()
                +"&Contrasena="+ContraseIngre.getText().toString()
                +"&TipoPermiso="+TipoPermiso;
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //si todo salio bien se muesta el mensaje
                Toast.makeText(AdminCrearEmpleado.this, "Empleado agregado con exito", Toast.LENGTH_LONG).show();
                //limpiar campos
                NumConIngre.setText("");
                NombreIngre.setText("");
                CorreoIngre.setText("");
                UsuarioIngre.setText("");
                ContraseIngre.setText("");
                TipoPermiso="";
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede registar en este momento", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);

    }

    //metodo que busca al empleado por id o numero de control
    public void CargarWebServiceDatosEmpleado(){
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarEmpleadoAdminCrud.php?IdEmp="+IdIngre.getText().toString()+
                "&NumCon="+NumConIngre.getText().toString();
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //obtenemos la respuesta del php del json llamado empleadosAdmCrud
                JSONArray jsonArregloEmpleados = response.optJSONArray("empleadosAdmCrud");
                try {
                    JSONObject jsonObjetoEmpleados = null;
                    //obtenemos los campos de respuesta y se los asignamos al objeto
                    jsonObjetoEmpleados = jsonArregloEmpleados.getJSONObject(0);
                    //mostramos los datos obtenido en los campos de texto
                    IdIngre.setText(jsonObjetoEmpleados.optString("IdEmpleado"));
                    NumConIngre.setText(jsonObjetoEmpleados.optString("NumControlEmp"));
                    NombreIngre.setText(jsonObjetoEmpleados.optString("NombreEmp"));
                    CorreoIngre.setText(jsonObjetoEmpleados.optString("CorreoEmp"));
                    UsuarioIngre.setText(jsonObjetoEmpleados.optString("UsuarioEmp"));
                    ContraseIngre.setText(jsonObjetoEmpleados.optString("Contrasena"));
                    //refrenciamos el radiogroup para acceder a el
                    RadioGroup contenedor = (RadioGroup) findViewById(R.id.radioGr);
                    //depeniendo de la opcion sera el que se seleccionara
                    if (jsonObjetoEmpleados.optString("TipoPermiso").equalsIgnoreCase("Jefe Departamento")){
                        //se selecciona la opcion de jefe
                        RadioButton opcionJefe = (RadioButton) contenedor.getChildAt(0);
                        contenedor.check(opcionJefe.getId());
                    }else if (jsonObjetoEmpleados.optString("TipoPermiso").equalsIgnoreCase("SubJefe")){
                        //se selecciona la opcion de subjefe
                        RadioButton opcionSubJefe = (RadioButton) contenedor.getChildAt(1);
                        contenedor.check(opcionSubJefe.getId());
                    }else if (jsonObjetoEmpleados.optString("TipoPermiso").equalsIgnoreCase("Empleado normal")){
                        //se selecciona la opcion de empleado normal
                        RadioButton opcionEmpleado = (RadioButton) contenedor.getChildAt(2);
                        contenedor.check(opcionEmpleado.getId());
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
                    Toast.makeText(AdminCrearEmpleado.this, "No existe ese registro", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(AdminCrearEmpleado.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);

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
