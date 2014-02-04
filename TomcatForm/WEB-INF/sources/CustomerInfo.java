
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CustomerInfo extends HttpServlet
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
		HttpSession session = request.getSession();
		if(session.getAttribute("loggedIn") == null || session.getAttribute("loggedIn") == "false")
		{
			response.sendRedirect("/TomcatForm/index.html");
		}

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

		  
				
				Map<String, Integer> cart= (HashMap<String, Integer>)session.getAttribute("cart");
			  
			  String firstName = request.getParameter("customerFirstName");
			  String lastName = request.getParameter("customerLastName");
			  String creditCard = request.getParameter("creditCard");
			  String expiration = request.getParameter("expiration");
			   //out.println("STU" + firstName + lastName + creditCard + expiration);
			  
			  if(request.getAttribute("error") != null)
				out.println(request.getAttribute("error"));
			 
			  if(firstName.isEmpty() || lastName.isEmpty() || creditCard.isEmpty() || expiration.isEmpty())
			  {
				out.println("errrrorrrrrr");
				request.setAttribute("error","<span id='error-msg'>Invalid Information</span>");
				request.getRequestDispatcher("/CustomerInfo.html").include(request, response);
				}
				
			  
				
			if(firstName != null && lastName != null && creditCard != null && expiration != null)
			{
			
			
				//request.getRequestDispatcher("/CustomerInfo.html").include(request, response);	
			  String query = "select * from creditcards where id='"+creditCard+"' AND first_name='"+firstName+"' AND last_name='"+lastName+"' AND expiration='"+expiration+"'";
			  ResultSet rs = statement.executeQuery(query);
			  out.println(query);
			  
			  
				
			  if(!rs.isBeforeFirst())
			  {
			  session.setAttribute("error","<span id='error-msg'>Invalid Information</span>");
				request.getRequestDispatcher("/CustomerInfo.html").forward(request, response);
				
			}
			else
			{
			
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				String d = "\""+df.format(cal.getTime())+"\"";
				PreparedStatement ps = null;
				for(String s: cart.keySet())
				{
					out.println("KEY: "+s);
					String querry = ("Insert into sales(customer_id, movie_id, sale_date) values(" + session.getAttribute("customerID") + ", " + s + ", " + d + " )");
					out.println("insert query: "+querry);
					ps = dbcon.prepareStatement(querry);
					ps.executeUpdate();
					out.println("Key: "+s+" is ADDED");
					
				}
	
				Map<String, Integer> cartClear = new HashMap<String, Integer>();
					session.setAttribute("cart", cartClear);
					
				ps.close();
				request.getRequestDispatcher("/CheckoutConfirmation.html").forward(request, response);
			}
			
			
			  

		
              rs.close();
              statement.close();
              dbcon.close();
			 }
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