CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

select p.name,c.name
from person p
join company c on p.company_id = c.id
where p.company_id <> 5;

SELECT c.name, COUNT(p.id) AS person_count
FROM person p
JOIN company c ON p.company_id = c.id
GROUP BY c.name
HAVING COUNT(p.id) = (
    SELECT MAX(cnt)
    FROM (
        SELECT company_id, COUNT(*) AS cnt
        FROM person
        GROUP BY company_id
    )
);
