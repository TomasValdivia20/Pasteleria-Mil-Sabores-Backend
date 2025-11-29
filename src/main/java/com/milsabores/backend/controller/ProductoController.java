package com.milsabores.backend.controller;

import com.milsabores.backend.model.Producto;
import com.milsabores.backend.model.VarianteProducto;
import com.milsabores.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList; // Importante

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @GetMapping("/categoria/{id}")
    public List<Producto> listarPorCategoria(@PathVariable Long id) {
        return productoRepository.findByCategoriaId(id);
    }

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        if (producto.getVariantes() != null) {
            for (VarianteProducto variante : producto.getVariantes()) {
                variante.setProducto(producto);
            }
        }
        return productoRepository.save(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id).map(prod -> {
            // 1. Actualizar campos simples
            prod.setNombre(productoActualizado.getNombre());
            prod.setDescripcion(productoActualizado.getDescripcion());
            prod.setPrecioBase(productoActualizado.getPrecioBase());
            prod.setImagen(productoActualizado.getImagen());
            prod.setCategoria(productoActualizado.getCategoria()); // Actualizar categoría si cambió

            // 2. Actualizar Variantes (Gestión de la Colección)
            // Primero, limpiamos la lista actual. Gracias a 'orphanRemoval=true' en la Entidad,
            // esto borrará de la BBDD las variantes que ya no estén.
            prod.getVariantes().clear();

            // Luego, agregamos las nuevas variantes que vienen del Frontend
            if (productoActualizado.getVariantes() != null) {
                for (VarianteProducto v : productoActualizado.getVariantes()) {
                    // Es CRUCIAL volver a asignar el padre a cada variante hija
                    v.setProducto(prod);
                    prod.getVariantes().add(v);
                }
            }

            return productoRepository.save(prod);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }
}