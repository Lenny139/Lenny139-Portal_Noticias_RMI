# Noticias UPB — Java RMI

Aplicación distribuida de noticias usando Java RMI (Remote Method Invocation).

## Estructura

```
src/co/edu/upb/noticias/
├── model/        Objetos que viajan por la red (Serializable)
│   ├── Noticia.java
│   └── ServerResponse.java
├── remote/       Contrato remoto (interfaz)
│   └── NoticiaService.java
├── server/       Implementación y arranque del servidor
│   ├── NoticiaServiceImpl.java
│   └── ServidorNoticias.java
└── client/       Cliente interactivo (menú por consola)
    └── ClienteNoticias.java
```

## Compilar

```powershell
.\build.ps1
```

## Ejecutar

En una terminal, levanta el servidor:

```powershell
java -cp out co.edu.upb.noticias.server.ServidorNoticias
```

En otra terminal, ejecuta el cliente:

```powershell
java -cp out co.edu.upb.noticias.client.ClienteNoticias
```

## Requisitos

- JDK 11 o superior (se usa `LocalDateTime` y `String.isBlank()`).
