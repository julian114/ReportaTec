package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;

import itchetumal.edu.mx.dadm.reportatec.Entidades.ItemsReportes;


public class Fragmento2MenuAdminSolucionados extends ListFragment {
    //para establecer la conexion con el web service
    RequestQueue reques;
    JsonObjectRequest jsonObjectRequest;
    //arreglo que contiene la imagenes para cada tipo de reporte
    int []imagenes = {R.drawable.reporareasverdes, R.drawable.reporelectricidad, R.drawable.reporinfraestructura,
            R.drawable.reporlimpieza,R.drawable.repormobilario,R.drawable.reporsanitario,R.drawable.itch};
    //arraylist para guardar los datos obtenido del servidor
    ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
    //adaptador
    SimpleAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //referenciar el objeto reques
        reques = Volley.newRequestQueue(getContext());

        //llamar al metodo que trae los datos
        CargarWebService();
        return super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        //     return inflater.inflate(R.layout.fragment_fragmento_lista_imagenes, container, false);


    }
    //metodo para saber cualo item se eligio
    @Override
    public void onStart(){
        // TODO Auto-generated method stub
        super.onStart();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                // TODO Auto-generated method stub
                //abrir detalles del reporte
                Intent ReporteTerminado = new Intent(getActivity(), ReporteTerminadoAdmin.class);
                //2 Envio de datos
                ReporteTerminado.putExtra("FolioEn",data.get(pos).get("FOLIO"));
                ReporteTerminado.putExtra("PrioridadEn",data.get(pos).get("Prioridad"));
                ReporteTerminado.putExtra("EstadoEn",data.get(pos).get("Estados"));
                ReporteTerminado.putExtra("ComentariosEn",data.get(pos).get("comentarios"));
                ReporteTerminado.putExtra("NombreEmpEn",data.get(pos).get("NombreEmp"));
                startActivity(ReporteTerminado);
            }
        });
    }

    //metodo que carga los datos del servidor de los reportes
    public void CargarWebService(){
        //creamos la url del archivo php al que accedera
        String url="http://"+direecionip()+"/ReportaTec/wsJSONConsultarReportesAdminResueltos.php?";
        //eliminamos los espacios por %20
        url= url.replace(" ","%20");
        //inicializamos el jsonobject por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo get
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //MAP para almacenar los datos
                HashMap<String, String> map=new HashMap<String, String>();
                //crear un objeto de itemsreportes
                ItemsReportes NuevoItemReportes=null;
                //obtenemos la respuesta del json llamado reportesAdm y se lo asignamos al arreglo json
                JSONArray jsonArregloReportes = response.optJSONArray("reportesAdm");

                 //intentamos acceder a los datos que envio el json
                try {
                    //recorre todos los registros obtenidos
                    for(int i=0;i<jsonArregloReportes.length();i++){
                        NuevoItemReportes = new ItemsReportes();
                        JSONObject jsonObjetoReportes = null;
                        //obtenemos los datos de cada registro
                        jsonObjetoReportes = jsonArregloReportes.getJSONObject(i);
                        NuevoItemReportes.setIdReporte(jsonObjetoReportes.optInt("IdReporte"));
                        NuevoItemReportes.setTipoReporte(jsonObjetoReportes.optString("TipoReporte"));
                        NuevoItemReportes.setProblema(jsonObjetoReportes.optString("Problema"));
                        NuevoItemReportes.setEdificio(jsonObjetoReportes.optString("Edificio"));
                        NuevoItemReportes.setDescripcion(jsonObjetoReportes.optString("Descripcion"));
                        NuevoItemReportes.setFecha(jsonObjetoReportes.optString("Fecha"));
                        NuevoItemReportes.setDato(jsonObjetoReportes.optString("Foto"));
                        NuevoItemReportes.setIdUsuario(jsonObjetoReportes.optInt("IdUsuario"));
                         //inicilizar objeto map
                        map=new HashMap<String, String>();
                        //asignarle los datos y un nombre
                        map.put("FOLIO", NuevoItemReportes.getIdReporte().toString());
                        map.put("Fechas",NuevoItemReportes.getFecha().toString() );
                        map.put("Estados", jsonObjetoReportes.optString("TipoEstatus"));
                        map.put("Prioridad", jsonObjetoReportes.optString("NivelPrioridad"));
                        //mostrar imagen segun el tipo
                        if (NuevoItemReportes.getTipoReporte().toString().equalsIgnoreCase("Areas Verdes")){
                            map.put("Image", Integer.toString(imagenes[0]));
                        }else if (NuevoItemReportes.getTipoReporte().toString().equalsIgnoreCase("Electrico y electronico")){
                            map.put("Image", Integer.toString(imagenes[1]));
                        }else if (NuevoItemReportes.getTipoReporte().toString().equalsIgnoreCase("Infraestructura general")){
                            map.put("Image", Integer.toString(imagenes[2]));
                        }else if (NuevoItemReportes.getTipoReporte().toString().equalsIgnoreCase("Limpieza")){
                            map.put("Image", Integer.toString(imagenes[3]));
                        }else if (NuevoItemReportes.getTipoReporte().toString().equalsIgnoreCase("Mobiliario")){
                            map.put("Image", Integer.toString(imagenes[4]));
                        }else if (NuevoItemReportes.getTipoReporte().toString().equalsIgnoreCase("Sanitarios")){
                            map.put("Image", Integer.toString(imagenes[5]));
                        }
                        map.put("comentarios", jsonObjetoReportes.optString("Comentario"));
                        map.put("NombreEmp", jsonObjetoReportes.optString("NombreEmp"));
                        //añadir los datos al data
                        data.add(map);

                    }
                    //arreglo con nombres de cada dato ingresado
                    String[] from={"FOLIO","Fechas","Estados","Prioridad","Image","comentarios","NombreEmp"};
                    //arreglo con el id de cada pare donde se visualizara la informacion
                    int[] to={R.id.idFolioReporteFragmentoMenuAdminSolucionados,R.id.txtFechaReporteFragmentoMenuAdminSolucionados,R.id.EstadoFragmentoMenuAdminSolucionados,R.id.PrioridadFragmentoMenuEmpleadoRecibidos,R.id.imageViewFragmentoMenuAdminSolucionados};
                    //adaptador para mostar la imagen
                    adapter=new SimpleAdapter(getActivity(), data, R.layout.fragment_fragmento2_menu_admin_solucionados, from, to);
                    //añadir al adaptador para que se muestren los datos
                    setListAdapter(adapter);

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
                    Toast.makeText(getContext(), "No hay registros", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(getContext(), "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }
                //añadir al adaptador los datos vacios
                setListAdapter(adapter);
            }
        });
        //para establecer la comunicacion con los metodos
        reques.add(jsonObjectRequest);

    }

    //metodo para obtener la ip ingresada
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        return pref.getString("ip_servidor","");
    }
    //metodo para sber el estado del internet
    public boolean exiteConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
