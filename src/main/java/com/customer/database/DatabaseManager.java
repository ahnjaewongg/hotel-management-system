package com.customer.database;

import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseManager {
  private static DatabaseManager instance;
  private static final String DB_URL = "jdbc:mysql://localhost:3306/crm";
  private static final String DB_USER = "root";
  private static final String DB_PASSWORD = "1234";
  private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

  private DatabaseManager() {
    // 데이터베이스 드라이버 로드
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static DatabaseManager getInstance() {
    if (instance == null) {
      instance = new DatabaseManager();
    }
    return instance;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
  }

  // 객실 관련 메서드들
  public JSONArray getAllRooms() throws SQLException {
    JSONArray rooms = new JSONArray();
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM room")) {

      while (rs.next()) {
        JSONObject room = new JSONObject();
        room.put("roomNumber", rs.getString("room_number"));
        room.put("roomType", rs.getString("room_type"));
        room.put("status", rs.getString("room_status"));
        rooms.put(room);
      }
    }
    return rooms;
  }

  public JSONObject getRoom(String roomNumber) throws SQLException {
    JSONObject room = new JSONObject();
    String sql = "SELECT * FROM room WHERE room_number = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, roomNumber);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        room.put("roomNumber", rs.getString("room_number"));
        room.put("roomType", rs.getString("room_type"));
        room.put("status", rs.getString("room_status"));
      }
    }
    return room;
  }

  public boolean addRoom(String roomNumber, String roomType, String status) throws SQLException {
    String sql = "INSERT INTO room (room_number, room_type, room_status) VALUES (?, ?, ?)";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      System.out.println("Adding room: " + roomNumber + ", " + roomType + ", " + status); // 디버깅 로그

      pstmt.setString(1, roomNumber);
      pstmt.setString(2, roomType);
      pstmt.setString(3, status);

      int result = pstmt.executeUpdate();
      System.out.println("Insert result: " + result); // 디버깅 로그

      return result > 0;
    } catch (SQLException e) {
      System.err.println("Error in addRoom: " + e.getMessage()); // 디버깅 로그
      e.printStackTrace();
      throw e;
    }
  }

  public boolean updateRoom(String roomNumber, String roomType, String status, String cleaningStatus)
      throws SQLException {
    String sql = "UPDATE room SET room_type = ?, status = ?, cleaning_status = ? WHERE room_number = ?";

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      System.out.println("Updating room with parameters:");
      System.out.println("Room Number: " + roomNumber);
      System.out.println("Room Type: " + roomType);
      System.out.println("Status: " + status);
      System.out.println("Cleaning Status: " + cleaningStatus);

      stmt.setString(1, roomType);
      stmt.setString(2, status);
      stmt.setString(3, cleaningStatus);
      stmt.setString(4, roomNumber);

      int rowsAffected = stmt.executeUpdate();
      System.out.println("Rows affected: " + rowsAffected);
      return rowsAffected > 0;
    } catch (SQLException e) {
      System.err.println("SQL Error: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public boolean deleteRoom(String roomNumber) throws SQLException {
    String sql = "DELETE FROM room WHERE room_number = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, roomNumber);
      int result = pstmt.executeUpdate();
      return result > 0;
    }
  }

  public boolean roomExists(String roomNumber) throws SQLException {
    String sql = "SELECT COUNT(*) FROM room WHERE room_number = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, roomNumber);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
      return false;
    }
  }

  public boolean checkUsernameExists(String username) throws SQLException {
    String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
    LOGGER.info("Executing SQL: " + sql + " with username: " + username);

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int count = rs.getInt(1);
          LOGGER.info("Found " + count + " users with username: " + username);
          return count > 0;
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error checking username existence", e);
      throw e;
    }
    return false;
  }

  public boolean checkEmailExists(String email) throws SQLException {
    String sql = "SELECT COUNT(*) FROM user WHERE email = ?";

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, email);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int count = rs.getInt(1);
          System.out.println("Database check - Email '" + email + "' count: " + count); // 디버깅 로그
          return count > 0;
        }
        return false;
      }
    }
  }

  public JSONArray getCustomers() throws SQLException {
    JSONArray customers = new JSONArray();
    String sql = "SELECT id, username, name, email, admin_yn FROM user WHERE admin_yn = 0";

    LOGGER.info("Executing SQL: " + sql);

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      LOGGER.info("Executing customer query");
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        JSONObject customer = new JSONObject();
        customer.put("id", rs.getInt("id"));
        customer.put("username", rs.getString("username"));
        customer.put("name", rs.getString("name"));
        customer.put("email", rs.getString("email"));
        customer.put("admin_yn", rs.getBoolean("admin_yn"));
        customers.put(customer);

        LOGGER.info("Added customer: " + customer.toString());
      }

      LOGGER.info("Retrieved " + customers.length() + " customers from database");
      return customers;
    } catch (SQLException e) {
      LOGGER.severe("Error retrieving customers: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public boolean updateCleaningStatus(String roomNumber, String cleaningStatus) throws SQLException {
    String sql = "UPDATE room SET cleaning_status = ? WHERE room_number = ?";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, cleaningStatus);
      stmt.setString(2, roomNumber);
      return stmt.executeUpdate() > 0;
    }
  }

  public JSONArray searchCustomers(String searchType, String searchTerm) throws SQLException {
    JSONArray results = new JSONArray();
    String sql;

    // 검색 조건에 따른 SQL 쿼리 생성
    if ("all".equals(searchType)) {
      sql = "SELECT id, username, name, email, admin_yn FROM user WHERE admin_yn = 0 AND " +
          "(username LIKE ? OR name LIKE ? OR email LIKE ?)";
    } else {
      sql = "SELECT id, username, name, email, admin_yn FROM user WHERE admin_yn = 0 AND " +
          switch (searchType) {
            case "username" -> "username LIKE ?";
            case "name" -> "name LIKE ?";
            case "email" -> "email LIKE ?";
            default -> "name LIKE ?"; // 기본값은 이름으로 검색
          };
    }

    LOGGER.info("Search SQL: " + sql + ", Type: " + searchType + ", Term: " + searchTerm);

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      String searchPattern = "%" + searchTerm + "%";
      if ("all".equals(searchType)) {
        // 전체 검색일 경우 세 개의 파라미터 설정
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        stmt.setString(3, searchPattern);
      } else {
        // 특정 필드 검색일 경우 하나의 파라미터만 설정
        stmt.setString(1, searchPattern);
      }

      LOGGER.info("Executing search query with pattern: " + searchPattern);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        JSONObject customer = new JSONObject();
        customer.put("id", rs.getInt("id"));
        customer.put("username", rs.getString("username"));
        customer.put("name", rs.getString("name"));
        customer.put("email", rs.getString("email"));
        customer.put("admin_yn", rs.getBoolean("admin_yn"));
        results.put(customer);
      }

      LOGGER.info("Search found " + results.length() + " results");
      return results;
    }
  }

  public boolean updateCustomer(JSONObject customerData) throws SQLException {
    String sql = "UPDATE user SET name = ?, email = ? WHERE id = ? AND admin_yn = 0";
    LOGGER.info("Updating customer with data: " + customerData.toString());

    try (Connection conn = getConnection()) {
      // MySQL 연결에 대한 인코딩 설정
      try (Statement stmt = conn.createStatement()) {
        stmt.execute("SET NAMES utf8mb4");
        stmt.execute("SET CHARACTER SET utf8mb4");
        stmt.execute("SET character_set_connection=utf8mb4");
      }

      try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, customerData.getString("name"));
        pstmt.setString(2, customerData.getString("email"));
        pstmt.setInt(3, customerData.getInt("id"));

        int result = pstmt.executeUpdate();
        LOGGER.info("Update result: " + result);
        return result > 0;
      }
    } catch (SQLException e) {
      LOGGER.severe("Error updating customer: " + e.getMessage());
      throw e;
    }
  }

  public boolean deleteCustomer(int userId) throws SQLException {
    String sql = "DELETE FROM user WHERE id = ? AND admin_yn = 0";

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, userId);
      return stmt.executeUpdate() > 0;
    }
  }

  public boolean addCustomer(JSONObject customerData) throws SQLException {
    String sql = "INSERT INTO user (username, password, name, email, admin_yn) VALUES (?, ?, ?, ?, 0)";
    LOGGER.info("Starting addCustomer with data: " + customerData.toString());

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, customerData.getString("username"));
      stmt.setString(2, customerData.getString("password"));
      stmt.setString(3, customerData.getString("name"));
      stmt.setString(4, customerData.getString("email"));

      LOGGER.info("Executing SQL: " + sql);
      int result = stmt.executeUpdate();
      LOGGER.info("SQL execution result: " + result);

      return result > 0;

    } catch (SQLException e) {
      LOGGER.severe("SQL Error in addCustomer: " + e.getMessage());
      throw e;
    }
  }

  public boolean isEmailAvailable(String email) throws SQLException {
    String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
    LOGGER.info("Checking email availability: " + email);

    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, email);
      LOGGER.info("Executing SQL: " + sql + " with email: " + email);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int count = rs.getInt(1);
          LOGGER.info("Email check result - count: " + count + " for email: " + email);
          return count == 0; // 0이면 사용 가능
        }
        LOGGER.warning("No result returned from database for email: " + email);
        return true; // 결과가 없으면 사용 가능
      }
    } catch (SQLException e) {
      LOGGER.severe("Database error in isEmailAvailable: " + e.getMessage());
      throw e;
    }
  }
}