# Sprint 7:

## Sprintin tavoite

Varmistaa Classroom Attendance System -sovelluksen toimivuus Sprint 6:n hyväksymiskriteerejä vasten, korjata testauksessa löytyvät tärkeimmät virheet ja tuottaa todennettava CI/CD- sekä staattisen analyysin tulos. Päivittää bugiseuranta, tuotebacklog ja tekninen dokumentaatio vastaamaan sprintissä tehtyjä muutoksia.
## Sprintin kesto

- 2 viikkoa

## Valitut tuotebacklogin kohdat

- Viimeistellä toiminnallinen ja ei-toiminnallinen testisuunnitelma.
- Ajaa yksikkötestit koodin siistimisen ja korjausten jälkeen sekä korjata löydetyt regressiot.
- Käsitellä ja päivittää UAT- ja heuristisessa arvioinnissa löydetyt bugit ja issuet.
- Tarkentaa Trellon user storyt, hyväksymiskriteerit ja sprintin tehtävät vastaamaan nykyisiä ominaisuuksia.
- Suorittaa Jenkinsissä build, testit, JaCoCo-raportti, SonarQube-analyysi ja Docker-kuvan rakentaminen/julkaisu.
- Varmistaa SonarQuben laatuluokka: tavoite A, vähintään B kaikissa raportoiduissa luokissa.
- Päivittää arkkitehtuuri-, määritys- ja käyttöohjeet testien tulosten perusteella.

## Suunnitellut tehtävät ja erittely

- Testisuunnitelma ja testiaineisto
    - Kirjataan testauksen tavoite, resurssit, ympäristö sekä toiminnalliset ja ei-toiminnalliset testit. 
    - Valmistellaan synteettinen testiaineisto ja varmistetaan, että MariaDB-testien rajat dokumentoidaan. 
- Yksikkö- ja integraatiotestaus
    - Ajetaan koko Maven-testisarja koodin siistimisen jälkeen ja tallennetaan testitulokset. 
    - Korjataan testien paljastamat regressiot ja lisätään testit korjatuille toiminnoille.
- Bugien ja issueiden käsittely
    - Testataan asettelu, kielivalikoima sekä MariaDB:n tallennus.
- Backlogin tarkennus
- CI/CD ja staattinen analyysi
    - Suoritetaan Jenkins-putki: checkout, Maven build/test, JaCoCo, SonarQube. 
    - Tarkistetaan SonarQube Quality Gate, korjataan korkean prioriteetin löydökset ja tallennetaan raporttinäkymän kuvakaappaus. 



## Tiimin kapasiteetti ja oletukset

- Jenkins-, SonarQube- ja Docker Hub -tunnukset sekä palvelut ovat CI/CD-vastaavan käytettävissä.
- Jenkinsfile sisältää build-, test-, JaCoCo-, SonarQube- ja Docker-vaiheita. Putken onnistuminen ja Quality Gate on silti todennettava ajokohtaisista raporteista.



## Valmiin työn määritelmä

- Testisuunnitelma kattaa toiminnalliset ja ei-toiminnalliset vaatimukset, testiresurssit ja ympäristön.
- Lopullinen yksikkötestiajo on tehty koodin siistimisen jälkeen; tulokset ja korjatut viat on dokumentoitu.
- Bugiseuranta sisältää omistajan, tilan, hyväksymisehdon ja uudelleentestauksen näytön; ratkaisemattomat asiat jäävät avoimiksi.
- Jenkinsissä on onnistunut build/test/analyysi/Docker-ajo ja SonarQube Quality Gate täyttää vähintään B-tason vaatimuksen; raportista on tallennettu kuvakaappaus.
- Dokumentaatio, arkkitehtuurimuutokset ja Sprint 7 -raportti on päivitetty GitHubiin.