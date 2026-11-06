package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.Cliente;
import com.taller.mecanica.repository.ClienteRepository;
import com.taller.mecanica.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    public List<Cliente> listar(String q) {
        if (q != null && !q.isBlank()) {
            return clienteRepository.buscar(q.trim());
        }
        return clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "nombreCompleto"));
    }

    public Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Cliente", id));
    }

    @Transactional
    public Cliente crear(Cliente datos) {
        normalizar(datos);
        if (clienteRepository.existsByDniRuc(datos.getDniRuc())) {
            throw new BusinessException("Ya existe un cliente con el DNI/RUC " + datos.getDniRuc());
        }
        datos.setId(null);
        datos.setFechaRegistro(LocalDateTime.now());
        return clienteRepository.save(datos);
    }

    @Transactional
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente cliente = obtener(id);
        normalizar(datos);
        if (clienteRepository.existsByDniRucAndIdNot(datos.getDniRuc(), id)) {
            throw new BusinessException("Ya existe otro cliente con el DNI/RUC " + datos.getDniRuc());
        }
        cliente.setDniRuc(datos.getDniRuc());
        cliente.setNombreCompleto(datos.getNombreCompleto());
        cliente.setTelefonoWhatsapp(datos.getTelefonoWhatsapp());
        cliente.setCorreo(datos.getCorreo());
        cliente.setDireccion(datos.getDireccion());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = obtener(id);
        if (vehiculoRepository.existsByClienteId(id)) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene vehiculos registrados");
        }
        clienteRepository.delete(cliente);
    }

    private void normalizar(Cliente cliente) {
        if (cliente.getDniRuc() != null) {
            cliente.setDniRuc(cliente.getDniRuc().trim());
        }
        if (cliente.getNombreCompleto() != null) {
            cliente.setNombreCompleto(cliente.getNombreCompleto().trim());
        }
        if (cliente.getTelefonoWhatsapp() != null) {
            cliente.setTelefonoWhatsapp(cliente.getTelefonoWhatsapp().trim());
        }
        if (cliente.getCorreo() != null) {
            cliente.setCorreo(cliente.getCorreo().trim().toLowerCase());
        }
    }
}
