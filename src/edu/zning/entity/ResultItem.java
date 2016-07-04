package edu.zning.entity;

/**
 * @author FanstyCoder
 *
 */
public class ResultItem {
	private String word;
	private int offe;
	
	public ResultItem(String word, int offe){
		this.word=word;
		this.offe=offe;
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getOffe() {
		return offe;
	}
	public void setOffe(int offe) {
		this.offe = offe;
	}
}
