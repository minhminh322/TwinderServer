import java.sql.*;
import java.util.ArrayList;

import org.apache.commons.dbcp2.*;

public class TwinderDao {
    private static BasicDataSource dataSource;

    public TwinderDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createMessage(TwinderBody tb) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO TwinderRecords (swiperId, swipeeId, swipeDirection) " +
                "VALUES (?,?,?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, tb.getSwiper());
            preparedStatement.setInt(2, tb.getSwipee());
            preparedStatement.setString(3, tb.getSwipe());
//            preparedStatement.setString(4, tb.getComment());

            // execute insert SQL statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public ArrayList<Integer> getMatches(int userID) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "SELECT SwipeeID FROM TwinderRecords WHERE SwiperID=? LIMIT 100 ";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, userID);

            // execute insert SQL statement
            ResultSet res = preparedStatement.executeQuery();

            ArrayList<Integer> matchesList = new ArrayList<>();
            while (res.next()) {
                matchesList.add(res.getInt("SwipeeID"));
            }
            return matchesList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return null;
    }

    public int getStats(int userID) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "SELECT COUNT(*) FROM TwinderRecords WHERE SwipeeID=? ";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, userID);


            // execute insert SQL statement
            ResultSet res = preparedStatement.executeQuery();
            int statsRes = 0;

            if (res.next()) {
                return res.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return 0;
    }
}