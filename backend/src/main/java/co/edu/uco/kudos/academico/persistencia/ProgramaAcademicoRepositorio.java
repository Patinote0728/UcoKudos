package co.edu.uco.kudos.academico.persistencia;

import co.edu.uco.kudos.academico.dominio.ProgramaAcademico;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface ProgramaAcademicoRepositorio extends JpaRepository<ProgramaAcademico, UUID> {
    List<ProgramaAcademico> findByFacultadIdAndActivoTrue(UUID facultadId);
    @EntityGraph(attributePaths = "facultad")
    List<ProgramaAcademico> findByActivoTrueOrderByNombreAsc();
}
