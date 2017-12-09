package itchetumal.edu.mx.dadm.reportatec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //declarar variables para guardar los datos del archivo de preferencias
    String Tipo;
    String TipoPermiso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //obtenr los datos del archivo de preferencias
        //al iniciar la aplicacion, validar si el usuario ya se ha registrado
        SharedPreferences datosUsuario=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
        Tipo= datosUsuario.getString("TipoUs","");
        TipoPermiso= datosUsuario.getString("TipoPermiso","");
        ///////////////////

        //Vista de navegacion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Inicio", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Llamar a las opciones para modificar las opciones
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Obtener el menu para modificar
        Menu navigationViewMenu = navigationView.getMenu();

        //Ocultar alguna opcion del menu
        //navigationViewMenu.findItem(R.id.AnuncioGeneral).setVisible(false);
        //ocultar opciones segun su perfil
        //si es un usuario normal se le ocultaran las opciones del admin y el empleado
        if (Tipo.equalsIgnoreCase("Usuario")){
            navigationView.getMenu().findItem(R.id.Frag2MenuOpAdmin).setVisible(false);
            navigationView.getMenu().findItem(R.id.Frag3MenuOpEmp).setVisible(false);
            navigationView.getMenu().findItem(R.id.EmpleadosAdmin).setVisible(false);
        } else if(Tipo.equalsIgnoreCase("Empleado")){
            //si es un usuario empleado se le ocultaran las opciones de realizar un reporte y su perfil
            navigationView.getMenu().findItem(R.id.Frag1MenuOpUser).setVisible(false);
            navigationView.getMenu().findItem(R.id.Reportes).setVisible(false);
            navigationView.getMenu().findItem(R.id.PerfilUsuario).setVisible(false);
            //si es un jefe de departamento o subjefe entra
            if(TipoPermiso.equalsIgnoreCase("Jefe Departamento")||TipoPermiso.equalsIgnoreCase("SubJefe")){
                //se le oculta el menu de los empleados
                if (TipoPermiso.equalsIgnoreCase("SubJefe")){
                    //si es un subjefe se le oculta la opcion de administrar a los empleado
                    navigationView.getMenu().findItem(R.id.EmpleadosAdmin).setVisible(false);
                }
                navigationView.getMenu().findItem(R.id.Frag3MenuOpEmp).setVisible(false);
            }else if (TipoPermiso.equalsIgnoreCase("Empleado normal")){
                //si es un empleado normal se le oculta el menu de usuarios y admin
                navigationView.getMenu().findItem(R.id.Frag2MenuOpAdmin).setVisible(false);
                navigationView.getMenu().findItem(R.id.EmpleadosAdmin).setVisible(false);
            }

        }

        //
        navigationView.setNavigationItemSelectedListener(this);
        //Cierre de vista de navegacion

        //Iniciar la aplicacion sobre un fragmento
        FragmentManager ManejadorFragmentos = getSupportFragmentManager();
        //mostar fragmento menu segun el usuario
        //si es un usuario se le muestra el menu de menu de opciones para usuarios y se muestren sus fragmentos correspondientes
        if (Tipo.equalsIgnoreCase("Usuario")){
            ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new Fragmento1MenuOpcionesUsuario()).commit();
        } else if(Tipo.equalsIgnoreCase("Empleado")) {
            //si es un empleado se le muestra el menu de menu de opciones para empleados y se muestren sus fragmentos correspondientes
            //se verifica que tipo de empleado es para iniciar el fragmento correspondientes
            if (TipoPermiso.equalsIgnoreCase("Jefe Departamento") || TipoPermiso.equalsIgnoreCase("SubJefe")) {
                ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new Fragmento2MenuOpcionesAdmin()).commit();

            } else if (TipoPermiso.equalsIgnoreCase("Empleado normal")) {
                ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new Fragmento3MenuOpcionesEmpleado()).commit();
            }
        }
        //metodo para bloquera la pantalla segun el archivo de preferencias
        BloquearPantalla();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //mostrar el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //metodo para saber si seleciono el menu de configuraciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // si se selecciona la opcion de settings se abre la actividad de configuraciones
        if (item.getItemId() == R.id.action_settings) {
            //1 Crear actividad para llamar a la segunda actividad
            Intent Preferencia = new Intent(MainActivity.this, configuraciones.class);
            //3 Iniciar actividad
            startActivity(Preferencia);
        }

        return super.onOptionsItemSelected(item);
    }

    //metodo para saber a cualm opcion del menu de la izquierda se selecciono
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager ManejadorFragmentos = getSupportFragmentManager();
        //dependiendo de los menu que vea son las opciones que podra elegir
        //Segun la opcion que se elija es la actividad que se mostrara
        if (id == R.id.Frag1MenuOpUser) {
            ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new Fragmento1MenuOpcionesUsuario()).commit();
        }else if (id == R.id.Frag2MenuOpAdmin) {
            ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new Fragmento2MenuOpcionesAdmin()).commit();
        }else if (id == R.id.Frag3MenuOpEmp) {
            ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new Fragmento3MenuOpcionesEmpleado()).commit();
        }else if (id == R.id.Reportes) {
            ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new FragmentoListaDeProblemas()).commit();
        } else if (id == R.id.FragPreguntaFrecuente) {
            ManejadorFragmentos.beginTransaction().replace(R.id.contenedor, new FragmentoPreguntasFrecuentes()).commit();
        }else if (id == R.id.EmpleadosAdmin) {
            Intent in1=new Intent(MainActivity.this, AdminCrearEmpleado.class);
            startActivity(in1);
        }else if (id == R.id.PerfilUsuario) {
            Intent in1=new Intent(MainActivity.this, UsuarioPerfil.class);
            startActivity(in1);
        }else if (id == R.id.Salir) {
            //si se selecciona la opcion salir
            //eliminar archivo de preferencias
            SharedPreferences datosUsuario=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
            datosUsuario.edit().clear().commit();
            //eliminar archivo de preferencias
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(this);
            pref.edit().clear().commit();
            //REGRESAR AL LOGIN
            //1 Crear actividad para llamar a la segunda actividad
            Intent intencionMenu = new Intent(MainActivity.this, Login.class);
            //2 Iniciar actividad
            startActivity(intencionMenu);
            //finalizar actividad
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {

    }

    //metodo para bloquear la pantalla segun el archivo de preferencias
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
        //pref.edit().clear().commit();
        return pref.getBoolean("bloquear",false);
    }
}
