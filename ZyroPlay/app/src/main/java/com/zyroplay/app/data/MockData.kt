package com.zyroplay.app.data

import com.zyroplay.app.model.Channel
import com.zyroplay.app.model.ContentRow
import com.zyroplay.app.model.SeriesItem
import com.zyroplay.app.model.VodItem

object MockData {

    val liveCategories = listOf(
        "Tous", "Sports", "Info", "Cinéma", "Enfants", "Musique", "Documentaire", "International"
    )

    val channels = listOf(
        Channel("1", "BeIN Sports 1", "Sports", isLive = true, currentProgram = "Ligue 1 — PSG vs OM"),
        Channel("2", "Canal+ Cinema", "Cinéma", isLive = true, currentProgram = "Dune: Part Two"),
        Channel("3", "France 24", "Info", isLive = true, currentProgram = "World News"),
        Channel("4", "Cartoon Network", "Enfants", isLive = true, currentProgram = "Adventure Time"),
        Channel("5", "MTV Hits", "Musique", isLive = true, currentProgram = "Top 40 Countdown"),
        Channel("6", "Discovery", "Documentaire", isLive = true, currentProgram = "Planet Earth III"),
        Channel("7", "ESPN", "Sports", isLive = true, currentProgram = "NBA Live"),
        Channel("8", "HBO", "Cinéma", isLive = true, currentProgram = "The Last of Us"),
        Channel("9", "CNN", "Info", isLive = true, currentProgram = "Breaking News"),
        Channel("10", "Nickelodeon", "Enfants", isLive = true, currentProgram = "SpongeBob"),
        Channel("11", "Sky Sports", "Sports", isLive = true, currentProgram = "Premier League"),
        Channel("12", "National Geographic", "Documentaire", isLive = true, currentProgram = "Cosmos")
    )

    val featuredMovie = VodItem(
        id = "f1",
        title = "Dune: Part Two",
        year = "2024",
        rating = "8.8",
        genre = "Sci-Fi • Aventure",
        duration = "2h 46min",
        description = "Paul Atreides s'unit à Chani et aux Fremen pour mener la révolte contre ceux qui ont détruit sa famille."
    )

    val movieRows = listOf(
        ContentRow("Continuer à regarder", listOf(
            VodItem("m1", "Oppenheimer", "2023", "8.5", "Drame", duration = "3h 00min"),
            VodItem("m2", "Killers of the Flower Moon", "2023", "7.8", "Crime", duration = "3h 26min"),
            VodItem("m3", "Poor Things", "2023", "8.0", "Comédie", duration = "2h 21min")
        )),
        ContentRow("Nouveautés", listOf(
            VodItem("m4", "Gladiator II", "2024", "7.2", "Action", duration = "2h 28min"),
            VodItem("m5", "Wicked", "2024", "7.6", "Musical", duration = "2h 40min"),
            VodItem("m6", "Moana 2", "2024", "7.0", "Animation", duration = "1h 40min"),
            VodItem("m7", "Venom 3", "2024", "6.5", "Action", duration = "1h 49min")
        )),
        ContentRow("Action", listOf(
            VodItem("m8", "John Wick 4", "2023", "7.7", "Action", duration = "2h 49min"),
            VodItem("m9", "Mission Impossible 7", "2023", "7.6", "Action", duration = "2h 43min"),
            VodItem("m10", "Fast X", "2023", "5.8", "Action", duration = "2h 21min")
        ))
    )

    val seriesList = listOf(
        SeriesItem("s1", "The Last of Us", 2, "8.9", "Drame", episodes = 16),
        SeriesItem("s2", "House of the Dragon", 2, "8.4", "Fantasy", episodes = 18),
        SeriesItem("s3", "Wednesday", 2, "8.1", "Comédie", episodes = 16),
        SeriesItem("s4", "Squid Game", 2, "8.0", "Thriller", episodes = 15),
        SeriesItem("s5", "The Bear", 3, "8.6", "Drame", episodes = 28),
        SeriesItem("s6", "Shogun", 1, "8.7", "Historique", episodes = 10),
        SeriesItem("s7", "Fallout", 1, "8.4", "Sci-Fi", episodes = 8),
        SeriesItem("s8", "Reacher", 3, "8.1", "Action", episodes = 24)
    )

    val catchUpPrograms = listOf(
        VodItem("c1", "Match PSG — OM", "2024", "", "Sports", duration = "1h 45min"),
        VodItem("c2", "Journal 20h", "2024", "", "Info", duration = "30min"),
        VodItem("c3", "F1 — Grand Prix Monaco", "2024", "", "Sports", duration = "2h 10min"),
        VodItem("c4", "Concert Live", "2024", "", "Musique", duration = "1h 30min")
    )
}
