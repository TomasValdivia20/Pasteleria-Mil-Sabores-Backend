package com.milsabores.backend.controller;

import com.milsabores.backend.model.Producto;
import com.milsabores.backend.model.VarianteProducto;
import com.milsabores.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; // Importa PutMapping y demás

import java.util.List;

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

    // 🛑 CORRECCIÓN: Usar @PutMapping en lugar de @PUT
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id).map(prod -> {
            prod.setNombre(productoActualizado.getNombre());
            prod.setDescripcion(productoActualizado.getDescripcion());
            prod.setPrecioBase(productoActualizado.getPrecioBase());
            prod.setImagen(productoActualizado.getImagen());
            return productoRepository.save(prod);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }
}