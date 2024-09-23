package ca.jrvs.stockquote;

import java.sql.Connection;
// import java.sql.Timestamp;
// import java.time.Instant;
import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        QuoteHttpHelper helper = new QuoteHttpHelper();
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote", pgUsername, pgPassword);
        PositionDao positionDao = new PositionDao(connection);
        positionDao.deleteAll();
        Position tsla = new Position();
        tsla.setNumOfShares(10);
        tsla.setTicker("TSLA");
        tsla.setValuePaid(10000.12);
        positionDao.save(tsla);

        Position ford = new Position();
        ford.setTicker("F");
        ford.setNumOfShares(20);
        ford.setValuePaid(2000);
        positionDao.save(ford);

        ArrayList<Position> positions = (ArrayList<Position>)positionDao.findAll();
        for(Position position:positions) {
            System.out.println(position);
        }
        positionDao.deleteById("TSLA");
        System.out.println("============================ DELETED TESLA ============================");
        positions = (ArrayList<Position>)positionDao.findAll();
        for(Position position:positions) {
            System.out.println(position);
        }
    }
}
