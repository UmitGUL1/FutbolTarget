package com.example.data.questions

import com.example.data.model.Question

object LaLigaSerieAQuestions {
    val list: List<Question> = listOf(
        // === LA LIGA GOALS (La Liga Golleri) ===
        Question(
            id = "ll-goals-300",
            title = "2 Futbolcuyla 300 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 300,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("La Liga"),
            description = "İspanya'da fırtınalar estirmiş 2 yıldızla 300 La Liga golüne tam yaklaş."
        ),
        Question(
            id = "ll-goals-450",
            title = "3 Futbolcuyla 450 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 450,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("La Liga"),
            description = "İspanya La Liga tarihinin en ikonik golcülerini kadrona katarak 450 gol hedefini vur."
        ),
        Question(
            id = "ll-goals-600",
            title = "3 Futbolcuyla 600 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 600,
            pickCount = 3,
            difficulty = "Zor",
            eligibleCompetitions = listOf("La Liga"),
            description = "Messi, Ronaldo, Benzema, Suarez gibi devlerden 3'üyle 600 golü yakala."
        ),
        Question(
            id = "ll-goals-750",
            title = "4 Futbolcuyla 750 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 750,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("La Liga"),
            description = "4 usta golcüyle 750 La Liga golüne en yakın skoru bul."
        ),
        Question(
            id = "ll-goals-900",
            title = "4 Futbolcuyla 900 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 900,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("La Liga"),
            description = "La Liga'nın gol canavarlarından 4'üyle 900 gol hedefine tam isabet et."
        ),
        Question(
            id = "ll-goals-1100",
            title = "5 Futbolcuyla 1100 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 1100,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("La Liga"),
            description = "Messi (474) ve Ronaldo (311) gibi lig tarihini yazan 5 isimle 1100 gole yanaş."
        ),
        Question(
            id = "ll-goals-200",
            title = "2 Futbolcuyla 200 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 200,
            pickCount = 2,
            difficulty = "Hızlı",
            eligibleCompetitions = listOf("La Liga"),
            description = "100'er gol atmış 2 La Liga forvetiyle 200 gole ulaş."
        ),
        Question(
            id = "ll-goals-500",
            title = "3 Futbolcuyla 500 La Liga Golüne Yaklaş",
            competition = "La Liga",
            statType = "goals",
            target = 500,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("La Liga"),
            description = "3 futbolcuyla 500 La Liga golü sınırını tuttur."
        ),

        // === LA LIGA APPEARANCES (La Liga Maçları) ===
        Question(
            id = "ll-app-800",
            title = "2 Futbolcuyla 800 La Liga Maçına Yaklaş",
            competition = "La Liga",
            statType = "appearances",
            target = 800,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("La Liga"),
            description = "La Liga'da 400'er maça çıkmış 2 emektar futbolcuyla 800 maça ulaş."
        ),
        Question(
            id = "ll-app-1200",
            title = "3 Futbolcuyla 1200 La Liga Maçına Yaklaş",
            competition = "La Liga",
            statType = "appearances",
            target = 1200,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("La Liga"),
            description = "Ramos, Raul, Messi gibi lig abideleriyle 1200 maçı hedefle."
        ),
        Question(
            id = "ll-app-1500",
            title = "4 Futbolcuyla 1500 La Liga Maçına Yaklaş",
            competition = "La Liga",
            statType = "appearances",
            target = 1500,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("La Liga"),
            description = "4 oyuncu ile toplam 1500 La Liga maçına yaklaş."
        ),
        Question(
            id = "ll-app-2000",
            title = "5 Futbolcuyla 2000 La Liga Maçına Yaklaş",
            competition = "La Liga",
            statType = "appearances",
            target = 2000,
            pickCount = 5,
            difficulty = "Zor",
            eligibleCompetitions = listOf("La Liga"),
            description = "5 futbolcu tercihinle 2000 La Liga maçına tam isabet et."
        ),
        Question(
            id = "ll-app-2400",
            title = "5 Futbolcuyla 2400 La Liga Maçına Yaklaş",
            competition = "La Liga",
            statType = "appearances",
            target = 2400,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("La Liga"),
            description = "La Liga'da 500 civarı maçı olan 5 ismi seçerek 2400 barajını vur."
        ),
        Question(
            id = "ll-app-1000",
            title = "3 Futbolcuyla 1000 La Liga Maçına Yaklaş",
            competition = "La Liga",
            statType = "appearances",
            target = 1000,
            pickCount = 3,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("La Liga"),
            description = "3 oyuncu ile 1000 maçlık lig tecrübesine odaklan."
        ),

        // === LA LIGA ASSISTS & CLEAN SHEETS ===
        Question(
            id = "ll-ast-150",
            title = "2 Futbolcuyla 150 La Liga Asistine Yaklaş",
            competition = "La Liga",
            statType = "assists",
            target = 150,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("La Liga"),
            description = "La Liga'da asistleriyle öne çıkan 2 futbolcuyla 150 asiste ulaş."
        ),
        Question(
            id = "ll-ast-250",
            title = "3 Futbolcuyla 250 La Liga Asistine Yaklaş",
            competition = "La Liga",
            statType = "assists",
            target = 250,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("La Liga"),
            description = "Messi (192), Xavi (129), Benzema (119) gibi isimlerden 3'üyle 250 asisti yakala."
        ),
        Question(
            id = "ll-ast-350",
            title = "4 Futbolcuyla 350 La Liga Asistine Yaklaş",
            competition = "La Liga",
            statType = "assists",
            target = 350,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("La Liga"),
            description = "4 pas dehasıyla 350 La Liga asist hedefine en yakın sonucu al."
        ),
        Question(
            id = "ll-ast-450",
            title = "5 Futbolcuyla 450 La Liga Asistine Yaklaş",
            competition = "La Liga",
            statType = "assists",
            target = 450,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("La Liga"),
            description = "İspanya ligi tarihinin en iyi 5 pasörünü seçerek 450 asiste ulaş."
        ),
        Question(
            id = "ll-cs-200",
            title = "2 Kaleciyle 200 La Liga Gol Yememe Maçına Yaklaş",
            competition = "La Liga",
            statType = "cleanSheets",
            target = 200,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("La Liga"),
            description = "Iker Casillas (177) ve bir kaleci daha seçerek 200 clean sheet'e yanaş."
        ),
        Question(
            id = "ll-cs-350",
            title = "3 Kaleciyle 350 La Liga Gol Yememe Maçına Yaklaş",
            competition = "La Liga",
            statType = "cleanSheets",
            target = 350,
            pickCount = 3,
            difficulty = "Zor",
            eligibleCompetitions = listOf("La Liga"),
            description = "3 kaleci ile toplam 350 maçta kalesini gole kapat."
        ),

        // === SERIE A GOALS (Serie A Golleri) ===
        Question(
            id = "sa-goals-250",
            title = "2 Futbolcuyla 250 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 250,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Serie A"),
            description = "İtalya'da gol krallığı yaşamış 2 santrforla 250 Serie A golüne ulaş."
        ),
        Question(
            id = "sa-goals-350",
            title = "3 Futbolcuyla 350 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 350,
            pickCount = 3,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Serie A"),
            description = "3 İtalyan ligi efsanesiyle toplam 350 gole en yakın noktada dur."
        ),
        Question(
            id = "sa-goals-450",
            title = "3 Futbolcuyla 450 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 450,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Serie A"),
            description = "Totti (250), Di Natale (209), Immobile (201) gibi isimlerden 3'üyle 450'yi bul."
        ),
        Question(
            id = "sa-goals-550",
            title = "4 Futbolcuyla 550 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 550,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Serie A"),
            description = "4 forvet tercihinle 550 Serie A golünü hassas yakala."
        ),
        Question(
            id = "sa-goals-650",
            title = "4 Futbolcuyla 650 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 650,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Serie A"),
            description = "Del Piero, Shevchenko, Ibrahimovic, Totti gibi isimlerle 650 hedefini vur."
        ),
        Question(
            id = "sa-goals-750",
            title = "5 Futbolcuyla 750 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 750,
            pickCount = 5,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Serie A"),
            description = "5 futbolcu ile 750 Serie A golü barajına en yakın skoru üret."
        ),
        Question(
            id = "sa-goals-900",
            title = "5 Futbolcuyla 900 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 900,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Serie A"),
            description = "Serie A tarihinin en büyük 5 golcüsünü bir araya getirerek 900 gole yaklaş."
        ),
        Question(
            id = "sa-goals-200",
            title = "2 Futbolcuyla 200 Serie A Golüne Yaklaş",
            competition = "Serie A",
            statType = "goals",
            target = 200,
            pickCount = 2,
            difficulty = "Hızlı",
            eligibleCompetitions = listOf("Serie A"),
            description = "100'er gol atmış 2 Serie A yıldızıyla 200 gole ulaş."
        ),

        // === SERIE A APPEARANCES (Serie A Maçları) ===
        Question(
            id = "sa-app-800",
            title = "2 Futbolcuyla 800 Serie A Maçına Yaklaş",
            competition = "Serie A",
            statType = "appearances",
            target = 800,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Serie A"),
            description = "Serie A'da 400+ maça çıkmış 2 emektar futbolcuyla 800 maça ulaş."
        ),
        Question(
            id = "sa-app-1200",
            title = "3 Futbolcuyla 1200 Serie A Maçına Yaklaş",
            competition = "Serie A",
            statType = "appearances",
            target = 1200,
            pickCount = 3,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Serie A"),
            description = "3 futbolcuyla toplam 1200 Serie A maçına yaklaş."
        ),
        Question(
            id = "sa-app-1500",
            title = "3 Futbolcuyla 1500 Serie A Maçına Yaklaş",
            competition = "Serie A",
            statType = "appearances",
            target = 1500,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Serie A"),
            description = "Buffon (657), Totti (619), Pirlo (493) gibi efsanelerle 1500 maça yaklaş."
        ),
        Question(
            id = "sa-app-1800",
            title = "4 Futbolcuyla 1800 Serie A Maçına Yaklaş",
            competition = "Serie A",
            statType = "appearances",
            target = 1800,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Serie A"),
            description = "4 oyuncu tercihinle 1800 Serie A maçını yakala."
        ),
        Question(
            id = "sa-app-2200",
            title = "5 Futbolcuyla 2200 Serie A Maçına Yaklaş",
            competition = "Serie A",
            statType = "appearances",
            target = 2200,
            pickCount = 5,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Serie A"),
            description = "5 futbolcu ile 2200 Serie A maçı hedefini tam tuttur."
        ),
        Question(
            id = "sa-app-2500",
            title = "5 Futbolcuyla 2500 Serie A Maçına Yaklaş",
            competition = "Serie A",
            statType = "appearances",
            target = 2500,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Serie A"),
            description = "Serie A tarihinin en çok forma giymiş 5 oyuncusuyla 2500 maç rekoruna yanaş."
        ),

        // === SERIE A ASSISTS & CLEAN SHEETS ===
        Question(
            id = "sa-ast-120",
            title = "2 Futbolcuyla 120 Serie A Asistine Yaklaş",
            competition = "Serie A",
            statType = "assists",
            target = 120,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Serie A"),
            description = "İtalya'da 60'ar asist yapmış 2 maestroyla 120 asiste ulaş."
        ),
        Question(
            id = "sa-ast-200",
            title = "3 Futbolcuyla 200 Serie A Asistine Yaklaş",
            competition = "Serie A",
            statType = "assists",
            target = 200,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Serie A"),
            description = "Totti (158), Del Piero (112), Pirlo (106) gibi isimlerle 200 asisti tuttur."
        ),
        Question(
            id = "sa-ast-280",
            title = "4 Futbolcuyla 280 Serie A Asistine Yaklaş",
            competition = "Serie A",
            statType = "assists",
            target = 280,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Serie A"),
            description = "4 pas ustasıyla 280 Serie A asistine tam isabet et."
        ),
        Question(
            id = "sa-ast-350",
            title = "5 Futbolcuyla 350 Serie A Asistine Yaklaş",
            competition = "Serie A",
            statType = "assists",
            target = 350,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Serie A"),
            description = "Serie A'nın en yaratıcı 5 oyuncusuyla 350 asiste ulaş."
        ),
        Question(
            id = "sa-cs-250",
            title = "2 Kaleciyle 250 Serie A Gol Yememe Maçına Yaklaş",
            competition = "Serie A",
            statType = "cleanSheets",
            target = 250,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Serie A"),
            description = "Gianluigi Buffon (299) ve bir kaleci seçerek 250 clean sheet sınırına yanaş."
        ),
        Question(
            id = "sa-cs-350",
            title = "2 Kaleciyle 350 Serie A Gol Yememe Maçına Yaklaş",
            competition = "Serie A",
            statType = "cleanSheets",
            target = 350,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Serie A"),
            description = "İtalyan liginde kalesini gole kapatmış 2 kaleciyle 350'ye yaklaş."
        )
    )
}
