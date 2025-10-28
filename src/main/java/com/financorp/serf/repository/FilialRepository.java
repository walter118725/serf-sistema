package com.financorp.serf.repository;

import com.financorp.serf.model.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * REPOSITORIO de Filiales
 */
@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {
    
    /**
     * Buscar filial por país
     * 
     * Optional<Filial> = Puede encontrar una filial o no
     * - Si existe: optional.get() devuelve la filial
     * - Si no existe: optional.isEmpty() es true
     */
    Optional<Filial> findByPais(String pais);
}
