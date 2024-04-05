import java.util.Date;

public class ImagenPost extends Post {
    public ImagenPost(Date fecha, String titulo, String dimensiones) {
        super(fecha, titulo);
        this.dimensiones = dimensiones;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    private String dimensiones;
}
