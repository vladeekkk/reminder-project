package com.example.reminder.test;

import com.example.reminder.Reminder;
import com.example.reminder.ReminderDAOImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class ReminderDAOImplTest {
    private Connection connect() {
        String url = "jdbc:sqlite:identifier.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    @Before
    public void delete() {
        String sql = "DELETE FROM data_base";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testSave() {
        ReminderDAOImpl dataBase = new ReminderDAOImpl();
        dataBase.save(new Reminder(1, "01.02.03", "test1"));
        dataBase.save(new Reminder(2, "04.05.05", "test2"));
        dataBase.save(new Reminder(3, "07.08.09", "test3"));
        dataBase.save(new Reminder(4, "10.11.12", "test4"));
        dataBase.save(new Reminder(5, "13.14.15", "test5"));

        ArrayList<Reminder> check = new ArrayList<>();
        check.add(new Reminder(1, "01.02.03", "test1"));
        check.add(new Reminder(2, "04.05.05", "test2"));
        check.add(new Reminder(3, "07.08.09", "test3"));
        check.add(new Reminder(4, "10.11.12", "test4"));
        check.add(new Reminder(5, "13.14.15", "test5"));
        List<Reminder> check2 = dataBase.findAll();
        Assert.assertEquals(check, check2);
    }

    @Test
    public void testSaveWithDuplicate() {
        ReminderDAOImpl dataBase = new ReminderDAOImpl();
        dataBase.save(new Reminder(1, "01.02.03", "test1"));
        dataBase.save(new Reminder(2, "04.05.05", "test2"));
        dataBase.save(new Reminder(3, "07.08.09", "test3"));
        dataBase.save(new Reminder(4, "10.11.12", "test4"));
        dataBase.save(new Reminder(5, "13.14.15", "test5"));

        dataBase.save(new Reminder(1, "01.02.03", "test1"));
        dataBase.save(new Reminder(2, "04.05.05", "test2"));
        dataBase.save(new Reminder(3, "07.08.09", "test3"));
        dataBase.save(new Reminder(4, "10.11.12", "test4"));
        dataBase.save(new Reminder(5, "13.14.15", "test5"));

        ArrayList<Reminder> check = new ArrayList<>();
        check.add(new Reminder(1, "01.02.03", "test1"));
        check.add(new Reminder(2, "04.05.05", "test2"));
        check.add(new Reminder(3, "07.08.09", "test3"));
        check.add(new Reminder(4, "10.11.12", "test4"));
        check.add(new Reminder(5, "13.14.15", "test5"));
        List<Reminder> check2 = dataBase.findAll();
        Assert.assertEquals(check, check2);
    }

    @Test
    public void testUpdate() {
        ReminderDAOImpl dataBase = new ReminderDAOImpl();
        dataBase.save(new Reminder(1, "01.02.03", "test1BeforeUpdate"));
        dataBase.save(new Reminder(2, "04.05.05", "test2BeforeUpdate"));
        dataBase.save(new Reminder(3, "07.08.09", "test3BeforeUpdate"));
        dataBase.save(new Reminder(4, "10.11.12", "test4BeforeUpdate"));
        dataBase.save(new Reminder(5, "13.14.15", "test5BeforeUpdate"));

        dataBase.update(new Reminder(1, "01.02.03", "test1"));
        dataBase.update(new Reminder(2, "04.05.05", "test2"));
        dataBase.update(new Reminder(3, "07.08.09", "test3"));
        dataBase.update(new Reminder(4, "10.11.12", "test4"));
        dataBase.update(new Reminder(5, "13.14.15", "test5"));

        ArrayList<Reminder> check = new ArrayList<>();
        check.add(new Reminder(1, "01.02.03", "test1"));
        check.add(new Reminder(2, "04.05.05", "test2"));
        check.add(new Reminder(3, "07.08.09", "test3"));
        check.add(new Reminder(4, "10.11.12", "test4"));
        check.add(new Reminder(5, "13.14.15", "test5"));
        List<Reminder> check2 = dataBase.findAll();
        Assert.assertEquals(check, check2);
    }

    @Test
    public void testUpdateNotExist() {
        ReminderDAOImpl dataBase = new ReminderDAOImpl();
        dataBase.save(new Reminder(1, "01.02.03", "test1"));
        dataBase.save(new Reminder(2, "04.05.05", "test2"));
        dataBase.save(new Reminder(3, "07.08.09", "test3"));
        dataBase.save(new Reminder(4, "10.11.12", "test4"));
        dataBase.save(new Reminder(5, "13.14.15", "test5"));

        dataBase.update(new Reminder(6, "01.02.03", "test6"));
        dataBase.update(new Reminder(7, "04.05.05", "test7"));
        dataBase.update(new Reminder(8, "07.08.09", "test8"));
        dataBase.update(new Reminder(9, "10.11.12", "test9"));
        dataBase.update(new Reminder(10, "13.14.15", "test10"));

        ArrayList<Reminder> check = new ArrayList<>();
        check.add(new Reminder(1, "01.02.03", "test1"));
        check.add(new Reminder(2, "04.05.05", "test2"));
        check.add(new Reminder(3, "07.08.09", "test3"));
        check.add(new Reminder(4, "10.11.12", "test4"));
        check.add(new Reminder(5, "13.14.15", "test5"));
        check.add(new Reminder(6, "01.02.03", "test6"));
        check.add(new Reminder(7, "04.05.05", "test7"));
        check.add(new Reminder(8, "07.08.09", "test8"));
        check.add(new Reminder(9, "10.11.12", "test9"));
        check.add(new Reminder(10, "13.14.15", "test10"));
        List<Reminder> check2 = dataBase.findAll();
        Assert.assertEquals(check, check2);
    }

    @Test
    public void testDelete() {
        ReminderDAOImpl dataBase = new ReminderDAOImpl();
        dataBase.save(new Reminder(1, "01.02.03", "test1"));
        dataBase.save(new Reminder(2, "04.05.05", "test2"));
        dataBase.save(new Reminder(3, "07.08.09", "test3"));
        dataBase.save(new Reminder(4, "10.11.12", "test4"));
        dataBase.save(new Reminder(5, "13.14.15", "test5"));

        dataBase.delete(new Reminder(1, "01.02.03", "test1"));
        dataBase.delete(new Reminder(3, "07.08.09", "test3"));
        dataBase.delete(new Reminder(5, "13.14.15", "test5"));

        ArrayList<Reminder> check = new ArrayList<>();
        check.add(new Reminder(2, "04.05.05", "test2"));
        check.add(new Reminder(4, "10.11.12", "test4"));
        List<Reminder> check2 = dataBase.findAll();
        Assert.assertEquals(check, check2);
    }

    @Test
    public void testDeleteNotExist() {
        ReminderDAOImpl dataBase = new ReminderDAOImpl();
        dataBase.save(new Reminder(1, "01.02.03", "test1"));
        dataBase.save(new Reminder(2, "04.05.05", "test2"));
        dataBase.save(new Reminder(3, "07.08.09", "test3"));
        dataBase.save(new Reminder(4, "10.11.12", "test4"));
        dataBase.save(new Reminder(5, "13.14.15", "test5"));

        dataBase.delete(new Reminder(6, "01.02.03", "test1"));
        dataBase.delete(new Reminder(7, "07.08.09", "test3"));
        dataBase.delete(new Reminder(8, "13.14.15", "test5"));

        ArrayList<Reminder> check = new ArrayList<>();
        check.add(new Reminder(1, "01.02.03", "test1"));
        check.add(new Reminder(2, "04.05.05", "test2"));
        check.add(new Reminder(3, "07.08.09", "test3"));
        check.add(new Reminder(4, "10.11.12", "test4"));
        check.add(new Reminder(5, "13.14.15", "test5"));
        List<Reminder> check2 = dataBase.findAll();
        Assert.assertEquals(check, check2);
    }

    @Test
    public void testStress() {
        ReminderDAOImpl dataBase = new ReminderDAOImpl();
        ArrayList<Reminder> check = new ArrayList<>();
        HashMap<Integer, Boolean> idCheck = new HashMap<>();
        Random rand = new Random(239);
        for (int i = 0; i < 1000; i++) {
            int idTest = 0;
            while (idCheck.containsKey(idTest)) {
                idTest = rand.nextInt(10000);
            }
            idCheck.put(idTest, true);
            String dateTest = String.valueOf(rand.nextInt(30) + 1) + '.'
                    + String.valueOf(rand.nextInt(12) + 1) + '.'
                    + "20" + String.valueOf(rand.nextInt(21));
            StringBuilder commentTest = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                commentTest.append((char) ('a' + rand.nextInt(26)));
            }
            int command = rand.nextInt(14);
            if (command == 0) {
                dataBase.save(new Reminder(idTest, dateTest, commentTest.toString()));
                check.add(new Reminder(idTest, dateTest, commentTest.toString()));
            } else if (command == 1) {
                dataBase.delete(new Reminder(idTest, dateTest, commentTest.toString()));
                check.remove(new Reminder(idTest, dateTest, commentTest.toString()));
            } else if (command == 3) {
                List<Reminder> checkData = dataBase.findAll();
                Assert.assertEquals(check, checkData);
            }

        }
        List<Reminder> check2 = dataBase.findAll();
        Assert.assertEquals(check2, check);
    }
}