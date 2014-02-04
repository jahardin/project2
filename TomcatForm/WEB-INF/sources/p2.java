
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class p2 extends HttpServlet
{

	
	
    public String getServletInfo()
    {
       return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET

	
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
		//String loggedIN = request.getParameter("login").toString();
		PrintWriter out = response.getWriter();
        String loginUser = "root";
        String loginPasswd = "G1veupiwin";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
		if(request.getAttribute("login") == null)
		{
			//response.sendRedirect("/TomcatForm/index.html");
			out.println("yayaya null");
		}

        out.println("<HTML><HEAD><TITLE>p2</TITLE><link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'><link rel='text/javascript' href='/TomcatForm/WEB-INF/myScript.js'><link rel='text/javascript' href='//code.jquery.com/jquery-1.10.2.min.js'></HEAD>");
		out.println(" <div class='header'><h1>FAPflix </h1><p><a href='/TomcatForm/search.html'>Search</a> <span class='white'>|</span>  <a href='/TomcatForm'>Sign Out</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/Checkout'>My Cart</a> <span class='white'>|</span>  <a href='/TomcatForm/index.html'>Home</a> <span class='white'>|</span>  <a href='/TomcatForm/index.html'>Logout</a></p></div>");
        out.println("<BODY><H1>p2</H1>");
		out.println(request.getAttribute("login"));


        try
           {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();

			String lastName = request.getParameter("lastName");
		  
              String query = "SELECT * from customers where last_name = '" + lastName + "'";

              // Perform the query
              ResultSet rs = statement.executeQuery(query);

              out.println("<TABLE border>");

              // Iterate through each row of rs
              while (rs.next())
              {
                  String m_ID = rs.getString("ID");
                  String m_FN = rs.getString("first_name");
                  String m_LN = rs.getString("last_name");
                  out.println("<tr>" +
                              "<td>" + m_ID + "</td>" +
                              "<td>" + m_FN + "</td>" +
                              "<td>" + m_LN + "</td>" +
                              "</tr>");
              }

              out.println("</TABLE>");

              rs.close();
              statement.close();
              dbcon.close();
            }
        catch (SQLException ex) {
              while (ex != null) {
                    System.out.println ("SQL Exception:  " + ex.getMessage ());
                    ex = ex.getNextException ();
                }  // end while
            }  // end catch SQLException

        catch(java.lang.Exception ex)
            {
                out.println("<HTML>" +
                            "<HEAD><TITLE>" +
                            "MovieDB: Error" +
                            "</TITLE></HEAD>\n<BODY>" +
                            "<P>SQL error in doGet: " +
                            ex.getMessage() + "</P></BODY></HTML>");
                return;
            }
         out.close();
    }
	

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
	doGet(request, response);
    }
	
	public void search(HttpServletResponse response)
	throws IOException, ServletException
	{
		PrintWriter out = response.getWriter();
		out.println("SEARCH WORKS");
	}
}