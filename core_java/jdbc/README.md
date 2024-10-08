# Introduction
This is a basic Java app that allows the user to:
1. Buy a stock
2. Sell an owned stock
3. Display one stock (online or in the database) or all stocks in the database
4. Display one or all stocks that the user owns
The information on the stocks comes from the Alpha Vantage API. The retrieved information is then stored in a local PostgreSQL database that runs in Docker. Since we do not have write access to the API, all user transactions are stored in the local database. The user interface is inspired by VIM.
To use the app:
1. `sudo docker run -d -p 5432:5432 --name test-psql ruijie99/jrvs_stockquote_db`
2. `sudo docker exec -i test-psql psql -U postgres -d postgres < path/to/stock_quote.sql`
3. `sudo docker run --network=host -it -e API_KEY=your-api-key-here ruijie99/stockquote`
To get an API key, visit https://rapidapi.com/alphavantage/api/alpha-vantage

# Implementaiton
## ER Diagram
![ER Diagram](./ER%20diagram%20stock%20quote.png)

## Design Patterns
This project mainly uses the DAO pattern, since we do not need to do any joins on the server, and with only two tables, it makes sense to make only two DAOs. Also, in this case, the database runs locally on a single machine, so there is really no need to do a repository pattern, which is mostly useful in a case where the database is distributed across many physical locations.

# Test
The production database (`stock_quote`) and the test database (`stock_quote_test`) are separate, so that the tests do not affect production data. The unit tests and integration tests are done with Junit 5 and Mockito, so that no test will make an actual call to the API (a limit of 5 calls per minute will likely cause test failures due to the rate being reached). The tests focus mostly on branch coverage and sometimes path coverage. The integration tests will setup the test database and make moditications to it. The state of the database in integration tests are verified by calling SQL queries in the tests.