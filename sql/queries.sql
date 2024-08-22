CREATE SCHEMA IF NOT EXISTS cd;
CREATE TABLE IF NOT EXISTS cd.members(
	memid INTEGER PRIMARY KEY,
	surname VARCHAR(200),
	firstname VARCHAR(200),
	address VARCHAR(300),
	zipcode INTEGER,
	telephone VARCHAR(20),
	recommendedby INTEGER,
	CONSTRAINT fk_members_recommanded_by FOREIGN KEY (recommendedby)
		REFERENCES cd.members(memid) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS cd.facilities(
	facid INTEGER PRIMARY KEY,
	"name" VARCHAR(100),
	membercost NUMERIC,
	guestcost NUMERIC,
	initialoutlay NUMERIC,
	monthlymaintenance NUMERIC
);

CREATE TABLE IF NOT EXISTS cd.bookings(
	bookid INTEGER PRIMARY KEY,
	facid INTEGER REFERENCES cd.facilities(facid),
	memid INTEGER REFERENCES cd.members(memid),
	starttime TIMESTAMP,
	slots INTEGER
);

-- q1
INSERT INTO cd.facilities 
VALUES 
  (9, 'Spa', 20, 30, 100000, 800)

-- q2
-- attempt 1
WITH MAX_ID AS (
  SELECT 
    MAX(facid) + 1 AS next_id 
  FROM 
    cd.facilities
) INSERT INTO cd.facilities (
  facid, "name", membercost, guestcost, 
  initialoutlay, monthlymaintenance
) 
SELECT 
  next_id, 
  'Spa', 
  20, 
  30, 
  100000, 
  800 
FROM 
  MAX_ID;

-- attempt 2 from chatgpt
INSERT INTO cd.facilities (
  facid, "name", membercost, guestcost, 
  initialoutlay, monthlymaintenance
) 
SELECT 
  MAX(facid) + 1, 
  'Spa', 
  20, 
  30, 
  100000, 
  800 
FROM 
  cd.facilities

-- or seriously just change it to serial...
DO $$DECLARE max_id INTEGER;
BEGIN 
SELECT 
  max(facid) + 1 
FROM 
  cd.facilities INTO max_id;
EXECUTE format(
  'CREATE SEQUENCE cd.facid_seq START %s', 
  max_id
);
END$$;
ALTER TABLE 
  cd.facilities ALTER COLUMN facid 
SET 
  DEFAULT nextval('cd.facid_seq');
INSERT INTO cd.facilities (
  "name", membercost, guestcost, initialoutlay, 
  monthlymaintenance
) 
VALUES 
  ('Spa', 20, 30, 100000, 800);

--REMOVE THE WHOLE THING
-- DELETE FROM 
--   cd.facilities 
-- WHERE 
--   "name" = 'Spa'

--Q3
UPDATE 
  cd.facilities 
SET 
  initialoutlay = 10000 
WHERE 
  "name" = 'Tennis Court 2';

--Q4
UPDATE 
  cd.facilities 
SET 
  membercost = target.membercost * 1.1, 
  guestcost = target.guestcost * 1.1 
FROM 
  (
    SELECT 
      membercost, 
      guestcost 
    FROM 
      cd.facilities 
    WHERE 
      "name" = 'Tennis Court 1'
  ) AS target

--Q5
-- DROP TABLE cd.bookings
DELETE FROM 
  cd.bookings 
WHERE 
  1 = 1

--Q6
DELETE FROM 
  cd.members 
WHERE 
  memid = 37

-- NOTE
-- DELETE FROM cd.members
-- USING (
--     SELECT 4 AS id
-- ) AS test
-- WHERE cd.members.memid = test.id;

--Q7
SELECT 
  "name", 
  membercost, 
  monthlymaintenance 
FROM 
  cd.facilities 
WHERE 
  membercost * 50 < monthlymaintenance;

--Q8
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  "name" LIKE '%Tennis%'

--q9
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  facid IN (1, 5)

--q10
SELECT 
  memid, 
  surname, 
  firstname, 
  joindate 
FROM 
  cd.members 
WHERE 
  joindate >= DATE('2012-09-01')

--Q11
(
  SELECT 
    surname 
  FROM 
    cd.members
) 
UNION 
  (
    SELECT 
      "name" 
    FROM 
      cd.facilities
  )

--Q12
SELECT 
  starttime 
FROM 
  cd.bookings AS bookings 
  JOIN cd.members members ON members.memid = bookings.memid 
WHERE 
  members.surname = 'Farrell' 
  AND members.firstname = 'David'

--Q13
SELECT 
  starttime, 
  facilities.name 
from 
  cd.bookings as bookings 
  JOIN (
    SELECT 
      "name", 
      facid 
    from 
      cd.facilities
  ) as facilities ON facilities.facid = bookings.facid 
WHERE 
  facilities.name LIKE '%Tennis Court%' 
  AND DATE(bookings.starttime) = DATE('2012-09-21') 
ORDER BY 
  bookings.starttime

--Q14
SELECT 
  members.firstname as memfname, 
  members.surname as memsname, 
  referers.firstname as recfname, 
  referers.surname as recsname 
FROM 
  cd.members AS members 
  LEFT JOIN cd.members referers ON members.recommendedby = referers.memid 
ORDER BY 
  (
    members.surname, members.firstname
  )

--Q15
SELECT 
  DISTINCT members.firstname, 
  members.surname 
FROM 
  cd.members referered 
  JOIN cd.members members ON referered.recommendedby = members.memid 
WHERE 
  referered.recommendedby IS NOT NULL 
ORDER BY 
  members.surname, 
  members.firstname

--Q16
SELECT 
  DISTINCT firstname || ' ' || surname AS "member", 
  (
    SELECT 
      referers.firstname || ' ' || referers.surname AS referer 
    FROM 
      cd.members referers 
    WHERE 
      referers.memid = members.recommendedby
  ) 
FROM 
  cd.members AS members 
ORDER BY 
  "member"

--Q17
SELECT 
  recommendedby, 
  COUNT(memid) 
FROM 
  cd.members 
WHERE 
  recommendedby IS NOT NULL 
GROUP BY 
  recommendedby 
ORDER BY 
  recommendedby

--Q18
SELECT 
  facid, 
  SUM(slots) AS "Total Slots" 
FROM 
  cd.bookings 
GROUP BY 
  facid 
ORDER BY 
  facid

--Q19
SELECT 
  facid, 
  SUM(slots) AS "Total Slots" 
FROM 
  cd.bookings 
WHERE 
  date_part('month', starttime) = '09' 
GROUP BY 
  facid 
ORDER BY 
  "Total Slots"

--Q20
SELECT 
  facid, 
  date_part('month', starttime) AS "month", 
  SUM(slots) AS "Total Slots" 
FROM 
  cd.bookings 
WHERE 
  date_part('year', starttime) = '2012' 
GROUP BY 
  facid, 
  "month" 
ORDER BY 
  facid, 
  "month"

--Q21
SELECT 
  COUNT(bookings.memid) 
FROM 
  (
    SELECT 
      DISTINCT memid 
    FROM 
      cd.bookings
  ) AS bookings

--Q22
SELECT 
  members.surname, 
  members.firstname, 
  members.memid, 
  MIN(bookings.starttime) 
FROM 
  cd.members members 
  JOIN (
    SELECT 
      * 
    FROM 
      cd.bookings 
    WHERE 
      date_part('year', starttime)= '2012' 
      AND date_part('month', starttime)= '09'
  ) AS bookings ON bookings.memid = members.memid 
GROUP BY 
  (
    members.surname, members.firstname, 
    members.memid
  ) 
ORDER BY 
  members.memid

--Q23
SELECT 
  firstname, 
  surname, 
  total.cnt 
FROM 
  cd.members CROSS 
  JOIN (
    SELECT 
      COUNT(*) as cnt 
    FROM 
      cd.members
  ) AS total

-- OR
SELECT 
  COUNT(firstname) OVER() as "count", 
  firstname, 
  surname 
FROM 
  cd.members 
ORDER BY 
  joindate

--Q24
SELECT 
  RANK() OVER(
    ORDER BY 
      joindate
  ), 
  firstname, 
  surname 
FROM 
  cd.members

--Q25
WITH sum_slots AS (
  SELECT 
    SUM(slots) as sum_, 
    facid 
  FROM 
    cd.bookings 
  GROUP BY 
    facid
), 
max_slot AS (
  SELECT 
    MAX(sum_) AS max_ 
  FROM 
    sum_slots
) 
SELECT 
  sum_slots.facid, 
  sum_slots.sum_ AS total 
FROM 
  sum_slots 
  INNER JOIN max_slot ON sum_slots.sum_ = max_slot.max_


