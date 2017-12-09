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


public class FragmentoListaDeProblemas extends ListFragment {
    //arreglo de los tipos de problemas que se mostraran en la lista
    String[] TipoProblema = {"Áreas verdes","Infraestructura electrica y electronica","Infraestructura general","Limpieza",
    "Mobiliario","Sanitarios" };
    //arreglo con la descripcion de cada problema
    String[] DescripcionProblema ={"Encargada de cuidar el crecimiento de plantas y árboles: ",
            "Se encarga de revisar cualquier problema relacionado a electricidad y electronicos:",
            "Se encarga de revisar cualquier problema con infraestructura",
    "Se encarga de revisar que todo este limpio y en orden","Se encarga de revisar que todo el mobiliario escolar este en optimas condiciones",
    "Se encarga de revisar que los sanitarios operen correctamente"};
    //arreglo con las imagenes de cada tipo
    int[] imagenes = {R.drawable.reporareasverdes, R.drawable.reporelectricidad, R.drawable.reporinfraestructura,
            R.drawable.reporlimpieza,R.drawable.repormobilario,R.drawable.reporsanitario,};
    //arraylist para guardar los datos obtenido del servidor
    ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
    //adaptador
    SimpleAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //MAP para almacenar los datos
        HashMap<String, String> map=new HashMap<String, String>();
        //recorrer los datos
        for(int i=0;i<TipoProblema.length;i++)
        {
            //inicilizar objeto map
            map=new HashMap<String, String>();
            //asignar los datos de cada arreglo
            map.put("TipoProblema", TipoProblema[i]);
            map.put("Descripcion", DescripcionProblema[i]);
            map.put("Image", Integer.toString(imagenes[i]));
            //añadir los datos al data
            data.add(map);
        }
        //arreglo con nombres de cada dato ingresado
        String[] from={"TipoProblema","Descripcion","Image"};
        //arreglo con el id de cada pare donde se visualizara la informacion
        int[] to={R.id.tvtituloListaDeProblemasFragmento,R.id.tvDetallesListaDeProblemasFragmento,R.id.imageViewListaDeProblemasFragmento};
        //adaptador para mostar la imagen
        adapter=new SimpleAdapter(getActivity(), data, R.layout.fragment_fragmento_lista_de_problemas, from, to);
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
                //dependiendo de la opcion se le envia un dato con el tipo de problema a la vista formularioreportes y se inicia la actividad
                if (pos == 0) {
                    Intent verdes = new Intent(getActivity(), FormularioReportes.class);
                    verdes.putExtra("TipoEle","Areas Verdes");
                    startActivity(verdes);
                }
                if (pos == 1) {
                    Intent electri = new Intent(getActivity(), FormularioReportes.class);
                    electri.putExtra("TipoEle","Electrico y electronico");
                    startActivity(electri);
                }
                if (pos == 2) {
                    Intent general = new Intent(getActivity(), FormularioReportes.class);
                    general.putExtra("TipoEle","Infraestructura general");
                    startActivity(general);
                }
                if (pos == 3) {
                    Intent limpi = new Intent(getActivity(), FormularioReportes.class);
                    limpi.putExtra("TipoEle","Limpieza");
                    startActivity(limpi);
                }
                if (pos == 4) {
                    Intent mobi = new Intent(getActivity(), FormularioReportes.class);
                    mobi.putExtra("TipoEle","Mobiliario");
                    startActivity(mobi);
                }
                if (pos == 5) {
                    Intent sani = new Intent(getActivity(), FormularioReportes.class);
                    sani.putExtra("TipoEle","Sanitarios");
                    startActivity(sani);
                }
            }
        });
    }
}
