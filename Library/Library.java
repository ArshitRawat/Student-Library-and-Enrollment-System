package Library;

import java.sql.*;
import java.util.Scanner;
import StudentEnrollment.MainProgram;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class Library {
	static final String DB_URL = "jdbc:mysql://localhost/";
	static final String USER = "root";
	static final String PASS = "dizzie5048";
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Connecting to database...");
		Class.forName("com.mysql.cj.jdbc.Driver");
		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
		){
			String query = "USE LIBRARY";
			stmt.executeUpdate(query);
			System.out.println("Connection to database successful.");
			System.out.println();
			System.out.flush();  
			int run = 1;
		    while(run!=0) {
		    	  System.out.println("===LIBRARY MANAGEMENT MENU===");
		    	  System.out.println("\t1. DISPLAY BOOKS.");
		    	  System.out.println("\t2. ADD BOOK.");
		    	  System.out.println("\t3. DELETE BOOK.");
		    	  System.out.println("\t4. BORROW BOOK.");
		    	  System.out.println("\t5. RETURN BOOK.");	  
		    	  System.out.println("\t6. DISPLAY BORROW HISTORY.");
		    	  System.out.println("\t7. EXIT.");
		    	  System.out.println();
		    	  System.out.print("Enter your choice:");
		    	  int ch = Integer.valueOf(sc.nextLine());
		    	  System.out.println();
		    	  switch (ch) {
		    	  case 1 -> peekBooks(conn,stmt);
		    	  case 2 -> addBooks(conn,stmt,sc);
		    	  case 3 -> deleteBooks(conn,stmt,sc);
		    	  case 4 -> borrowBook(conn,stmt,sc);
		    	  case 5 -> returnBook(conn,stmt,sc);
		    	  case 6 -> peekHistory(conn,stmt);
		    	  case 7 -> {
		    		  System.out.println("Exiting...");
		    		  run = 0;
		    	  }    	
		    	  default -> System.out.println("Invalid Choice");   	  
		    	  }
		    	  System.out.println();
		    }
		} catch (SQLException e) {
			System.out.println("Connection failed.");
			e.printStackTrace();			
		}
		
		
	}
	
	public static void addBooks(Connection conn, Statement stmt,Scanner sc) throws Exception{
		System.out.print("Name of the book: ");
		String bName = sc.nextLine();
		System.out.print("Author of the book: ");
		String bAuth = sc.nextLine();
		System.out.println("Link to an existing course...");
		System.out.println();
		switchToStudents(conn,stmt);	
		StudentEnrollment.MainProgram.peekCourses(conn, stmt);
		System.out.print("Enter the course id: ");
		String cid = sc.nextLine();	
		System.out.print("Enter Book id: ");
		String bid = sc.nextLine();
		switchToLibrary(conn,stmt);
		System.out.println();
		System.out.println("Adding book...");
		try {
			String query = "insert into books values (?,?,?,?,?) ";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, bid);
			ps.setString(2, bName);
			ps.setString(3, bAuth);
			ps.setString(4, cid);
			ps.setString(5, "YES");
			ps.executeUpdate();
			System.out.println("Book added.");
			System.out.println();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public static void peekBooks(Connection conn, Statement stmt) throws Exception{
		System.out.println("BOOKID : BNAME : AUTHOR : COURSEID : AVAIL");
		try {
			String query = "SELECT* FROM BOOKS";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString(1)+" : "+rs.getString(2)+" : "+rs.getString(3)+" : "+rs.getString(4)+" : "+rs.getString(5));
			}
			System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();			
		}
	}
	
	public static void borrowBook(Connection conn, Statement stmt,Scanner sc) throws Exception{
		peekBooks(conn,stmt);
		System.out.print("Enter the BookId of book to borrow: ");
		String bid = sc.nextLine();
		switchToStudents(conn,stmt);
		System.out.println();
		StudentEnrollment.MainProgram.peekStudents(conn, stmt);
		System.out.println("Please enter your Student ID: ");
		String stdid = sc.nextLine();
		LocalDateTime borDateobj = LocalDateTime.now();
		DateTimeFormatter newBorDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String borDate = borDateobj.format(newBorDate);
		System.out.println("Enter the Due Date format (yyyy-MM-dd HH:mm:ss): ");
		String dueDate = sc.nextLine();
		System.out.println();
		System.out.println("Inserting...");
		switchToLibrary(conn,stmt);
		try {
			String avail = "NO";
			String query1 = "UPDATE BOOKS SET AVAILABLE= ? WHERE BOOKID = ?";
			PreparedStatement ps1 = conn.prepareStatement(query1);
			ps1.setString(1, avail);
			ps1.setString(2, bid);	
			ps1.executeUpdate();			
			String query2 = "INSERT INTO BorrowedBooks (book_id,student_id,borrow_Date,due_Date) VALUES (?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(query2);
			ps.setString(1, bid);
			ps.setString(2, stdid);
			ps.setString(3, borDate);
			ps.setString(4, dueDate);

			int result = ps.executeUpdate();
			if (result == 0) {
				System.out.println("ERROR INSERTING...");
			} else {
				System.out.println("Insertion Complete.");
			}
			System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void peekHistory(Connection conn, Statement stmt)throws Exception {
		System.out.println("SNO. : BOOKID : STDID : BORROWDATE : DUEDATE : RETURNDATE ");
		try {
			String query = "SELECT* FROM BORROWEDBOOKS";
			ResultSet rs = stmt.executeQuery(query);
			while( rs.next()) {
				System.out.println(rs.getInt(1)+" : "+rs.getString(2)+" : "+rs.getString(3)+" : "+rs.getString(4)+" : "+rs.getString(5)+" : "+rs.getString(6));
			}
			System.out.println();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void returnBook(Connection conn, Statement stmt, Scanner sc) throws Exception{
		System.out.println("BookID : BName : Author : CourseID");
		try {
			String query = "Select* from Books where available = 'NO'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println(rs.getString(1)+" : "+rs.getString(1)+" : "+rs.getString(2)+" : "+rs.getString(3)+" : "+rs.getString(4));
			}
			System.out.println();
			System.out.print("Enter the BookId:");
			String id = sc.nextLine();
			System.out.println();
			System.out.println("Returning...");
			String query1 = "UPDATE BOOKS SET available = 'YES' WHERE BOOKID = ?";
			PreparedStatement ps = conn.prepareStatement(query1);
			ps.setString(1, id);
			ps.executeUpdate();
			
			LocalDateTime retObj = LocalDateTime.now();
			DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String returnTime = retObj.format(formatObj);
			String query2 = "UPDATE BORROWEDBOOKS SET RETURN_DATE = ? WHERE BOOK_ID = ?";
			ps = conn.prepareStatement(query2);
			ps.setString(1, returnTime);
			ps.setString(2, id);
			int result = ps.executeUpdate();
			if (result == 0 )
				System.out.println("NO BOOK WITH SUCH ID");
		 	else
				System.out.println("Book returned to the Library.");
			System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	
	
	public static void deleteBooks(Connection conn, Statement stmt, Scanner sc) throws Exception {
		peekBooks(conn,stmt);
		System.out.print("Enter the BookId of book to delete: ");
		String id = sc.nextLine();
		System.out.println("Deleting...");
		try {
			String query = "Delete from books where bookid = ? ";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, id);
			int result = ps.executeUpdate();
			if (result == 0) {
				System.out.println("NO BOOK WITH SUCH ID.");
			}
			else {
				System.out.println("Deletion Complete.");
			}
			System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	
	
	public static void switchToStudents(Connection conn, Statement stmt) throws Exception{
		try {
			String query = "USE STUDENTS";
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void switchToLibrary(Connection conn, Statement stmt) {
		try {
			String query = "Use Library";
			stmt.executeUpdate(query);
		} catch (SQLException e ) {
			e.printStackTrace();
		}
	}
	

}
