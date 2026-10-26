---
title: "**A programozás alapjai 3:** *Nagy házi*"
subtitle: "Specifikáció"
author: "Bencsik Gergő (`I48U0D`)"
date: \today
geometry: "margin=3cm"
listings: true
mainfont: "Latin Modern Roman"
header-includes:
  - \usepackage{graphicx}\graphicspath{{./docs/spec}}
  - \usepackage{float}
  - \floatplacement{figure}{H}
  - \usepackage[bottom]{footmisc}
  - \usepackage[utf8]{inputenc}
  - \usepackage{caption}
  - \usepackage{subcaption}
---

\tableofcontents
\newpage

# Feladat leírása

A választott feladat a második világháborúban használt, módosított *Enigma* gép modellezése **objektum orientáltan** Java-ban.
Továbbá a titkosítási és beállítási funkcióihoz **grafikus felület** biztosítása.

## Enigma gép működése

Legfontosabb alkatrésze a *rotor(-ok)*. Ezek voltak, amik lehetővé tették, hogy minden billentyűnyomás után változzon az egyes betű kódolása.
A rotor-okon 26 érintkezési pont volt, ahol a billentyűk érintkeztek velük.
Ahogy a rotor elforgott, egy-egy billentyű ezáltal más helyen érintkezett immár vele.

A rotor belsejében az egyik oldal 26 pontja a másik oldalán lévő 26 ponttal keverten volt összekötve.
Ezáltal az egyik oldalon, a 26 lehetséges hely valamelyikén beérkező jel, a másik oldal 26 valamelyik véletlenszerű (de adott) pontján ment ki.

A gép modellétől függően változó darabszámú rotor volt bennük, jellemzően három.
Az első rotor minden billentyűnyomásra forgott, a második csak amikor az első teljesen körbefordult[^1], a harmadik pedig, amikor a második is egy teljes fordulatot megtett.

[^1]: Valójában a középső, mechanikai adottság miatt akkor is fordult, amikor a harmadiknak is kellett, ezáltal elveszítve egy teljes 26-os blokkot.

Ezáltal a jel a 26 pont valamelyikén bemegy, kijön valamelyik másikon, és ismét bemegy egy rotor-ba, ahol megint máshol jön ki a túloldalt, és így tovább.
Amikor a rotor elfordult, megváltozott, hogy a 26 pont melyikén jön ki a jel.

A harmadik rotor a *reflector* nevű komponensel érintkezett, amely a belé érkező jelet visszafordította, és (ezúttal visszafelé) végigküldte a rotor-okon megint.
Ez véletlenszerűen kötötte össze, párosával, az érintkezési pontokat.

\begin{figure}[H]
\centering
\begin{subfigure}[t]{0.46\textwidth}
  \centering
  \includegraphics[width=\linewidth]{./enigma-rotors.png}
  \caption{Az egyes rotorok forogtak, változtatva minden billentyűnyomás után a kimenetet.}
  \label{fig:rotors}
\end{subfigure}\hfill
\begin{subfigure}[t]{0.46\textwidth}
  \centering
  \includegraphics[width=\linewidth]{./enigma-plugboard.jpg}
  \caption{A plugboarddal változtatni lehetett, melyik billentyű „milyen betűhöz és lámpához tartozik”.}
  \label{fig:plugboard}
\end{subfigure}
\caption{A rotorok logikai működése és a plugboard.}
\label{fig:enigma-components}
\end{figure}

Később, annak érdekében, hogy még nehezebb legyen feltörni, a gép elejére szerelték a *plugboard-ot*.
Ez konfigurálható összeköttetést biztosított, hogy össze lehessen társítani betűket.
Ez az összeköttetés tulajdonképpen módosította, hogy a lenyomott billentyű valójában milyen betűt jelent, és a kimeneti jel melyik lámpához tartozik (ahonnan leolvasták a titkosított, elküldendő betűt).

## Enigma használata

A rotor-ok kezdőpozícióját azonosra beállítva, továbbá a plugboard-ot is ugyanúgy összedugdosva, két (azonos modellű) gép közül az egyik által titkosított szöveget a másik képes volt visszakódolni.
Ez a kapcsolat köztük szimmetrikus volt, azaz az azonos beállítású gépek bármelyike egyszerre ugyanazon kód szerint titkosított, és olvasta azt.

# Alkalmazás specifikációja

Alapvetően a **menu bar** az oldalakat (konfigurációs, exportálási/importálási és titkosítási) különíti el. Ezek közül kiválasztva egyet, az adott funkció oldalára visz.

## Titkosítási felület

A felület ahol a titkosítást végezni lehet, **kétpaneles**, ahol az egyik oldalra kerül a bemeneti (titkosítandó/visszafordítandó) szöveg, másik oldalon pedig egy gombnyomásra a kimenet:

![Kétpaneles felület titkosításra és visszafordításra.](./codingwindow.svg)

Fent lehet látni az előbb említett **menu bar-t** is.

## Mentés / Betöltés

A beállított rotor és plugboard konfigurációt ezután lehetőség van elmenteni egy fájlba, vagy onnan betölteni:

![Mentési, betöltési lenyíló menü.](./saveload.svg)

A fájl **XML formátumban** tárolja ezen beállításokat.
A felhasználó mentés opció kiválasztásakor kiválasztja a helyét a gépén, ahová a program menti majd a konfigurációt.
Betöltés opció választásakor pedig, a már elmentett fájl helyét választja ki, ahonnan a program betölti majd azt.

# Felhasznált források

- [`Pandoc`](https://pandoc.org/): Markdown-ból (némi LaTeX kiegészítéssel) PDF generálása.
- [`PlantUML (Salt)`](https://plantuml.com/salt): wireframe ábrákhoz
- [`graphviz DOTlang`](https://graphviz.org/doc/info/lang.html): plugboard összeköttetés ábrához
- Képek, információk forrásai:
  - Rotor ábra: [*Wikipédia*](https://commons.wikimedia.org/wiki/File:Enigma-action.svg)
  - Plugboard kép: Úgyszintén [*Wikipédia*](https://commons.wikimedia.org/wiki/File:Enigma-plugboard.jpg), azonban eredetileg [*innen származik*](http://www.ilord.com/enigma)
