package co.analisys.clases.repository;

import co.analisys.clases.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import co.analisys.clases.model.ClaseId;

public interface ClaseRepository extends JpaRepository<Clase, ClaseId> {
}
