package itchetumal.edu.mx.dadm.reportatec;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemEmpleados;
import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsUsuarios;

public class Login extends AppCompatActivity  {
    //declarar variables para referenciar los comeponentes
    EditText Usuario,Contrase;
    Button Iniciarsesion;
    Button registro;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //para ventana de progreso en caso que se tarde
    ProgressDialog progre;

    //variable para saber a cual tabla consultar
    int TablaConsultar;

    //url web service 00web
    // final String UrlGlo="https://reportatec.000webhostapp.com/ReportaTec/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //al iniciar la aplicacion, validar si el usuario ya se ha registrado
        //obtenemos los datos del archivo de preferencias
        SharedPreferences datosUsuario=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
        //obtenemos el tipo de usuario
        String usuario= (datosUsuario.getString("Usuario",""));
        //obtenemos las contraseña
        String Contraseña= (datosUsuario.getString("Contrasena",""));
        //si estos campos contienen informacion entra
        if (usuario!=""&&Contraseña!=""){
            //se llama a la acatividad principal
            //1 Crear actividad para llamar a la segunda actividad
            Intent intencionMenu = new Intent(Login.this, MainActivity.class);
            //2 Iniciar actividad
            startActivity(intencionMenu);
            //cerrar esta actividad
            finish();
        }else {
            //en caso de que no contengan informacion se muestra la pantalla de inicio de sesion
            setContentView(R.layout.activity_login);
            //orientacion de pantalla bloqueada
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //inicializar botones y referenciarlos
            Iniciarsesion = (Button) findViewById(R.id.btnsesion);
            registro = (Button) findViewById(R.id.btnReg);
            //inicializar edittext
            Usuario = (EditText) findViewById(R.id.usu);
            Contrase = (EditText) findViewById(R.id.clave);
            //referenciar el objeto reques
            reques = Volley.newRequestQueue(Login.this);
            //esta variabla se inicializara con cero
            TablaConsultar = 0;
            //metodo para saber si se presiono el boton iniciar sesion, contiene un escuchardor
            Iniciarsesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //se llamara al metodo que buscara en que tabla esta ese regsitro
                    buscando();
                }
            });
            //metodo para saber si se presiono el boton iniciar sesion, contiene un escuchardor
            registro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //se mostara la actividad para registrar al usuario
                    Intent intento = new Intent(Login.this, Registrar.class);
                    startActivity(intento);
                }
            });
        }
    }
    //para mostar el menu de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //para saber si se presiono el menu con las opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //si se presiona el menu con tres puntos entra
        if (item.getItemId() == R.id.action_settings) {
            //se habre la actividad de configuraciones para las preferencias
            //1 Crear actividad para llamar a la segunda actividad
            Intent Preferencia = new Intent(Login.this, configuraciones.class);
            //3 Iniciar actividad
            startActivity(Preferencia);
        }
        return super.onOptionsItemSelected(item);
    }
    //metodo que dependiendo del numero sera el webservice que cargara
    public void TablasAConsultar(int n){
        if (n==1){
            //si es 1 se cargara el empleado para que inicie sesion
            CargarWebServiceEmp();
        }
        else if (n==2){
            //si es 2 se cargara el usuario para que inicie sesion
            CargarWebServiceUsua();
        }else if (n==3){
            //si es 3 indica que no existe ese usuario en ninguna tabla
            Toast.makeText(Login.this, "Usario o contraseña incorrecto", Toast.LENGTH_LONG).show();
        }
    }


    //cargar web service para determinar cual es la tabla
    public void buscando(){
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/ConsultarTablas.php?Usuari="+Usuario.getText().toString()
                +"&Contrase="+Contrase.getText().toString();
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            //si todo sale bien y hay una respuesta entra aqui
            @Override
            public void onResponse(JSONObject response) {
                //obtenemos la respuesta del json llamado buscado y se lo asignamos al arreglo json
                JSONArray jsonArregloTemp = response.optJSONArray("buscado");
                JSONObject jsonObjetoTemp = null;
                //intentamos acceder a los datos que envio el json
                try {
                    //obtenemos los datos del primer registro
                    jsonObjetoTemp = jsonArregloTemp.getJSONObject(0);
                    //llamamos al metodo tablas a consultar y le enviamos la respuesta del json para saber a cual tabla pertenece el usuario que desea ingresar
                    TablasAConsultar(jsonObjetoTemp.getInt("respuesta"));

                } catch (JSONException e) {
                    //capturamos en caso de ocurra un error
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            //si hay algun error entra aqui
            @Override
            public void onErrorResponse(VolleyError error) {
                //mostrar mensaje de que ocurrio un error
                Toast.makeText(Login.this, "No se puede conectar ", Toast.LENGTH_LONG).show();

            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);
    }


    //metodo para cargar el web service que iniciara sesion de los usuarios
    public void CargarWebServiceUsua(){
        //se mostrara un ventana para indicar que se esta intentando conectar
        progre= new ProgressDialog(this);
        //escribimos el mensaje
        progre.setMessage("Iniciando sesion");
        //mostramos la ventana
        progre.show();
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarUsuario.php?Usuari="+Usuario.getText().toString()
                +"&Contrase="+Contrase.getText().toString();
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");

        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //si todo sale bien, entra y ocultamos la ventana
                progre.hide();
                //crear objeto de la clase itemsusuarios para almacenar los datos
                ItemsUsuarios NuevoItemUsua = new ItemsUsuarios();
                //obtenemos la respuesta del php del json llamado usuario
                JSONArray jsonArreglo = response.optJSONArray("usuario");
                //inicializamos el objeto
                JSONObject jsonObjeto = null;
                //intentamos acceder a los datos que envio el json
                try {
                    //obtenemos el primer registro del resultado
                    jsonObjeto = jsonArreglo.getJSONObject(0);
                    //obtenemos los campos de respuesta y se los asignamos al objeto nuevoitemusua
                    NuevoItemUsua.setIdUsua(jsonObjeto.optInt("IdUsuario"));
                    NuevoItemUsua.setNumControlUsu(jsonObjeto.optString("NumControlUsua"));
                    NuevoItemUsua.setNombreUsua(jsonObjeto.optString("NombreUsua"));
                    NuevoItemUsua.setCorreoUsu(jsonObjeto.optString("CorreoUsua"));
                    NuevoItemUsua.setUsuari(jsonObjeto.optString("Usuario"));
                    NuevoItemUsua.setContrase(jsonObjeto.optString("Contrasena"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //guardar datos en un archivo de preferencias
                SharedPreferences preferencias=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferencias.edit();
                editor.putString("TipoUs", "Usuario");
                editor.putInt("Id", NuevoItemUsua.getIdUsua());
                editor.putString("NumControl", NuevoItemUsua.getNombreUsua());
                editor.putString("Nombre", NuevoItemUsua.getNombreUsua());
                editor.putString("Correo", NuevoItemUsua.getCorreoUsu());
                editor.putString("Usuario", NuevoItemUsua.getUsuari());
                editor.putString("Contrasena", NuevoItemUsua.getContrase());
                editor.commit();
                //abrimos el menu principal
                //1 Crear actividad para llamar a la segunda actividad
                Intent intencionMenu = new Intent(Login.this, MainActivity.class);
                //2 Iniciar actividad
                startActivity(intencionMenu);
                //cerrar esta actividad
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //si hay un error ocultamos la ventana
                progre.hide();
                //mostrar mensaje de error
                Toast.makeText(Login.this, "No se puede conectar ", Toast.LENGTH_LONG).show();

            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);
    }

    //metodo para cargar el web service del empleado
    public void CargarWebServiceEmp(){
        //se mostrara un ventana para indicar que se esta intentando conectar
        progre= new ProgressDialog(this);
        //escribimos el mensaje
        progre.setMessage("Iniciando sesion");
        //mostramos la ventana
        progre.show();
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarEmpleado.php?Usuari="+Usuario.getText().toString()
                +"&Contrase="+Contrase.getText().toString();
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //ocultar barra
                progre.hide();
                //crear objeto de la clase itemempelado
                ItemEmpleados NuevoItemEmp = new ItemEmpleados();
                //obtenemos la respuesta del php del json llamado empleado
                JSONArray jsonArregloEmp = response.optJSONArray("empleado");
                //inicializamos el objeto
                JSONObject jsonObjetoEmp = null;
                //intentamos acceder a los datos que envio el json
                try {
                    //obtenemos los campos de respuesta y se los asignamos al objeto
                    jsonObjetoEmp = jsonArregloEmp.getJSONObject(0);
                    NuevoItemEmp.setIdEmpleado(jsonObjetoEmp.optInt("IdEmpleado"));
                    NuevoItemEmp.setNumControlEmp(jsonObjetoEmp.optString("NumControlEmp"));
                    NuevoItemEmp.setNombreEmp(jsonObjetoEmp.optString("NombreEmp"));
                    NuevoItemEmp.setCorreoEmp(jsonObjetoEmp.optString("CorreoEmp"));
                    NuevoItemEmp.setUsuarioEmp(jsonObjetoEmp.optString("UsuarioEmp"));
                    NuevoItemEmp.setContraseña(jsonObjetoEmp.optString("Contrasena"));
                    NuevoItemEmp.setTipoPermiso(jsonObjetoEmp.optString("TipoPermiso"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //guardar datos en un archivo de preferencias
                SharedPreferences preferencias=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferencias.edit();
                editor.putString("TipoUs", "Empleado");
                editor.putInt("Id", NuevoItemEmp.getIdEmpleado());
                editor.putString("NumControl", NuevoItemEmp.getNumControlEmp());
                editor.putString("Nombre", NuevoItemEmp.getNombreEmp());
                editor.putString("Correo", NuevoItemEmp.getCorreoEmp());
                editor.putString("Usuario", NuevoItemEmp.getUsuarioEmp());
                editor.putString("Contrasena", NuevoItemEmp.getContraseña());
                editor.putString("TipoPermiso", NuevoItemEmp.getTipoPermiso());
                editor.commit();
                //abrimos el menu principal
                //1 Crear actividad para llamar a la segunda actividad
                Intent intencionMenu = new Intent(Login.this, MainActivity.class);
                //2 Iniciar actividad
                startActivity(intencionMenu);
                //cerrar esta actividad
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //si hay un error ocultamos la ventana
                progre.hide();
                //mostrar mensaje de error
                Toast.makeText(Login.this, "No se puede conectar ", Toast.LENGTH_LONG).show();

            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);
    }
    //metodo para obtener la ip ingresada en el archivo de preferencias
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        //regresar lo que este en ese campo
        return pref.getString("ip_servidor","");
    }

}