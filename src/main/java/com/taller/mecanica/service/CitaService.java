package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.Cita;
import com.taller.mecanica.model.entity.Cliente;
import com.taller.mecanica.model.entity.PaqueteServicio;
import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.model.enums.EstadoCita;
import com.taller.mecanica.dto.request.CitaRequest;
import com.taller.mecanica.dto.request.ConvertirCitaRequest;
import com.taller.mecanica.dto.request.InformeCreateRequest;
import com.taller.mecanica.dto.response.CitaResponse;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.repository.CitaRepository;
import com.taller.mecanica.repository.ClienteRepository;
import com.taller.mecanica.repository.PaqueteServicioRepository;
import com.taller.mecanica.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitaService {

    private static final Map<EstadoCita, Set<EstadoCita>> TRANSICIONES = Map.of(
            EstadoCita.PENDIENTE, Set.of(EstadoCita.CONFIRMADA, EstadoCita.EN_TALLER, EstadoCita.CANCELADA),
            EstadoCita.CONFIRMADA, Set.of(EstadoCita.PENDIENTE, EstadoCita.EN_TALLER, EstadoCita.CANCELADA),
            EstadoCita.EN_TALLER, Set.of(EstadoCita.CANCELADA),
            EstadoCita.CANCELADA, Set.of(EstadoCita.PENDIENTE)
    );

    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PaqueteServicioRepository paqueteRepository;
    private final ConfiguracionService configuracionService;
    private final InformeService informeService;

    public List<CitaResponse> listar(LocalDateTime desde, LocalDateTime hasta, EstadoCita estado) {
        List<Cita> citas;
        if (desde != null && hasta != null) {
            citas = citaRepository.findByFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(desde, hasta);
        } else {
            citas = citaRepository.findAllByOrderByFechaHoraInicioDesc();
        }
        return citas.stream()
                .filter(c -> estado == null || c.getEstadoCita() == estado)
                .map(CitaResponse::from)
                .toList();
    }

    public CitaResponse obtener(Long id) {
        return CitaResponse.from(cargar(id));
    }

    @Transactional
    public CitaResponse crear(CitaRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> ResourceNotFoundException.de("Cliente", request.clienteId()));
        Vehiculo vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .orElseThrow(() -> ResourceNotFoundException.de("Vehiculo", request.vehiculoId()));
        if (!vehiculo.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("El vehiculo no pertenece al cliente seleccionado");
        }
        PaqueteServicio paquete = paqueteRepository.findById(request.paqueteId())
                .orElseThrow(() -> ResourceNotFoundException.de("Paquete de servicio", request.paqueteId()));
        if (!Boolean.TRUE.equals(paquete.getActivo())) {
            throw new BusinessException("El paquete de servicio esta inactivo");
        }
        LocalDateTime inicio = request.fechaHoraInicio();
        LocalDateTime fin = inicio.plusHours(paquete.getTiempoEstimadoHoras());
        validarCapacidad(inicio, fin, null);

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setVehiculo(vehiculo);
        cita.setPaqueteDeseado(paquete);
        cita.setFechaHoraInicio(inicio);
        cita.setFechaHoraFinCalculada(fin);
        cita.setEstadoCita(EstadoCita.PENDIENTE);
        cita.setObservaciones(request.observaciones());
        cita.setFechaCreacion(LocalDateTime.now());
        return CitaResponse.from(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponse actualizar(Long id, CitaRequest request) {
        Cita cita = cargar(id);
        if (cita.getEstadoCita() == EstadoCita.EN_TALLER || cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new BusinessException("Solo se pueden reprogramar citas pendientes o confirmadas");
        }
        if (cita.getInformeId() != null) {
            throw new BusinessException("La cita ya tiene una orden asociada");
        }
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> ResourceNotFoundException.de("Cliente", request.clienteId()));
        Vehiculo vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .orElseThrow(() -> ResourceNotFoundException.de("Vehiculo", request.vehiculoId()));
        if (!vehiculo.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("El vehiculo no pertenece al cliente seleccionado");
        }
        PaqueteServicio paquete = paqueteRepository.findById(request.paqueteId())
                .orElseThrow(() -> ResourceNotFoundException.de("Paquete de servicio", request.paqueteId()));
        LocalDateTime inicio = request.fechaHoraInicio();
        LocalDateTime fin = inicio.plusHours(paquete.getTiempoEstimadoHoras());
        validarCapacidad(inicio, fin, id);

        cita.setCliente(cliente);
        cita.setVehiculo(vehiculo);
        cita.setPaqueteDeseado(paquete);
        cita.setFechaHoraInicio(inicio);
        cita.setFechaHoraFinCalculada(fin);
        cita.setObservaciones(request.observaciones());
        return CitaResponse.from(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponse cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = cargar(id);
        if (cita.getEstadoCita() == nuevoEstado) {
            return CitaResponse.from(cita);
        }
        Set<EstadoCita> permitidos = TRANSICIONES.getOrDefault(cita.getEstadoCita(), Set.of());
        if (!permitidos.contains(nuevoEstado)) {
            throw new BusinessException("Transicion no permitida: " + cita.getEstadoCita() + " -> " + nuevoEstado);
        }
        cita.setEstadoCita(nuevoEstado);
        return CitaResponse.from(citaRepository.save(cita));
    }

    @Transactional
    public InformeDetalleResponse convertir(Long id, ConvertirCitaRequest request) {
        Cita cita = cargar(id);
        if (cita.getInformeId() != null) {
            throw new BusinessException("La cita ya fue convertida en una orden de trabajo");
        }
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new BusinessException("No se puede convertir una cita cancelada");
        }
        InformeCreateRequest create = new InformeCreateRequest(
                cita.getVehiculo().getId(),
                cita.getPaqueteDeseado().getId(),
                request.mecanicoId(),
                cita.getId(),
                null,
                request.kilometrajeIngreso(),
                request.nivelCombustible(),
                request.inventarioRecepcion(),
                request.observacionesRecepcion(),
                null,
                null
        );
        return informeService.crear(create);
    }

    @Transactional
    public void eliminar(Long id) {
        Cita cita = cargar(id);
        if (cita.getInformeId() != null) {
            throw new BusinessException("No se puede eliminar una cita que ya tiene orden de trabajo");
        }
        citaRepository.delete(cita);
    }

    private Cita cargar(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Cita", id));
    }

    private void validarCapacidad(LocalDateTime inicio, LocalDateTime fin, Long excluirId) {
        int capacidad = configuracionService.obtener().getCapacidadMaximaSimultanea() == null
                ? 3
                : configuracionService.obtener().getCapacidadMaximaSimultanea();
        List<Cita> solapadas = excluirId == null
                ? citaRepository.buscarSolapadas(inicio, fin)
                : citaRepository.buscarSolapadasExcluyendo(inicio, fin, excluirId);
        if (solapadas.size() >= capacidad) {
            throw new BusinessException("Capacidad maxima del taller alcanzada (" + capacidad
                    + " vehiculos simultaneos) para el horario solicitado");
        }
    }
}
