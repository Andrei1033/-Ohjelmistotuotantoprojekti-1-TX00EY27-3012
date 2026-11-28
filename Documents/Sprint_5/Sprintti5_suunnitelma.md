# Sprint 5:

## Sprintin tavoite

Valmistella Classroom Attendance System -sovelluksen tietokanta monikieliselle kurssi- ja oppituntisisällölle. 
Säilyttää nykyiset tiedot ja mahdollistaa sisällön hakeminen valitulla kielellä. 
Jos käännöstä ei löydy, käytetään sovittua varakieltä.

## Sprintin kesto

- 2 viikkoa

## Valitut tuotebacklogin kohdat

- Suunnitella tietomalli kurssien nimien ja oppituntien aiheiden käännöksille.
- Lisätä kielikohtaiset käännöstaulut ja turvalliset tietokantamigraatiot.
- Säilyttää kurssien ja oppituntien nykyiset tiedot migraation aikana.
- Toteuttaa kielikohtainen tiedonhaku ja toiminta puuttuvan käännöksen tilanteessa.
- Varmistaa suomen-, englannin-, venäjän-, japanin- ja arabiankielisen tekstin tallennus ja haku.
- Päivittää tietokannan dokumentaatio ja ER-kaavio.


## Suunnitellut tehtävät ja erittely

- Monikielisen tietomallin suunnittelu
    - Vahvistetaan Product Ownerin kanssa käännettävät kentät ja nykyisten tietojen oletuskieli.
    - Päivitetään ER-kaavio sekä määritellään kielitunnisteet, avaimet, rajoitteet ja varakielen käyttö. 
- Tietokantarakenteen päivittäminen
    - Luodaan migraatiot kurssien ja oppituntien käännöstauluille sekä määritellään pääavaimet, viiteavaimet ja yksilöllisyysrajoitteet. 
    - Siirretään nykyiset kurssien nimet ja oppituntien aiheet säilyttäen tunnisteet ja tiedot. tietokantakehittäjä ja testaaja.
- Kielikohtaisen tiedonhaun toteutus
    - Päivitetään tietokantakerros hakemaan valitun kielen käännös ja käyttämään sovittua varakieltä, jos käännös puuttuu. 
- Monikielisen tiedon testaaminen
    - Testataan ääkkösten, kyrillisten kirjainten, japanilaisten merkkien ja arabian tallennus ja haku sovelluksen tietokantayhteyden kautta. 
    - Testataan kurssi-, oppitunti- ja läsnäolotoiminnot migraation jälkeen sekä kirjataan löydökset. 
- Dokumentaation päivittäminen


## Tiimin kapasiteetti ja oletukset



## Valmiin työn määritelmä

- vahvistanut käännettävät kentät, kielitunnisteiden muodon, oletuskielen ja varakielen käytön.
- Tietokantamigraatiot luovat hyväksytyn käännösrakenteen ja säilyttävät nykyiset tietueet sekä niiden yhteydet.
- Sovellus hakee kurssin nimen ja oppitunnin aiheen valitulla kielellä ja käsittelee puuttuvan käännöksen sovitulla tavalla.
- Tietokantakerroksen tarkistukset sekä kurssi-, oppitunti- ja läsnäolotoimintojen regressiotestit on tehty.
- ER-kaavio ja tietokantadokumentaatio on päivitetty GitHubiin.
- Sprint atselmointiraportti -esittelyn näyttöaineisto, valmis työ ja avoimet ongelmat on dokumentoitu.