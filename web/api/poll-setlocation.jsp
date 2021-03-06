<%--
    Document   : poll-setlocation
    Created on : 12/08/2011, 7:13:37 PM
    Author     : Darren
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="java.text.*"%>
<%@page import="java.io.*"%>
<jsp:useBean id="db" scope="session" class="db.database" /> 

<%
try{
    if(db.accessCheck("master") == 1) {
	    out.println("{");
	    out.println("\"access\": \"OK\"");
	    out.println("},");
	    String dataID = request.getParameter("id"); //ID of the above type
	    String location = request.getParameter("location"); //The number generated by Google Maps
            String country = request.getParameter("country"); 
            String state = request.getParameter("state"); 
            String city = request.getParameter("city"); 
            String suburb = request.getParameter("suburb"); 
            String street = request.getParameter("street"); 
            String unit = request.getParameter("unit"); 
            //String[] list_add_com = {country, state, city, suburb, street, unit};// a list of all component for address.
	    if (location.equals(null)) {
		    out.println("{");
		    out.println("\"error\": " + "\"undefined coords\"");
		    out.println("}");
	    } else {
		    String query = "UPDATE Polls SET location=?, country=?, state=?, city=?, suburb=?, street=?, unitNumber=? WHERE pollID=?";
		    String[] values = {location, country, state, city, suburb, street, unit, dataID};
		    String[] types = {"string", "string", "string", "string", "string", "string", "string", "int"};
		    String status = db.doPreparedExecute(query, values, types);
		    out.println("{");
		    out.println("\"status\": " + "\"Location Changed.\"");
		    out.println("}");
	    }   
    } else {
	    out.println("{");
	    out.println("\"access\": \"bad\"");
	    out.println("}");
    }
} catch(Exception e){
    out.write(e.toString());
}

%>