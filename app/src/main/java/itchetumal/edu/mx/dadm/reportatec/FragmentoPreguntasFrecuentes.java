package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentoPreguntasFrecuentes extends ListFragment {
    //arreglo con los titulos de las preguntas frecuentes
    String[] PreguntaFrecuente = {"Funciones del departamento","¿Quien ve mi información?","No se soluciona mi problema"};
    //arreglo con la descripcion de cada pregunta
    String[] DescripcionPregunta ={"Solucionar y asistir a los problemas generales del tec ",
            "Tu información es confidencial, solo lo puede ver personal del tecnologico:",
            "Revisa el estado de tu reporte, puede que este pendiente por solucionar"};
    //imagenes para cada pregunta
    int[] imagenes = {R.drawable.itch, R.drawable.itch, R.drawable.itch};
    //arraylist para guardar los datos obtenido del servidor
    ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
    //adaptador
    SimpleAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //MAP para almacenar los datos
        HashMap<String, String> map=new HashMap<String, String>();
        //recorrer los datos
        for(int i=0;i<PreguntaFrecuente.length;i++)
        {
            //inicilizar objeto map
            map=new HashMap<String, String>();
            //asignar los datos de cada arreglo
            map.put("Pregunta", PreguntaFrecuente[i]);
            map.put("Descripcion", DescripcionPregunta[i]);
            map.put("Image", Integer.toString(imagenes[i]));
            //añadir los datos al data
            data.add(map);
        }
        //arreglo con nombres de cada dato ingresado
        String[] from={"Pregunta","Descripcion","Image"};
        //arreglo con el id de cada pare donde se visualizara la informacion
        int[] to={R.id.tvtituloPreguntasFrecuentesFragmento,R.id.tvDetallesPreguntasFrecuentesFragmento,R.id.imageViewPreguntasFrecuentesFragmento};
        //adaptador para mostar la imagen
        adapter=new SimpleAdapter(getActivity(), data, R.layout.fragment_fragmento_preguntas_frecuentes, from, to);
        //añadir al adaptador para que se muestren los datos
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);

    }
    //metodo para saber el item que se esta selecionando
    @Override
    public void onStart(){
        // TODO Auto-generated method stub
        super.onStart();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                // TODO Auto-generated method stub
                //ddependidnedo de la opcion en la actividad que se mostrara
                if (pos == 0) {

                }
                if (pos == 1) {
                    //Intent electri = new Intent(getActivity(), ReporteTerminadoAdmin.class);
                    //startActivity(electri);
                }
                if (pos == 2) {
                   // Intent electri = new Intent(getActivity(), ReporteRecibidoAdmin.class);
                    //startActivity(electri);
                }
               // Toast.makeText(getActivity(), data.get(pos).get("Pregunta"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
