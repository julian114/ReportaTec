package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragmento2MenuOpcionesAdmin extends Fragment {
    //Gestionar la barra donde iran las pestañas
    private AppBarLayout appBar;
    //Para las pestañas o tabs
    private TabLayout tabs;
    //Para mostrar una vista de cada fragmento dependiendo la vista seleccionada
    private ViewPager viewPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Agregar las barras y las pestañas
        View view = inflater.inflate(R.layout.fragment_fragmento2_menu_opciones_admin, container, false);
        View contenedor = (View)container.getParent();
        appBar =(AppBarLayout)contenedor.findViewById(R.id.appbar);
        tabs = new TabLayout(getActivity());
        //cambia el color de las pestañas al estar en reposo y al estar seleccionadas
        tabs.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#F0FF00"));
        //tabs.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabs.setBackgroundColor(Color.parseColor("#3385C1"));
        appBar.addView(tabs);

        //agregar titulos a las pestañas o fragmentos que se mostraran dependiendo la pestaña que seleccionamos

        viewPager= (ViewPager)view.findViewById(R.id.pagerFragmentoMenuOpcionesAdmin);
        ViewPageAdapter pagerAdapter = new ViewPageAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        //tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        return view;
    }

    //Metodo que destruye las pestañas al seleccionar otras para evitar que se repitan y vuelvan a salir
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appBar.removeView(tabs);
    }

    //Gestionar el uso y manipular datos

    public class ViewPageAdapter extends FragmentStatePagerAdapter {
        public ViewPageAdapter(FragmentManager fragmentManager){

            super(fragmentManager);
        }

        String[] tituloTabs = {"Recibidos", "Pendientes", "Reportes Solucionados"};

        @Override
        public Fragment getItem(int position) {
            //retorna el fragmento dependiendo la posicion de la pestaña seleccionada
            switch (position){
                case 0: return new Fragmento2MenuAdminRecibidos();
                case 1: return new Fragmento2MenuAdminPendientes();
                case 2: return new Fragmento2MenuAdminSolucionados();

            }
            return null;
        }

        //Retornar las barras que se agregan  al menu
        @Override
        public int getCount() {
            return 3;
        }

        //Retornamos el arreglo que tiene los nombres para las pestañas que usaremos

        @Override
        public CharSequence getPageTitle(int position) {
            return tituloTabs[position];
        }
    }
}
