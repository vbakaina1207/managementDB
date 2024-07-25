import java.sql.*;
import java.time.LocalDate;

public class Management {

    public  void testName(String name) throws InvalidPersonName {
        char[] nameChar = name.toLowerCase().toCharArray();
        for (char c : nameChar) {
            if ((int) c < 61 || (int) c > 122) {
                throw new InvalidPersonName("The name " + name + " mast be contain only characters");
            }
        }
    }

    public int findPerson (String firstName, String lastName) throws SQLException, InvalidPersonName {
        String findByNameQueryString = "SELECT id from person where first_name = ? and last_name = ?;";
        int id = -1;
        ResultSet resultSet = null;
        try {
            PreparedStatement findByName = DatabaseConnection.getInstance().getConnection().prepareStatement(findByNameQueryString);
            // set the parameter
            findByName.setString(1, firstName);
            findByName.setString(2, lastName);
            resultSet = findByName.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("id");
                System.out.println(firstName + " " + lastName + " has the ID: " + resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return  id;
    }
    public  void createPerson(String firstName, String lastName, LocalDate DOB, int idSex, int idCity, int idStreet, String numHouse, int numFlat) throws InvalidPersonName {
        String findByNameQueryString = "INSERT INTO person (first_name, last_name, birthday, id_sex, id_city, id_street, num_house, num_flat) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            testName(firstName);
            testName(lastName);

            if (findPerson(firstName, lastName) < 1) {
                PreparedStatement insertPersonStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(findByNameQueryString, Statement.RETURN_GENERATED_KEYS);
                insertPersonStatement.setString(1, firstName);
                insertPersonStatement.setString(2, lastName);
                insertPersonStatement.setDate(3, java.sql.Date.valueOf(DOB));
                insertPersonStatement.setInt(4, idSex);
                insertPersonStatement.setInt(5, idCity);
                insertPersonStatement.setInt(6, idStreet);
                insertPersonStatement.setString(7, numHouse);
                insertPersonStatement.setInt(8, numFlat);

                int rowsAffected = insertPersonStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Person " + firstName + " " + lastName + " created successfully.");
                    ResultSet generatedKeys = insertPersonStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Generated ID: " + generatedId);
                    }  else {
                        System.out.println("Failed to create person.");
                    }
                }

            } else {
                System.out.println("The person already exists");
            }
        }   catch (InvalidPersonName e){
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePerson (String firstName, String lastName) throws InvalidPersonName, NullpointerException {
        String findByNameQueryString = "DELETE from person where id = ?;";
        try {
            testName(firstName);
            testName(lastName);
            try {
                int idRemove = findPerson(firstName, lastName);
                PreparedStatement deletePersonStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(findByNameQueryString);
                if ( idRemove > 0) {
                    deletePersonStatement.setInt(1, idRemove);
                    int rowsAffected = deletePersonStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Person " + firstName + " " + lastName + " deleted successfully.");
                    }
                }
                else
                    throw new NullpointerException("Person not found");
            }
            catch (NullpointerException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        catch (InvalidPersonName e) {
            System.out.println(e.getMessage());
        }
    }


    public void printAllPersons() {
        try {
            String query = "SELECT * FROM person;";
            // execute query
            PreparedStatement selectPersonStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
            ResultSet resultSet = selectPersonStatement.executeQuery(query);

            // print result
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("first_name") + " " + resultSet.getString("last_name");

                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
