package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.entity.Mecanico;

public record MecanicoInfo(
        Long id,
        String nombreCompleto,
        String dni,
        String especialidad,
        String telefono,
        Boolean activo
) {

    public static MecanicoInfo from(Mecanico mecanico) {
        if (mecanico == null) {
            return null;
        }
        return new MecanicoInfo(
                mecanico.getId(),
                mecanico.getNombreCompleto(),
                mecanico.getDni(),
                mecanico.getEspecialidad(),
                mecanico.getTelefono(),
                mecanico.getActivo()
        );
    }
}
