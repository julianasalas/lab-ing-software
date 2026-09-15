# Banco de Preguntas Saber Pro — Taller 4

Aplicación Java SE de escritorio (Swing) que implementa la gestión del ciclo de vida de
preguntas del banco Saber Pro, usando **arquitectura en capas**, el **micro patrón MVC**
y el **patrón Observer**.

Laboratorio de Ingeniería del Software II — Universidad del Cauca, periodo 2-2026.

## Requisitos

- JDK 17 o superior
- Maven 3.8+

## Cómo ejecutar

```bash
mvn clean compile
mvn exec:java
```

O generando el jar ejecutable:

```bash
mvn clean package
java -jar target/banco-preguntas-saberpro-1.0.0.jar
```

## Cómo ejecutar las pruebas

```bash
mvn test
```

## Estructura por capas

```
src/main/java/co/unicauca/bancopreguntas/
├── presentation/                 Capa de presentación
│   ├── GUIQuestions.java         Vista principal: combo, formulario, cambio de estado
│   ├── GUIObserver1.java         Vista observadora 1: estadísticas por estado
│   ├── GUIObserver2.java         Vista observadora 2: gráfica de pastel
│   └── QuestionController.java   Controlador del patrón MVC
├── domain/                       Capa de dominio
│   ├── Question.java             Entidad pregunta
│   ├── QuestionDistractors.java  Objeto de valor: las 4 opciones
│   ├── QuestionState.java        Estados: Borrador, Pendiente de revisión, Eliminada
│   ├── QuestionService.java      Modelo (MVC) y Sujeto observable (Observer)
│   └── QuestionRepository.java   Interfaz de persistencia
├── access/                       Capa de acceso a datos
│   └── QuestionImplRepository.java   Implementación en memoria (LinkedHashMap)
├── infra/                        Capa transversal
│   ├── Observer.java             Contrato de observador
│   └── Subject.java              Gestión de suscriptores y notificación
└── Main.java                     Punto de entrada y ensamblado de dependencias
```

## Funcionamiento

1. La ventana principal carga las preguntas del banco en un `JComboBox`.
2. Al pulsar **Cargar pregunta**, el formulario muestra Id, Nombre, Enunciado,
   Opciones, Respuesta correcta y Estado actual.
3. Al cambiar el estado y pulsar **Actualizar estado**, el `QuestionService`
   persiste el cambio y notifica a sus observadores.
4. Las dos ventanas observadoras (estadísticas y gráfica de pastel) se redibujan
   automáticamente, sin que la ventana principal las invoque.
