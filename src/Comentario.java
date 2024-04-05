import java.util.Date;

public class Comentario {
    public Comentario(String texto, Date fecha, Usuario usuario) {
        this.texto = texto;
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    private String texto;
    private Date fecha;
    private Usuario usuario;
}
