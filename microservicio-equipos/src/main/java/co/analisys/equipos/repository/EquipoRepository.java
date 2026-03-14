package co.analisys.equipos.repository;

import co.analisys.equipos.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import co.analisys.equipos.model.EquipoId;

public interface EquipoRepository extends JpaRepository<Equipo, EquipoId> {
}
