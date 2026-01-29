package kz.assign.api;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final String URL  = "jdbc:postgresql://localhost:5432/oop_db";
    private static final String USER = "postgres";
    private static final String PASS = "1234"; // <-- change if your password is different

    private Connection conn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    @GetMapping
    public List<Map<String, Object>> getAll() throws SQLException {
        String sql = "SELECT id, code, title, capacity FROM courses ORDER BY code";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", rs.getObject("id").toString());
                row.put("code", rs.getString("code"));
                row.put("title", rs.getString("title"));
                row.put("capacity", rs.getInt("capacity"));
                list.add(row);
            }
            return list;
        }
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> body) throws SQLException {
        String sql = "INSERT INTO courses (id, code, title, capacity) VALUES (gen_random_uuid(), ?, ?, ?)";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(body.getOrDefault("code", "")));
            ps.setString(2, String.valueOf(body.getOrDefault("title", "")));
            ps.setInt(3, Integer.parseInt(String.valueOf(body.getOrDefault("capacity", 0))));
            int rows = ps.executeUpdate();
            return Map.of("status", "created", "rows", rows);
        }
    }


    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable String id, @RequestBody Map<String, Object> body) throws SQLException {
        String sql = "UPDATE courses SET title=?, capacity=? WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(body.getOrDefault("title", "")));
            ps.setInt(2, Integer.parseInt(String.valueOf(body.getOrDefault("capacity", 0))));
            ps.setObject(3, UUID.fromString(id));
            int rows = ps.executeUpdate();
            return Map.of("status", "updated", "rows", rows);
        }
    }


    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable String id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(id));
            int rows = ps.executeUpdate();
            return Map.of("status", "deleted", "rows", rows);
        }
    }
}
