package co.edu.upb.noticias.model;

import java.io.Serializable;

/**
 * Respuesta estandar que el servidor devuelve al cliente.
 *
 * Sigue el mismo patron que usaste en el proyecto del cajero: en lugar de
 * devolver tipos sueltos o lanzar excepciones por cada caso de negocio, se
 * envuelve el resultado en un objeto con un {@link Status} y un mensaje
 * legible. Asi el cliente siempre sabe como interpretar la respuesta.
 *
 * Opcionalmente transporta un dato ({@link #data}) para devolver, por ejemplo,
 * una {@link Noticia} encontrada sin necesitar otra clase de respuesta.
 */
public class ServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Estados posibles de una operacion en el servidor. */
    public enum Status {
        /** La operacion se completo correctamente. */
        OK,
        /** La operacion no se realizo por reglas de negocio (p. ej. sin permisos, no existe). */
        NOT_OK,
        /** Hubo un fallo tecnico inesperado en el servidor. */
        FAIL
    }

    private Status status;
    private String mensaje;

    /** Carga util opcional (la noticia, una lista, etc.). Puede ser null. */
    private Object data;

    public ServerResponse() {
    }

    public ServerResponse(Status status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
    }

    public ServerResponse(Status status, String mensaje, Object data) {
        this.status = status;
        this.mensaje = mensaje;
        this.data = data;
    }

    /* ---- Metodos fabrica para escribir respuestas mas legibles ---- */

    public static ServerResponse ok(String mensaje) {
        return new ServerResponse(Status.OK, mensaje);
    }

    public static ServerResponse ok(String mensaje, Object data) {
        return new ServerResponse(Status.OK, mensaje, data);
    }

    public static ServerResponse notOk(String mensaje) {
        return new ServerResponse(Status.NOT_OK, mensaje);
    }

    public static ServerResponse fail(String mensaje) {
        return new ServerResponse(Status.FAIL, mensaje);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "[" + status + "] " + mensaje;
    }
}
