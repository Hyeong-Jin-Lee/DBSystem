package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {
    // 데이터베이스 연결 정보
    private static final String URL = "jdbc:mysql://192.168.56.101:4567/madang?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
//  private static final String URL = "jdbc:mysql://localhost:3306/kakao?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nChoose an operation:");
                System.out.println("1. Insert Data");
                System.out.println("2. Search Data");
                System.out.println("3. Delete Data");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                switch (choice) {
                    case 1 -> insertData(connection, scanner);
                    case 2 -> searchData(connection);
                    case 3 -> deleteData(connection, scanner);
                    case 4 -> {
                        System.out.println("Exiting program.");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    // 데이터 삽입 함수
    private static void insertData(Connection connection, Scanner scanner) {
        System.out.print("Enter Book Id: ");
        int bookid = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Book Name: ");
        String bookname = scanner.nextLine();

        System.out.print("Enter Publisher: ");
        String publisher = scanner.nextLine();

        System.out.print("Enter Price: ");
        int price = scanner.nextInt();

        String sql = "INSERT INTO Book (bookid, bookname, publisher, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookid);
            preparedStatement.setString(2, bookname);
            preparedStatement.setString(3, publisher);
            preparedStatement.setInt(4, price);
            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    // 데이터 검색 함수
    private static void searchData(Connection connection) {
        System.out.println("Choose an operation: ");
        System.out.println("1. Search All");
        System.out.println("2. Search bookid");
        System.out.println("3. Search bookname");
        System.out.println("4. Search publisher");
        System.out.print("Enter your choice: ");

        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        String colName = "";
        String input = scanner.nextLine();

        if(num==2) colName = "bookid";
        else if(num==3) colName = "bookname";
        else if(num==4) colName = "publisher";

        if(num<1||num>4){
            return;
        }

        if(num>1) {
            System.out.print("Input "+colName+": ");
            input = scanner.nextLine();
        }

        String sql = "SELECT * FROM Book";

        if(num == 2) {
            sql +=  " WHERE "+colName+"="+input;
        } else if(num==3||num==4){
            sql +=  " WHERE "+colName+ " LIKE '%" + input + "%'" ; // where
        }

        System.out.println(sql);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Search Results:");


            while (resultSet.next()) {
                int bookid = resultSet.getInt("bookid");
                String bookname = resultSet.getString("bookname");
                String publisher = resultSet.getString("publisher");
                int price = resultSet.getInt("price");




                System.out.printf("ID: %d, Name: %s, Publisher: %s, Price: %d%n",
                        bookid, bookname, publisher, price);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving data: " + e.getMessage());
        }
    }

    // 데이터 삭제 함수
    private static void deleteData(Connection connection, Scanner scanner) {
        System.out.print("Enter Book ID to delete: ");
        int bookId = scanner.nextInt();

        String sql = "DELETE FROM Book WHERE bookid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data deleted successfully.");
            } else {
                System.out.println("No data found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting data: " + e.getMessage());
        }
    }
}
