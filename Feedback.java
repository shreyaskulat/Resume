package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/submit_feedback")
public class Feedback extends HttpServlet {
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

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String question = req.getParameter("question");
        String answer = req.getParameter("answer");
        String feedback = req.getParameter("feedback");
        String about = req.getParameter("about");
        int rating = 0; // default value if parameter is null

        String ratingParam = req.getParameter("rating");
        if (ratingParam != null && !ratingParam.isEmpty()) {
            rating = Integer.parseInt(ratingParam);
        }

        // Save the form data to the database
        saveFeedbackToDatabase(name, email, question, answer, feedback, about, rating);

        // Display a success message
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.println("<html><body>");
        pw.println("<h2>Thank you for your feedback!</h2>");
        pw.println("</body></html>");
    }

    private void saveFeedbackToDatabase(String name, String email, String question, String answer, String feedback,
            String about, int rating) {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO feedback (name, email, question, answer, feedback, about, rating) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, question);
            pstmt.setString(4, answer);
            pstmt.setString(5, feedback);
            pstmt.setString(6, about);
            pstmt.setInt(7, rating);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
