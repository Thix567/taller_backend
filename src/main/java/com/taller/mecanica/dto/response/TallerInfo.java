package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.entity.ConfiguracionTaller;

public record TallerInfo(
        Long id,
        String nombreTaller,
        String ruc,
        String direccion,
        String telefono,
        String telefonoWhatsapp,
        String correo,
        String logoUrl,
        String horarioAtencion,
        String textoGarantia,
        String mensajeRecordatorio,
        Integer capacidadMaximaSimultanea
) {

    public static TallerInfo from(ConfiguracionTaller taller) {
        if (taller == null) {
            return null;
        }
        return new TallerInfo(
                taller.getId(),
                taller.getNombreTaller(),
                taller.getRuc(),
                taller.getDireccion(),
                taller.getTelefono(),
                taller.getTelefonoWhatsapp(),
                taller.getCorreo(),
                taller.getLogoUrl(),
                taller.getHorarioAtencion(),
                taller.getTextoGarantia(),
                taller.getMensajeRecordatorio(),
                taller.getCapacidadMaximaSimultanea()
        );
    }
}
