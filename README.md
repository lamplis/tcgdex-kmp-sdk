# tcgdex-kmp-sdk (WIP)

Kotlin Multiplatform SDK for TCGdex.

## Targets
- Android (CIO engine)
- iOS (Darwin engine)

## Usage
```kotlin
val client = app.cardium.tcgdex.sdk.TcgDex.from()
```

## API mapping (outline)
- Models: `@Serializable` domain types in `commonMain`
- Client: `TcgDexClient` exposes typed operations (cards, sets, languages)
- HTTP: Ktor with ContentNegotiation + Kotlinx JSON

## Notes
- This module replaces the prior Python SDK usage; Java submodule kept for reference.

