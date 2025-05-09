package study.studydemo.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author dongfeng
 * @date 2022/12/23 21:34
 */
public class JdbcTest {
    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection("url", "user", "password");

            Statement statement = connection.createStatement();
            statement.executeUpdate("");
            ResultSet set = statement.executeQuery("");
            while (set.next()) {
                set.getString(1);
            }

            connection.prepareStatement("");


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
