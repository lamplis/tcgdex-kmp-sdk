# TCGdex Kotlin Multiplatform SDK

Kotlin Multiplatform (KMP) SDK for the TCGdex REST API. Provides a small, pragmatic client and serializable models for cards and sets across Android, iOS and JVM.

## Supported targets

- Android (Ktor CIO engine)
- iOS (Ktor Darwin engine)
- JVM (Ktor CIO engine)

## Requirements

- Kotlin 2.2.20
- Ktor 3.3.x
- Java 17 (Android compile options target/source 17)
- Android: compileSdk 36, minSdk 24

## Getting started

### 1) Add the module to your project

Option A: As a local Gradle module (recommended in a monorepo)

1. Place this module under `libs/tcgdex-kmp-sdk/` (submodule or folder).
2. In `settings.gradle.kts`:

```kotlin
include(":libs:tcgdex-kmp-sdk")
```

3. In your module (e.g., `shared/build.gradle.kts`) add a dependency:

```kotlin
dependencies {
    commonMainImplementation(project(":libs:tcgdex-kmp-sdk"))
}
```

Option B: As a standalone repository

- Include this repository as a Git submodule, then follow steps 2 and 3 above to reference `:libs:tcgdex-kmp-sdk`.

### 2) Initialize the client

Use the factory to obtain a platform-appropriate Ktor client under the hood.

```kotlin
import app.cardium.tcgdex.sdk.TcgDex
import app.cardium.tcgdex.sdk.TcgDexClient

val tcgdex: TcgDexClient = TcgDex.from()
```

### 3) Make requests

Cards and sets are available through simple suspending functions. Pass the TCGdex language code (e.g., "en", "fr").

```kotlin
// Get first page of sets
val sets = tcgdex.getSets(language = "en", page = 1, pageSize = 50).getOrThrow()

// Load a specific set by id (e.g., "A1")
val baseSet = tcgdex.getSetById(language = "en", setId = "A1").getOrThrow()

// Fetch a card by id
val card = tcgdex.getCardById(language = "en", cardId = "A1-001").getOrThrow()

// Search by name (accented names supported by TCGdex)
val results = tcgdex.searchCardsByName(language = "fr", query = "évol", page = 1, pageSize = 20).getOrThrow()
```

### Filtering by set

The search API returns a paginated list of cards. To exclude specific sets (e.g., A1):

```kotlin
val response = tcgdex.searchCardsByName(language = "en", query = "eevee").getOrThrow()
val filtered = response.data?.filter { it.set?.id != "A1" }.orEmpty()
```

## Dependency details

This module uses:

- Ktor Client + ContentNegotiation (Kotlinx JSON)
- Kotlinx Serialization (`@Serializable` models in `commonMain`)

Android engine: CIO
iOS engine: Darwin
JVM engine: CIO

No additional configuration is required; the client is created via `TcgDex.from()` which supplies a platform engine.

## Language handling notes

TCGdex endpoints are language-scoped (e.g., `https://api.tcgdex.net/v2/{lang}/...`). If your UI accepts user input in various locales, pass an appropriate language code:

- English: `en`
- French: `fr`
- Spanish: `es`
- German: `de`
- Portuguese: `pt`
- Italian: `it`
- Japanese: `ja`
- Korean: `ko`
- Chinese: `zh`

Accented queries such as "évol" generally require a language where those names exist (e.g., `fr`). If you need cross-language matching, implement a small fallback strategy in your application (try the user language first, then `fr`, then `en`).

## Testing

The SDK can be tested with Ktor’s `MockEngine` or real network calls.

Mock example (common tests):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*

val engine = MockEngine { request ->
    respond(
        content = "[]",
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    )
}
val client = HttpClient(engine)
```

Real network tests require internet access and are best marked as integration tests.

## Versioning

This module is currently versioned alongside the host repo. When separating into an independent artifact, prefer semantic versioning.

## License

MIT License. See `LICENSE` if present in your distribution.
