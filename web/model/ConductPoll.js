var html = "<embed codebase='http://localhost:80/'\
       archive='ControlApplet.jar,gson-1.7.1.jar,jna.jar,rc_sdk.jar'\
       code='controller.class'\
      width='950' height='600'\
      type='application/x-java-applet'\
      userid='"+dbPoll.userID+"'\
      pluginspage='http://java.sun.com/j2se/1.5.0/download.html'/>";
	  
$("#container").html(html);