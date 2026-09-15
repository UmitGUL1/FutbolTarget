package com.example.data.questions

import com.example.data.model.Question

object PremierLeagueQuestions {
    val list: List<Question> = listOf(
        // === PREMIER LEAGUE GOALS (Premier League Golleri) ===
        Question(
            id = "pl-goals-300",
            title = "3 Futbolcuyla 300 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 300,
            pickCount = 3,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "Premier League'in efsanevi golcülerinden 3 tanesini seçerek tam 300 gole yaklaş."
        ),
        Question(
            id = "pl-goals-400",
            title = "3 Futbolcuyla 400 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 400,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "Shearer, Rooney, Kane, Aguero gibi isimlerden 3'üyle 400 gol hedefine en yakın skoru bul."
        ),
        Question(
            id = "pl-goals-500",
            title = "4 Futbolcuyla 500 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 500,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "Tarihin en iyi Premier League golcülerini seç ve toplam 500 gole en yakın skora ulaş."
        ),
        Question(
            id = "pl-goals-600",
            title = "4 Futbolcuyla 600 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 600,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "4 büyük forvetle 600 Premier League golü sınırını vurmak için titiz hesap yap."
        ),
        Question(
            id = "pl-goals-700",
            title = "5 Futbolcuyla 700 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 700,
            pickCount = 5,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "5 futbolcu tercihinle 700 Premier League golüne en yakın noktada dur."
        ),
        Question(
            id = "pl-goals-800",
            title = "5 Futbolcuyla 800 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 800,
            pickCount = 5,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "5 Premier League forvetiyle toplam 800 gol hedefini tuttur."
        ),
        Question(
            id = "pl-goals-900",
            title = "5 Futbolcuyla 900 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 900,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Premier League"),
            description = "Alan Shearer, Harry Kane, Wayne Rooney gibi devleri kadrona katarak 900 gole yaklaş."
        ),
        Question(
            id = "pl-goals-200",
            title = "2 Futbolcuyla 200 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 200,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "100'ler kulübünden 2 oyuncu seçerek tam 200 gole ulaş."
        ),
        Question(
            id = "pl-goals-250",
            title = "2 Futbolcuyla 250 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 250,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "2 seçkin golcüyle 250 gole yaklaşmaya çalış."
        ),
        Question(
            id = "pl-goals-350",
            title = "3 Futbolcuyla 350 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 350,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "Ortalama 116 gol atan 3 forvetle 350 golü yakala."
        ),
        Question(
            id = "pl-goals-450",
            title = "4 Futbolcuyla 450 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 450,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "4 oyuncu ile 450 gol hedefini dengeli kur."
        ),
        Question(
            id = "pl-goals-550",
            title = "4 Futbolcuyla 550 Premier League Golüne Yaklaş",
            competition = "Premier League",
            statType = "goals",
            target = 550,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "4 futbolcuyla 550 gole en yakın skoru üret."
        ),

        // === PREMIER LEAGUE APPEARANCES (Premier League Maçları) ===
        Question(
            id = "pl-app-800",
            title = "2 Futbolcuyla 800 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 800,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "İngiltere'de 400+ maça çıkmış 2 emektar futbolcuyla 800 maça ulaş."
        ),
        Question(
            id = "pl-app-1000",
            title = "3 Futbolcuyla 1000 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 1000,
            pickCount = 3,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "3 futbolcuyla toplam 1000 Premier League maçına yaklaş."
        ),
        Question(
            id = "pl-app-1200",
            title = "3 Futbolcuyla 1200 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 1200,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "Lampard, Gerrard, Shearer gibi lig simgelerinden 3'üyle 1200 maçı hedefle."
        ),
        Question(
            id = "pl-app-1500",
            title = "4 Futbolcuyla 1500 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 1500,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "Ligin istikrar abidelerini seçerek tam 1500 maça ulaşmaya çalış."
        ),
        Question(
            id = "pl-app-1800",
            title = "4 Futbolcuyla 1800 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 1800,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "4 oyuncu tercihinle 1800 Premier League maçını yakala."
        ),
        Question(
            id = "pl-app-2000",
            title = "5 Futbolcuyla 2000 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 2000,
            pickCount = 5,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "5 futbolcu ile 2000 Premier League maçı hedefini tam tuttur."
        ),
        Question(
            id = "pl-app-2400",
            title = "5 Futbolcuyla 2400 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 2400,
            pickCount = 5,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "Ryan Giggs (632), Frank Lampard (609) gibi isimlerle 2400 maç sınırına yaklaş."
        ),
        Question(
            id = "pl-app-2600",
            title = "5 Futbolcuyla 2600 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 2600,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Premier League"),
            description = "Tarihin en çok Premier League maçına çıkmış 5 oyuncusunu toplayıp 2600'e yanaş."
        ),
        Question(
            id = "pl-app-600",
            title = "2 Futbolcuyla 600 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 600,
            pickCount = 2,
            difficulty = "Hızlı",
            eligibleCompetitions = listOf("Premier League"),
            description = "300'er maçlık iki Premier League yıldızıyla 600 maça ulaş."
        ),
        Question(
            id = "pl-app-1400",
            title = "4 Futbolcuyla 1400 Premier League Maçına Yaklaş",
            competition = "Premier League",
            statType = "appearances",
            target = 1400,
            pickCount = 4,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "350 ortalamayla 1400 maça yaklaş."
        ),

        // === PREMIER LEAGUE ASSISTS (Premier League Asistleri) ===
        Question(
            id = "pl-ast-100",
            title = "2 Futbolcuyla 100 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 100,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "50'şer asisti olan 2 Premier League yıldızıyla 100 asist hedefini vur."
        ),
        Question(
            id = "pl-ast-150",
            title = "3 Futbolcuyla 150 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 150,
            pickCount = 3,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "3 futbolcuyla toplam 150 Premier League asistine ulaş."
        ),
        Question(
            id = "pl-ast-200",
            title = "3 Futbolcuyla 200 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 200,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "De Bruyne, Gerrard, Lampard gibi isimlerden 3'üyle 200 asist hedefine yanaş."
        ),
        Question(
            id = "pl-ast-250",
            title = "4 Futbolcuyla 250 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 250,
            pickCount = 4,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "Premier League'in en yetenekli oyun kurucularını seçerek 250 asiste ulaş."
        ),
        Question(
            id = "pl-ast-300",
            title = "4 Futbolcuyla 300 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 300,
            pickCount = 4,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "4 oyuncu tercihinle 300 asist sayısına en yakın skoru bul."
        ),
        Question(
            id = "pl-ast-350",
            title = "5 Futbolcuyla 350 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 350,
            pickCount = 5,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "5 futbolcuyla 350 Premier League asistini tuttur."
        ),
        Question(
            id = "pl-ast-400",
            title = "5 Futbolcuyla 400 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 400,
            pickCount = 5,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "Giggs (162), De Bruyne (112), Rooney (103), Lampard (102) gibi liderlerle 400'e yaklaş."
        ),
        Question(
            id = "pl-ast-450",
            title = "5 Futbolcuyla 450 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 450,
            pickCount = 5,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Premier League"),
            description = "Premier League tarihinin en çok asist yapan 5 oyuncusuyla 450 asist sınırını zorla."
        ),
        Question(
            id = "pl-ast-120",
            title = "2 Futbolcuyla 120 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 120,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "60'ar asistlik 2 orta saha ile 120'ye ulaş."
        ),
        Question(
            id = "pl-ast-180",
            title = "3 Futbolcuyla 180 Premier League Asistine Yaklaş",
            competition = "Premier League",
            statType = "assists",
            target = 180,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "3 futbolcuyla 180 asisti dengeli yakala."
        ),

        // === PREMIER LEAGUE CLEAN SHEETS (Gol Yememe) ===
        Question(
            id = "pl-cs-150",
            title = "2 Kaleciyle 150 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 150,
            pickCount = 2,
            difficulty = "Kolay",
            eligibleCompetitions = listOf("Premier League"),
            description = "2 Premier League eldiveniyle toplam 150 maçta kalesini gole kapat."
        ),
        Question(
            id = "pl-cs-200",
            title = "2 Kaleciyle 200 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 200,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "Ederson ve Alisson gibi modern kalecilerle 200 clean sheet'e yaklaş."
        ),
        Question(
            id = "pl-cs-250",
            title = "2 Kaleciyle 250 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 250,
            pickCount = 2,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "Petr Cech (202) ve bir kaleci daha seçerek 250 clean sheet sınırını vur."
        ),
        Question(
            id = "pl-cs-300",
            title = "3 Kaleciyle 300 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 300,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "3 Premier League kalecisiyle 300 gol yememe maçına ulaş."
        ),
        Question(
            id = "pl-cs-350",
            title = "3 Kaleciyle 350 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 350,
            pickCount = 3,
            difficulty = "Zor",
            eligibleCompetitions = listOf("Premier League"),
            description = "Premier League'de en çok kalesini gole kapatan eldivenleri seçerek 350'ye yaklaş."
        ),
        Question(
            id = "pl-cs-400",
            title = "3 Kaleciyle 400 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 400,
            pickCount = 3,
            difficulty = "Efsane",
            eligibleCompetitions = listOf("Premier League"),
            description = "Cech, Ederson ve Alisson ile 400 clean sheet barajını zorla."
        ),
        Question(
            id = "pl-cs-180",
            title = "2 Kaleciyle 180 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 180,
            pickCount = 2,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "90'ar clean sheetlik iki kaleciyle 180'e yaklaş."
        ),
        Question(
            id = "pl-cs-320",
            title = "3 Kaleciyle 320 Premier League Gol Yememe Maçına Yaklaş",
            competition = "Premier League",
            statType = "cleanSheets",
            target = 320,
            pickCount = 3,
            difficulty = "Orta",
            eligibleCompetitions = listOf("Premier League"),
            description = "3 kaleci ile 320 gol yememe sayısını tam tuttur."
        )
    )
}
