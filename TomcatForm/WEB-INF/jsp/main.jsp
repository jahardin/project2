<!--VARIABLE IMPORTS FROM SERVLETS-->
<jsp:useBean id="email" scope="request" class="java.lang.String" />
<jsp:useBean id="password" scope="request" class="java.lang.String" />
<!--END VARIABLE IMPORTS-->

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="javax.servlet.RequestDispatcher" %>

   <%


		String loginUser = "root";
        String loginPasswd = "G1veupiwin";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		Class.forName("com.mysql.jdbc.Driver").newInstance();
        response.setContentType("text/html");    // Response mime type
		Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
		out.println("before check" + session.getAttribute("loggedIn"));
		if(session.getAttribute("loggedIn") == null || session.getAttribute("loggedIn") == "false")
		{
			response.sendRedirect("/TomcatForm/index.html");
		}
				Statement statement = dbcon.createStatement();
		ResultSet rs = statement.executeQuery("Select name from genres order by name asc");
	%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
   <head>
      <title>Browse</title>
	  <link rel='stylesheet' href='/TomcatForm/stylesheet.css' type='text/css' media='all'>
	  <link rel='text/javascript' href='/myScript.js'><link rel='text/javascript'href='//code.jquery.com/jquery-1.10.2.min.js'>
   </head>
   <body>
    <div class="header">
	<h1>FAPflix </h1>
		<p><a href="/TomcatForm/search.html">Search</a> <span class="white">|</span>  <a href="/TomcatForm/servlet/logout">Sign Out</a> <span class="white">|</span>  <a href="/TomcatForm/servlet/Checkout">My Cart</a> <span class="white">|</span>  <a href="/TomcatForm/main">Home</a> </p>
        
    
    
    
    </div>
    <div class="main-body">
    <h1>Featured Movie</h1>
<div class='movie-elem'>
<a href='/TomcatForm/servlet/singleMovie?title=Raiders Of The Lost Ark'><img src='http://www.markheadrick.com/dvd/images/IndianaJones-RaidersOfTheLostArk.jpg' /></a>
<a class='title' href='/TomcatForm/servlet/singleMovie?title=Raiders Of The Lost Ark'>Title: Raiders Of The Lost Ark<br/></a>
<span>Year: 1981<br/></span>
<span>Director: Steven Spielberg<br/></span>
Stars: <span>
<a href='/TomcatForm/servlet/singleStar?starName=Harrison Ford'>Harrison Ford</a>|
<br/></span>
Genres: <span>
Action | Adventure | <span>Price: $15.99<br/></span>
<p>&nbsp;<br/></p>
<FORM ACTION='/TomcatForm/servlet/singleMovie?addId=755007&title=Raiders Of The Lost Ark' METHOD='POST'><INPUT TYPE='SUBMIT' VALUE='Add To Cart'></FORM>
<span></div>


			
			
	</div>
    
    
    </div>
    <div class="footer">
 
		
<%	
		out.println("<table id='table1'>");
		out.println("<tbody>");
		out.println("<tr><th colspan=5 class='white'>Browse By Genre</th></tr>");
		int counter = 1; 
		out.println("<tr>");
		while(rs.next())
		{
		out.println("<td><a href='/TomcatForm/servlet/showMovies?genre=" + rs.getString("name") + "&offset=0&limit=10'>" + rs.getString("name") + "</a></td>");
			if(counter > 4)
			{
				counter = 0; 
				out.println("</tr>");
				out.println("<tr>");
			}
			
			counter++;
		}
		out.println("</tr>");
		out.println("</tbody>");
		out.println("</table>");
		
		
		out.println("<table id='table2'>");
		out.println("<tbody>");
		int counter2 = 1; 
		int counter3 = 1; 
		out.println("<tr><th colspan=3 class='white'>Browse By Title</th></tr>");
		out.println("<tr>");
		for(int i = 0; i < 10; i++)
		{
		
		out.println("<td><a href='/TomcatForm/servlet/showMovies?title=" + i + "&order=title asc&offset=0&limit=10'>" + i + "</a></td>");
			if(counter3 > 3)
			{
				counter3 = 0; 
				out.println("</tr>");
				out.println("<tr>");
			}
			
			counter3++;
		
		}
		for(int i = 65; i <= 90; i++)
		{
		out.println("<td><a href='/TomcatForm/servlet/showMovies?title=" + (char)i + "&order=title asc&offset=0&limit=10'>" + (char)i + "</a></td>");
			if(counter2 > 3)
			{
				counter2 = 0; 
				out.println("</tr>");
				out.println("<tr>");
			}
			
			counter2++;
		}
		out.println("</tr>");
		out.println("</tbody>");
		out.println("</table>");
%>
	</div>
    
    

   </body>
</html>


<!--END HTML-->
<!--BEGIN STYLESHEET-->





<!--END STYLESHEET-->
<!--BEGIN JAVA CODE-->
<%
		rs.close();
        statement.close();
        dbcon.close();
%>

