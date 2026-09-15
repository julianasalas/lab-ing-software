# Taller 2 — Principios SOLID
**Laboratorio de Ingeniería del Software II — Universidad del Cauca — Periodo 2-2026**

Aplicación de escritorio (Java + Swing) para la gestión de usuarios del sistema, aplicando los cinco principios SOLID sobre una base de datos SQLite.

## 1. Requisitos funcionales cubiertos

- Registro de usuarios con: usuario (login), nombre completo, rol, estado (Activo/Inactivo) y contraseña.
- Validación de contraseña: mínimo 6 caracteres, al menos un dígito, una mayúscula y un carácter especial.
- Contraseña almacenada **cifrada** en la base de datos usando **Argon2id**.
- Inicio de sesión con validación de credenciales y de estado (un usuario Inactivo no puede iniciar sesión).
- Tablero/menú principal con opciones distintas según el rol del usuario (Administrador, Autor de preguntas, Revisor, Docente, Estudiante).
- Persistencia en **SQLite** (archivo `usuarios.db`, creado automáticamente en la primera ejecución).

## 2. Cómo se aplicó cada principio SOLID

| Principio | Dónde se aplica |
|---|---|
| **S**RP (Responsabilidad única) | `User` solo representa datos; `UserValidator` solo valida; `SQLiteUserRepository` solo persiste; `Argon2PasswordHasher` solo cifra; `AuthService` solo autentica; `UserService` solo gestiona el registro/consulta de usuarios. |
| **O**CP (Abierto/Cerrado) | `PasswordPolicy` y `RoleMenuProvider` son interfaces: se pueden agregar nuevas políticas de contraseña o nuevos menús por rol creando nuevas implementaciones, sin modificar las clases existentes. |
| **L**SP (Sustitución de Liskov) | Cualquier implementación de `UserRepository`, `PasswordHasher` o `PasswordPolicy` puede sustituir a la actual sin romper el comportamiento esperado por quien la usa (por ejemplo, en las pruebas unitarias se usan mocks en lugar de las implementaciones reales). |
| **I**SP (Segregación de Interfaces) | `UserRepository` y `PasswordHasher` exponen únicamente los métodos que sus clientes realmente necesitan, sin mezclar responsabilidades ajenas. |
| **D**IP (Inversión de Dependencias) | `UserService` y `AuthService` (módulos de alto nivel) dependen de las abstracciones `UserRepository` y `PasswordHasher`, no de `SQLiteUserRepository` ni `Argon2PasswordHasher` directamente. Las implementaciones concretas se ensamblan en un único lugar: `Main.java` (el "composition root"), igual que en el ejemplo 5 visto en la clase teórica. |

## 3. Estructura del proyecto

```
src/main/java/co/unicauca/taller2/
 ├── Main.java                     # Composition root: ensambla todas las dependencias
 ├── model/                        # Entidades de dominio (User, Role, UserStatus)
 ├── db/                           # Conexión y creación del esquema SQLite
 ├── repository/                   # UserRepository (interfaz) + SQLiteUserRepository (impl.)
 ├── hashing/                      # PasswordHasher (interfaz) + Argon2PasswordHasher (impl.)
 ├── validation/                   # PasswordPolicy, UserValidator, excepciones
 ├── service/                      # UserService, AuthService (lógica de negocio)
 └── ui/                           # LoginFrame, RegisterFrame, MainMenuFrame (Swing)

src/test/java/co/unicauca/taller2/
 ├── validation/                   # Pruebas de DefaultPasswordPolicy y UserValidator
 └── service/                      # Pruebas de UserService y AuthService (con Mockito)
```

## 4. Cómo ejecutar el proyecto

Requisitos: JDK 17+ y Maven.

```bash
# Compilar y ejecutar las pruebas unitarias
mvn test

# Generar el ejecutable (fat jar) y correrlo
mvn package
java -jar target/gestion-usuarios-solid.jar
```

Al ejecutarse por primera vez se crea automáticamente el archivo `usuarios.db` (SQLite) en el directorio de ejecución, con la tabla `users` ya definida. No es necesario crear la base de datos manualmente.

Si prefieres importar el proyecto en IntelliJ IDEA o VS Code (ver referencias de la guía del taller para configurar el plugin de SQLite), solo abre la carpeta como proyecto Maven; las dependencias (`sqlite-jdbc`, `argon2-jvm`, JUnit 5, Mockito) se descargan automáticamente desde el `pom.xml`.

## 5. Pruebas unitarias

Se probaron las clases de dominio/lógica de negocio (no la interfaz gráfica):

- `DefaultPasswordPolicyTest`: valida cada regla de complejidad de contraseñas por separado.
- `UserValidatorTest`: valida username, nombre completo y contraseña.
- `UserServiceTest`: registro exitoso, username duplicado, contraseña inválida, listado y cambio de estado — usando **Mockito** para simular `UserRepository` y `PasswordHasher` sin necesidad de una base de datos real.
- `AuthServiceTest`: login exitoso, usuario inexistente, contraseña incorrecta y usuario inactivo.

## 6. Subir el proyecto a GitHub por consola

```bash
git init
git add .
git commit -m "Taller 2: Gestión de usuarios con principios SOLID"
git branch -M main
git remote add origin <URL-de-tu-repositorio>
git push -u origin main
```

## 7. Notas de diseño adicionales

- La contraseña en texto plano se maneja como `char[]` en vez de `String` cuando es posible, y se sobrescribe (`wipeArray` / `Arrays.fill`) después de usarla, como buena práctica para reducir su tiempo de vida en memoria.
- `AuthService` devuelve siempre el mismo mensaje genérico ("Usuario o contraseña incorrectos") cuando el usuario no existe o la contraseña es incorrecta, para no revelar cuál de los dos datos falló.
