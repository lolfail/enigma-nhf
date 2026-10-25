---
title: "**A programozás alapjai 3:** *Nagy házi*"
subtitle: "Specifikáció"
author: "Bencsik Gergő (`I48U0D`)"
date: \today
geometry: "margin=3cm"
listings: true
mainfont: "Latin Modern Roman"
header-includes:
  - \usepackage{float}
  - \floatplacement{figure}{H}
  - \usepackage[bottom]{footmisc}
  - \usepackage[utf8]{inputenc}
  - \usepackage{caption}
  - \usepackage{subcaption}
---

\tableofcontents
\newpage

# A feladatról...

A választott feladat a második világháborúban használt, módosított *Enigma* gép modellezése **objektum orientáltan** Java-ban.
Továbbá a titkosítási és beállítási funkcióihoz **grafikus felület** biztosítása.

## Enigma gép működése

Legfontosabb alkatrésze a *rotor(-ok)*. Ezek voltak, amik lehetővé tették, hogy minden billentyűnyomás után változzon az egyes betű kódolása.
Az egyes billentyűk vezetékei valamely (számunkra lényegtelen) módon érintkeztek egy fix ponton a bemeneti rotor-ral.

A rotor belsejében az egyik oldalának jelvezető pontjai a másik oldalának pontjaival keverten voltak összekötve.
Ezáltal az egyik oldalon, 26 lehetséges hely valamelyikén beérkező jel, a másik oldal 26 valamelyik véletlenszerű (de adott) pontján ment ki.
Amikor az operátor lenyomott egy billentyűt, az rotor elforgott, ezáltal megváltoztatva, hogy melyik ez a kimeneti pont.

A gép modellétől függően változó darabszámú rotor volt bennük, jellemzően három.
Ennek példáját véve, a három rotor érintkezett egymással, és a 26 pontjuk valamelyikén átadták egymásnak a jelet.

Az első rotor minden billentyűnyomásra forgott, a második csak amikor az első teljesen körbefordult, a harmadik pedig, amikor a második is egy teljes fordulatot megtett.

A harmadik rotor a *reflector* nevű komponensel érintkezett, amely a belé érkező jelet visszaküldte a harmadik rotor-ba.
Ez nem egy mozgó alkatrész, csupán keverten összekötötte a rotor pontjait egy másik (nem azonos) pontjával.
Ezáltal a jel végigment a rotor-okon megint.

\begin{figure}[!h]
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

A kijövő jel ekkor feloltotta volna a lámpát, amely már a titkosított betű amit feljegyeztek és elküldtek.
Azonban annak érdekében, hogy még több lehetőség legyen, a gép elejére szerelték a *plugboard-ot*.
Ez csak annyit csinált, hogy a lenyomott billentyűhöz társított, rotor-okba bemenő és azokból kimenő jelet, konfigurálhatóan megkeverte.

## Enigma használata

A valóságban egy komplexebb protokollt használtak, mely hozzájárult a titkosítás feltöréséhez.
Továbbá az operátoroknak volt ráhatásuk az aznapi titkosításra, a kapott beállítások után elküldtek azon való módosításokat, melyeket aztán mindketten beállítottak.
Ez rajtuk múlott és gyakran használtak erre valamely ismerősük nevét.

Azonban ami fontos, hogy a rotor-ok kezdőpozícióját beállítva, továbbá a plugboard-ot adott módon összedugdosva, két (kompatibilis) gép ugyanazon kimenetet adták.
Illetve, a titkosított szöveget beírva, visszaadta az eredetileg gépeltet.
