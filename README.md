# Documentación del Proyecto: Tamashi

## 1. Arquitectura General

El proyecto sigue una arquitectura moderna de desarrollo de Android, basada en los siguientes principios:

*   **MVVM (Model-View-ViewModel)**: La lógica de la interfaz de usuario (UI) está separada de la lógica de negocio. Las vistas (Compose) observan los datos de los ViewModels y reaccionan a los cambios.
*   **Inyección de Dependencias (Manual)**: Los ViewModels y otras clases reciben sus dependencias (como los Repositorios) a través de sus constructores. Esto facilita las pruebas y el desacoplamiento.
*   **Repositorio (Repository Pattern)**: Se utiliza un repositorio para abstraer el origen de los datos. La aplicación utiliza **Room Database** (`RoomPlaylistRepository`) para persistir playlists y objetivos localmente. Esto permite que el resto de la aplicación no necesite saber *cómo* se obtienen o guardan los datos.
*   **UI Declarativa con Jetpack Compose**: Toda la interfaz de usuario está construida con Jetpack Compose, lo que permite crear vistas de forma más rápida y con menos código.

## 2. Componentes Principales

### 2.1. Capa de Datos (`/data`)

Incluye la persistencia de selección de Tamashi mediante `TamashiPreferencesRepository` (SharedPreferences), con claves:
- `selected_tamashi_name`
- `selected_tamashi_asset`
- `is_tamashi_chosen`

Provee flujos `flowSelectedTamashi()` y `flowIsTamashiChosen()` para sincronizar configuración global (`TutorialConfig`) y gating de arranque.

#### Base de Datos Local (`/data/local`)

La persistencia de playlists y objetivos se realiza mediante **Room Database**:

- **`TamashiDatabase.kt`**: Clase principal de la base de datos Room (Singleton).
- **`PlaylistEntity.kt`**: Entidad que representa una playlist en la BD.
- **`ObjectiveEntity.kt`**: Entidad que representa un objetivo/tarea (con ForeignKey a playlist).
- **`PlaylistDao.kt`**: DAO con operaciones CRUD para playlists.
- **`ObjectiveDao.kt`**: DAO con operaciones CRUD para objetivos.
- **`RoomPlaylistRepository.kt`**: Implementación de `PlaylistRepository` que usa Room.

Las playlists y objetivos se persisten automáticamente y sobreviven al cierre de la app.

### 2.2. Capa de Lógica (`/viewmodel`)

#### `HomeViewModel.kt`
Gestiona playlists y objetivos, y expone StateFlows observados por la UI.

#### `TamashiSelectionViewModel.kt`
Administra la selección de Tamashi inicial y desde Perfil. Expone `TamashiSelectionUiState` con opciones, seleccionado y flag `isChosen`. Acciones: `selectTamashi`, `confirmSelection`.

#### `tutorial/TutorialViewModel.kt`
Orquesta el estado del tutorial (paso actual, visibilidad) delegando en `TutorialRepository`.

### 2.3. Capa de UI (`/ui`)

#### `TamashiSelectionScreen.kt`
Pantalla de onboarding para elegir Tamashi: muestra 3 círculos en pirámide con Bublu arriba (seleccionable) y dos inferiores bloqueados. Centrada y con indicador visual de selección persistente (borde).

#### `tutorial/TutorialOverlay.kt`
Overlay reutilizable para tutoriales:
- Tamashi en esquina inferior derecha.
- Globo encima creciendo hacia arriba, con fondo lila y texto negro.
- Animación de escritura y avance sólo por tap (no automático).
- Callback `onStepCompleted` opcional en el tap.

#### `tutorial/SpeechBubble.kt`
Globo de texto con estilos por defecto y soporte de “cola” triangular apuntando al Tamashi. Parámetros para personalizar tamaño y desplazamiento.

#### `HomeScreen.kt`
Capa 1: Botón centrado "Aprender a Agregar una Playlist" cuando no hay playlists.
Capa 2: FAB "+ Nueva Playlist" cuando hay playlists.
Capa 3: Overlay de tutorial por encima (cuando visible).

#### `PetScreen.kt`
Pantalla principal del Tamashi con animación Lottie:
- Muestra animación del Tamashi usando **Lottie Compose** (archivo JSON en `/res/raw/`).
- Animación en loop infinito con `LottieConstants.IterateForever`.
- Mensaje de bienvenida y motivación debajo de la animación.

### 2.4. Utilidades (`/util`)

#### `tutorial/TutorialConfig.kt`
Configuración central del Tamashi activo (nombre y asset). Facilita escalar a múltiples Tamashis.

#### `tutorial/TutorialLayoutUtils.kt`
Constantes de layout reutilizables.

## 3. Flujo de Arranque y Onboarding

1. `MainActivity` crea `TamashiPreferencesRepository` y `TamashiSelectionViewModel`.
2. Si `isTamashiChosen=false`, muestra `TamashiSelectionScreen`. Al confirmar, persiste selección y sincroniza `TutorialConfig`.
3. Si `isTamashiChosen=true`, navega a `MainScreen` con Home.

## 4. Tutorial de Playlists

- Cuando no hay playlists, aparece el botón “Aprender a Agregar una Playlist” centrado.
- Al tocar, inicia el tutorial en overlay. Avance por tap.
- Tras el primer paso (al tocar), puede navegar a “Crear Playlist”.

## 5. Reutilización del Formato Tamashi + Globo

`TamashiMessageOverlay` permite mostrar mensajes de apoyo o contextuales reutilizando `TamashiAvatar` y `SpeechBubble` con animación opcional. Puede activarse desde cualquier pantalla.

## 6. Pruebas y Calidad

- Preferir tests unitarios de `TamashiPreferencesRepository` y `TamashiSelectionViewModel`.
- Lint: mantener imports limpios y parámetros `modifier` como primero en composables públicos.
- Avance del tutorial: verificado que no cambia de paso automáticamente; sólo mediante tap.
