package com.taller.mecanica.service;

import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.ConfiguracionTaller;
import com.taller.mecanica.repository.ConfiguracionTallerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionTallerRepository configuracionRepository;

    @Value("${app.capacidad-default:3}")
    private int capacidadDefault;

    @Transactional
    public ConfiguracionTaller obtener() {
        return configuracionRepository.findFirstByOrderByIdAsc().orElseGet(this::crearPorDefecto);
    }

    @Transactional
    public ConfiguracionTaller actualizar(ConfiguracionTaller datos) {
        ConfiguracionTaller configuracion = obtener();
        configuracion.setNombreTaller(datos.getNombreTaller());
        configuracion.setRuc(datos.getRuc());
        configuracion.setDireccion(datos.getDireccion());
        configuracion.setTelefono(datos.getTelefono());
        configuracion.setTelefonoWhatsapp(datos.getTelefonoWhatsapp());
        configuracion.setCorreo(datos.getCorreo());
        configuracion.setLogoUrl(datos.getLogoUrl());
        configuracion.setHorarioAtencion(datos.getHorarioAtencion());
        configuracion.setTextoGarantia(datos.getTextoGarantia());
        configuracion.setMensajeRecordatorio(datos.getMensajeRecordatorio());
        if (datos.getCapacidadMaximaSimultanea() != null) {
            configuracion.setCapacidadMaximaSimultanea(datos.getCapacidadMaximaSimultanea());
        }
        return configuracionRepository.save(configuracion);
    }

    @Transactional
    public ConfiguracionTaller obtenerPorId(Long id) {
        return configuracionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Configuracion del taller", id));
    }

    private ConfiguracionTaller crearPorDefecto() {
        ConfiguracionTaller configuracion = new ConfiguracionTaller();
        configuracion.setNombreTaller("Taller Mecanico Automotriz");
        configuracion.setRuc("20123456789");
        configuracion.setDireccion("Av. Principal 123 - Lima");
        configuracion.setTelefono("(01) 555-1234");
        configuracion.setTelefonoWhatsapp("51987654321");
        configuracion.setCorreo("contacto@taller.com");
        configuracion.setHorarioAtencion("Lunes a Sabado de 08:00 a 18:00");
        configuracion.setCapacidadMaximaSimultanea(capacidadDefault);
        configuracion.setTextoGarantia("Garantia valida por el kilometraje y tiempo indicados, "
                + "presentando este informe y la factura de servicio. No cubre desgaste normal, "
                + "danos por accidente, uso indebido ni reparaciones realizadas por terceros.");
        configuracion.setMensajeRecordatorio("Hola {cliente}, le recordamos que su {vehiculo} ({placa}) "
                + "ya cumple el intervalo de mantenimiento. Le esperamos en {taller} para agendar su proximo servicio.");
        return configuracionRepository.save(configuracion);
    }
}
