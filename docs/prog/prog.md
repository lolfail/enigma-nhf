---
title: "**A programozás alapjai 3:** *Nagy házi*"
subtitle: "Programozói dokumentáció"
author: "Bencsik Gergő (`I48U0D`)"
date: \today
geometry: "margin=3cm"
listings: true
mainfont: "Latin Modern Roman"
header-includes:
  - |
    \usepackage{xeCJK}
    \setCJKmonofont{WenQuanYi Micro Hei Mono}
  - \usepackage{graphicx}\graphicspath{{./docs/prog}}
  - \usepackage{float}
  - \usepackage{svg}
  - \floatplacement{figure}{H}
  - \usepackage[bottom]{footmisc}
  - \usepackage[utf8]{inputenc}
  - \usepackage{caption}
  - \usepackage{subcaption}
---

\tableofcontents
\newpage

# Build környezet, annak használata és a dokumentáció

A projekt [_Maven-t_](https://maven.apache.org/) használ a különféle munkafolyamatok futtatására, mint például a tesztek indítása, vagy a fordítás és csomagolás.

## Futtatás

A projekt a következő parancsok kiadásával futtatható:

```sh
mvn compile
mvn exec:exec
```

Ez azért van így, mivel fejlesztés során néhány hiba került elő, melyet explicit argumentum megadással lehetett megoldani.

Egy ilyen például, hogy (linux feletti) Wayland-en, a program nem jelenítődik megfelelően meg.
Ezt a következő változókkal sikerült orvosolni: [^1]

```sh
_JAVA_AWT_WM_NONREPARENTING=1 GDK_BACKEND=x11 mvn exec:java
```

[^1]: Ez természetesen nem végleges megoldás, de a projekt célja nem az, hogy Wayland alatt fusson specifikusan a program, így számunkra most lényegtelen.

## Tesztelés

Teszteléshez [_JUnit-ot_](https://docs.junit.org/), használjunk.
A program tesztelhető a következőképp:

```sh
mvn test
```

Emellett [_JaCoCo_](https://www.eclemma.org/jacoco/trunk/doc/maven.html) van integrálva a Maven buildfolyamatba, annak érdekében, hogy mérhessük a teszteléskor érintett ágakat, stb.

Ez elérhető a projektkönyvtárból: `./target/site/jacoco/index.html`, és a teszteléshez használt parancs futtatása után generálódik.

## Dokumentálás

A dokumentáció három részre bomlik:

- **Felhasználói dokumentáció**: A felhasználónak íródik, segítse a programot használni, kezelni.
- **Programozói dokumentáció**: Fejlesztőnek íródik, nagyobb, architektúrális döntésekről adjon képet.
- **Programkód dokumentációja**: Fejlesztőknek íródik, aktív programozás során segítse megérteni, mi-mit csinál.

### Programozói és felhasználói dokumentációk

Ezekhez elsősorban a [pandoc](https://pandoc.org/) nevű programot használjuk, mely alkalmas arra, hogy markdown dokumentumot (esetlegesen kis LaTeX szintaxissal kiegészítve) PDF formátumba generáljon.
<!-- TODO! -->

### Programkód dokumentációja

Ehhez használjunk _JavaDoc_ stílusú kommenteket. Igyekezzünk jellemezni az összes paramétert és a visszatérési értéket minden esetben.
<!-- TODO! -->

# Projekt struktúrája

A projekt a Maven konvenciót követi, a forráskód az `src` directory-ba kerül, azon belül a `main`-be maga az alkalmazásé, `test`-en belülre pedig a teszteléshez használt kód:

- `src/`
  - `main/java/`
  - `test/java/`

Ezen kettőn belül van egyedül egy `java` directory (hiszen tisztán Java-t használ a projekt), majd azon belül az egyes `package`-ek directory-jai, amelyben (esetleg még al-`package`-eké) pedig a forrásfájlok.

A `test` tükrözi a `main` tartalmát, az egyes (feltéve, hogy tesztelt az adott `class`) fájlok nevei megegyeznek a kettőben, csak a tesztelők végére oda van szúrva a `Test` szó.

Jelenleg a struktúra a következő:

- `Main.java`: A program belépési pontja, itt van a `main` függvény.
- `utils/`: Segédosztályokat, speciális konténereket tartalmaz, melyeket a projekt használ.
- `components/`: Magát az enigma gépet és komponenseit modellező osztályok, és segédosztályaik.
- `validators/`: A modellezett gép validációját, paraméterek ellenőrzését leíró "szabályok".
- `graphical/`: Grafikai logikát definiáló, megjelenítéssel foglalkozó osztályok.

# Enigma gép modellezése

\begin{figure}[H]
\centering
\begin{subfigure}[t]{0.56\textwidth}
  \centering
  \includesvg[inkscapelatex=false, width=\linewidth]{enigmamodel.svg}
  \caption{\texttt{EnigmaMachine} felbontása annak komponenseire.}
  \label{fig:enigmamodel}
\end{subfigure}\hfill
\begin{subfigure}[t]{0.36\textwidth}
  \centering
  \includesvg[inkscapelatex=false, width=\linewidth]{utilsmodel.svg}
  \caption{Kapcsolatok az egyes utility osztályok közt.}
  \label{fig:utilsmodel}
\end{subfigure}
\caption{Az alap komponensek modellezési döntései, szétbontva könnyebb átláthatóság érdekében.}
\label{fig:enigma-classes}
\end{figure}

## Utility osztályok

Két utility osztály is implementálva van.

Egyik a `PairMap<A, B>`. Ez egy generikus `Map<A, B>` implementáció, mely egyértelműen összetársít `A` és `B` beli elemeket (mind `A` és `B` kulcsa).

Másik pedig a `SelfMap<T>`. Ez felhasználja az előzőt, és olyan párosításokat létesít, ahol tulajdonképpen egy csoport elemeit önmagával rendeljük össze egyértelműen.
Az, hogy felhasználja a `PairMap`-et és úgy valósít meg ilyesféle társításokat, mutatja, hogy van valami kapcsolat valóban a kettő logikája közt.

Viszont különbség például, hogy a `SelfMap` esetén tilthatjuk az identitás összerendeléseket (ahol például 1-et 1-hez rendeljük).

Ez például (és specifikusan) ott hasznos, amikor a _reflector_-esetén tiltjuk, viszont _plugboard_-nál megengedjük őket.
Hiszen _reflector_-nál szó sem lehet róla, hogy ugyanazon az úton küldjük vissza a jelet, azonban _plugbard_-nál ha nem dugunk be valamit akkor az önmagaként lép a _rotor_-okba.

## `EnigmaMachine` felépítése

Az `EnigmaMachine` a legfelső réteg, a kész gép, ami képes "megkavarni" a bemenetet és kiadni azt.
Egyrészt ő lesz az, aki kezeli a _plugboard_-ot, de egyfajta magasabb szintű absztrakciót is biztosít a kényelmesebb[^2] használat érdekében.

[^2]: Kényelemmel kapcsolatban: ha a `RotorCompartment`-et használnánk, akkor nem is egy getter/setter metóduson keresztül kéne dolgozni a géppel. Így viszont egy metódus, egy index és set esetén még egy paraméterrel kezelhetjük.

Másrészt viszont elméletben tökéletesen lehetne már a `RotorCompartment` osztályt is használni, hiszen számos logikai funkciót az biztosít (vagy az azalatti `Rotor`-ok amik alkotják),
azonban biztosra vélhető volna, hogy így sok kódduplikáció alakulna ki (egyrészt vagy sok ugyanolyan, vagy ami mégrosszabb, sok különböző).

Ennek érdekében definiál számos magasabb szintű metódust amivel kezelhető a gép.

Ábráról még talán kevésbé érthető, miért van gyenge asszociáció a `Rotor`-nál önmagára?
Ez azért van, mivel amikor meghívjuk a `Rotor` forgatás metódusát, hogy az egy pozícióval odébb forduljon, akkor csak önmagát tudná forgatni, így viszont, ha körbefordult, forgathatja a következőt.
És azért gyenge, mert nem kötelező egyáltalán megadni ki a következő, ha nincs, akkor csak simán önmagát forgatja.

Természesen lehetett volna kezelni a körbeforgást egy szinttel feljebb, azonban úgy tűnt, hogy most ez a legtisztább megoldás.

Ugyanakkor ezáltal a `RotorCompartment` egy láncolt listává vált, melynek elemei `Rotor`-ok.
Valójában ő belsejében `ArrayList`-et használ, így vigyázni kell a `List<Rotor>` interfész megvalósításakor a hozzáaadás, törlés és felülírás műveletekkor.

## Egyéb komponensek

### `TranslationLayer`

A `TranslationLayer` egy olyan segédosztály, ami izolált az `EnigmaMachine`-től, olyan értelemben, hogy nem függnek egymástól önmagukban.
Azonban a célja során nagyon is szoros kapcsolat kell, hogy kialakuljon a kettő közt.
Ugyanis magában az `EnigmaMachine` számokkal dolgozik (ráadásul 0-tól kezdődően a pin-ek számáig), viszont a tényleges enigmát betűk titkosítására használjuk.

Pont erre való ez, ahogy neve is utal rá, feltöltjük bele, hogy melyik számhoz melyik betűt kívánjuk rendelni, ezután mindkét irányba végezhetjük a "fordítást".
Erre persze alkalmas volna egy egyszerű `PairMap<Character, Integer>` is, azért érdemes használni egy külön osztályt rá, hogy egyéb segédmetódusokat is definiálhassunk.

Ilyen segédmetódus, például egy teljes `PairMap<Character, Character>` átfordítása `PairMap<Integer, Integer>`-ré.
Emellett érdemes valamely alapértelmezett "ábécé"-t is megadni. Ezt is ez teszi meg, ha mi nem.

### `EngimaMarshaller`

Talán kissé régies kifejezés, de _Marshaller_-ként ennek feladata, hogy az eltárolt objektumot valamely külső formátummá alakítsa.

Ez a külső formátum most éppen az `XML`. Ennek pontos formátumáért lásd a specifikációt.

Használatügyileg, az osztály nem birtokolja a kiírandó `EnigmaMachine` osztályt, se veszi azt át konstruktorban.
Konstruktorában át lehet adni, hogy 1 indexelt, vagy 0 indexelten szeretnénk kezelni.
Ez ha a specifikációt be akarjuk tartani, 1 indexelt kell, hogy legyen.

Majd meghívjuk a respektív `.read(Path)` (visszadva az `EnigmaMachine`-t) vagy `.write(Path, EnigmaMachine)` metódusokat.

# Megjelenítés struktúrája és annak felépítése

![A `graphics/` directory-ban található főbb osztályok kapcsolata.](./graphicsmodel.svg)

Mint említve volt, a grafika valamelyest elkülönítve, a `graphics` subdirectory-ban van, az ugyanilyen nevű `package`-ben.

A `main` függvény megjeleníti a szokványos `SwingUtilities.invokeLater` metódussal a `MainFrame`-et, amelyben beállításra kerül a `MenuBar`, ahonnan lehet elérni a mindenféle funkcióit a programnak.

A legfontosabb a `CryptoPanel` osztály, ez az, ahol a felhasználó számára elérhetővé tesszük a szövegmezőket és a titkosítás lehetőségét.

Továbbá ott van még a `FileMenu`, mely nem egy `JPanel`-t definiál (nem is tartozik hozzá "view") hanem csak szimplán menüsoron elérhető, lenyíló menüben afféle gombokként kezelhető menüelemeket kreál.
Ezek, ahogy a név is utal rá, a mentés és betöltési ablakot nyitják meg, és felülírják a jelenlegi `EnigmaMachine`-t.

A `MapperPanel` olyan módon különleges, hogy az egy generikus `JPanel` megvalósítás. Generikus típusos értelemben persze: `MapperPanel<V extends Map<Integer, Integer>>`.
De generikus olyan értelemben is (és ebből származik az előbbi), hogy a specifikációban megadott, összerendelési interfész (UI értelemben) az meglehetősen általános.
Ezt a stílusú interfészt valósítja ez meg.

Ezáltal könnyen felhasználható mind a _reflector_, a _plugboard_ és az egyes _rotor_-ok belső összeköttetésének kezelésére.
