# Registro de Personas

Aplicación Java de escritorio (Swing) para gestionar una tabla de personas en MySQL 8,
construida con **arquitectura hexagonal** y respetando los **principios SOLID**.

## Requisitos

- JDK 26
- Maven 3.9+
- MySQL Server 8.x en ejecución

## 1. Preparar la base de datos

Ejecutar el script `src/main/resources/schema.sql` en MySQL:

```bash
mysql -u root -p < src/main/resources/schema.sql
```

Este script:
1. Crea la base `registro_personas`.
2. Crea la tabla `personas` con `dni` como clave primaria.
3. Incluye (comentado) la creación de un usuario de aplicación dedicado,
   con permisos limitados a SELECT/INSERT/UPDATE/DELETE sobre esa tabla.
   **Se recomienda descomentarlo y usarlo en vez de root.**

## 2. Configurar la conexión

Editar `src/main/resources/database.properties` con los datos reales de tu servidor:

```properties
db.url=jdbc:mysql://localhost:3306/registro_personas?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true
db.user=app_registro
db.password=TuPasswordReal
```

## 3. Compilar y ejecutar

```bash
mvn clean package
java -jar target/registro-personas.jar
```

(El plugin `maven-shade-plugin` genera un JAR con todas las dependencias incluidas,
así que un solo archivo es suficiente para ejecutar la app.)

## Arquitectura

```
com.registropersonas
│
├── domain                      → Núcleo: entidad Persona y excepciones de negocio.
│   ├── Persona.java               No depende de NADA externo (ni JDBC, ni Swing).
│   ├── DatosInvalidosException.java
│   ├── PersonaYaExisteException.java
│   ├── PersonaNoEncontradaException.java
│   ├── PersistenciaException.java
│   └── port
│       └── PersonaRepository.java → Puerto de salida (interfaz). El dominio define
│                                     el contrato; la infraestructura lo implementa.
│
├── application                 → Casos de uso. Cada clase = una sola operación de negocio.
│   ├── AgregarPersonaUseCase.java
│   ├── EditarPersonaUseCase.java
│   ├── EliminarPersonaUseCase.java
│   ├── ListarPersonasUseCase.java
│   └── BuscarPersonaUseCase.java
│
└── infrastructure              → Adaptadores: el "mundo exterior" conectado a los puertos.
    ├── config
    │   ├── ConfiguracionBaseDeDatos.java → Lee database.properties
    │   └── FabricaDataSource.java        → Crea y administra el pool HikariCP
    ├── persistence
    │   └── PersonaRepositoryJdbc.java    → Adaptador de salida: implementa
    │                                        PersonaRepository con JDBC + SQL parametrizado
    └── ui
        ├── ModeloTablaPersonas.java      → TableModel de Swing
        ├── PanelFormularioPersona.java   → Formulario reutilizable
        ├── VentanaPrincipal.java         → Adaptador de entrada: GUI que llama a los casos de uso
        └── AplicacionPrincipal.java      → main() / composition root
```

### Por qué es hexagonal

- El **dominio** (`Persona`, excepciones, `PersonaRepository`) no importa nada de
  `javax.swing`, `java.sql` ni HikariCP. Es Java puro.
- La **aplicación** (casos de uso) solo conoce el puerto `PersonaRepository`
  (una interfaz), nunca la clase concreta `PersonaRepositoryJdbc`.
- La **infraestructura** tiene dos tipos de adaptadores:
  - *Adaptador de salida*: `PersonaRepositoryJdbc`, que traduce el puerto a SQL real.
  - *Adaptador de entrada*: `VentanaPrincipal`, que traduce clics de Swing a llamadas
    a los casos de uso.
- Se podría reemplazar Swing por una API REST, o MySQL por PostgreSQL,
  sin tocar una sola línea de `domain` ni `application`.

### Cómo se respetan los principios SOLID

- **S — Responsabilidad única**: cada caso de uso hace una sola cosa;
  `ConfiguracionBaseDeDatos` solo lee properties; `FabricaDataSource` solo administra
  el pool; `ModeloTablaPersonas` solo adapta datos a JTable; `PanelFormularioPersona`
  solo maneja el formulario.
- **O — Abierto/cerrado**: se puede agregar un nuevo adaptador de persistencia
  (por ejemplo uno en memoria para tests) implementando `PersonaRepository`,
  sin modificar los casos de uso existentes.
- **L — Sustitución de Liskov**: cualquier implementación de `PersonaRepository`
  (JDBC, en memoria, mock) puede sustituir a otra sin romper el comportamiento
  esperado por los casos de uso.
- **I — Segregación de interfaces**: `PersonaRepository` expone solo las
  operaciones que el dominio necesita (guardar, actualizar, eliminar, buscar,
  listar), sin métodos irrelevantes.
- **D — Inversión de dependencias**: los casos de uso dependen de la abstracción
  `PersonaRepository`, no de `PersonaRepositoryJdbc`. La clase concreta se inyecta
  desde `AplicacionPrincipal` (composition root).

## Seguridad: SQL embebido sin inyección

Todo el SQL vive en `PersonaRepositoryJdbc` usando `PreparedStatement` con
parámetros vinculados (`?`). Nunca se concatena texto proveniente del usuario
dentro de una sentencia SQL, por lo que el riesgo de inyección SQL queda
eliminado por diseño.

## Funcionalidades

- **Agregar** una persona nueva (con validación de DNI duplicado y de formato).
- **Editar** los datos de una persona existente (el DNI no es editable, ya que
  es la clave primaria).
- **Eliminar** una persona, con diálogo de confirmación.
- **Listado** automático en una tabla, con botón de refrescar manual.
