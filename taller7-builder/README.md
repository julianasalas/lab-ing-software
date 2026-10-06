# Taller 7 – Patrón Builder · Banco de Preguntas Saber Pro

Laboratorio de Ingeniería del Software II – Universidad del Cauca (2-2026)

Implementación del patrón creacional **Builder** para construir preguntas del proyecto
*Gestión del Banco de Preguntas*: selección múltiple, verdadero/falso y abiertas.

## Estructura

```
lisw2_t07_g0X/
├── pom.xml
├── docs/                         Diagrama UML (PlantUML, PNG y SVG)
└── src
    ├── main/java
    │   ├── ApplicationMain.java          Cliente (main)
    │   ├── builder/
    │   │   ├── QuestionBuilder.java              Builder abstracto + reglas comunes
    │   │   ├── MultipleChoiceQuestionBuilder.java
    │   │   ├── TrueFalseQuestionBuilder.java
    │   │   └── OpenQuestionBuilder.java
    │   ├── director/
    │   │   └── QuestionDirector.java             Define el orden de construcción
    │   └── domain/
    │       ├── Question.java                     Producto (inmutable)
    │       ├── Option.java
    │       ├── QuestionType.java
    │       └── DifficultyLevel.java
    └── test/java
        ├── MultipleChoiceQuestionBuilderTest.java
        ├── TrueFalseQuestionBuilderTest.java
        ├── OpenQuestionBuilderTest.java
        ├── QuestionDirectorTest.java
        ├── QuestionBuilderCommonRulesTest.java
        └── DomainTest.java
```

## Reglas de negocio

| Regla | Dónde |
|---|---|
| Enunciado obligatorio, 10–1000 caracteres | `QuestionBuilder` (todas) |
| Dificultad, competencia y autor obligatorios | `QuestionBuilder` (todas) |
| Justificación obligatoria, mínimo 10 caracteres; fuente opcional | `QuestionBuilder` (todas) |
| 3 a 5 opciones (A–E), sin vacías ni repetidas | `MultipleChoiceQuestionBuilder` |
| Exactamente una respuesta correcta que corresponda a una opción existente (por letra o texto) | `MultipleChoiceQuestionBuilder` |
| Opciones fijas Verdadero/Falso; respuesta V/F obligatoria; enunciado es afirmación (no termina en `?`) | `TrueFalseQuestionBuilder` |
| Sin opciones; respuesta de referencia obligatoria (mín. 10 caracteres) | `OpenQuestionBuilder` |

Los pasos `buildXxx` lanzan `IllegalArgumentException` si el dato es inválido, y `build()` lanza
`IllegalStateException` si falta algo obligatorio. Después de `build()` el builder se reinicia.

## Ejecutar

Abrir la carpeta como proyecto Maven en NetBeans / IntelliJ / VS Code, o por consola:

```bash
mvn test                          # 146 métodos de prueba (247 casos con los parametrizados)
mvn compile exec:java             # ejecuta ApplicationMain
```

Requiere Java 17 o superior y JUnit 5 (lo descarga Maven).

## Diagrama UML

`docs/diagrama-clases.png` (fuente editable en `docs/diagrama-clases.puml`).
