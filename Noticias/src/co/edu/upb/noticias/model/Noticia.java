package co.edu.upb.noticias.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Modelo de dominio que representa una noticia de la UPB.
 *
 * Implementa {@link Serializable} porque las instancias de esta clase viajan
 * por la red entre el servidor RMI y el cliente. Todo objeto que se pase como
 * parametro o se devuelva en un metodo remoto debe ser serializable.
 */
public class Noticia implements Serializable {

    /**
     * Identificador de version de la clase serializable. Mantenerlo fijo evita
     * errores de "InvalidClassException" si el cliente y el servidor compilan
     * la clase por separado.
     */
    private static final long serialVersionUID = 1L;

    /** Identificador unico de la noticia (clave logica, p. ej. un slug). */
    private String nombreUnico;

    /** Titular o titulo visible de la noticia. */
    private String titular;

    /** Momento en que se creo la noticia. */
    private LocalDateTime fechaCreacion;

    /** Momento de la ultima modificacion. */
    private LocalDateTime fechaActualizacion;

    /** Usuario que creo la noticia. */
    private String autor;

    /** Cuerpo o contenido completo de la noticia. */
    private String contenido;

    /** Constructor vacio: util para frameworks y para construir por pasos. */
    public Noticia() {
    }

    /** Constructor de conveniencia con todos los campos. */
    public Noticia(String nombreUnico, String titular, LocalDateTime fechaCreacion,
                   LocalDateTime fechaActualizacion, String autor, String contenido) {
        this.nombreUnico = nombreUnico;
        this.titular = titular;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.autor = autor;
        this.contenido = contenido;
    }

    public String getNombreUnico() {
        return nombreUnico;
    }

    public void setNombreUnico(String nombreUnico) {
        this.nombreUnico = nombreUnico;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "Noticia{" +
                "nombreUnico='" + nombreUnico + '\'' +
                ", titular='" + titular + '\'' +
                ", autor='" + autor + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
