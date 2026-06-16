package co.edu.upb.noticias.client;

import co.edu.upb.noticias.model.Noticia;
import co.edu.upb.noticias.model.ServerResponse;
import co.edu.upb.noticias.remote.NoticiaService;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/**
 * Cliente RMI interactivo para la app de noticias de la UPB.
 *
 * Se conecta al Registry, obtiene el stub del servicio remoto a partir de la
 * interfaz {@link NoticiaService} y ofrece un menu por consola. El usuario "se
 * loguea" indicando su nombre y si es administrador; esos datos se envian al
 * servidor en cada llamada que lo necesite (modelo de seguridad simple).
 */
public class ClienteNoticias {

    private static final String HOST = "localhost";
    private static final int PUERTO = 1099;
    private static final String NOMBRE_SERVICIO = "NoticiaService";

    private final NoticiaService servicio;
    private final Scanner sc;
    private String usuario;
    private boolean esAdmin;

    public ClienteNoticias(NoticiaService servicio) {
        this.servicio = servicio;
        this.sc = new Scanner(System.in);
    }

    public static void main(String[] args) {
        try {
            // 1. Localiza el Registry en host:puerto. No abre conexion todavia;
            //    solo obtiene una referencia al "directorio" de servicios.
            Registry registry = LocateRegistry.getRegistry(HOST, PUERTO);

            // 2. lookup devuelve el STUB: un proxy local que implementa la misma
            //    interfaz remota y reenvia cada llamada al servidor por la red.
            NoticiaService servicio = (NoticiaService) registry.lookup(NOMBRE_SERVICIO);

            new ClienteNoticias(servicio).iniciar();

        } catch (java.rmi.NotBoundException e) {
            System.err.println("El servicio '" + NOMBRE_SERVICIO + "' no esta publicado en el servidor.");
        } catch (RemoteException e) {
            // Caso tipico: el servidor no esta levantado o el puerto es otro.
            System.err.println("No se pudo conectar al servidor, ¿esta corriendo? (host="
                    + HOST + ", puerto=" + PUERTO + ")");
        } catch (Exception e) {
            System.err.println("Error inesperado al iniciar el cliente: " + e.getMessage());
        }
    }

    /** Login simple + bucle del menu. */
    private void iniciar() {
        System.out.println("=== Noticias UPB - Cliente RMI ===");
        login();

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = leer("Elige una opcion: ");
            switch (opcion) {
                case "1": listarNoticias();   break;
                case "2": leerNoticia();       break;
                case "3": buscarNoticias();    break;
                case "4": crearNoticia();      break;
                case "5": modificarNoticia();  break;
                case "6": eliminarNoticia();   break;
                case "7":
                    System.out.println("Cerrando sesion de " + usuario + "...");
                    login(); // vuelve a la pantalla de login sin cerrar el programa
                    break;
                case "0":
                    salir = true;
                    System.out.println("Hasta luego, " + usuario + ".");
                    break;
                default:
                    System.out.println("Opcion no valida, intenta de nuevo.");
            }
        }
    }

    /**
     * Login real: el servidor valida las credenciales. La cuenta "admin"
     * requiere contrasena; cualquier otro usuario entra solo con su nombre.
     * Se repite hasta que el ingreso sea valido.
     */
    private void login() {
        System.out.println("\n----------- INICIAR SESION -----------");
        while (true) {
            String user = leer("Usuario: ").trim();
            if (user.isEmpty()) {
                System.out.println("Debes ingresar un usuario.\n");
                continue;
            }
            // Solo la cuenta admin pide contrasena.
            String pass = "";
            if (user.equalsIgnoreCase("admin")) {
                pass = leer("Contrasena: ").trim();
            }
            try {
                ServerResponse r = servicio.iniciarSesion(user, pass);
                if (r.getStatus() == ServerResponse.Status.OK) {
                    this.usuario = user;
                    this.esAdmin = Boolean.TRUE.equals(r.getData());
                    System.out.println(r.getMensaje()
                            + (esAdmin ? " [rol: administrador]" : " [rol: usuario]") + "\n");
                    return;
                }
                System.out.println("[X] " + r.getMensaje() + " Intenta de nuevo.\n");
            } catch (RemoteException e) {
                errorConexion(e);
                return;
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\n---------------- MENU ----------------");
        System.out.println(" 1. Listar noticias");
        System.out.println(" 2. Leer noticia por nombre unico");
        System.out.println(" 3. Buscar noticias por palabra clave");
        System.out.println(" 4. Crear noticia");
        System.out.println(" 5. Modificar noticia");
        System.out.println(" 6. Eliminar noticia");
        System.out.println(" 7. Cerrar sesion");
        System.out.println(" 0. Salir");
        System.out.println("--------------------------------------");
    }

    /* ----------------------- Opciones del menu ----------------------- */

    private void listarNoticias() {
        try {
            List<Noticia> noticias = servicio.listarNoticias();
            if (noticias.isEmpty()) {
                System.out.println("No hay noticias registradas.");
                return;
            }
            System.out.println("\nNoticias (" + noticias.size() + "):");
            for (Noticia n : noticias) {
                imprimirResumen(n);
            }
        } catch (RemoteException e) {
            errorConexion(e);
        }
    }

    private void leerNoticia() {
        String id = leer("Nombre unico de la noticia: ").trim();
        try {
            ServerResponse r = servicio.leerNoticia(id);
            mostrarRespuesta(r);
            if (r.getStatus() == ServerResponse.Status.OK && r.getData() instanceof Noticia) {
                imprimirDetalle((Noticia) r.getData());
            }
        } catch (RemoteException e) {
            errorConexion(e);
        }
    }

    private void buscarNoticias() {
        String clave = leer("Palabra clave: ").trim();
        try {
            List<Noticia> noticias = servicio.buscarNoticias(clave);
            if (noticias.isEmpty()) {
                System.out.println("Sin coincidencias para '" + clave + "'.");
                return;
            }
            System.out.println("\nCoincidencias (" + noticias.size() + "):");
            for (Noticia n : noticias) {
                imprimirResumen(n);
            }
        } catch (RemoteException e) {
            errorConexion(e);
        }
    }

    private void crearNoticia() {
        String id = leer("Nombre unico (identificador): ").trim();
        String titular = leer("Titular: ");
        String contenido = leer("Contenido: ");

        Noticia n = new Noticia();
        n.setNombreUnico(id);
        n.setTitular(titular);
        n.setContenido(contenido);
        try {
            // El servidor asigna autor y fechas a partir del usuario actual.
            ServerResponse r = servicio.crearNoticia(n, usuario);
            mostrarRespuesta(r);
        } catch (RemoteException e) {
            errorConexion(e);
        }
    }

    private void modificarNoticia() {
        String id = leer("Nombre unico de la noticia a modificar: ").trim();
        String titular = leer("Nuevo titular: ");
        String contenido = leer("Nuevo contenido: ");

        Noticia cambios = new Noticia();
        cambios.setTitular(titular);
        cambios.setContenido(contenido);
        try {
            ServerResponse r = servicio.modificarNoticia(id, cambios, usuario, esAdmin);
            mostrarRespuesta(r);
        } catch (RemoteException e) {
            errorConexion(e);
        }
    }

    private void eliminarNoticia() {
        String id = leer("Nombre unico de la noticia a eliminar: ").trim();
        try {
            ServerResponse r = servicio.eliminarNoticia(id, usuario, esAdmin);
            mostrarRespuesta(r);
        } catch (RemoteException e) {
            errorConexion(e);
        }
    }

    /* ----------------------- Utilidades de UI ----------------------- */

    private String leer(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    /** Muestra el ServerResponse con un prefijo claro segun el status. */
    private void mostrarRespuesta(ServerResponse r) {
        switch (r.getStatus()) {
            case OK:
                System.out.println("[OK] Exito: " + r.getMensaje());
                break;
            case NOT_OK:
                System.out.println("[X] Error: " + r.getMensaje());
                break;
            case FAIL:
            default:
                System.out.println("[!] Fallo del servidor: " + r.getMensaje());
        }
    }

    private void imprimirResumen(Noticia n) {
        System.out.println("  - " + n.getNombreUnico() + " | " + n.getTitular()
                + " (autor: " + n.getAutor() + ")");
    }

    private void imprimirDetalle(Noticia n) {
        System.out.println("  Titular   : " + n.getTitular());
        System.out.println("  Autor     : " + n.getAutor());
        System.out.println("  Creada    : " + n.getFechaCreacion());
        System.out.println("  Actualizada: " + n.getFechaActualizacion());
        System.out.println("  Contenido : " + n.getContenido());
    }

    /** Mensaje amigable cuando una llamada remota falla en mitad de la sesion. */
    private void errorConexion(RemoteException e) {
        if (e instanceof ConnectException) {
            System.out.println("[!] No se pudo conectar al servidor, ¿sigue corriendo?");
        } else {
            System.out.println("[!] Error de comunicacion con el servidor: " + e.getMessage());
        }
    }
}
