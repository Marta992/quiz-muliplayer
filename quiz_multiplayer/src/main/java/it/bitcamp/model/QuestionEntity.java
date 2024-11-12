package it.bitcamp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "QUESTION")
public class QuestionEntity {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "QUESTION_TEXT")
	private String questionText;

	@Column(name = "CORRECT_OPTION")
	private String correctOption;

	@Column(name = "OTHER_OPTION_1")
	private String otherOption1;

	@Column(name = "OTHER_OPTION_2")
	private String otherOption2;

	@Column(name = "OTHER_OPTION_3")
	private String otherOption3;

	public QuestionEntity() { }
	
	public QuestionEntity(int id, String questionText, String correctOption, String otherOption1, String otherOption2,
			String otherOption3) {
		super();
		this.id = id;
		this.questionText = questionText;
		this.correctOption = correctOption;
		this.otherOption1 = otherOption1;
		this.otherOption2 = otherOption2;
		this.otherOption3 = otherOption3;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getCorrectOption() {
		return correctOption;
	}

	public void setCorrectOption(String correctOption) {
		this.correctOption = correctOption;
	}

	public String getOtherOption1() {
		return otherOption1;
	}

	public void setOtherOption1(String otherOption1) {
		this.otherOption1 = otherOption1;
	}

	public String getOtherOption2() {
		return otherOption2;
	}

	public void setOtherOption2(String otherOption2) {
		this.otherOption2 = otherOption2;
	}

	public String getOtherOption3() {
		return otherOption3;
	}

	public void setOtherOption3(String otherOption3) {
		this.otherOption3 = otherOption3;
	}

}
