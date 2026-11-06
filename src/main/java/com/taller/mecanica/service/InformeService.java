package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.Cita;
import com.taller.mecanica.model.entity.ConsumoInsumo;
import com.taller.mecanica.model.entity.DetalleInspeccionInforme;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.entity.Mecanico;
import com.taller.mecanica.model.entity.PaqueteServicio;
import com.taller.mecanica.model.entity.PlantillaItemPaquete;
import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.model.enums.AccionRealizada;
import com.taller.mecanica.model.enums.EstadoCita;
import com.taller.mecanica.model.enums.EstadoInforme;
import com.taller.mecanica.dto.request.ChecklistUpdateRequest;
import com.taller.mecanica.dto.request.EscaneoRequest;
import com.taller.mecanica.dto.request.InformeCreateRequest;
import com.taller.mecanica.dto.request.InformeUpdateRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.dto.response.InformeResumenResponse;
import com.taller.mecanica.dto.mapper.InformeMapper;
import com.taller.mecanica.repository.CitaRepository;
import com.taller.mecanica.repository.DetalleInspeccionInformeRepository;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.InsumoRepository;
import com.taller.mecanica.repository.MecanicoRepository;
import com.taller.mecanica.repository.PaqueteServicioRepository;
import com.taller.mecanica.repository.VehiculoRepository;
import com.taller.mecanica.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InformeService {

    private static final Map<EstadoInforme, Set<EstadoInforme>> TRANSICIONES = Map.of(
            EstadoInforme.RECEPCIONADO, Set.of(EstadoInforme.EN_PROCESO, EstadoInforme.LISTO_PARA_ENTREGA, EstadoInforme.CANCELADO),
            EstadoInforme.EN_PROCESO, Set.of(EstadoInforme.LISTO_PARA_ENTREGA, EstadoInforme.CANCELADO),
            EstadoInforme.LISTO_PARA_ENTREGA, Set.of(EstadoInforme.ENTREGADO, EstadoInforme.EN_PROCESO),
            EstadoInforme.ENTREGADO, Set.of(),
            EstadoInforme.CANCELADO, Set.of()
    );

    private final InformeVehiculoRepository informeRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PaqueteServicioRepository paqueteRepository;
    private final MecanicoRepository mecanicoRepository;
    private final CitaRepository citaRepository;
    private final DetalleInspeccionInformeRepository detalleRepository;
    private final InsumoRepository insumoRepository;
    private final ConfiguracionService configuracionService;
    private final FileStorageService fileStorageService;

    public List<InformeResumenResponse> listar(EstadoInforme estado, Long mecanicoId, String placa,
                                               LocalDateTime desde, LocalDateTime hasta) {
        Specification<InformeVehiculo> spec = (root, query, cb) -> cb.conjunction();
        if (estado != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }
        if (mecanicoId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("mecanico").get("id"), mecanicoId));
        }
        if (placa != null && !placa.isBlank()) {
            String patron = "%" + placa.trim().toUpperCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.upper(root.get("vehiculo").<String>get("placa")), patron));
        }
        if (desde != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.<LocalDateTime>get("fechaIngreso"), desde));
        }
        if (hasta != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.<LocalDateTime>get("fechaIngreso"), hasta));
        }
        return informeRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "fechaIngreso"))
                .stream()
                .map(InformeMapper::toResumen)
                .toList();
    }

    public InformeDetalleResponse obtenerDetalle(Long id) {
        return InformeMapper.toDetalle(cargar(id), configuracionService.obtener());
    }

    public List<InformeDetalleResponse.Item> listarChecklist(Long informeId) {
        cargar(informeId);
        return detalleRepository.findByInformeIdOrderByOrdenVisualizacionAsc(informeId)
                .stream()
                .map(InformeMapper::toItem)
                .toList();
    }

    @Transactional
    public InformeDetalleResponse crear(InformeCreateRequest request) {
        Vehiculo vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .orElseThrow(() -> ResourceNotFoundException.de("Vehiculo", request.vehiculoId()));
        PaqueteServicio paquete = paqueteRepository.findById(request.paqueteId())
                .orElseThrow(() -> ResourceNotFoundException.de("Paquete de servicio", request.paqueteId()));
        if (!Boolean.TRUE.equals(paquete.getActivo())) {
            throw new BusinessException("El paquete de servicio esta inactivo");
        }
        Mecanico mecanico = mecanicoRepository.findById(request.mecanicoId())
                .orElseThrow(() -> ResourceNotFoundException.de("Mecanico", request.mecanicoId()));
        if (!Boolean.TRUE.equals(mecanico.getActivo())) {
            throw new BusinessException("El mecanico seleccionado esta inactivo");
        }
        if (paquete.getItems() == null || paquete.getItems().isEmpty()) {
            throw new BusinessException("El paquete no tiene items de plantilla configurados");
        }

        Cita cita = null;
        if (request.citaId() != null) {
            cita = citaRepository.findById(request.citaId())
                    .orElseThrow(() -> ResourceNotFoundException.de("Cita", request.citaId()));
            if (cita.getInformeId() != null) {
                throw new BusinessException("La cita ya fue convertida en una orden de trabajo");
            }
            if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
                throw new BusinessException("No se puede convertir una cita cancelada");
            }
            if (!cita.getVehiculo().getId().equals(vehiculo.getId())) {
                throw new BusinessException("El vehiculo de la cita no coincide con el vehiculo indicado");
            }
            if (!cita.getPaqueteDeseado().getId().equals(paquete.getId())) {
                throw new BusinessException("El paquete de la cita no coincide con el paquete indicado");
            }
        }

        LocalDateTime ingreso = request.fechaIngreso() != null ? request.fechaIngreso() : LocalDateTime.now();
        InformeVehiculo informe = new InformeVehiculo();
        informe.setNumeroOrden(siguienteNumero(ingreso.getYear()));
        informe.setVehiculo(vehiculo);
        informe.setPaquete(paquete);
        informe.setMecanico(mecanico);
        informe.setCitaId(cita == null ? null : cita.getId());
        informe.setFechaIngreso(ingreso);
        informe.setEstado(EstadoInforme.RECEPCIONADO);
        informe.setKilometrajeIngreso(request.kilometrajeIngreso());
        informe.setNivelCombustible(request.nivelCombustible());
        informe.setInventarioRecepcion(request.inventarioRecepcion());
        informe.setObservacionesRecepcion(request.observacionesRecepcion());
        informe.setEscaneoInicialResultado(request.escaneoInicialResultado());
        informe.setObservacionesGenerales(request.observacionesGenerales());
        informe.setPrecioPaquete(paquete.getPrecioBase());
        informe.setHorasEstimadas(paquete.getTiempoEstimadoHoras());
        informe.setGarantiaKm(paquete.getGarantiaKm());
        informe.setGarantiaMeses(paquete.getGarantiaMeses());
        informe.setFechaCreacion(LocalDateTime.now());
        aplicarCalculosServicio(informe, ingreso);
        clonarPlantilla(paquete, informe);

        InformeVehiculo guardado = informeRepository.save(informe);
        actualizarKilometrajeVehiculo(vehiculo, request.kilometrajeIngreso());

        if (cita != null) {
            cita.setInformeId(guardado.getId());
            cita.setEstadoCita(EstadoCita.EN_TALLER);
            citaRepository.save(cita);
        }
        return InformeMapper.toDetalle(guardado, configuracionService.obtener());
    }

    @Transactional
    public InformeDetalleResponse actualizar(Long id, InformeUpdateRequest request) {
        InformeVehiculo informe = cargar(id);
        validarEditable(informe);

        if (request.mecanicoId() != null) {
            Mecanico mecanico = mecanicoRepository.findById(request.mecanicoId())
                    .orElseThrow(() -> ResourceNotFoundException.de("Mecanico", request.mecanicoId()));
            if (!Boolean.TRUE.equals(mecanico.getActivo())) {
                throw new BusinessException("El mecanico seleccionado esta inactivo");
            }
            informe.setMecanico(mecanico);
        }
        if (request.kilometrajeIngreso() != null) {
            informe.setKilometrajeIngreso(request.kilometrajeIngreso());
            informe.setKilometrajeProximoServicio(
                    InformeCalculo.kilometrajeProximo(request.kilometrajeIngreso(), informe.getGarantiaKm()));
            actualizarKilometrajeVehiculo(informe.getVehiculo(), request.kilometrajeIngreso());
        }
        if (request.nivelCombustible() != null) {
            informe.setNivelCombustible(request.nivelCombustible());
        }
        if (request.inventarioRecepcion() != null) {
            informe.setInventarioRecepcion(request.inventarioRecepcion());
        }
        if (request.observacionesRecepcion() != null) {
            informe.setObservacionesRecepcion(request.observacionesRecepcion());
        }
        if (request.recomendacionesFuturas() != null) {
            informe.setRecomendacionesFuturas(request.recomendacionesFuturas());
        }
        if (request.observacionesGenerales() != null) {
            informe.setObservacionesGenerales(request.observacionesGenerales());
        }
        return InformeMapper.toDetalle(informeRepository.save(informe), configuracionService.obtener());
    }

    @Transactional
    public InformeDetalleResponse actualizarEscaneos(Long id, EscaneoRequest request) {
        InformeVehiculo informe = cargar(id);
        validarEditable(informe);
        if (request.escaneoInicialResultado() != null) {
            informe.setEscaneoInicialResultado(request.escaneoInicialResultado());
        }
        if (request.escaneoFinalResultado() != null) {
            informe.setEscaneoFinalResultado(request.escaneoFinalResultado());
        }
        return InformeMapper.toDetalle(informeRepository.save(informe), configuracionService.obtener());
    }

    @Transactional
    public InformeDetalleResponse cambiarEstado(Long id, EstadoInforme nuevoEstado) {
        InformeVehiculo informe = cargar(id);
        EstadoInforme actual = informe.getEstado();
        if (actual == nuevoEstado) {
            return InformeMapper.toDetalle(informe, configuracionService.obtener());
        }
        Set<EstadoInforme> permitidos = TRANSICIONES.getOrDefault(actual, Set.of());
        if (!permitidos.contains(nuevoEstado)) {
            throw new BusinessException("Transicion no permitida: " + actual + " -> " + nuevoEstado);
        }
        if (nuevoEstado == EstadoInforme.LISTO_PARA_ENTREGA || nuevoEstado == EstadoInforme.ENTREGADO) {
            InformeCalculo.validarCierre(informe.getEscaneoFinalResultado(), informe.getDetalles());
        }
        if (nuevoEstado == EstadoInforme.ENTREGADO) {
            informe.setFechaEntregaReal(LocalDateTime.now());
        }
        if (nuevoEstado == EstadoInforme.CANCELADO) {
            reponerStock(informe);
        }
        informe.setEstado(nuevoEstado);
        return InformeMapper.toDetalle(informeRepository.save(informe), configuracionService.obtener());
    }

    @Transactional
    public InformeDetalleResponse.Item actualizarChecklist(Long informeId, Long detalleId,
                                                           ChecklistUpdateRequest request) {
        InformeVehiculo informe = cargar(informeId);
        validarEditable(informe);
        DetalleInspeccionInforme detalle = detalleRepository.findByIdAndInformeId(detalleId, informeId)
                .orElseThrow(() -> ResourceNotFoundException.de("Item del checklist", detalleId));

        if (Boolean.TRUE.equals(detalle.getEsCondicional())
                && request.accionRealizada() == AccionRealizada.PENDIENTE) {
            throw new BusinessException("El item condicional '" + detalle.getDescripcionTarea()
                    + "' exige indicar SOLO_VERIFICADO, RELLENADO, CAMBIADO o NO_REQUERIDO");
        }

        detalle.setEstadoComponente(request.estadoComponente());
        detalle.setAccionRealizada(request.accionRealizada());
        detalle.setValorMedido(limpiar(request.valorMedido()));
        detalle.setObservacionTecnica(limpiar(request.observacionTecnica()));
        detalle.setFechaActualizacion(LocalDateTime.now());
        return InformeMapper.toItem(detalleRepository.save(detalle));
    }

    @Transactional
    public InformeDetalleResponse subirFichaTecnica(Long id, MultipartFile archivo) {
        InformeVehiculo informe = cargar(id);
        validarEditable(informe);
        String anterior = informe.getFichaTecnicaPdfUrl();
        String url = fileStorageService.guardarFichaTecnica("informes", id, archivo);
        informe.setFichaTecnicaPdfUrl(url);
        InformeVehiculo guardado = informeRepository.save(informe);
        if (anterior != null && !anterior.equals(url)) {
            fileStorageService.eliminar(anterior);
        }
        return InformeMapper.toDetalle(guardado, configuracionService.obtener());
    }

    public InformeVehiculo cargar(Long id) {
        return informeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Informe", id));
    }

    public static void validarEditable(InformeVehiculo informe) {
        if (informe.getEstado() == EstadoInforme.ENTREGADO) {
            throw new BusinessException("La orden ya fue entregada y no admite modificaciones");
        }
        if (informe.getEstado() == EstadoInforme.CANCELADO) {
            throw new BusinessException("La orden esta cancelada y no admite modificaciones");
        }
    }

    private void aplicarCalculosServicio(InformeVehiculo informe, LocalDateTime ingreso) {
        informe.setFechaEstimadaEntrega(
                InformeCalculo.fechaEstimadaEntrega(ingreso, informe.getHorasEstimadas()));
        informe.setKilometrajeProximoServicio(
                InformeCalculo.kilometrajeProximo(informe.getKilometrajeIngreso(), informe.getGarantiaKm()));
        informe.setFechaProximoServicio(
                InformeCalculo.fechaProximo(ingreso, informe.getGarantiaMeses()));
    }

    private void clonarPlantilla(PaqueteServicio paquete, InformeVehiculo informe) {
        for (PlantillaItemPaquete plantilla : paquete.getItems()) {
            DetalleInspeccionInforme detalle = new DetalleInspeccionInforme();
            detalle.setInforme(informe);
            detalle.setPlantillaItemId(plantilla.getId());
            detalle.setCategoria(plantilla.getCategoria());
            detalle.setDescripcionTarea(plantilla.getDescripcionTarea());
            detalle.setEsCondicional(plantilla.getEsCondicional());
            detalle.setRequiereFichaTecnica(plantilla.getRequiereFichaTecnica());
            detalle.setOrdenVisualizacion(plantilla.getOrdenVisualizacion());
            detalle.setAccionRealizada(AccionRealizada.PENDIENTE);
            informe.getDetalles().add(detalle);
        }
    }

    private String siguienteNumero(int anio) {
        String prefijo = "OT-" + anio + "-";
        String ultimo = informeRepository.ultimoNumero(prefijo);
        int secuencia = 1;
        if (ultimo != null && ultimo.length() > prefijo.length()) {
            try {
                secuencia = Integer.parseInt(ultimo.substring(prefijo.length())) + 1;
            } catch (NumberFormatException ignored) {
                secuencia = 1;
            }
        }
        return prefijo + String.format("%04d", secuencia);
    }

    private void actualizarKilometrajeVehiculo(Vehiculo vehiculo, Integer kilometraje) {
        int actual = vehiculo.getKilometrajeActual() == null ? 0 : vehiculo.getKilometrajeActual();
        if (kilometraje != null && kilometraje > actual) {
            vehiculo.setKilometrajeActual(kilometraje);
            vehiculoRepository.save(vehiculo);
        }
    }

    private void reponerStock(InformeVehiculo informe) {
        for (ConsumoInsumo consumo : informe.getConsumos()) {
            insumoRepository.buscarParaActualizar(consumo.getInsumo().getId()).ifPresent(insumo -> {
                insumo.setStockActual(insumo.getStockActual() + consumo.getCantidad());
                insumoRepository.save(insumo);
            });
        }
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }
}
