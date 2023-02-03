# TDDD80 Labbar
## Theodor Larsson & Philip Mikkelä

## Lab A4 Skriftlig Förklaring:
- 1. Om man försöker bestämma bildens storlek med pixlar kan man få problem med olika skärmstorlekar. Istället bör man
     sätta bildens storlek beroende på DP som då anpassar sig till skärmstorleken. Man löste detta genom att ha DP som enhet på width
     och height i xml-filen.
- 2. Eftersom IntentService använder en lyssnare så gör det ingenting om processen tar ett tag att utföras, exempelvis att decodea koordinater. 
     Den kräver inte heller att ge en respons om lyssnaren stängs av; exempelvis om aktiviteten byts och därmed missas en "callback". AsyncTask 
     körs i bakgrunden och resultatet publiceras mot UI tråden och kräver att det finns något som tar hand resultatet om den vilket kan leda till 
     oväntade krasher, så kallade "Context leaks" och liknande. IntentService är speciellt designat för att köra bakgrundsprocesser men kräver 
     registrering i applikationens manifest.
- 3. Om hur locationManager uppdaterar positionen, om den gör det hela tiden eller om den bara tar en fixed position.

## Lab A3 Skriftlig Förklaring:
- 1. Man lägger in biblioteket i gradle under dependencies (build.gradle-filen)
- 2. JavaBeans är en standard. Alla egenskaper är privata, så man använder getters och setters. Konstruktorn
     är publik utan parameter och klassen implementerar Serializable. GSON kan konvertera json data till ett
     java objekt. Javas inbyggda parser skapar inte objekt vilket betyder att även om man har all information
     som krävs för att skapa ett objekt kommer den inte göra det och därmed har man inte tillgång till dess
     funktionalitet.
- 3. Hur man gör egna ArrayAdapters. Det är nödvändigt för att kunna skapa en egen layout för listan av meddelanden, användare och emails.

## Lab A2 Skriftlig Förklaring:
- 1. Man ska ärva från AppCompatActivity istället för Activity när man vill hämta de senaste
     funktionerna som finns tillgängliga för flest antal enheter. AppCompatActivity tillåter
     äldre enheter att använda många nya funktioner; exempelvis widgets och "support action bars".

- 2. Listview vet inte vad det är den visar i listan utan den använder sig av en adapter som sedan
     tar fram rätt objekt för att visa. Skillnaden mellan ListFragment och ListActivty är att med
     ListActivty måste man ge en custom screen layout om man inte vill att den ska täcka hela skärmen.
     Annars är dem väldigt liknande.
- 3. Hur man byter mellan fragments.

## Lab A1 Skriftlig Förklaring:
- 1. R är en autogenererad klass som innehåller definitionerna för alla resurser i en package. 
     R står för Resource. Det finns även ett annat kontext R finns i vilket är android framework paketet.
     Den används i ett statiskt kontext för att få ut t.ex layouten.
     
- 2. Gradle är ett generellt build system. Den kontrollerar development processen från kompilering, packaging,
    testning, deployment till publisering. Gradle sync letar igenom alla dependencies i build.gradle och
     laddar ner rätt version av dessa.
     
- 3. Instant run är att när man gör ändringar i sitt projekt överförs dessa direkt till emulatorn via
    en hot swap, warm swap eller cold swap beroende på vad den anser är nödvändigt. Det betyder att
     emulatorn slipper startas om från början och det blir lättare att testa mindre ändringar.
     

## Lab S4 Skriftlig Förklaring:
- 1. Det försvårar rainbow table attacker genom att göra hashnings algoritmen mer obskyr. 
    Det tillåter också flera användare att ha samma lösenord på ett säkrare sätt då om en hash för ett av 
     lösenorden läcks så är de andra fortfarande skyddade eftersom 
     deras salt såg annorlunda ut vilket resulterar i en annan hashkod även om lösenordet är detsamma.
    
- 2. Det tillåter att autentisering inte måste jämföras med databasens lösenord varje gång en handling 
     sker som kräver det. Signaturer veriferar att anslutningen sker endast mellan servern och 
     klienten utan att någon annan som inte bör ha tillgång till den skickade informationen kan nå den.
     Dock kan fortfarande middleman attacker ske mot JWS eftersom det endast signerar datan och inte 
     krypterar den. Kan använda ex JWE för utökat skydd när man skickar sårbar data
     
- 3. om bcrypt saltade sina lösenord eller om vi skulle behöva implementera den funktionen själva.
    https://en.wikipedia.org/wiki/Bcrypt#Description
    https://stackoverflow.com/questions/6832445/how-can-bcrypt-have-built-in-salts


## Lab S3 Skriftlig Förklaring:
- 1. Heroku är en molntjänst som erbjuder utvecklare att ladda upp sina applikationer
och hosta dem. När en applikation hostas på tjänster som Heroku eller på en lokal server
finns det möjlighet att skapa utomstående anslutningar. Detta tar bort behovet
av att själv hålla på med hårdvara.

- 2. SQLite är inte optimerat för kommunikation med många klienter samtidigt då
datan endast lagrar i en fil. Detta innebär att varje gång någon vill skriva mot
databasen måste den öppnas, skrivas mot och sparas. Detta kan också leda till problem
om flera instanser måste skriva mot databasen samtidigt och belasta servern. Andra
databaser löser detta mer effektivt, exempelvis PostgreSQL, mariaDB elr liknande.
För just Heroku så finns inget direkt filsystem utan det återställs varje dygn.
Att använda SQLite fungerar inte då.

- 3. Heroku Procfile och hur kommandot "heroku run" fungerade.
Vi insåg inte att kommandot behövde användas för att initiera databasen, vi antog
att *init* automatiskt kördes när applikationen startade.
https://devcenter.heroku.com/articles/procfile



## Lab S2 Skriftlig Förklaring:
- 1. Alchemy erbjuder ett lättare sätt för olika delar av databasen att relatera
till varandra. Ett par direkta fördelar som detta leder till är att data kan hämtas
på flera sätt och det är enkelt att omstrukturera datastrukturen, även i stora
databaser. Alchemy visar dessutom öppet med hjälp av olika verktyg alla relationer
i databasen och hanterar onödiga uppgifter i bakgrunden samtidigt som utvecklaren
får styra hur strukturen på databasen ser ut.
SQLAlchemy gör så att man enkelt kan använda databaser i python.

- 2. Modulär kod-struktur betyder i Flask att man separerar programmets funktioner
i oberoende moduler där varje modul ansvarar för en del av hela programmets
funktionalitet. Cirkulära importer händer när två eller fler moduler beror på och
importerar varandra. (Med moduler menas ex separata filer). Vanligtvis händer detta
om programmet är dåligt strukturerat, om en modul egentligen inte behöver
en annan modul eller om de egentligen kan kombineras till en större modul.
Ett sätt att lösa detta på om man inte vill kombinera modulerna är att endast
importera moduler när de verkligen behövs, exempelvis inuti en funktion istället
för i hela modulen.

- 3. När man byter databasstruktur så kan mycket gå fel. Det är vanligt att man missar
en variabel eller liknande vilket kan leda till att en del av programmet slutar
fungera.
När man gör ändringar i databasstrukturen bör man ta säkerhetskopieringar och sedan
skriva en konverterare som transformerar datan från den gamla databasen och skriver
mot den nya. Använd versionsnummer för att veta vilken struktur databasen har.
Det är inte alltid bara databasen som behöver ändras, därför är det viktigt att skriva
kod tester för att se att både gamla och nya fungerar eftersom
relationer också förändras vilket kan leda till problem i existerande kod.
Vi kommer att använda oss av alla de tipsen ovan.


## Lab S1 Skriftlig Förklaring:
- 1. Skillnaden mellan ramverk och bibliotek är vilket syfte de uppfyller.
Ett bibliotek uppfyller ett specifikt syfte såsom math, den sköter
matematik. Ett ramverk fyller däremot en mer grundläggande roll inom en
applikation.

- 2. Flask har flera unika funktioner som förenklar beslut och hela
utvecklingsprocessen. Den har även modulär design. Med flask får
man tillgång till många verktyg och libraries vilket är väldigt
bekvämt, speciellt när man är en nybörjare. Det gör det lättare att utveckla
en back-end för ett mindre projekt eftersom det går lätt och snabbt att
testa koden och få upp en server. Men det är också tillräckligt kraftfullt
för större projekt.

- 3. En session är tiden från att en klient anropar servern tills att man
lämnar servern. Denna data är lagrad på servern till skillnad från
cookies. Man tilldelar en session ID till varje klient för att servern ska
kunna hålla reda mellan olika klienter. I flask skickas en klients unika
session cookie till servern med varje request för att verifiera vilken klient
servern har att göra med. Cookien är secure, dock inte enkrypterad. Att den är
secure innebär att ingen kan redigera cookien utan att det märks, det går dock
att läsa av den. 
