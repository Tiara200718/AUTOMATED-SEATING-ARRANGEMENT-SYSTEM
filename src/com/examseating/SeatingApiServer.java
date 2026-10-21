
package com.examseating;

import com.examseating.dao.DatabaseManager;
import com.examseating.model.Exam;
import com.examseating.model.Room;
import com.examseating.model.SeatAllocation;
import com.examseating.model.Student;
import com.examseating.service.SeatingArrangementEngine;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeatingApiServer {

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(
        System.getenv().getOrDefault("PORT", "8000"));

HttpServer server = HttpServer.create(
        new InetSocketAddress("0.0.0.0", port), 0);

        // Health check
        server.createContext("/health", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, "{\"error\":\"Use GET\"}");
                return;
            }

            send(exchange, 200,
                    "{\"status\":\"Java backend running\"}");
        });

        // =====================================================
        // STUDENT MANAGEMENT
        // GET /students
        // POST /students
        // =====================================================

        server.createContext("/students", exchange -> {

            String method = exchange.getRequestMethod();
            DatabaseManager db = DatabaseManager.getInstance();

            if ("GET".equalsIgnoreCase(method)) {
                try {
                    List<Student> students = db.getAllStudents();
                    StringBuilder json = new StringBuilder("[");

                    for (int i = 0; i < students.size(); i++) {
                        Student s = students.get(i);

                        if (i > 0) {
                            json.append(",");
                        }

                        json.append("{")
                            .append("\"id\":").append(s.getId()).append(",")
                            .append("\"rollNumber\":\"")
                            .append(escape(s.getRollNumber())).append("\",")
                            .append("\"name\":\"")
                            .append(escape(s.getName())).append("\",")
                            .append("\"department\":\"")
                            .append(escape(s.getDepartment())).append("\",")
                            .append("\"semester\":").append(s.getSemester())
                            .append("}");
                    }

                    json.append("]");
                    send(exchange, 200, json.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    send(exchange, 500,
                            "{\"error\":\"Could not retrieve students\"}");
                }
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                try {
                    String body = readBody(exchange);

                    String roll = jsonString(body, "rollNumber").trim();
                    String name = jsonString(body, "name").trim();
                    String department =
                            jsonString(body, "department").trim();
                    int semester = Integer.parseInt(
                            jsonString(body, "semester"));

                    if (roll.isEmpty() || name.isEmpty()
                            || department.isEmpty()
                            || semester < 1 || semester > 12) {
                        send(exchange, 400,
                                "{\"error\":\"Enter valid student details\"}");
                        return;
                    }

                    for (Student existing : db.getAllStudents()) {
                        if (existing.getRollNumber() != null
                                && existing.getRollNumber()
                                    .equalsIgnoreCase(roll)) {
                            send(exchange, 409,
                                    "{\"error\":\"Roll number already exists\"}");
                            return;
                        }
                    }

                    boolean added = db.addStudent(
                            new Student(roll, name, department, semester));

                    if (added) {
                        send(exchange, 201,
                                "{\"success\":true,"
                                + "\"message\":\"Student added successfully\"}");
                    } else {
                        send(exchange, 400,
                                "{\"error\":\"Could not add student\"}");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    send(exchange, 400,
                            "{\"error\":\"Invalid student details\"}");
                }
                return;
            }

            send(exchange, 405, "{\"error\":\"Use GET or POST\"}");
        });

        // =====================================================
        // ROOM MANAGEMENT
        // GET /rooms
        // POST /rooms
        // =====================================================

        server.createContext("/rooms", exchange -> {

            String method = exchange.getRequestMethod();
            DatabaseManager db = DatabaseManager.getInstance();

            if ("GET".equalsIgnoreCase(method)) {
                try {
                    List<Room> rooms = db.getAllRooms();
                    StringBuilder json = new StringBuilder("[");

                    for (int i = 0; i < rooms.size(); i++) {
                        Room room = rooms.get(i);

                        if (i > 0) {
                            json.append(",");
                        }

                        json.append("{")
                            .append("\"id\":").append(room.getId()).append(",")
                            .append("\"roomCode\":\"")
                            .append(escape(room.getRoomCode())).append("\",")
                            .append("\"building\":\"")
                            .append(escape(room.getBuilding())).append("\",")
                            .append("\"totalRows\":")
                            .append(room.getTotalRows()).append(",")
                            .append("\"totalColumns\":")
                            .append(room.getTotalColumns()).append(",")
                            .append("\"seatsPerBench\":")
                            .append(room.getSeatsPerBench()).append(",")
                            .append("\"totalCapacity\":")
                            .append(room.getTotalCapacity())
                            .append("}");
                    }

                    json.append("]");
                    send(exchange, 200, json.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    send(exchange, 500,
                            "{\"error\":\"Could not retrieve rooms\"}");
                }
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                try {
                    String body = readBody(exchange);

                    String roomCode =
                            jsonString(body, "roomCode").trim();
                    String building =
                            jsonString(body, "building").trim();
                    int rows = Integer.parseInt(
                            jsonString(body, "totalRows"));
                    int columns = Integer.parseInt(
                            jsonString(body, "totalColumns"));
                    int seatsPerBench = Integer.parseInt(
                            jsonString(body, "seatsPerBench"));

                    if (roomCode.isEmpty() || building.isEmpty()
                            || rows < 1 || columns < 1
                            || seatsPerBench < 1) {
                        send(exchange, 400,
                                "{\"error\":\"Enter valid room details\"}");
                        return;
                    }

                    for (Room existing : db.getAllRooms()) {
                        if (existing.getRoomCode() != null
                                && existing.getRoomCode()
                                    .equalsIgnoreCase(roomCode)) {
                            send(exchange, 409,
                                    "{\"error\":\"Room code already exists\"}");
                            return;
                        }
                    }

                    // Constructor order follows DatabaseManager.getAllRooms().
                    Room room = new Room(
                            0,
                            roomCode,
                            building,
                            rows,
                            columns,
                            seatsPerBench
                    );

                    boolean added = db.addRoom(room);

                    if (added) {
                        send(exchange, 201,
                                "{\"success\":true,"
                                + "\"message\":\"Room added successfully\","
                                + "\"totalCapacity\":"
                                + room.getTotalCapacity() + "}");
                    } else {
                        send(exchange, 400,
                                "{\"error\":\"Could not add room\"}");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    send(exchange, 400,
                            "{\"error\":\"Invalid room details\"}");
                }
                return;
            }

            send(exchange, 405, "{\"error\":\"Use GET or POST\"}");
        });

        // =====================================================
        // GENERATE SEATING ARRANGEMENT
        // POST /generate
        // =====================================================

        server.createContext("/generate", exchange -> {

            if (!"POST".equalsIgnoreCase(
                    exchange.getRequestMethod())) {
                send(exchange, 405, "{\"error\":\"Use POST\"}");
                return;
            }

            try {
                DatabaseManager db = DatabaseManager.getInstance();
                db.seedSampleDataIfEmpty();

                List<Room> rooms = db.getAllRooms();
                List<Student> students = db.getAllStudents();
                List<Exam> exams = db.getAllExams();

                if (exams.isEmpty()) {
                    send(exchange, 400,
                            "{\"error\":\"No exams found\"}");
                    return;
                }

                if (students.isEmpty()) {
                    send(exchange, 400,
                            "{\"error\":\"No students found\"}");
                    return;
                }

                if (rooms.isEmpty()) {
                    send(exchange, 400,
                            "{\"error\":\"No rooms found\"}");
                    return;
                }

                Exam exam = exams.get(0);

                SeatingArrangementEngine engine =
                        new SeatingArrangementEngine();

                SeatingArrangementEngine.AllocationResult result =
                        engine.allocateSeats(
                                exam.getId(), rooms, students);

                if (!result.isSuccess()) {
                    send(exchange, 400,
                            "{\"error\":\""
                            + escape(result.getMessage()) + "\"}");
                    return;
                }

                db.saveAllocations(
                        exam.getId(), result.getAllocations());

                List<SeatAllocation> allocations =
                        db.getAllocationsForExam(exam.getId());

                StringBuilder json = new StringBuilder();

                json.append("{")
                    .append("\"success\":true,")
                    .append("\"message\":\"")
                    .append(escape(result.getMessage())).append("\",")
                    .append("\"totalStudents\":")
                    .append(result.getTotalStudents()).append(",")
                    .append("\"allocatedStudents\":")
                    .append(result.getAllocatedStudents()).append(",")
                    .append("\"roomsUsed\":")
                    .append(result.getRoomsUsed()).append(",")
                    .append("\"executionTimeMs\":")
                    .append(result.getExecutionTimeMs()).append(",")
                    .append("\"allocations\":[");

                for (int i = 0; i < allocations.size(); i++) {
                    SeatAllocation a = allocations.get(i);

                    if (i > 0) {
                        json.append(",");
                    }

                    json.append("{")
                        .append("\"studentRoll\":\"")
                        .append(escape(a.getStudentRoll())).append("\",")
                        .append("\"studentName\":\"")
                        .append(escape(a.getStudentName())).append("\",")
                        .append("\"department\":\"")
                        .append(escape(a.getDepartment())).append("\",")
                        .append("\"roomCode\":\"")
                        .append(escape(a.getRoomCode())).append("\",")
                        .append("\"benchRow\":")
                        .append(a.getBenchRow()).append(",")
                        .append("\"benchCol\":")
                        .append(a.getBenchCol()).append(",")
                        .append("\"seatPosition\":\"")
                        .append(escape(a.getSeatPositionText())).append("\",")
                        .append("\"seatCoordinate\":\"")
                        .append(escape(a.getSeatCoordinate())).append("\"")
                        .append("}");
                }

                json.append("]}");
                send(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500,
                        "{\"error\":\"Seating generation failed\"}");
            }
        });

        
        // =====================================================
        // VIEW SEATING ARRANGEMENT
        // GET /allocations
        // =====================================================
        server.createContext("/allocations", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, "{\"error\":\"Use GET\"}");
                return;
            }

            try {
                DatabaseManager db = DatabaseManager.getInstance();
                List<Exam> exams = db.getAllExams();

                if (exams == null || exams.isEmpty()) {
                    send(exchange, 200, "[]");
                    return;
                }

                Exam exam = exams.get(0);
                List<SeatAllocation> allocations =
                        db.getAllocationsForExam(exam.getId());

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < allocations.size(); i++) {
                    SeatAllocation a = allocations.get(i);

                    if (i > 0) {
                        json.append(",");
                    }

                    json.append("{")
                        .append("\"studentRoll\":\"")
                        .append(escape(a.getStudentRoll())).append("\",")
                        .append("\"studentName\":\"")
                        .append(escape(a.getStudentName())).append("\",")
                        .append("\"department\":\"")
                        .append(escape(a.getDepartment())).append("\",")
                        .append("\"roomCode\":\"")
                        .append(escape(a.getRoomCode())).append("\",")
                        .append("\"benchRow\":").append(a.getBenchRow()).append(",")
                        .append("\"benchCol\":").append(a.getBenchCol()).append(",")
                        .append("\"seatPosition\":\"")
                        .append(escape(a.getSeatPositionText())).append("\",")
                        .append("\"seatCoordinate\":\"")
                        .append(escape(a.getSeatCoordinate())).append("\"")
                        .append("}");
                }

                json.append("]");
                send(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500,
                        "{\"error\":\"Could not load seating allocations\"}");
            }
        });


server.start();

        System.out.println(
                "Java Seating API running at http://127.0.0.1:8000");
        System.out.println("Available endpoints:");
        System.out.println("GET  /health");
        System.out.println("GET  /students");
        System.out.println("POST /students");
        System.out.println("GET  /rooms");
        System.out.println("POST /rooms");
        System.out.println("POST /generate");
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private static String readBody(HttpExchange exchange)
            throws IOException {
        return new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static String jsonString(String json, String key) {
        String expression = "\""
                + Pattern.quote(key)
                + "\"\\s*:\\s*(?:\"((?:\\\\.|[^\"\\\\])*)\"|(-?\\d+))";

        Matcher matcher = Pattern.compile(expression).matcher(json);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Missing field: " + key);
        }

        if (matcher.group(1) == null) {
            return matcher.group(2);
        }

        return unescapeJson(matcher.group(1));
    }

    private static String unescapeJson(String value) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c != '\\' || i + 1 >= value.length()) {
                result.append(c);
                continue;
            }

            char next = value.charAt(++i);

            switch (next) {
                case '"': result.append('"'); break;
                case '\\': result.append('\\'); break;
                case '/': result.append('/'); break;
                case 'b': result.append('\b'); break;
                case 'f': result.append('\f'); break;
                case 'n': result.append('\n'); break;
                case 'r': result.append('\r'); break;
                case 't': result.append('\t'); break;

                case 'u':
                    if (i + 4 >= value.length()) {
                        throw new IllegalArgumentException(
                                "Invalid Unicode escape");
                    }

                    String hex = value.substring(i + 1, i + 5);
                    result.append((char) Integer.parseInt(hex, 16));
                    i += 4;
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Invalid JSON escape");
            }
        }

        return result.toString();
    }

    private static void send(
            HttpExchange exchange, int status, String response)
            throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "http://localhost:8501");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
