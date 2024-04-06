import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public Post(LocalDateTime fecha, String titulo) {
        Fecha = fecha;
        this.comentarios = new ArrayList<>();
        this.titulo = titulo;
        this.idPost = "-1";
    }

    public Post() {
    }

    public LocalDateTime getFecha() {
        return Fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        Fecha = fecha;

    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    private LocalDateTime Fecha;
    private List<Comentario> comentarios;
    private String titulo;

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    private String idPost;

    public void añadirComentario(Comentario comentario) {
        comentarios.add(comentario);
        System.out.println("Comentario añadido\n");
    }

    public void eliminarComentario(Comentario comentario) {
        if (comentarios.contains(comentario)) {
            comentarios.remove(comentario);
            System.out.println("Comentario eliminado");
        } else System.out.printf("El comentario no existe");
    }

    public void comentariosEnPost() {
        System.out.printf("El numero de comentarios del post %s son %s", this.titulo, this.comentarios.size());
    }
}
