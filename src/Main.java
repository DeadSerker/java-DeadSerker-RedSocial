import jdk.dynalink.beans.StaticClass;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.text.ElementIterator;
import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Document documento;
    private static List<Usuario> usuariosAplicacion;
    private static Usuario usuarioActual;
    private static List<Post> postAplicacion;
    private static int indice;
    private static Element elementUsuarioActual;
    private static Element elementPostActual;
    private static List<String> comentariosPostActual;
    private static Map<String, List<Comentario>> comentariosAplicacion;

    public static void main(String[] args) {
        usuariosAplicacion = new ArrayList<>();
        postAplicacion = new ArrayList<>();
        indice = 0;
        comentariosAplicacion = new HashMap<>();
        elementPostActual = null;
        comentariosPostActual = new ArrayList<>();
        boolean nueva = false;
        //String usuarioName = scanner.nextLine();
        if (cargarXML()) System.out.println("Datos cargados correctamente");
        else {
            crearXML();
            System.out.println("Red social creada como nueva");
            nueva = true;
        }
        System.out.println("Bienvenido a CanelonesTwich");
        if (nueva) {
            if (usuarioSiNoQuestion("Actualmente no hay usuarios, registrase?\n-Si\n-No")) {
                System.out.println("Introduce tu nombre");
                String nombre = scanner.nextLine();
                registrarUsuario(nombre);
                System.out.println("Usuario registrado correctamente");
                inicioSesion(nombre);
            }

        } else {
            //No es nueva
            try {
                cargarUsuarios();
                System.out.println("Introduce tu nombre para iniciar sesion");
                String nombre = scanner.nextLine();
                if (inicioSesion(nombre)) ;
                else {
                    if (usuarioSiNoQuestion("¿No se encuentra el usuario, quieres registrate? (Si/No)")) {
                        registrarUsuario(nombre);
                        System.out.println("Usuario registrado correctamente");
                        cargarUsuarios();
                        inicioSesion(nombre);
                    } else System.out.println("Saliendo...");
                }

            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static void cargarPostAplicacion() {
        NodeList posts = documento.getElementsByTagName("Post");
        for (int i = 0; i < posts.getLength(); i++) {
            Element post = (Element) posts.item(i);
            System.out.printf("Titulo del post: %S\nID: %S\nFecha: %S\n"
                    , post.getAttribute("titulo")
                    , post.getAttribute("idPost")
                    ,post.getAttribute("fecha"));
        }

    }

    private static boolean inicioSesion(String nombre) {
        NodeList listaUsuarios = documento.getElementsByTagName("Usuario");
        String buscarNombre;
        boolean inicioCorrecto = true;

        for (int i = 0; i < listaUsuarios.getLength(); i++) {
            Element usuarioElement = (Element) listaUsuarios.item(i);
            buscarNombre = usuarioElement.getAttribute("nombre");
            //Si encuentra el usuario...
            if (buscarNombre.equalsIgnoreCase(nombre)) {
                elementUsuarioActual = usuarioElement;

                inicioCorrecto = true;
                usuarioActual = new Usuario(nombre);
                //Cargo los seguidos
                Element usuariosSeguidos = (Element) elementUsuarioActual.getElementsByTagName("UsuariosSeguidos").item(0);

                if (usuariosSeguidos != null) {
                    NodeList usuariosSeguidosList = usuariosSeguidos.getElementsByTagName("UsuarioSeguido");
                    List<String> usuariosSeguidosAux = new ArrayList<>();
                    for (int j = 0; j < usuariosSeguidosList.getLength(); j++) {
                        Element usuarioSeguido = (Element) usuariosSeguidosList.item(j);
                        if (usuarioSeguido != null) {
                            String nomUsuario = usuarioSeguido.getAttribute("nombre");
                            if (!usuariosSeguidosAux.contains(nomUsuario)) usuariosSeguidosAux.add(nomUsuario);
                        }
                    }
                    usuarioActual.setUsuariosSeguidos(usuariosSeguidosAux);
                }

                Element postPropios = (Element) elementUsuarioActual.getElementsByTagName("Posts").item(0);
                if (postPropios != null) {
                    NodeList postPropiosList = postPropios.getElementsByTagName("Post");
                    List<Post>potsUserAux = new ArrayList<>();
                    for (int j = 0; j < postPropiosList.getLength(); j++) {
                        Element postElement = (Element) postPropiosList.item(j);
                        Post post = new Post();
                        post.setTitulo(postElement.getAttribute("titulo"));
                        post.setFecha(fechaFormateada(postElement.getAttribute("fecha")));
                        post.setIdPost(postElement.getAttribute("idPost"));
                        NodeList comentarios = postElement.getElementsByTagName("Comentario");
                        List<Comentario> comentariosUsuario = new ArrayList<>();
                        for (int k = 0; k < comentarios.getLength(); k++) {
                            Element comentarioElement = (Element) comentarios.item(k);
                            comentariosUsuario.add(new Comentario(comentarioElement.getTextContent()
                                    , fechaFormateada(comentarioElement.getAttribute("fecha"))
                                    , new Usuario(comentarioElement.getAttribute("usuario"))));
                        }
                        post.setComentarios(comentariosUsuario);
                        potsUserAux.add(post);
                    }
                    usuarioActual.setPosts(potsUserAux);
                }
                break;
            } else inicioCorrecto = false;
        }


        if (inicioCorrecto) {

            System.out.println("Bienvenido: " + nombre);
            menuPrincipal();
            return true;
        } else {
            System.out.println("Inicio de sesion incorrecto");
            return false;
        }
    }

    private static void menuPrincipal() {
        int opcion;
        do {
            System.out.printf("++++++++++++++++++++++++++++++\n" +
                    "Acciones:\n1-Ver Usuarios seguidos" +
                    "\n2-Ver Posts propios" +
                    "\n3-Ver todos los posts" +
                    "\n4-Ver todos los usuarios" +
                    "\n5-Añadir/Seguir" +
                    "\n6-Eliminar/Dejar de seguir" +
                    "\n7-Acceder a un post" +
                    "\n8-Salir\n" +
                    "++++++++++++++++++++++++++++++\n");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = 100;
            }

            switch (opcion) {
                case 1:
                    System.out.println("Usuarios que sigues");
                    mostrarUsuariosSigo();
                    break;
                case 2:
                    System.out.println("Post propios:");
                    usuarioActual.listarMisPosts();
                    break;
                case 3:
                    System.out.println("Mostrando posts de la aplicacion");
                    cargarPostAplicacion();
                    break;
                case 4:
                    System.out.println("Usuarios de la aplicacion");
                    try {
                        cargarUsuarios();
                    } catch (XPathExpressionException e) {
                        throw new RuntimeException(e);
                    }
                    for (Usuario usuario : usuariosAplicacion) {
                        System.out.println(usuario.getNombre());
                    }
                    break;
                case 5:
                    System.out.println("Que quieres añadir?");
                    menuCrear();
                    break;
                case 6:
                    System.out.println("Que quieres eliminar?");
                    menuEliminar();
                    break;
                case 7:
                    ///////////////////////////////////
                    System.out.println("Selecciona el id del post al que quieres entrar: ");
                    String postId = scanner.nextLine();
                    entrarEnPost(postId);
                    break;
                case 8:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Elige una opcion valida");
            }
        } while (opcion != 8);
    }
    private static void entrarEnPost(String idPost){
        elementPostActual = null;
        NodeList posts = documento.getElementsByTagName("Post");
        for (int i = 0; i < posts.getLength(); i++) {
            Element post = (Element) posts.item(i);
            if (post.getAttribute("idPost").equalsIgnoreCase(idPost)){
                elementPostActual = post;
                break;
            }
        }
        if (elementPostActual!=null){
            System.out.println("Entrando en el post: "+elementPostActual.getAttribute("titulo"));
            System.out.println("Comentarios: ");
            separadorCustom();
            NodeList comentariosListNodes = elementPostActual.getElementsByTagName("Comentario");

        }else System.out.println("No se encuentra ningun post con ese id");

    }
    private static void menuCrear() {
        int opcion;
        do {
            System.out.println("1-Seguir usuario\n2-Añadir Post\n-3 Salir");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = 6;
            }
            switch (opcion) {
                case 1:
                    System.out.println("A quien quieres seguir?");
                    String nombrePersona = scanner.nextLine();
                    boolean existe = false;
                    boolean soyYo = false;
                    try {
                        cargarUsuarios();
                    } catch (XPathExpressionException e) {
                        throw new RuntimeException(e);
                    }
                    for (Usuario usuario : usuariosAplicacion) {
                        if (usuario.getNombre().equalsIgnoreCase(nombrePersona)) {
                            existe = true;
                            if (nombrePersona.equalsIgnoreCase(usuarioActual.getNombre())) soyYo=true;
                            else soyYo=false;
                            break;
                        } else {
                            existe = false;
                        }
                    }
                    if (existe && !soyYo) {
                        if(usuarioActual.seguirUsuario(nombrePersona)){
                            Element nuevoSeguido = documento.createElement("UsuarioSeguido");
                            nuevoSeguido.setAttribute("nombre", nombrePersona);
                            elementUsuarioActual.getElementsByTagName("UsuariosSeguidos").item(0).appendChild(nuevoSeguido);
                            System.out.println("Seguiendo a "+ nombrePersona);
                            trasformerAux();
                        }else System.out.println("Ya sigues a este usuario");

                    } else System.out.println("El usuario que buscas no existe o eres tu mismo rufian");
                    break;
                case 2:
                    System.out.println("Escribe el titulo del post a crear");
                    String titulo = scanner.nextLine();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    String fechaFormateada = LocalDateTime.now().format(formatter);
                    LocalDateTime fecha = LocalDateTime.parse(fechaFormateada, formatter);

                    Post post = new Post(fecha, titulo);
                    //NodeList posts = elementUsuarioActual.getElementsByTagName("Posts");
                    NodeList posts = documento.getElementsByTagName("Post");
                    String actualPostID = "0";
                    if (posts.getLength()!=0){
                        Element ultimoPost = (Element) posts.item(posts.getLength() - 1);
                        actualPostID = (Integer.parseInt(ultimoPost.getAttribute("idPost")) + 1) + "";
                    }

                    post.setIdPost(actualPostID);
                    Comentario comentario;
                    Element raizPosts = (Element) elementUsuarioActual.getElementsByTagName("Posts").item(0);
                    Element nuevoPost = documento.createElement("Post");
                    if (usuarioSiNoQuestion("¿Quieres añadir un primer comentario? (Si/No)")) {
                        //Quiere añadir un comentario
                        System.out.println("Introduce el comentario: -->");
                        String texto = scanner.nextLine();
                        comentario = new Comentario(texto, fecha, usuarioActual);
                        Element comentarioElement = documento.createElement("Comentario");
                        comentarioElement.setAttribute("usuario", usuarioActual.getNombre());
                        comentarioElement.setAttribute("fecha", fecha.toString());
                        comentarioElement.setTextContent(texto);
                        nuevoPost.appendChild(comentarioElement);
                        System.out.println("Comentario añadido");
                    }
                    nuevoPost.setAttribute("titulo", titulo);
                    nuevoPost.setAttribute("fecha", post.getFecha().toString());
                    nuevoPost.setAttribute("idPost", actualPostID);
                    raizPosts.appendChild(nuevoPost);
                    usuarioActual.añadirPost(post);
                    trasformerAux();

                    System.out.println("Post creado");
                    break;
                case 3:
                    System.out.println("Saliendo");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }

        } while (opcion != 3);
    }

    private static boolean usuarioSiNoQuestion(String mensaje) {
        String respuesta;
        do {
            System.out.println(mensaje);
            respuesta = scanner.nextLine();
        } while (!(respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("no")));
        if (respuesta.equalsIgnoreCase("si")) return true;
        else return false;
    }

    private static void menuEliminar() {
        int opcion;
        do {
            System.out.println("1-Dejar de seguir usuario\n2-Eliminar Post\n3-Salir");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = 6;
            }
            switch (opcion){
                case 1:
                    System.out.println("A quien quieres dejar de seguir?\nLos usuarios que sigues son:");
                    mostrarUsuariosSigo();
                    separadorCustom();
                    String nombrePersona = scanner.nextLine();
                    boolean existe = false;
                    boolean soyYo = false;
                    try {
                        //No creo que sea necesario
                        cargarUsuarios();
                    } catch (XPathExpressionException e) {
                        throw new RuntimeException(e);
                    }
                    for (String usuario : usuarioActual.getUsuariosSeguidos()) {
                        if (usuario.equalsIgnoreCase(nombrePersona)) {
                            existe = true;
                            if (nombrePersona.equalsIgnoreCase(usuarioActual.getNombre())) soyYo=true;
                            else soyYo=false;
                            break;
                        } else {
                            existe = false;
                        }
                    }
                    if (existe && !soyYo) {
                        //Eliminar el usuario de seguidos
                        StringBuilder ruta = new StringBuilder();
                        ruta.append("//Usuario[@nombre='")
                                .append(usuarioActual.getNombre())
                                .append("']/UsuariosSeguidos/UsuarioSeguido[@nombre='")
                                .append(nombrePersona).append("']");
                        Node nodoEliminar = buscarNodoXpath(ruta.toString());
                        try {
                            Node nodoPadreEliminar = nodoEliminar.getParentNode();
                            nodoPadreEliminar.removeChild(nodoEliminar);
                            usuarioActual.dejarSeguirUsuario(nombrePersona);
                            trasformerAux();
                        }catch (Exception e){
                            System.out.println("Error al eliminar el seguido");
                        }

                    } else System.out.println("No sigues a este usuario o eres tu mismo rufian");
                    break;
                case 2:
                    System.out.println("Escribe el id del post a eliminar\nTus posts son:");
                    usuarioActual.listarMisPosts();
                    separadorCustom();
                    String id = "-1";
                    try {
                        id = scanner.nextLine();
                    }catch (Exception e){
                        id = "-1";
                    }
                    StringBuilder ruta = new StringBuilder();
                    ruta.append("//Usuario[@nombre='")
                            .append(usuarioActual.getNombre())
                            .append("']/Posts/Post[@idPost='")
                            .append(id)
                            .append("']");
                    Node nodoBorrar = buscarNodoXpath(ruta.toString());
                    if (nodoBorrar!=null){
                        Node nodoPadreEliminar = nodoBorrar.getParentNode();
                        nodoPadreEliminar.removeChild(nodoBorrar);
                        final String idComparar = id;
                        usuarioActual.getPosts().removeIf(c->c.getIdPost().equalsIgnoreCase(idComparar));
                        trasformerAux();

                    }else{
                        System.out.println("No hay nada que borrar con ese id");
                    }

                    System.out.println("Post Eliminado");
                    break;
                case 3:
                    System.out.println("Saliendo");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }while (opcion!=3);
    }
    private static Node buscarNodoXpath(String ruta){
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression consulta = xpath.compile(ruta);
            Node resultado = (Node)consulta.evaluate(documento,XPathConstants.NODE);
            return resultado;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
    private static void mostrarUsuariosSigo(){
        for (String usuariosSeguido : usuarioActual.getUsuariosSeguidos()) {
            System.out.println(usuariosSeguido);
        }
    }
    private static void separadorCustom(){
        System.out.println("+++++<-------->+++++");
    }
    public static LocalDateTime fechaFormateada(String fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.parse(fecha, formatter);
    }

    private static void registrarUsuario(String nombre) {
        try {
            Element usuariosTag = (Element) documento.getElementsByTagName("Usuarios").item(0);
            Element usuarioNuevo = documento.createElement("Usuario");
            usuarioNuevo.setAttribute("nombre", nombre);
            //Creamos sus nodos hijos vacios de Usuarios seguidos y Posts para rellenarlos despues
            Element usuariosSeguidos = documento.createElement("UsuariosSeguidos");
            Element PostsUsuario = documento.createElement("Posts");
            usuarioNuevo.appendChild(usuariosSeguidos);
            usuarioNuevo.appendChild(PostsUsuario);
            usuariosTag.appendChild(usuarioNuevo);

            trasformerAux();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void cargarUsuarios() throws XPathExpressionException {
        usuariosAplicacion = new ArrayList<>();
        XPathFactory navegadorFactory = XPathFactory.newDefaultInstance();
        XPath navegador = navegadorFactory.newXPath();
        String lineaBusqueda = "//Usuarios/Usuario";
        XPathExpression busqueda = navegador.compile(lineaBusqueda);
        NodeList resultado = (NodeList) busqueda.evaluate(documento, XPathConstants.NODESET);
        for (int i = 0; i < resultado.getLength(); i++) {
            Node nodo = resultado.item(i);
            NamedNodeMap atributos = nodo.getAttributes();
            usuariosAplicacion.add(new Usuario(atributos.getNamedItem("nombre").getTextContent()));
        }
    }

    private static boolean cargarXML() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            //Cogemos el documento que ya existe
            documento = db.parse(new File("src/../resources/redSocial.xml"));
            return true;
        } catch (IOException e) {
            return false;
        } catch (ParserConfigurationException e) {
            return false;
        } catch (SAXException e) {
            return false;
        }
    }

    private static void trasformerAux() {
        try {
            //Trasformer trasforma archivos DOM en XML, o en otros
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer trasformer = tff.newTransformer();
            //Crea un DOM que tiene el documento que quiero manejar
            DOMSource sourrce = new DOMSource(documento);
            //StreamResult crea el archivo fisico dentro de la ruta que le paso
            StreamResult result = new StreamResult(new File("src/../resources/RedSocial.xml"));
            //Trasforma el archivo de source en el archivo que esta en result
            trasformer.transform(sourrce, result);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private static void crearXML() {
        try {
            //Creo el manejador del documento
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            //Creo el documento
            documento = db.newDocument();
            //Creo la raiz
            Element raiz = documento.createElement("RedSocial");
            documento.appendChild(raiz);
            //Creo el apartado items
            Element items = documento.createElement("Usuarios");
            raiz.appendChild(items);
            trasformerAux();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
