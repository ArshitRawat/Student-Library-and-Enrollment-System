package StudentEnrollment;

import java.sql.*;

import java.util.Scanner;

public class MainProgram {

   static final String DB_URL = "jdbc:mysql://localhost/students";
   static final String USER = "root";
   static final String PASS = "dizzie5048";
   static int enroll  = 0;
   
   static Scanner sc = new Scanner(System.in); 
   
   public static void main(String[] args) throws Exception{	   
	   	System.out.println("Connecting to database...");
		Class.forName("com.mysql.cj.jdbc.Driver");
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         Statement stmt = conn.createStatement();
      ) {		      
         String sql = "USE STUDENTS";
         stmt.executeUpdate(sql);
         System.out.println("Connection to database successful.");   	               
      System.out.println();
      retrievevar(conn,stmt);
      int run = 1;
      while(run!=0) {
    	  System.out.println("===ENROLLMENT SYSTEM MENU===");
    	  System.out.println("\t1. DISPLAY COURSES.");
    	  System.out.println("\t2. ADD COURSE.");
    	  System.out.println("\t3. DELETE COURSE.");
    	  System.out.println("\t4. DISPLAY ENROLLMENT LIST.");
    	  System.out.println("\t5. DISPLAY STUDENT DETAILS.");	  
    	  System.out.println("\t6. ENROLL STUDENT.");
    	  System.out.println("\t7. DE-ENROLL STUDENT.");
    	  System.out.println("\t8. EXIT.");
    	  System.out.println();
    	  System.out.print("Enter your choice:");
    	  int ch = Integer.valueOf(sc.nextLine());
    	  System.out.println();
    	  switch (ch) {
    	  case 1 -> peekCourses(conn,stmt);
    	  case 2 -> addCourse(conn,stmt);
    	  case 3 -> deleteCourse(conn,stmt);
    	  case 4 -> peekEnrollment(conn,stmt);
    	  case 5 -> peekStudents(conn,stmt);
    	  case 6 -> enrollStudents(conn,stmt);
    	  case 7 -> deleteEnrollment(conn,stmt);
    	  case 8 -> {
    		  System.out.println("Exiting...");
    		  run = 0;
    	  }    	
    	  default -> System.out.println("Invalid Choice");   	  
    	  }
    	  System.out.println();    	  
      }
      insertvar(conn,stmt);   
   }catch (SQLException e) {
    	  System.out.println("Connection failed...");
         //e.printStackTrace();
      }
   }
   public static void addCourse(Connection conn, Statement stmt) throws Exception{
   
	   System.out.print("Enter the course id: ");
	   String cid = sc.nextLine();
	   System.out.print("Enter the course name: ");
	   String cname = sc.nextLine();
	   System.out.println();
	   System.out.println("Inserting...");
	      try{	    	  
	         String query = "INSERT INTO COURSES VALUES (?,?)";
	         PreparedStatement ps = conn.prepareStatement(query);
	         ps.setString(1, cid);
	         ps.setString(2, cname);
	         ps.executeUpdate();
	         System.out.println("Insertion Successful");   	  
	      } catch (SQLException e) {
	    	  System.out.println("Insertion failed...");
	         e.printStackTrace();
	      }
	      System.out.println();
   }
   
   public static void peekCourses(Connection conn, Statement stmt) throws Exception{
	   System.out.println("Courses Table:");
	   System.out.println("CourseID : CourseName");
	      try {	    	  
	         String query = "SELECT* FROM COURSES";
	         ResultSet rs = stmt.executeQuery(query);
	         while(rs.next()) {
	        	 System.out.println(rs.getString(1)+" : "+rs.getString(2));
	         }
	      } catch (SQLException e) {
	    	  System.out.println("NO FIELDS ENTERED");
	         e.printStackTrace();
	      }
	      System.out.println();
   }
   
   public static void deleteCourse(Connection conn, Statement stmt) throws Exception {

	   peekCourses(conn,stmt);
	   System.out.print("Enter the CourseID of the course to delete: ");
	   String cid = sc.nextLine();
	   System.out.println();
	   System.out.println("Deleting...");
	   try {  	  
		         String query = "DELETE FROM COURSES WHERE CID = ?";
		         PreparedStatement ps = conn.prepareStatement(query);
		         ps.setString(1, cid);
		         int result = ps.executeUpdate();
		         if (result == 0 ) {
		        	 System.out.println("NO COURSE WITH SUCH ID");		         
		         } else {
		        	 System.out.println("Deletion Successful.");
		         }
		         
		      } catch (SQLException e) {
		    	  System.out.println("NO FIELDS ENTERED");
		         e.printStackTrace();
		      }
	   System.out.println();	   	   
   }
   
   public static void addStudents(String stid, String sname, String no, String residence,Connection conn, Statement stmt) throws Exception {
	   try {	    	  
		         String query = "INSERT INTO STUDENTS VALUES (?,?,?,?)";		         
		         PreparedStatement ps = conn.prepareStatement(query);
		         ps.setString(1, stid);
		         ps.setString(2, sname);
		         ps.setString(3, no);
		         ps.setString(4, residence);
		         ps.executeUpdate();	         		         
		      } catch (SQLException e) {
		    	  System.out.println("NO FIELDS ENTERED");
		         e.printStackTrace();
		      }   		 		  	  	  	   	   
   }
   
   public static void peekStudents(Connection conn,Statement stmt) throws SQLException {	   
	   System.out.println("SId : SName : SPhoneNo : CId");
	   try{
		   String query ="SELECT* FROM STUDENTS";
		   ResultSet rs = stmt.executeQuery(query);
		   while (rs.next()) {
			   System.out.println(rs.getString(1)+" : "+rs.getString(2)+" : "+rs.getString(3)+" : "+rs.getString(4));
		   } 		   	   		
	   }
	   catch (SQLException e) {		   
		   e.printStackTrace();
	   }
	   System.out.println();			   
   }
   
   public static void deleteStudents(Connection conn, Statement stmt) throws Exception{
	   peekStudents(conn,stmt);
	   System.out.println("Enter the StudentId of student to be deleted: ");
	   String id = sc.nextLine();
	   System.out.println();
	   System.out.println("Deleting...");
	   try{
		   String query = "DELETE FROM STUDENTS WHERE STDID = ?";
		   PreparedStatement ps = conn.prepareStatement(query);
		   ps.setString(1, id);
		   int result = ps.executeUpdate();
		   if(result == 0) {
			   System.out.println("NO STUDENT WITH SUCH ID FOUND.");
		   } else {
			   System.out.println("Deletion from 'STUDENTS' Successful.");
		   }
	   } catch (SQLException e) {
		   e.printStackTrace();
	   }
	   System.out.println();
	   
   }
   
   public static void enrollStudents(Connection conn, Statement stmt) throws Exception {
	   enroll++;
	   String enrollid ="";
	   if(enroll>9) {
		   enrollid = "0"+String.valueOf(enroll);
	   } else {
		   enrollid = "00"+String.valueOf(enroll);
	   }
	   System.out.print("Enter the student name: ");
	   String sname = sc.nextLine();	  
	   System.out.println();
	   peekCourses(conn, stmt);
	   System.out.print("What course to enroll in? ");
	   String cid = sc.nextLine();	   
	   System.out.print("Enter student's PhoneNo.: ");
	   String no = sc.nextLine();
	   System.out.print("Student's Residence? (Scholar or Hostelite) ");
	   String residence = sc.nextLine();
	   System.out.println();
	   System.out.println("Inserting...");	
	   
	   addStudents(enrollid,sname,no,residence,conn,stmt);
	   
	   try{	    	  
		         String query = "INSERT INTO ENROLLMENT VALUES (?,?,?,?)";
		         
		         PreparedStatement ps = conn.prepareStatement(query);
		         ps.setString(1, enrollid);
		         ps.setString(2, enrollid);
		         ps.setString(3, sname);
		         ps.setString(4, cid);
		         ps.executeUpdate();
		         System.out.println("Insertion Successful.");
		         		         
		      } catch (SQLException e) {
		    	  System.out.println("NO FIELDS ENTERED");
		         e.printStackTrace();
		      }
	   System.out.println();	   		 		  	  	  	   	   
   }
   
   public static void peekEnrollment(Connection conn,Statement stmt) throws SQLException {	   
	   System.out.println("EnrollID : SID : SName : CId");
	   try{
		   String query ="SELECT* FROM ENROLLMENT";
		   ResultSet rs = stmt.executeQuery(query);
		   while (rs.next()) {
			   System.out.println(rs.getString(1)+" : "+rs.getString(2)+" : "+rs.getString(3)+" : "+rs.getString(4));
		   } 		   	   		
	   }
	   catch (SQLException e) {		   
		   e.printStackTrace();
	   }
	   System.out.println();			   
   }
   
   public static void deleteEnrollment(Connection conn,Statement stmt) throws Exception{	   
	   peekEnrollment(conn, stmt);
	   System.out.println("Enter the enrollment id of the student: ");
	   String id = sc.nextLine();
	   System.out.println();
	   System.out.println("Deleting...");
	   try{
		   String query = "DELETE FROM ENROLLMENT WHERE ENROLLID = ?";
		   PreparedStatement ps = conn.prepareStatement(query);
		   ps.setString(1, id);
		   int result = ps.executeUpdate();
		   if ( result == 0) {
			   System.out.println("NO ENROLLMENT WITH SUCH ID.");
		   }		   		   
	   }catch (SQLException e) {
		   e.printStackTrace();
	   }
	   deleteStudents(conn,stmt);
	   System.out.println();
	   
   }
   
   
   
   
   public static void insertvar(Connection conn, Statement stmt) throws Exception{
	   try{
		   String query = "UPDATE VARSTORE SET VALUE = ? WHERE ID = 1";
		   PreparedStatement ps = conn.prepareStatement(query);
		   ps.setInt(1, enroll);
		   ps.executeUpdate();
		   System.out.println("Values saved.");
		   
	   } catch (SQLException e) {
		   e.printStackTrace();
	   }
	   System.out.println();
   }
   
   public static void retrievevar(Connection conn, Statement stmt) throws Exception{
	   System.out.println("Retrieving Ids...");
	   try{
		   String query = "Select value from varstore";
		   ResultSet rs = stmt.executeQuery(query);
		   rs.next();
		   enroll = rs.getInt("value");	   
		   System.out.println("Successful Retrieval of values.");		   	   		   		   
	   }catch (SQLException e) {
		   e.printStackTrace();
	   }
	   System.out.println();
   }
   

    
   
   
   
   
}
