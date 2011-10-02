/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * controller.java
 *
 * Created on Sep 26, 2011, 10:06:03 PM
 */
package webapps;

import com.google.gson.Gson;
import com.turningtech.poll.Poll;
import com.turningtech.poll.PollService;
import com.turningtech.poll.Response;
import com.turningtech.poll.ResponseListener;
import com.turningtech.receiver.Receiver;
import com.turningtech.receiver.ResponseCardLibrary;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import oracle.net.aso.i;

/**
 *
 * @author Darren
 */
public class controller extends javax.swing.JApplet {

    private String path = "file:/c:/applet/";
    private int userID;
    private int curPollID = -1;
    private int curQuesID = -1;
    private int curQuesIndex = -1;
    private boolean receiving = false;
    private Polls polls = null;
    private Questions questions = null;
    private int curChannel = 42;

    /**Posts data to jsp page
     * Returns the return data (json or not)
     * @param jspURL
     * @return 
     */
    private String getJson(String jspURL) {
	try {
	    // Send data
	    URL url = new URL(jspURL);
	    URLConnection conn = url.openConnection();
	    // Get the response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String line;
	    String result = "";
	    while ((line = rd.readLine()) != null) {
		result = result + line;
	    }
	    rd.close();
	    return result;
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	    System.err.println(jspURL);
	    return "";
	}
    }

    /**Loads all the questions for a given poll into the global variable.
     * Fills the question list with question names/titles
     * Sets the current poll id
     * 
     * @param pollID 
     */
    private void getQuestions(int pollID) {
	//String json = getJson(path + "webuser-getquestions.jsp?pollid=" + pollID);
	String json = getJson(path + "webuser-getquestions.jsp"); //Testing
	Gson gson = new Gson();
	questions = gson.fromJson(json, Questions.class);
	((DefaultListModel) questionList.getModel()).clear();
	for (Question p : questions.getQuestions()) {
	    ((DefaultListModel) questionList.getModel()).addElement(p);
	}
	curPollID = pollID;
    }

    /**Loads the question onto the main screen
     * Sets the current question id
     * 
     * @param questID 
     */
    private void loadQuestion(int questID) {
	for (int i = 0; i < questions.getQuestions().size(); i++) {
	    if (questions.getQuestions().get(i).getId() == questID) {
		curQuesIndex = i;
		break;
	    }
	}
	List<Answer> current = questions.getQuestions().get(curQuesIndex).getAnswers();
	lblQuestion.setText("");
	lblAnswer1.setText("");
	lblAnswer2.setText("");
	lblAnswer3.setText("");
	lblAnswer4.setText("");
	lblAnswer5.setText("");
	lblAnswer6.setText("");
	lblAnswer7.setText("");
	lblAnswer8.setText("");
	lblAnswer9.setText("");
	lblAnswer0.setText("");
	try {
	    lblQuestion.setText(questions.getQuestions().get(curQuesIndex).getQuestion());
	    lblAnswer1.setText("1) " + current.get(0).getAnswer());
	    lblAnswer2.setText("2) " + current.get(1).getAnswer());
	    lblAnswer3.setText("3) " + current.get(2).getAnswer());
	    lblAnswer4.setText("4) " + current.get(3).getAnswer());
	    lblAnswer5.setText("5) " + current.get(4).getAnswer());
	    lblAnswer6.setText("6) " + current.get(5).getAnswer());
	    lblAnswer7.setText("7) " + current.get(6).getAnswer());
	    lblAnswer8.setText("8) " + current.get(7).getAnswer());
	    lblAnswer9.setText("9) " + current.get(8).getAnswer());
	    lblAnswer0.setText("0) " + current.get(9).getAnswer());
	} catch (Exception e) {
	    //Ignore
	}
	pollList.setEnabled(true);
	questionList.setEnabled(true);
	cmdStart.setEnabled(true);
	cmdStop.setEnabled(false);
	cmdNext.setEnabled(false);
	cmdSet.setEnabled(true);
	txtChannel.setEnabled(true);
	curQuesID = questID;
    }

    private void stopQuestion() {
	String json = getJson(path + "setactivequestion.jsp?pollid=" + curPollID + "&questID=-1");
	receiving = false;
	pollList.setEnabled(true);
	questionList.setEnabled(true);
	cmdStart.setEnabled(true);
	cmdStop.setEnabled(false);
	cmdNext.setEnabled(false);
	cmdSet.setEnabled(true);
	txtChannel.setEnabled(true);
    }

    private void nextQuestion(int curQuestID) {
	//Calls startQuestion with the next questID, or calls stopQuestion if there is no next
	for (int i = 0; i < questions.getQuestions().size(); i++) {
	    if (questions.getQuestions().get(i).getId() == curQuestID) {
		if (questions.getQuestions().size() - 1 == i) {
		    stopQuestion();
		    JOptionPane.showMessageDialog(rootPane, "No more questions left.", "Poll finished!", JOptionPane.INFORMATION_MESSAGE);
		} else {
		    questionList.setSelectedIndex(i + 1);
		    startQuestion(questions.getQuestions().get(i + 1).getId());
		    //CHANGE THE SELECTION
		}
	    }
	}
    }

    private void startQuestion(int questID) {
	loadQuestion(questID);
	String json = getJson(path + "setactivequestion.jsp?pollid=" + curPollID + "&questID=" + questID);
	receiving = true;
	pollList.setEnabled(false);
	questionList.setEnabled(false);
	cmdStart.setEnabled(false);
	cmdStop.setEnabled(true);
	cmdNext.setEnabled(true);
	cmdSet.setEnabled(false);
	txtChannel.setEnabled(false);
    }

    private void sendResponse(int questID, int clickerID, int answerID) {
	if (receiving) {
	    String json = getJson(path + "clickeranswer.jsp?questionid=" + questID + "&answerid=" + answerID + "&clickerID=" + clickerID);
	}
    }

    private void getPolls() {
	String json = getJson(path + "clickergetpolls.jsp");
	Gson gson = new Gson();
	polls = gson.fromJson(json, Polls.class);
	userID = polls.getID();
	if (userID == -1) {
	    pollList.setEnabled(false);
	    questionList.setEnabled(false);
	    cmdSet.setEnabled(false);
	    txtChannel.setEnabled(false);
	    cmdStart.setEnabled(false);
	    cmdStop.setEnabled(false);
	    cmdNext.setEnabled(false);
	    JOptionPane.showMessageDialog(rootPane, "You are not authorised to use this applet.", "Poll Master authorisation failed!", JOptionPane.ERROR_MESSAGE);
	} else {
	    ((DefaultListModel) pollList.getModel()).clear();
	    for (pollPairing p : polls.getPolls()) {
		((DefaultListModel) pollList.getModel()).addElement(p);
	    }
	    pollList.setEnabled(true);
	    questionList.setEnabled(true);
	    cmdSet.setEnabled(true);
	    txtChannel.setEnabled(true);
	    cmdStart.setEnabled(false);
	    cmdStop.setEnabled(false);
	    cmdNext.setEnabled(false);
	}
    }

    class pollListSelectionHandler implements ListSelectionListener {

	public void valueChanged(ListSelectionEvent e) {
	    if (!e.getValueIsAdjusting()) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty()) {
		    ((DefaultListModel) questionList.getModel()).clear();
		    stopQuestion();
		    cmdStart.setEnabled(false);
		    lblQuestion.setText("");
		    lblAnswer1.setText("");
		    lblAnswer2.setText("");
		    lblAnswer3.setText("");
		    lblAnswer4.setText("");
		    lblAnswer5.setText("");
		    lblAnswer6.setText("");
		    lblAnswer7.setText("");
		    lblAnswer8.setText("");
		    lblAnswer9.setText("");
		    lblAnswer0.setText("");
		} else {
		    curPollID = polls.getPolls().get(lsm.getMinSelectionIndex()).getID();
		    getQuestions(curPollID);
		}
	    }
	}
    }

    class startActionListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    startQuestion(curQuesID);
	}
    }

    class stopActionListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    stopQuestion();
	}
    }

    class nextActionListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    nextQuestion(curQuesID);
	}
    }

    class setActionListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    try {
		curChannel = Integer.parseInt(txtChannel.getText());
	    } catch (Exception ex) {
		JOptionPane.showMessageDialog(rootPane, "Invalid channel number.", "Channel switching failed!", JOptionPane.ERROR_MESSAGE);
		txtChannel.setText(String.valueOf(curChannel));
	    }
	}
    }

    class questionListSelectionHandler implements ListSelectionListener {

	public void valueChanged(ListSelectionEvent e) {
	    if (!e.getValueIsAdjusting()) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();
		if (lsm.isSelectionEmpty()) {
		    ((DefaultListModel) questionList.getModel()).clear();
		    stopQuestion();
		    cmdStart.setEnabled(false);
		    lblQuestion.setText("");
		    lblAnswer1.setText("");
		    lblAnswer2.setText("");
		    lblAnswer3.setText("");
		    lblAnswer4.setText("");
		    lblAnswer5.setText("");
		    lblAnswer6.setText("");
		    lblAnswer7.setText("");
		    lblAnswer8.setText("");
		    lblAnswer9.setText("");
		    lblAnswer0.setText("");
		} else {
		    curQuesID = questions.getQuestions().get(lsm.getMinSelectionIndex()).getId();
		    loadQuestion(curQuesID);
		}
	    }
	}
    }

    /** Initializes the applet controller */
    @Override
    public void init() {
	/* Set the Nimbus look and feel */
	//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
	 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
	 */
	try {
	    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		if ("Nimbus".equals(info.getName())) {
		    javax.swing.UIManager.setLookAndFeel(info.getClassName());
		    break;
		}
	    }
	} catch (ClassNotFoundException ex) {
	    java.util.logging.Logger.getLogger(controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (InstantiationException ex) {
	    java.util.logging.Logger.getLogger(controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (IllegalAccessException ex) {
	    java.util.logging.Logger.getLogger(controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (javax.swing.UnsupportedLookAndFeelException ex) {
	    java.util.logging.Logger.getLogger(controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	}
	//</editor-fold>

	/* Create and display the applet */
	try {
	    java.awt.EventQueue.invokeAndWait(new Runnable() {

		public void run() {
		    initComponents();
		}
	    });
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	pollList.setModel(new DefaultListModel());
	pollList.getSelectionModel().addListSelectionListener(new pollListSelectionHandler());
	pollList.setEnabled(false);
	questionList.setModel(new DefaultListModel());
	questionList.getSelectionModel().addListSelectionListener(new questionListSelectionHandler());
	questionList.setEnabled(false);
	cmdStart.addActionListener(new startActionListener());
	cmdStop.addActionListener(new stopActionListener());
	cmdNext.addActionListener(new nextActionListener());
	cmdSet.addActionListener(new setActionListener());
	lblQuestion.setText("");
	lblAnswer1.setText("");
	lblAnswer2.setText("");
	lblAnswer3.setText("");
	lblAnswer4.setText("");
	lblAnswer5.setText("");
	lblAnswer6.setText("");
	lblAnswer7.setText("");
	lblAnswer8.setText("");
	lblAnswer9.setText("");
	lblAnswer0.setText("");

	getPolls();
	ResponseCardLibrary.initializeLicense("University of Queensland", "24137BBFEEEA9C7F5D65B2432F10F960");
	/*Poll myPoll = PollService.createPoll();
	List<Receiver> receivers = myPoll.getReceivers();
	receivers.get(0).setChannel(45);
	myPoll.addResponseListener(new ResponseListener(){
	public void responseReceived(Response response) {
	System.out.println("Response:"+response);
	}
	});
	myPoll.start();*/
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pollList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        questionList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        txtChannel = new javax.swing.JTextField();
        cmdSet = new javax.swing.JButton();
        lblQuestion = new javax.swing.JLabel();
        lblAnswer1 = new javax.swing.JLabel();
        cmdStart = new javax.swing.JButton();
        cmdStop = new javax.swing.JButton();
        cmdNext = new javax.swing.JButton();
        lblAnswer2 = new javax.swing.JLabel();
        lblAnswer3 = new javax.swing.JLabel();
        lblAnswer4 = new javax.swing.JLabel();
        lblAnswer5 = new javax.swing.JLabel();
        lblAnswer6 = new javax.swing.JLabel();
        lblAnswer7 = new javax.swing.JLabel();
        lblAnswer8 = new javax.swing.JLabel();
        lblAnswer9 = new javax.swing.JLabel();
        lblAnswer0 = new javax.swing.JLabel();

        pollList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(pollList);

        questionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        questionList.setEnabled(false);
        jScrollPane2.setViewportView(questionList);

        jLabel1.setText("Receiver Channel:");

        txtChannel.setText("42");
        txtChannel.setName(""); // NOI18N
        txtChannel.setPreferredSize(new java.awt.Dimension(27, 20));

        cmdSet.setText("Set");

        lblQuestion.setText("Question");

        lblAnswer1.setText("Answer 1");

        cmdStart.setText("Start question");

        cmdStop.setText("Stop question");

        cmdNext.setText("Next question");

        lblAnswer2.setText("Answer 2");

        lblAnswer3.setText("Answer 3");

        lblAnswer4.setText("Answer 4");

        lblAnswer5.setText("Answer 5");

        lblAnswer6.setText("Answer 6");

        lblAnswer7.setText("Answer 7");

        lblAnswer8.setText("Answer 8");

        lblAnswer9.setText("Answer 9");

        lblAnswer0.setText("Answer 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblAnswer3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(lblAnswer0, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblQuestion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(cmdStart)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmdStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(14, 14, 14)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSet)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtChannel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdSet)
                            .addComponent(jLabel1))
                        .addGap(21, 21, 21)
                        .addComponent(lblQuestion)
                        .addGap(44, 44, 44)
                        .addComponent(lblAnswer1)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer2)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer3)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer4)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer5)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer6)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer7)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer8)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer9)
                        .addGap(18, 18, 18)
                        .addComponent(lblAnswer0)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmdStart)
                            .addComponent(cmdStop)
                            .addComponent(cmdNext))))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdNext;
    private javax.swing.JButton cmdSet;
    private javax.swing.JButton cmdStart;
    private javax.swing.JButton cmdStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAnswer0;
    private javax.swing.JLabel lblAnswer1;
    private javax.swing.JLabel lblAnswer2;
    private javax.swing.JLabel lblAnswer3;
    private javax.swing.JLabel lblAnswer4;
    private javax.swing.JLabel lblAnswer5;
    private javax.swing.JLabel lblAnswer6;
    private javax.swing.JLabel lblAnswer7;
    private javax.swing.JLabel lblAnswer8;
    private javax.swing.JLabel lblAnswer9;
    private javax.swing.JLabel lblQuestion;
    private javax.swing.JList pollList;
    private javax.swing.JList questionList;
    private javax.swing.JTextField txtChannel;
    // End of variables declaration//GEN-END:variables
}

class Answer {

    private int answerID;
    //private char keypad;
    private String answer;
    //private char correct;

    public int getAnswerID() {
	return answerID;
    }

    public void setAnswerID(int answerID) {
	this.answerID = answerID;
    }

    //public char getKeypad() {
//	return keypad;
    //}
    //public void setKeypad(char keypad) {
//	this.keypad = keypad;
    //}
    public String getAnswer() {
	return answer;
    }

    public void setAnswer(String answer) {
	this.answer = answer;
    }
    //public char getCorrect() {
    //return correct;
    //}
    //public void setCorrect(char correct) {
//	this.correct = correct;
    //}
}

class Question {

    private int id;
    private String type;
    private String question;
    private String font;
    private String correctIndicator;
    private String images;
    private String fontColor;
    private String fontSize;
    private List<Answer> answers;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getQuestion() {
	return question;
    }

    public void setQuestion(String question) {
	this.question = question;
    }

    public String getFont() {
	return font;
    }

    public void setFont(String font) {
	this.font = font;
    }

    public String getCorrectIndicator() {
	return correctIndicator;
    }

    public void setCorrectIndicator(String correctIndicator) {
	this.correctIndicator = correctIndicator;
    }

    public String getImages() {
	return images;
    }

    public void setImages(String images) {
	this.images = images;
    }

    public String getFontColor() {
	return fontColor;
    }

    public void setFontColor(String fontColor) {
	this.fontColor = fontColor;
    }

    public String getFontSize() {
	return fontSize;
    }

    public void setFontSize(String fontSize) {
	this.fontSize = fontSize;
    }

    public List<Answer> getAnswers() {
	return answers;
    }

    public void setAnswers(List<Answer> answers) {
	this.answers = answers;
    }

    public String toString() {
	return this.getQuestion();
    }
}

class Polls {

    private List<pollPairing> polls;
    private int ID;

    public List<pollPairing> getPolls() {
	return polls;
    }

    public void setPolls(List<pollPairing> polls) {
	this.polls = polls;
    }

    public int getID() {
	return ID;
    }

    public void setID(int ID) {
	this.ID = ID;
    }
}

class Questions {

    private List<Question> questions;

    public List<Question> getQuestions() {
	return questions;
    }

    public void setQuestions(List<Question> questions) {
	this.questions = questions;
    }
}

class pollPairing {

    private int ID;
    private String Name;

    public int getID() {
	return ID;
    }

    public void setID(int ID) {
	this.ID = ID;
    }

    public String getName() {
	return Name;
    }

    public void setName(String Name) {
	this.Name = Name;
    }

    public String toString() {
	return Name;
    }
}