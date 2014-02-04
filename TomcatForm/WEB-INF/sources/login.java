
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;

public class login extends HttpServlet
{

	private RequestDispatcher browseJsp;
	
	
    public String getServletInfo()
    {
       return "Servlet connects to MySQL database and displays result of a SELECT";
    }
	
	public void init(ServletConfig config) throws ServletException {
               ServletContext context = config.getServletContext();
               browseJsp = context.getRequestDispatcher("/WEB-INF/jsp/main.jsp");
			 
       }

    // Use http GET

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "root";
        String loginPasswd = "G1veupiwin";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        out.println("<HTML><HEAD><TITLE>MovieDB</TITLE><link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'><link rel='text/javascript' href='/TomcatForm/WEB-INF/myScript.js'><link rel='text/javascript'href='//code.jquery.com/jquery-1.10.2.min.js'></HEAD>");


        try
           {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();

			String email = request.getParameter("email");
			String password = request.getParameter("password");
		  
              String query = "SELECT id, email, password from customers where email = '" + email + "' AND password='" + password + "'";
				HttpSession session = request.getSession();

              // Perform the query
              ResultSet rs = statement.executeQuery(query);
				//request.getRequestDispatcher("/WEB-INF/jsp/browse.jsp").forward(request, response);
			  

			  if(session.getAttribute("error") != null)
				out.println(session.getAttribute("error"));
			  
			  
			  
			  if(rs.next())
			  {		
				
				request.setAttribute("email", email);
				request.setAttribute("password", password);
				session.setAttribute("customerID", rs.getString("id"));
				ResultSet cartCheck = statement.executeQuery("select movieID, quantity from cart where customerID='"+session.getAttribute("customerID")+"'");
				
				Map<String, Integer> cart = new HashMap<String, Integer>();
				
				while(cartCheck.next())
				{
					cart.put(cartCheck.getString("movieID"), Integer.parseInt(cartCheck.getString("quantity")));
				}
				session.setAttribute("cart", cart);
				cartCheck.close();
				session.setAttribute("loggedIn", "true");
				browseJsp.forward(request, response);
			  }
			  else
			  {
				request.setAttribute("error", "<span class='error-msg'>wrong username/password</span>");
				request.getRequestDispatcher("/index.html").include(request, response);
			  }
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

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
	doPost(request, response);
    }
}