# TCGdex KMP SDK – API Mapping (Outline)

- Cards
  - getCardById(id: String): Result<TcgdxCard>
  - searchCardsByName(name: String, page: Int, pageSize: Int): Result<TcgdxSearchResponse>
  - searchCardsByReference(localId/officialCount): composed via sets + card ids
- Sets
  - getSets(page: Int, pageSize: Int): Result<TcgdxSetResponse>
  - getSetById(id: String): Result<TcgdxSet>
- Languages
  - expose language code in client init (default 'en')

Notes
- REST preferred for cards; GraphQL only where necessary.
- Exclude Pocket (tcgp) by default as in app logic.
- Use kotlinx.serialization models in commonMain.
