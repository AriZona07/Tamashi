# Documentación del Proyecto: Tamashi

## 1. Arquitectura General

El proyecto sigue una arquitectura moderna de desarrollo de Android, basada en los siguientes principios:

*   **MVVM (Model-View-ViewModel)**: La lógica de la interfaz de usuario (UI) está separada de la lógica de negocio. Las vistas (Compose) observan los datos de los ViewModels y reaccionan a los cambios.
*   **Inyección de Dependencias (Manual)**: Los ViewModels y otras clases reciben sus dependencias (como los Repositorios) a través de sus constructores. Esto facilita las pruebas y el desacoplamiento.
*   **Repositorio (Repository Pattern)**: Se utiliza un repositorio para abstraer el origen de los datos. La aplicación utiliza una implementación de repositorio en memoria (`PlaylistRepositoryImpl`) que simula una base de datos local. Esto permite que el resto de la aplicación no necesite saber *cómo* se obtienen o guardan los datos.
*   **UI Declarativa con Jetpack Compose**: Toda la interfaz de usuario está construida con Jetpack Compose, lo que permite crear vistas de forma más rápida y con menos código.

## 2. Componentes Principales

### 2.1. Capa de Datos (`/data`)

Esta capa es responsable de manejar los datos de la aplicación, como las playlists y sus objetivos. Toda la lógica de autenticación ha sido eliminada.

#### `PlaylistRepository.kt` (Interfaz)

*   **Responsabilidad**: Define el "contrato" para todas las operaciones de gestión de playlists y objetivos. No contiene implementación, solo las funciones que se pueden realizar (`createPlaylist`, `getUserPlaylists`, `addObjective`, etc.). Esto permite desacoplar la lógica de negocio de la fuente de datos.

#### `PlaylistRepositoryImpl.kt` (Implementación)

*   **Responsabilidad**: Contiene la implementación real de `PlaylistRepository`. Utiliza `MutableStateFlow` para simular una base de datos en memoria. Gestiona la lista de playlists y un mapa de objetivos para cada playlist, proveyendo una solución reactiva y local sin dependencias externas.

#### `Objective.kt` y `Playlist.kt`

*   **Responsabilidad**: Definen las clases de datos (data classes) que modelan las entidades principales de la aplicación: `Playlist` y `Objective`. Son estructuras simples que contienen las propiedades de cada entidad.

### 2.2. Capa de Lógica (`/viewmodel`)

Esta capa contiene los ViewModels, que preparan y gestionan los datos para la UI, interactuando con la capa de datos.

#### `HomeViewModel.kt`

*   **Responsabilidad**: Gestiona la lógica de la pantalla principal, incluyendo la lista de playlists y los objetivos de la playlist seleccionada. Hereda de `androidx.lifecycle.ViewModel` para ser consciente del ciclo de vida.
*   **Flujos de Datos**:
    *   `playlists`: Un `StateFlow` que expone la lista de playlists del usuario, obtenida del repositorio.
    *   `selectedPlaylistId`: Un `StateFlow` que mantiene el ID de la playlist seleccionada por el usuario.
    *   `objectives`: Un `StateFlow` que, usando `flatMapLatest`, reacciona a los cambios en `selectedPlaylistId` para emitir la lista de objetivos de la playlist correcta.
*   **Funciones Clave**: `selectPlaylist()`, `createPlaylist()`, `deletePlaylist()`, `addObjective()`, `toggleObjectiveStatus()`, etc. Estas funciones se comunican con el `PlaylistRepository` dentro de un `viewModelScope`.

### 2.3. Capa de UI (`/ui`)

Contiene los componentes de Jetpack Compose y utilidades relacionadas con la UI.

#### `App.kt`

*   **Responsabilidad**: Es el componente raíz de la aplicación Compose. Configura `MaterialTheme` y renderiza `MainScreen`, pasándole el `HomeViewModel`.
*   **Componentes Clave**:
    *   `App(homeViewModel)`: La función principal que arranca la UI.
    *   `AppPreview()`: Una previsualización para el editor de Android Studio que usa `FakePlaylistRepository`.
    *   `FakePlaylistRepository`: Una implementación falsa del repositorio de playlists que funciona en memoria. Es crucial para el desarrollo y las pruebas de la UI de forma aislada.

#### `MainActivity.kt`

*   **Responsabilidad**: Es el punto de entrada de la aplicación Android (`ComponentActivity`). Inicializa `PlaylistRepositoryImpl` y `HomeViewModel`, y establece el contenido de la UI llamando a la función `App`.

#### `CategoryUtils.kt`

*   **Responsabilidad**: Proporciona una función de utilidad (`getIconForCategory`) que devuelve un icono de Material Design específico para cada categoría de playlist, ayudando a la visualización en la UI.

### 2.4. Utilidades (`/util`)

#### `ValidationUtils.kt`

*   **Estado Actual**: **Este archivo está casi vacío.**
*   **Responsabilidad**: Contener lógica de validación reutilizable. La validación de contraseñas fue eliminada junto con la autenticación. Puede ser extendido en el futuro para validar otros datos, como nombres de playlists.

## 3. Flujo de Datos (Ejemplo: Mostrar Playlists)

1.  **`MainActivity`** crea una instancia de `PlaylistRepositoryImpl` y la usa para inicializar `HomeViewModel`.
2.  **`HomeViewModel`**, al inicializarse, llama a `playlistRepository.getUserPlaylists()`.
3.  **`PlaylistRepositoryImpl`** devuelve un `Flow` que está respaldado por un `MutableStateFlow` en memoria. Este flujo emitirá la lista actual de playlists y cualquier actualización futura.
4.  **`HomeViewModel`** convierte este `Flow` en un `StateFlow` (`playlists`) usando `stateIn`, haciéndolo más robusto para el consumo en la UI.
5.  La **UI (Compose)** en `MainScreen` observa el `StateFlow` `playlists` del `HomeViewModel`. Cada vez que el `StateFlow` emite una nueva lista (porque se añadió o eliminó una playlist), la UI se recompone automáticamente para mostrar la lista actualizada.
