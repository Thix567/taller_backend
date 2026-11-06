package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> EXTENSIONES_IMAGEN = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> EXTENSIONES_PDF = Set.of("pdf");

    private final Path raiz;

    public FileStorageService(@Value("${app.uploads-dir:uploads}") String uploadsDir) {
        this.raiz = Paths.get(uploadsDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(raiz.resolve("evidencias"));
            Files.createDirectories(raiz.resolve("fichas-tecnicas"));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo crear la carpeta de uploads: " + raiz, ex);
        }
    }

    public String guardarEvidencia(Long informeId, MultipartFile archivo) {
        return guardar("evidencias/" + informeId, archivo, EXTENSIONES_IMAGEN, "imagen");
    }

    public String guardarFichaTecnica(String ambito, Long propietarioId, MultipartFile archivo) {
        return guardar("fichas-tecnicas/" + ambito + "/" + propietarioId, archivo, EXTENSIONES_PDF, "pdf");
    }

    private String guardar(String subcarpeta, MultipartFile archivo, Set<String> extensionesPermitidas, String tipo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("El archivo esta vacio");
        }
        String original = StringUtils.cleanPath(Objects.requireNonNullElse(archivo.getOriginalFilename(), "archivo"));
        if (original.contains("..")) {
            throw new BusinessException("Nombre de archivo invalido");
        }
        int punto = original.lastIndexOf('.');
        if (punto < 0) {
            throw new BusinessException("El archivo debe tener una extension valida");
        }
        String extension = original.substring(punto + 1).toLowerCase(Locale.ROOT);
        if (!extensionesPermitidas.contains(extension)) {
            throw new BusinessException("Extension no permitida. Se aceptan: " + String.join(", ", extensionesPermitidas));
        }
        String contentType = Objects.requireNonNullElse(archivo.getContentType(), "");
        boolean tipoValido = "imagen".equals(tipo)
                ? contentType.toLowerCase(Locale.ROOT).startsWith("image/")
                : contentType.toLowerCase(Locale.ROOT).startsWith("application/pdf") || contentType.isBlank();
        if (!tipoValido) {
            throw new BusinessException("El tipo de contenido del archivo no corresponde a un " + tipo);
        }

        Path directorio = raiz.resolve(subcarpeta).normalize();
        if (!directorio.startsWith(raiz)) {
            throw new BusinessException("Ruta de almacenamiento invalida");
        }
        String nombre = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        try {
            Files.createDirectories(directorio);
            Path destino = directorio.resolve(nombre);
            try (InputStream entrada = archivo.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new BusinessException("No se pudo guardar el archivo: " + ex.getMessage());
        }
        return "/uploads/" + subcarpeta + "/" + nombre;
    }

    public void eliminar(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }
        Path archivo = raiz.resolve(url.substring("/uploads/".length())).normalize();
        if (!archivo.startsWith(raiz)) {
            return;
        }
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException ignored) {
            // El archivo fisico ya no existe o esta bloqueado: no se interrumpe la operacion.
        }
    }
}
