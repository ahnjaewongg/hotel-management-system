package com.customer.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import org.json.JSONObject;
import org.json.JSONArray;
import com.customer.database.DatabaseManager;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = { "/dashboard", "/api/room", "/static/*" })
public class DashboardServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    // 세션 체크 추가
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("username") == null) {
      resp.sendRedirect("/login");
      return;
    }

    String pathInfo = req.getServletPath();

    // static 리소스 요청 처리
    if (pathInfo.startsWith("/static/")) {
      handleStaticResource(req, resp);
      return;
    }

    // URL 패턴에 따라 다른 처리
    if ("/dashboard".equals(pathInfo)) {
      handlePageRequest(resp);
    } else if ("/api/room".equals(pathInfo)) {
      handleApiRequest(resp);
    }
  }

  // API 요청 처리
  private void handleApiRequest(HttpServletResponse resp) throws IOException {
    DatabaseManager db = DatabaseManager.getInstance();

    try {
      JSONArray rooms = db.getAllRooms();
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write(rooms.toString());
    } catch (SQLException e) {
      handleError(resp, e);
    }
  }

  // HTML 페이지 요청 처리
  private void handlePageRequest(HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html; charset=UTF-8");

    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("html/dashboard.html")) {
      if (inputStream != null) {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
          String line;
          while ((line = reader.readLine()) != null) {
            resp.getWriter().println(line);
          }
        }
      } else {
        resp.getWriter().println("Error: dashboard.html file not found");
      }
    }
  }

  // 에러 처리
  private void handleError(HttpServletResponse resp, SQLException e) throws IOException {
    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    JSONObject error = new JSONObject();
    error.put("error", e.getMessage());
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().write(error.toString());
  }

  // static 리소스 처리 메서드 추가
  private void handleStaticResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String fileName = req.getServletPath();
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName.substring(1))) {
      if (inputStream != null) {
        // Content-Type 설정
        if (fileName.endsWith(".css")) {
          resp.setContentType("text/css");
        } else if (fileName.endsWith(".js")) {
          resp.setContentType("application/javascript");
        }
        resp.setCharacterEncoding("UTF-8");
        
        // 파일 내용 전송
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          resp.getOutputStream().write(buffer, 0, bytesRead);
        }
      } else {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
  }
}