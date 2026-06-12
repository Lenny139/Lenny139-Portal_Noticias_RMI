package co.edu.upb.noticias.remote;

import co.edu.upb.noticias.model.Noticia;
import co.edu.upb.noticias.model.ServerResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Contrato remoto del servicio de noticias.
 *
 * Extiende {@link Remote} para que RMI sepa que sus metodos pueden invocarse
 * desde otra JVM. Cada metodo declara {@code throws RemoteException} porque
 * cualquier llamada puede fallar por problemas de red o del transporte RMI.
 *
 * Esta interfaz es lo unico que el cliente necesita conocer: trabaja contra el
 * contrato, no contra la implementacion concreta del servidor.
 */
public interface NoticiaService extends Remote {

    /**
     * Devuelve una noticia por su identificador unico.
     * La noticia encontrada viaja dentro de {@link ServerResponse#getData()}.
     */
    ServerResponse leerNoticia(String nombreUnico) throws RemoteException;

    /** Lista todas las noticias disponibles. */
    List<Noticia> listarNoticias() throws RemoteException;

    /** Busca noticias cuyo titular o contenido contenga la palabra clave. */
    List<Noticia> buscarNoticias(String palabraClave) throws RemoteException;

    /**
     * Crea una noticia nueva.
     *
     * @param noticia      datos de la noticia a crear
     * @param autorActual  usuario que realiza la creacion (queda como autor)
     */
    ServerResponse crearNoticia(Noticia noticia, String autorActual) throws RemoteException;

    /**
     * Modifica una noticia existente.
     *
     * @param nombreUnico        identificador de la noticia a modificar
     * @param noticiaActualizada nuevos datos
     * @param usuarioActual      usuario que solicita la modificacion
     * @param esAdmin            si es admin puede modificar noticias de otros autores
     */
    ServerResponse modificarNoticia(String nombreUnico, Noticia noticiaActualizada,
                                    String usuarioActual, boolean esAdmin) throws RemoteException;

    /**
     * Elimina una noticia existente.
     *
     * @param nombreUnico   identificador de la noticia a eliminar
     * @param usuarioActual usuario que solicita la eliminacion
     * @param esAdmin       si es admin puede eliminar noticias de otros autores
     */
    ServerResponse eliminarNoticia(String nombreUnico, String usuarioActual,
                                   boolean esAdmin) throws RemoteException;
}
