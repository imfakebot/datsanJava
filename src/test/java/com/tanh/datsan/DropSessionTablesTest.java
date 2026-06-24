package com.tanh.datsan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class DropSessionTablesTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void dropTables() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS SPRING_SESSION_ATTRIBUTES");
            jdbcTemplate.execute("DROP TABLE IF EXISTS SPRING_SESSION");
            System.out.println("========== DROPPED SPRING SESSION TABLES ==========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
