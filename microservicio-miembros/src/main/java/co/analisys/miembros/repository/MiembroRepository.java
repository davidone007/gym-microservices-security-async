package co.analisys.miembros.repository;

import co.analisys.miembros.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import co.analisys.miembros.model.MiembroId;

public interface MiembroRepository extends JpaRepository<Miembro, MiembroId> {
}
