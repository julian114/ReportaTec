package itchetumal.edu.mx.dadm.reportatec;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FormularioReportes extends AppCompatActivity  implements OnClickListener{
    //declara componentes  para referenciarlos
    TextView TVTipoReporte;
    EditText EDDescripcion;
    ImageView IVImagen;
    TextView OculatTextoPlano;
    //declarar variables para tomar la foto y guardarla
    Bitmap bitmap;
    //variables para almacenar la imagen tomada
    private static final String LugarAlmacena="misImagenes/";
    private static final String RutaImagen="misFotos";
    private static final String CarpetaImagen=LugarAlmacena+RutaImagen;
    //almacenar la ruta de la imagen
    private String path;
    File Imagen;
    //boton para adjuntar
    Button BTNAdjuntar;
    //variables para obtener los datos de los campos
    String Problema2, Edificio2, Salon2;

    //para establecer la conexion con el web service
    RequestQueue reques;
    //declarar variables para guardar los datos del archivo de preferencias
    int IdLog;
    //declara un objeto de tipo stringrequest para que nos retorne el texto
    StringRequest stringRequest;

    //almacenar el dato enviado de otra actividad
    String dato;

    //arreglo para almacenar los datos segun el tipo de reporte
    String [] values;
    //para ventana de progreso en caso que se tarde
    ProgressDialog progre;

    //url web service 00web
   // final String UrlGlo="https://reportatec.000webhostapp.com/ReportaTec/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_reportes);

        //recuperar la intencion que la llamo
        Intent intencionLlamo=getIntent();

        //recuperar los datos recibidos
        Bundle bundle =intencionLlamo.getExtras();
        //obtener el tipo de reporte
        dato= bundle.getString("TipoEle");

        //metodo para decidir el contenido del arreglo
        CargarArreglo(dato);

        //declrara y referenciar spinner
        Spinner SPINNERPROBLEMA = (Spinner) findViewById(R.id.spinnerProblemaReporte);
        //adaptador
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        //forma en la que se presentan los datos
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        //asignar los datos para que se muestren
        SPINNERPROBLEMA.setAdapter(myAdapter);

        //arreglo para mostrar los edificios
        final String [] values2 =
                {"Edificio","AA","AB","AC","AD","A","B","C","D",
                        "E","F","G","H","I","J",
                        "K","L","M","M´","N",
                        "O","P","Q","R","S","T","U","U´","V","W",
                        "X","Y","Z","Z´","Unidad deportiva","Otro",};
        //declrara y referenciar spinner
        Spinner SPINNEREDIFICIO = (Spinner) findViewById(R.id.spinnerEdificioReporte);
        //adaptador
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values2);
        //forma en la que se presentan los datos
        myAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        //asignar los datos para que se muestren
        SPINNEREDIFICIO.setAdapter(myAdapter2);

        //arreglo para mostar los salones
        final String [] values3 =
                {"¿En qué salón?","1","2","3","4","5","6","7","8","9","10","11","12","13","14","Unidad deportiva","Otro",};
        //declrara y referenciar spinner
        Spinner SPINNERSALON = (Spinner) findViewById(R.id.spinnerSalonReporte);
        //adaptador
        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values3);
        //forma en la que se presentan los datos
        myAdapter3.setDropDownViewResource(android.R.layout.simple_list_item_1);
        //asignar los datos para que se muestren
        SPINNERSALON.setAdapter(myAdapter3);

        //referenciar los campos
        TVTipoReporte=(TextView) findViewById(R.id.textViewNombreTipoReporte);
        EDDescripcion= (EditText)findViewById(R.id.editTextDescripcionReporte);
        IVImagen=(ImageView)findViewById(R.id.imageViewReporte);
        OculatTextoPlano=(TextView) findViewById(R.id.TextoPlanoSalon);
        //si el tipo de dato en areas verdes el spinner de salon se oculta porque no lleva ese dato
        if (dato.equalsIgnoreCase("Areas Verdes")){
            SPINNERSALON.setVisibility(View.GONE);
            OculatTextoPlano.setVisibility(View.GONE);
        }

        //crear el boton atras
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //asignar texto del tipo de reporte
        TVTipoReporte.setText(dato);
        //referenciar botones
        BTNAdjuntar= (Button) findViewById(R.id.btnAdjuntarReporte);
        BTNAdjuntar.setOnClickListener(this);
        View BTNCancelar=findViewById(R.id.BtnCancelarReporte);
        BTNCancelar.setOnClickListener(this);
        View BTNEnviar=findViewById(R.id.btnEnviarReporte);
        BTNEnviar.setOnClickListener(this);
        //referenciar el objeto reques
        reques= Volley.newRequestQueue(this);
        //en caso de que se trate de android 6 en adelante hay que pedir permisos, asi que llamamos al metodo que valida los permisos
        if(validaPermisos()){
            //si los permisos fueron aceptado se abilita el boton para cargar la imagen
            BTNAdjuntar.setEnabled(true);
        }else{
            //si los permisos no fueron aceptado se deshabilita el boton para cargar la imagen
            BTNAdjuntar.setEnabled(false);
        }
        //obtenermos del archivo de referencias el id del usuario que hace el reporte
        SharedPreferences datosUsuario=getSharedPreferences("DatosLog", Context.MODE_PRIVATE);
        IdLog= datosUsuario.getInt("Id",0);
        //escuchador del spinner
        SPINNERPROBLEMA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    //obtener el item elegido
                    Problema2=values[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //escuchador del spinner
        SPINNEREDIFICIO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    //obtener el item elegido
                    Edificio2=values2[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //escuchador del spinner
        SPINNERSALON.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    //obtener el item elegido
                    Salon2=values3[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //llamar al metodo que bloqueara la pantalla segun la opcion del archivo de preferencias
        BloquearPantalla();
    }

    //guardar lo que este en el bitmap para evitar que se pierda al rotar la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //guardar el bitmap
        outState.putParcelable("guardar", bitmap);
    }
    //recuperar la informacion
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        //recuperar la imagen y asignarcelo al  imageview
        bitmap = savedInstanceState.getParcelable("guardar");
        IVImagen.setImageBitmap(bitmap);
    }

    //metodo para llenar el arreglo segun el tipo de problema
    private void CargarArreglo(String dato) {
        if (dato.equalsIgnoreCase("Areas Verdes")){
            values = new String[6];
            values[0]="Problema";
            values[1]="Maleza";
            values[2]="Ramas caidas";
            values[3]="Cesped alto";
            values[4]="Árbol caído";
            values[5]="Otro";
        }else if (dato.equalsIgnoreCase("Electrico y electronico")){
            values = new String[9];
            values[0]="Problema";
            values[1]="Cable sobrecalentado (Chispas)";
            values[2]="Contactos (Enchufe eléctrico)";
            values[3]="Falta de electricidad";
            values[4]="Lámparas";
            values[5]="Balastro";
            values[6]="Aires acondicionados";
            values[7]="Ventiladores";
            values[8]="Otro";
        }else if (dato.equalsIgnoreCase("Infraestructura general")){
            values = new String[6];
            values[0]="Problema";
            values[1]="Pintura";
            values[2]="Cerraduras";
            values[3]="Grietas";
            values[4]="Vitropizo";
            values[5]="Otro";
        }else if (dato.equalsIgnoreCase("Limpieza")){
            values = new String[4];
            values[0]="Problema";
            values[1]="Salón sucio";
            values[2]="Pasillo sucio.";
            values[3]="Otro";
        }else if (dato.equalsIgnoreCase("Mobiliario")){
            values = new String[8];
            values[0]="Problema";
            values[1]="Sillas";
            values[2]="Mesa";
            values[3]="Escritorios";
            values[4]="Ventanas";
            values[5]="Puertas";
            values[6]="Pintarrones";
            values[7]="Otro";
        }else if (dato.equalsIgnoreCase("Sanitarios")){
            values = new String[6];
            values[0]="Problema";
            values[1]="Inodoro obstruido";
            values[2]="Urinario obstruido";
            values[3]="Sin papel";
            values[4]="No hay agua";
            values[5]="Otro";
        }
    }

    //metodo para validar los permisos
    private boolean validaPermisos() {
        //si es una version inferior a android 5 no es necesario pedir permisos
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        //si es superior a android 5 verificar que los permisos esten aceptado
        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        //si no estan aceptados cargar el dialogo de recomendacion
        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            //se solicitaran los permisos
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    //recomendacion para asignarle los permisos
    private void cargarDialogoRecomendacion() {
        //crear un dialogo de alerta
        AlertDialog.Builder dialogo=new AlertDialog.Builder(FormularioReportes.this);
        //itulo
        dialogo.setTitle("Permisos Desactivados");
        //mensaje
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");
        //boton aceptar para que se carguen los pemisos de ecritura y la camara
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M) // a la fuerza
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        //mostrar dialodo
        dialogo.show();
    }

    //metodo para pedir los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //si el codifo es 100 entra
        if(requestCode==100){
            //validar si corresponde a 2 posisicion del arreglo, es decir dos permisos, para saber si ya se le dio el permiso
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                //se abilita el boton
                BTNAdjuntar.setEnabled(true);
            }else{
                //metodo para solicitar los permisos de forma manual
                solicitarPermisosManual();
            }
        }
    }

    //metodo para slictar los permisos de forma manual
    private void solicitarPermisosManual() {
        //arreglo de opciones
        final CharSequence[] opciones={"si","no"};
        //crear un dialogo de alerta
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(FormularioReportes.this);
        //tiyulo
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        //asignar botones
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //si la opcion es si
                if (opciones[i].equals("si")){
                    //abrimos las configuraciones para que las configure de forma manual
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    //en caso contrario se muestra el mensaje
                    Toast.makeText(FormularioReportes.this,"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    //se cierra el dialogo
                    dialogInterface.dismiss();
                }
            }
        });
        //mostrar dialogo
        alertOpciones.show();
    }

    //para regresar con el boton atras
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //metodo para saber cual boton se esta presionando
    @Override
    public void onClick(View v) {
        //si se presiona el boton adjuntar
        if (v.getId()==R.id.btnAdjuntarReporte){
            //llamar al metodo que carga la imagen
            cargarimagen();

        } else if (v.getId()==R.id.BtnCancelarReporte){
            //si presiona el boton cancelar se finalizar la actividad
            finish();

        }else if (v.getId()==R.id.btnEnviarReporte){
            //si se presiona el boton enviar entra
            //antes de cargar el web service comprobar si se eligieron datos
            //si datos estan vacios entonces no entra
            if (Problema2!=null && Edificio2!=null && bitmap!=null) {
                //comprobar si es reporte de areas verdes para permitir que el salon sea nulo
                if (dato.equalsIgnoreCase("Areas Verdes")){
                    //si es areas verdes el salon no se aplica
                    Salon2="No Aplica";
                    //si la descripcion no esta vacia entra
                    if (!EDDescripcion.getText().toString().isEmpty()){
                        CargarWebService();
                    }else {
                        //si la descripcion esta vacia se le escrie no aplica
                        EDDescripcion.setText("No Aplica");
                        CargarWebService();
                    }
                }else if(Salon2!=null) {
                    //si no es areas verdes y el salon esta elegido entra, y si la descripcion no esta vacia
                    if (!EDDescripcion.getText().toString().isEmpty()){
                        CargarWebService();
                    }else {
                        //si la descripcion esta vacia se le asigana el texto
                        EDDescripcion.setText("No Aplica");
                        CargarWebService();
                    }
                }
            }else
            {
                //mensaje en caso de que esten vacios
                Toast.makeText(FormularioReportes.this,"Falta llenar campos obligatorios",Toast.LENGTH_SHORT).show();
            }

        }

    }

    //metodo para guardar el reporte
    public void CargarWebService(){
        //se mostrara un ventana para indicar que se esta intentando conecatr
        progre= new ProgressDialog(this);
        //escribimos el mensaje
        progre.setMessage("Enviando reporte, por favor espere");
        //mostramos la ventana
        progre.show();
        //creamos la url del archivo php al que accedera, y con concatenamos
        String url="http://"+direecionip()+"/ReportaTec/wsJSONRegistroMovil.php?";
        //inicializamos el stringrequest por medio de una clase anonima, para enviarcelo a volley, indicamos que se enviara los datos por medio del metodo post
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //ocultar barra
                progre.hide();
                //si la respuesta de tipo string es actualiza entra
                if (response.trim().equalsIgnoreCase("registra")){
                    //si el php devuelve la palabra registra indica que si se ingreso satisfactoriamente
                    //mostrar el mensaje
                    Toast.makeText(FormularioReportes.this,"Se ha registrado con exito",Toast.LENGTH_SHORT).show();
                    //finalizar la actividad
                    finish();
                }else{
                    //en caso que no sea registra mostrar el mensaje que no se registro
                    Toast.makeText(FormularioReportes.this,"No se ha registrado ",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //ocultar barra
                progre.hide();
                //en caso de ocurra un error llmar al metodo que verifica que se tenga conexion a internet
                if (exiteConexionInternet()){
                    //si exite conexion a internet mostar
                    Toast.makeText(FormularioReportes.this, "No se puede reportar en este momento", Toast.LENGTH_LONG).show();
                }else{
                    //en caso de que no exista conexion a internet
                    Toast.makeText(FormularioReportes.this, "No se puede conectar compruebe su conexion a internet", Toast.LENGTH_LONG).show();
                }

            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //declrara e inicilizar los datos que se enviaran al php por medio del metodo post
                //asignarle los datos qu estan en los campos de texto
                String TipoReport=TVTipoReporte.getText().toString();
                String Problem=Problema2.toString();
                String Edifici=Edificio2.toString();
                String Salo=Salon2.toString();
                String Descripcio=EDDescripcion.getText().toString();
                String Fot=convertirImgString(bitmap);
                String IdUsuari=String.valueOf(IdLog);
                //enviarle los datos al php con el nombre de los campos
                Map<String,String> parametros=new HashMap<>();
                parametros.put("TipoReporte",TipoReport);
                parametros.put("Problema",Problem);
                parametros.put("Edificio",Edifici);
                parametros.put("Salon",Salo);
                parametros.put("Descripcion",Descripcio);
                parametros.put("Foto",Fot);
                parametros.put("IdUsuario",IdUsuari);
                //retornar los datos
                return parametros;
            }
        };
        //para establecer la comunicacion con los metodos
        reques.add(stringRequest);

    }
    //metodo para convertir la imagen a string y asi almacenarla en la base de datos
    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);
        return imagenString;
    }
    //Metodo para cargar imagen
    public void cargarimagen(){
        //arreglo para las opciones
        final CharSequence[] opciones={"Tomar foto","Cargar imagen","Cancelar"};
        //crear un dialogo de alerta
        final AlertDialog.Builder aleropciones= new AlertDialog.Builder(FormularioReportes.this);
        //titulo
        aleropciones.setTitle("Seleccione una Opcion");
        aleropciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //si la opcion es tomar foto entra
                if (opciones[which].equals("Tomar foto")){
                    //llamar al metodo para tomar la foto
                    tomarfoto();

                }else {
                    //en caso contrario preguntar si eligio la opcion de argar la imagen
                    if (opciones[which].equals("Cargar imagen")){
                        //abiri la intencion para cargar la imagen
                        Intent intento = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //indicamos que sera una imagen
                        intento.setType("image/*");
                        //en caso de tener muchas aplicaciones para seleccionar imagenes, le enviamos la intencion y el codigo
                        startActivityForResult(intento.createChooser(intento,"seleccione la aplicacion"),10);

                    }else{
                        //para que cierre el dialogo
                        dialog.dismiss();
                    }
                }

            }
        });
        //para que se visualice
        aleropciones.show();

    }

    //metodo para tomar la foto
    private void tomarfoto() {
        //archivo para guardar la foto
        File fileImagenn=new File(Environment.getExternalStorageDirectory(),CarpetaImagen);
        //para saber si fue creada la foto
        Boolean creada=fileImagenn.exists();

        //en caso de que no se creo
        if (creada==false){
            //se intenta crear nuevamente
            creada=fileImagenn.mkdirs();
        }
        if (creada==true){
            //para el nombre de la imagen, en formato hora en milisegundos
            Long NombreImaConse=System.currentTimeMillis()/1000;
            String  NombreIma =NombreImaConse.toString()+".jpg";
            //lugar donde se almacena la imagen
            path =Environment.getExternalStorageDirectory()+
                    File.separator+CarpetaImagen+File.separator+NombreIma;
            //archivo con la imagen creada
            Imagen= new File(path);
            //intencion para la camara
            Intent intencionCamara= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intencionCamara.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Imagen));

            // en caso de que sea versiones de android superiores se realiza este proceso
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            {
                String authorities=FormularioReportes.this.getPackageName()+".provider";
                Uri imageUri= FileProvider.getUriForFile(FormularioReportes.this,authorities,Imagen);
                intencionCamara.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else
            {
                //si no es una version de nadroid superior se trata normalmente
                intencionCamara.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Imagen));
            }
            //le enviamos la intencion al metodo y el codigo para crear la imagen
            startActivityForResult(intencionCamara,20);

        }

    }

    //metodo que resive la intencion y el codigo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //si todo esta bien y si se lecciona la imagen
        if (resultCode==RESULT_OK){

            //dependiendo del codico que sea tomara la foto o elegira una imagen
            switch (requestCode){
                //si el codigo es 10 es para seleccionar la imagen de la galeria
                case 10:
                    Uri mipath = data.getData();
                    IVImagen.setImageURI(mipath);
                    try {
                        bitmap =MediaStore.Images.Media.getBitmap(this.getContentResolver(),mipath);
                        IVImagen.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 20:
                    //si el codigo es 20 es para tomar la fotografia
                    //permitir que la imagen se guarde en la galeria
                    MediaScannerConnection.scanFile(this,new String[]{path},null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                //para saber si el metodo se completo
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                    //craer la imagen y mostarla
                    bitmap= BitmapFactory.decodeFile(path);
                    //Asignarle la imagen al imageview
                    IVImagen.setImageBitmap(bitmap);
                    break;

            }

            //optimizar la imagen para que se almacene en la base de datos en caso de se muy pesada
            bitmap= optimizarImagen(bitmap,600,800);


        }
    }
    //metodo para optimizar
    private Bitmap optimizarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {
        //obtener el ancho y alto actual de la imagen
        int anchoActual=bitmap.getWidth();
        int altoActual=bitmap.getHeight();
        //si es mayor a las medididas que se enviaron entra
        if (anchoActual>anchoNuevo||altoActual>altoNuevo){
            //obtener nueva escala
            float escalaAncho=anchoNuevo/anchoActual;
            float escalaAlto=altoNuevo/altoActual;

            //crear objeto tipo matrix, para manipular los datos de la imagen
            Matrix matri= new Matrix();
            matri.postScale(escalaAncho,escalaAlto);
            //retornar el bitmap
            return bitmap.createBitmap(bitmap,0,0,anchoActual,altoActual,matri,false);

        }else
        {
            //en caso de que no sobrepase el tamaño se envia normalmente
            return bitmap;
        }

    }

    //metodo para obtener la ip ingresada
    public String direecionip(){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        return pref.getString("ip_servidor","");
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
        //pref.edit().clear().commit();
        return pref.getBoolean("bloquear",false);
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
