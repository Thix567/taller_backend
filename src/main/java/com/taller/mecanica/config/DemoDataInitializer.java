package com.taller.mecanica.config;

import com.taller.mecanica.model.entity.Cita;
import com.taller.mecanica.model.entity.Cliente;
import com.taller.mecanica.model.entity.DetalleInspeccionInforme;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.entity.Mecanico;
import com.taller.mecanica.model.entity.PaqueteServicio;
import com.taller.mecanica.model.entity.PlantillaItemPaquete;
import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.model.enums.AccionRealizada;
import com.taller.mecanica.model.enums.EstadoCita;
import com.taller.mecanica.model.enums.EstadoComponente;
import com.taller.mecanica.model.enums.EstadoInforme;
import com.taller.mecanica.model.enums.NivelCombustible;
import com.taller.mecanica.model.enums.TipoCombustible;
import com.taller.mecanica.repository.CitaRepository;
import com.taller.mecanica.repository.ClienteRepository;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.MecanicoRepository;
import com.taller.mecanica.repository.PaqueteServicioRepository;
import com.taller.mecanica.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos de ejemplo para la presentacion (perfil "demo").
 * No se ejecuta en desarrollo con MySQL ni en produccion.
 */
@Component
@Profile("demo")
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class DemoDataInitializer implements ApplicationRunner {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final MecanicoRepository mecanicoRepository;
    private final CitaRepository citaRepository;
    private final InformeVehiculoRepository informeRepository;
    private final PaqueteServicioRepository paqueteRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (clienteRepository.count() > 0) {
            return;
        }

        Cliente juan = cliente("12345678", "Juan Perez Rodriguez", "987654321",
                "juan.perez@correo.com", "Av. Los Olivos 456 - Lima");
        Cliente maria = cliente("87654321", "Maria Garcia Torres", "912345678",
                "maria.garcia@correo.com", "Jr. Las Flores 789 - Lima");
        Cliente carlos = cliente("45678912", "Carlos Quispe Mamani", "998877665",
                "carlos.quispe@correo.com", "Calle San Martin 123 - Lima");

        Vehiculo corolla = vehiculo(juan, "ABC-123", "Toyota", "Corolla", 2020,
                TipoCombustible.GASOLINA, 45000, "Blanco");
        Vehiculo accent = vehiculo(maria, "XYZ-789", "Hyundai", "Accent", 2019,
                TipoCombustible.GASOLINA, 62000, "Gris");
        Vehiculo rio = vehiculo(carlos, "PLK-456", "Kia", "Rio", 2021,
                TipoCombustible.GASOLINA, 28000, "Rojo");

        Mecanico luis = mecanico("Luis Torres Vega", "44556677", "Mecanica general", "955112233");
        Mecanico jorge = mecanico("Jorge Ramirez Soto", "77889900", "Electronica automotriz", "944556677");

        PaqueteServicio paquete = paqueteRepository.findByNombreIgnoreCase("MANTENIMIENTO MENOR GASOLINA")
                .orElseThrow(() -> new IllegalStateException("Paquete demo no encontrado"));

        LocalDateTime ahora = LocalDateTime.now();
        cita(juan, corolla, paquete, ahora.plusDays(1).withHour(9).withMinute(0),
                EstadoCita.CONFIRMADA, "Cliente solicita revision general antes de viaje.");
        cita(maria, accent, paquete, ahora.plusDays(2).withHour(11).withMinute(0),
                EstadoCita.PENDIENTE, "Prefiere atencion por la manana.");
        cita(carlos, rio, paquete, ahora.plusHours(2),
                EstadoCita.EN_TALLER, "Vehiculo ingresado para mantenimiento preventivo.");

        informe(corolla, paquete, luis, "OT-2026-0001", 45000, 50000, ahora.minusHours(3));

        log.info("Datos de demostracion cargados: 3 clientes, 3 vehiculos, 2 mecanicos, 3 citas y 1 orden");
    }

    private Cliente cliente(String dni, String nombre, String telefono, String correo, String direccion) {
        Cliente c = new Cliente();
        c.setDniRuc(dni);
        c.setNombreCompleto(nombre);
        c.setTelefonoWhatsapp(telefono);
        c.setCorreo(correo);
        c.setDireccion(direccion);
        return clienteRepository.save(c);
    }

    private Vehiculo vehiculo(Cliente cliente, String placa, String marca, String modelo, int anio,
                              TipoCombustible combustible, int km, String color) {
        Vehiculo v = new Vehiculo();
        v.setCliente(cliente);
        v.setPlaca(placa);
        v.setMarca(marca);
        v.setModelo(modelo);
        v.setAnio(anio);
        v.setTipoCombustible(combustible);
        v.setKilometrajeActual(km);
        v.setColor(color);
        return vehiculoRepository.save(v);
    }

    private Mecanico mecanico(String nombre, String dni, String especialidad, String telefono) {
        Mecanico m = new Mecanico();
        m.setNombreCompleto(nombre);
        m.setDni(dni);
        m.setEspecialidad(especialidad);
        m.setTelefono(telefono);
        m.setActivo(true);
        return mecanicoRepository.save(m);
    }

    private void cita(Cliente cliente, Vehiculo vehiculo, PaqueteServicio paquete,
                      LocalDateTime inicio, EstadoCita estado, String observaciones) {
        Cita c = new Cita();
        c.setCliente(cliente);
        c.setVehiculo(vehiculo);
        c.setPaqueteDeseado(paquete);
        c.setFechaHoraInicio(inicio);
        c.setFechaHoraFinCalculada(inicio.plusHours(paquete.getTiempoEstimadoHoras()));
        c.setEstadoCita(estado);
        c.setObservaciones(observaciones);
        citaRepository.save(c);
    }

    private void informe(Vehiculo vehiculo, PaqueteServicio paquete, Mecanico mecanico,
                         String numeroOrden, int kmIngreso, int kmProximo, LocalDateTime ingreso) {
        InformeVehiculo informe = new InformeVehiculo();
        informe.setNumeroOrden(numeroOrden);
        informe.setVehiculo(vehiculo);
        informe.setPaquete(paquete);
        informe.setMecanico(mecanico);
        informe.setFechaIngreso(ingreso);
        informe.setFechaEstimadaEntrega(ingreso.plusHours(paquete.getTiempoEstimadoHoras()));
        informe.setKilometrajeIngreso(kmIngreso);
        informe.setKilometrajeProximoServicio(kmProximo);
        informe.setFechaProximoServicio(ingreso.plusMonths(6));
        informe.setNivelCombustible(NivelCombustible.MEDIO);
        informe.setInventarioRecepcion("Documentos, llave de repuesto y triangulo de seguridad.");
        informe.setObservacionesRecepcion("Vehiculo sin golpes visibles. Se realiza mantenimiento preventivo.");
        informe.setEstado(EstadoInforme.EN_PROCESO);
        informe.setPrecioPaquete(paquete.getPrecioBase());
        informe.setHorasEstimadas(paquete.getTiempoEstimadoHoras());
        informe.setGarantiaKm(paquete.getGarantiaKm());
        informe.setGarantiaMeses(paquete.getGarantiaMeses());

        int orden = 0;
        List<PlantillaItemPaquete> items = paquete.getItems();
        for (PlantillaItemPaquete item : items) {
            DetalleInspeccionInforme detalle = new DetalleInspeccionInforme();
            detalle.setInforme(informe);
            detalle.setPlantillaItemId(item.getId());
            detalle.setCategoria(item.getCategoria());
            detalle.setDescripcionTarea(item.getDescripcionTarea());
            detalle.setEsCondicional(item.getEsCondicional());
            detalle.setRequiereFichaTecnica(item.getRequiereFichaTecnica());
            detalle.setOrdenVisualizacion(item.getOrdenVisualizacion());
            detalle.setEstadoComponente(EstadoComponente.OPTIMO);
            detalle.setAccionRealizada(orden < 4 ? AccionRealizada.COMPLETADO : AccionRealizada.PENDIENTE);
            detalle.setObservacionTecnica(orden < 4 ? "Sin novedad." : null);
            informe.getDetalles().add(detalle);
            orden++;
        }

        informeRepository.save(informe);
    }
}
