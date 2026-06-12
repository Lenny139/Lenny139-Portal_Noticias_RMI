package co.edu.upb.noticias.server;

import co.edu.upb.noticias.remote.NoticiaService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Punto de arranque del servidor RMI de noticias.
 *
 * Resumen del flujo RMI (util para explicar en clase):
 *   1. REGISTRO   -> es una "guia telefonica" donde los servicios se publican
 *                    bajo un nombre. El cliente lo consulta para encontrarlos.
 *   2. EXPORTAR   -> ocurre dentro de NoticiaServiceImpl (UnicastRemoteObject):
 *                    crea el stub y abre el socket que recibe las llamadas.
 *   3. BINDING    -> asocia un nombre logico ("NoticiaService") con el objeto
 *                    remoto dentro del registro.
 */
public class ServidorNoticias {

    /** Nombre logico con el que se publica el servicio en el registro. */
    public static final String NOMBRE_SERVICIO = "NoticiaService";

    /** Puerto estandar del registro RMI. */
    public static final int PUERTO = 1099;

    public static void main(String[] args) {
        try {
            // 1. Crea el RMI Registry en esta misma JVM. Es necesario porque el
            //    cliente necesita un punto conocido (host:puerto) donde buscar
            //    servicios por nombre. createRegistry evita tener que lanzar el
            //    comando externo "rmiregistry" aparte.
            Registry registry = LocateRegistry.createRegistry(PUERTO);

            // 2. Instancia el servicio. Su constructor (UnicastRemoteObject) lo
            //    exporta automaticamente: genera el stub y queda escuchando.
            NoticiaService servicio = new NoticiaServiceImpl();

            // 3. Binding: publica el objeto remoto bajo un nombre. El cliente
            //    hara lookup("NoticiaService") para obtener el stub. Se usa
            //    rebind (en vez de bind) para sobrescribir un enlace previo y
            //    no fallar si el nombre ya existia.
            registry.rebind(NOMBRE_SERVICIO, servicio);

            System.out.println("====================================================");
            System.out.println(" Servidor de Noticias UPB - RMI");
            System.out.println("====================================================");
            System.out.println(" Estado : CORRIENDO y listo para recibir clientes");
            System.out.println(" Servicio: '" + NOMBRE_SERVICIO + "'");
            System.out.println(" Puerto : " + PUERTO);
            System.out.println(" (Presiona Ctrl+C para detener)");
            System.out.println("====================================================");

            // No hace falta un bucle: el hilo de RMI mantiene viva la JVM
            // mientras el objeto remoto siga exportado.
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
