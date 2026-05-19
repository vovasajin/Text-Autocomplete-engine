# Text Autocomplete Engine

Aplicatie mobila Android realizata in Kotlin pentru practica, cu interfata moderna si motor de cautare bazat pe `Trie`.

## Prezentare generala

Proiectul demonstreaza cum o structura de date eficienta poate fi folosita intr-un caz real: autocomplete pentru cuvinte si expresii. Utilizatorul introduce un prefix, iar aplicatia afiseaza rapid sugestii ordonate dupa frecventa sau alfabetic.

## Functionalitati implementate

- adaugare cuvinte sau expresii in dictionar
- actualizare frecventa pentru o intrare existenta
- stergere intrari
- cautare exacta
- sugestii dupa prefix
- limitarea numarului de sugestii
- ordonare dupa frecventa sau alfabetic
- stocarea popularitatii fiecarui cuvant
- suport pentru expresii, nu doar cuvinte simple
- salvare si incarcare din fisier JSON local
- fuzzy matching pentru typo-uri mici
- dashboard cu statistici pentru prezentare

## MVP

- interfata Android functionala
- implementare `Trie` in Kotlin
- dictionar local persistent
- operatii CRUD pentru dictionar
- motor de sugestii pregatit pentru demo

## Planificare

1. implementarea structurii `TrieNode` si a clasei `Trie`
2. construirea repository-ului pentru salvare/incarcare JSON
3. conectarea logicei cu interfata Android
4. imbunatatirea UI-ului si adaugarea statisticilor
5. testare unitara pentru algoritmul principal

## Structura proiectului

- `app/src/main/java/com/example/textautocompleteengine/data/Trie.kt` - logica structurii Trie
- `app/src/main/java/com/example/textautocompleteengine/data/AutocompleteRepository.kt` - dictionar, persistenta si fuzzy matching
- `app/src/main/java/com/example/textautocompleteengine/MainActivity.kt` - logica ecranului principal
- `app/src/main/res/layout/activity_main.xml` - designul principal al aplicatiei
- `app/src/test/java/com/example/textautocompleteengine/data/TrieTest.kt` - teste unitare pentru Trie

## Rulare in Android Studio

1. deschide proiectul in Android Studio
2. asteapta `Gradle Sync`
3. ruleaza aplicatia pe emulator sau pe telefon

## GitHub

Proiectul este organizat ca proiect Android Studio standard, deci poate fi incarcat direct intr-un repository GitHub. Fisierele de build, sursele Kotlin, resursele XML si README-ul sunt deja structurate pentru asta.

## Observatie

In acest mediu nu este instalat `Java`, deci nu am putut rula local comenzi de build precum `./gradlew.bat assembleDebug`. Verificarea finala a build-ului trebuie facuta in Android Studio pe masina ta.
