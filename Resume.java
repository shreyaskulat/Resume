package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/resume_search")
public class Resume extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection con;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/1eja8", "root", "sql@123");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();

        // Retrieve user input
        String name = req.getParameter("name");
        String email = req.getParameter("email");

        try {
            // Fetch resume information from the database
            String sql = "SELECT * FROM feedback WHERE name=? AND email=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If a matching resume is found
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                String about = rs.getString("about");
                String feedback = rs.getString("feedback");
                int rating = rs.getInt("rating");

                pw.println("<!DOCTYPE html>");
                pw.println("<html>");
                pw.println("<head>");
                pw.println("<title>Resume Information</title>");
                pw.println("</head>");
                pw.println("<body>");
                pw.println("<h2>Resume Information:</h2>");
                pw.println("<p>Name: " + name + "</p>");
                pw.println("<p>Email: " + email + "</p>");
                pw.println("<p>Question: " + question + "</p>");
                pw.println("<p>Answer: " + answer + "</p>");
                pw.println("<p>About: " + about + "</p>");
                pw.println("<p>Feedback: " + feedback + "</p>");
                pw.println("<p>Rating: " + rating + "</p>");

                // Check if it is your personal resume
                if (name.equals("Shreyas Kulat") && email.equals("shreyaskulat@gmail.com")) {
                    // Add link to offline resume
                    pw.println("<p><a href='https://drive.google.com/file/d/1JRm7YMFJsfk98hviv-pcZjPKhnfQGjI5/view?usp=drive_link'>Download Resume</a></p>");
                }

                pw.println("</body>");
                pw.println("</html>");
            } else {
                // If no matching resume is found, redirect to Google
                resp.sendRedirect("https://www.google.com/");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
