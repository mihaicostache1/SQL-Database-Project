CREATE TABLE client (
    id_client INT PRIMARY KEY,
    nume VARCHAR(100) NOT NULL,
    telefon VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE curier (
    id_curier INT PRIMARY KEY,
    nume VARCHAR(100) NOT NULL,
    telefon VARCHAR(20),
    tura VARCHAR(20),
    activ BOOLEAN DEFAULT TRUE
);

CREATE TABLE produs (
    id_prod INT PRIMARY KEY,
    denumire VARCHAR(150) NOT NULL,
    categorie VARCHAR(50),
    pret DECIMAL(10, 2) NOT NULL,
    disponibil BOOLEAN DEFAULT TRUE
);

CREATE TABLE comanda (
    id_com INT PRIMARY KEY,
    id_client INT NOT NULL,
    id_curier INT,
    data_comanda DATE NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(30),
    metoda_plata VARCHAR(30),
    adresa_livrare VARCHAR(200),
    FOREIGN KEY (id_client) REFERENCES client(id_client),
    FOREIGN KEY (id_curier) REFERENCES curier(id_curier)
);

CREATE TABLE comanda_item (
    id_com INT,
    id_prod INT,
    cantitate INT NOT NULL,
    pret_unitar DECIMAL(10, 2) NOT NULL,
    reducere_pct DECIMAL(5, 2) DEFAULT 0,
    PRIMARY KEY (id_com, id_prod),
    FOREIGN KEY (id_com) REFERENCES comanda(id_com),
    FOREIGN KEY (id_prod) REFERENCES Produs(id_prod)
);



alter table client
add column adresa varchar(200);


alter table client 
add constraint check_phone_format check ( telefon like '07%' and length(telefon) = 10);


alter table produs 
add constraint pret_pozitiv check (pret > 0);


alter table comanda 
add constraint validare_metoda_plata check ( metoda_plata is null or metoda_plata in ('cash', 'card'));


alter table comanda_item
add constraint pret_cantitate_pozitiv check (pret_unitar > 0 and cantitate > 0);

alter table comanda 
add constraint status_valid check(status is null or status in ('inregistrata', 'in preparare', 'in curs de livrare', 'livrata', 'anulata'));


alter table curier 
add constraint curier_activ check (activ in (true, false));



--comenzile achitate numerar

select *
from comanda c 
where c.metoda_plata = 'cash'
order by total desc;


select *
from comanda c 
where c.status = 'in curs de livrare'
order by total;


select c.id_com , c.id_client , c.id_curier , c.data_comanda , c.total
from comanda c left join curier c2 on c.id_curier = c2.id_curier 
where c.status = 'livrata';

select p.id_prod , p2.id_prod , p.denumire
from produs p join produs p2 on (p.denumire = p2.denumire)
order by p.id_prod ;

select distinct c.id_client 
from client c
where exists (
	select * 
	from comanda c2 
	where c.id_client = c2.id_client
);

select p.denumire, p.id_prod 
from produs p
where not exists (
	select * 
	from comanda_item ci 
	where ci.id_prod = p.id_prod 
);

select c.nume, sum(c2.total) as total_comenzi_2025
from client c join comanda c2 on c.id_client = c2.id_client 
group by c.nume;

select c2.nume, sum(c.total) as suma_curier
from comanda c join curier c2 on c.id_curier = c2.id_curier
group by c2.nume
order by c.total desc;


INSERT INTO client (id_client, nume, telefon, email, adresa) VALUES
(101, 'Popescu Ana', '0722123456', 'ana.popescu@email.com', 'Strada Zambilei nr. 5'),
(102, 'Ionescu Mihai', '0745987654', 'mihai.i@email.com', 'Bulevardul Eroilor 22'),
(103, 'Gheorghe Elena', '0760112233', 'elena.g@email.com', 'Aleea Florilor 1A'),
(104, 'Vasilescu Radu', '0788554433', 'radu.v@email.com', 'Strada Lalelelor 45');

INSERT INTO curier (id_curier, nume, telefon, tura, activ) VALUES
(201, 'Petrescu Ion', '0730001122', 'zi', TRUE),
(202, 'Stancu Maria', '0730003344', 'seara', TRUE),
(203, 'Dinescu Andrei', '0730005566', 'zi', FALSE); 

INSERT INTO produs (id_prod, denumire, categorie, pret, disponibil) VALUES
(301, 'Pizza Margherita', 'Pizza', 35.50, TRUE),
(302, 'Burger Clasic', 'Fast Food', 42.00, TRUE),
(303, 'Salata Caesar', 'Salate', 28.99, TRUE),
(304, 'Apa Minerala', 'Bauturi', 7.50, TRUE),
(305, 'Tiramisu', 'Desert', 19.90, TRUE),
(306, 'Paine Integrală', 'Panificatie', 12.00, TRUE); 

INSERT INTO comanda (id_com, id_client, id_curier, data_comanda, total, status, metoda_plata, adresa_livrare) VALUES
(401, 101, 201, '2025-11-28', 78.00, 'livrata', 'card', 'Strada Zambilei nr. 5'),
(402, 102, 202, '2025-11-29', 125.50, 'in curs de livrare', 'cash', 'Bulevardul Eroilor 22'), 
(403, 101, 201, '2025-11-30', 45.00, 'livrata', 'cash', 'Strada Zambilei nr. 5'),
(404, 103, NULL, '2025-12-01', 35.50, 'inregistrata', NULL, 'Aleea Florilor 1A'),
(405, 102, 202, '2025-12-02', 200.00, 'in curs de livrare', 'cash', 'Bulevardul Eroilor 22'); 

INSERT INTO comanda_item (id_com, id_prod, cantitate, pret_unitar, reducere_pct) VALUES
(401, 301, 1, 35.50, 0),
(401, 304, 2, 7.50, 0),
(402, 302, 2, 42.00, 5.00),
(402, 305, 1, 19.90, 0),
(403, 301, 1, 35.50, 0),
(403, 303, 1, 28.99, 10.00),
(405, 301, 3, 35.50, 0);


--7
insert into produs(id_prod, denumire, categorie, pret, disponibil) values (210, 'Supa de legume', 'Supe/Ciorbe', 15.5, true);

delete from produs 
where disponibil = false 
and id_prod not in (
select distinct id_prod from comanda_item);

select * from produs;
 

update produs 
set pret = pret * 0.9
where categorie = 'Desert';


--8
create function update_total()
returns trigger as $$
begin
	update comanda
	set total = (
        select SUM(ci.cantitate * ci.pret_unitar)
        from comanda_item ci	
        where ci.id_com = new.id_com
    )
    where id_com = new.id_com;
   return new;
end;
$$ language plpgsql;

create trigger update_total_comanda
after insert or update on comanda_item 
for each row 
execute function update_total();


create function update_after_delete()
returns trigger as $$
begin
	update comanda 
	set total = (
	select sum(ci.cantitate* ci.pret_unitar)
	from comanda_item ci
	where ci.id_com = old.id_com
	)
	where id_com = old.id_com;
return new;
end;
$$ language plpgsql;

create trigger update_total_comanda_stergere
after delete on comanda_item
for each row 
execute function update_after_delete();

create function aplica_reducere_functie()
returns trigger as $$
begin
	if(select p.pret from produs where p.id_prod = new.id_prod) > 100 then 
		new.pret_unitar := (
			select pret*0.9
			from produs
			where id_prod = new.id_prod
		);
	else
		new.pret_unitar := (
			select p.pret 
			from produs p
			where p.id_prod = new.id_prod
		);
	end if;
return new;
end;
$$ language plpgsql;

create trigger aplica_reducere
before insert on comanda_item 
for each row
execute function aplica_reducere_functie();


CREATE VIEW ComandaExtinsa202 AS
SELECT
c.id_com, c.data_comanda, c.status, c.metoda_plata, c.total,
cl.id_client, cl.nume AS nume_client, cl.telefon AS telefon_client,
cu.id_curier, cu.nume AS nume_curier,
p.id_prod, p.denumire AS denumire_produs, p.pret AS pret_lista, p.disponibil,
ci.cantitate, ci.pret_unitar, ci.reducere_pct
FROM comanda c
JOIN client cl ON cl.id_client = c.id_client
LEFT JOIN curier cu ON cu.id_curier = c.id_curier
JOIN comanda_item ci ON ci.id_com = c.id_com
JOIN produs p ON p.id_prod = ci.id_prod;

create function insert_comanda_extinsa202()
returns trigger as $$
begin
	if not exists(
		select *
		from comanda
		where id_com = new.id_com
	)then raise exception 'Comanda nu exista';
	end if;
	if exists(
		select *
		from produs
		where id_prod = new.id_prod and disponibil = false
	)then
		raise exception 'Produsul % nu este disponibil', new.id_prod;
	end if;
	
	insert into comanda_item(id_com, id_prod, cantitate, pret_unitar) values (new.id_com, new.id_prod, new.cantitate, new.pret_unitar);

	return null;
end;
$$ language plpgsql;

create trigger instead_of_insert
instead of insert on ComandaExtinsa202
for each row
execute function insert_comanda_extinsa202();






