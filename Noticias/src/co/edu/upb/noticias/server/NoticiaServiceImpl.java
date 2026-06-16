package co.edu.upb.noticias.server;

import co.edu.upb.noticias.model.Noticia;
import co.edu.upb.noticias.model.ServerResponse;
import co.edu.upb.noticias.remote.NoticiaService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementacion concreta del servicio remoto de noticias.
 *
 * Extiende {@link UnicastRemoteObject}: al heredar de el (y llamar a super() en
 * el constructor) el objeto queda "exportado", es decir, RMI le crea un stub y
 * abre un socket para escuchar llamadas remotas. Sin esa exportacion el objeto
 * seria una clase normal que solo se podria usar dentro de esta misma JVM.
 *
 * El almacenamiento es en memoria ({@link ConcurrentHashMap}) para mantener el
 * ejemplo simple; en un proyecto real se reemplazaria por una base de datos.
 * Se usa una estructura concurrente porque RMI atiende cada llamada del cliente
 * en su propio hilo, y varios clientes podrian escribir a la vez.
 */
public class NoticiaServiceImpl extends UnicastRemoteObject implements NoticiaService {

    private static final long serialVersionUID = 1L;

    /** Credenciales del administrador (login real validado en el servidor). */
    private static final String ADMIN_USUARIO = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    /** Mapa nombreUnico -> Noticia. */
    private final Map<String, Noticia> noticias = new ConcurrentHashMap<>();

    public NoticiaServiceImpl() throws RemoteException {
        super(); // exporta este objeto remoto (obligatorio en UnicastRemoteObject)
        precargarNoticias();
    }

    /** Inserta unas noticias de ejemplo para que el servidor arranque con datos. */
    private void precargarNoticias() {
        LocalDateTime ahora = LocalDateTime.now();

        Noticia n1 = new Noticia("bienvenida-2026",
                "Bienvenida a los nuevos estudiantes",
                ahora.minusDays(5), ahora.minusDays(5),
                "admin",
                "La UPB da la bienvenida al semestre 2026-1 a todos sus estudiantes.");

        Noticia n2 = new Noticia("semana-investigacion",
                "Semana de la investigacion UPB",
                ahora.minusDays(3), ahora.minusDays(3),
                "juan",
                "Del 15 al 19 de junio se realizara la semana de la investigacion con ponencias.");

        Noticia n3 = new Noticia("torneo-futbol",
                "Inscripciones abiertas al torneo de futbol",
                ahora.minusDays(1), ahora.minusDays(1),
                "maria",
                "Ya estan abiertas las inscripciones para el torneo interfacultades de futbol.");

        noticias.put(n1.getNombreUnico(), n1);
        noticias.put(n2.getNombreUnico(), n2);
        noticias.put(n3.getNombreUnico(), n3);
    }

    @Override
    public ServerResponse iniciarSesion(String usuario, String password) throws RemoteException {
        if (usuario == null || usuario.isBlank()) {
            return ServerResponse.notOk("El nombre de usuario no puede estar vacio.");
        }
        // La cuenta de administrador exige contrasena correcta.
        if (usuario.equalsIgnoreCase(ADMIN_USUARIO)) {
            if (ADMIN_PASSWORD.equals(password)) {
                return ServerResponse.ok("Bienvenido administrador.", Boolean.TRUE);
            }
            return ServerResponse.notOk("Contrasena incorrecta para el administrador.");
        }
        // Cualquier otro usuario entra solo con su nombre (rol normal).
        return ServerResponse.ok("Bienvenido " + usuario + ".", Boolean.FALSE);
    }

    @Override
    public ServerResponse leerNoticia(String nombreUnico) throws RemoteException {
        Noticia noticia = noticias.get(nombreUnico);
        if (noticia == null) {
            return ServerResponse.notOk("No existe una noticia con el identificador: " + nombreUnico);
        }
        return ServerResponse.ok("Noticia encontrada.", noticia);
    }

    @Override
    public List<Noticia> listarNoticias() throws RemoteException {
        return new ArrayList<>(noticias.values());
    }

    @Override
    public List<Noticia> buscarNoticias(String palabraClave) throws RemoteException {
        List<Noticia> resultado = new ArrayList<>();
        if (palabraClave == null || palabraClave.isBlank()) {
            return resultado;
        }
        String clave = palabraClave.toLowerCase();
        for (Noticia n : noticias.values()) {
            boolean enTitular = n.getTitular() != null
                    && n.getTitular().toLowerCase().contains(clave);
            boolean enContenido = n.getContenido() != null
                    && n.getContenido().toLowerCase().contains(clave);
            if (enTitular || enContenido) {
                resultado.add(n);
            }
        }
        return resultado;
    }

    @Override
    public ServerResponse crearNoticia(Noticia noticia, String autorActual) throws RemoteException {
        if (noticia == null || noticia.getNombreUnico() == null || noticia.getNombreUnico().isBlank()) {
            return ServerResponse.notOk("La noticia debe tener un nombreUnico valido.");
        }
        if (noticias.containsKey(noticia.getNombreUnico())) {
            return ServerResponse.notOk("Ya existe una noticia con ese identificador.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        noticia.setAutor(autorActual);
        noticia.setFechaCreacion(ahora);
        noticia.setFechaActualizacion(ahora);

        noticias.put(noticia.getNombreUnico(), noticia);
        return ServerResponse.ok("Noticia creada correctamente.", noticia);
    }

    @Override
    public ServerResponse modificarNoticia(String nombreUnico, Noticia noticiaActualizada,
                                           String usuarioActual, boolean esAdmin) throws RemoteException {
        Noticia existente = noticias.get(nombreUnico);
        if (existente == null) {
            return ServerResponse.notOk("No existe la noticia a modificar: " + nombreUnico);
        }
        // Solo el autor original o un admin pueden modificar.
        if (!esAdmin && !existente.getAutor().equals(usuarioActual)) {
            return ServerResponse.notOk("No tienes permisos para modificar esta noticia.");
        }

        existente.setTitular(noticiaActualizada.getTitular());
        existente.setContenido(noticiaActualizada.getContenido());
        existente.setFechaActualizacion(LocalDateTime.now());

        return ServerResponse.ok("Noticia modificada correctamente.", existente);
    }

    @Override
    public ServerResponse eliminarNoticia(String nombreUnico, String usuarioActual,
                                          boolean esAdmin) throws RemoteException {
        Noticia existente = noticias.get(nombreUnico);
        if (existente == null) {
            return ServerResponse.notOk("No existe la noticia a eliminar: " + nombreUnico);
        }
        if (!esAdmin && !existente.getAutor().equals(usuarioActual)) {
            return ServerResponse.notOk("No tienes permisos para eliminar esta noticia.");
        }

        noticias.remove(nombreUnico);
        return ServerResponse.ok("Noticia eliminada correctamente.");
    }
}
