package com.taller.mecanica.service;

import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.AdicionalOrden;
import com.taller.mecanica.model.entity.ConfiguracionTaller;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.entity.Mecanico;
import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.model.enums.CategoriaInsumo;
import com.taller.mecanica.model.enums.EstadoCita;
import com.taller.mecanica.model.enums.EstadoInforme;
import com.taller.mecanica.dto.response.ClienteInfo;
import com.taller.mecanica.dto.response.DashboardResponse;
import com.taller.mecanica.dto.response.HistorialPlacaResponse;
import com.taller.mecanica.dto.response.InformeResumenResponse;
import com.taller.mecanica.dto.response.ProductividadMecanicoResponse;
import com.taller.mecanica.dto.response.RecordatorioResponse;
import com.taller.mecanica.dto.response.VehiculoInfo;
import com.taller.mecanica.dto.mapper.InformeMapper;
import com.taller.mecanica.repository.CitaRepository;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.InsumoRepository;
import com.taller.mecanica.repository.MecanicoRepository;
import com.taller.mecanica.repository.PagoOrdenRepository;
import com.taller.mecanica.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InformeVehiculoRepository informeRepository;
    private final VehiculoRepository vehiculoRepository;
    private final CitaRepository citaRepository;
    private final InsumoRepository insumoRepository;
    private final PagoOrdenRepository pagoRepository;
    private final MecanicoRepository mecanicoRepository;
    private final ConfiguracionService configuracionService;

    @Value("${app.dias-recordatorio:30}")
    private int diasRecordatorio;

    @Value("${app.km-recordatorio:500}")
    private int kmRecordatorio;

    public DashboardResponse dashboard() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioDia = ahora.toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);
        LocalDateTime inicioMes = ahora.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = inicioMes.plusMonths(1);

        List<InformeVehiculo> todos = informeRepository.findAll();
        long recepcionadas = contarEstado(todos, EstadoInforme.RECEPCIONADO);
        long enProceso = contarEstado(todos, EstadoInforme.EN_PROCESO);
        long listas = contarEstado(todos, EstadoInforme.LISTO_PARA_ENTREGA);
        long entregadasMes = todos.stream()
                .filter(i -> i.getEstado() == EstadoInforme.ENTREGADO)
                .filter(i -> i.getFechaEntregaReal() != null)
                .filter(i -> !i.getFechaEntregaReal().isBefore(inicioMes) && i.getFechaEntregaReal().isBefore(finMes))
                .count();
        long citasHoy = citaRepository.findByFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(inicioDia, finDia).size();
        long citasPendientes = citaRepository.countByEstadoCita(EstadoCita.PENDIENTE)
                + citaRepository.countByEstadoCita(EstadoCita.CONFIRMADA);

        BigDecimal ingresosMes = valor(pagoRepository.sumMontoEntreFechas(inicioMes, finMes));
        BigDecimal saldoPorCobrar = todos.stream()
                .filter(i -> i.getEstado() != EstadoInforme.CANCELADO)
                .map(i -> InformeCalculo.saldoPendiente(
                        InformeCalculo.totalOrden(i.getPrecioPaquete(), i.getAdicionales()),
                        InformeCalculo.totalPagado(i.getPagos())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RecordatorioResponse> recordatorios = recordatorios(null);
        List<InformeResumenResponse> recientes = informeRepository.findAllByOrderByFechaIngresoDesc()
                .stream()
                .limit(8)
                .map(InformeMapper::toResumen)
                .toList();

        List<DashboardResponse.CategoriaConteo> porEstado = new ArrayList<>();
        porEstado.add(new DashboardResponse.CategoriaConteo("RECEPCIONADO", recepcionadas));
        porEstado.add(new DashboardResponse.CategoriaConteo("EN_PROCESO", enProceso));
        porEstado.add(new DashboardResponse.CategoriaConteo("LISTO_PARA_ENTREGA", listas));
        porEstado.add(new DashboardResponse.CategoriaConteo("ENTREGADO", contarEstado(todos, EstadoInforme.ENTREGADO)));
        porEstado.add(new DashboardResponse.CategoriaConteo("CANCELADO", contarEstado(todos, EstadoInforme.CANCELADO)));

        return new DashboardResponse(
                todos.size(),
                recepcionadas + enProceso + listas,
                recepcionadas,
                enProceso,
                listas,
                entregadasMes,
                citasHoy,
                citasPendientes,
                ingresosMes,
                saldoPorCobrar,
                insumoRepository.findConStockBajo().size(),
                recordatorios.size(),
                recientes,
                recordatorios.stream().limit(10).toList(),
                porEstado
        );
    }

    public List<RecordatorioResponse> recordatorios(Integer dias) {
        int ventana = (dias == null || dias < 0) ? diasRecordatorio : dias;
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusDays(ventana);
        ConfiguracionTaller taller = configuracionService.obtener();
        List<RecordatorioResponse> resultado = new ArrayList<>();

        for (Vehiculo vehiculo : vehiculoRepository.findAll()) {
            InformeVehiculo ultimo = informeRepository
                    .findByVehiculoIdOrderByFechaIngresoDesc(vehiculo.getId())
                    .stream()
                    .filter(i -> i.getEstado() != EstadoInforme.CANCELADO)
                    .findFirst()
                    .orElse(null);
            if (ultimo == null) {
                continue;
            }
            LocalDateTime fecha = ultimo.getFechaProximoServicio();
            Integer kmProximo = ultimo.getKilometrajeProximoServicio();
            int kmActual = vehiculo.getKilometrajeActual() != null
                    ? vehiculo.getKilometrajeActual()
                    : (ultimo.getKilometrajeIngreso() != null ? ultimo.getKilometrajeIngreso() : 0);

            boolean fechaVencida = fecha != null && !fecha.isAfter(ahora);
            boolean fechaProxima = fecha != null && !fecha.isAfter(limite);
            boolean kmVencido = kmProximo != null && kmActual >= kmProximo;
            boolean kmProximoCerca = kmProximo != null && (kmProximo - kmActual) <= kmRecordatorio;

            boolean porFecha = fechaVencida || fechaProxima;
            boolean porKm = kmVencido || kmProximoCerca;
            if (!porFecha && !porKm) {
                continue;
            }

            String estadoAlerta = (fechaVencida || kmVencido) ? "VENCIDO" : "PROXIMO";
            String motivo = porFecha && porKm ? "FECHA_Y_KILOMETRAJE" : (porFecha ? "FECHA" : "KILOMETRAJE");
            Long diasRestantes = fecha == null
                    ? null
                    : ChronoUnit.DAYS.between(ahora.toLocalDate(), fecha.toLocalDate());
            Integer kmRestantes = kmProximo == null ? null : kmProximo - kmActual;
            String mensaje = construirMensaje(taller, vehiculo, fecha, kmProximo);

            resultado.add(new RecordatorioResponse(
                    ultimo.getId(),
                    vehiculo.getCliente().getId(),
                    vehiculo.getCliente().getNombreCompleto(),
                    vehiculo.getCliente().getTelefonoWhatsapp(),
                    vehiculo.getId(),
                    vehiculo.getPlaca(),
                    (vehiculo.getMarca() + " " + vehiculo.getModelo()).trim(),
                    ultimo.getFechaIngreso(),
                    ultimo.getKilometrajeIngreso(),
                    kmActual,
                    kmProximo,
                    fecha,
                    diasRestantes,
                    kmRestantes,
                    motivo,
                    estadoAlerta,
                    construirWhatsapp(vehiculo.getCliente().getTelefonoWhatsapp(), mensaje),
                    mensaje
            ));
        }

        resultado.sort(Comparator
                .comparingInt((RecordatorioResponse r) -> "VENCIDO".equals(r.estadoAlerta()) ? 0 : 1)
                .thenComparing(r -> r.fechaProximoServicio() == null ? LocalDateTime.MAX : r.fechaProximoServicio()));
        return resultado;
    }

    public HistorialPlacaResponse historial(String placa) {
        Vehiculo vehiculo = vehiculoRepository.findByPlacaIgnoreCase(placa.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado con placa " + placa));
        List<InformeVehiculo> informes = informeRepository
                .findByVehiculoPlacaIgnoreCaseOrderByFechaIngresoDesc(vehiculo.getPlaca());

        List<HistorialPlacaResponse.Visita> visitas = informes.stream()
                .map(informe -> {
                    String aceite = informe.getConsumos().stream()
                            .filter(c -> c.getInsumo().getCategoria() == CategoriaInsumo.ACEITE)
                            .map(c -> c.getCantidad() + " x " + c.getInsumo().getNombre())
                            .collect(Collectors.joining(", "));
                    List<String> adicionales = informe.getAdicionales().stream()
                            .map(AdicionalOrden::getDescripcion)
                            .toList();
                    BigDecimal total = InformeCalculo.totalOrden(informe.getPrecioPaquete(), informe.getAdicionales());
                    BigDecimal pagado = InformeCalculo.totalPagado(informe.getPagos());
                    return new HistorialPlacaResponse.Visita(
                            informe.getId(),
                            informe.getNumeroOrden(),
                            informe.getFechaIngreso(),
                            informe.getPaquete().getNombre(),
                            informe.getMecanico().getNombreCompleto(),
                            informe.getKilometrajeIngreso(),
                            aceite.isBlank() ? null : aceite,
                            informe.getFichaTecnicaPdfUrl(),
                            adicionales,
                            informe.getRecomendacionesFuturas(),
                            total,
                            pagado,
                            InformeCalculo.saldoPendiente(total, pagado),
                            informe.getEstado()
                    );
                })
                .toList();

        return new HistorialPlacaResponse(
                ClienteInfo.from(vehiculo.getCliente()),
                VehiculoInfo.from(vehiculo),
                visitas
        );
    }

    public List<ProductividadMecanicoResponse> productividad(LocalDateTime desde, LocalDateTime hasta) {
        LocalDateTime fin = hasta != null ? hasta : LocalDateTime.now();
        LocalDateTime inicio = desde != null ? desde : fin.minusDays(30);
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
        }
        List<InformeVehiculo> informes = informeRepository.findByFechaIngresoBetween(inicio, fin);
        Map<Long, List<InformeVehiculo>> porMecanico = informes.stream()
                .filter(i -> i.getMecanico() != null)
                .collect(Collectors.groupingBy(i -> i.getMecanico().getId()));

        return mecanicoRepository.findAllByOrderByNombreCompletoAsc().stream()
                .map(mecanico -> construirProductividad(mecanico, porMecanico.getOrDefault(mecanico.getId(), List.of())))
                .toList();
    }

    private ProductividadMecanicoResponse construirProductividad(Mecanico mecanico, List<InformeVehiculo> informes) {
        long entregadas = informes.stream()
                .filter(i -> i.getEstado() == EstadoInforme.ENTREGADO)
                .count();
        BigDecimal facturado = informes.stream()
                .filter(i -> i.getEstado() != EstadoInforme.CANCELADO)
                .map(i -> InformeCalculo.totalOrden(i.getPrecioPaquete(), i.getAdicionales()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ProductividadMecanicoResponse(
                mecanico.getId(),
                mecanico.getNombreCompleto(),
                mecanico.getEspecialidad(),
                mecanico.getActivo(),
                informes.size(),
                entregadas,
                facturado
        );
    }

    private long contarEstado(List<InformeVehiculo> informes, EstadoInforme estado) {
        return informes.stream().filter(i -> i.getEstado() == estado).count();
    }

    private String construirMensaje(ConfiguracionTaller taller, Vehiculo vehiculo,
                                    LocalDateTime fecha, Integer kmProximo) {
        String plantilla = taller.getMensajeRecordatorio();
        if (plantilla == null || plantilla.isBlank()) {
            plantilla = "Hola {cliente}, le recordamos que su {vehiculo} ({placa}) ya cumple el "
                    + "intervalo de mantenimiento. Le esperamos en {taller} para agendar su proximo servicio.";
        }
        return plantilla
                .replace("{cliente}", vehiculo.getCliente().getNombreCompleto())
                .replace("{vehiculo}", (vehiculo.getMarca() + " " + vehiculo.getModelo()).trim())
                .replace("{placa}", vehiculo.getPlaca())
                .replace("{taller}", taller.getNombreTaller() == null ? "nuestro taller" : taller.getNombreTaller())
                .replace("{fecha}", fecha == null ? "" : fecha.format(FORMATO_FECHA))
                .replace("{km}", kmProximo == null ? "" : String.valueOf(kmProximo));
    }

    private String construirWhatsapp(String telefono, String mensaje) {
        return "https://wa.me/" + normalizarTelefono(telefono) + "?text="
                + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
    }

    private String normalizarTelefono(String telefono) {
        if (telefono == null) {
            return "";
        }
        String digits = telefono.replaceAll("\\D", "");
        if (digits.startsWith("00")) {
            digits = digits.substring(2);
        }
        if (digits.startsWith("51") && digits.length() > 9) {
            return digits;
        }
        if (digits.startsWith("0")) {
            digits = digits.substring(1);
        }
        if (digits.length() == 9) {
            return "51" + digits;
        }
        return digits;
    }

    private BigDecimal valor(BigDecimal monto) {
        return monto == null ? BigDecimal.ZERO : monto;
    }
}
