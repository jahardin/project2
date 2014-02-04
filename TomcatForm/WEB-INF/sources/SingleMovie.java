
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SingleMovie extends HttpServlet
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
		HttpSession session = request.getSession();
		if(session.getAttribute("loggedIn") == null || session.getAttribute("loggedIn") == "false")
		{
			response.sendRedirect("/TomcatForm/index.html");
		}

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        out.println("<HTML><HEAD><TITLE>Search</TITLE><link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'><link rel='text/javascript' href='/TomcatForm/WEB-INF/myScript.js'><link rel='text/javascript'href='//code.jquery.com/jquery-1.10.2.min.js'></HEAD>");
		out.println(" <div class='header'><h1>FAPflix </h1><p><a href='/TomcatForm/search.html'>Search</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/logout'>Sign Out</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/Checkout'>My Cart</a> <span class='white'>|</span>  <a href='/TomcatForm/main'>Home</a> <span class='white'>|</span>  </p></div>");
        out.println("<BODY><H1>Single Movie</H1>");
		


        try
           {
		   out.println("beginning");
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();

			
			
			Map<String, Integer> cart = (HashMap<String, Integer>)session.getAttribute("cart");
            String query = "select m.id, m.title, m.year, g.name, s.first_name, s.last_name, m.banner_url, m.director from genres as g, genres_in_movies as gm, movies as m, stars as s, stars_in_movies as sm where m.id=gm.movie_id and gm.genre_id=g.id and s.id=sm.star_id and sm.movie_id=m.id";
			
			 String movieToAdd = request.getParameter("addId");
			out.println("Movie to add: "+movieToAdd);
			if(movieToAdd != null)
			{
				if(!cart.containsKey(movieToAdd))
					cart.put(movieToAdd, 1);
				else
					cart.put(movieToAdd, (Integer)cart.get(movieToAdd)+1);
				
				for(String k : cart.keySet())
				out.println("In Cart: " + k + " " + cart.get(k));
				
				session.setAttribute("cart", cart);
			}
			
			String qtitle = "";
			if(!request.getParameter("title").isEmpty())
			{
					qtitle = " AND m.title='" + request.getParameter("title") + "'";
			}
			query+= qtitle;
			
              // Perform the query stars, movies, stars_in_movies
              ResultSet rs = statement.executeQuery(query);
			  //out.println(query);
			  
			 ArrayList<SMovie> mList = new ArrayList<SMovie>();
				
				String previousMovieID = "";
				int numMovies = 0;
			  while(rs.next()) {
			  //out.println("entered while loop");
			  
			  //different movie
			  String id = (""+rs.getInt("id")+"");
			 // out.println("ID: " + id);
			  //out.println("Prviousmovieid"+previousMovieID);
			  //if(!id.equals(previousMovieID))
				//out.println("nonesense");
			  if(!id.equals(previousMovieID))
			  {
			  //out.println("entered if");
				//String title, String genre, String id, String banner_url,String starName, String directorName, String year
				String starName = rs.getString("first_name") + " " + rs.getString("last_name");
				//out.println("StarName: "+starName);
				//out.println("test1");
				
				String title = rs.getString("title");
				//out.println(title);
				String genre = rs.getString("name");
				//out.println(genre);
				String banner = rs.getString("banner_url");
				//out.println(banner);
				String director = rs.getString("director");
				//out.println(director);
				String year = ""+rs.getInt("year") +"";
				//out.println(year);
				//out.print("\ndifferent movie, Star: " + starName + " title: " + title + " genre: " + genre + " banner: " + banner + " direcotr: " + director + "year: "+ year);
				
				mList.add(new SMovie(title, genre , id, banner ,starName, director , year));
				numMovies++;
			  }
			  else//same movie
			  {
				String starName = rs.getString("first_name") + " " + rs.getString("last_name");
				String genre = rs.getString("name");
				//out.println("\nsame movie,star: " + starName + " genre: " + genre);
				
				//out.println("MMMMMMMMM" + numMovies);
				mList.get(numMovies - 1).addStar(starName);
				mList.get(numMovies - 1).addGenre(genre);
			  
			  
			  }
			 
				previousMovieID = ""+rs.getInt("id")+"";
				/*out.println("<tr><td>" + rs.getString("m.title") + "</td>");
				out.println("<td>" + rs.getString("m.year") + "</td>");
				out.println("<td>" + rs.getString("g.name") + "</td>");
				out.println("<td>" + rs.getString("s.first_name") + "</td>");
				out.println("<td>" + rs.getString("s.last_name") + "</td></tr>");*/
			  }
			  // out.println("outside everything now");
			  
			  for(SMovie m : mList)
			  {
					out.println("<div class='movie-elem'>");
					out.println("<a href='/TomcatForm/servlet/singleMovie?title="+ m.getTitle() +"'><img src='"+m.getBanner_url()+"' /></a>");
					
					
					out.println("<a class='title' href='/TomcatForm/servlet/singleMovie?title="+ m.getTitle() +"'>Title: "+m.getTitle()+"<br/></a>");
					out.println("<span>Year: "+m.getYear()+"<br/></span>");
					out.println("<span>Director: "+m.getDirectorName()+"<br/></span>");
					
					
					
					
					
					out.println("Stars: <span>");
					for(String s : m.getStars())
						out.println("<a href='/TomcatForm/servlet/singleStar?starName="+s+"'>"+s+"</a>" + '|' );
						
						out.println("<br/></span>");
						
					out.println("Genres: <span>");
					for(String g : m.getGenres())
						out.print("<a href='/TomcatForm/servlet/showMovies?genre="+g+"&offset=0&limit=10'>"+g+"</a>"+" | ");
						out.println("<span>Price: "+m.getPrice()+"<br/></span>");
						out.println("<p>&nbsp;<br/></p>");
						//out.println("<button class='addToCart' href='/TomcatForm/servlet/singleMovie?addId="+m.getId()+"&title="+ m.getTitle()+"' method='POST' value='Add to Cart'>");
						
						
						out.println("<FORM ACTION='/TomcatForm/servlet/singleMovie?" +"addId="+m.getId()+"&title="+ m.getTitle()+"' METHOD='POST'><INPUT TYPE='SUBMIT' VALUE='Add To Cart'></FORM>");
						
						out.println("<span></div>"); 
						
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
	
	private class SMovie 
{

	String title,  id, banner_url,  directorName, year;
	ArrayList<String> stars;
	ArrayList<String> genres;
	String price;

	
	

	public SMovie(String title, String genre, String id, String banner_url,
			String starName, String directorName, String year) {
		
		stars = new ArrayList<String>();
		genres = new ArrayList<String>();
		this.title = title;
		genres.add(genre);
		this.id = id;
		this.banner_url = banner_url;
		stars.add(starName);
		this.directorName = directorName;
		this.year = year;
		price = "$15.99";
	}

	public String getPrice()
	{
		return price;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<String> getGenres() {
		return genres;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBanner_url() {
		return banner_url;
	}

	public void setBanner_url(String banner_url) {
		this.banner_url = banner_url;
	}

	public ArrayList<String> getStars() {
		return stars;
	}


	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	public void addStar(String n)
	{
		if(!stars.contains(n))
			stars.add( n);
	}
	
	
	public void addGenre(String genre)
	{
		if(!genres.contains(genre))
			genres.add(genre);
		
	}



}
	
	
	
}