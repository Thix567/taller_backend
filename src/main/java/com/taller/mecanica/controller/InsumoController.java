package com.taller.mecanica.controller;

import com.taller.mecanica.model.entity.Insumo;
import com.taller.mecanica.model.enums.CategoriaInsumo;
import com.taller.mecanica.service.InsumoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
public class InsumoController {

    private final InsumoService insumoService;

    @GetMapping
    public List<Insumo> listar(@RequestParam(required = false) CategoriaInsumo categoria,
                               @RequestParam(required = false) String q) {
        return insumoService.listar(categoria, q);
    }

    @GetMapping("/alertas-stock")
    public List<Insumo> alertasStock() {
        return insumoService.alertasStock();
    }

    @GetMapping("/{id}")
    public Insumo obtener(@PathVariable Long id) {
        return insumoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Insumo crear(@Valid @RequestBody Insumo datos) {
        return insumoService.crear(datos);
    }

    @PutMapping("/{id}")
    public Insumo actualizar(@PathVariable Long id, @Valid @RequestBody Insumo datos) {
        return insumoService.actualizar(id, datos);
    }

    @PostMapping(value = "/{id}/ficha-tecnica", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Insumo subirFichaTecnica(@PathVariable Long id, @RequestPart("archivo") MultipartFile archivo) {
        return insumoService.subirFichaTecnica(id, archivo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        insumoService.eliminar(id);
    }
}
