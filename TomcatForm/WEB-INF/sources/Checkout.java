
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;

public class Checkout extends HttpServlet
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

        out.println("<HTML><HEAD><TITLE>MovieDB</TITLE><link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'><link rel='text/javascript' href='/TomcatForm/WEB-INF/myScript.js'><link rel='text/javascript' href='//code.jquery.com/jquery-1.10.2.min.js'></HEAD>");
		out.println(" <div class='header'><h1>FAPflix </h1><p><a href='/TomcatForm/search.html'>Search</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/logout'>Sign Out</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/Checkout'>My Cart</a> <span class='white'>|</span>  <a href='/TomcatForm/main'>Home</a> <span class='white'>|</span></p></div>");


        try
           {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();
				
				
				
				Map<String, Integer> cart = (Map<String, Integer>)session.getAttribute("cart");
				
				if(request.getParameter("remove") != null && request.getParameter("remove").equals("1"))
					{
						cart.remove((String)request.getParameter("movieID"));
						out.println("Id has been removed");
					}
				if(request.getParameter("inc") != null && request.getParameter("inc").equals("1"))
					{
						cart.put(request.getParameter("movieID") ,cart.get((String)request.getParameter("movieID")) + 1);
						out.println("Id has been removed");
					}
				if(request.getParameter("dec") != null && request.getParameter("dec").equals("1"))
					{
						cart.put(request.getParameter("movieID") ,cart.get((String)request.getParameter("movieID")) - 1);
						if(cart.get(request.getParameter("movieID")) == 0)
							cart.remove((String)request.getParameter("movieID"));
					}
				
				double price = 0;
				for(String s: cart.keySet())
				{
					price += cart.get(s) * 15.99; //hardcoded price
					ResultSet rs = statement.executeQuery("select * from movies where id='" + s + "'");
					while(rs.next())
					{
						out.println("<div class='checkout-elem'>");
						out.println("<img class='checkout-img' src='"+rs.getString("banner_url") + "'/>");
						
						out.println("<p>Title: " +rs.getString("title")+"</p>");
						out.println("<p>Quantity: " + cart.get(s)+"</p>");
						out.println("<p>Price Per Movie: $15.99"+"</p>"); // hardcoded price
						out.println("<form action='/TomcatForm/servlet/Checkout?remove=1&movieID="+s+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Remove All'></form>");
						out.println("<form action='/TomcatForm/servlet/Checkout?inc=1&movieID="+s+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='INC'></form>");
						out.println("<form action='/TomcatForm/servlet/Checkout?dec=1&movieID="+s+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='DEC'></form>");
						out.println("</div>");
					}
					rs.close();
				}
				
				
				
				out.println("<div class='checkout-total'>Total: " + price+"</div>");
				out.println("<FORM class='proceedToCheckout' action='/TomcatForm/CustomerInfo.html' method='post'>");
				out.println("<INPUT id='' TYPE='SUBMIT' VALUE='Proceed To Checkout'></form>");
				
					
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