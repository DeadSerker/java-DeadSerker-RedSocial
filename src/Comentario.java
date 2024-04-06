import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Comentario {
    public Comentario(String texto, LocalDate fecha, Usuario usuario) {
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    private String texto;
    private LocalDate fecha;
    private Usuario usuario;
}
