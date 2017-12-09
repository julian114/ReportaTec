package itchetumal.edu.mx.dadm.reportatec.Entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by WIN on 28/11/2017.
 */

public class ItemsReportes {
    private Integer IdReporte;
    private String TipoReporte;
    private String Problema;
    private String Edificio;
    private String Salon;
    private String Descripcion;
    private String Fecha;
    //para recibir la informacion directamente desde el web service
    private String Dato;
    //
    private Bitmap Foto;
    private Integer IdUsuario;

    public ItemsReportes(Integer IdReporte,String TipoReporte,String Problema,String Edificio,String Salon,String Descripcion,String Fecha,String Dato,Bitmap Foto,Integer IdUsuario){
        this.IdReporte=IdReporte;
        this.TipoReporte=TipoReporte;
        this.Problema=Problema;
        this.Edificio=Edificio;
        this.Salon=Salon;
        this.Descripcion=Descripcion;
        this.Fecha=Fecha;
        this.Dato=Dato;
        this.Foto=Foto;


    }
    public ItemsReportes(){

    }

    public String getDato() {
        return Dato;
    }

    public void setDato(String dato) {
        this.Dato = dato;
        try {
            byte[] bytedecode= Base64.decode(Dato,Base64.DEFAULT);
            this.Foto= BitmapFactory.decodeByteArray(bytedecode,0,bytedecode.length);

        }catch (Exception e){

        }
    }

    public Bitmap getFoto() {
        return Foto;
    }

    public void setFoto(Bitmap foto) {
        Foto = foto;
    }

    public void setSalon(String salon) {
        Salon = salon;
    }

    public void setIdReporte(Integer idReporte) {
        IdReporte = idReporte;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public void setEdificio(String edificio) {
        Edificio = edificio;
    }

    public void setIdUsuario(Integer idUsuario) {
        IdUsuario = idUsuario;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }


    public void setProblema(String problema) {
        Problema = problema;
    }

    public void setTipoReporte(String tipoReporte) {
        TipoReporte = tipoReporte;
    }

    public Integer getIdReporte() {
        return IdReporte;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public Integer getIdUsuario() {
        return IdUsuario;
    }

    public String getEdificio() {
        return Edificio;
    }

    public String getFecha() {
        return Fecha;
    }


    public String getProblema() {
        return Problema;
    }

    public String getTipoReporte() {
        return TipoReporte;
    }

    public String getSalon() {
        return Salon;
    }
}
