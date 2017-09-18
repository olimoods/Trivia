/**
 * Created by student on 9/13/17.
 */
public class questionAnswer {
    private String question;
    private String [] answers =new String[4];
    private int correctAnswer;
    public questionAnswer(String q,String a1, String a2, String a3, String a4, int correct ){
        question = q;
        answers[0]= a1;
        answers[1]= a2;
        answers[2]= a3;
        answers[3]= a4;
        correctAnswer = correct;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswers(int index) {
        return answers[index];
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
