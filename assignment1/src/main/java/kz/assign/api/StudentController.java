package kz.assign.api;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private static final String URL  = "jdbc:postgresql://localhost:5432/oop_db";
    private static final String USER = "postgres";
    private static final String PASS = "1234";

    private Connection conn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }


    @GetMapping
    public List<Map<String, Object>> getAll() throws SQLException {
        String sql = "SELECT id, first_name, last_name, major FROM students ORDER BY first_name";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", rs.getObject("id").toString());
                row.put("firstName", rs.getString("first_name"));
                row.put("lastName", rs.getString("last_name"));
                row.put("major", rs.getString("major"));
                list.add(row);
            }
            return list;
        }
    }


    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, String> body) throws SQLException {
        String sql = "INSERT INTO students (id, first_name, last_name, major) VALUES (gen_random_uuid(), ?, ?, ?)";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, body.getOrDefault("firstName", ""));
            ps.setString(2, body.getOrDefault("lastName", ""));
            ps.setString(3, body.getOrDefault("major", ""));
            int rows = ps.executeUpdate();
            return Map.of("status", "created", "rows", rows);
        }
    }


    @PutMapping("/{id}")
    public Map<String, Object> updateMajor(@PathVariable String id, @RequestBody Map<String, String> body) throws SQLException {
        String sql = "UPDATE students SET major=? WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, body.getOrDefault("major", ""));
            ps.setObject(2, UUID.fromString(id));
            int rows = ps.executeUpdate();
            return Map.of("status", "updated", "rows", rows);
        }
    }


    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable String id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(id));
            int rows = ps.executeUpdate();
            return Map.of("status", "deleted", "rows", rows);
        }
    }
}
