package com.taller.mecanica.config;

import com.taller.mecanica.model.entity.ConfiguracionTaller;
import com.taller.mecanica.model.entity.Insumo;
import com.taller.mecanica.model.entity.PaqueteServicio;
import com.taller.mecanica.model.entity.PlantillaItemPaquete;
import com.taller.mecanica.model.enums.CategoriaInsumo;
import com.taller.mecanica.model.enums.CategoriaItem;
import com.taller.mecanica.model.enums.TipoCombustible;
import com.taller.mecanica.repository.ConfiguracionTallerRepository;
import com.taller.mecanica.repository.InsumoRepository;
import com.taller.mecanica.repository.PaqueteServicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final ConfiguracionTallerRepository configuracionRepository;
    private final InsumoRepository insumoRepository;
    private final PaqueteServicioRepository paqueteRepository;

    @Value("${app.capacidad-default:3}")
    private int capacidadDefault;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        inicializarConfiguracion();
        inicializarInsumos();
        inicializarPaqueteMantenimientoMenor();
    }

    private void inicializarConfiguracion() {
        if (configuracionRepository.count() > 0) {
            return;
        }
        ConfiguracionTaller configuracion = new ConfiguracionTaller();
        configuracion.setNombreTaller("Taller Mecanico Automotriz");
        configuracion.setRuc("20123456789");
        configuracion.setDireccion("Av. Principal 123 - Lima");
        configuracion.setTelefono("(01) 555-1234");
        configuracion.setTelefonoWhatsapp("51987654321");
        configuracion.setCorreo("contacto@taller.com");
        configuracion.setHorarioAtencion("Lunes a Sabado de 08:00 a 18:00");
        configuracion.setCapacidadMaximaSimultanea(capacidadDefault);
        configuracion.setTextoGarantia("Garantia valida por el kilometraje y el tiempo indicados, presentando este "
                + "informe y la factura de servicio. No cubre desgaste normal, danos por accidente, uso indebido "
                + "ni reparaciones realizadas por terceros.");
        configuracion.setMensajeRecordatorio("Hola {cliente}, le recordamos que su {vehiculo} ({placa}) ya cumple "
                + "el intervalo de mantenimiento. Le esperamos en {taller} para agendar su proximo servicio.");
        configuracionRepository.save(configuracion);
        log.info("Configuracion inicial del taller creada");
    }

    private void inicializarInsumos() {
        if (insumoRepository.count() > 0) {
            return;
        }
        List<Insumo> insumos = new ArrayList<>();
        insumos.add(insumo("ACE-5W30", "Aceite de motor sintetico 5W-30 (1 galon)",
                CategoriaInsumo.ACEITE, 24, 6, "185.00"));
        insumos.add(insumo("ACE-10W40", "Aceite de motor semisintetico 10W-40 (1 galon)",
                CategoriaInsumo.ACEITE, 18, 5, "145.00"));
        insumos.add(insumo("FIL-ACEITE", "Filtro de aceite de motor",
                CategoriaInsumo.FILTRO, 30, 10, "28.00"));
        insumos.add(insumo("FIL-AIRE", "Filtro de aire de motor",
                CategoriaInsumo.FILTRO, 22, 8, "35.00"));
        insumos.add(insumo("FIL-COMB", "Filtro de combustible",
                CategoriaInsumo.FILTRO, 14, 6, "42.00"));
        insumos.add(insumo("FIL-AC", "Filtro de aire acondicionado",
                CategoriaInsumo.FILTRO, 16, 6, "38.00"));
        insumos.add(insumo("ARR-CARTER", "Arandela de carter (tapón de drenaje)",
                CategoriaInsumo.ARANDELA, 40, 15, "5.00"));
        insumos.add(insumo("LIQ-FRENOS", "Liquido de frenos DOT 4 (1/2 litro)",
                CategoriaInsumo.FLUIDO, 20, 8, "32.00"));
        insumos.add(insumo("LIQ-REFRIG", "Refrigerante de motor concentrado (1 galon)",
                CategoriaInsumo.FLUIDO, 15, 6, "55.00"));
        insumos.add(insumo("LIQ-LIMPIA", "Liquido limpiaparabrisas (1 galon)",
                CategoriaInsumo.FLUIDO, 25, 10, "18.00"));
        insumos.add(insumo("BUJIA-IRC", "Bujia de iridio",
                CategoriaInsumo.REPUESTO, 32, 12, "26.00"));
        insumoRepository.saveAll(insumos);
        log.info("Insumos iniciales registrados: {}", insumos.size());
    }

    private Insumo insumo(String codigo, String nombre, CategoriaInsumo categoria,
                          int stockActual, int stockMinimo, String precio) {
        Insumo insumo = new Insumo();
        insumo.setCodigo(codigo);
        insumo.setNombre(nombre);
        insumo.setCategoria(categoria);
        insumo.setStockActual(stockActual);
        insumo.setStockMinimo(stockMinimo);
        insumo.setPrecioVenta(new BigDecimal(precio));
        return insumo;
    }

    private void inicializarPaqueteMantenimientoMenor() {
        String nombre = "MANTENIMIENTO MENOR GASOLINA";
        if (paqueteRepository.existsByNombreIgnoreCase(nombre)) {
            return;
        }
        PaqueteServicio paquete = new PaqueteServicio();
        paquete.setNombre(nombre);
        paquete.setTipoCombustible(TipoCombustible.GASOLINA);
        paquete.setPrecioBase(new BigDecimal("550.00"));
        paquete.setTiempoEstimadoHoras(5);
        paquete.setGarantiaKm(5000);
        paquete.setGarantiaMeses(6);
        paquete.setActivo(true);

        paquete.agregarItem(item(CategoriaItem.ESCANEO,
                "Escaneo computarizado OBD-II inicial y final con reporte de codigos DTC",
                false, false, 1));
        paquete.agregarItem(item(CategoriaItem.LUBRICACION,
                "Cambio de aceite de motor segun especificacion del fabricante",
                false, true, 2));
        paquete.agregarItem(item(CategoriaItem.LUBRICACION,
                "Cambio de filtro de aceite de motor",
                false, false, 3));
        paquete.agregarItem(item(CategoriaItem.LUBRICACION,
                "Cambio de filtro de aire de motor (segun estado)",
                true, false, 4));
        paquete.agregarItem(item(CategoriaItem.MOTOR,
                "Revision de niveles, fugas y estado general del motor",
                false, false, 5));
        paquete.agregarItem(item(CategoriaItem.PRUEBA_ELECTRONICA,
                "Prueba de bateria, alternador y sistema de carga",
                false, false, 6));
        paquete.agregarItem(item(CategoriaItem.PRUEBA_ELECTRONICA,
                "Prueba de luces, senalizacion y claxon",
                false, false, 7));
        paquete.agregarItem(item(CategoriaItem.PRUEBA_ELECTRONICA,
                "Prueba de sistema de arranque y encendido",
                false, false, 8));
        paquete.agregarItem(item(CategoriaItem.MOTOR,
                "Revision del sistema de refrigeracion y mangueras",
                false, false, 9));
        paquete.agregarItem(item(CategoriaItem.MOTOR,
                "Revision de correas, tensores y poleas",
                false, false, 10));
        paquete.agregarItem(item(CategoriaItem.MOTOR,
                "Revision de soportes de motor y transmision",
                false, false, 11));
        paquete.agregarItem(item(CategoriaItem.FLUIDOS,
                "Revision y relleno de liquido de frenos",
                false, false, 12));
        paquete.agregarItem(item(CategoriaItem.FLUIDOS,
                "Revision y relleno de liquido refrigerante",
                true, false, 13));
        paquete.agregarItem(item(CategoriaItem.FLUIDOS,
                "Revision y relleno de liquido limpiaparabrisas",
                true, false, 14));
        paquete.agregarItem(item(CategoriaItem.FLUIDOS,
                "Revision y relleno de aceite de transmision (segun especificacion)",
                true, false, 15));
        paquete.agregarItem(item(CategoriaItem.CORTESIA,
                "Lavado exterior e interior de cortesia",
                false, false, 16));
        paquete.agregarItem(item(CategoriaItem.FRENOS,
                "Revision de pastillas, discos y espesor de frenos",
                false, false, 17));
        paquete.agregarItem(item(CategoriaItem.FRENOS,
                "Revision de freno de mano y purga del sistema",
                false, false, 18));
        paquete.agregarItem(item(CategoriaItem.CORTESIA,
                "Pulverizado de ambiente y perfume de cortesia",
                false, false, 19));

        paqueteRepository.save(paquete);
        log.info("Paquete '{}' creado con {} items", nombre, paquete.getItems().size());
    }

    private PlantillaItemPaquete item(CategoriaItem categoria, String descripcion,
                                      boolean condicional, boolean requiereFicha, int orden) {
        PlantillaItemPaquete item = new PlantillaItemPaquete();
        item.setCategoria(categoria);
        item.setDescripcionTarea(descripcion);
        item.setEsCondicional(condicional);
        item.setRequiereFichaTecnica(requiereFicha);
        item.setOrdenVisualizacion(orden);
        return item;
    }
}
