package com.financorp.serf.repository;

import com.financorp.serf.model.Producto;
import com.financorp.serf.model.Producto.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByCategoria(CategoriaProducto categoria);
    List<Producto> findByStockActualLessThan(Integer stock);
}