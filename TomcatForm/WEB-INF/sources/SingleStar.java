
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SingleStar extends HttpServlet
{
    public String getServletInfo()
    {
       return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    // Use http GET
	


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String loginUser = "root";
        String loginPasswd = "G1veupiwin";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        out.println("<HTML><HEAD><TITLE>Search</TITLE><link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'><link rel='text/javascript' href='/TomcatForm/WEB-INF/myScript.js'><link rel='text/javascript'href='//code.jquery.com/jquery-1.10.2.min.js'></HEAD>");
		out.println(" <div class='header'><h1>FAPflix </h1><p><a href='/TomcatForm/search.html'>Search</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/logout'>Sign Out</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/Checkout'>My Cart</a> <span class='white'>|</span>  <a href='/TomcatForm/main'>Home</a> <span class='white'>|</span>  </p></div>");
        out.println("<BODY><H1>Single Star</H1>");
		HttpSession session = request.getSession();
				if(session.getAttribute("loggedIn") == null || session.getAttribute("loggedIn") == "false")
		{
			response.sendRedirect("/TomcatForm/index.html");
		}


        try
           {
		   out.println("beginning");
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();

			
			
			Map<String, Integer> cart = (HashMap<String, Integer>)session.getAttribute("cart");
            String query = "select s.id, s.first_name, s.last_name, s.dob, s.photo_url, m.title from stars as s join movies as m join stars_in_movies as sm where sm.star_id=s.id AND sm.movie_id=m.id";

			
			String query2 = "";
			String starID = "";
			if(!request.getParameter("starName").isEmpty())
			{
			out.println("entered first if");
					StringTokenizer st = new StringTokenizer((String)request.getParameter("starName"));
							String fn = st.nextToken();
							out.println("First Name: "+fn);
							String ln = "";
							boolean moreTokens = false;
							while(st.hasMoreTokens())
							{
									moreTokens = true;
									ln = st.nextToken();
									out.println("Last Name: "+ln);
							}
							if(moreTokens)
								query2 = "select id from stars where first_name like '%" + fn + "%' AND last_name like '%" + ln + "%'";
							else
								query2 = "select id from stars first_name like '%" + fn + "%' OR last_name like '%" + fn + "%'";
								
					out.println("query2: "+query2);
					ResultSet rs2 = statement.executeQuery(query2);
					if(!rs2.isBeforeFirst())
						out.println("star ID from rs2"+rs2.getString("id"));
					else
					{
						while(rs2.next()) //only runs for one star
						{
							starID = rs2.getString("id");
						}
						
					}
						
					
						
					query += " AND s.id='"+starID+"' group by title";
					out.println("query"+query);
					rs2.close();
			}
			
			out.println("out of if");
			
              ResultSet rs = statement.executeQuery(query);
			  out.println("Query: "+query);
			  
			  String nameOfStar ="", dob="", photo_url="";
			  ArrayList<String> titles = new ArrayList<String>();
			  while(rs.next())
			  {
				nameOfStar = rs.getString("first_name") + " " + rs.getString("last_name");
				dob = (String)rs.getString("dob");
				photo_url = rs.getString("photo_url");
				titles.add(rs.getString("title"));
			  }
			  
			 out.println("<div class='movie-elem'>");
			out.println("<a><img src='"+photo_url+"' /></a>");
			out.println("<span>Date of Birth: "+dob+"<br/></span>");
			out.println("<span>Star: "+nameOfStar+" <span>");
			for(String s : titles)
			{
				out.println("<br/><a class='title' href='/TomcatForm/servlet/singleMovie?title="+s+"'>Title: "+s+"</a>");
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

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
	doGet(request, response);
    }
	
}