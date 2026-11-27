# enigma-nhf

A teljes projekt fent van githubon: https://github.com/lolfail/enigma-nhf
Ha netán a JPorta megenne valamit...

- felhasználói dokumentáció: `docs/userdoc.pdf`
- programozói dokumentáció: `docs/progdoc.pdf`
- specifikáció: `docs/spec.pdf`
- osztálydiagram: (lásd lejjebb miért van 2)
  - `docs/classisolated.svg`
  - `docs/classconnected.svg`
- kódbázis:
  - forráskód: `src/main/`
  - tesztkód: `src/test/`

Illetve mivel az alapértelmezés ki se használja az enigma rotorjainak forgását, egy konfiguráció amit be lehet tölteni a grafikus felületen `File>Load` menüpontban.
Ez a `ukwb.xml` (UKW-B).

Futtatni:
`mvn clean compile package && java -jar target/enigma-0.jar`

## Dokumentációk

Talán érdemes elsőként elindítani a programot és kipróbálni, utána (vagy közben) a programozói dokumentációt olvasni.

A felhasználói dokumentáció ugyan kötelező, de nem ad valami sokat a programhoz, mert annyira egyszerű a UI.
Próbáltam kicsit feltuningolni legalább GIF-ekkel, de mivel LaTeX-en keresztül megy a pandoc, az ötlet elég korán elhalt...

## Kódbázis

Az `src/main/java/` directory-n belül az összes azalatti **directory már egyben packaget** is jelöl, mely neve megegyezik a directory nevével.

Valószínűleg érdemes említeni, hogy a kód mellett az összes komment is angolul van (még a javadoc-osak is).
Ez csak preferencia kérdése volt, szerintem nem elegáns a nem magyar nyelvű kód mellé magyar kommenteket szúrni (a kód meg ne legyen magyar)...

Félreértés elkerülése végett, nem LLM slop miatt van angolul.
LLM a kódhoz nem nyúlt (de egyébként dokumentációhoz sem).
Egy kicsit kevésbé passzív-agresszív StackOverflow frontendként használom általánosan is, afféle keresőmotorként.

## Beadott diagramokról

A különbség a `classisolated.svg` és `classconnected.svg` közt az, hogy a **connected az a másikhoz képest egy picivel több kapcsolatot** mutat.
Azonban ez nem biztos, hogy megéri minden esetben, mert a szépségét a diagramnak nem picit rontja, a másik sokkal olvashatóbb.

Ugyanitt maradva lehet gyanús, hogy a **utility osztályok semmihez sincsenek kötve**, ez azért van, mert ha oda lenne kötve, olvashatatlanná válna a diagram.
Inkább felvettem például a `reflector` osztályt, amit sztereotipizáltam `SelfMap<Integer, Integer>`-re.
Így nem kutyulja össze a diagramot feleslegesen, beszédesebb és látszik az `EnigmaMachine` szép fa struktúrában.
