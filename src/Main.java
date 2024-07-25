import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Connection connection = databaseConnection.getConnection();
        Management managementInstance = new Management();
        try {
            Statement statement = connection.createStatement();
            int idPerson = managementInstance.findPerson("Boris", "Storm");

            managementInstance.createPerson("Philip", "Hammer", LocalDate.parse("2002-01-01"), 1, 2, 1, "5", 7);
            managementInstance.removePerson("Philip", "Hammer");
            managementInstance.printAllPersons();


        } catch (SQLException  ex) {
            throw new RuntimeException(ex);
        } catch (InvalidPersonName e) {
            throw new RuntimeException(e);
        } catch (NullpointerException e) {
            throw new RuntimeException(e);
        }

    }




}
