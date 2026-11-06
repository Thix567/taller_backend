package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.Cliente;
import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.model.enums.EstadoCita;
import com.taller.mecanica.repository.CitaRepository;
import com.taller.mecanica.repository.ClienteRepository;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final InformeVehiculoRepository informeRepository;
    private final CitaRepository citaRepository;

    public List<Vehiculo> listar(Long clienteId, String q) {
        if (q != null && !q.isBlank()) {
            return vehiculoRepository.buscar(q.trim());
        }
        if (clienteId != null) {
            return vehiculoRepository.findByClienteIdOrderByPlacaAsc(clienteId);
        }
        return vehiculoRepository.findAllByOrderByPlacaAsc();
    }

    public Vehiculo obtener(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Vehiculo", id));
    }

    public Vehiculo porPlaca(String placa) {
        return vehiculoRepository.findByPlacaIgnoreCase(placa.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Vehiculo no encontrado con placa " + placa));
    }

    @Transactional
    public Vehiculo crear(Vehiculo datos) {
        if (datos.getCliente() == null || datos.getCliente().getId() == null) {
            throw new BusinessException("Debe indicar el cliente del vehiculo");
        }
        Cliente cliente = clienteRepository.findById(datos.getCliente().getId())
                .orElseThrow(() -> ResourceNotFoundException.de("Cliente", datos.getCliente().getId()));
        normalizar(datos);
        if (vehiculoRepository.existsByPlacaIgnoreCase(datos.getPlaca())) {
            throw new BusinessException("Ya existe un vehiculo con la placa " + datos.getPlaca());
        }
        datos.setId(null);
        datos.setCliente(cliente);
        if (datos.getKilometrajeActual() == null) {
            datos.setKilometrajeActual(0);
        }
        return vehiculoRepository.save(datos);
    }

    @Transactional
    public Vehiculo actualizar(Long id, Vehiculo datos) {
        Vehiculo vehiculo = obtener(id);
        normalizar(datos);
        if (vehiculoRepository.existsByPlacaIgnoreCaseAndIdNot(datos.getPlaca(), id)) {
            throw new BusinessException("Ya existe otro vehiculo con la placa " + datos.getPlaca());
        }
        if (datos.getCliente() != null && datos.getCliente().getId() != null) {
            Cliente cliente = clienteRepository.findById(datos.getCliente().getId())
                    .orElseThrow(() -> ResourceNotFoundException.de("Cliente", datos.getCliente().getId()));
            vehiculo.setCliente(cliente);
        }
        vehiculo.setPlaca(datos.getPlaca());
        vehiculo.setMarca(datos.getMarca());
        vehiculo.setModelo(datos.getModelo());
        vehiculo.setAnio(datos.getAnio());
        vehiculo.setTipoCombustible(datos.getTipoCombustible());
        vehiculo.setVinMotor(datos.getVinMotor());
        vehiculo.setColor(datos.getColor());
        if (datos.getKilometrajeActual() != null) {
            vehiculo.setKilometrajeActual(datos.getKilometrajeActual());
        }
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = obtener(id);
        if (informeRepository.existsByVehiculoId(id)) {
            throw new BusinessException("No se puede eliminar el vehiculo porque tiene ordenes registradas");
        }
        if (citaRepository.existsByVehiculoIdAndEstadoCitaIn(id,
                List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_TALLER))) {
            throw new BusinessException("No se puede eliminar el vehiculo porque tiene citas activas");
        }
        vehiculoRepository.delete(vehiculo);
    }

    private void normalizar(Vehiculo vehiculo) {
        if (vehiculo.getPlaca() != null) {
            vehiculo.setPlaca(vehiculo.getPlaca().trim().toUpperCase(Locale.ROOT));
        }
        if (vehiculo.getMarca() != null) {
            vehiculo.setMarca(vehiculo.getMarca().trim());
        }
        if (vehiculo.getModelo() != null) {
            vehiculo.setModelo(vehiculo.getModelo().trim());
        }
    }
}
