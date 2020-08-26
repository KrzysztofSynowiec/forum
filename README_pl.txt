Zadanie polega na zaprojektowaniu i zaprogramowaniu prostego backendu (HTTP + REST) dla forum dyskusyjnego w stylu... http://kafeteria.pl/forum 
Jako niezalogowany użytkownik:
- mogę założyć nowy temat dyskusji, podając: temat, treść posta, pseudonim i adres email,
- mogę odpowiedzieć na wybrany temat dyskusji, podając: treść posta, pseudonim i adres email,
- mogę edytować post (jego treść), podając: nową treść, sekret dla danego posta - unikalny sekret powiązany z postem jest otrzymywany przy każdym dodaniu nowego posta (mechanizm analogiczny do edytowania poprzez link w mailu - tylko uproszczony),
- mogę kasować post podając sekret dla danego posta,

- mogę przeglądać listę tematów posortowanych od najbardziej do najmniej aktywnych tematów (aktywność tematu mierzymy tym, kiedy ostatnio ktoś w nim odpowiadał), dodatkowo mogę podać limit i offset, żeby otrzymać prostą paginację (limit powinien być ograniczony maksymalną wartością wczytaną z pliku konfiguracyjnego - żeby nie dało się wyenumerować całej zawartości bazy “na raz”)

- mogę przeglądać odpowiedzi na dany temat od najstarszego (pierwszego) do najnowszego posta; tutaj paginacja jest trochę trudniejsza (a’la Reddit jeśli kojarzysz) - podajemy id elementu “środkowego”, liczbę elementów przed i liczbę elementów po elemencie środkowym (według daty dodania) - na tak spaginowanej liście znajduje się zawsze element środkowy i liczba elementów przed i po nim; ale uwaga - nadal obowiązuje nas maksymalny limit wczytany z pliku konfiguracyjnego (wspominane wcześniej) - w przypadku jeśli MAX_LIMIT < (before + after + 1) musimy obciąć listę proporcjonalnie - np. jeśli MAX_LIMIT = 50, before = 50, after = 100, id = X (before:after = 1 : 2) to na liście będziemy mieć 50 elementów łącznie - 17 elementów before, element środkowy (id = X), 32 elementy after (zaokrąglenia mogą być dowolnie “spolaryzowane”).

Dodatkowo:
- wymagane są rozsądne walidacje danych - np. na email, na długość tematu, posta czy pseudonimu
- jeśli coś nie jest opisane z biznesowego punktu widzenia - dopuszczamy “rozsądną dowolność”

Ważna jest:
- jakość kodu
- odpowiednie zaprojektowanie endpointów REST-owych (albo REST-o-podobnych jeśli nie jesteś purystą) - ścieżki, metody (HTTP methods), odpowiednie kody HTTP, JSONy (także błędy zwracane w JSONie)
- rozsądne (silne) użycie typów
- mile widziane (ale zupełnie niewymagane) testy - integracyjne, w mniejszym stopniu jednostkowe

Preferowany stack technologiczny (ale odstępstwa są dopuszczalne - daj tylko wcześniej znać):
- Java
- Spring
- Hibernate
- PostgreSQL