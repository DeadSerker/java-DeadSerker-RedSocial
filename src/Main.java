
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        RedSocialUI.usuariosAplicacion = new ArrayList<>();
        RedSocialUI.elementPostActual = null;
        RedSocialUI.comentariosPostActual = new ArrayList<>();
        RedSocialUI.usuariosParaRecomendar = new ArrayList<>();
        boolean nueva = false;
        //String usuarioName = scanner.nextLine();
        if (RedSocialUI.cargarXML()) System.out.println("Datos cargados correctamente");
        else {
            RedSocialUI.crearXML();
            System.out.println("Red social creada como nueva");
            nueva = true;
        }
        System.out.println("Bienvenido a CanelonesTwich");
        if (nueva) {
            if (RedSocialUI.usuarioSiNoQuestion("Actualmente no hay usuarios, registrase?\n-Si\n-No")) {
                System.out.println("Introduce tu nombre");
                String nombre = scanner.nextLine();
                RedSocialUI.registrarUsuario(nombre);
                System.out.println("Usuario registrado correctamente");
                RedSocialUI.inicioSesion(nombre);
            }

        } else {
            //No es nueva
            try {
                RedSocialUI.cargarUsuarios();
                System.out.println("Introduce tu nombre para iniciar sesion");
                String nombre = scanner.nextLine();
                if (RedSocialUI.inicioSesion(nombre)) ;
                else {
                    if (RedSocialUI.usuarioSiNoQuestion("¿No se encuentra el usuario, quieres registrate? (Si/No)")) {
                        RedSocialUI.registrarUsuario(nombre);
                        System.out.println("Usuario registrado correctamente");
                        RedSocialUI.cargarUsuarios();
                        RedSocialUI.inicioSesion(nombre);
                    } else System.out.println("Saliendo...");
                }

            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

