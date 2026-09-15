package co.unicauca.bancopreguntas;

import co.unicauca.bancopreguntas.core.*;
import co.unicauca.bancopreguntas.plugins.PipelineQuestionPlugin;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionPipelineTest {

    @Test
    public void testPipelineExitoso() throws Exception {
        PipelineQuestionPlugin plugin = new PipelineQuestionPlugin();
        QuestionRequest req = new QuestionRequest(
            "Pregunta 1", "Texto explicativo largo de prueba para validar", 
            "PIPELINE_VALIDATED", Arrays.asList("A", "B", "C", "D"), 
            "A", "Arquitectura de Software", "ALTA"
        );
        Question q = plugin.generate(req);
        assertNotNull(q.getId());
        assertEquals("Arquitectura de Software", q.getCategory());
        assertEquals("A", q.getCorrectAnswer());
    }

    @Test
    public void testPipelineFallaOpciones() {
        PipelineQuestionPlugin plugin = new PipelineQuestionPlugin();
        QuestionRequest req = new QuestionRequest(
            "Pregunta 2", "Texto explicativo largo de prueba para validar", 
            "PIPELINE_VALIDATED", Arrays.asList("A"), 
            "A", "Arquitectura de Software", "ALTA"
        );
        Exception exception = assertThrows(Exception.class, () -> plugin.generate(req));
        assertTrue(exception.getMessage().contains("mínimo 2 opciones"));
    }

    @Test
    public void testPipelineFallaRespuestaCorrectaFueraDeOpciones() {
        PipelineQuestionPlugin plugin = new PipelineQuestionPlugin();
        QuestionRequest req = new QuestionRequest(
            "Pregunta 3", "Texto explicativo largo de prueba para validar", 
            "PIPELINE_VALIDATED", Arrays.asList("A", "B", "C", "D"), 
            "Z", "Arquitectura de Software", "ALTA"
        );
        Exception exception = assertThrows(Exception.class, () -> plugin.generate(req));
        assertTrue(exception.getMessage().contains("debe pertenecer a las opciones"));
    }
}
