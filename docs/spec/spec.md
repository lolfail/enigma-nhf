---
title: "**A programozás alapjai 3:** *Nagy házi*"
subtitle: "Specifikáció"
author: "Bencsik Gergő (`I48U0D`)"
date: \today
geometry: "margin=3cm"
listings: true
mainfont: "Latin Modern Roman"
header-includes:
  - |
    \usepackage{xeCJK}
    \setCJKmonofont{WenQuanYi Micro Hei Mono}
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

Legfontosabb alkatrésze a *rotor(-ok)*. Ezek voltak, amik lehetővé tették, hogy minden billentyűnyomás után "változzon" az egyes betűk kódolása.
A rotor-okon 26 érintkezési pont volt, ahol a billentyűk érintkeztek velük.
Ahogy a rotor elforgott, egy-egy billentyű ezáltal más helyen érintkezett immár vele.

A rotor belsejében az egyik oldal (tegyük fel 26 pontja) a másik oldalán lévő ugyanúgy 26 ponttal keverten volt összekötve.
Ezáltal az egyik oldalon, a 26 lehetséges hely valamelyikén beérkező jel, a másik oldal 26 valamelyik véletlenszerű (de adott) pontján hagyta el.

A gép modellétől függően változó darabszámú rotor volt bennük, jellemzően három.
Az első rotor minden billentyűnyomásra forgott, a második csak amikor az első teljesen körbefordult[^1], a harmadik pedig, amikor a második is egy teljes fordulatot megtett.

[^1]: Valójában a középső, mechanikai adottság miatt akkor is fordult, amikor a harmadiknak is kellett, ezáltal elveszítve egy teljes 26-os blokkot.

Ezáltal a jel a 26 pont valamelyikén bemegy, kijön valamelyik másikon, és ismét bemegy egy rotor-ba, ahol megint máshol jön ki a túloldalt, és így tovább.
Amikor a rotor elfordult, megváltozott, hogy a 26 pont melyikén jön ki a jel.

A harmadik rotor a *reflector* nevű komponensel érintkezett, ez véletlenszerűen, párosával összekötötte a 26 pontot egymással.
Így a belé érkező jelet visszafordította, és (ezúttal visszafelé) végigküldte a rotor-okon megint.

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

A felület ahol a titkosítást végezni lehet, **kétpaneles**, ahol az egyik oldalra kerül a bemeneti (titkosítandó/visszafordítandó) szöveg, másik oldalon pedig gombnyomásra a kimenet jelenik meg:

![Kétpaneles felület titkosításra és visszafordításra.](./codingwindow.svg)

Fent lehet látni az előbb említett **menu bar-t** is.

## Plugboard konfigurációja

Továbbá legyen olyan felület is, ahol a **plugboard konfigurálható**.

![Plugboard konfigurációs felülete.](./plugboardconfig.svg)

Ahol az *input box*-ba beleírva a szóközzel elválasztott karakterpárosításokat, a felhasználó létrehoz karakter-összetársításokat.
Ez a felület jelezze vissza, hogy sikerült-e a társítás, vagy ha sikertelen akkor hogy miért:

| Hiba oka                      | Példa      |
| ---                           | ---        |
| Egy karakterhez több társítás | `AD AB FG` |
| Illegális formátum            | `AD BFG`   |
| Illegális karakter            | `AD 你好`  |
| Karakter önmagával összekötve | `AA BF`    |

## Rotor-ok konfigurációja

Legyen olyan felület, ahol az egyes rotor-ok, **kezdőállapotát beállítani** lehet.
Illetve egyáltalán azt, hogy **hány rotor legyen** a gépben.
Továbbá természetesen azt, hogy a belsejükben hogyan legyenek **összekötögetve** azok.

![Rotor-ok számának és kezdőállapotának beállítása.](./rotorconfig.svg)

Míg a valós gépek esetén természetesen már egy gombnyomás után elforogtak a rotor-ok, amiket újrakezdéskor vissza kéne állítni, az alkalmazás ezt megteszi helyettünk.
Olyan értelemben, hogy a beállított rotor-okat nem szükséges visszaállítani kódolás végeztekor.
Más szóval, azok konfigurációikor a kezdőállapotot állítjuk be, és nem a rotor-ok jelenlegi állapotát.

A rotor-ok összeköttetésének konfigurációja hasonló felületen történik.
Természetesen azon különbséggel, hogy míg plugboard esetén nem lehetett összekötni "két azonos betűt", itt ez megtehető, mivel összesen 2*26 betű van a két oldalon összesen.

## Mentés / Betöltés

A beállított rotor és plugboard konfigurációt ezután lehetőség van elmenteni egy fájlba, vagy onnan betölteni:

![Mentési, betöltési lenyíló menü.](./saveload.svg)

A fájl **XML formátumban** tárolja ezen beállításokat.
A felhasználó mentés opció kiválasztásakor kiválasztja a helyét a gépén, ahová a program menti majd a konfigurációt.
Betöltés opció választásakor pedig, a már elmentett fájl helyét választja ki, ahonnan a program betölti majd azt.

Konfigurációs állomány specifikációját lásd alább.

# Konfigurációs fájl

A konfigurációs állománynak a következőket kell tárolnia (implicit vagy explicit):

- Plugboard összeköttetései
- Egyes rotor-ok összeköttetése
- Rotor-ok kezdőpozíciói (állásuk)

A legfelső _element_ az `EnigmaMachine`.
Ezen belül található a `Plugboard` és a `Rotors` (rotor-ok).

```xml
<EnigmaMachine>
    <Plugboard></Plugboard>
    <Rotors></Rotors>
</EnigmaMachine>
```

Innentől a dokumentum csak a releváns, onnanstól elhelyezkedésileg beazonosítható szülőt mutatja.

## Plugboard

Ezen belül találhatók az összeköttetések, mégpedig a következő formában:
Minden összeköttetést egy `Cable` tárol, melynek két attribútuma van, `from` és `to`.
Ezek tárolják a kábel, összeköttetés két végpontját.

Megjegyzendő, hogy ezek az összetársítások teljesen szimmetrikusak, akár fel is cserélhető tetszőlegesen a két attribútum által tárolt érték.

```xml
<Plugboard>
    <Cable from="C" to="A"/>
    <Cable from="F" to="Z"/>
</Plugboard>
```

## Rotor-ok

# Felhasznált források

- [`Pandoc`](https://pandoc.org/): Markdown-ból (némi LaTeX kiegészítéssel) PDF generálása.
- [`PlantUML (Salt)`](https://plantuml.com/salt): wireframe ábrákhoz
- [`graphviz DOTlang`](https://graphviz.org/doc/info/lang.html): plugboard összeköttetés ábrához
- Képek, információk forrásai:
  - Rotor ábra: [*Wikipédia*](https://commons.wikimedia.org/wiki/File:Enigma-action.svg)
  - Plugboard kép: Úgyszintén [*Wikipédia*](https://commons.wikimedia.org/wiki/File:Enigma-plugboard.jpg), azonban eredetileg [*innen származik*](http://www.ilord.com/enigma)
