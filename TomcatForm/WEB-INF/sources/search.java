
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class search extends HttpServlet
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
		
		if(session.getAttribute("loggedIn") == null || session.getAttribute("loggedIn") == "false")
		{
			response.sendRedirect("/TomcatForm/index.html");
		}

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        out.println("<HTML><HEAD><TITLE>Search</TITLE><link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'><link rel='text/javascript' href='/TomcatForm/WEB-INF/myScript.js'><link rel='text/javascript'href='//code.jquery.com/jquery-1.10.2.min.js'></HEAD>");
		out.println(" <div class='header'><h1>FAPflix </h1><p><a href='/TomcatForm/search.html'>Search</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/logout'>Sign Out</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/Checkout'>My Cart</a> <span class='white'>|</span>  <a href='/TomcatForm/main'>Home</a> <span class='white'>|</span>  </p></div>");
        out.println("<BODY><H1>Search</H1>");
		HttpSession session = request.getSession();


        try
           {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();

			String year = request.getParameter("year");
			String title = request.getParameter("title");
			String director = request.getParameter("director");
			String name = request.getParameter("name");
			String qyear="", qtitle="", qname="", qdirector="";
			
		  
		  
            String query = "select m.id, m.title, m.director, m.year, s.first_name, s.last_name from movies as m JOIN stars as s JOIN stars_in_movies as sm where sm.star_id = s.id AND sm.movie_id = m.id ";
			
			String parameters = "";
			if(!year.isEmpty())
			{
					qyear = "AND m.year='" + year + "'";
					parameters += "year=" + year;
			}
			
			if(!title.isEmpty())
			{
					qtitle = "AND m.title like '%" + title + "%'";
			}
			
			if(!director.isEmpty())
			{
					qdirector = "AND m.director like '%" + director + "%'";
			}
			if(!name.isEmpty())
			{
					qname = "AND (s.first_name like '%" + name + "%' OR s.last_name like '%" + name + "%')";
			}
			
			session.setAttribute("title", title);
			session.setAttribute("year", year);
			session.setAttribute("director", director);
			session.setAttribute("star", name);
	
		
			
			query+= qyear + qtitle + qdirector + qname;
			
			
				
              // Perform the query stars, movies, stars_in_movies
              ResultSet rs = statement.executeQuery(query);
			  
			  
			  request.getRequestDispatcher("/servlet/showMovies?"+parameters +"&offset=0&limit=10").forward(request, response);
			  
			  ResultSetMetaData rsmd = rs.getMetaData();
			  out.println(query);
              out.println("<TABLE border>");
			  out.println("<tr>");
			  for(int i =1; i <= rsmd.getColumnCount(); i++)
				out.println("<th>" + rsmd.getColumnName(i) + "</th>");
			  out.println("</tr>");

              // Iterate through each row of rs
			  
              while (rs.next())
              {
                  //String m_ID = rs.getString("id");
                  //String movieTitle = rs.getString("title");
				  //String m_year
				  out.println("<tr>");
				  for(int i = 1; i <= rsmd.getColumnCount(); i++)
					out.println("<td>" + rs.getString(rsmd.getColumnName(i)) + "</td>");
				  out.println("</tr>");
              }

              out.println("</TABLE>");
				request.getRequestDispatcher("/servlet/showMovies?"+parameters).forward(request, response);
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