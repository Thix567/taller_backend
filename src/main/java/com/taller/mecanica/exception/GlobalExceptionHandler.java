package com.taller.mecanica.exception;

import com.taller.mecanica.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> reglaNegocio(BusinessException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> noAutenticado(AuthenticationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED,
                        "Usuario o contrasena incorrectos", request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Datos invalidos en la solicitud", request.getRequestURI(), campos));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> restriccion(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> campos.put(v.getPropertyPath().toString(), v.getMessage()));
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Parametros invalidos", request.getRequestURI(), campos));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Conflicto de integridad: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT,
                        "El registro entra en conflicto con datos existentes (valor duplicado o referencia en uso)",
                        request.getRequestURI()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> archivoMuyGrande(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ErrorResponse.of(HttpStatus.PAYLOAD_TOO_LARGE,
                        "El archivo excede el tamano maximo permitido (15 MB por archivo)",
                        request.getRequestURI()));
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> parteFaltante(Exception ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Falta un parametro o archivo requerido: " + ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST,
                        "JSON invalido o valor no permitido en un campo (revise enums, fechas y numeros)",
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorGeneral(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocurrio un error interno. Revise los logs del backend.", request.getRequestURI()));
    }
}
