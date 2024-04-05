import java.util.Date;

public class VideoPost extends Post{
    public VideoPost(Date fecha, String titulo, String calidad, double duracionSeg) {
        super(fecha, titulo);
        this.calidad = calidad;
        this.duracionSeg = duracionSeg;
    }

    public String getCalidad() {
        return calidad;
    }

    public void setCalidad(String calidad) {
        this.calidad = calidad;
    }

    private String calidad;
    private double duracionSeg;
}