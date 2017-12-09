package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;

public class HistorialEstatus extends AppCompatActivity {
    //declrara objetos para referencarlos
    ListView lis;
    //cadena paera almacenar el folio que le enviaron
    String FolioRe;
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //arraylist para guardar los datos obtenido del servidor
    ArrayList<String> HistorialRep = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_estatus);
        //referenciar el objeto reques
        reques = Volley.newRequestQueue(HistorialEstatus.this);
        //crear el boton atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //recuperar el id del reporte
        //recuperar la intencion que la llamo
        Intent intencionLlamo=getIntent();
        //recuperar los datos recibidos
        Bundle bundle =intencionLlamo.getExtras();
        FolioRe= bundle.getString("Idreporte");
        //llamar al metodo que carga los datos
       CargarWebServiceHistorial();
    }

    //metodo que carga los datos del historial de los datos
    public void CargarWebServiceHistorial(){
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarHistorialEstatus.php?IdRep="+FolioRe;
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //obtenemos la respuesta del json llamado historialRep y se lo asignamos al arreglo json
                JSONArray jsonArregloHistorialReportes = response.optJSONArray("historialRep");
                try {
                    //recorre todos los registros obtenidos
                    for(int i=0;i<jsonArregloHistorialReportes.length();i++){
                        //craer objeto json e inicializarlo con null para qie se reinice con cada registro
                        JSONObject jsonObjetoReportesHistorial = null;
                        //obtenemos los datos de cada registro
                        jsonObjetoReportesHistorial = jsonArregloHistorialReportes.getJSONObject(i);
                        //condatenar datos para mostrarlos en el listview
                        String cadena ="Estatus: "+jsonObjetoReportesHistorial.optString("TipoEstatus")+
                                "\nFecha: "+jsonObjetoReportesHistorial.optString("FechaEstatus");
                        HistorialRep.add(cadena);
                    }
                    //referenciar lista
                   lis = (ListView)findViewById(R.id.lista);
                    //asignarle los datos al list view
                    ArrayAdapter arr= new ArrayAdapter(HistorialEstatus.this,android.R.layout.simple_expandable_list_item_1,HistorialRep);
                    //mostrar los datos
                    lis.setAdapter(arr);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(HistorialEstatus.this, "No hay registros", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(HistorialEstatus.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
