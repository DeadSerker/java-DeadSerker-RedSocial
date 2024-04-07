import java.util.Comparator;

public class ComparadorFechaPost implements Comparator<Post> {
    @Override
    public int compare(Post o1, Post o2) {
        return o1.getFecha().compareTo(o2.getFecha());
    }

}
