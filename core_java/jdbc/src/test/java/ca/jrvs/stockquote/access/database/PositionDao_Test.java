package ca.jrvs.stockquote.access.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ca.jrvs.stockquote.access.database.util.TestPositionUtil;

@TestInstance(Lifecycle.PER_CLASS)
public class PositionDao_Test {
    PositionDao positionDao;
    Connection connection;
    @BeforeAll
    public void initConnection() throws SQLException {
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote_test", pgUsername, pgPassword);
        this.positionDao = new PositionDao(connection);
        this.connection = connection;
        TestPositionUtil.resetDB(connection);
    }

    @BeforeEach
    public void init() throws SQLException, ParseException {
        TestPositionUtil.initDB(connection);
    }
    @AfterEach
    public void resetDB() throws SQLException {
        TestPositionUtil.resetDB(connection);
    }

    @Test
    public void testGetById() {
        Position position = this.positionDao.findById("DOGE").get();
        assertTrue(position.equals(TestPositionUtil.getDefaultPosition()));
    }
    @Test
    public void testGetByIdNotExists() {
        String idThatDoesNotExist = "HAND ON KEYBOARD ASLKJDBFASBL";
        assertTrue(!this.positionDao.findById(idThatDoesNotExist).isPresent());
    }

    @Test
    public void testCreateNew() {
        Position position = TestPositionUtil.getPosition(
            "SHIBAINU", 10000, 100
        );
        Position insertedPosition = this.positionDao.createNew(position);
        assertTrue(position.equals(insertedPosition));
    }
    @Test
    public void testCreateNewViolatingFK() {
        Position position = TestPositionUtil.getPosition(
            "FLSKDJ", 10000, 100
        );
        assertThrows(RuntimeException.class, ()-> {
            this.positionDao.createNew(position);
        });
    }
    @Test
    public void testSaveExisting() {
        Position position = TestPositionUtil.getDefaultPosition();
        position.setNumOfShares(0);
        position.setValuePaid(0);
        Position returned = this.positionDao.saveExisting(position);
        assertTrue(returned.equals(position));
    }

    @Test
    public void testSave_Existing() {
        Position position = TestPositionUtil.getPosition(
            "SHIBAINU",
            1000,
            10
        );
        Position returned = this.positionDao.save(position);
        assertTrue(returned.equals(position));
    }
    @Test
    public void testSave_Null() {
        Position position = null;
        assertThrows(IllegalArgumentException.class, () -> {
            this.positionDao.save(position);
        });
    }
    @Test
    public void testFindAll() {
        List<Position> positions = (ArrayList<Position>)this.positionDao.findAll();
        // ArrayList<String> stringList = positions.stream()
        //     .map(Position::toString)
        //     .collect(Collectors.toCollection(ArrayList::new));
        assertEquals(2, positions.size());
    }

    @Test
    public void testDeleteById() throws SQLException {
        this.positionDao.deleteById("DOGE");
        int i = 0;
        String SELECT = "SELECT * FROM position WHERE symbol='DOGE'";
        PreparedStatement ps = connection.prepareStatement(SELECT);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            i++;
        }
        assertEquals(0, i);
    }

    @Test
    public void testDeleteAll() throws SQLException {
        this.positionDao.deleteAll();
        int i = 0;
        String SELECT = "SELECT * FROM position";
        PreparedStatement ps = connection.prepareStatement(SELECT);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            i++;
        }
        assertEquals(0, i);
    }
}
