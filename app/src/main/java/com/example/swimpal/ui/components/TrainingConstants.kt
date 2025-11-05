package com.example.swimpal.ui.components

import java.text.SimpleDateFormat
import java.util.*

val globalOpis = """
🟦 Kraul (styl dowolny)

    Najpopularniejszy ze stylów pływackich. Pływamy na brzuchu, pracując jednocześnie nogami oraz naprzemiennie ramionami. Oddech bierzemy co kilka ruchów, przekręcając głowę na bok.

    Ćwiczenia techniczne:

    Ramiona kraul – mała deska między nogami (między kolanami a biodrami), pracujemy tylko ramionami, utrzymując napięcie mięśni brzucha.

    Nogi kraul – duża deska trzymana za oba końce, ręce proste, biodra wysoko, pracujemy samymi nogami, głowa pod wodą, oddech cykliczny.

    Dokładanka do kraula – duża deska z przodu, raz pracuje lewa, a raz prawa ręka, przy jednocześnie mocnej pracy nóg.

    🟩 Grzbiet (styl grzbietowy)

    Styl pływany na plecach. Ciało ułożone w jednej linii, biodra wysoko przy powierzchni wody. Ruchy ramion naprzemienne, nogi pracują ruchem zbliżonym do kraula, ale w odwrotnym ułożeniu. Oddech swobodny, twarz cały czas nad wodą.

    Ćwiczenia techniczne:

    Ramiona grzbiet – mała deska między nogami, pracują same ramiona, kontrola ruchu i rotacji tułowia.

    Nogi grzbiet – duża deska nad głową, ręce wyprostowane, praca samymi nogami, biodra wysoko.

    Dokładanka do grzbietu – jedna ręka cały czas wyprostowana, druga wykonuje ruch, zmiana co kilka cykli.

    🟨 Styl klasyczny (żabka)

    Styl pływany na brzuchu, gdzie ruch ramion i nóg wykonywany jest symetrycznie. Ręce pracują ruchem sercowym, nogi szerokim ruchem odpychającym. Głowa wychodzi z wody przy oddechu, a potem z powrotem się zanurza.

    Ćwiczenia techniczne:

    Ramiona żabka – mała deska między nogami, praca tylko rękami, skupienie na chwytaniu i prowadzeniu wody.

    Nogi żabka – duża deska trzymana przed sobą, praca samymi nogami, głowa w wodzie, oddech co kilka cykli.

    Dokładanka do żabki – jedna praca rąk na jeden cykl nóg, skupienie na koordynacji i rytmie.

    🟥 Styl zmienny (indywidualny)

    Styl łączący cztery techniki: delfin, grzbiet, żabka i kraul. Każdy odcinek pływamy innym stylem w tej kolejności. Wymaga dobrej techniki, kondycji i płynnych przejść między stylami.

    Ćwiczenia techniczne:

    Zmiany stylów – ćwiczenie przejść między stylami (np. delfin → grzbiet, grzbiet → żabka, żabka → kraul).

    Krótkie odcinki 4×25 m – każdy odcinek innym stylem, praca nad rytmem i utrzymaniem techniki.

    Zmienny na nogi – wszystkie style pływane tylko nogami z deską.
""".trimIndent()

fun formatDate(dateString: String): String {
    return try {
        if (dateString.isBlank()) return ""
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = input.parse(dateString)
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date ?: return dateString)
    } catch (e: Exception) {
        dateString
    }
}
