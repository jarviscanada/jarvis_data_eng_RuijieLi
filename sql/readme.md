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
```
