# SistemaIluminacion

Prototipo de sistema de iluminación inteligente con control y monitoreo en tiempo real mediante aplicación móvil Android, base de datos en la nube (Firebase Realtime Database) y circuito con sensor de luz y LED.

## Asignatura y equipo
- **Asignatura:** Ingeniería de Software (TI3043 / ES2) — Sección TI3043
- **Docente:** Oscar Rodríguez

**Integrantes y roles:**
- **Dan Bravo:** Líder de proyecto
- **Guido Jiménez:** Programador
- **Cristóbal Padilla:** Analista

## Qué hace el sistema
- Muestra en tiempo real el nivel de luz actual, el estado del LED y el modo de operación.
- Permite trabajar en modo automático (según umbrales configurados) o manual.
- Incluye gestión de registros (lugar y descripción) para crear, consultar, editar y eliminar datos, con validaciones y confirmaciones.
- Sincroniza los cambios con Firebase Realtime Database.

## Cómo abrir el proyecto
1. Descargar o clonar el repositorio y extraer los archivos en el equipo.
2. Abrir la carpeta `SistemaIluminacion` en **Android Studio**.
3. Esperar que cargue Gradle, revisar que esté el archivo `google-services.json` de Firebase y correr la app en un emulador o teléfono.
