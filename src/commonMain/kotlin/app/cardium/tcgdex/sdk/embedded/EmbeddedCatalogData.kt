package app.cardium.tcgdex.sdk.embedded

object EmbeddedCatalogData {
  private val seriesByLang: Map<String, String> = mapOf(
    "en" to """
[
    {
        "id": "base",
        "name": "Base"
    },
    {
        "id": "misc",
        "name": "Miscellaneous"
    },
    {
        "id": "gym",
        "name": "Gym"
    },
    {
        "id": "neo",
        "name": "Neo"
    },
    {
        "id": "lc",
        "name": "Legendary Collection"
    },
    {
        "id": "ecard",
        "name": "E-Card"
    },
    {
        "id": "ex",
        "name": "EX"
    },
    {
        "id": "pop",
        "name": "POP"
    },
    {
        "id": "tk",
        "name": "Trainer kits"
    },
    {
        "id": "dp",
        "name": "Diamond & Pearl"
    },
    {
        "id": "pl",
        "name": "Platinum"
    },
    {
        "id": "hgss",
        "name": "HeartGold & SoulSilver"
    },
    {
        "id": "col",
        "name": "Call of Legends"
    },
    {
        "id": "bw",
        "name": "Black & White"
    },
    {
        "id": "mc",
        "name": "McDonald's Collection"
    },
    {
        "id": "xy",
        "name": "XY"
    },
    {
        "id": "sm",
        "name": "Sun & Moon"
    },
    {
        "id": "swsh",
        "name": "Sword & Shield"
    },
    {
        "id": "sv",
        "name": "Scarlet & Violet"
    },
    {
        "id": "tcgp",
        "name": "Pokémon TCG Pocket"
    },
    {
        "id": "me",
        "name": "Mega Evolution"
    }
]
""",
    "fr" to """
[
    {
        "id": "base",
        "name": "Base"
    },
    {
        "id": "misc",
        "name": "Miscellaneous"
    },
    {
        "id": "neo",
        "name": "Neo"
    },
    {
        "id": "ecard",
        "name": "E-Card"
    },
    {
        "id": "ex",
        "name": "EX"
    },
    {
        "id": "pop",
        "name": "POP"
    },
    {
        "id": "tk",
        "name": "Trainer kits"
    },
    {
        "id": "dp",
        "name": "Diamond & Pearl"
    },
    {
        "id": "pl",
        "name": "Platinum"
    },
    {
        "id": "hgss",
        "name": "HeartGold & SoulSilver"
    },
    {
        "id": "col",
        "name": "Call of Legends"
    },
    {
        "id": "bw",
        "name": "Black & White"
    },
    {
        "id": "mc",
        "name": "McDonald's Collection"
    },
    {
        "id": "xy",
        "name": "XY"
    },
    {
        "id": "sm",
        "name": "Sun & Moon"
    },
    {
        "id": "swsh",
        "name": "Sword & Shield"
    },
    {
        "id": "sv",
        "name": "Scarlet & Violet"
    },
    {
        "id": "tcgp",
        "name": "Pokémon TCG Pocket"
    },
    {
        "id": "me",
        "name": "Mega Evolution"
    }
]
""",
    "de" to """
[
    {
        "id": "base",
        "name": "Base"
    },
    {
        "id": "neo",
        "name": "Neo"
    },
    {
        "id": "ecard",
        "name": "E-Card"
    },
    {
        "id": "ex",
        "name": "EX"
    },
    {
        "id": "dp",
        "name": "Diamond & Pearl"
    },
    {
        "id": "pl",
        "name": "Platinum"
    },
    {
        "id": "hgss",
        "name": "HeartGold & SoulSilver"
    },
    {
        "id": "col",
        "name": "Call of Legends"
    },
    {
        "id": "bw",
        "name": "Black & White"
    },
    {
        "id": "xy",
        "name": "XY"
    },
    {
        "id": "sm",
        "name": "Sun & Moon"
    },
    {
        "id": "swsh",
        "name": "Sword & Shield"
    },
    {
        "id": "sv",
        "name": "Scarlet & Violet"
    },
    {
        "id": "tcgp",
        "name": "Pokémon TCG Pocket"
    },
    {
        "id": "me",
        "name": "Mega Evolution"
    }
]
""",
    "es" to """
[
    {
        "id": "base",
        "name": "Base"
    },
    {
        "id": "neo",
        "name": "Neo"
    },
    {
        "id": "ex",
        "name": "EX"
    },
    {
        "id": "pop",
        "name": "POP"
    },
    {
        "id": "tk",
        "name": "Trainer kits"
    },
    {
        "id": "dp",
        "name": "Diamond & Pearl"
    },
    {
        "id": "pl",
        "name": "Platinum"
    },
    {
        "id": "hgss",
        "name": "HeartGold & SoulSilver"
    },
    {
        "id": "bw",
        "name": "Black & White"
    },
    {
        "id": "mc",
        "name": "McDonald's Collection"
    },
    {
        "id": "xy",
        "name": "XY"
    },
    {
        "id": "sm",
        "name": "Sun & Moon"
    },
    {
        "id": "swsh",
        "name": "Sword & Shield"
    },
    {
        "id": "sv",
        "name": "Scarlet & Violet"
    },
    {
        "id": "tcgp",
        "name": "Pokémon TCG Pocket"
    },
    {
        "id": "me",
        "name": "Mega Evolution"
    }
]
""",
    "it" to """
[
    {
        "id": "base",
        "name": "Base"
    },
    {
        "id": "neo",
        "name": "Neo"
    },
    {
        "id": "ecard",
        "name": "E-Card"
    },
    {
        "id": "ex",
        "name": "EX"
    },
    {
        "id": "pop",
        "name": "POP"
    },
    {
        "id": "tk",
        "name": "Trainer kits"
    },
    {
        "id": "dp",
        "name": "Diamond & Pearl"
    },
    {
        "id": "pl",
        "name": "Platinum"
    },
    {
        "id": "hgss",
        "name": "HeartGold & SoulSilver"
    },
    {
        "id": "col",
        "name": "Call of Legends"
    },
    {
        "id": "bw",
        "name": "Black & White"
    },
    {
        "id": "mc",
        "name": "McDonald's Collection"
    },
    {
        "id": "xy",
        "name": "XY"
    },
    {
        "id": "sm",
        "name": "Sun & Moon"
    },
    {
        "id": "swsh",
        "name": "Sword & Shield"
    },
    {
        "id": "sv",
        "name": "Scarlet & Violet"
    },
    {
        "id": "tcgp",
        "name": "Pokémon TCG Pocket"
    },
    {
        "id": "me",
        "name": "Mega Evolution"
    }
]
""",
    "pt" to """
[
    {
        "id": "base",
        "name": "Base"
    },
    {
        "id": "ex",
        "name": "EX"
    },
    {
        "id": "dp",
        "name": "Diamond & Pearl"
    },
    {
        "id": "hgss",
        "name": "HeartGold & SoulSilver"
    },
    {
        "id": "col",
        "name": "Call of Legends"
    },
    {
        "id": "bw",
        "name": "Black & White"
    },
    {
        "id": "xy",
        "name": "XY"
    },
    {
        "id": "sm",
        "name": "Sun & Moon"
    },
    {
        "id": "swsh",
        "name": "Sword & Shield"
    },
    {
        "id": "sv",
        "name": "Scarlet & Violet"
    },
    {
        "id": "tcgp",
        "name": "Pokémon TCG Pocket"
    },
    {
        "id": "me",
        "name": "Mega Evolution"
    }
]
""",
  )
  private val setsByLang: Map<String, String> = mapOf(
    "en" to """
[
    {
        "id": "base1",
        "name": "Base Set",
        "releaseDate": "1999-01-09",
        "logo": "https://assets.tcgdex.net/en/base/base1/logo",
        "serieId": "base",
        "official": 102
    },
    {
        "id": "base2",
        "name": "Jungle",
        "releaseDate": "1999-06-16",
        "logo": "https://assets.tcgdex.net/en/base/base2/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/base2/symbol",
        "serieId": "base",
        "official": 64
    },
    {
        "id": "basep",
        "name": "Wizards Black Star Promos",
        "releaseDate": "1999-07-01",
        "logo": "https://assets.tcgdex.net/en/base/basep/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/basep/symbol",
        "serieId": "base",
        "official": 53
    },
    {
        "id": "wp",
        "name": "W Promotional",
        "releaseDate": "1999-09-01",
        "serieId": "base",
        "official": 7
    },
    {
        "id": "base3",
        "name": "Fossil",
        "releaseDate": "1999-10-10",
        "logo": "https://assets.tcgdex.net/en/base/base3/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/base3/symbol",
        "serieId": "base",
        "official": 62
    },
    {
        "id": "jumbo",
        "name": "Jumbo cards",
        "releaseDate": "2000-02-01",
        "serieId": "misc",
        "official": 160
    },
    {
        "id": "base4",
        "name": "Base Set 2",
        "releaseDate": "2000-02-24",
        "logo": "https://assets.tcgdex.net/en/base/base4/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/base4/symbol",
        "serieId": "base",
        "official": 130
    },
    {
        "id": "base5",
        "name": "Team Rocket",
        "releaseDate": "2000-04-24",
        "logo": "https://assets.tcgdex.net/en/base/base5/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/base5/symbol",
        "serieId": "base",
        "official": 82
    },
    {
        "id": "gym1",
        "name": "Gym Heroes",
        "releaseDate": "2000-08-14",
        "logo": "https://assets.tcgdex.net/en/gym/gym1/logo",
        "symbol": "https://assets.tcgdex.net/univ/gym/gym1/symbol",
        "serieId": "gym",
        "official": 132
    },
    {
        "id": "gym2",
        "name": "Gym Challenge",
        "releaseDate": "2000-10-16",
        "logo": "https://assets.tcgdex.net/en/gym/gym2/logo",
        "symbol": "https://assets.tcgdex.net/univ/gym/gym2/symbol",
        "serieId": "gym",
        "official": 132
    },
    {
        "id": "neo1",
        "name": "Neo Genesis",
        "releaseDate": "2000-12-16",
        "logo": "https://assets.tcgdex.net/en/neo/neo1/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo1/symbol",
        "serieId": "neo",
        "official": 111
    },
    {
        "id": "neo2",
        "name": "Neo Discovery",
        "releaseDate": "2001-06-01",
        "logo": "https://assets.tcgdex.net/en/neo/neo2/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo2/symbol",
        "serieId": "neo",
        "official": 75
    },
    {
        "id": "si1",
        "name": "Southern Islands",
        "releaseDate": "2001-07-31",
        "logo": "https://assets.tcgdex.net/en/neo/si1/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/si1/symbol",
        "serieId": "neo",
        "official": 18
    },
    {
        "id": "neo3",
        "name": "Neo Revelation",
        "releaseDate": "2001-09-21",
        "logo": "https://assets.tcgdex.net/en/neo/neo3/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo3/symbol",
        "serieId": "neo",
        "official": 64
    },
    {
        "id": "neo4",
        "name": "Neo Destiny",
        "releaseDate": "2002-02-28",
        "logo": "https://assets.tcgdex.net/en/neo/neo4/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo4/symbol",
        "serieId": "neo",
        "official": 105
    },
    {
        "id": "lc",
        "name": "Legendary Collection",
        "releaseDate": "2002-05-24",
        "logo": "https://assets.tcgdex.net/en/lc/lc/logo",
        "symbol": "https://assets.tcgdex.net/univ/lc/lc/symbol",
        "serieId": "lc",
        "official": 110
    },
    {
        "id": "sp",
        "name": "Sample",
        "releaseDate": "2002-08-01",
        "symbol": "https://assets.tcgdex.net/univ/ecard/sp/symbol",
        "serieId": "ecard",
        "official": 10
    },
    {
        "id": "ecard1",
        "name": "Expedition Base Set",
        "releaseDate": "2002-09-15",
        "logo": "https://assets.tcgdex.net/en/ecard/ecard1/logo",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard1/symbol",
        "serieId": "ecard",
        "official": 165
    },
    {
        "id": "bog",
        "name": "Best of game",
        "releaseDate": "2002-12-01",
        "symbol": "https://assets.tcgdex.net/univ/ecard/bog/symbol",
        "serieId": "ecard",
        "official": 9
    },
    {
        "id": "ecard2",
        "name": "Aquapolis",
        "releaseDate": "2003-01-15",
        "logo": "https://assets.tcgdex.net/en/ecard/ecard2/logo",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard2/symbol",
        "serieId": "ecard",
        "official": 147
    },
    {
        "id": "ecard3",
        "name": "Skyridge",
        "releaseDate": "2003-05-12",
        "logo": "https://assets.tcgdex.net/en/ecard/ecard3/logo",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard3/symbol",
        "serieId": "ecard",
        "official": 144
    },
    {
        "id": "ex1",
        "name": "Ruby & Sapphire",
        "releaseDate": "2003-07-01",
        "logo": "https://assets.tcgdex.net/en/ex/ex1/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex1/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "ex2",
        "name": "Sandstorm",
        "releaseDate": "2003-09-18",
        "logo": "https://assets.tcgdex.net/en/ex/ex2/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex2/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "np",
        "name": "Nintendo Black Star Promos",
        "releaseDate": "2003-10-01",
        "logo": "https://assets.tcgdex.net/en/pop/np/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/np/symbol",
        "serieId": "pop",
        "official": 40
    },
    {
        "id": "ex3",
        "name": "Dragon",
        "releaseDate": "2003-11-24",
        "logo": "https://assets.tcgdex.net/en/ex/ex3/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex3/symbol",
        "serieId": "ex",
        "official": 97
    },
    {
        "id": "ex4",
        "name": "Team Magma vs Team Aqua",
        "releaseDate": "2004-03-01",
        "logo": "https://assets.tcgdex.net/en/ex/ex4/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex4/symbol",
        "serieId": "ex",
        "official": 95
    },
    {
        "id": "ex5",
        "name": "Hidden Legends",
        "releaseDate": "2004-06-01",
        "logo": "https://assets.tcgdex.net/en/ex/ex5/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex5/symbol",
        "serieId": "ex",
        "official": 101
    },
    {
        "id": "ex5.5",
        "name": "Poké Card Creator Pack",
        "releaseDate": "2004-07-01",
        "serieId": "ex",
        "official": 5
    },
    {
        "id": "tk-ex-latio",
        "name": "EX trainer Kit (Latios)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latio/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "tk-ex-latia",
        "name": "EX trainer Kit (Latias)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latia/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "ex6",
        "name": "FireRed & LeafGreen",
        "releaseDate": "2004-09-01",
        "logo": "https://assets.tcgdex.net/en/ex/ex6/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex6/symbol",
        "serieId": "ex",
        "official": 112
    },
    {
        "id": "pop1",
        "name": "POP Series 1",
        "releaseDate": "2004-09-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop1/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop1/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex7",
        "name": "Team Rocket Returns",
        "releaseDate": "2004-11-01",
        "logo": "https://assets.tcgdex.net/en/ex/ex7/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex7/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "ex8",
        "name": "Deoxys",
        "releaseDate": "2005-02-01",
        "logo": "https://assets.tcgdex.net/en/ex/ex8/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex8/symbol",
        "serieId": "ex",
        "official": 107
    },
    {
        "id": "ex9",
        "name": "Emerald",
        "releaseDate": "2005-05-09",
        "logo": "https://assets.tcgdex.net/en/ex/ex9/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex9/symbol",
        "serieId": "ex",
        "official": 106
    },
    {
        "id": "pop2",
        "name": "POP Series 2",
        "releaseDate": "2005-08-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop2/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop2/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "exu",
        "name": "Unseen Forces Unown Collection",
        "releaseDate": "2005-08-22",
        "serieId": "ex",
        "official": 28
    },
    {
        "id": "ex10",
        "name": "Unseen Forces",
        "releaseDate": "2005-08-22",
        "logo": "https://assets.tcgdex.net/en/ex/ex10/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex10/symbol",
        "serieId": "ex",
        "official": 115
    },
    {
        "id": "ex11",
        "name": "Delta Species",
        "releaseDate": "2005-10-31",
        "logo": "https://assets.tcgdex.net/en/ex/ex11/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex11/symbol",
        "serieId": "ex",
        "official": 113
    },
    {
        "id": "ex12",
        "name": "Legend Maker",
        "releaseDate": "2006-02-13",
        "logo": "https://assets.tcgdex.net/en/ex/ex12/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex12/symbol",
        "serieId": "ex",
        "official": 92
    },
    {
        "id": "tk-ex-p",
        "name": "EX trainer Kit 2 (Plusle)",
        "releaseDate": "2006-03-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-p/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "tk-ex-m",
        "name": "EX trainer Kit 2 (Minun)",
        "releaseDate": "2006-03-01",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "pop3",
        "name": "POP Series 3",
        "releaseDate": "2006-04-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop3/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop3/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex13",
        "name": "Holon Phantoms",
        "releaseDate": "2006-05-03",
        "logo": "https://assets.tcgdex.net/en/ex/ex13/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex13/symbol",
        "serieId": "ex",
        "official": 110
    },
    {
        "id": "pop4",
        "name": "POP Series 4",
        "releaseDate": "2006-08-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop4/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop4/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex14",
        "name": "Crystal Guardians",
        "releaseDate": "2006-08-30",
        "logo": "https://assets.tcgdex.net/en/ex/ex14/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex14/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "ex15",
        "name": "Dragon Frontiers",
        "releaseDate": "2006-11-08",
        "logo": "https://assets.tcgdex.net/en/ex/ex15/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex15/symbol",
        "serieId": "ex",
        "official": 101
    },
    {
        "id": "ex16",
        "name": "Power Keepers",
        "releaseDate": "2007-02-17",
        "logo": "https://assets.tcgdex.net/en/ex/ex16/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex16/symbol",
        "serieId": "ex",
        "official": 108
    },
    {
        "id": "pop5",
        "name": "POP Series 5",
        "releaseDate": "2007-03-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop5/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop5/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "dp1",
        "name": "Diamond & Pearl",
        "releaseDate": "2007-05-01",
        "logo": "https://assets.tcgdex.net/en/dp/dp1/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp1/symbol",
        "serieId": "dp",
        "official": 130
    },
    {
        "id": "dpp",
        "name": "DP Black Star Promos",
        "releaseDate": "2007-05-01",
        "logo": "https://assets.tcgdex.net/en/dp/dpp/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dpp/symbol",
        "serieId": "dp",
        "official": 56
    },
    {
        "id": "dp2",
        "name": "Mysterious Treasures",
        "releaseDate": "2007-08-01",
        "logo": "https://assets.tcgdex.net/en/dp/dp2/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp2/symbol",
        "serieId": "dp",
        "official": 122
    },
    {
        "id": "pop6",
        "name": "POP Series 6",
        "releaseDate": "2007-09-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop6/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop6/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "tk-dp-l",
        "name": "DP trainer Kit (Lucario)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-l/symbol",
        "serieId": "tk",
        "official": 11
    },
    {
        "id": "tk-dp-m",
        "name": "DP trainer Kit (Manaphy)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-m/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "dp3",
        "name": "Secret Wonders",
        "releaseDate": "2007-11-01",
        "logo": "https://assets.tcgdex.net/en/dp/dp3/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp3/symbol",
        "serieId": "dp",
        "official": 132
    },
    {
        "id": "dp4",
        "name": "Great Encounters",
        "releaseDate": "2008-02-01",
        "logo": "https://assets.tcgdex.net/en/dp/dp4/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp4/symbol",
        "serieId": "dp",
        "official": 106
    },
    {
        "id": "pop7",
        "name": "POP Series 7",
        "releaseDate": "2008-03-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop7/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop7/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "dp5",
        "name": "Majestic Dawn",
        "releaseDate": "2008-05-01",
        "logo": "https://assets.tcgdex.net/en/dp/dp5/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp5/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "dp6",
        "name": "Legends Awakened",
        "releaseDate": "2008-08-01",
        "logo": "https://assets.tcgdex.net/en/dp/dp6/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp6/symbol",
        "serieId": "dp",
        "official": 146
    },
    {
        "id": "pop8",
        "name": "POP Series 8",
        "releaseDate": "2008-09-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop8/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop8/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "dp7",
        "name": "Stormfront",
        "releaseDate": "2008-11-01",
        "logo": "https://assets.tcgdex.net/en/dp/dp7/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp7/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "pl1",
        "name": "Platinum",
        "releaseDate": "2009-02-11",
        "logo": "https://assets.tcgdex.net/en/pl/pl1/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl1/symbol",
        "serieId": "pl",
        "official": 127
    },
    {
        "id": "pop9",
        "name": "POP Series 9",
        "releaseDate": "2009-03-01",
        "logo": "https://assets.tcgdex.net/en/pop/pop9/logo",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop9/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "pl2",
        "name": "Rising Rivals",
        "releaseDate": "2009-05-16",
        "logo": "https://assets.tcgdex.net/en/pl/pl2/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl2/symbol",
        "serieId": "pl",
        "official": 111
    },
    {
        "id": "pl3",
        "name": "Supreme Victors",
        "releaseDate": "2009-08-19",
        "logo": "https://assets.tcgdex.net/en/pl/pl3/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl3/symbol",
        "serieId": "pl",
        "official": 147
    },
    {
        "id": "pl4",
        "name": "Arceus",
        "releaseDate": "2009-11-04",
        "logo": "https://assets.tcgdex.net/en/pl/pl4/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl4/symbol",
        "serieId": "pl",
        "official": 99
    },
    {
        "id": "ru1",
        "name": "Pokémon Rumble",
        "releaseDate": "2009-12-02",
        "logo": "https://assets.tcgdex.net/en/pl/ru1/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/ru1/symbol",
        "serieId": "pl",
        "official": 16
    },
    {
        "id": "hgss1",
        "name": "HeartGold SoulSilver",
        "releaseDate": "2010-02-10",
        "logo": "https://assets.tcgdex.net/en/hgss/hgss1/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss1/symbol",
        "serieId": "hgss",
        "official": 123
    },
    {
        "id": "hgssp",
        "name": "HGSS Black Star Promos",
        "releaseDate": "2010-02-11",
        "logo": "https://assets.tcgdex.net/en/hgss/hgssp/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgssp/symbol",
        "serieId": "hgss",
        "official": 25
    },
    {
        "id": "tk-hs-g",
        "name": "HS trainer Kit (Gyarados)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-g/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-hs-r",
        "name": "HS trainer Kit (Raichu)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "hgss2",
        "name": "Unleashed",
        "releaseDate": "2010-05-12",
        "logo": "https://assets.tcgdex.net/en/hgss/hgss2/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss2/symbol",
        "serieId": "hgss",
        "official": 95
    },
    {
        "id": "hgss3",
        "name": "Undaunted",
        "releaseDate": "2010-08-18",
        "logo": "https://assets.tcgdex.net/en/hgss/hgss3/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss3/symbol",
        "serieId": "hgss",
        "official": 90
    },
    {
        "id": "hgss4",
        "name": "Triumphant",
        "releaseDate": "2010-11-03",
        "logo": "https://assets.tcgdex.net/en/hgss/hgss4/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss4/symbol",
        "serieId": "hgss",
        "official": 102
    },
    {
        "id": "col1",
        "name": "Call of Legends",
        "releaseDate": "2011-02-09",
        "logo": "https://assets.tcgdex.net/en/col/col1/logo",
        "symbol": "https://assets.tcgdex.net/univ/col/col1/symbol",
        "serieId": "col",
        "official": 95
    },
    {
        "id": "bw1",
        "name": "Black & White",
        "releaseDate": "2011-04-25",
        "logo": "https://assets.tcgdex.net/en/bw/bw1/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw1/symbol",
        "serieId": "bw",
        "official": 114
    },
    {
        "id": "bwp",
        "name": "BW Black Star Promos",
        "releaseDate": "2011-04-26",
        "logo": "https://assets.tcgdex.net/en/bw/bwp/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bwp/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "2011bw",
        "name": "Macdonald's Collection 2011",
        "releaseDate": "2011-06-17",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "bw2",
        "name": "Emerging Powers",
        "releaseDate": "2011-08-31",
        "logo": "https://assets.tcgdex.net/en/bw/bw2/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw2/symbol",
        "serieId": "bw",
        "official": 98
    },
    {
        "id": "tk-bw-z",
        "name": "BW trainer Kit (Zoroark)",
        "releaseDate": "2011-09-01",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-bw-e",
        "name": "BW trainer Kit (Excadrill)",
        "releaseDate": "2011-09-01",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "bw3",
        "name": "Noble Victories",
        "releaseDate": "2011-11-16",
        "logo": "https://assets.tcgdex.net/en/bw/bw3/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw3/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw4",
        "name": "Next Destinies",
        "releaseDate": "2012-02-08",
        "logo": "https://assets.tcgdex.net/en/bw/bw4/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw4/symbol",
        "serieId": "bw",
        "official": 99
    },
    {
        "id": "bw5",
        "name": "Dark Explorers",
        "releaseDate": "2012-05-09",
        "logo": "https://assets.tcgdex.net/en/bw/bw5/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw5/symbol",
        "serieId": "bw",
        "official": 108
    },
    {
        "id": "2012bw",
        "name": "Macdonald's Collection 2012",
        "releaseDate": "2012-06-15",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "bw6",
        "name": "Dragons Exalted",
        "releaseDate": "2012-08-15",
        "logo": "https://assets.tcgdex.net/en/bw/bw6/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw6/symbol",
        "serieId": "bw",
        "official": 124
    },
    {
        "id": "dv1",
        "name": "Dragon Vault",
        "releaseDate": "2012-10-05",
        "logo": "https://assets.tcgdex.net/en/bw/dv1/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/dv1/symbol",
        "serieId": "bw",
        "official": 20
    },
    {
        "id": "bw7",
        "name": "Boundaries Crossed",
        "releaseDate": "2012-11-07",
        "logo": "https://assets.tcgdex.net/en/bw/bw7/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw7/symbol",
        "serieId": "bw",
        "official": 149
    },
    {
        "id": "bw8",
        "name": "Plasma Storm",
        "releaseDate": "2013-02-06",
        "logo": "https://assets.tcgdex.net/en/bw/bw8/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw8/symbol",
        "serieId": "bw",
        "official": 135
    },
    {
        "id": "bw9",
        "name": "Plasma Freeze",
        "releaseDate": "2013-05-08",
        "logo": "https://assets.tcgdex.net/en/bw/bw9/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw9/symbol",
        "serieId": "bw",
        "official": 116
    },
    {
        "id": "bw10",
        "name": "Plasma Blast",
        "releaseDate": "2013-08-14",
        "logo": "https://assets.tcgdex.net/en/bw/bw10/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw10/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "xyp",
        "name": "XY Black Star Promos",
        "releaseDate": "2013-10-12",
        "logo": "https://assets.tcgdex.net/en/xy/xyp/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xyp/symbol",
        "serieId": "xy",
        "official": 211
    },
    {
        "id": "rc",
        "name": "Radiant Collection",
        "releaseDate": "2013-11-06",
        "serieId": "bw",
        "official": 25
    },
    {
        "id": "bw11",
        "name": "Legendary Treasures",
        "releaseDate": "2013-11-06",
        "logo": "https://assets.tcgdex.net/en/bw/bw11/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw11/symbol",
        "serieId": "bw",
        "official": 113
    },
    {
        "id": "xy0",
        "name": "Kalos Starter Set",
        "releaseDate": "2013-11-08",
        "logo": "https://assets.tcgdex.net/en/xy/xy0/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy0/symbol",
        "serieId": "xy",
        "official": 39
    },
    {
        "id": "xy1",
        "name": "XY",
        "releaseDate": "2014-02-05",
        "logo": "https://assets.tcgdex.net/en/xy/xy1/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy1/symbol",
        "serieId": "xy",
        "official": 146
    },
    {
        "id": "xya",
        "name": "Yello A Alternate",
        "releaseDate": "2014-02-05",
        "serieId": "xy",
        "official": 6
    },
    {
        "id": "tk-xy-sy",
        "name": "XY trainer Kit (Sylveon)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-sy/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-n",
        "name": "XY trainer Kit (Noivern)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-n/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy2",
        "name": "Flashfire",
        "releaseDate": "2014-05-07",
        "logo": "https://assets.tcgdex.net/en/xy/xy2/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy2/symbol",
        "serieId": "xy",
        "official": 106
    },
    {
        "id": "2014xy",
        "name": "Macdonald's Collection 2014",
        "releaseDate": "2014-05-23",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy3",
        "name": "Furious Fists",
        "releaseDate": "2014-08-13",
        "logo": "https://assets.tcgdex.net/en/xy/xy3/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy3/symbol",
        "serieId": "xy",
        "official": 111
    },
    {
        "id": "tk-xy-b",
        "name": "XY trainer Kit (Bisharp)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-b/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-w",
        "name": "XY trainer Kit (Wigglytuff)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-w/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy4",
        "name": "Phantom Forces",
        "releaseDate": "2014-11-05",
        "logo": "https://assets.tcgdex.net/en/xy/xy4/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy4/symbol",
        "serieId": "xy",
        "official": 119
    },
    {
        "id": "xy5",
        "name": "Primal Clash",
        "releaseDate": "2015-02-04",
        "logo": "https://assets.tcgdex.net/en/xy/xy5/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy5/symbol",
        "serieId": "xy",
        "official": 160
    },
    {
        "id": "dc1",
        "name": "Double Crisis",
        "releaseDate": "2015-03-25",
        "logo": "https://assets.tcgdex.net/en/xy/dc1/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/dc1/symbol",
        "serieId": "xy",
        "official": 34
    },
    {
        "id": "tk-xy-latia",
        "name": "XY trainer Kit (Latias)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latia/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-latio",
        "name": "XY trainer Kit (Latios)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latio/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy6",
        "name": "Roaring Skies",
        "releaseDate": "2015-05-06",
        "logo": "https://assets.tcgdex.net/en/xy/xy6/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy6/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "xy7",
        "name": "Ancient Origins",
        "releaseDate": "2015-08-12",
        "logo": "https://assets.tcgdex.net/en/xy/xy7/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy7/symbol",
        "serieId": "xy",
        "official": 98
    },
    {
        "id": "xy8",
        "name": "BREAKthrough",
        "releaseDate": "2015-11-04",
        "logo": "https://assets.tcgdex.net/en/xy/xy8/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy8/symbol",
        "serieId": "xy",
        "official": 162
    },
    {
        "id": "2015xy",
        "name": "Macdonald's Collection 2015",
        "releaseDate": "2015-11-27",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy9",
        "name": "BREAKpoint",
        "releaseDate": "2016-02-03",
        "logo": "https://assets.tcgdex.net/en/xy/xy9/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy9/symbol",
        "serieId": "xy",
        "official": 122
    },
    {
        "id": "g1",
        "name": "Generations",
        "releaseDate": "2016-02-22",
        "logo": "https://assets.tcgdex.net/en/xy/g1/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/g1/symbol",
        "serieId": "xy",
        "official": 83
    },
    {
        "id": "tk-xy-p",
        "name": "XY trainer Kit (Pikachu Libre)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-p/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-su",
        "name": "XY trainer Kit (Suicune)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-su/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy10",
        "name": "Fates Collide",
        "releaseDate": "2016-05-02",
        "logo": "https://assets.tcgdex.net/en/xy/xy10/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy10/symbol",
        "serieId": "xy",
        "official": 124
    },
    {
        "id": "xy11",
        "name": "Steam Siege",
        "releaseDate": "2016-08-03",
        "logo": "https://assets.tcgdex.net/en/xy/xy11/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy11/symbol",
        "serieId": "xy",
        "official": 114
    },
    {
        "id": "2016xy",
        "name": "Macdonald's Collection 2016",
        "releaseDate": "2016-08-20",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy12",
        "name": "Evolutions",
        "releaseDate": "2016-11-02",
        "logo": "https://assets.tcgdex.net/en/xy/xy12/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy12/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "sm1",
        "name": "Sun & Moon",
        "releaseDate": "2017-02-03",
        "logo": "https://assets.tcgdex.net/en/sm/sm1/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm1/symbol",
        "serieId": "sm",
        "official": 149
    },
    {
        "id": "smp",
        "name": "SM Black Star Promos",
        "releaseDate": "2017-02-03",
        "logo": "https://assets.tcgdex.net/en/sm/smp/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/smp/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "tk-sm-l",
        "name": "SM trainer Kit (Lycanroc)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-l/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-sm-r",
        "name": "SM trainer Kit (Alolan Raichu)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "sm2",
        "name": "Guardians Rising",
        "releaseDate": "2017-05-05",
        "logo": "https://assets.tcgdex.net/en/sm/sm2/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm2/symbol",
        "serieId": "sm",
        "official": 145
    },
    {
        "id": "2017sm",
        "name": "Macdonald's Collection 2017",
        "releaseDate": "2017-08-03",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "sm3",
        "name": "Burning Shadows",
        "releaseDate": "2017-08-04",
        "logo": "https://assets.tcgdex.net/en/sm/sm3/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm3/symbol",
        "serieId": "sm",
        "official": 147
    },
    {
        "id": "sm3.5",
        "name": "Shining Legends",
        "releaseDate": "2017-10-06",
        "serieId": "sm",
        "official": 73
    },
    {
        "id": "sm4",
        "name": "Crimson Invasion",
        "releaseDate": "2017-11-03",
        "logo": "https://assets.tcgdex.net/en/sm/sm4/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm4/symbol",
        "serieId": "sm",
        "official": 111
    },
    {
        "id": "sm5",
        "name": "Ultra Prism",
        "releaseDate": "2018-02-02",
        "logo": "https://assets.tcgdex.net/en/sm/sm5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm5/symbol",
        "serieId": "sm",
        "official": 156
    },
    {
        "id": "sm6",
        "name": "Forbidden Light",
        "releaseDate": "2018-05-04",
        "logo": "https://assets.tcgdex.net/en/sm/sm6/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm6/symbol",
        "serieId": "sm",
        "official": 131
    },
    {
        "id": "sm7",
        "name": "Celestial Storm",
        "releaseDate": "2018-08-03",
        "logo": "https://assets.tcgdex.net/en/sm/sm7/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm7/symbol",
        "serieId": "sm",
        "official": 168
    },
    {
        "id": "sm7.5",
        "name": "Dragon Majesty",
        "releaseDate": "2018-09-07",
        "serieId": "sm",
        "official": 70
    },
    {
        "id": "2018sm",
        "name": "Macdonald's Collection 2018",
        "releaseDate": "2018-10-19",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "sm8",
        "name": "Lost Thunder",
        "releaseDate": "2018-11-02",
        "logo": "https://assets.tcgdex.net/en/sm/sm8/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm8/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm9",
        "name": "Team Up",
        "releaseDate": "2019-01-31",
        "logo": "https://assets.tcgdex.net/en/sm/sm9/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm9/symbol",
        "serieId": "sm",
        "official": 181
    },
    {
        "id": "det1",
        "name": "Detective Pikachu",
        "releaseDate": "2019-03-29",
        "logo": "https://assets.tcgdex.net/en/sm/det1/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/det1/symbol",
        "serieId": "sm",
        "official": 18
    },
    {
        "id": "sm10",
        "name": "Unbroken Bonds",
        "releaseDate": "2019-05-03",
        "logo": "https://assets.tcgdex.net/en/sm/sm10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm10/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm11",
        "name": "Unified Minds",
        "releaseDate": "2019-08-02",
        "logo": "https://assets.tcgdex.net/en/sm/sm11/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm11/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "sm115",
        "name": "Hidden Fates",
        "releaseDate": "2019-08-23",
        "logo": "https://assets.tcgdex.net/en/sm/sm115/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm115/symbol",
        "serieId": "sm",
        "official": 68
    },
    {
        "id": "sma",
        "name": "Yellow A Alternate",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sma/symbol",
        "serieId": "sm",
        "official": 94
    },
    {
        "id": "2019sm",
        "name": "Macdonald's Collection 2019",
        "releaseDate": "2019-10-15",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "sm12",
        "name": "Cosmic Eclipse",
        "releaseDate": "2019-11-01",
        "logo": "https://assets.tcgdex.net/en/sm/sm12/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm12/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "swshp",
        "name": "SWSH Black Star Promos",
        "releaseDate": "2019-11-15",
        "logo": "https://assets.tcgdex.net/en/swsh/swshp/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swshp/symbol",
        "serieId": "swsh",
        "official": 107
    },
    {
        "id": "swsh1",
        "name": "Sword & Shield",
        "releaseDate": "2020-02-07",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh1/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh1/symbol",
        "serieId": "swsh",
        "official": 202
    },
    {
        "id": "swsh2",
        "name": "Rebel Clash",
        "releaseDate": "2020-05-01",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh2/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh2/symbol",
        "serieId": "swsh",
        "official": 192
    },
    {
        "id": "swsh3",
        "name": "Darkness Ablaze",
        "releaseDate": "2020-08-14",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh3/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "fut2020",
        "name": "Pokémon Futsal 2020",
        "releaseDate": "2020-09-11",
        "logo": "https://assets.tcgdex.net/en/swsh/fut2020/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/fut2020/symbol",
        "serieId": "swsh",
        "official": 5
    },
    {
        "id": "swsh3.5",
        "name": "Champion's Path",
        "releaseDate": "2020-09-25",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh3.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3.5/symbol",
        "serieId": "swsh",
        "official": 70
    },
    {
        "id": "swsh4",
        "name": "Vivid Voltage",
        "releaseDate": "2020-11-13",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh4/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4/symbol",
        "serieId": "swsh",
        "official": 185
    },
    {
        "id": "2021swsh",
        "name": "Macdonald's Collection 2021",
        "releaseDate": "2021-02-09",
        "symbol": "https://assets.tcgdex.net/univ/mc/2021swsh/symbol",
        "serieId": "mc",
        "official": 25
    },
    {
        "id": "swsh4.5",
        "name": "Shining Fates",
        "releaseDate": "2021-02-19",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh4.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4.5/symbol",
        "serieId": "swsh",
        "official": 72
    },
    {
        "id": "swsh5",
        "name": "Battle Styles",
        "releaseDate": "2021-03-19",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh5/symbol",
        "serieId": "swsh",
        "official": 163
    },
    {
        "id": "swsh6",
        "name": "Chilling Reign",
        "releaseDate": "2021-06-18",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh6/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh6/symbol",
        "serieId": "swsh",
        "official": 198
    },
    {
        "id": "swsh7",
        "name": "Evolving Skies",
        "releaseDate": "2021-08-27",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh7/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh7/symbol",
        "serieId": "swsh",
        "official": 203
    },
    {
        "id": "cel25",
        "name": "Celebrations",
        "releaseDate": "2021-10-08",
        "logo": "https://assets.tcgdex.net/en/swsh/cel25/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/cel25/symbol",
        "serieId": "swsh",
        "official": 25
    },
    {
        "id": "swsh8",
        "name": "Fusion Strike",
        "releaseDate": "2021-11-12",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh8/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh8/symbol",
        "serieId": "swsh",
        "official": 264
    },
    {
        "id": "swsh9",
        "name": "Brilliant Stars",
        "releaseDate": "2022-02-25",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh9/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh9/symbol",
        "serieId": "swsh",
        "official": 172
    },
    {
        "id": "swsh10",
        "name": "Astral Radiance",
        "releaseDate": "2022-05-27",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh10/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh10.5",
        "name": "Pokémon GO",
        "releaseDate": "2022-07-01",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh10.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10.5/symbol",
        "serieId": "swsh",
        "official": 78
    },
    {
        "id": "swsh11",
        "name": "Lost Origin",
        "releaseDate": "2022-09-09",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh11/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh11/symbol",
        "serieId": "swsh",
        "official": 196
    },
    {
        "id": "swsh12",
        "name": "Silver Tempest",
        "releaseDate": "2022-11-11",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh12/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12/symbol",
        "serieId": "swsh",
        "official": 195
    },
    {
        "id": "swsh12.5",
        "name": "Crown Zenith",
        "releaseDate": "2023-01-20",
        "logo": "https://assets.tcgdex.net/en/swsh/swsh12.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12.5/symbol",
        "serieId": "swsh",
        "official": 159
    },
    {
        "id": "svp",
        "name": "SVP Black Star Promos",
        "releaseDate": "2023-03-31",
        "serieId": "sv",
        "official": 0
    },
    {
        "id": "sv01",
        "name": "Scarlet & Violet",
        "releaseDate": "2023-03-31",
        "logo": "https://assets.tcgdex.net/en/sv/sv01/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv01/symbol",
        "serieId": "sv",
        "official": 198
    },
    {
        "id": "sv02",
        "name": "Paldea Evolved",
        "releaseDate": "2023-06-09",
        "logo": "https://assets.tcgdex.net/en/sv/sv02/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv02/symbol",
        "serieId": "sv",
        "official": 193
    },
    {
        "id": "sv03",
        "name": "Obsidian Flames",
        "releaseDate": "2023-08-11",
        "logo": "https://assets.tcgdex.net/en/sv/sv03/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03/symbol",
        "serieId": "sv",
        "official": 197
    },
    {
        "id": "sv03.5",
        "name": "151",
        "releaseDate": "2023-09-22",
        "logo": "https://assets.tcgdex.net/en/sv/sv03.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03.5/symbol",
        "serieId": "sv",
        "official": 165
    },
    {
        "id": "sv04",
        "name": "Paradox Rift",
        "releaseDate": "2023-11-03",
        "logo": "https://assets.tcgdex.net/en/sv/sv04/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv04.5",
        "name": "Paldean Fates",
        "releaseDate": "2024-01-26",
        "logo": "https://assets.tcgdex.net/en/sv/sv04.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04.5/symbol",
        "serieId": "sv",
        "official": 91
    },
    {
        "id": "sv05",
        "name": "Temporal Forces",
        "releaseDate": "2024-03-22",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv05/symbol",
        "serieId": "sv",
        "official": 162
    },
    {
        "id": "sv06",
        "name": "Twilight Masquerade",
        "releaseDate": "2024-05-24",
        "logo": "https://assets.tcgdex.net/en/sv/sv06/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06/symbol",
        "serieId": "sv",
        "official": 167
    },
    {
        "id": "sv06.5",
        "name": "Shrouded Fable",
        "releaseDate": "2024-08-02",
        "logo": "https://assets.tcgdex.net/en/sv/sv06.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06.5/symbol",
        "serieId": "sv",
        "official": 64
    },
    {
        "id": "sv07",
        "name": "Stellar Crown",
        "releaseDate": "2024-09-13",
        "logo": "https://assets.tcgdex.net/en/sv/sv07/logo",
        "serieId": "sv",
        "official": 142
    },
    {
        "id": "A1",
        "name": "Genetic Apex",
        "releaseDate": "2024-10-30",
        "logo": "https://assets.tcgdex.net/en/tcgp/A1/logo",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A1/symbol",
        "serieId": "tcgp",
        "official": 226
    },
    {
        "id": "P-A",
        "name": "Promos-A",
        "releaseDate": "2024-10-30",
        "logo": "https://assets.tcgdex.net/en/tcgp/P-A/logo",
        "serieId": "tcgp",
        "official": 0
    },
    {
        "id": "sv08",
        "name": "Surging Sparks",
        "releaseDate": "2024-11-08",
        "logo": "https://assets.tcgdex.net/en/sv/sv08/logo",
        "serieId": "sv",
        "official": 191
    },
    {
        "id": "A1a",
        "name": "Mythical Island",
        "releaseDate": "2024-12-17",
        "logo": "https://assets.tcgdex.net/en/tcgp/A1a/logo",
        "serieId": "tcgp",
        "official": 68
    },
    {
        "id": "sv08.5",
        "name": "Prismatic Evolutions",
        "releaseDate": "2025-01-17",
        "logo": "https://assets.tcgdex.net/en/sv/sv08.5/logo",
        "serieId": "sv",
        "official": 131
    },
    {
        "id": "A2",
        "name": "Space-Time Smackdown",
        "releaseDate": "2025-01-30",
        "logo": "https://assets.tcgdex.net/en/tcgp/A2/logo",
        "serieId": "tcgp",
        "official": 140
    },
    {
        "id": "A2a",
        "name": "Triumphant Light",
        "releaseDate": "2025-02-28",
        "logo": "https://assets.tcgdex.net/en/tcgp/A2a/logo",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2a/symbol",
        "serieId": "tcgp",
        "official": 75
    },
    {
        "id": "A2b",
        "name": "Shining Revelry",
        "releaseDate": "2025-03-27",
        "logo": "https://assets.tcgdex.net/en/tcgp/A2b/logo",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2b/symbol",
        "serieId": "tcgp",
        "official": 72
    },
    {
        "id": "sv09",
        "name": "Journey Together",
        "releaseDate": "2025-03-28",
        "logo": "https://assets.tcgdex.net/en/sv/sv09/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv09/symbol",
        "serieId": "sv",
        "official": 159
    },
    {
        "id": "A3",
        "name": "Celestial Guardians",
        "releaseDate": "2025-04-30",
        "logo": "https://assets.tcgdex.net/en/tcgp/A3/logo",
        "serieId": "tcgp",
        "official": 155
    },
    {
        "id": "A3a",
        "name": "Extradimensional Crisis",
        "releaseDate": "2025-05-29",
        "serieId": "tcgp",
        "official": 69
    },
    {
        "id": "sv10",
        "name": "Destined Rivals",
        "releaseDate": "2025-05-30",
        "logo": "https://assets.tcgdex.net/en/sv/sv10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "A3b",
        "name": "Eevee Grove",
        "releaseDate": "2025-06-26",
        "serieId": "tcgp",
        "official": 69
    },
    {
        "id": "sv10.5b",
        "name": "Black Bolt",
        "releaseDate": "2025-07-17",
        "logo": "https://assets.tcgdex.net/en/sv/sv10.5b/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5b/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "sv10.5w",
        "name": "White Flare",
        "releaseDate": "2025-07-17",
        "logo": "https://assets.tcgdex.net/en/sv/sv10.5w/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5w/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "A4",
        "name": "Wisdom of Sea and Sky",
        "releaseDate": "2025-07-30",
        "logo": "https://assets.tcgdex.net/en/tcgp/A4/logo",
        "serieId": "tcgp",
        "official": 161
    },
    {
        "id": "A4a",
        "name": "Secluded Springs",
        "releaseDate": "2025-08-28",
        "logo": "https://assets.tcgdex.net/en/tcgp/A4a/logo",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A4a/symbol",
        "serieId": "tcgp",
        "official": 71
    },
    {
        "id": "me01",
        "name": "Mega Evolution",
        "releaseDate": "2025-09-26",
        "logo": "https://assets.tcgdex.net/en/me/me01/logo",
        "symbol": "https://assets.tcgdex.net/univ/me/me01/symbol",
        "serieId": "me",
        "official": 132
    },
    {
        "id": "B1",
        "name": "Mega Rising",
        "releaseDate": "2025-10-30",
        "logo": "https://assets.tcgdex.net/en/tcgp/B1/logo",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/B1/symbol",
        "serieId": "tcgp",
        "official": 226
    }
]
""",
    "fr" to """
[
    {
        "id": "base1",
        "name": "Set de Base",
        "releaseDate": "1999-01-09",
        "serieId": "base",
        "official": 102
    },
    {
        "id": "base2",
        "name": "Jungle",
        "releaseDate": "1999-06-16",
        "logo": "https://assets.tcgdex.net/fr/base/base2/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/base2/symbol",
        "serieId": "base",
        "official": 64
    },
    {
        "id": "basep",
        "name": "Wizards Black Star Promos",
        "releaseDate": "1999-07-01",
        "symbol": "https://assets.tcgdex.net/univ/base/basep/symbol",
        "serieId": "base",
        "official": 53
    },
    {
        "id": "wp",
        "name": "W Promotional",
        "releaseDate": "1999-09-01",
        "serieId": "base",
        "official": 7
    },
    {
        "id": "base3",
        "name": "Fossile",
        "releaseDate": "1999-10-10",
        "logo": "https://assets.tcgdex.net/fr/base/base3/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/base3/symbol",
        "serieId": "base",
        "official": 62
    },
    {
        "id": "jumbo",
        "name": "Cartes Jumbo",
        "releaseDate": "2000-02-01",
        "serieId": "misc",
        "official": 160
    },
    {
        "id": "base5",
        "name": "Team Rocket",
        "releaseDate": "2000-04-24",
        "logo": "https://assets.tcgdex.net/fr/base/base5/logo",
        "symbol": "https://assets.tcgdex.net/univ/base/base5/symbol",
        "serieId": "base",
        "official": 82
    },
    {
        "id": "neo1",
        "name": "Neo Genesis",
        "releaseDate": "2000-12-16",
        "logo": "https://assets.tcgdex.net/fr/neo/neo1/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo1/symbol",
        "serieId": "neo",
        "official": 111
    },
    {
        "id": "neo2",
        "name": "Neo Discovery",
        "releaseDate": "2001-06-01",
        "logo": "https://assets.tcgdex.net/fr/neo/neo2/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo2/symbol",
        "serieId": "neo",
        "official": 75
    },
    {
        "id": "neo3",
        "name": "Neo Revelation",
        "releaseDate": "2001-09-21",
        "logo": "https://assets.tcgdex.net/fr/neo/neo3/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo3/symbol",
        "serieId": "neo",
        "official": 64
    },
    {
        "id": "neo4",
        "name": "Neo Destiny",
        "releaseDate": "2002-02-28",
        "logo": "https://assets.tcgdex.net/fr/neo/neo4/logo",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo4/symbol",
        "serieId": "neo",
        "official": 105
    },
    {
        "id": "ecard1",
        "name": "Expedition",
        "releaseDate": "2002-09-15",
        "logo": "https://assets.tcgdex.net/fr/ecard/ecard1/logo",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard1/symbol",
        "serieId": "ecard",
        "official": 165
    },
    {
        "id": "ecard2",
        "name": "Aquapolis",
        "releaseDate": "2003-01-15",
        "logo": "https://assets.tcgdex.net/fr/ecard/ecard2/logo",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard2/symbol",
        "serieId": "ecard",
        "official": 147
    },
    {
        "id": "ex1",
        "name": "EX Rubis & Saphir",
        "releaseDate": "2003-07-01",
        "logo": "https://assets.tcgdex.net/fr/ex/ex1/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex1/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "ex2",
        "name": "EX Tempête de sable",
        "releaseDate": "2003-09-18",
        "logo": "https://assets.tcgdex.net/fr/ex/ex2/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex2/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "np",
        "name": "Promo Nintendo",
        "releaseDate": "2003-10-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/np/symbol",
        "serieId": "pop",
        "official": 40
    },
    {
        "id": "ex3",
        "name": "EX Dragon",
        "releaseDate": "2003-11-24",
        "logo": "https://assets.tcgdex.net/fr/ex/ex3/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex3/symbol",
        "serieId": "ex",
        "official": 97
    },
    {
        "id": "ex4",
        "name": "EX Team Magma vs Team Aqua",
        "releaseDate": "2004-03-01",
        "logo": "https://assets.tcgdex.net/fr/ex/ex4/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex4/symbol",
        "serieId": "ex",
        "official": 95
    },
    {
        "id": "ex5",
        "name": "EX Légendes Oubliées",
        "releaseDate": "2004-06-01",
        "logo": "https://assets.tcgdex.net/fr/ex/ex5/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex5/symbol",
        "serieId": "ex",
        "official": 101
    },
    {
        "id": "tk-ex-latio",
        "name": "EX Kit dresseur (Latios)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latio/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "tk-ex-latia",
        "name": "EX Kit dresseur (Latias)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latia/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "ex6",
        "name": "EX Rouge Feu & Vert Feuille",
        "releaseDate": "2004-09-01",
        "logo": "https://assets.tcgdex.net/fr/ex/ex6/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex6/symbol",
        "serieId": "ex",
        "official": 112
    },
    {
        "id": "pop1",
        "name": "POP Série 1",
        "releaseDate": "2004-09-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop1/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex8",
        "name": "EX Deoxys",
        "releaseDate": "2005-02-01",
        "logo": "https://assets.tcgdex.net/fr/ex/ex8/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex8/symbol",
        "serieId": "ex",
        "official": 107
    },
    {
        "id": "ex9",
        "name": "EX Émeraude",
        "releaseDate": "2005-05-09",
        "logo": "https://assets.tcgdex.net/fr/ex/ex9/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex9/symbol",
        "serieId": "ex",
        "official": 106
    },
    {
        "id": "pop2",
        "name": "POP Série 2",
        "releaseDate": "2005-08-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop2/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "exu",
        "name": "EX Forces Cachées Collection Zarbi",
        "releaseDate": "2005-08-22",
        "serieId": "ex",
        "official": 28
    },
    {
        "id": "ex10",
        "name": "EX Forces Cachées",
        "releaseDate": "2005-08-22",
        "logo": "https://assets.tcgdex.net/fr/ex/ex10/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex10/symbol",
        "serieId": "ex",
        "official": 115
    },
    {
        "id": "ex11",
        "name": "EX Espèces Delta",
        "releaseDate": "2005-10-31",
        "logo": "https://assets.tcgdex.net/fr/ex/ex11/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex11/symbol",
        "serieId": "ex",
        "official": 113
    },
    {
        "id": "ex12",
        "name": "EX Créateurs de légendes",
        "releaseDate": "2006-02-13",
        "logo": "https://assets.tcgdex.net/fr/ex/ex12/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex12/symbol",
        "serieId": "ex",
        "official": 92
    },
    {
        "id": "tk-ex-p",
        "name": "EX Kit dresseur (Positi)",
        "releaseDate": "2006-03-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-p/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "tk-ex-m",
        "name": "EX Kit dresseur (Négapi)",
        "releaseDate": "2006-03-01",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "pop3",
        "name": "POP Série 3",
        "releaseDate": "2006-04-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop3/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex13",
        "name": "EX Fantômes Holon",
        "releaseDate": "2006-05-03",
        "logo": "https://assets.tcgdex.net/fr/ex/ex13/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex13/symbol",
        "serieId": "ex",
        "official": 110
    },
    {
        "id": "pop4",
        "name": "POP Série 4",
        "releaseDate": "2006-08-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop4/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex14",
        "name": "EX Gardiens de Cristal",
        "releaseDate": "2006-08-30",
        "logo": "https://assets.tcgdex.net/fr/ex/ex14/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex14/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "ex15",
        "name": "EX Île des Dragons",
        "releaseDate": "2006-11-08",
        "logo": "https://assets.tcgdex.net/fr/ex/ex15/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex15/symbol",
        "serieId": "ex",
        "official": 101
    },
    {
        "id": "ex16",
        "name": "EX Gardiens du Pouvoir",
        "releaseDate": "2007-02-17",
        "logo": "https://assets.tcgdex.net/fr/ex/ex16/logo",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex16/symbol",
        "serieId": "ex",
        "official": 108
    },
    {
        "id": "dp1",
        "name": "Diamant & Perle",
        "releaseDate": "2007-05-01",
        "logo": "https://assets.tcgdex.net/fr/dp/dp1/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp1/symbol",
        "serieId": "dp",
        "official": 130
    },
    {
        "id": "dpp",
        "name": "Promo DP",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dpp/symbol",
        "serieId": "dp",
        "official": 56
    },
    {
        "id": "dp2",
        "name": "Trésors Mystérieux",
        "releaseDate": "2007-08-01",
        "logo": "https://assets.tcgdex.net/fr/dp/dp2/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp2/symbol",
        "serieId": "dp",
        "official": 122
    },
    {
        "id": "tk-dp-l",
        "name": "DP Kit dresseur (Lucario)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-l/symbol",
        "serieId": "tk",
        "official": 11
    },
    {
        "id": "tk-dp-m",
        "name": "DP Kit dresseur (Manaphy)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-m/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "dp3",
        "name": "Merveilles Secrètes",
        "releaseDate": "2007-11-01",
        "logo": "https://assets.tcgdex.net/fr/dp/dp3/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp3/symbol",
        "serieId": "dp",
        "official": 132
    },
    {
        "id": "dp4",
        "name": "Duels au Sommets",
        "releaseDate": "2008-02-01",
        "logo": "https://assets.tcgdex.net/fr/dp/dp4/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp4/symbol",
        "serieId": "dp",
        "official": 106
    },
    {
        "id": "pop7",
        "name": "POP Série 7",
        "releaseDate": "2008-03-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop7/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "dp5",
        "name": "Aube Majestueuse",
        "releaseDate": "2008-05-01",
        "logo": "https://assets.tcgdex.net/fr/dp/dp5/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp5/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "dp6",
        "name": "Éveil des Légendes",
        "releaseDate": "2008-08-01",
        "logo": "https://assets.tcgdex.net/fr/dp/dp6/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp6/symbol",
        "serieId": "dp",
        "official": 146
    },
    {
        "id": "dp7",
        "name": "Tempête",
        "releaseDate": "2008-11-01",
        "logo": "https://assets.tcgdex.net/fr/dp/dp7/logo",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp7/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "pl1",
        "name": "Platine",
        "releaseDate": "2009-02-11",
        "logo": "https://assets.tcgdex.net/fr/pl/pl1/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl1/symbol",
        "serieId": "pl",
        "official": 127
    },
    {
        "id": "pop9",
        "name": "POP Série 9",
        "releaseDate": "2009-03-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop9/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "pl2",
        "name": "Rivaux Émergeants",
        "releaseDate": "2009-05-16",
        "logo": "https://assets.tcgdex.net/fr/pl/pl2/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl2/symbol",
        "serieId": "pl",
        "official": 111
    },
    {
        "id": "pl3",
        "name": "Vainqueurs Suprêmes",
        "releaseDate": "2009-08-19",
        "logo": "https://assets.tcgdex.net/fr/pl/pl3/logo",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl3/symbol",
        "serieId": "pl",
        "official": 147
    },
    {
        "id": "hgss1",
        "name": "HeartGold SoulSilver",
        "releaseDate": "2010-02-10",
        "logo": "https://assets.tcgdex.net/fr/hgss/hgss1/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss1/symbol",
        "serieId": "hgss",
        "official": 123
    },
    {
        "id": "hgssp",
        "name": "Promo HGSS",
        "releaseDate": "2010-02-11",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgssp/symbol",
        "serieId": "hgss",
        "official": 25
    },
    {
        "id": "tk-hs-g",
        "name": "HS Kit du dresseur (Léviator)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-g/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-hs-r",
        "name": "HS Kit du dresseur (Raichu)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "hgss2",
        "name": "Déchaînement",
        "releaseDate": "2010-05-12",
        "logo": "https://assets.tcgdex.net/fr/hgss/hgss2/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss2/symbol",
        "serieId": "hgss",
        "official": 95
    },
    {
        "id": "hgss3",
        "name": "Indomptable",
        "releaseDate": "2010-08-18",
        "logo": "https://assets.tcgdex.net/fr/hgss/hgss3/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss3/symbol",
        "serieId": "hgss",
        "official": 90
    },
    {
        "id": "hgss4",
        "name": "Triomphant",
        "releaseDate": "2010-11-03",
        "logo": "https://assets.tcgdex.net/fr/hgss/hgss4/logo",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss4/symbol",
        "serieId": "hgss",
        "official": 102
    },
    {
        "id": "col1",
        "name": "L'appel des Légendes",
        "releaseDate": "2011-02-09",
        "logo": "https://assets.tcgdex.net/fr/col/col1/logo",
        "symbol": "https://assets.tcgdex.net/univ/col/col1/symbol",
        "serieId": "col",
        "official": 95
    },
    {
        "id": "bw1",
        "name": "Noir & Blanc",
        "releaseDate": "2011-04-25",
        "logo": "https://assets.tcgdex.net/fr/bw/bw1/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw1/symbol",
        "serieId": "bw",
        "official": 114
    },
    {
        "id": "bwp",
        "name": "Promo BW",
        "releaseDate": "2011-04-26",
        "symbol": "https://assets.tcgdex.net/univ/bw/bwp/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "2011bw",
        "name": "Promo McDonald's 2012",
        "releaseDate": "2011-06-17",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "bw2",
        "name": "Pouvoirs Émergents",
        "releaseDate": "2011-08-31",
        "logo": "https://assets.tcgdex.net/fr/bw/bw2/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw2/symbol",
        "serieId": "bw",
        "official": 98
    },
    {
        "id": "tk-bw-z",
        "name": "BW Kit du dresseur (Zoroark)",
        "releaseDate": "2011-09-01",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-bw-e",
        "name": "BW Kit du dresseur (Minitaupe)",
        "releaseDate": "2011-09-01",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "bw3",
        "name": "Nobles Victoires",
        "releaseDate": "2011-11-16",
        "logo": "https://assets.tcgdex.net/fr/bw/bw3/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw3/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw4",
        "name": "Destinées Futures",
        "releaseDate": "2012-02-08",
        "logo": "https://assets.tcgdex.net/fr/bw/bw4/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw4/symbol",
        "serieId": "bw",
        "official": 99
    },
    {
        "id": "bw5",
        "name": "Explorateurs Obscurs",
        "releaseDate": "2012-05-09",
        "logo": "https://assets.tcgdex.net/fr/bw/bw5/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw5/symbol",
        "serieId": "bw",
        "official": 108
    },
    {
        "id": "bw6",
        "name": "Dragons Éxaltés",
        "releaseDate": "2012-08-15",
        "logo": "https://assets.tcgdex.net/fr/bw/bw6/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw6/symbol",
        "serieId": "bw",
        "official": 124
    },
    {
        "id": "dv1",
        "name": "Coffre des Dragons",
        "releaseDate": "2012-10-05",
        "logo": "https://assets.tcgdex.net/fr/bw/dv1/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/dv1/symbol",
        "serieId": "bw",
        "official": 20
    },
    {
        "id": "bw7",
        "name": "Frontières Franchies",
        "releaseDate": "2012-11-07",
        "logo": "https://assets.tcgdex.net/fr/bw/bw7/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw7/symbol",
        "serieId": "bw",
        "official": 149
    },
    {
        "id": "bw8",
        "name": "Tempète Plasma",
        "releaseDate": "2013-02-06",
        "logo": "https://assets.tcgdex.net/fr/bw/bw8/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw8/symbol",
        "serieId": "bw",
        "official": 135
    },
    {
        "id": "bw9",
        "name": "Glaciation Plasma",
        "releaseDate": "2013-05-08",
        "logo": "https://assets.tcgdex.net/fr/bw/bw9/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw9/symbol",
        "serieId": "bw",
        "official": 116
    },
    {
        "id": "bw10",
        "name": "Explosion Plasma",
        "releaseDate": "2013-08-14",
        "logo": "https://assets.tcgdex.net/fr/bw/bw10/logo",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw10/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "xyp",
        "name": "Promo XY",
        "releaseDate": "2013-10-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xyp/symbol",
        "serieId": "xy",
        "official": 211
    },
    {
        "id": "2013bw",
        "name": "Promo McDonald's 2013",
        "releaseDate": "2013-11-01",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "rc",
        "name": "Radiant Collection",
        "releaseDate": "2013-11-06",
        "serieId": "bw",
        "official": 25
    },
    {
        "id": "xy0",
        "name": "Bienvenue à Kalos",
        "releaseDate": "2013-11-08",
        "logo": "https://assets.tcgdex.net/fr/xy/xy0/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy0/symbol",
        "serieId": "xy",
        "official": 39
    },
    {
        "id": "xy1",
        "name": "XY",
        "releaseDate": "2014-02-05",
        "logo": "https://assets.tcgdex.net/fr/xy/xy1/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy1/symbol",
        "serieId": "xy",
        "official": 146
    },
    {
        "id": "xya",
        "name": "carte alternative A Jaune",
        "releaseDate": "2014-02-05",
        "serieId": "xy",
        "official": 6
    },
    {
        "id": "tk-xy-sy",
        "name": "XY Kit du dresseur (Nymphali)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-sy/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-n",
        "name": "XY Kit du dresseur (Bruyverne)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-n/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy2",
        "name": "Étincelles",
        "releaseDate": "2014-05-07",
        "logo": "https://assets.tcgdex.net/fr/xy/xy2/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy2/symbol",
        "serieId": "xy",
        "official": 106
    },
    {
        "id": "2014xy",
        "name": "Promo McDonald's 2014",
        "releaseDate": "2014-05-23",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy3",
        "name": "Poings Furieux",
        "releaseDate": "2014-08-13",
        "logo": "https://assets.tcgdex.net/fr/xy/xy3/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy3/symbol",
        "serieId": "xy",
        "official": 111
    },
    {
        "id": "tk-xy-b",
        "name": "XY Kit du dresseur (Scalproie)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-b/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-w",
        "name": "XY Kit du dresseur (Grodoudou)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-w/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy4",
        "name": "Vigueur Spectrale",
        "releaseDate": "2014-11-05",
        "logo": "https://assets.tcgdex.net/fr/xy/xy4/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy4/symbol",
        "serieId": "xy",
        "official": 119
    },
    {
        "id": "xy5",
        "name": "Primo-Choc",
        "releaseDate": "2015-02-04",
        "logo": "https://assets.tcgdex.net/fr/xy/xy5/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy5/symbol",
        "serieId": "xy",
        "official": 160
    },
    {
        "id": "dc1",
        "name": "Double Danger",
        "releaseDate": "2015-03-25",
        "logo": "https://assets.tcgdex.net/fr/xy/dc1/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/dc1/symbol",
        "serieId": "xy",
        "official": 34
    },
    {
        "id": "tk-xy-latia",
        "name": "XY Kit du dresseur (Latias)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latia/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-latio",
        "name": "XY Kit du dresseur (Latios)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latio/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy6",
        "name": "Ciel Rugissant",
        "releaseDate": "2015-05-06",
        "logo": "https://assets.tcgdex.net/fr/xy/xy6/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy6/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "xy7",
        "name": "Origines Antiques",
        "releaseDate": "2015-08-12",
        "logo": "https://assets.tcgdex.net/fr/xy/xy7/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy7/symbol",
        "serieId": "xy",
        "official": 98
    },
    {
        "id": "xy8",
        "name": "Impulsion Turbo",
        "releaseDate": "2015-11-04",
        "logo": "https://assets.tcgdex.net/fr/xy/xy8/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy8/symbol",
        "serieId": "xy",
        "official": 162
    },
    {
        "id": "2015xy",
        "name": "Promo McDonald's 2015",
        "releaseDate": "2015-11-27",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy9",
        "name": "Rupture Turbo",
        "releaseDate": "2016-02-03",
        "logo": "https://assets.tcgdex.net/fr/xy/xy9/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy9/symbol",
        "serieId": "xy",
        "official": 122
    },
    {
        "id": "g1",
        "name": "Générations",
        "releaseDate": "2016-02-22",
        "logo": "https://assets.tcgdex.net/fr/xy/g1/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/g1/symbol",
        "serieId": "xy",
        "official": 83
    },
    {
        "id": "tk-xy-p",
        "name": "XY Kit du dresseur (Pikachu Libre)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-p/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-su",
        "name": "XY Kit du dresseur (Suicune)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-su/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy10",
        "name": "Impact des Destins",
        "releaseDate": "2016-05-02",
        "logo": "https://assets.tcgdex.net/fr/xy/xy10/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy10/symbol",
        "serieId": "xy",
        "official": 124
    },
    {
        "id": "xy11",
        "name": "Offensive Vapeur",
        "releaseDate": "2016-08-03",
        "logo": "https://assets.tcgdex.net/fr/xy/xy11/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy11/symbol",
        "serieId": "xy",
        "official": 114
    },
    {
        "id": "2016xy",
        "name": "Promo McDonald's 2016",
        "releaseDate": "2016-08-20",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy12",
        "name": "Évolutions",
        "releaseDate": "2016-11-02",
        "logo": "https://assets.tcgdex.net/fr/xy/xy12/logo",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy12/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "sm1",
        "name": "Soleil et Lune",
        "releaseDate": "2017-02-03",
        "logo": "https://assets.tcgdex.net/fr/sm/sm1/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm1/symbol",
        "serieId": "sm",
        "official": 149
    },
    {
        "id": "smp",
        "name": "Promo SM",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/smp/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "tk-sm-l",
        "name": "SM Kit du dresseur (Lougarox)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-l/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-sm-r",
        "name": "SM Kit du dresseur (Raichu d'Alola)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "sm2",
        "name": "Gardiens Ascendants",
        "releaseDate": "2017-05-05",
        "logo": "https://assets.tcgdex.net/fr/sm/sm2/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm2/symbol",
        "serieId": "sm",
        "official": 145
    },
    {
        "id": "2017sm",
        "name": "Promo McDonald's 2017",
        "releaseDate": "2017-08-03",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "sm3",
        "name": "Ombres Ardentes",
        "releaseDate": "2017-08-04",
        "logo": "https://assets.tcgdex.net/fr/sm/sm3/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm3/symbol",
        "serieId": "sm",
        "official": 147
    },
    {
        "id": "sm3.5",
        "name": "Légendes Brillantes",
        "releaseDate": "2017-10-06",
        "logo": "https://assets.tcgdex.net/fr/sm/sm3.5/logo",
        "serieId": "sm",
        "official": 73
    },
    {
        "id": "sm4",
        "name": "Invasion Carmin",
        "releaseDate": "2017-11-03",
        "logo": "https://assets.tcgdex.net/fr/sm/sm4/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm4/symbol",
        "serieId": "sm",
        "official": 111
    },
    {
        "id": "sm5",
        "name": "Ultra-Prisme",
        "releaseDate": "2018-02-02",
        "logo": "https://assets.tcgdex.net/fr/sm/sm5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm5/symbol",
        "serieId": "sm",
        "official": 156
    },
    {
        "id": "sm6",
        "name": "Lumière Interdite",
        "releaseDate": "2018-05-04",
        "logo": "https://assets.tcgdex.net/fr/sm/sm6/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm6/symbol",
        "serieId": "sm",
        "official": 131
    },
    {
        "id": "2018sm-fr",
        "name": "Promo McDonald's 2018",
        "releaseDate": "2018-06-13",
        "serieId": "mc",
        "official": 40
    },
    {
        "id": "sm7",
        "name": "Tempête Céleste",
        "releaseDate": "2018-08-03",
        "logo": "https://assets.tcgdex.net/fr/sm/sm7/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm7/symbol",
        "serieId": "sm",
        "official": 168
    },
    {
        "id": "sm7.5",
        "name": "Majesté Des Dragons",
        "releaseDate": "2018-09-07",
        "logo": "https://assets.tcgdex.net/fr/sm/sm7.5/logo",
        "serieId": "sm",
        "official": 70
    },
    {
        "id": "sm8",
        "name": "Tonnerre Perdu",
        "releaseDate": "2018-11-02",
        "logo": "https://assets.tcgdex.net/fr/sm/sm8/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm8/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm9",
        "name": "Duo de Choc",
        "releaseDate": "2019-01-31",
        "logo": "https://assets.tcgdex.net/fr/sm/sm9/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm9/symbol",
        "serieId": "sm",
        "official": 181
    },
    {
        "id": "det1",
        "name": "Détective Pikachu",
        "releaseDate": "2019-03-29",
        "logo": "https://assets.tcgdex.net/fr/sm/det1/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/det1/symbol",
        "serieId": "sm",
        "official": 18
    },
    {
        "id": "sm10",
        "name": "Alliance Infaillible",
        "releaseDate": "2019-05-03",
        "logo": "https://assets.tcgdex.net/fr/sm/sm10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm10/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm11",
        "name": "Harmonie des Esprits",
        "releaseDate": "2019-08-02",
        "logo": "https://assets.tcgdex.net/fr/sm/sm11/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm11/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "sm115",
        "name": "Destinées Occultes",
        "releaseDate": "2019-08-23",
        "logo": "https://assets.tcgdex.net/fr/sm/sm115/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm115/symbol",
        "serieId": "sm",
        "official": 68
    },
    {
        "id": "sma",
        "name": "Carte Alternative A Jaune",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sma/symbol",
        "serieId": "sm",
        "official": 94
    },
    {
        "id": "2019sm-fr",
        "name": "Promo McDonald's 2019",
        "releaseDate": "2019-10-30",
        "serieId": "mc",
        "official": 40
    },
    {
        "id": "sm12",
        "name": "Éclipse Cosmique",
        "releaseDate": "2019-11-01",
        "logo": "https://assets.tcgdex.net/fr/sm/sm12/logo",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm12/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "swshp",
        "name": "Promo SWSH",
        "releaseDate": "2019-11-15",
        "logo": "https://assets.tcgdex.net/fr/swsh/swshp/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swshp/symbol",
        "serieId": "swsh",
        "official": 107
    },
    {
        "id": "swsh1",
        "name": "Épée et Bouclier",
        "releaseDate": "2020-02-07",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh1/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh1/symbol",
        "serieId": "swsh",
        "official": 202
    },
    {
        "id": "swsh2",
        "name": "Clash des Rebelles",
        "releaseDate": "2020-05-01",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh2/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh2/symbol",
        "serieId": "swsh",
        "official": 192
    },
    {
        "id": "swsh3",
        "name": "Ténèbres Embrasées",
        "releaseDate": "2020-08-14",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh3/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh3.5",
        "name": "La Voie du Maître",
        "releaseDate": "2020-09-25",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh3.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3.5/symbol",
        "serieId": "swsh",
        "official": 70
    },
    {
        "id": "swsh4",
        "name": "Voltage Éclatant",
        "releaseDate": "2020-11-13",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh4/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4/symbol",
        "serieId": "swsh",
        "official": 185
    },
    {
        "id": "2021swsh",
        "name": "Promo McDonald's 2021",
        "releaseDate": "2021-02-09",
        "symbol": "https://assets.tcgdex.net/univ/mc/2021swsh/symbol",
        "serieId": "mc",
        "official": 25
    },
    {
        "id": "swsh4.5",
        "name": "Destinées Radieuses",
        "releaseDate": "2021-02-19",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh4.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4.5/symbol",
        "serieId": "swsh",
        "official": 72
    },
    {
        "id": "swsh5",
        "name": "Styles de combat",
        "releaseDate": "2021-03-19",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh5/symbol",
        "serieId": "swsh",
        "official": 163
    },
    {
        "id": "swsh6",
        "name": "Règne de Glace",
        "releaseDate": "2021-06-18",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh6/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh6/symbol",
        "serieId": "swsh",
        "official": 198
    },
    {
        "id": "swsh7",
        "name": "Évolution Céleste",
        "releaseDate": "2021-08-27",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh7/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh7/symbol",
        "serieId": "swsh",
        "official": 203
    },
    {
        "id": "cel25",
        "name": "Célébrations",
        "releaseDate": "2021-10-08",
        "logo": "https://assets.tcgdex.net/fr/swsh/cel25/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/cel25/symbol",
        "serieId": "swsh",
        "official": 25
    },
    {
        "id": "swsh8",
        "name": "Poing de Fusion",
        "releaseDate": "2021-11-12",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh8/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh8/symbol",
        "serieId": "swsh",
        "official": 264
    },
    {
        "id": "swsh9",
        "name": "Stars Étincelantes",
        "releaseDate": "2022-02-25",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh9/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh9/symbol",
        "serieId": "swsh",
        "official": 172
    },
    {
        "id": "swsh10",
        "name": "Astres Radieux",
        "releaseDate": "2022-05-27",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh10/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh10.5",
        "name": "Pokémon GO",
        "releaseDate": "2022-07-01",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh10.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10.5/symbol",
        "serieId": "swsh",
        "official": 78
    },
    {
        "id": "swsh11",
        "name": "Origine Perdue",
        "releaseDate": "2022-09-09",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh11/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh11/symbol",
        "serieId": "swsh",
        "official": 196
    },
    {
        "id": "swsh12",
        "name": "Tempête Argentée",
        "releaseDate": "2022-11-11",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh12/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12/symbol",
        "serieId": "swsh",
        "official": 195
    },
    {
        "id": "swsh12.5",
        "name": "Zénith Suprême",
        "releaseDate": "2023-01-20",
        "logo": "https://assets.tcgdex.net/fr/swsh/swsh12.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12.5/symbol",
        "serieId": "swsh",
        "official": 159
    },
    {
        "id": "svp",
        "name": "SVP Black Star Promos",
        "releaseDate": "2023-03-31",
        "serieId": "sv",
        "official": 0
    },
    {
        "id": "sv01",
        "name": "Écarlate et Violet",
        "releaseDate": "2023-03-31",
        "logo": "https://assets.tcgdex.net/fr/sv/sv01/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv01/symbol",
        "serieId": "sv",
        "official": 198
    },
    {
        "id": "sv02",
        "name": "Évolutions à Paldea",
        "releaseDate": "2023-06-09",
        "logo": "https://assets.tcgdex.net/fr/sv/sv02/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv02/symbol",
        "serieId": "sv",
        "official": 193
    },
    {
        "id": "sv03",
        "name": "Flammes Obsidiennes",
        "releaseDate": "2023-08-11",
        "logo": "https://assets.tcgdex.net/fr/sv/sv03/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03/symbol",
        "serieId": "sv",
        "official": 197
    },
    {
        "id": "sv03.5",
        "name": "151",
        "releaseDate": "2023-09-22",
        "logo": "https://assets.tcgdex.net/fr/sv/sv03.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03.5/symbol",
        "serieId": "sv",
        "official": 165
    },
    {
        "id": "sv04",
        "name": "Faille Paradoxe",
        "releaseDate": "2023-11-03",
        "logo": "https://assets.tcgdex.net/fr/sv/sv04/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv04.5",
        "name": "Destinées de Paldea",
        "releaseDate": "2024-01-26",
        "logo": "https://assets.tcgdex.net/fr/sv/sv04.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04.5/symbol",
        "serieId": "sv",
        "official": 91
    },
    {
        "id": "sv05",
        "name": "Forces Temporelles",
        "releaseDate": "2024-03-22",
        "logo": "https://assets.tcgdex.net/fr/sv/sv05/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv05/symbol",
        "serieId": "sv",
        "official": 162
    },
    {
        "id": "sv06",
        "name": "Mascarade Crépusculaire",
        "releaseDate": "2024-05-24",
        "logo": "https://assets.tcgdex.net/fr/sv/sv06/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06/symbol",
        "serieId": "sv",
        "official": 167
    },
    {
        "id": "sv06.5",
        "name": "Fable Nébuleuse",
        "releaseDate": "2024-08-02",
        "logo": "https://assets.tcgdex.net/fr/sv/sv06.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06.5/symbol",
        "serieId": "sv",
        "official": 64
    },
    {
        "id": "sv07",
        "name": "Couronne Stellaire",
        "releaseDate": "2024-09-13",
        "logo": "https://assets.tcgdex.net/fr/sv/sv07/logo",
        "serieId": "sv",
        "official": 142
    },
    {
        "id": "A1",
        "name": "Puissance Génétique",
        "releaseDate": "2024-10-30",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A1/symbol",
        "serieId": "tcgp",
        "official": 226
    },
    {
        "id": "P-A",
        "name": "Promo-A",
        "releaseDate": "2024-10-30",
        "serieId": "tcgp",
        "official": 0
    },
    {
        "id": "sv08",
        "name": "Étincelles Déferlantes",
        "releaseDate": "2024-11-08",
        "logo": "https://assets.tcgdex.net/fr/sv/sv08/logo",
        "serieId": "sv",
        "official": 191
    },
    {
        "id": "A1a",
        "name": "L’Île Fabuleuse",
        "releaseDate": "2024-12-17",
        "serieId": "tcgp",
        "official": 68
    },
    {
        "id": "sv08.5",
        "name": "Évolutions Prismatiques",
        "releaseDate": "2025-01-17",
        "logo": "https://assets.tcgdex.net/fr/sv/sv08.5/logo",
        "serieId": "sv",
        "official": 131
    },
    {
        "id": "A2",
        "name": "Choc Spatio-Temporel",
        "releaseDate": "2025-01-30",
        "serieId": "tcgp",
        "official": 140
    },
    {
        "id": "A2a",
        "name": "Lumière Triomphale",
        "releaseDate": "2025-02-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2a/symbol",
        "serieId": "tcgp",
        "official": 75
    },
    {
        "id": "A2b",
        "name": "Réjouissances Rayonnantes",
        "releaseDate": "2025-03-27",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2b/symbol",
        "serieId": "tcgp",
        "official": 72
    },
    {
        "id": "sv09",
        "name": "Aventures Ensemble",
        "releaseDate": "2025-03-28",
        "logo": "https://assets.tcgdex.net/fr/sv/sv09/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv09/symbol",
        "serieId": "sv",
        "official": 159
    },
    {
        "id": "A3",
        "name": "Gardiens Astraux",
        "releaseDate": "2025-04-30",
        "serieId": "tcgp",
        "official": 155
    },
    {
        "id": "A3a",
        "name": "Crise Interdimensionnelle",
        "releaseDate": "2025-05-29",
        "serieId": "tcgp",
        "official": 69
    },
    {
        "id": "sv10",
        "name": "Rivalités Destinées",
        "releaseDate": "2025-05-30",
        "logo": "https://assets.tcgdex.net/fr/sv/sv10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "A3b",
        "name": "La Clairière d'Évoli",
        "releaseDate": "2025-06-26",
        "serieId": "tcgp",
        "official": 69
    },
    {
        "id": "sv10.5b",
        "name": "Foudre Noire",
        "releaseDate": "2025-07-17",
        "logo": "https://assets.tcgdex.net/fr/sv/sv10.5b/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5b/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "sv10.5w",
        "name": "Flamme Blanche",
        "releaseDate": "2025-07-17",
        "logo": "https://assets.tcgdex.net/fr/sv/sv10.5w/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5w/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "A4",
        "name": "Sagesse Entre Ciel et Mer",
        "releaseDate": "2025-07-30",
        "serieId": "tcgp",
        "official": 161
    },
    {
        "id": "A4a",
        "name": "Source Secrète",
        "releaseDate": "2025-08-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A4a/symbol",
        "serieId": "tcgp",
        "official": 71
    },
    {
        "id": "me01",
        "name": "Méga-Évolution",
        "releaseDate": "2025-09-26",
        "logo": "https://assets.tcgdex.net/fr/me/me01/logo",
        "symbol": "https://assets.tcgdex.net/univ/me/me01/symbol",
        "serieId": "me",
        "official": 132
    }
]
""",
    "de" to """
[
    {
        "id": "base1",
        "name": "Grundset",
        "releaseDate": "1999-01-09",
        "serieId": "base",
        "official": 102
    },
    {
        "id": "base2",
        "name": "Dschungel",
        "releaseDate": "1999-06-16",
        "symbol": "https://assets.tcgdex.net/univ/base/base2/symbol",
        "serieId": "base",
        "official": 64
    },
    {
        "id": "basep",
        "name": "Wizards Black Star Promos",
        "releaseDate": "1999-07-01",
        "symbol": "https://assets.tcgdex.net/univ/base/basep/symbol",
        "serieId": "base",
        "official": 53
    },
    {
        "id": "base3",
        "name": "Fossil",
        "releaseDate": "1999-10-10",
        "symbol": "https://assets.tcgdex.net/univ/base/base3/symbol",
        "serieId": "base",
        "official": 62
    },
    {
        "id": "base5",
        "name": "Team Rocket",
        "releaseDate": "2000-04-24",
        "symbol": "https://assets.tcgdex.net/univ/base/base5/symbol",
        "serieId": "base",
        "official": 82
    },
    {
        "id": "neo1",
        "name": "Neo Genesis",
        "releaseDate": "2000-12-16",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo1/symbol",
        "serieId": "neo",
        "official": 111
    },
    {
        "id": "neo2",
        "name": "Neo Entdeckung",
        "releaseDate": "2001-06-01",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo2/symbol",
        "serieId": "neo",
        "official": 75
    },
    {
        "id": "neo3",
        "name": "Neo Revelation",
        "releaseDate": "2001-09-21",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo3/symbol",
        "serieId": "neo",
        "official": 64
    },
    {
        "id": "neo4",
        "name": "Neo Destiny",
        "releaseDate": "2002-02-28",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo4/symbol",
        "serieId": "neo",
        "official": 105
    },
    {
        "id": "ecard1",
        "name": "Expedition",
        "releaseDate": "2002-09-15",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard1/symbol",
        "serieId": "ecard",
        "official": 165
    },
    {
        "id": "ecard2",
        "name": "Aquapolis",
        "releaseDate": "2003-01-15",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard2/symbol",
        "serieId": "ecard",
        "official": 147
    },
    {
        "id": "ecard3",
        "name": "Skyridge",
        "releaseDate": "2003-05-12",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard3/symbol",
        "serieId": "ecard",
        "official": 144
    },
    {
        "id": "ex1",
        "name": "EX Rubin & Saphir",
        "releaseDate": "2003-07-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex1/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "ex2",
        "name": "EX Sandsturm",
        "releaseDate": "2003-09-18",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex2/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "ex3",
        "name": "EX Drache",
        "releaseDate": "2003-11-24",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex3/symbol",
        "serieId": "ex",
        "official": 97
    },
    {
        "id": "ex4",
        "name": "EX Team Magma vs Team Aqua",
        "releaseDate": "2004-03-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex4/symbol",
        "serieId": "ex",
        "official": 95
    },
    {
        "id": "ex6",
        "name": "EX Feuerrot & Blattgrün",
        "releaseDate": "2004-09-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex6/symbol",
        "serieId": "ex",
        "official": 112
    },
    {
        "id": "ex8",
        "name": "EX Deoxys",
        "releaseDate": "2005-02-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex8/symbol",
        "serieId": "ex",
        "official": 107
    },
    {
        "id": "ex9",
        "name": "EX Smaragd",
        "releaseDate": "2005-05-09",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex9/symbol",
        "serieId": "ex",
        "official": 106
    },
    {
        "id": "exu",
        "name": "EX Verborgene Mächte Icognito",
        "releaseDate": "2005-08-22",
        "serieId": "ex",
        "official": 28
    },
    {
        "id": "ex10",
        "name": "EX Verborgene Mächte",
        "releaseDate": "2005-08-22",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex10/symbol",
        "serieId": "ex",
        "official": 115
    },
    {
        "id": "ex11",
        "name": "EX Delta Species",
        "releaseDate": "2005-10-31",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex11/symbol",
        "serieId": "ex",
        "official": 113
    },
    {
        "id": "ex12",
        "name": "EX Legend Maker",
        "releaseDate": "2006-02-13",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex12/symbol",
        "serieId": "ex",
        "official": 92
    },
    {
        "id": "ex13",
        "name": "EX Holon Phantoms",
        "releaseDate": "2006-05-03",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex13/symbol",
        "serieId": "ex",
        "official": 110
    },
    {
        "id": "ex14",
        "name": "EX Crystal Guardians",
        "releaseDate": "2006-08-30",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex14/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "ex15",
        "name": "EX Dragon Frontiers",
        "releaseDate": "2006-11-08",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex15/symbol",
        "serieId": "ex",
        "official": 101
    },
    {
        "id": "ex16",
        "name": "EX Power Keepers",
        "releaseDate": "2007-02-17",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex16/symbol",
        "serieId": "ex",
        "official": 108
    },
    {
        "id": "dp1",
        "name": "Diamant & Perl",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp1/symbol",
        "serieId": "dp",
        "official": 130
    },
    {
        "id": "dpp",
        "name": "Promo DP",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dpp/symbol",
        "serieId": "dp",
        "official": 56
    },
    {
        "id": "dp2",
        "name": "Geheimnisvolle Schätze",
        "releaseDate": "2007-08-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp2/symbol",
        "serieId": "dp",
        "official": 122
    },
    {
        "id": "dp3",
        "name": "Rätselhafte Wunder",
        "releaseDate": "2007-11-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp3/symbol",
        "serieId": "dp",
        "official": 132
    },
    {
        "id": "dp4",
        "name": "Epische Begegnungen",
        "releaseDate": "2008-02-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp4/symbol",
        "serieId": "dp",
        "official": 106
    },
    {
        "id": "dp5",
        "name": "Majestätischer Morgen",
        "releaseDate": "2008-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp5/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "dp6",
        "name": "Erwachte Legenden",
        "releaseDate": "2008-08-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp6/symbol",
        "serieId": "dp",
        "official": 146
    },
    {
        "id": "dp7",
        "name": "Sturmtief",
        "releaseDate": "2008-11-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp7/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "pl1",
        "name": "Platin",
        "releaseDate": "2009-02-11",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl1/symbol",
        "serieId": "pl",
        "official": 127
    },
    {
        "id": "pl2",
        "name": "Aufstieg der Rivalen",
        "releaseDate": "2009-05-16",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl2/symbol",
        "serieId": "pl",
        "official": 111
    },
    {
        "id": "pl3",
        "name": "Ultimative Sieger",
        "releaseDate": "2009-08-19",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl3/symbol",
        "serieId": "pl",
        "official": 147
    },
    {
        "id": "pl4",
        "name": "Arceus",
        "releaseDate": "2009-11-04",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl4/symbol",
        "serieId": "pl",
        "official": 99
    },
    {
        "id": "hgss1",
        "name": "HeartGold SoulSilver",
        "releaseDate": "2010-02-10",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss1/symbol",
        "serieId": "hgss",
        "official": 123
    },
    {
        "id": "hgssp",
        "name": "Promo HGSS",
        "releaseDate": "2010-02-11",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgssp/symbol",
        "serieId": "hgss",
        "official": 25
    },
    {
        "id": "hgss2",
        "name": "Entfesselt",
        "releaseDate": "2010-05-12",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss2/symbol",
        "serieId": "hgss",
        "official": 95
    },
    {
        "id": "hgss3",
        "name": "Unerschrocken",
        "releaseDate": "2010-08-18",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss3/symbol",
        "serieId": "hgss",
        "official": 90
    },
    {
        "id": "hgss4",
        "name": "Triumph",
        "releaseDate": "2010-11-03",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss4/symbol",
        "serieId": "hgss",
        "official": 102
    },
    {
        "id": "col1",
        "name": "Ruf der Legenden",
        "releaseDate": "2011-02-09",
        "symbol": "https://assets.tcgdex.net/univ/col/col1/symbol",
        "serieId": "col",
        "official": 95
    },
    {
        "id": "bw1",
        "name": "Schwarz & Weiß",
        "releaseDate": "2011-04-25",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw1/symbol",
        "serieId": "bw",
        "official": 114
    },
    {
        "id": "bwp",
        "name": "SW Promokarten",
        "releaseDate": "2011-04-26",
        "symbol": "https://assets.tcgdex.net/univ/bw/bwp/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw2",
        "name": "Aufstreben der Mächtigen",
        "releaseDate": "2011-08-31",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw2/symbol",
        "serieId": "bw",
        "official": 98
    },
    {
        "id": "bw3",
        "name": "Königliche Siege",
        "releaseDate": "2011-11-16",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw3/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw4",
        "name": "Kommende Schicksale",
        "releaseDate": "2012-02-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw4/symbol",
        "serieId": "bw",
        "official": 99
    },
    {
        "id": "bw5",
        "name": "Erfoscher der Finsternis",
        "releaseDate": "2012-05-09",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw5/symbol",
        "serieId": "bw",
        "official": 108
    },
    {
        "id": "bw6",
        "name": "Hoheit der Drachen",
        "releaseDate": "2012-08-15",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw6/symbol",
        "serieId": "bw",
        "official": 124
    },
    {
        "id": "dv1",
        "name": "Drachengruft",
        "releaseDate": "2012-10-05",
        "symbol": "https://assets.tcgdex.net/univ/bw/dv1/symbol",
        "serieId": "bw",
        "official": 20
    },
    {
        "id": "bw7",
        "name": "Überschrittene Schwellen",
        "releaseDate": "2012-11-07",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw7/symbol",
        "serieId": "bw",
        "official": 149
    },
    {
        "id": "bw8",
        "name": "Plasma-Sturm",
        "releaseDate": "2013-02-06",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw8/symbol",
        "serieId": "bw",
        "official": 135
    },
    {
        "id": "bw9",
        "name": "Plasma-Frost",
        "releaseDate": "2013-05-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw9/symbol",
        "serieId": "bw",
        "official": 116
    },
    {
        "id": "bw10",
        "name": "Plasma-Blaster",
        "releaseDate": "2013-08-14",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw10/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "xyp",
        "name": "XY Promokarten",
        "releaseDate": "2013-10-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xyp/symbol",
        "serieId": "xy",
        "official": 211
    },
    {
        "id": "xy0",
        "name": "Willkommen in Kalos",
        "releaseDate": "2013-11-08",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy0/symbol",
        "serieId": "xy",
        "official": 39
    },
    {
        "id": "xy1",
        "name": "XY",
        "releaseDate": "2014-02-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy1/symbol",
        "serieId": "xy",
        "official": 146
    },
    {
        "id": "xya",
        "name": "Gelbes-A-Alternativkarten",
        "releaseDate": "2014-02-05",
        "serieId": "xy",
        "official": 6
    },
    {
        "id": "xy2",
        "name": "Flammenmeer",
        "releaseDate": "2014-05-07",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy2/symbol",
        "serieId": "xy",
        "official": 106
    },
    {
        "id": "xy3",
        "name": "Fliegende Fäuste",
        "releaseDate": "2014-08-13",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy3/symbol",
        "serieId": "xy",
        "official": 111
    },
    {
        "id": "xy4",
        "name": "Phantomkräfte",
        "releaseDate": "2014-11-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy4/symbol",
        "serieId": "xy",
        "official": 119
    },
    {
        "id": "xy5",
        "name": "Protoshock",
        "releaseDate": "2015-02-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy5/symbol",
        "serieId": "xy",
        "official": 160
    },
    {
        "id": "xy6",
        "name": "Drachenleuchten",
        "releaseDate": "2015-05-06",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy6/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "xy7",
        "name": "Ewiger Anfang",
        "releaseDate": "2015-08-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy7/symbol",
        "serieId": "xy",
        "official": 98
    },
    {
        "id": "xy8",
        "name": "TURBOstart",
        "releaseDate": "2015-11-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy8/symbol",
        "serieId": "xy",
        "official": 162
    },
    {
        "id": "xy9",
        "name": "TURBOfieber",
        "releaseDate": "2016-02-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy9/symbol",
        "serieId": "xy",
        "official": 122
    },
    {
        "id": "g1",
        "name": "Generationen",
        "releaseDate": "2016-02-22",
        "symbol": "https://assets.tcgdex.net/univ/xy/g1/symbol",
        "serieId": "xy",
        "official": 83
    },
    {
        "id": "xy10",
        "name": "Schicksalsschmiede",
        "releaseDate": "2016-05-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy10/symbol",
        "serieId": "xy",
        "official": 124
    },
    {
        "id": "xy11",
        "name": "Dampfkessel",
        "releaseDate": "2016-08-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy11/symbol",
        "serieId": "xy",
        "official": 114
    },
    {
        "id": "xy12",
        "name": "Evolution",
        "releaseDate": "2016-11-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy12/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "sm1",
        "name": "Sonne & Mond",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm1/symbol",
        "serieId": "sm",
        "official": 149
    },
    {
        "id": "smp",
        "name": "SM Promokarten",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/smp/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "sm2",
        "name": "Stunde der Wächter",
        "releaseDate": "2017-05-05",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm2/symbol",
        "serieId": "sm",
        "official": 145
    },
    {
        "id": "sm3",
        "name": "Nacht in Flammen",
        "releaseDate": "2017-08-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm3/symbol",
        "serieId": "sm",
        "official": 147
    },
    {
        "id": "sm3.5",
        "name": "Schimmernde Legenden",
        "releaseDate": "2017-10-06",
        "serieId": "sm",
        "official": 73
    },
    {
        "id": "sm4",
        "name": "Aufziehen der Sturmröte",
        "releaseDate": "2017-11-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm4/symbol",
        "serieId": "sm",
        "official": 111
    },
    {
        "id": "sm5",
        "name": "Ultra-Prisma",
        "releaseDate": "2018-02-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm5/symbol",
        "serieId": "sm",
        "official": 156
    },
    {
        "id": "sm6",
        "name": "Grauen Der Lichtfinsternis",
        "releaseDate": "2018-05-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm6/symbol",
        "serieId": "sm",
        "official": 131
    },
    {
        "id": "sm7",
        "name": "Sturm Am Firmament",
        "releaseDate": "2018-08-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm7/symbol",
        "serieId": "sm",
        "official": 168
    },
    {
        "id": "sm7.5",
        "name": "Majestät Der Drachen",
        "releaseDate": "2018-09-07",
        "serieId": "sm",
        "official": 70
    },
    {
        "id": "sm8",
        "name": "Echo des Donners",
        "releaseDate": "2018-11-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm8/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm9",
        "name": "Teams Sind Trumpf",
        "releaseDate": "2019-01-31",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm9/symbol",
        "serieId": "sm",
        "official": 181
    },
    {
        "id": "det1",
        "name": "Meisterdetektiv Pikachu",
        "releaseDate": "2019-03-29",
        "symbol": "https://assets.tcgdex.net/univ/sm/det1/symbol",
        "serieId": "sm",
        "official": 18
    },
    {
        "id": "sm10",
        "name": "Kräfte im Einklang",
        "releaseDate": "2019-05-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm10/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm11",
        "name": "Bund der Gleichgesinnten",
        "releaseDate": "2019-08-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm11/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "sm115",
        "name": "Verborgenes Schicksal",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm115/symbol",
        "serieId": "sm",
        "official": 68
    },
    {
        "id": "sma",
        "name": "Gelbes A-Alternativkarte",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sma/symbol",
        "serieId": "sm",
        "official": 94
    },
    {
        "id": "sm12",
        "name": "Welten im Wandel",
        "releaseDate": "2019-11-01",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm12/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "swshp",
        "name": "SCSC Promokarten",
        "releaseDate": "2019-11-15",
        "logo": "https://assets.tcgdex.net/de/swsh/swshp/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swshp/symbol",
        "serieId": "swsh",
        "official": 107
    },
    {
        "id": "swsh1",
        "name": "Schwert & Schild",
        "releaseDate": "2020-02-07",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh1/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh1/symbol",
        "serieId": "swsh",
        "official": 202
    },
    {
        "id": "swsh2",
        "name": "Clash der Rebellen",
        "releaseDate": "2020-05-01",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh2/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh2/symbol",
        "serieId": "swsh",
        "official": 192
    },
    {
        "id": "swsh3",
        "name": "Flammende Finsternis",
        "releaseDate": "2020-08-14",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh3/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh3.5",
        "name": "Weg des Champs",
        "releaseDate": "2020-09-25",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh3.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3.5/symbol",
        "serieId": "swsh",
        "official": 70
    },
    {
        "id": "swsh4",
        "name": "Farbenschock",
        "releaseDate": "2020-11-13",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh4/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4/symbol",
        "serieId": "swsh",
        "official": 185
    },
    {
        "id": "swsh4.5",
        "name": "Glänzendes Schicksal",
        "releaseDate": "2021-02-19",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh4.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4.5/symbol",
        "serieId": "swsh",
        "official": 72
    },
    {
        "id": "swsh5",
        "name": "Kampfstile",
        "releaseDate": "2021-03-19",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh5/symbol",
        "serieId": "swsh",
        "official": 163
    },
    {
        "id": "swsh6",
        "name": "Schaurige Herrschaft",
        "releaseDate": "2021-06-18",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh6/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh6/symbol",
        "serieId": "swsh",
        "official": 198
    },
    {
        "id": "swsh7",
        "name": "Drachenwandel",
        "releaseDate": "2021-08-27",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh7/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh7/symbol",
        "serieId": "swsh",
        "official": 203
    },
    {
        "id": "cel25",
        "name": "Celebrations",
        "releaseDate": "2021-10-08",
        "logo": "https://assets.tcgdex.net/de/swsh/cel25/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/cel25/symbol",
        "serieId": "swsh",
        "official": 25
    },
    {
        "id": "swsh8",
        "name": "Fusions Angriff",
        "releaseDate": "2021-11-12",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh8/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh8/symbol",
        "serieId": "swsh",
        "official": 264
    },
    {
        "id": "swsh9",
        "name": "Strahlende Sterne",
        "releaseDate": "2022-02-25",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh9/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh9/symbol",
        "serieId": "swsh",
        "official": 172
    },
    {
        "id": "swsh10",
        "name": "Astralglanz",
        "releaseDate": "2022-05-27",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh10/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh10.5",
        "name": "Pokémon GO",
        "releaseDate": "2022-07-01",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh10.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10.5/symbol",
        "serieId": "swsh",
        "official": 78
    },
    {
        "id": "swsh11",
        "name": "Verlorener Ursprung",
        "releaseDate": "2022-09-09",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh11/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh11/symbol",
        "serieId": "swsh",
        "official": 196
    },
    {
        "id": "swsh12",
        "name": "Silberne Sturmwinde",
        "releaseDate": "2022-11-11",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh12/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12/symbol",
        "serieId": "swsh",
        "official": 195
    },
    {
        "id": "swsh12.5",
        "name": "Zenit der Könige",
        "releaseDate": "2023-01-20",
        "logo": "https://assets.tcgdex.net/de/swsh/swsh12.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12.5/symbol",
        "serieId": "swsh",
        "official": 159
    },
    {
        "id": "svp",
        "name": "SVP Black Star Promos",
        "releaseDate": "2023-03-31",
        "serieId": "sv",
        "official": 0
    },
    {
        "id": "sv01",
        "name": "Karmesin & Purpur",
        "releaseDate": "2023-03-31",
        "logo": "https://assets.tcgdex.net/de/sv/sv01/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv01/symbol",
        "serieId": "sv",
        "official": 198
    },
    {
        "id": "sv02",
        "name": "Entwicklungen in Paldea",
        "releaseDate": "2023-06-09",
        "logo": "https://assets.tcgdex.net/de/sv/sv02/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv02/symbol",
        "serieId": "sv",
        "official": 193
    },
    {
        "id": "sv03",
        "name": "Obsidian Flammen",
        "releaseDate": "2023-08-11",
        "logo": "https://assets.tcgdex.net/de/sv/sv03/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03/symbol",
        "serieId": "sv",
        "official": 197
    },
    {
        "id": "sv03.5",
        "name": "151",
        "releaseDate": "2023-09-22",
        "logo": "https://assets.tcgdex.net/de/sv/sv03.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03.5/symbol",
        "serieId": "sv",
        "official": 165
    },
    {
        "id": "sv04",
        "name": "Paradoxrift",
        "releaseDate": "2023-11-03",
        "logo": "https://assets.tcgdex.net/de/sv/sv04/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv04.5",
        "name": "Paldeas Schicksale",
        "releaseDate": "2024-01-26",
        "logo": "https://assets.tcgdex.net/de/sv/sv04.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04.5/symbol",
        "serieId": "sv",
        "official": 91
    },
    {
        "id": "sv05",
        "name": "Gewalten der Zeit",
        "releaseDate": "2024-03-22",
        "logo": "https://assets.tcgdex.net/de/sv/sv05/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv05/symbol",
        "serieId": "sv",
        "official": 162
    },
    {
        "id": "sv06",
        "name": "Maskerade im Zwielicht",
        "releaseDate": "2024-05-24",
        "logo": "https://assets.tcgdex.net/de/sv/sv06/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06/symbol",
        "serieId": "sv",
        "official": 167
    },
    {
        "id": "sv06.5",
        "name": "Nebel der Sagen",
        "releaseDate": "2024-08-02",
        "logo": "https://assets.tcgdex.net/de/sv/sv06.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06.5/symbol",
        "serieId": "sv",
        "official": 64
    },
    {
        "id": "sv07",
        "name": "Stellarkrone",
        "releaseDate": "2024-09-13",
        "logo": "https://assets.tcgdex.net/de/sv/sv07/logo",
        "serieId": "sv",
        "official": 142
    },
    {
        "id": "A1",
        "name": "Unschlagbare Gene",
        "releaseDate": "2024-10-30",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A1/symbol",
        "serieId": "tcgp",
        "official": 226
    },
    {
        "id": "P-A",
        "name": "PROMO-A",
        "releaseDate": "2024-10-30",
        "serieId": "tcgp",
        "official": 0
    },
    {
        "id": "sv08",
        "name": "Stürmische Funken",
        "releaseDate": "2024-11-08",
        "logo": "https://assets.tcgdex.net/de/sv/sv08/logo",
        "serieId": "sv",
        "official": 191
    },
    {
        "id": "A1a",
        "name": "Mysteriöse Insel",
        "releaseDate": "2024-12-17",
        "serieId": "tcgp",
        "official": 68
    },
    {
        "id": "sv08.5",
        "name": "Prismatische Entwicklungen",
        "releaseDate": "2025-01-17",
        "logo": "https://assets.tcgdex.net/de/sv/sv08.5/logo",
        "serieId": "sv",
        "official": 131
    },
    {
        "id": "A2",
        "name": "Kollision von Raum und Zeit",
        "releaseDate": "2025-01-30",
        "serieId": "tcgp",
        "official": 140
    },
    {
        "id": "A2a",
        "name": "Licht des Triumphs",
        "releaseDate": "2025-02-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2a/symbol",
        "serieId": "tcgp",
        "official": 75
    },
    {
        "id": "A2b",
        "name": "Glänzendes Festival",
        "releaseDate": "2025-03-27",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2b/symbol",
        "serieId": "tcgp",
        "official": 72
    },
    {
        "id": "sv09",
        "name": "Reisegefährten",
        "releaseDate": "2025-03-28",
        "logo": "https://assets.tcgdex.net/de/sv/sv09/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv09/symbol",
        "serieId": "sv",
        "official": 159
    },
    {
        "id": "A3",
        "name": "Hüter des Firmaments",
        "releaseDate": "2025-04-30",
        "serieId": "tcgp",
        "official": 155
    },
    {
        "id": "sv10",
        "name": "Ewige Rivalen",
        "releaseDate": "2025-05-30",
        "logo": "https://assets.tcgdex.net/de/sv/sv10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv10.5b",
        "name": "Schwarze Blitze",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5b/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "sv10.5w",
        "name": "Weiße Flammen",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5w/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "A4a",
        "name": "Verborgene Quelle",
        "releaseDate": "2025-08-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A4a/symbol",
        "serieId": "tcgp",
        "official": 71
    },
    {
        "id": "me01",
        "name": "Mega-Entwicklung",
        "releaseDate": "2025-09-26",
        "logo": "https://assets.tcgdex.net/de/me/me01/logo",
        "symbol": "https://assets.tcgdex.net/univ/me/me01/symbol",
        "serieId": "me",
        "official": 132
    }
]
""",
    "es" to """
[
    {
        "id": "base1",
        "name": "Edición Básica",
        "releaseDate": "1999-01-09",
        "serieId": "base",
        "official": 102
    },
    {
        "id": "base2",
        "name": "Jungla",
        "releaseDate": "1999-06-16",
        "symbol": "https://assets.tcgdex.net/univ/base/base2/symbol",
        "serieId": "base",
        "official": 64
    },
    {
        "id": "basep",
        "name": "Wizards Black Star Promos",
        "releaseDate": "1999-07-01",
        "symbol": "https://assets.tcgdex.net/univ/base/basep/symbol",
        "serieId": "base",
        "official": 53
    },
    {
        "id": "base3",
        "name": "Fósil",
        "releaseDate": "1999-10-10",
        "symbol": "https://assets.tcgdex.net/univ/base/base3/symbol",
        "serieId": "base",
        "official": 62
    },
    {
        "id": "neo1",
        "name": "Neo Genesis",
        "releaseDate": "2000-12-16",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo1/symbol",
        "serieId": "neo",
        "official": 111
    },
    {
        "id": "ex1",
        "name": "Rubí & Zafiro",
        "releaseDate": "2003-07-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex1/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "np",
        "name": "Nintendo Black Star Promos",
        "releaseDate": "2003-10-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/np/symbol",
        "serieId": "pop",
        "official": 40
    },
    {
        "id": "tk-ex-latio",
        "name": "Kit de Entrenador EX (Latios)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latio/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "tk-ex-latia",
        "name": "Kit de Entrenador EX (Latias)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latia/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "exu",
        "name": "EX Fuerzas Ocultas Unown",
        "releaseDate": "2005-08-22",
        "serieId": "ex",
        "official": 28
    },
    {
        "id": "ex10",
        "name": "EX Fuerzas Ocultas",
        "releaseDate": "2005-08-22",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex10/symbol",
        "serieId": "ex",
        "official": 115
    },
    {
        "id": "tk-ex-p",
        "name": "Kit de Entrenador EX (Plusle)",
        "releaseDate": "2006-03-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-p/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "tk-ex-m",
        "name": "Kit de Entrenador EX (Ninun)",
        "releaseDate": "2006-03-01",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "dp1",
        "name": "Diamante & Perla",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp1/symbol",
        "serieId": "dp",
        "official": 130
    },
    {
        "id": "dpp",
        "name": "DP Black Star Promos",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dpp/symbol",
        "serieId": "dp",
        "official": 56
    },
    {
        "id": "dp2",
        "name": "Tesoros Misteriosos",
        "releaseDate": "2007-08-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp2/symbol",
        "serieId": "dp",
        "official": 122
    },
    {
        "id": "tk-dp-l",
        "name": "Kit de Entrenador Diamante & Perla (Lucario)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-l/symbol",
        "serieId": "tk",
        "official": 11
    },
    {
        "id": "tk-dp-m",
        "name": "Kit de Entrenador Diamante & Perla (Manaphy)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-m/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "dp3",
        "name": "Maravillas Secretas",
        "releaseDate": "2007-11-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp3/symbol",
        "serieId": "dp",
        "official": 132
    },
    {
        "id": "dp4",
        "name": "Grandes Encuentros",
        "releaseDate": "2008-02-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp4/symbol",
        "serieId": "dp",
        "official": 106
    },
    {
        "id": "dp6",
        "name": "Despertar de las Leyendas",
        "releaseDate": "2008-08-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp6/symbol",
        "serieId": "dp",
        "official": 146
    },
    {
        "id": "dp7",
        "name": "Frente Tormentoso",
        "releaseDate": "2008-11-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp7/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "pl1",
        "name": "Platino",
        "releaseDate": "2009-02-11",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl1/symbol",
        "serieId": "pl",
        "official": 127
    },
    {
        "id": "pl4",
        "name": "Arceus",
        "releaseDate": "2009-11-04",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl4/symbol",
        "serieId": "pl",
        "official": 99
    },
    {
        "id": "ru1",
        "name": "Pokémon Rumble",
        "releaseDate": "2009-12-02",
        "symbol": "https://assets.tcgdex.net/univ/pl/ru1/symbol",
        "serieId": "pl",
        "official": 16
    },
    {
        "id": "hgss1",
        "name": "HeartGold SoulSilver",
        "releaseDate": "2010-02-10",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss1/symbol",
        "serieId": "hgss",
        "official": 123
    },
    {
        "id": "hgssp",
        "name": "HGSS Black Star Promos",
        "releaseDate": "2010-02-11",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgssp/symbol",
        "serieId": "hgss",
        "official": 25
    },
    {
        "id": "tk-hs-g",
        "name": "Kit de Entrenador HeatGold & SoulSilver (Gyarados)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-g/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-hs-r",
        "name": "Kit de Entrenador HeatGold & SoulSilver (Raichu)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "hgss2",
        "name": "Liberados",
        "releaseDate": "2010-05-12",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss2/symbol",
        "serieId": "hgss",
        "official": 95
    },
    {
        "id": "bw1",
        "name": "Negro y Blanco",
        "releaseDate": "2011-04-25",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw1/symbol",
        "serieId": "bw",
        "official": 114
    },
    {
        "id": "bwp",
        "name": "NB Promo",
        "releaseDate": "2011-04-26",
        "symbol": "https://assets.tcgdex.net/univ/bw/bwp/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "2011bw",
        "name": "Colección de McDonald's 2011",
        "releaseDate": "2011-06-17",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "bw2",
        "name": "Fuerzas Emergentes",
        "releaseDate": "2011-08-31",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw2/symbol",
        "serieId": "bw",
        "official": 98
    },
    {
        "id": "tk-bw-z",
        "name": "Kit de Entrenador XY (Pikachu Enmascarada)",
        "releaseDate": "2011-09-01",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-bw-e",
        "name": "Kit de Entrenador BW (Excadrill)",
        "releaseDate": "2011-09-01",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "bw3",
        "name": "Nobles Victorias",
        "releaseDate": "2011-11-16",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw3/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw4",
        "name": "Próximos Destinos",
        "releaseDate": "2012-02-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw4/symbol",
        "serieId": "bw",
        "official": 99
    },
    {
        "id": "bw5",
        "name": "Oscuros Exploradores",
        "releaseDate": "2012-05-09",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw5/symbol",
        "serieId": "bw",
        "official": 108
    },
    {
        "id": "2012bw",
        "name": "Colección de McDonald's 2012",
        "releaseDate": "2012-06-15",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "bw6",
        "name": "Dragones Majestuosos",
        "releaseDate": "2012-08-15",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw6/symbol",
        "serieId": "bw",
        "official": 124
    },
    {
        "id": "dv1",
        "name": "Tesoro de Dragones",
        "releaseDate": "2012-10-05",
        "symbol": "https://assets.tcgdex.net/univ/bw/dv1/symbol",
        "serieId": "bw",
        "official": 20
    },
    {
        "id": "bw7",
        "name": "Fronteras Cruzadas",
        "releaseDate": "2012-11-07",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw7/symbol",
        "serieId": "bw",
        "official": 149
    },
    {
        "id": "bw8",
        "name": "Tormenta Plasma",
        "releaseDate": "2013-02-06",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw8/symbol",
        "serieId": "bw",
        "official": 135
    },
    {
        "id": "bw9",
        "name": "Glaciación Plasma",
        "releaseDate": "2013-05-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw9/symbol",
        "serieId": "bw",
        "official": 116
    },
    {
        "id": "xyp",
        "name": "XY Cartas de promoción",
        "releaseDate": "2013-10-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xyp/symbol",
        "serieId": "xy",
        "official": 211
    },
    {
        "id": "xy1",
        "name": "XY",
        "releaseDate": "2014-02-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy1/symbol",
        "serieId": "xy",
        "official": 146
    },
    {
        "id": "xya",
        "name": "Cartas alternativas",
        "releaseDate": "2014-02-05",
        "serieId": "xy",
        "official": 6
    },
    {
        "id": "tk-xy-sy",
        "name": "Kit de Entrenador XY (Pikachu Enmascarada)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-sy/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-n",
        "name": "Kit de Entrenador XY (Noivern)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-n/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy2",
        "name": "Destellos de Fuego",
        "releaseDate": "2014-05-07",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy2/symbol",
        "serieId": "xy",
        "official": 106
    },
    {
        "id": "xy3",
        "name": "Puños Furiosos",
        "releaseDate": "2014-08-13",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy3/symbol",
        "serieId": "xy",
        "official": 111
    },
    {
        "id": "tk-xy-b",
        "name": "Kit de Entrenador XY (Bisharp)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-b/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-w",
        "name": "Kit de Entrenador XY (Wigglytuff)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-w/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy4",
        "name": "Fuerzas Fantasmales",
        "releaseDate": "2014-11-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy4/symbol",
        "serieId": "xy",
        "official": 119
    },
    {
        "id": "xy5",
        "name": "Duelos Primigenios",
        "releaseDate": "2015-02-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy5/symbol",
        "serieId": "xy",
        "official": 160
    },
    {
        "id": "tk-xy-latia",
        "name": "Kit de Entrenador XY (Latias)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latia/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-latio",
        "name": "Kit de Entrenador XY (Latios)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latio/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy6",
        "name": "Cielos Rugientes",
        "releaseDate": "2015-05-06",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy6/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "xy7",
        "name": "Antiguos Orígenes",
        "releaseDate": "2015-08-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy7/symbol",
        "serieId": "xy",
        "official": 98
    },
    {
        "id": "xy8",
        "name": "Turbo Impulso",
        "releaseDate": "2015-11-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy8/symbol",
        "serieId": "xy",
        "official": 162
    },
    {
        "id": "xy9",
        "name": "TURBOLímite",
        "releaseDate": "2016-02-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy9/symbol",
        "serieId": "xy",
        "official": 122
    },
    {
        "id": "g1",
        "name": "Generaciones",
        "releaseDate": "2016-02-22",
        "symbol": "https://assets.tcgdex.net/univ/xy/g1/symbol",
        "serieId": "xy",
        "official": 83
    },
    {
        "id": "tk-xy-p",
        "name": "Kit de Entrenador XY (Pikachu Enmascarada)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-p/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-su",
        "name": "Kit de Entrenador XY (Suicune)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-su/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy10",
        "name": "Destinos Enfrentados",
        "releaseDate": "2016-05-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy10/symbol",
        "serieId": "xy",
        "official": 124
    },
    {
        "id": "xy11",
        "name": "Asedio de Vapor",
        "releaseDate": "2016-08-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy11/symbol",
        "serieId": "xy",
        "official": 114
    },
    {
        "id": "xy12",
        "name": "Evoluciones",
        "releaseDate": "2016-11-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy12/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "sm1",
        "name": "Sol y Luna",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm1/symbol",
        "serieId": "sm",
        "official": 149
    },
    {
        "id": "smp",
        "name": "SL Cartas de promoción",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/smp/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "tk-sm-l",
        "name": "Kit de Entrenador Sol y Luna (Lycanroc)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-l/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-sm-r",
        "name": "Kit de Entrenador Sol y Luna (Raichu de Alola)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "sm2",
        "name": "Albor de Guadianes",
        "releaseDate": "2017-05-05",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm2/symbol",
        "serieId": "sm",
        "official": 145
    },
    {
        "id": "sm3",
        "name": "Sombras Ardientes",
        "releaseDate": "2017-08-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm3/symbol",
        "serieId": "sm",
        "official": 147
    },
    {
        "id": "sm3.5",
        "name": "Leyendas Luminosas",
        "releaseDate": "2017-10-06",
        "serieId": "sm",
        "official": 73
    },
    {
        "id": "sm4",
        "name": "Invasión Carmesí",
        "releaseDate": "2017-11-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm4/symbol",
        "serieId": "sm",
        "official": 111
    },
    {
        "id": "sm5",
        "name": "Ultraprisma",
        "releaseDate": "2018-02-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm5/symbol",
        "serieId": "sm",
        "official": 156
    },
    {
        "id": "sm6",
        "name": "Luz Prohibida",
        "releaseDate": "2018-05-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm6/symbol",
        "serieId": "sm",
        "official": 131
    },
    {
        "id": "sm7",
        "name": "Tormenta Celestial",
        "releaseDate": "2018-08-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm7/symbol",
        "serieId": "sm",
        "official": 168
    },
    {
        "id": "sm7.5",
        "name": "Majestad De Dragones",
        "releaseDate": "2018-09-07",
        "serieId": "sm",
        "official": 70
    },
    {
        "id": "sm8",
        "name": "Truenos Perdidos",
        "releaseDate": "2018-11-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm8/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm9",
        "name": "Union de Aliados",
        "releaseDate": "2019-01-31",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm9/symbol",
        "serieId": "sm",
        "official": 181
    },
    {
        "id": "det1",
        "name": "Detective Pikachu",
        "releaseDate": "2019-03-29",
        "symbol": "https://assets.tcgdex.net/univ/sm/det1/symbol",
        "serieId": "sm",
        "official": 18
    },
    {
        "id": "sm10",
        "name": "Vínculos Indestructibles",
        "releaseDate": "2019-05-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm10/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm11",
        "name": "Mentes Unidas",
        "releaseDate": "2019-08-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm11/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "sm115",
        "name": "Destinos Ocultos",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm115/symbol",
        "serieId": "sm",
        "official": 68
    },
    {
        "id": "sma",
        "name": "Cartas alternativas",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sma/symbol",
        "serieId": "sm",
        "official": 94
    },
    {
        "id": "sm12",
        "name": "Eclipse Cósmico",
        "releaseDate": "2019-11-01",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm12/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "swshp",
        "name": "Cartas de promoción ESES",
        "releaseDate": "2019-11-15",
        "logo": "https://assets.tcgdex.net/es/swsh/swshp/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swshp/symbol",
        "serieId": "swsh",
        "official": 107
    },
    {
        "id": "swsh1",
        "name": "Espada y Escudo",
        "releaseDate": "2020-02-07",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh1/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh1/symbol",
        "serieId": "swsh",
        "official": 202
    },
    {
        "id": "swsh2",
        "name": "Choque Rebelde",
        "releaseDate": "2020-05-01",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh2/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh2/symbol",
        "serieId": "swsh",
        "official": 192
    },
    {
        "id": "swsh3",
        "name": "Oscuridad Incandescente",
        "releaseDate": "2020-08-14",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh3/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh3.5",
        "name": "Camino de Campeones",
        "releaseDate": "2020-09-25",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh3.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3.5/symbol",
        "serieId": "swsh",
        "official": 70
    },
    {
        "id": "swsh4",
        "name": "Voltaje Vívido",
        "releaseDate": "2020-11-13",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh4/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4/symbol",
        "serieId": "swsh",
        "official": 185
    },
    {
        "id": "2021swsh",
        "name": "Colección de McDonald's 2021",
        "releaseDate": "2021-02-09",
        "symbol": "https://assets.tcgdex.net/univ/mc/2021swsh/symbol",
        "serieId": "mc",
        "official": 25
    },
    {
        "id": "swsh4.5",
        "name": "Destinos Brillantes",
        "releaseDate": "2021-02-19",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh4.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4.5/symbol",
        "serieId": "swsh",
        "official": 72
    },
    {
        "id": "swsh5",
        "name": "Estilos de Combate",
        "releaseDate": "2021-03-19",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh5/symbol",
        "serieId": "swsh",
        "official": 163
    },
    {
        "id": "swsh6",
        "name": "Reinado Escalofriante",
        "releaseDate": "2021-06-18",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh6/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh6/symbol",
        "serieId": "swsh",
        "official": 198
    },
    {
        "id": "swsh7",
        "name": "Cielos Evolutivos",
        "releaseDate": "2021-08-27",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh7/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh7/symbol",
        "serieId": "swsh",
        "official": 203
    },
    {
        "id": "cel25",
        "name": "Celebraciones",
        "releaseDate": "2021-10-08",
        "logo": "https://assets.tcgdex.net/es/swsh/cel25/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/cel25/symbol",
        "serieId": "swsh",
        "official": 25
    },
    {
        "id": "swsh8",
        "name": "Golpe Fusión",
        "releaseDate": "2021-11-12",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh8/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh8/symbol",
        "serieId": "swsh",
        "official": 264
    },
    {
        "id": "swsh9",
        "name": "Astros Brillantes",
        "releaseDate": "2022-02-25",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh9/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh9/symbol",
        "serieId": "swsh",
        "official": 172
    },
    {
        "id": "swsh10",
        "name": "Resplandor Astral",
        "releaseDate": "2022-05-27",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh10/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh10.5",
        "name": "Pokémon GO",
        "releaseDate": "2022-07-01",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh10.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10.5/symbol",
        "serieId": "swsh",
        "official": 78
    },
    {
        "id": "swsh11",
        "name": "Origen Perdido",
        "releaseDate": "2022-09-09",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh11/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh11/symbol",
        "serieId": "swsh",
        "official": 196
    },
    {
        "id": "swsh12",
        "name": "Tempestad Plateada",
        "releaseDate": "2022-11-11",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh12/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12/symbol",
        "serieId": "swsh",
        "official": 195
    },
    {
        "id": "swsh12.5",
        "name": "Cenit Supremo",
        "releaseDate": "2023-01-20",
        "logo": "https://assets.tcgdex.net/es/swsh/swsh12.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12.5/symbol",
        "serieId": "swsh",
        "official": 159
    },
    {
        "id": "svp",
        "name": "SVP Black Star Promos",
        "releaseDate": "2023-03-31",
        "serieId": "sv",
        "official": 0
    },
    {
        "id": "sv01",
        "name": "Escarlata y Púrpura",
        "releaseDate": "2023-03-31",
        "logo": "https://assets.tcgdex.net/es/sv/sv01/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv01/symbol",
        "serieId": "sv",
        "official": 198
    },
    {
        "id": "sv02",
        "name": "Evoluciones en Paldea",
        "releaseDate": "2023-06-09",
        "logo": "https://assets.tcgdex.net/es/sv/sv02/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv02/symbol",
        "serieId": "sv",
        "official": 193
    },
    {
        "id": "sv03",
        "name": "Llamas Obsidianas",
        "releaseDate": "2023-08-11",
        "logo": "https://assets.tcgdex.net/es/sv/sv03/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03/symbol",
        "serieId": "sv",
        "official": 197
    },
    {
        "id": "sv03.5",
        "name": "151",
        "releaseDate": "2023-09-22",
        "logo": "https://assets.tcgdex.net/es/sv/sv03.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03.5/symbol",
        "serieId": "sv",
        "official": 165
    },
    {
        "id": "sv04",
        "name": "Brecha Paradójica",
        "releaseDate": "2023-11-03",
        "logo": "https://assets.tcgdex.net/es/sv/sv04/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv04.5",
        "name": "Destinos de Paldea",
        "releaseDate": "2024-01-26",
        "logo": "https://assets.tcgdex.net/es/sv/sv04.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04.5/symbol",
        "serieId": "sv",
        "official": 91
    },
    {
        "id": "sv05",
        "name": "Fuerzas Temporales",
        "releaseDate": "2024-03-22",
        "logo": "https://assets.tcgdex.net/es/sv/sv05/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv05/symbol",
        "serieId": "sv",
        "official": 162
    },
    {
        "id": "sv06",
        "name": "Mascarada Crepuscular",
        "releaseDate": "2024-05-24",
        "logo": "https://assets.tcgdex.net/es/sv/sv06/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06/symbol",
        "serieId": "sv",
        "official": 167
    },
    {
        "id": "sv06.5",
        "name": "Fabula Sombría",
        "releaseDate": "2024-08-02",
        "logo": "https://assets.tcgdex.net/es/sv/sv06.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06.5/symbol",
        "serieId": "sv",
        "official": 64
    },
    {
        "id": "sv07",
        "name": "Corona Astral",
        "releaseDate": "2024-09-13",
        "logo": "https://assets.tcgdex.net/es/sv/sv07/logo",
        "serieId": "sv",
        "official": 142
    },
    {
        "id": "A1",
        "name": "Genes Formidables",
        "releaseDate": "2024-10-30",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A1/symbol",
        "serieId": "tcgp",
        "official": 226
    },
    {
        "id": "P-A",
        "name": "Promo-A",
        "releaseDate": "2024-10-30",
        "serieId": "tcgp",
        "official": 0
    },
    {
        "id": "sv08",
        "name": "Chispas Fulgurantes",
        "releaseDate": "2024-11-08",
        "logo": "https://assets.tcgdex.net/es/sv/sv08/logo",
        "serieId": "sv",
        "official": 191
    },
    {
        "id": "A1a",
        "name": "La Isla Singular",
        "releaseDate": "2024-12-17",
        "serieId": "tcgp",
        "official": 68
    },
    {
        "id": "sv08.5",
        "name": "Evoluciones Prismáticas",
        "releaseDate": "2025-01-17",
        "logo": "https://assets.tcgdex.net/es/sv/sv08.5/logo",
        "serieId": "sv",
        "official": 131
    },
    {
        "id": "A2",
        "name": "Pugna Espaciotemporal",
        "releaseDate": "2025-01-30",
        "serieId": "tcgp",
        "official": 140
    },
    {
        "id": "A2a",
        "name": "Luz Triunfal",
        "releaseDate": "2025-02-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2a/symbol",
        "serieId": "tcgp",
        "official": 75
    },
    {
        "id": "A2b",
        "name": "Festival Brillante",
        "releaseDate": "2025-03-27",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2b/symbol",
        "serieId": "tcgp",
        "official": 72
    },
    {
        "id": "sv09",
        "name": "Juntos de Aventuras",
        "releaseDate": "2025-03-28",
        "logo": "https://assets.tcgdex.net/es/sv/sv09/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv09/symbol",
        "serieId": "sv",
        "official": 159
    },
    {
        "id": "A3",
        "name": "Guardianes Celestiales",
        "releaseDate": "2025-04-30",
        "serieId": "tcgp",
        "official": 155
    },
    {
        "id": "sv10",
        "name": "Rivales Predestinados",
        "releaseDate": "2025-05-30",
        "logo": "https://assets.tcgdex.net/es/sv/sv10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv10.5b",
        "name": "Fulgor Negro",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5b/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "sv10.5w",
        "name": "Llama Blanca",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5w/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "A4a",
        "name": "Manantial Oculto",
        "releaseDate": "2025-08-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A4a/symbol",
        "serieId": "tcgp",
        "official": 71
    },
    {
        "id": "me01",
        "name": "Megaevolución",
        "releaseDate": "2025-09-26",
        "logo": "https://assets.tcgdex.net/es/me/me01/logo",
        "symbol": "https://assets.tcgdex.net/univ/me/me01/symbol",
        "serieId": "me",
        "official": 132
    }
]
""",
    "it" to """
[
    {
        "id": "base1",
        "name": "Set Base",
        "releaseDate": "1999-01-09",
        "serieId": "base",
        "official": 102
    },
    {
        "id": "base2",
        "name": "Jungle",
        "releaseDate": "1999-06-16",
        "symbol": "https://assets.tcgdex.net/univ/base/base2/symbol",
        "serieId": "base",
        "official": 64
    },
    {
        "id": "basep",
        "name": "Wizards Black Star Promos",
        "releaseDate": "1999-07-01",
        "symbol": "https://assets.tcgdex.net/univ/base/basep/symbol",
        "serieId": "base",
        "official": 53
    },
    {
        "id": "wp",
        "name": "Carte Promo Wizards",
        "releaseDate": "1999-09-01",
        "serieId": "base",
        "official": 7
    },
    {
        "id": "base3",
        "name": "Fossil",
        "releaseDate": "1999-10-10",
        "symbol": "https://assets.tcgdex.net/univ/base/base3/symbol",
        "serieId": "base",
        "official": 62
    },
    {
        "id": "base5",
        "name": "Team Rocket",
        "releaseDate": "2000-04-24",
        "symbol": "https://assets.tcgdex.net/univ/base/base5/symbol",
        "serieId": "base",
        "official": 82
    },
    {
        "id": "neo1",
        "name": "Neo Genesis",
        "releaseDate": "2000-12-16",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo1/symbol",
        "serieId": "neo",
        "official": 111
    },
    {
        "id": "neo2",
        "name": "Neo Discovery",
        "releaseDate": "2001-06-01",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo2/symbol",
        "serieId": "neo",
        "official": 75
    },
    {
        "id": "neo3",
        "name": "Neo Revelation",
        "releaseDate": "2001-09-21",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo3/symbol",
        "serieId": "neo",
        "official": 64
    },
    {
        "id": "neo4",
        "name": "Neo Destiny",
        "releaseDate": "2002-02-28",
        "symbol": "https://assets.tcgdex.net/univ/neo/neo4/symbol",
        "serieId": "neo",
        "official": 105
    },
    {
        "id": "ecard1",
        "name": "Expedition Set Base",
        "releaseDate": "2002-09-15",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard1/symbol",
        "serieId": "ecard",
        "official": 165
    },
    {
        "id": "ecard2",
        "name": "Aquapolis",
        "releaseDate": "2003-01-15",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard2/symbol",
        "serieId": "ecard",
        "official": 147
    },
    {
        "id": "ecard3",
        "name": "Skyridge",
        "releaseDate": "2003-05-12",
        "symbol": "https://assets.tcgdex.net/univ/ecard/ecard3/symbol",
        "serieId": "ecard",
        "official": 144
    },
    {
        "id": "ex1",
        "name": "EX Rubino & Zaffiro",
        "releaseDate": "2003-07-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex1/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "ex2",
        "name": "EX Tempesta di Sabbia",
        "releaseDate": "2003-09-18",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex2/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "np",
        "name": "Nintendo Black Star Promos",
        "releaseDate": "2003-10-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/np/symbol",
        "serieId": "pop",
        "official": 40
    },
    {
        "id": "ex3",
        "name": "EX Drago",
        "releaseDate": "2003-11-24",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex3/symbol",
        "serieId": "ex",
        "official": 97
    },
    {
        "id": "ex4",
        "name": "EX Team Magma vs Team Idro",
        "releaseDate": "2004-03-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex4/symbol",
        "serieId": "ex",
        "official": 95
    },
    {
        "id": "ex5",
        "name": "EX Leggende Nascoste",
        "releaseDate": "2004-06-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex5/symbol",
        "serieId": "ex",
        "official": 101
    },
    {
        "id": "tk-ex-latio",
        "name": "EX trainer Kit (Latios)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latio/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "tk-ex-latia",
        "name": "EX trainer Kit (Latias)",
        "releaseDate": "2004-07-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-latia/symbol",
        "serieId": "tk",
        "official": 10
    },
    {
        "id": "ex6",
        "name": "EX RossoFuoco e VerdeFoglia",
        "releaseDate": "2004-09-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex6/symbol",
        "serieId": "ex",
        "official": 112
    },
    {
        "id": "pop1",
        "name": "POP Serie 1",
        "releaseDate": "2004-09-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop1/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex8",
        "name": "EX Deoxys",
        "releaseDate": "2005-02-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex8/symbol",
        "serieId": "ex",
        "official": 107
    },
    {
        "id": "ex9",
        "name": "EX Smeraldo",
        "releaseDate": "2005-05-09",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex9/symbol",
        "serieId": "ex",
        "official": 106
    },
    {
        "id": "pop2",
        "name": "POP Serie 2",
        "releaseDate": "2005-08-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop2/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "exu",
        "name": "EX Forze Segrete Unown",
        "releaseDate": "2005-08-22",
        "serieId": "ex",
        "official": 28
    },
    {
        "id": "ex10",
        "name": "EX Forze Segrete",
        "releaseDate": "2005-08-22",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex10/symbol",
        "serieId": "ex",
        "official": 115
    },
    {
        "id": "ex11",
        "name": "EX Specie Delta",
        "releaseDate": "2005-10-31",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex11/symbol",
        "serieId": "ex",
        "official": 113
    },
    {
        "id": "ex12",
        "name": "EX La Leggenda di Mew",
        "releaseDate": "2006-02-13",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex12/symbol",
        "serieId": "ex",
        "official": 92
    },
    {
        "id": "tk-ex-p",
        "name": "EX trainer Kit (Plusle)",
        "releaseDate": "2006-03-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-ex-p/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "tk-ex-m",
        "name": "EX trainer Kit (Ninun)",
        "releaseDate": "2006-03-01",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "pop3",
        "name": "POP Serie 3",
        "releaseDate": "2006-04-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop3/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex13",
        "name": "EX Fantasmi di Holon",
        "releaseDate": "2006-05-03",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex13/symbol",
        "serieId": "ex",
        "official": 110
    },
    {
        "id": "pop4",
        "name": "POP Serie 4",
        "releaseDate": "2006-08-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop4/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "ex14",
        "name": "EX Guardiani dei Cristalli",
        "releaseDate": "2006-08-30",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex14/symbol",
        "serieId": "ex",
        "official": 100
    },
    {
        "id": "ex15",
        "name": "EX L'Isola dei Draghi",
        "releaseDate": "2006-11-08",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex15/symbol",
        "serieId": "ex",
        "official": 101
    },
    {
        "id": "pop5",
        "name": "POP Serie 5",
        "releaseDate": "2007-03-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop5/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "dp1",
        "name": "Diamante & Perla",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp1/symbol",
        "serieId": "dp",
        "official": 130
    },
    {
        "id": "dpp",
        "name": "DP Black Star Promos",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dpp/symbol",
        "serieId": "dp",
        "official": 56
    },
    {
        "id": "dp2",
        "name": "Tesori Misteriosi",
        "releaseDate": "2007-08-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp2/symbol",
        "serieId": "dp",
        "official": 122
    },
    {
        "id": "tk-dp-l",
        "name": "Diamond & Pearl trainer Kit (Lucario)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-l/symbol",
        "serieId": "tk",
        "official": 11
    },
    {
        "id": "tk-dp-m",
        "name": "Diamond & Pearl trainer Kit (Manaphy)",
        "releaseDate": "2007-09-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-dp-m/symbol",
        "serieId": "tk",
        "official": 12
    },
    {
        "id": "dp3",
        "name": "Prodigi Segreti",
        "releaseDate": "2007-11-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp3/symbol",
        "serieId": "dp",
        "official": 132
    },
    {
        "id": "dp4",
        "name": "Incontri Leggendari",
        "releaseDate": "2008-02-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp4/symbol",
        "serieId": "dp",
        "official": 106
    },
    {
        "id": "pop7",
        "name": "POP Serie 7",
        "releaseDate": "2008-03-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop7/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "dp5",
        "name": "Alba Suprema",
        "releaseDate": "2008-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp5/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "dp6",
        "name": "Il Risveglio dei Miti",
        "releaseDate": "2008-08-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp6/symbol",
        "serieId": "dp",
        "official": 146
    },
    {
        "id": "dp7",
        "name": "Fronte di Tempesta",
        "releaseDate": "2008-11-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp7/symbol",
        "serieId": "dp",
        "official": 100
    },
    {
        "id": "pl1",
        "name": "Platino",
        "releaseDate": "2009-02-11",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl1/symbol",
        "serieId": "pl",
        "official": 127
    },
    {
        "id": "pop9",
        "name": "POP Serie 9",
        "releaseDate": "2009-03-01",
        "symbol": "https://assets.tcgdex.net/univ/pop/pop9/symbol",
        "serieId": "pop",
        "official": 17
    },
    {
        "id": "pl2",
        "name": "L'Ascesa dei Rivali",
        "releaseDate": "2009-05-16",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl2/symbol",
        "serieId": "pl",
        "official": 111
    },
    {
        "id": "pl4",
        "name": "Arceus",
        "releaseDate": "2009-11-04",
        "symbol": "https://assets.tcgdex.net/univ/pl/pl4/symbol",
        "serieId": "pl",
        "official": 99
    },
    {
        "id": "hgss1",
        "name": "HeartGold SoulSilver",
        "releaseDate": "2010-02-10",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss1/symbol",
        "serieId": "hgss",
        "official": 123
    },
    {
        "id": "hgssp",
        "name": "HGSS Promo",
        "releaseDate": "2010-02-11",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgssp/symbol",
        "serieId": "hgss",
        "official": 25
    },
    {
        "id": "tk-hs-g",
        "name": "HS trainer Kit (Gyarados)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-g/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-hs-r",
        "name": "HS trainer Kit (Raichu)",
        "releaseDate": "2010-05-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-hs-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "hgss2",
        "name": "Forze Scatenate",
        "releaseDate": "2010-05-12",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss2/symbol",
        "serieId": "hgss",
        "official": 95
    },
    {
        "id": "hgss3",
        "name": "Senza Paura",
        "releaseDate": "2010-08-18",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss3/symbol",
        "serieId": "hgss",
        "official": 90
    },
    {
        "id": "hgss4",
        "name": "Battaglie Trionfali",
        "releaseDate": "2010-11-03",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss4/symbol",
        "serieId": "hgss",
        "official": 102
    },
    {
        "id": "col1",
        "name": "Richiamo delle Leggende",
        "releaseDate": "2011-02-09",
        "symbol": "https://assets.tcgdex.net/univ/col/col1/symbol",
        "serieId": "col",
        "official": 95
    },
    {
        "id": "bw1",
        "name": "Nero e Bianco",
        "releaseDate": "2011-04-25",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw1/symbol",
        "serieId": "bw",
        "official": 114
    },
    {
        "id": "bwp",
        "name": "NB Promo",
        "releaseDate": "2011-04-26",
        "symbol": "https://assets.tcgdex.net/univ/bw/bwp/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "2011bw",
        "name": "McDonald's Collection",
        "releaseDate": "2011-06-17",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "bw2",
        "name": "Nuove Forze",
        "releaseDate": "2011-08-31",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw2/symbol",
        "serieId": "bw",
        "official": 98
    },
    {
        "id": "bw3",
        "name": "Vittorie Regali",
        "releaseDate": "2011-11-16",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw3/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw4",
        "name": "Destini Futuri",
        "releaseDate": "2012-02-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw4/symbol",
        "serieId": "bw",
        "official": 99
    },
    {
        "id": "bw5",
        "name": "Esploratori delle Tenebre",
        "releaseDate": "2012-05-09",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw5/symbol",
        "serieId": "bw",
        "official": 108
    },
    {
        "id": "2012bw",
        "name": "McDonald's Collection 2012",
        "releaseDate": "2012-06-15",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "bw6",
        "name": "Stirpe dei Draghi",
        "releaseDate": "2012-08-15",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw6/symbol",
        "serieId": "bw",
        "official": 124
    },
    {
        "id": "dv1",
        "name": "Tesoro dei Draghi",
        "releaseDate": "2012-10-05",
        "symbol": "https://assets.tcgdex.net/univ/bw/dv1/symbol",
        "serieId": "bw",
        "official": 20
    },
    {
        "id": "bw7",
        "name": "Confini Varcati",
        "releaseDate": "2012-11-07",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw7/symbol",
        "serieId": "bw",
        "official": 149
    },
    {
        "id": "bw8",
        "name": "Uragano Plasma",
        "releaseDate": "2013-02-06",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw8/symbol",
        "serieId": "bw",
        "official": 135
    },
    {
        "id": "bw9",
        "name": "Glaciazione Plasma",
        "releaseDate": "2013-05-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw9/symbol",
        "serieId": "bw",
        "official": 116
    },
    {
        "id": "xyp",
        "name": "XY Promo",
        "releaseDate": "2013-10-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xyp/symbol",
        "serieId": "xy",
        "official": 211
    },
    {
        "id": "xy0",
        "name": "Benvenuti a Kalos",
        "releaseDate": "2013-11-08",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy0/symbol",
        "serieId": "xy",
        "official": 39
    },
    {
        "id": "xy1",
        "name": "XY",
        "releaseDate": "2014-02-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy1/symbol",
        "serieId": "xy",
        "official": 146
    },
    {
        "id": "xya",
        "name": "Carta Alternatica A Gialla",
        "releaseDate": "2014-02-05",
        "serieId": "xy",
        "official": 6
    },
    {
        "id": "tk-xy-sy",
        "name": "XY trainer Kit (Sylveon)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-sy/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-n",
        "name": "XY trainer Kit (Noivern)",
        "releaseDate": "2014-03-12",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-n/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy2",
        "name": "Fuoco Infernale",
        "releaseDate": "2014-05-07",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy2/symbol",
        "serieId": "xy",
        "official": 106
    },
    {
        "id": "2014xy",
        "name": "McDonald's Collection 2014",
        "releaseDate": "2014-05-23",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy3",
        "name": "Colpi Furiosi",
        "releaseDate": "2014-08-13",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy3/symbol",
        "serieId": "xy",
        "official": 111
    },
    {
        "id": "tk-xy-b",
        "name": "XY trainer Kit (Bisharp)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-b/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-w",
        "name": "XY trainer Kit (Wigglytuff)",
        "releaseDate": "2014-11-01",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-w/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy4",
        "name": "Forze Spettrali",
        "releaseDate": "2014-11-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy4/symbol",
        "serieId": "xy",
        "official": 119
    },
    {
        "id": "xy5",
        "name": "Scontro Primordiale",
        "releaseDate": "2015-02-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy5/symbol",
        "serieId": "xy",
        "official": 160
    },
    {
        "id": "tk-xy-latia",
        "name": "XY trainer Kit (Latias)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latia/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-latio",
        "name": "XY trainer Kit (Latios)",
        "releaseDate": "2015-04-29",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-latio/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy6",
        "name": "Furie Volanti",
        "releaseDate": "2015-05-06",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy6/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "xy7",
        "name": "Antiche Origini",
        "releaseDate": "2015-08-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy7/symbol",
        "serieId": "xy",
        "official": 98
    },
    {
        "id": "xy8",
        "name": "Turbo Blitz",
        "releaseDate": "2015-11-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy8/symbol",
        "serieId": "xy",
        "official": 162
    },
    {
        "id": "2015xy",
        "name": "McDonald's Collection 2015",
        "releaseDate": "2015-11-27",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy9",
        "name": "Turbo Crash",
        "releaseDate": "2016-02-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy9/symbol",
        "serieId": "xy",
        "official": 122
    },
    {
        "id": "g1",
        "name": "Generazioni",
        "releaseDate": "2016-02-22",
        "symbol": "https://assets.tcgdex.net/univ/xy/g1/symbol",
        "serieId": "xy",
        "official": 83
    },
    {
        "id": "tk-xy-p",
        "name": "XY trainer Kit (Pikachu Libre)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-p/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-xy-su",
        "name": "XY trainer Kit (Suicune)",
        "releaseDate": "2016-04-27",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-xy-su/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "xy10",
        "name": "Destini Incrociati",
        "releaseDate": "2016-05-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy10/symbol",
        "serieId": "xy",
        "official": 124
    },
    {
        "id": "xy11",
        "name": "Vapori Accesi",
        "releaseDate": "2016-08-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy11/symbol",
        "serieId": "xy",
        "official": 114
    },
    {
        "id": "2016xy",
        "name": "McDonald's Collection 2016",
        "releaseDate": "2016-08-20",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "xy12",
        "name": "Evoluzioni",
        "releaseDate": "2016-11-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy12/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "sm1",
        "name": "Sole e Luna",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm1/symbol",
        "serieId": "sm",
        "official": 149
    },
    {
        "id": "smp",
        "name": "SL Promo",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/smp/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "tk-sm-l",
        "name": "Sole e Luna trainer Kit (Lycanroc)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-l/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "tk-sm-r",
        "name": "Sole e Luna trainer Kit (Alolan Raichu)",
        "releaseDate": "2017-04-21",
        "symbol": "https://assets.tcgdex.net/univ/tk/tk-sm-r/symbol",
        "serieId": "tk",
        "official": 30
    },
    {
        "id": "sm2",
        "name": "Guardiani Nascenti",
        "releaseDate": "2017-05-05",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm2/symbol",
        "serieId": "sm",
        "official": 145
    },
    {
        "id": "2017sm",
        "name": "McDonald's Collection 2017",
        "releaseDate": "2017-08-03",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "sm3",
        "name": "Ombre Infuocate",
        "releaseDate": "2017-08-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm3/symbol",
        "serieId": "sm",
        "official": 147
    },
    {
        "id": "sm3.5",
        "name": "Leggende Iridescenti",
        "releaseDate": "2017-10-06",
        "serieId": "sm",
        "official": 73
    },
    {
        "id": "sm4",
        "name": "Invasione Scarlatta",
        "releaseDate": "2017-11-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm4/symbol",
        "serieId": "sm",
        "official": 111
    },
    {
        "id": "sm5",
        "name": "Ultraprisma",
        "releaseDate": "2018-02-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm5/symbol",
        "serieId": "sm",
        "official": 156
    },
    {
        "id": "sm6",
        "name": "Apocalisse Di Luce",
        "releaseDate": "2018-05-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm6/symbol",
        "serieId": "sm",
        "official": 131
    },
    {
        "id": "sm7",
        "name": "Tempesta Astrale",
        "releaseDate": "2018-08-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm7/symbol",
        "serieId": "sm",
        "official": 168
    },
    {
        "id": "sm7.5",
        "name": "Trionfo Dei Draghi",
        "releaseDate": "2018-09-07",
        "serieId": "sm",
        "official": 70
    },
    {
        "id": "2018sm",
        "name": "McDonald's Collection 2018",
        "releaseDate": "2018-10-19",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "sm8",
        "name": "Tuoni Perduti",
        "releaseDate": "2018-11-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm8/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm9",
        "name": "Gioco di Squadra",
        "releaseDate": "2019-01-31",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm9/symbol",
        "serieId": "sm",
        "official": 181
    },
    {
        "id": "det1",
        "name": "Detective Pikachu",
        "releaseDate": "2019-03-29",
        "symbol": "https://assets.tcgdex.net/univ/sm/det1/symbol",
        "serieId": "sm",
        "official": 18
    },
    {
        "id": "sm10",
        "name": "Legami Inossidabili",
        "releaseDate": "2019-05-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm10/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm11",
        "name": "Sintonia Mentale",
        "releaseDate": "2019-08-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm11/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "sm115",
        "name": "Destino Sfuggente",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm115/symbol",
        "serieId": "sm",
        "official": 68
    },
    {
        "id": "sma",
        "name": "Carta Alternativa A Gialla",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sma/symbol",
        "serieId": "sm",
        "official": 94
    },
    {
        "id": "2019sm",
        "name": "McDonald's Collection 2019",
        "releaseDate": "2019-10-15",
        "serieId": "mc",
        "official": 12
    },
    {
        "id": "sm12",
        "name": "Eclissi Cosmica",
        "releaseDate": "2019-11-01",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm12/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "swshp",
        "name": "SPSC Promo",
        "releaseDate": "2019-11-15",
        "logo": "https://assets.tcgdex.net/it/swsh/swshp/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swshp/symbol",
        "serieId": "swsh",
        "official": 107
    },
    {
        "id": "swsh1",
        "name": "Spada e Scudo",
        "releaseDate": "2020-02-07",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh1/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh1/symbol",
        "serieId": "swsh",
        "official": 202
    },
    {
        "id": "swsh2",
        "name": "Fragore Ribelle",
        "releaseDate": "2020-05-01",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh2/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh2/symbol",
        "serieId": "swsh",
        "official": 192
    },
    {
        "id": "swsh3",
        "name": "Fiamme Oscure",
        "releaseDate": "2020-08-14",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh3/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh3.5",
        "name": "Futuri Campioni",
        "releaseDate": "2020-09-25",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh3.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3.5/symbol",
        "serieId": "swsh",
        "official": 70
    },
    {
        "id": "swsh4",
        "name": "Voltaggio Sfolgorante",
        "releaseDate": "2020-11-13",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh4/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4/symbol",
        "serieId": "swsh",
        "official": 185
    },
    {
        "id": "2021swsh",
        "name": "McDonald's Collection 2021",
        "releaseDate": "2021-02-09",
        "symbol": "https://assets.tcgdex.net/univ/mc/2021swsh/symbol",
        "serieId": "mc",
        "official": 25
    },
    {
        "id": "swsh4.5",
        "name": "Destino Splendente",
        "releaseDate": "2021-02-19",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh4.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4.5/symbol",
        "serieId": "swsh",
        "official": 72
    },
    {
        "id": "swsh5",
        "name": "Stili di Lotta",
        "releaseDate": "2021-03-19",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh5/symbol",
        "serieId": "swsh",
        "official": 163
    },
    {
        "id": "swsh6",
        "name": "Regno Glaciale",
        "releaseDate": "2021-06-18",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh6/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh6/symbol",
        "serieId": "swsh",
        "official": 198
    },
    {
        "id": "swsh7",
        "name": "Evoluzioni Eteree",
        "releaseDate": "2021-08-27",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh7/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh7/symbol",
        "serieId": "swsh",
        "official": 203
    },
    {
        "id": "cel25",
        "name": "Gran Festa",
        "releaseDate": "2021-10-08",
        "logo": "https://assets.tcgdex.net/it/swsh/cel25/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/cel25/symbol",
        "serieId": "swsh",
        "official": 25
    },
    {
        "id": "swsh8",
        "name": "Colpo Fusione",
        "releaseDate": "2021-11-12",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh8/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh8/symbol",
        "serieId": "swsh",
        "official": 264
    },
    {
        "id": "swsh9",
        "name": "Astri Lucenti",
        "releaseDate": "2022-02-25",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh9/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh9/symbol",
        "serieId": "swsh",
        "official": 172
    },
    {
        "id": "swsh10",
        "name": "Lucentezza Siderale",
        "releaseDate": "2022-05-27",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh10/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh10.5",
        "name": "Pokémon GO",
        "releaseDate": "2022-07-01",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh10.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10.5/symbol",
        "serieId": "swsh",
        "official": 78
    },
    {
        "id": "swsh11",
        "name": "Origine Perduta",
        "releaseDate": "2022-09-09",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh11/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh11/symbol",
        "serieId": "swsh",
        "official": 196
    },
    {
        "id": "swsh12",
        "name": "Tempesta Argentata",
        "releaseDate": "2022-11-11",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh12/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12/symbol",
        "serieId": "swsh",
        "official": 195
    },
    {
        "id": "swsh12.5",
        "name": "Zenit Regale",
        "releaseDate": "2023-01-20",
        "logo": "https://assets.tcgdex.net/it/swsh/swsh12.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12.5/symbol",
        "serieId": "swsh",
        "official": 159
    },
    {
        "id": "svp",
        "name": "SVP Black Star Promos",
        "releaseDate": "2023-03-31",
        "serieId": "sv",
        "official": 0
    },
    {
        "id": "sv01",
        "name": "Scarlatto e Violetto",
        "releaseDate": "2023-03-31",
        "logo": "https://assets.tcgdex.net/it/sv/sv01/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv01/symbol",
        "serieId": "sv",
        "official": 198
    },
    {
        "id": "sv02",
        "name": "Evoluzioni a Paldea",
        "releaseDate": "2023-06-09",
        "logo": "https://assets.tcgdex.net/it/sv/sv02/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv02/symbol",
        "serieId": "sv",
        "official": 193
    },
    {
        "id": "sv03",
        "name": "Ossidiana Infuocata",
        "releaseDate": "2023-08-11",
        "logo": "https://assets.tcgdex.net/it/sv/sv03/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03/symbol",
        "serieId": "sv",
        "official": 197
    },
    {
        "id": "sv03.5",
        "name": "151",
        "releaseDate": "2023-09-22",
        "logo": "https://assets.tcgdex.net/it/sv/sv03.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03.5/symbol",
        "serieId": "sv",
        "official": 165
    },
    {
        "id": "sv04",
        "name": "Paradosso Temporale",
        "releaseDate": "2023-11-03",
        "logo": "https://assets.tcgdex.net/it/sv/sv04/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv04.5",
        "name": "Destino di Paldea",
        "releaseDate": "2024-01-26",
        "logo": "https://assets.tcgdex.net/it/sv/sv04.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04.5/symbol",
        "serieId": "sv",
        "official": 91
    },
    {
        "id": "sv05",
        "name": "Cronoforze",
        "releaseDate": "2024-03-22",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv05/symbol",
        "serieId": "sv",
        "official": 162
    },
    {
        "id": "sv06",
        "name": "Crepuscolo Mascherato",
        "releaseDate": "2024-05-24",
        "logo": "https://assets.tcgdex.net/it/sv/sv06/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06/symbol",
        "serieId": "sv",
        "official": 167
    },
    {
        "id": "sv06.5",
        "name": "Segreto Fiabesco",
        "releaseDate": "2024-08-02",
        "logo": "https://assets.tcgdex.net/it/sv/sv06.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06.5/symbol",
        "serieId": "sv",
        "official": 64
    },
    {
        "id": "sv07",
        "name": "Corona Astrale",
        "releaseDate": "2024-09-13",
        "logo": "https://assets.tcgdex.net/it/sv/sv07/logo",
        "serieId": "sv",
        "official": 142
    },
    {
        "id": "A1",
        "name": "Geni Supremi",
        "releaseDate": "2024-10-30",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A1/symbol",
        "serieId": "tcgp",
        "official": 226
    },
    {
        "id": "P-A",
        "name": "Promo-A",
        "releaseDate": "2024-10-30",
        "serieId": "tcgp",
        "official": 0
    },
    {
        "id": "sv08",
        "name": "Scintille Folgoranti",
        "releaseDate": "2024-11-08",
        "logo": "https://assets.tcgdex.net/it/sv/sv08/logo",
        "serieId": "sv",
        "official": 191
    },
    {
        "id": "A1a",
        "name": "L'Isola Misteriosa",
        "releaseDate": "2024-12-17",
        "serieId": "tcgp",
        "official": 68
    },
    {
        "id": "sv08.5",
        "name": "Evoluzioni Prismatiche",
        "releaseDate": "2025-01-17",
        "logo": "https://assets.tcgdex.net/it/sv/sv08.5/logo",
        "serieId": "sv",
        "official": 131
    },
    {
        "id": "A2",
        "name": "Scontro Spaziotemporale",
        "releaseDate": "2025-01-30",
        "serieId": "tcgp",
        "official": 140
    },
    {
        "id": "A2a",
        "name": "Luce Trionfale",
        "releaseDate": "2025-02-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2a/symbol",
        "serieId": "tcgp",
        "official": 75
    },
    {
        "id": "A2b",
        "name": "Tripudio Splendente",
        "releaseDate": "2025-03-27",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2b/symbol",
        "serieId": "tcgp",
        "official": 72
    },
    {
        "id": "sv09",
        "name": "Avventure Insieme",
        "releaseDate": "2025-03-28",
        "logo": "https://assets.tcgdex.net/it/sv/sv09/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv09/symbol",
        "serieId": "sv",
        "official": 159
    },
    {
        "id": "A3",
        "name": "Guardiani Astrali",
        "releaseDate": "2025-04-30",
        "serieId": "tcgp",
        "official": 155
    },
    {
        "id": "sv10",
        "name": "Rivali Predestinati",
        "releaseDate": "2025-05-30",
        "logo": "https://assets.tcgdex.net/it/sv/sv10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv10.5b",
        "name": "Luce Nera",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5b/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "sv10.5w",
        "name": "Fuoco Bianco",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5w/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "A4a",
        "name": "Sorgenti Recondite",
        "releaseDate": "2025-08-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A4a/symbol",
        "serieId": "tcgp",
        "official": 71
    },
    {
        "id": "me01",
        "name": "Megaevoluzione",
        "releaseDate": "2025-09-26",
        "logo": "https://assets.tcgdex.net/it/me/me01/logo",
        "symbol": "https://assets.tcgdex.net/univ/me/me01/symbol",
        "serieId": "me",
        "official": 132
    }
]
""",
    "pt" to """
[
    {
        "id": "base1",
        "name": "Coleção Básica",
        "releaseDate": "1999-01-09",
        "serieId": "base",
        "official": 102
    },
    {
        "id": "base2",
        "name": "Selva",
        "releaseDate": "1999-06-16",
        "symbol": "https://assets.tcgdex.net/univ/base/base2/symbol",
        "serieId": "base",
        "official": 64
    },
    {
        "id": "base3",
        "name": "Fóssil",
        "releaseDate": "1999-10-10",
        "symbol": "https://assets.tcgdex.net/univ/base/base3/symbol",
        "serieId": "base",
        "official": 62
    },
    {
        "id": "ex1",
        "name": "EX Rubi e Safira",
        "releaseDate": "2003-07-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex1/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "ex7",
        "name": "EX O Retorno da Equipe Rocket ",
        "releaseDate": "2004-11-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex7/symbol",
        "serieId": "ex",
        "official": 109
    },
    {
        "id": "ex8",
        "name": "EX Deoxys",
        "releaseDate": "2005-02-01",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex8/symbol",
        "serieId": "ex",
        "official": 107
    },
    {
        "id": "ex9",
        "name": "EX Esmeralda",
        "releaseDate": "2005-05-09",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex9/symbol",
        "serieId": "ex",
        "official": 106
    },
    {
        "id": "ex10",
        "name": "EX Forças Ocultas",
        "releaseDate": "2005-08-22",
        "symbol": "https://assets.tcgdex.net/univ/ex/ex10/symbol",
        "serieId": "ex",
        "official": 115
    },
    {
        "id": "dp1",
        "name": "Diamante & Pérola",
        "releaseDate": "2007-05-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp1/symbol",
        "serieId": "dp",
        "official": 130
    },
    {
        "id": "dp2",
        "name": "Tesouros Misteriosos",
        "releaseDate": "2007-08-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp2/symbol",
        "serieId": "dp",
        "official": 122
    },
    {
        "id": "dp3",
        "name": "Maravilhas Secretas",
        "releaseDate": "2007-11-01",
        "symbol": "https://assets.tcgdex.net/univ/dp/dp3/symbol",
        "serieId": "dp",
        "official": 132
    },
    {
        "id": "hgss1",
        "name": "HeartGold SoulSilver",
        "releaseDate": "2010-02-10",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss1/symbol",
        "serieId": "hgss",
        "official": 123
    },
    {
        "id": "hgss2",
        "name": "Revelado",
        "releaseDate": "2010-05-12",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss2/symbol",
        "serieId": "hgss",
        "official": 95
    },
    {
        "id": "hgss3",
        "name": "Destemido",
        "releaseDate": "2010-08-18",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss3/symbol",
        "serieId": "hgss",
        "official": 90
    },
    {
        "id": "hgss4",
        "name": "Triunfante",
        "releaseDate": "2010-11-03",
        "symbol": "https://assets.tcgdex.net/univ/hgss/hgss4/symbol",
        "serieId": "hgss",
        "official": 102
    },
    {
        "id": "col1",
        "name": "Chamado das Lendas",
        "releaseDate": "2011-02-09",
        "symbol": "https://assets.tcgdex.net/univ/col/col1/symbol",
        "serieId": "col",
        "official": 95
    },
    {
        "id": "bw1",
        "name": "Black & White",
        "releaseDate": "2011-04-25",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw1/symbol",
        "serieId": "bw",
        "official": 114
    },
    {
        "id": "bw2",
        "name": "Poderes Emergentes",
        "releaseDate": "2011-08-31",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw2/symbol",
        "serieId": "bw",
        "official": 98
    },
    {
        "id": "bw3",
        "name": "Vitórias Nobres",
        "releaseDate": "2011-11-16",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw3/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw4",
        "name": "Próximos Destinos",
        "releaseDate": "2012-02-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw4/symbol",
        "serieId": "bw",
        "official": 99
    },
    {
        "id": "bw5",
        "name": "Exploradores da Escuridão",
        "releaseDate": "2012-05-09",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw5/symbol",
        "serieId": "bw",
        "official": 108
    },
    {
        "id": "bw6",
        "name": "Dragões Enaltecidos",
        "releaseDate": "2012-08-15",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw6/symbol",
        "serieId": "bw",
        "official": 124
    },
    {
        "id": "dv1",
        "name": "Cofre do Dragão",
        "releaseDate": "2012-10-05",
        "symbol": "https://assets.tcgdex.net/univ/bw/dv1/symbol",
        "serieId": "bw",
        "official": 20
    },
    {
        "id": "bw7",
        "name": "Fronteiras Cruzadas",
        "releaseDate": "2012-11-07",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw7/symbol",
        "serieId": "bw",
        "official": 149
    },
    {
        "id": "bw8",
        "name": "Tempestade de Plasma",
        "releaseDate": "2013-02-06",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw8/symbol",
        "serieId": "bw",
        "official": 135
    },
    {
        "id": "bw9",
        "name": "Congelamento de Plasma",
        "releaseDate": "2013-05-08",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw9/symbol",
        "serieId": "bw",
        "official": 116
    },
    {
        "id": "bw10",
        "name": "Explosão de Plasma",
        "releaseDate": "2013-08-14",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw10/symbol",
        "serieId": "bw",
        "official": 101
    },
    {
        "id": "bw11",
        "name": "Tesouros Lendários",
        "releaseDate": "2013-11-06",
        "symbol": "https://assets.tcgdex.net/univ/bw/bw11/symbol",
        "serieId": "bw",
        "official": 113
    },
    {
        "id": "xy0",
        "name": "Conjunto para Iniciantes Kalos",
        "releaseDate": "2013-11-08",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy0/symbol",
        "serieId": "xy",
        "official": 39
    },
    {
        "id": "xy1",
        "name": "XY",
        "releaseDate": "2014-02-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy1/symbol",
        "serieId": "xy",
        "official": 146
    },
    {
        "id": "xy2",
        "name": "Flash de Fogo",
        "releaseDate": "2014-05-07",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy2/symbol",
        "serieId": "xy",
        "official": 106
    },
    {
        "id": "xy3",
        "name": "Punhos Furiosos",
        "releaseDate": "2014-08-13",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy3/symbol",
        "serieId": "xy",
        "official": 111
    },
    {
        "id": "xy4",
        "name": "Força Fantasma",
        "releaseDate": "2014-11-05",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy4/symbol",
        "serieId": "xy",
        "official": 119
    },
    {
        "id": "xy5",
        "name": "Conflito Primitivo",
        "releaseDate": "2015-02-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy5/symbol",
        "serieId": "xy",
        "official": 160
    },
    {
        "id": "dc1",
        "name": "Crise Dupla",
        "releaseDate": "2015-03-25",
        "symbol": "https://assets.tcgdex.net/univ/xy/dc1/symbol",
        "serieId": "xy",
        "official": 34
    },
    {
        "id": "xy6",
        "name": "Céus Estrondosos",
        "releaseDate": "2015-05-06",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy6/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "xy7",
        "name": "Origens Ancestrais",
        "releaseDate": "2015-08-12",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy7/symbol",
        "serieId": "xy",
        "official": 98
    },
    {
        "id": "xy8",
        "name": "Turbo Revolução",
        "releaseDate": "2015-11-04",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy8/symbol",
        "serieId": "xy",
        "official": 162
    },
    {
        "id": "xy9",
        "name": "Turbo Colisão",
        "releaseDate": "2016-02-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy9/symbol",
        "serieId": "xy",
        "official": 122
    },
    {
        "id": "g1",
        "name": "Gerações",
        "releaseDate": "2016-02-22",
        "symbol": "https://assets.tcgdex.net/univ/xy/g1/symbol",
        "serieId": "xy",
        "official": 83
    },
    {
        "id": "xy10",
        "name": "Fusão de Destinos",
        "releaseDate": "2016-05-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy10/symbol",
        "serieId": "xy",
        "official": 124
    },
    {
        "id": "xy11",
        "name": "Cerco de Vapor",
        "releaseDate": "2016-08-03",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy11/symbol",
        "serieId": "xy",
        "official": 114
    },
    {
        "id": "xy12",
        "name": "Evoluções",
        "releaseDate": "2016-11-02",
        "symbol": "https://assets.tcgdex.net/univ/xy/xy12/symbol",
        "serieId": "xy",
        "official": 108
    },
    {
        "id": "sm1",
        "name": "Sol e Lua",
        "releaseDate": "2017-02-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm1/symbol",
        "serieId": "sm",
        "official": 149
    },
    {
        "id": "sm2",
        "name": "Guardiões Ascendentes",
        "releaseDate": "2017-05-05",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm2/symbol",
        "serieId": "sm",
        "official": 145
    },
    {
        "id": "sm3",
        "name": "Sombras Ardentes",
        "releaseDate": "2017-08-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm3/symbol",
        "serieId": "sm",
        "official": 147
    },
    {
        "id": "sm3.5",
        "name": "Lendas Luminescentes",
        "releaseDate": "2017-10-06",
        "serieId": "sm",
        "official": 73
    },
    {
        "id": "sm4",
        "name": "Invasão Carmim",
        "releaseDate": "2017-11-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm4/symbol",
        "serieId": "sm",
        "official": 111
    },
    {
        "id": "sm5",
        "name": "Ultra Prisma",
        "releaseDate": "2018-02-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm5/symbol",
        "serieId": "sm",
        "official": 156
    },
    {
        "id": "sm6",
        "name": "Luz Proibida",
        "releaseDate": "2018-05-04",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm6/symbol",
        "serieId": "sm",
        "official": 131
    },
    {
        "id": "sm7",
        "name": "Tempestade Celestial",
        "releaseDate": "2018-08-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm7/symbol",
        "serieId": "sm",
        "official": 168
    },
    {
        "id": "sm7.5",
        "name": "Dragões Soberanos",
        "releaseDate": "2018-09-07",
        "serieId": "sm",
        "official": 70
    },
    {
        "id": "sm8",
        "name": "Trovões Perdidos",
        "releaseDate": "2018-11-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm8/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm9",
        "name": "União de Aliados",
        "releaseDate": "2019-01-31",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm9/symbol",
        "serieId": "sm",
        "official": 181
    },
    {
        "id": "det1",
        "name": "Detetive Pikachu",
        "releaseDate": "2019-03-29",
        "symbol": "https://assets.tcgdex.net/univ/sm/det1/symbol",
        "serieId": "sm",
        "official": 18
    },
    {
        "id": "sm10",
        "name": "Elos Inquebráveis",
        "releaseDate": "2019-05-03",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm10/symbol",
        "serieId": "sm",
        "official": 214
    },
    {
        "id": "sm11",
        "name": "Sintonia Mental ",
        "releaseDate": "2019-08-02",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm11/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "sm115",
        "name": "Destinos Ocultos",
        "releaseDate": "2019-08-23",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm115/symbol",
        "serieId": "sm",
        "official": 68
    },
    {
        "id": "sm12",
        "name": "Eclipse Cósmico",
        "releaseDate": "2019-11-01",
        "symbol": "https://assets.tcgdex.net/univ/sm/sm12/symbol",
        "serieId": "sm",
        "official": 236
    },
    {
        "id": "swshp",
        "name": "ESES Promos",
        "releaseDate": "2019-11-15",
        "logo": "https://assets.tcgdex.net/pt/swsh/swshp/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swshp/symbol",
        "serieId": "swsh",
        "official": 107
    },
    {
        "id": "swsh1",
        "name": "Espada e Escudo",
        "releaseDate": "2020-02-07",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh1/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh1/symbol",
        "serieId": "swsh",
        "official": 202
    },
    {
        "id": "swsh2",
        "name": "Rixa Rebelde",
        "releaseDate": "2020-05-01",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh2/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh2/symbol",
        "serieId": "swsh",
        "official": 192
    },
    {
        "id": "swsh3",
        "name": "Escuridão Incandescente",
        "releaseDate": "2020-08-14",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh3/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh3.5",
        "name": "Caminho do Campeão",
        "releaseDate": "2020-09-25",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh3.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh3.5/symbol",
        "serieId": "swsh",
        "official": 70
    },
    {
        "id": "swsh4",
        "name": "Voltagem Vívida",
        "releaseDate": "2020-11-13",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh4/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4/symbol",
        "serieId": "swsh",
        "official": 185
    },
    {
        "id": "swsh4.5",
        "name": "Destinos Brilhantes ",
        "releaseDate": "2021-02-19",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh4.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh4.5/symbol",
        "serieId": "swsh",
        "official": 72
    },
    {
        "id": "swsh5",
        "name": "Estilos de Batalha",
        "releaseDate": "2021-03-19",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh5/symbol",
        "serieId": "swsh",
        "official": 163
    },
    {
        "id": "swsh6",
        "name": "Reinado Arrepiante",
        "releaseDate": "2021-06-18",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh6/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh6/symbol",
        "serieId": "swsh",
        "official": 198
    },
    {
        "id": "swsh7",
        "name": "Céus em Evolução",
        "releaseDate": "2021-08-27",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh7/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh7/symbol",
        "serieId": "swsh",
        "official": 203
    },
    {
        "id": "cel25",
        "name": "Celebrações",
        "releaseDate": "2021-10-08",
        "logo": "https://assets.tcgdex.net/pt/swsh/cel25/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/cel25/symbol",
        "serieId": "swsh",
        "official": 25
    },
    {
        "id": "swsh8",
        "name": "Golpe Fusão",
        "releaseDate": "2021-11-12",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh8/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh8/symbol",
        "serieId": "swsh",
        "official": 264
    },
    {
        "id": "swsh9",
        "name": "Astros Cintilantes",
        "releaseDate": "2022-02-25",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh9/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh9/symbol",
        "serieId": "swsh",
        "official": 172
    },
    {
        "id": "swsh10",
        "name": "Estrelas Radiantes",
        "releaseDate": "2022-05-27",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh10/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10/symbol",
        "serieId": "swsh",
        "official": 189
    },
    {
        "id": "swsh10.5",
        "name": "Pokémon GO",
        "releaseDate": "2022-07-01",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh10.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh10.5/symbol",
        "serieId": "swsh",
        "official": 78
    },
    {
        "id": "swsh11",
        "name": "Origem Perdida",
        "releaseDate": "2022-09-09",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh11/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh11/symbol",
        "serieId": "swsh",
        "official": 196
    },
    {
        "id": "swsh12",
        "name": "Tempestade Prateada",
        "releaseDate": "2022-11-11",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh12/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12/symbol",
        "serieId": "swsh",
        "official": 195
    },
    {
        "id": "swsh12.5",
        "name": "Realeza Absoluta",
        "releaseDate": "2023-01-20",
        "logo": "https://assets.tcgdex.net/pt/swsh/swsh12.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/swsh/swsh12.5/symbol",
        "serieId": "swsh",
        "official": 159
    },
    {
        "id": "svp",
        "name": "SVP Black Star Promos",
        "releaseDate": "2023-03-31",
        "serieId": "sv",
        "official": 0
    },
    {
        "id": "sv01",
        "name": "Escarlate e Violeta",
        "releaseDate": "2023-03-31",
        "logo": "https://assets.tcgdex.net/pt/sv/sv01/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv01/symbol",
        "serieId": "sv",
        "official": 198
    },
    {
        "id": "sv02",
        "name": "Evoluções em Paldea",
        "releaseDate": "2023-06-09",
        "logo": "https://assets.tcgdex.net/pt/sv/sv02/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv02/symbol",
        "serieId": "sv",
        "official": 193
    },
    {
        "id": "sv03",
        "name": "Obsidiana em Chamas",
        "releaseDate": "2023-08-11",
        "logo": "https://assets.tcgdex.net/pt/sv/sv03/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03/symbol",
        "serieId": "sv",
        "official": 197
    },
    {
        "id": "sv03.5",
        "name": "151",
        "releaseDate": "2023-09-22",
        "logo": "https://assets.tcgdex.net/pt/sv/sv03.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv03.5/symbol",
        "serieId": "sv",
        "official": 165
    },
    {
        "id": "sv04",
        "name": "Fenda Paradoxal",
        "releaseDate": "2023-11-03",
        "logo": "https://assets.tcgdex.net/pt/sv/sv04/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv04.5",
        "name": "Destinos de Paldea",
        "releaseDate": "2024-01-26",
        "logo": "https://assets.tcgdex.net/pt/sv/sv04.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv04.5/symbol",
        "serieId": "sv",
        "official": 91
    },
    {
        "id": "sv05",
        "name": "Forças Temporais",
        "releaseDate": "2024-03-22",
        "logo": "https://assets.tcgdex.net/pt/sv/sv05/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv05/symbol",
        "serieId": "sv",
        "official": 162
    },
    {
        "id": "sv06",
        "name": "Máscaras do Crepúsculo",
        "releaseDate": "2024-05-24",
        "logo": "https://assets.tcgdex.net/pt/sv/sv06/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06/symbol",
        "serieId": "sv",
        "official": 167
    },
    {
        "id": "sv06.5",
        "name": "Fábulas Nebulosas",
        "releaseDate": "2024-08-02",
        "logo": "https://assets.tcgdex.net/pt/sv/sv06.5/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv06.5/symbol",
        "serieId": "sv",
        "official": 64
    },
    {
        "id": "sv07",
        "name": "Coroa Estelar",
        "releaseDate": "2024-09-13",
        "logo": "https://assets.tcgdex.net/pt/sv/sv07/logo",
        "serieId": "sv",
        "official": 142
    },
    {
        "id": "A1",
        "name": "Dominação Genética",
        "releaseDate": "2024-10-30",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A1/symbol",
        "serieId": "tcgp",
        "official": 226
    },
    {
        "id": "P-A",
        "name": "Promo-A",
        "releaseDate": "2024-10-30",
        "serieId": "tcgp",
        "official": 0
    },
    {
        "id": "sv08",
        "name": "Fagulhas Impetudsas",
        "releaseDate": "2024-11-08",
        "serieId": "sv",
        "official": 191
    },
    {
        "id": "A1a",
        "name": "Ilha Mítica",
        "releaseDate": "2024-12-17",
        "serieId": "tcgp",
        "official": 68
    },
    {
        "id": "sv08.5",
        "name": "Evoluções Prismáticas",
        "releaseDate": "2025-01-17",
        "logo": "https://assets.tcgdex.net/pt/sv/sv08.5/logo",
        "serieId": "sv",
        "official": 131
    },
    {
        "id": "A2",
        "name": "Embate do Tempo e Espaço",
        "releaseDate": "2025-01-30",
        "serieId": "tcgp",
        "official": 140
    },
    {
        "id": "A2a",
        "name": "Luz Triunfante",
        "releaseDate": "2025-02-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2a/symbol",
        "serieId": "tcgp",
        "official": 75
    },
    {
        "id": "A2b",
        "name": "Festival Brilhante",
        "releaseDate": "2025-03-27",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A2b/symbol",
        "serieId": "tcgp",
        "official": 72
    },
    {
        "id": "sv09",
        "name": "Amigos de Jornada",
        "releaseDate": "2025-03-28",
        "logo": "https://assets.tcgdex.net/pt/sv/sv09/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv09/symbol",
        "serieId": "sv",
        "official": 159
    },
    {
        "id": "A3",
        "name": "Guardiões Celestiais",
        "releaseDate": "2025-04-30",
        "serieId": "tcgp",
        "official": 155
    },
    {
        "id": "sv10",
        "name": "Rivais Predestinados",
        "releaseDate": "2025-05-30",
        "logo": "https://assets.tcgdex.net/pt/sv/sv10/logo",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10/symbol",
        "serieId": "sv",
        "official": 182
    },
    {
        "id": "sv10.5b",
        "name": "Raio Preto",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5b/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "sv10.5w",
        "name": "Fogo Branco",
        "releaseDate": "2025-07-17",
        "symbol": "https://assets.tcgdex.net/univ/sv/sv10.5w/symbol",
        "serieId": "sv",
        "official": 86
    },
    {
        "id": "A4a",
        "name": "Nascentes Reclusas",
        "releaseDate": "2025-08-28",
        "symbol": "https://assets.tcgdex.net/univ/tcgp/A4a/symbol",
        "serieId": "tcgp",
        "official": 71
    },
    {
        "id": "me01",
        "name": "Megaevolução",
        "releaseDate": "2025-09-26",
        "logo": "https://assets.tcgdex.net/pt/me/me01/logo",
        "symbol": "https://assets.tcgdex.net/univ/me/me01/symbol",
        "serieId": "me",
        "official": 132
    }
]
""",
  )
  fun seriesJson(lang: String): String? = seriesByLang[lang.lowercase()]
  fun setsJson(lang: String): String? = setsByLang[lang.lowercase()]
}

