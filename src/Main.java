import jdk.dynalink.beans.StaticClass;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Document documento;
    private static List<Usuario> usuariosAplicacion;
    private static Usuario usuarioActual;
    private static List<Post> postAplicacion;

    public static void main(String[] args) {
        usuariosAplicacion = new ArrayList<>();
        postAplicacion = new ArrayList<>();
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
            String respuesta;
            do {
                System.out.println("Actualmente no hay usuarios, registrase?\n-Si\n-No");
                respuesta = scanner.nextLine();
                if (respuesta.equalsIgnoreCase("si")) {
                    System.out.println("Introduce tu nombre");
                    String nombre = scanner.nextLine();
                    registrarUsuario(nombre);
                    System.out.println("Usuario registrado correctamente");
                    inicioSesion(nombre);
                }
            } while (!(respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("no")));


        } else {
            //No es nueva
            try {
                cargarUsuarios();
                System.out.println("Introduce tu nombre para iniciar sesion");
                String nombre = scanner.nextLine();
                if (inicioSesion(nombre)) System.out.println("Sesion correcta");
                else{
                    String respuesta;
                    do {
                        System.out.println("¿No se encuentra el usuario, quieres registrate? (Si/No)");
                        respuesta = scanner.nextLine();
                    }while (!(respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("no")));
                    if (respuesta.equalsIgnoreCase("si")){
                        registrarUsuario(nombre);
                        System.out.println("Usuario registrado correctamente");
                        inicioSesion(nombre);
                    }else System.out.println("Saliendo...");
                }

            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private static void cargarPostAplicacion(){
        NodeList posts = documento.getElementsByTagName("Post");
        if (posts!=null){
            for (int i = 0; i < posts.getLength(); i++) {
                Element post = (Element) posts.item(i);
                System.out.printf("Titulo del post: %S\nContenido:\n%S",post.getAttribute("titulo"));
            }
        }else{
            System.out.println("No hay posts para mostrar");
        }
    }
    private static boolean inicioSesion(String nombre) {
        Node usuariosList = (Element) documento.getElementsByTagName("Usuarios").item(0);
        NodeList listaUsuarios = usuariosList.getChildNodes();
        String buscarNombre;
        boolean inicioCorrecto = true;

            for (int i = 0; i < listaUsuarios.getLength(); i++) {
                Element usuarioElement = (Element) listaUsuarios.item(1);
                buscarNombre = usuarioElement.getAttribute("nombre");
                //Si encuentra el usuario...
                if (buscarNombre.equalsIgnoreCase(nombre)) {
                    Node usuario = listaUsuarios.item(0);
                    inicioCorrecto = true;
                    usuarioActual = new Usuario(nombre);
                    //Cargo los seguidos
                    Node usuariosSeguidos = usuario.getChildNodes().item(0);

                    if (usuariosSeguidos!=null){
                        NodeList usuariosSeguidosList = usuariosSeguidos.getChildNodes();
                        for (int j = 0; j < usuariosSeguidosList.getLength(); j++) {
                            usuarioActual.seguirUsuario(usuariosSeguidos.getChildNodes().item(i).getTextContent());
                        }
                    }

                    Node postPropios = usuario.getChildNodes().item(1);
                    if (postPropios!=null){
                        NodeList postPropiosList = postPropios.getChildNodes();
                        for (int j = 0; j < postPropiosList.getLength(); j++) {
                            Node postNode = postPropios.getChildNodes().item(i);
                            Post post = new Post();
                            post.setTitulo(postNode.getAttributes().getNamedItem("titulo").getTextContent());
                            post.setFecha(fechaFormateada(postNode.getAttributes().getNamedItem("fecha").getTextContent()));
                            //Faltan los comentarios--------------------
                        }
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
                    "\n5-Salir\n" +
                    "++++++++++++++++++++++++++++++\n");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    System.out.println("Usuarios que sigues");
                    for (String usuariosSeguido : usuarioActual.getUsuariosSeguidos()) {
                        System.out.println(usuariosSeguido);
                    }
                    break;
                case 2:
                    System.out.println("Post propios:");
                    usuarioActual.listarMisPosts();
                    break;
                case 3:

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
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Elige una opcion valida");
            }
        } while (opcion != 5);
    }

    private static Date fechaFormateada(String fecha) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaAux = format.parse(fecha);
            return fechaAux;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
            StreamResult result = new StreamResult(new File("src/../resources/redSocial.xml"));
            //Trasforma el archivo de source en el archivo que esta en result
            trasformer.transform(sourrce, result);
            System.out.println("Datos XML creados");
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
