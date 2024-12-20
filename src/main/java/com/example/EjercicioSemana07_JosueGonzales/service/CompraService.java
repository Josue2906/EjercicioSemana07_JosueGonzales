package com.example.EjercicioSemana07_JosueGonzales.service;

import com.example.EjercicioSemana07_JosueGonzales.model.Compra;
import com.example.EjercicioSemana07_JosueGonzales.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompraService {
    @Autowired
    private CompraRepository compraRepository;

    // Guardar una compra
    public void guardar(Compra compra) {
        compraRepository.save(compra);
    }

    // Listar todas las compras
    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }

    // Buscar compra por ID
    public Optional<Compra> buscarPorId(Long id) {
        return compraRepository.findById(id);
    }

    // Eliminar compra por ID
    public void eliminarPorId(Long id) {
        compraRepository.deleteById(id);
    }
}
