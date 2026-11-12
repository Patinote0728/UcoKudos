package co.edu.uco.kudos.academico.api;
import co.edu.uco.kudos.academico.dominio.ProgramaAcademico;
import java.util.UUID;
public record ProgramaAcademicoRespuesta(UUID id,String nombre,UUID facultadId,String facultad) {
    public static ProgramaAcademicoRespuesta desde(ProgramaAcademico p){return new ProgramaAcademicoRespuesta(p.getId(),p.getNombre(),p.getFacultad().getId(),p.getFacultad().getNombre());}
}
