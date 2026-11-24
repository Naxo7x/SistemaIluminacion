# SistemaIluminacion

Prototipo de sistema de iluminación inteligente con control y monitoreo en tiempo real mediante aplicación móvil Android, base de datos en la nube (Firebase Realtime Database) y código para el microcontrolador con sensor de luz y LED.

## Asignatura y equipo
- **Asignatura:** Ingeniería de Software (TI3043 / ES2) — Sección TI3043
- **Docente:** Oscar Rodríguez

**Integrantes y roles:**
- **Guido Jiménez:** Líder de proyecto
- **Dan Bravo** Programador
- **Cristóbal Padilla:** Analista

## Qué hace el sistema
- Muestra en tiempo real el nivel de luz actual, el estado del LED y el modo de operación.
- Permite trabajar en modo automático (según umbrales configurados) o manual.
- Incluye gestión de registros (lugar y descripción) para crear, consultar, editar y eliminar datos, con validaciones y confirmaciones.
- Sincroniza los cambios con Firebase Realtime Database.

## Cómo abrir y usar el proyecto
1. Descargar o clonar el repositorio y extraer los archivos en el equipo.
2. **App Android:** Abrir la carpeta `SistemaIluminacion` en **Android Studio**, esperar que cargue Gradle, verificar `google-services.json` y ejecutar en emulador o celular.
3. //OPCIONAL Codigo de Esp32 , Abrir en Arduino IDE
4. **Microcontrolador (Arduino):** Abrir el archivo del código en el Arduino IDE, configurar los datos de red/Firebase si aplica y cargar el sketch en la placa.
