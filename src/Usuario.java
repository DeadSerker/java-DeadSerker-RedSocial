import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;

public class Usuario {
    public Usuario(String nombre) {
        this.nombre = nombre;
        this.usuariosSeguidos = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getUsuariosSeguidos() {
        return usuariosSeguidos;
    }

    public void setUsuariosSeguidos(List<String> usuariosSeguidos) {
        this.usuariosSeguidos = usuariosSeguidos;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    private String nombre;
    private List<String> usuariosSeguidos;
    private List<Post> posts;

    //Funcionalidades

    public void seguirUsuario(String usuario){
        if (usuariosSeguidos.contains(usuario)) System.out.println("Ya sigues a este usuario");
        else {
            usuariosSeguidos.add(usuario);
            System.out.printf("Siguiendo a %S\n",usuario);
        }
    }

    public void dejarSeguirUsuario(Usuario usuario){
        if (usuariosSeguidos.contains(usuario)){
            usuariosSeguidos.remove(usuario);
            System.out.printf("Dejando de seguir a %S",usuario.nombre);
        }else System.out.println("No sigues a este usuario");
    }
    public void añadirPost(Post post){
        //Suponiendo que pueda tener dos post iguales
        posts.add(post);
        System.out.printf("Post: %S añadido\n",post.getTitulo());
    }
    public void eliminarPost(Post post){
        if (posts.contains(post)){
            posts.remove(post);
            System.out.printf("Post: %S eliminado\n",post.getTitulo());
        }else System.out.printf("El post: %S no existe",post.getTitulo());
    }

    public void listarMisPosts(){
        for (Post post : this.posts) {
            System.out.printf("Titulo: %S\n",post.getTitulo());
        }
    }
    public void listarMisComentarios(){
        for (Post post : this.posts) {
            for (Comentario comentario : post.getComentarios()) {
                if (comentario.getUsuario().nombre==this.nombre){
                    System.out.printf("Comentario: %S\n\nFecha: %s\n\nPost: %S",
                            comentario.getTexto(),
                            comentario.getFecha(),
                            post.getTitulo());
                }
            }
        }
    }

}
