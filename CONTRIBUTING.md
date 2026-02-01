# Directrices para Contribuir a Tamashi

¡Gracias por tu interés en contribuir a Tamashi! Estamos emocionados de recibir ayuda de la comunidad.

Para asegurar que el proceso sea claro y efectivo para todos, hemos establecido algunas directrices.

## ¿Cómo Puedo Contribuir?

### Reporte de Errores (Bugs)

*   Asegúrate de que el error no haya sido reportado previamente buscando en los [Issues](https://github.com/AriZona07/tamashi/issues) de GitHub.
*   Si no encuentras un reporte existente, por favor, [abre un nuevo issue](https://github.com/AriZona07/tamashi/issues/new). Sé lo más específico posible, incluyendo la versión de la app, la versión de Android y los pasos para reproducir el error.

### Sugerencias de Nuevas Funcionalidades

*   Las sugerencias son bienvenidas. Antes de empezar a trabajar en una nueva funcionalidad, por favor, [abre un issue](https://github.com/AriZona07/tamashi/issues/new) para describirla.
*   Esto nos permite discutir la idea y asegurarnos de que se alinea con la visión del proyecto antes de que inviertas tiempo en el desarrollo.

### Pull Requests (PRs)

Nuestra estrategia de ramas es la siguiente:
*   `main`: Contiene el código de producción más reciente y estable.
*   `develop`: Es la rama de desarrollo principal. **Todos los Pull Requests deben apuntar a esta rama.**

El proceso es el siguiente:

1.  **Fork y Branch:** Haz un "fork" del repositorio y crea una nueva rama desde `develop`. Nómbrala de forma descriptiva (ej. `feat/nueva-funcionalidad` o `fix/error-visual`).
2.  **Desarrollo:** Realiza tus cambios en tu nueva rama. Asegúrate de que el proyecto compile sin errores ejecutando `./gradlew assembleDebug`.
3.  **Estilo de Código:** El proyecto usa un archivo `.editorconfig` para mantener un estilo de código consistente. Por favor, asegúrate de que tu editor lo respete para que tus cambios sigan las mismas reglas de formato.
4.  **Envía el PR:** Envía tu Pull Request a la rama `develop` del repositorio principal. Los administradores del proyecto se encargarán de fusionar `develop` en `main` cuando se prepare una nueva versión estable.

Al enviar un Pull Request, confirmas que tu contribución se licencia bajo la misma **Licencia MIT** que cubre el proyecto.

¡Gracias de nuevo por tu contribución!
