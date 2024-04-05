import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    public Post(Date fecha, String titulo) {
        Fecha = fecha;
        this.comentarios = new ArrayList<>();
        this.titulo = titulo;
    }
    public Post(){}

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
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

    private Date Fecha;
    private List<Comentario> comentarios;
    private String titulo;

    public void añadirComentario(Comentario comentario){
        comentarios.add(comentario);
        System.out.println("Comentario añadido\n");
    }
    public void eliminarComentario(Comentario comentario){
        if (comentarios.contains(comentario)){
            comentarios.remove(comentario);
            System.out.println("Comentario eliminado");
        }else System.out.printf("El comentario no existe");
    }

    public void comentariosEnPost(){
        System.out.printf("El numero de comentarios del post %s son %s",this.titulo,this.comentarios.size());
    }
}
