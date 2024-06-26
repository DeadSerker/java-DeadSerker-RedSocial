import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class TextPost extends Post{
    public TextPost(LocalDate fecha, String titulo, String contenido) {
        super(fecha, titulo);
        Contenido = contenido;
    }

    public String getContenido() {
        return Contenido;
    }

    public void setContenido(String contenido) {
        Contenido = contenido;
    }

    private String Contenido;
}
