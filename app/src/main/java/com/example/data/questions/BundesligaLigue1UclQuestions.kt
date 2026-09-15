package com.example.data.questions

import com.example.data.model.Question

object BundesligaLigue1UclQuestions {
    val list: List<Question> = listOf(
        // === BUNDESLIGA GOALS (Bundesliga Golleri - 7 Sorular) ===
        Question(
            id = "bl-goals-250",
            title = "2 Futbolcuyla 250 Bundesliga Golüne Yaklaş",
            competition = "Bundesliga",
            statType = "goals",
            target = 250,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Almanya Bundesliga'da fırtınalar estirmiş 2 yıldızla 250 gole tam yaklaş."
        ),
        Question(
            id = "bl-goals-400",
            title = "2 Futbolcuyla 400 Bundesliga Golüne Yaklaş",
            competition = "Bundesliga",
            statType = "goals",
            target = 400,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Gerd Müller (365) veya Lewandowski (312) gibi devlerle 400 golü yakala."
        ),
        Question(
            id = "bl-goals-500",
            title = "3 Futbolcuyla 500 Bundesliga Golüne Yaklaş",
            competition = "Bundesliga",
            statType = "goals",
            target = 500,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "3 forvet seçerek Bundesliga'da 500 gol hedefine en yakın skoru bul."
        ),
        Question(
            id = "bl-goals-650",
            title = "3 Futbolcuyla 650 Bundesliga Golüne Yaklaş",
            competition = "Bundesliga",
            statType = "goals",
            target = 650,
            pickCount = 3,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Lewandowski, Gerd Müller, Reus gibi yıldızlardan 3'üyle 650 golü tuttur."
        ),
        Question(
            id = "bl-goals-750",
            title = "4 Futbolcuyla 750 Bundesliga Golüne Yaklaş",
            competition = "Bundesliga",
            statType = "goals",
            target = 750,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "4 Alman ligi golcüsüyle 750 gol barajına tam isabet et."
        ),
        Question(
            id = "bl-goals-900",
            title = "5 Futbolcuyla 900 Bundesliga Golüne Yaklaş",
            competition = "Bundesliga",
            statType = "goals",
            target = 900,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Bundesliga tarihinin en skorer 5 oyuncusunu toplayarak 900 gole yaklaş."
        ),
        Question(
            id = "bl-goals-180",
            title = "2 Futbolcuyla 180 Bundesliga Golüne Yaklaş",
            competition = "Bundesliga",
            statType = "goals",
            target = 180,
            pickCount = 2,
            difficulty = "Hızlı",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "90'ar gollü iki Bundesliga yıldızıyla 180 gole ulaş."
        ),

        // === BUNDESLIGA APPEARANCES, ASSISTS & CLEAN SHEETS (8 Sorular) ===
        Question(
            id = "bl-app-600",
            title = "2 Futbolcuyla 600 Bundesliga Maçına Yaklaş",
            competition = "Bundesliga",
            statType = "appearances",
            target = 600,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Bundesliga'da 300'er maça çıkmış 2 emektar futbolcuyla 600 maça ulaş."
        ),
        Question(
            id = "bl-app-900",
            title = "3 Futbolcuyla 900 Bundesliga Maçına Yaklaş",
            competition = "Bundesliga",
            statType = "appearances",
            target = 900,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "3 futbolcuyla toplam 900 Bundesliga maçına yaklaş."
        ),
        Question(
            id = "bl-app-1300",
            title = "3 Futbolcuyla 1300 Bundesliga Maçına Yaklaş",
            competition = "Bundesliga",
            statType = "appearances",
            target = 1300,
            pickCount = 3,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Neuer (508), Müller (480), Reus (391) ile 1300 maça yaklaş."
        ),
        Question(
            id = "bl-app-1600",
            title = "4 Futbolcuyla 1600 Bundesliga Maçına Yaklaş",
            competition = "Bundesliga",
            statType = "appearances",
            target = 1600,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "4 oyuncu tercihinle 1600 Bundesliga maçını yakala."
        ),
        Question(
            id = "bl-ast-150",
            title = "2 Futbolcuyla 150 Bundesliga Asistine Yaklaş",
            competition = "Bundesliga",
            statType = "assists",
            target = 150,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Almanya'da asistleriyle parlayan 2 yıldızla 150 asiste ulaş."
        ),
        Question(
            id = "bl-ast-250",
            title = "2 Futbolcuyla 250 Bundesliga Asistine Yaklaş",
            competition = "Bundesliga",
            statType = "assists",
            target = 250,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Thomas Müller (192) ve Franck Ribery (121) gibi ustalarla 250 asisti hedefle."
        ),
        Question(
            id = "bl-ast-350",
            title = "3 Futbolcuyla 350 Bundesliga Asistine Yaklaş",
            competition = "Bundesliga",
            statType = "assists",
            target = 350,
            pickCount = 3,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Müller, Ribery, Reus ile 350 Bundesliga asistine en yakın sonucu al."
        ),
        Question(
            id = "bl-cs-225",
            title = "Manuel Neuer ile 225 Bundesliga Gol Yememe Rekoruna Yaklaş",
            competition = "Bundesliga",
            statType = "cleanSheets",
            target = 225,
            pickCount = 1,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Bundesliga"),
            description = "Bundesliga'nın gol yememe rekorunu elinde tutan efsane eldiveni seç."
        ),

        // === LIGUE 1 GOALS, APP & ASSISTS (10 Sorular) ===
        Question(
            id = "l1-goals-200",
            title = "2 Futbolcuyla 200 Ligue 1 Golüne Yaklaş",
            competition = "Ligue 1",
            statType = "goals",
            target = 200,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "Fransa Ligue 1'de 100'er gol barajını geçmiş 2 yıldızla 200 gole ulaş."
        ),
        Question(
            id = "l1-goals-300",
            title = "2 Futbolcuyla 300 Ligue 1 Golüne Yaklaş",
            competition = "Ligue 1",
            statType = "goals",
            target = 300,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "Kylian Mbappé (191) ve Edinson Cavani (138) veya Lacazette (148) ile 300 golü tuttur."
        ),
        Question(
            id = "l1-goals-400",
            title = "3 Futbolcuyla 400 Ligue 1 Golüne Yaklaş",
            competition = "Ligue 1",
            statType = "goals",
            target = 400,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "3 forvet tercihinle Fransa liginde toplam 400 gole en yakın noktada dur."
        ),
        Question(
            id = "l1-goals-500",
            title = "3 Futbolcuyla 500 Ligue 1 Golüne Yaklaş",
            competition = "Ligue 1",
            statType = "goals",
            target = 500,
            pickCount = 3,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "Mbappe, Ben Yedder, Cavani, Ibrahimovic gibi skorerlerle 500 hedefini vur."
        ),
        Question(
            id = "l1-goals-600",
            title = "4 Futbolcuyla 600 Ligue 1 Golüne Yaklaş",
            competition = "Ligue 1",
            statType = "goals",
            target = 600,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "4 usta golcüyle 600 Ligue 1 golüne tam isabet yap."
        ),
        Question(
            id = "l1-app-500",
            title = "2 Futbolcuyla 500 Ligue 1 Maçına Yaklaş",
            competition = "Ligue 1",
            statType = "appearances",
            target = 500,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "Ligue 1'de 250 civarı maça çıkmış 2 futbolcuyla 500 maça ulaş."
        ),
        Question(
            id = "l1-app-800",
            title = "3 Futbolcuyla 800 Ligue 1 Maçına Yaklaş",
            competition = "Ligue 1",
            statType = "appearances",
            target = 800,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "3 oyuncu ile toplam 800 Ligue 1 maçına yaklaş."
        ),
        Question(
            id = "l1-ast-120",
            title = "2 Futbolcuyla 120 Ligue 1 Asistine Yaklaş",
            competition = "Ligue 1",
            statType = "assists",
            target = 120,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "Kylian Mbappé (78) ve Angel Di María (72) ile 120 asisti yakala."
        ),
        Question(
            id = "l1-ast-180",
            title = "3 Futbolcuyla 180 Ligue 1 Asistine Yaklaş",
            competition = "Ligue 1",
            statType = "assists",
            target = 180,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "3 oyun kurucu ile 180 Ligue 1 asistini tuttur."
        ),
        Question(
            id = "l1-goals-150",
            title = "2 Futbolcuyla 150 Ligue 1 Golüne Yaklaş",
            competition = "Ligue 1",
            statType = "goals",
            target = 150,
            pickCount = 2,
            difficulty = "Hızlı",
            eligibleCompetitions = listOf("Ligue 1"),
            description = "75'er gol atmış 2 Ligue 1 forvetiyle 150 gole ulaş."
        ),

        // === CHAMPIONS LEAGUE (5 Sorular) ===
        Question(
            id = "ucl-goals-200",
            title = "3 Futbolcuyla 200 Şampiyonlar Ligi Golüne Yaklaş",
            competition = "Champions League",
            statType = "goals",
            target = 200,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Champions League"),
            description = "Devler Ligi'nin efsane isimlerinden 3'üyle 200 gole yaklaş."
        ),
        Question(
            id = "ucl-goals-270",
            title = "2 Futbolcuyla 270 Şampiyonlar Ligi Golüne Yaklaş",
            competition = "Champions League",
            statType = "goals",
            target = 270,
            pickCount = 2,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Champions League"),
            description = "Cristiano Ronaldo (140) ve Lionel Messi (129) ile 270 hedefine tam isabet yap."
        ),
        Question(
            id = "ucl-goals-300",
            title = "4 Futbolcuyla 300 Şampiyonlar Ligi Golüne Yaklaş",
            competition = "Champions League",
            statType = "goals",
            target = 300,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Champions League"),
            description = "Lewandowski, Benzema, Raul gibi efsanelerle 300 gol hedefini vur."
        ),
        Question(
            id = "ucl-goals-350",
            title = "4 Futbolcuyla 350 Şampiyonlar Ligi Golüne Yaklaş",
            competition = "Champions League",
            statType = "goals",
            target = 350,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Champions League"),
            description = "Devler arenasında 350 gole en yakın kombinasyonu kur."
        ),
        Question(
            id = "ucl-goals-400",
            title = "5 Futbolcuyla 400 Şampiyonlar Ligi Golüne Yaklaş",
            competition = "Champions League",
            statType = "goals",
            target = 400,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Champions League"),
            description = "Şampiyonlar Ligi tarihinin en skorer 5 forvetiyle 400 gol barajını zorla."
        ),

        // === INTERNATIONAL / MİLLİ TAKIM (5 Sorular) ===
        Question(
            id = "intl-goals-120",
            title = "2 Futbolcuyla 120 Milli Takım Golüne Yaklaş",
            competition = "International",
            statType = "goals",
            target = 120,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("International"),
            description = "Milli formayla 60'ar gol atmış 2 forvetle 120 gole ulaş."
        ),
        Question(
            id = "intl-goals-180",
            title = "3 Futbolcuyla 180 Milli Takım Golüne Yaklaş",
            competition = "International",
            statType = "goals",
            target = 180,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("International"),
            description = "Lewandowski (84), Neymar (79), Suarez (69) gibi isimlerle 180 gole yaklaş."
        ),
        Question(
            id = "intl-goals-240",
            title = "2 Futbolcuyla 240 Milli Takım Golüne Yaklaş",
            competition = "International",
            statType = "goals",
            target = 240,
            pickCount = 2,
            difficulty = "Zor",
            eligibleCompetitions = listOf("International"),
            description = "Cristiano Ronaldo (132) ve Lionel Messi (109) ile 240 hedefine tam yanaş."
        ),
        Question(
            id = "intl-goals-280",
            title = "4 Futbolcuyla 280 Milli Takım Golüne Yaklaş",
            competition = "International",
            statType = "goals",
            target = 280,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("International"),
            description = "4 milli gol makinesiyle 280 gol hedefini tuttur."
        ),
        Question(
            id = "intl-goals-350",
            title = "5 Futbolcuyla 350 Milli Takım Golüne Yaklaş",
            competition = "International",
            statType = "goals",
            target = 350,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("International"),
            description = "Milli takım tarihinin en çok gol atan 5 süper starıyla 350 gole ulaş."
        )
    )
}
