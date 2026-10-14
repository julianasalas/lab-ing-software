# Taller 8 – Patrones State, Template Method, Decorator, Adapter y Facade

Laboratorio de Ingeniería del Software II – Universidad del Cauca (2-2026)
Proyecto: Banco de Preguntas Saber PRO.

## Estructura

```
lisw2_t08_g0X/
└── src
    ├── main/java/co/unicauca/taller08
    │   ├── ApplicationMain.java     Cliente con menú en consola (usa solo la fachada)
    │   ├── adapter/                 Adapter: QuestionProvider, ExternalQuestionService, ExternalQuestionAdapter
    │   ├── decorator/               Decorator: QuestionComponent, BasicQuestion, QuestionDecorator y 5 decoradores
    │   ├── domain/                  Question (contexto del State), Option, QuestionType
    │   ├── facade/                  Facade: QuestionBankFacade
    │   ├── service/                 Subsistemas: QuestionService, QuestionRepository, QuestionValidator,
    │   │                            QuestionPublisher, QuestionImporter
    │   ├── state/                   State: QuestionState, AbstractQuestionState y 7 estados
    │   └── templatemethod/          Template Method: QuestionEvaluator y evaluadores por tipo
    └── test/java/co/unicauca/taller08   Pruebas JUnit 5
```

## Patrones

| Patrón | Dónde | Qué resuelve |
|---|---|---|
| State | `state/`, `Question` | Ciclo de vida: Borrador → Pendiente de revisión → En revisión → Aprobada → Publicada → Archivada, con Rechazada → Borrador. Cada estado decide qué operaciones permite. |
| Template Method | `templatemethod/` | `evaluate()` es final y fija los pasos: validar, obtener respuestas, comparar, resultado y retroalimentación. Cada tipo implementa solo `validate()` y `compareAnswers()`. |
| Decorator | `decorator/` | Alta dificultad, contextualizada, con retroalimentación, asociada a competencia y marcada para revisión, sin modificar `BasicQuestion`. |
| Adapter | `adapter/` | Convierte el JSON del servicio externo a `Question`. |
| Facade | `facade/` | `QuestionBankFacade` es la única clase que usa el cliente. |

## Ejecutar

- Abrir la carpeta en el IDE como proyecto Maven y correr `co.unicauca.taller08.ApplicationMain`.
  La opción 8 del menú muestra una demostración completa.
- Pruebas: `mvn test` (requiere Java 17+).
