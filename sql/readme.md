
# Introduction
This project aims at understanding SQL, more specifically Postgresql and PG Admin 4, both running in separate Docker containers. psql client was used to run queries contained inside a .sql file.

The subject covered include:
1. Creating a database, including schemas and tables
2. Queries, such as:
   1. SELECT, UPDATE, DELETE
   2. CREATE TABLE
   3. ALTER TABLE
3. Database normalizations, such as 1NF, 2NF, and 3NF
4. Database modeling


# SQL Queries
###### Setup
The following bash script can be used to set up the database and PGAdmin
```bash
# Download the docker images
sudo docker pull postgres;
sudo docker pull dpage/pgadmin4;

# Initial login information for the pgadmin container
export PGADMIN_DEFAULT_EMAIL="YOUR_EMAIL";
export PGADMIN_DEFAULT_PASSWORD="YOUR_PASSWORD";

# Password to access the database (default user: postgres)
export PGPASSWORD="YOUR_DATABASE_PASSWORD";

# Start the containers
sudo docker run --name psql_db2 \
    -e POSTGRES_PASSWORD=$PGPASSWORD \
    -d -v pgdata:/var/lib/postgresql/data \
    -p 5432:5432 postgres;
sudo docker run --name pgadmin4 \
    -p 80:80 \
    -e PGADMIN_DEFAULT_EMAIL=$PGADMIN_DEFAULT_EMAIL \
    -e PGADMIN_DEFAULT_PASSWORD=$PGADMIN_DEFAULT_PASSWORD \
    -d dpage/pgadmin4;
# you can how access pgadmin at http://[::]:80 
```
To use the queries, log into PGAdmin with `PGADMIN_DEFAULT_EMAIL` and `PGADMIN_DEFAULT_PASSWORD`, and right click `servers --> Register --> Server...`. In the pop up, enter a name, and in `connections`, enter the IP address of the container `pgadmin4` (which can be obtained with `sudo docker inspect pgadmin4`) and the database password (`PGPASSWORD`).

###### Table Setup (DDL)
The tables and the schema can be created with the following commands:
```sql
CREATE SCHEMA IF NOT EXISTS cd;
CREATE TABLE IF NOT EXISTS cd.members(
  memid INTEGER PRIMARY KEY, 
  surname VARCHAR(200), 
  firstname VARCHAR(200), 
  address VARCHAR(300), 
  zipcode INTEGER, 
  telephone VARCHAR(20), 
  recommendedby INTEGER, 
  CONSTRAINT fk_members_recommanded_by FOREIGN KEY (recommendedby) REFERENCES cd.members(memid) ON DELETE 
  SET 
    NULL
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
```
We can also setup everything automatically with the following bash command:
```bash
psql -U postgres -p 5432 -h localhost -f path/to/sql/clubdata.sql -d postgres -x -q
```

###### Question 1: INSERT a new facility
```SQL
INSERT INTO cd.facilities 
VALUES 
  (9, 'Spa', 20, 30, 100000, 800)
```

###### Question 2: INSERT a new facility, but this time assign ID automatically
Solution 1: this one seems to be the easiest (it was suggested by ChatGPT), just use `MAX(facid) + 1`
```SQL
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
```
However, I wanted a O(1) solution rather than O(N), so I decided to turn it into a serial:
```SQL
-- Create a sequence that starts with max_id
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
-- set the default value to the next number in the sequence
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
```
###### Question 3: Update with where
```SQL
UPDATE 
  cd.facilities 
SET 
  initialoutlay = 10000 
WHERE 
  "name" = 'Tennis Court 2';
```

###### Question 4: Update table with result from another query (make tennis court 2 10% more expensive then tennis court 1)
```SQL
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
 WHERE
 	"name" = 'Tennis Court 2'
```
###### Question 5: Delete all rows from table
```SQL
DELETE FROM 
  cd.bookings 
WHERE 
  1 = 1
```
###### Question 6: Delete particular record
```SQL
DELETE FROM 
  cd.members 
WHERE 
  memid = 37
```

###### Question 7: Select all rows where membercost is 1/50 of the maintenance cost
```SQL
SELECT 
  "name", 
  membercost, 
  monthlymaintenance 
FROM 
  cd.facilities 
WHERE 
  membercost * 50 < monthlymaintenance;
```

###### Question 8: Select all tennis courts
```sql
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  "name" LIKE '%Tennis%'
```
###### Question 9: Select a facility with an ID of 1 or 5
```SQL
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  facid IN (1, 5)
```
###### Question 10: Select with date (select all registrations after september 1st 2012)
```SQL
SELECT 
  memid, 
  surname, 
  firstname, 
  joindate 
FROM 
  cd.members 
WHERE 
  joindate >= DATE('2012-09-01')
```
###### Question 11: Concatenate the results of 2 queries
```SQL
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
```

###### Question 12: Inner join - Start times by member David Farrell
```SQL
SELECT 
  starttime 
FROM 
  cd.bookings AS bookings 
  JOIN cd.members members ON members.memid = bookings.memid 
WHERE 
  members.surname = 'Farrell' 
  AND members.firstname = 'David'
```

###### Question 13: Bookings for all tennis courts, on September 21st 2012, sorted by start time
```SQL
SELECT 
  starttime, 
  facilities.name 
FROM
  cd.bookings AS bookings 
  JOIN (
    SELECT 
      "name", 
      facid 
    FROM 
      cd.facilities
  ) AS facilities ON facilities.facid = bookings.facid 
WHERE 
  facilities.name LIKE '%Tennis Court%' 
  AND DATE(bookings.starttime) = DATE('2012-09-21') 
ORDER BY 
  bookings.starttime
```

###### Question 14: Self join, list members with their recommenders
```SQL
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
```

###### Question 15: List all members who have recommended another member, without duplicates, sorted by last name then by first name
```SQL
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
```
###### Question 16: List all members with their referers without using JOIN
```SQL
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
```

###### Question 17: The number of recommendations each member has made, sorted by member ID
```SQL
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
```

###### Question 18: List of total number of slots booked per facility, sorted by ID
``` SQL
SELECT 
  facid, 
  SUM(slots) AS "Total Slots" 
FROM 
  cd.bookings 
GROUP BY 
  facid 
ORDER BY 
  facid
```

###### Question 19: Total number of slots per facility for September 2012
```SQL
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
```
###### Question 20: Total number of slots booked per month for the year 2012, for each facility, sorted by facility ID then by month
```SQL
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
```
###### Question 21: Total members who have made at least one booking
```SQL
SELECT 
  COUNT(bookings.memid) 
FROM 
  (
    SELECT 
      DISTINCT memid 
    FROM 
      cd.bookings
  ) AS bookings
```
###### Question 22: List of members and their first booking for September 2012, sorted by member ID
```SQL
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
```
###### Question 23: First name and last name of each member, plus a row with the total number of members

```SQL
-- Solution with cross join
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
```
```SQL
-- Solution with window function
SELECT 
  COUNT(firstname) OVER() as "count", 
  firstname, 
  surname 
FROM 
  cd.members 
ORDER BY 
  joindate
```
###### Question 24: A list of members, sorted by their join date, with a row representing the row number
```SQL
SELECT 
  RANK() OVER(
    ORDER BY 
      joindate
  ), 
  firstname, 
  surname 
FROM 
  cd.members
```
###### Question 25: Display the facility(ies) with the highest number(s) of bookings
```SQL
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
```
###### Question 26: Concatenating strings (first name + , + last name)
```SQL
SELECT surname || ', ' || firstname FROM cd.members
```
###### Question 27: List of all members whose phone number is formatted like (111) 1111-1111
```SQL
SELECT 
  memid, 
  telephone 
FROM 
  cd.members 
WHERE 
  telephone SIMILAR TO '\([0-9]{3}\) [0-9]{3}\-[0-9]{4}' 
ORDER BY 
  memid
```
###### Question 28: For each letter of the alphabet, display the number of members whose name starts with it, except if the number is 0
```SQL
SELECT 
  SUBSTRING(surname, 1, 1) AS letter, 
  COUNT(*) 
FROM 
  cd.members 
GROUP BY 
  letter 
ORDER BY 
  letter
```