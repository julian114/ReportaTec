package itchetumal.edu.mx.dadm.reportatec.Entidades;

/**
 * Created by WIN on 26/11/2017.
 */

public class ItemEmpleados {

    private Integer IdEmpleado;
    private String NumControlEmp;
    private String NombreEmp;
    private String CorreoEmp;
    private String UsuarioEmp;
    private String Contraseña;
    private String TipoPermiso;

    public ItemEmpleados(Integer IdEmpleado,String NumControlEmp,String NombreEmp,String CorreoEmp,String UsuarioEmp,String Contraseña,String TipoPermiso){
        this.IdEmpleado=IdEmpleado;
        this.NumControlEmp=NumControlEmp;
        this.NombreEmp=NombreEmp;
        this.CorreoEmp=CorreoEmp;
        this.UsuarioEmp=UsuarioEmp;
        this.Contraseña=Contraseña;
        this.TipoPermiso=TipoPermiso;

    }
    public ItemEmpleados(){

    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }

    public void setCorreoEmp(String correoEmp) {
        CorreoEmp = correoEmp;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        IdEmpleado = idEmpleado;
    }

    public void setTipoPermiso(String tipoPermiso) {
        TipoPermiso = tipoPermiso;
    }

    public void setNombreEmp(String nombreEmp) {
        NombreEmp = nombreEmp;
    }

    public void setNumControlEmp(String numControlEmp) {
        NumControlEmp = numControlEmp;
    }

    public void setUsuarioEmp(String usuarioEmp) {
        UsuarioEmp = usuarioEmp;
    }

    public Integer getIdEmpleado() {
        return IdEmpleado;
    }

    public String getTipoPermiso() {
        return TipoPermiso;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public String getCorreoEmp() {
        return CorreoEmp;
    }

    public String getNombreEmp() {
        return NombreEmp;
    }

    public String getNumControlEmp() {
        return NumControlEmp;
    }

    public String getUsuarioEmp() {
        return UsuarioEmp;
    }
}
