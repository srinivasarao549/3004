/**
* Get list of polls
*/
dbPoll.api("api/admin-listpolls.jsp", function(data) {
	var i = 0, l = data.polls.length, j,
		poll, creator, html = "";
	
	//loop over polls
	for(; i < l; ++i) {
		poll = data.polls[i];
		
		html += "<div class='poll list'><h2>"+poll.pollName+"</h2>";
		html += "<div class='inner' data-id='"+poll.pollID+"'>";
		html += "<label>Name: <input type='text' class='name' value='"+poll.pollName+"' /></label>";
		html += "<label>Description: <input type='text' class='descr' value='"+(poll.description != "null" ? poll.description : "")+"' /></label>";
		html += "<label>Start: <input type='text' class='start' value='"+(poll.start != "null" ? poll.start : "")+"' /></label>"; 
		html += "<label>End: <input type='text' class='end' value='"+(poll.end != "null" ? poll.end : "")+"' /></label>";
		html += "<label>Online: <input type='checkbox' class='online' "+(poll.online == "TRUE" ? "checked" : "")+" /></label>";
		html += "<label><a class='button updateb'>Update</a></label>";
		html += "<label>Delete Poll: <a class='button delete'>Delete</a></label><div class='creators'>";
		
		//loop over the poll creators
		html += updateCreators(poll.pollCreators);

		html += "</div><label>Creator: <a class='button assign'>Assign</a><input type='text' class='username' /></label></div></div>";
	}
	
	$("#polls").html(html);
});

/**
* Create a Poll
*/
$("#create-poll .create").click(function() {
	var name = $("#create-poll .name").val(),
		start = $("#create-poll .start").val(),
		end = $("#create-poll .end").val(),
		descr = $("#create-poll .descr").val(),
		online = $("#create-poll .online").is(":checked");
	
	dbPoll.api("api/admin-createpoll.jsp", {pollName: name, start: start, finish: end, online: online, description: descr}, function(data) {
		if(data.status == "OK") {
			window.location.reload();
		}
	});
});

/**
* Delete poll
*/
$(".delete").live("click", function() {
    var $parent = $(this).parent().parent(),
            id = $parent.attr("data-id");
		
	
    dbPoll.api("api/admin-delpoll.jsp", {pollID: id}, function(data) {
         $parent.parent().remove();
    });
});

/**
* Assign a creator
*/
$(".assign").live("click", function() {
	console.log("click");
	var username = $(this).parent().find(".username").val(),
            $creators = $(this).parent().parent().find(".creators"),
            id = $(this).parent().parent().attr("data-id");
	
	dbPoll.message("<img src='assets/images/ajax-loader.gif'/> <strong>Loading:</strong> Assigning a Poll Creator");
	
	dbPoll.api("api/admin-assigncreator.jsp", {pollID: id, username: username}, function(data) {
		if(data.status !== "OK") {
			dbPoll.error(data.status);
		}
		
		var html = updateCreators(data.pollCreators);
		$creators.html(html);
		
		dbPoll.cancelMessage();
	});
});

/**
* Update a Poll
*/
$(".updateb").live("click", function() {
	var name = $(this).parent().parent().find("input.name").val(),
		id = $(this).parent().parent().attr("data-id"),
		start = $(this).parent().parent().find("input.start").val(),
		end = $(this).parent().parent().find("input.end").val(),
		online = $(this).parent().parent().find("input.online").is(":checked") ? "T" : "F",
		descr = $(this).parent().parent().find("input.descr").val();
		
	dbPoll.api("api/admin-editpoll.jsp", {pollID: id, pollName: name, start: start, finish: end, online: online, description: descr}, function() {
		window.location.reload();
	});
});

/**
* Unassign Creators
*/
$(".poll a.del").live("click", function() {
	var id = $(this).parent().parent().parent().attr("data-id"),
            $creators = $(this).parent().parent(),
		username = $(this).parent().text();
	
    username = username.substr(0, username.length - 1);
	
	dbPoll.api("api/admin-unassigncreator.jsp", {pollID: id, username: username}, function(data) {
		$creators.html(updateCreators(data.pollCreators));
	});
});

/**
* Generate HTML for creators list
*/
function updateCreators(creators) {
	var i = 0, l = creators.length, c,
		html = "";
	
	for(; i < l; ++i) {
		c = creators[i];
		
		html += "<span>" + c + " <a class='del'><img src='assets/images/cross.png' valign='text-bottom' /></a></span> ";
	}
	
	return html;
}

dbPoll.exit = function() {
	$(".poll a.del").die();
	$(".updateb").die();
	$(".assign").die();
	$(".delete").die();
};