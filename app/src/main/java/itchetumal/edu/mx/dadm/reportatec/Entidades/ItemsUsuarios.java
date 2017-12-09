package itchetumal.edu.mx.dadm.reportatec.Entidades;

/**
 * Created by WIN on 26/11/2017.
 */

public class ItemsUsuarios {

    private Integer IdUsua;
    private String NumControlUsu;
    private String NombreUsua;
    private String CorreoUsu;
    private String Usuari;
    private String Contrase;

    public ItemsUsuarios(Integer IdUsua,String NumControlUsu,String NombreUsua,String CorreoUsu,String Usuari,String Contrase){
        this.IdUsua=IdUsua;
        this.NumControlUsu=NumControlUsu;
        this.NombreUsua=NombreUsua;
        this.CorreoUsu=CorreoUsu;
        this.Usuari=Usuari;
        this.Contrase=Contrase;
    }
    public ItemsUsuarios(){

    }

    public void setNombreUsua(String nombreUsua) {
        NombreUsua = nombreUsua;
    }

    public void setContrase(String contrase) {
        Contrase = contrase;
    }

    public void setCorreoUsu(String correoUsu) {
        CorreoUsu = correoUsu;
    }

    public void setIdUsua(Integer idUsua) {
        IdUsua = idUsua;
    }

    public void setNumControlUsu(String numControlUsu) {
        NumControlUsu = numControlUsu;
    }

    public void setUsuari(String usuari) {
        Usuari = usuari;
    }

    public Integer getIdUsua() {
        return IdUsua;
    }

    public String getContrase() {
        return Contrase;
    }

    public String getCorreoUsu() {
        return CorreoUsu;
    }

    public String getNumControlUsu() {
        return NumControlUsu;
    }

    public String getUsuari() {
        return Usuari;
    }

    public String getNombreUsua() {
        return NombreUsua;
    }
}
