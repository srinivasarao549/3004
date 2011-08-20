var DATA, I, 
	QID, QTYPE, QNAME,
	CHARTTYPE = "bar";

function submit() {
	var param = {},
		ans = [];
		
	param.qid = QID;
	
	if(QTYPE == "mp-multiple") {
		$("input:checked").each(function() { 
			ans.push($(this).val());
		});
		
		param.aid = ans.join(",");
	} else if(QTYPE == "mp-single") {
		param.aid = $("input:radio[name=ans]:checked").val();
	} else if(QTYPE.substr(0, 2) == "sr") {
		param.a = $("#resp").val();
	}
	
	I++;
	$("#chart").empty();
	
	dbPoll.api("submitanswer.txt", param, function(data) {
		console.log(data);
		if(data.responses) {
			var table = new google.visualization.DataTable(),
				key, i = 0, chart;
				
			table.addColumn('string', 'Response');
			table.addColumn('number', 'Amount');
			
			table.addRows(data.responses);
			
			if(CHARTTYPE === "bar") {
				chart = new google.visualization.BarChart(document.getElementById('chart'));
			} else if(CHARTTYPE === "pie") {
				chart = new google.visualization.PieChart(document.getElementById('chart'));
			} else if(CHARTTYPE === "column") {
				chart = new google.visualization.ColumnChart(document.getElementById('chart'));
			}
			
			console.log(table);
			chart.draw(table, {width: 500, height: 400, title: DATA.questions[I-1].question});
		}
	});
}

//dbPoll.api("getquestion-json.jsp", {poll: dbPoll.q.poll}, function(data) {
dbPoll.api("AnswerQuestion.txt", function(data) {
	var o = dbPoll.obj, index = +dbPoll.q.q || 0, l = data.questions.length;
		
	if(index >= l) {
		index = l - 1;
	}
	
	DATA = data;
	I = index;
	loadQuest(data.questions[index], index);
});

function loadQuest(question, index) {
	//load first question
	var o = dbPoll.obj, html = "", l = DATA.questions.length;
	
	QID = question.id;
	QTYPE = question.type;
	QNAME = question.question;
		
	o.qnum.text(index+1);
	o.qtotal.text(l);
	
	console.log(question);
	
	if(question.font && question.font != "null") o.answer.css("font-family", question.font);
	else o.answer.css("font-family", "Arial");
	
	if(question.fontColor && question.fontColor != "null") o.answer.css("color", question.fontColor);
	else o.answer.css("color", "#000");
	
	if(question.images && question.images != "null") o.image.attr("src", question.images);
	else $("div.img").hide();
	
	o.name.text(question.question);
	
	if(question.type === "mp-single" || question.type === "mp-multiple") {
		var j, ans;
		
		for(j in question.answers) {
			ans = question.answers[j];
			if(question.type === "mp-single") {
				html += "<label><input type='radio' name='ans' value='"+j+"' /> "+ans+"</label>";
			} else if(question.type === "mp-multiple") {
				html += "<label><input type='checkbox' name='ans' value='"+j+"' /> "+ans+"</label>";
			}
		}
	} else {
		html = "<input id='resp' type='text' name='ans' />";
	}
	
	o.response.html(html);
	
	if(question.type === "sr-num") {
		$("#resp").keydown(function(e) {
			return (e.which >= 48 && e.which <= 57) || e.which === 8;
		});
	} else if(question.type === "sr-alphanum") {
		$("#resp").keydown(function(e) {
			return (e.which >= 48 && e.which <= 57) || (e.which >= 65 && e.which <= 90) || e.which === 8;
		});
	}
	
	console.log(index, l);
	if(index+1 < l) {
		o.next.show();
	} else {
		o.submit.text("Finish");
		o.next.hide();
	}
	
	$("#feedback").show();
}

$("#submit").click(submit);
$("#next").click(function() {
	submit();
	
	loadQuest(DATA.questions[I], I);
});

$("#feedback-sub").click(function() {
	var param = {};
	param.qid = QID;
	param.feedback = $("#feedback-resp").val();
	
	$("#feedback-resp").val("");
	
	dbPoll.api("submitfeedback.jsp", param);
	$("#feedback").hide();
});