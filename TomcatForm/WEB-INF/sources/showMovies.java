
/* A servlet to display the contents of the MySQL movieDB database */

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;

public class showMovies extends HttpServlet
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

        out.println("<HTML><HEAD><TITLE>MovieDB</TITLE><link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'><script rel='text/javascript' src='/TomcatForm/myScript.js'></script><script rel='text/javascript' src='//code.jquery.com/jquery-1.10.2.min.js'></script></HEAD>");
		
		out.println(" <div class='header'><h1>FAPflix </h1><p><a href='/TomcatForm/search.html'>Search</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/logout'>Sign Out</a> <span class='white'>|</span>  <a href='/TomcatForm/servlet/Checkout'>My Cart</a> <span class='white'>|</span>  <a href='/TomcatForm/main'>Home</a> <span class='white'>|</span></p></div>");
			
			
			
			Map<String, Integer> cart = (Map<String, Integer>)session.getAttribute("cart");
			
			//out.println("session year:" + session.getAttribute("year"));
			
			
			//String sentYear = (String)request.getAttribute("year");
			//out.println("yearSaved " + sentYear);
        try
           {
              //Class.forName("org.gjt.mm.mysql.Driver");
              Class.forName("com.mysql.jdbc.Driver").newInstance();

              Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
              // Declare our statement
              Statement statement = dbcon.createStatement();
			
			String url = "<FORM ACTION='/TomcatForm/servlet/showMovies?";
			
			if(request.getParameter("genre") != null)//browsing
			 url += "genre=" +request.getParameter("genre") /*+ "&order="*/;
			 else if(request.getParameter("title") != null)
			 url += "title=" +request.getParameter("title")/* + "&order="*/;
			 else if(request.getParameter("addId") != null)
			 url += "addId="+request.getParameter("addId")/*+ "&order="*/;
			 else if(request.getParameter("starName") != null)
			 url += "starName="+request.getParameter("starName")/*+ "&order="*/;
			/* else//search
			 {
			
				url +=  "order=";
			 }*/
			String movieToAdd = request.getParameter("addId");
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
			
			String o = "";
			String oParam ="";
			if(request.getParameter("order") != null)
			{
				o = " order by " + request.getParameter("order");
				oParam = "&order=" + request.getParameter("order");
			}
			
			out.println("<div id='nav-buttons'>");
			out.print(url +"&order=title asc&offset="+request.getParameter("offset")+ "&limit="+request.getParameter("limit")+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Title Asc'></form>");
			
			out.print(url +"&order=title desc&offset="+request.getParameter("offset")+ "&limit="+request.getParameter("limit")+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Title Desc'></form>");
			
			out.print(url +"&order=year desc&offset="+request.getParameter("offset")+ "&limit="+request.getParameter("limit")+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Year Desc'></form>");
			
			out.print(url + "&order=year asc&offset="+request.getParameter("offset")+ "&limit="+request.getParameter("limit")+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Year Asc'></form>");
			
			out.print(url +oParam+ "&limit=10&offset="+request.getParameter("offset")+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Display 10'></form>");
			
			out.print(url +oParam+ "&limit=50&offset="+request.getParameter("offset")+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Display 50'></form>");
			
			out.print(url + oParam+ "&limit=100&offset="+request.getParameter("offset")+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Display 100'></form>");
			out.println("</div>");
			
			
			
			
			String query = "select m.id, m.title, m.year, g.name, s.first_name, s.last_name, m.banner_url, m.director from genres as g, genres_in_movies as gm, movies as m, stars as s, stars_in_movies as sm where m.id=gm.movie_id and gm.genre_id=g.id and s.id=sm.star_id and sm.movie_id=m.id";
			//out.println(query);
			//if is browse don't do else. If is search, do else
			out.println(session.getAttribute("year"));
			out.println("TESTESTTEST");
			out.println("genre"+request.getParameter("genre"));
			out.println("title"+request.getParameter("title"));
			if(request.getParameter("genre") != null)
			query += " AND g.name='" + request.getParameter("genre")+ "'";
				//query += " AND g.name='" + request.getParameter("genre") + "'";
			else if(!request.getParameter("title").equals("") && request.getParameter("title") != null ) 
			{
				out.println("Parameter title : " +request.getParameter("title"));
				query += " AND m.title like '" + request.getParameter("title")+ "%'";
				out.println("query when genre just added: " + query);
				}
			else if(request.getParameter("starName") != null ) 
				query += " AND s.first_name like '" + request.getParameter("starName")+ "%' OR s.last_name like '" + request.getParameter("starName")+ "%' ";
			else
				{
				out.println("entered else");
					if(!session.getAttribute("title").equals(""))
						query += " AND m.title like '%" + session.getAttribute("title") +"%'";
					if(!session.getAttribute("year").equals(""))
					{
						out.println("entered year block");
						query += " AND m.year='" + session.getAttribute("year") + "'";
						}
					if(!session.getAttribute("director").equals(""))
						query += " AND m.director like '%" + session.getAttribute("director") + "%'";
					if(!session.getAttribute("star").equals(""))
					{
							StringTokenizer st = new StringTokenizer((String)session.getAttribute("star"));
							String fn = st.nextToken();
							String ln = "";
							boolean moreTokens = false;
							while(st.hasMoreTokens())
							{
									moreTokens = true;
									ln = st.nextToken();
							}
							if(moreTokens)
								query += " AND s.first_name like '%" + fn + "%' AND s.last_name like '%" + ln + "%'";
							else
								query += " AND s.first_name like '%" + fn + "%' OR s.last_name like '%" + fn + "%'";
					}
				}
				
				
				
			
			
			
			
			query += o;
			out.println("the o thingy: " + o);
				out.println("fully constructed query"+query);

			
              // Perform the query
              ResultSet rs = statement.executeQuery(query);
			  
			  //out.println(query);
			  out.println("<table><tbody>");
			  
			  
			  if(!rs.isBeforeFirst())
				out.println("<p class='no-results'>Your Search Did Not Return Any Results</p>");
				
				
				
				//populating Movie list
				ArrayList<Movie> mList = new ArrayList<Movie>();
				
				String previousMovieID = "";
				ArrayList<String> previousMovies = new ArrayList<String>();
				int numMovies = 0;
			  while(rs.next()) {
			  
			  
			  //different movie
			  String id = (""+rs.getInt("id")+"");
			  //out.println("ID: " + id);
			  //out.println("Comparing " + id + " and " + previousMovieID);
			  if(!previousMovies.contains(id))
			  {
				//String title, String genre, String id, String banner_url,String starName, String directorName, String year
				String starName = rs.getString("first_name") + " " + rs.getString("last_name");
				
				
				String title = rs.getString("title");
				String genre = rs.getString("name");
				String banner = rs.getString("banner_url");
				String director = rs.getString("director");
				String year = ""+rs.getInt("year") +"";
				
				//out.print("\ndifferent movie, Star: " + starName + " title: " + title + " genre: " + genre + " banner: " + banner + " direcotr: " + director + "year: "+ year);
				
				previousMovies.add(""+rs.getInt("id")+"");
				
				mList.add(new Movie(title, genre , id, banner ,starName, director , year));
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
			  
				//previousMovieID = ""+rs.getInt("id")+"";
				/*out.println("<tr><td>" + rs.getString("m.title") + "</td>");
				out.println("<td>" + rs.getString("m.year") + "</td>");
				out.println("<td>" + rs.getString("g.name") + "</td>");
				out.println("<td>" + rs.getString("s.first_name") + "</td>");
				out.println("<td>" + rs.getString("s.last_name") + "</td></tr>");*/
			  }
			  
			  int limitDisplay = numMovies;
			  int offset = 0;
			if(request.getParameter("limit") != null)
				limitDisplay = Integer.parseInt(request.getParameter("limit"));
			if(request.getParameter("offset") != null)
				offset = Integer.parseInt(request.getParameter("offset"));
			  out.println("BEFore for loops");
			  for(int i = offset; i < (limitDisplay + offset); i++)
			  {
			  out.println("entered for loops " + i);
			  out.println("limit: " + limitDisplay + " OFFset: "+offset + "NUMMOVIES: "+numMovies);
					if(i >= numMovies)
						break;
					
					Movie m = mList.get(i);
					out.println("<div class='movie-elem'>");
					out.println("<span>Movie ID: "+m.getId()+"<br/></span>");
					out.println("<a href='/TomcatForm/servlet/singleMovie?title="+ m.getTitle() +"'><img src='"+m.getBanner_url()+"' /></a>");
					
					
					out.println("<a class='title' href='/TomcatForm/servlet/singleMovie?title="+ m.getTitle() +"'>Title: "+m.getTitle()+"<br/></a>");
					out.println("<span>Year: "+m.getYear()+"<br/></span>");
					out.println("<span>Director: "+m.getDirectorName()+"<br/></span>");
					
					
					
					
					
					out.println("Stars: <span>");
					for(String s : m.getStars())
					{
						out.println("<a href='/TomcatForm/servlet/singleStar?starName="+s+"'>"+s+"</a>" + '|' );
						
						out.println("<br/></span>");
					}
						
					out.println("Genres: <span>");
					for(String g : m.getGenres())
					{
						out.print("<a href='/TomcatForm/servlet/showMovies?genre="+g+"&offset=0&limit=10'>"+g+"</a>"+" | ");
					}
						out.println("<span>Price: "+m.getPrice()+"<br/></span>");
						
						out.println("<p>&nbsp;<br/></p>");
						//out.println("<button class='addToCart' href='/TomcatForm/servlet/showMovies?addId="+m.getId()+"&order="+request.getParameter("order")+"' method='POST' value='Add to Cart'>");
						
					
						out.println(url +"&addId="+m.getId()+"' METHOD='POST'><INPUT TYPE='SUBMIT' VALUE='Add To Cart'></FORM>");
					
						out.println("</span></div>"); 
						
			  }
			  out.println("right before if's");
			  if((offset+ limitDisplay) < numMovies)
				out.println(url+ oParam +"&limit="+limitDisplay+"&offset="+(offset + limitDisplay)+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Next'></form>");
				
				if((offset - limitDisplay) >= 0)
					out.println(url+ oParam +"&limit="+limitDisplay+"&offset="+(offset -10)+"' METHOD='Post'><INPUT TYPE='SUBMIT' VALUE='Previous'></form>");
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
				out.println("</tbody></table>");
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



private class Movie 
{

	String title,  id, banner_url,  directorName, year;
	ArrayList<String> stars;
	ArrayList<String> genres;
	String price;

	
	

	public Movie(String title, String genre, String id, String banner_url,
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