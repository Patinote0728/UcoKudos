package co.edu.uco.kudos.academico.api;
import co.edu.uco.kudos.academico.persistencia.ProgramaAcademicoRepositorio;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/programas-academicos")
public class CatalogoAcademicoControlador {
    private final ProgramaAcademicoRepositorio programas;
    public CatalogoAcademicoControlador(ProgramaAcademicoRepositorio p){programas=p;}
    @GetMapping public List<ProgramaAcademicoRespuesta> listar(){return programas.findByActivoTrueOrderByNombreAsc().stream().map(ProgramaAcademicoRespuesta::desde).toList();}
}
