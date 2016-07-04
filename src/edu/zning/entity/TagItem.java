package edu.zning.entity;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTML.Tag;

public class TagItem {
	private String raw;
	private String[] subFeatures;
	private String finalTag;
	
	

	/** 该item标记为每个tag的概率  */ 
	public double[] valueForEachTag;
	/** 最佳标签 */
	public String bestTag;
	
	public TagItem(String raw, String[] features, String tag){
		this.raw=raw;
		this.subFeatures=features;
		this.bestTag=tag;
	}
	
	public TagItem(String raw, int featureCount){
		this.raw=raw;
		this.subFeatures=new String[featureCount];
		this.bestTag="NONE";
	}
	
	/**
	 * 得到每个sub feature
	 * @param subIndex
	 * @return
	 */
	public String getSubFeature(int subIndex){
		if(subIndex>this.subFeatures.length)
			return null;
		return this.subFeatures[subIndex];
	}
	
	/**
	 * 不同的需要，会依赖不同的输入语料，通过这个函数，把每个输入句子改造成crf输入格式<br>
	 * 此处是直接用分词结果得到模型的输入
	 * @param inputString
	 * @return
	 */
	public static TagItem[] makeTagItemArray(String inputString){
		String[] splits=segSplitTitle(inputString);
		ArrayList<TagItem> tmpResult = new ArrayList<TagItem>();
		for(String item : splits){
			TagItem newItem = new TagItem(item,2);
			int delimiterIdx = item.lastIndexOf("/");
			newItem.subFeatures[0]=item.substring(0, delimiterIdx);
			newItem.subFeatures[1] = item.substring(delimiterIdx);
			tmpResult.add(newItem);
		}
		TagItem[] finalResult = new TagItem[tmpResult.size()];
		System.arraycopy(tmpResult, 0, finalResult, 0, tmpResult.size());
		return finalResult;
	}
	
	/**
	 * 当心有空格的完整词，比如：IBM corp/nt
	 * @param title
	 * @return
	 */
	private static String[] segSplitTitle(String title){
		List<String> list=new ArrayList<String>();
		String[] titleSplits=title.split(" ");
		StringBuilder sbb = new StringBuilder();
		boolean mark = false;
		for(String ts : titleSplits){
			try{
				int idx=ts.indexOf('/');
				if(idx==1){
					sbb.append(ts+" ");
					mark=true;
					continue;
				}else{
					if(mark==true){
						list.add(sbb.toString()+ts);
						sbb=new StringBuilder();
						mark=false;
					}else{
						list.add(ts);
					}
				}
			}catch(Exception ex){
				
			}
		}
		String[] result= new String[list.size()];
		int i=0;
		for(String str : list){
			result[i++]=str;
		}
		return result;
	}
	
	/**
	 * 直接用训练数据的格式作为输入
	 * @param testLine 测试数据格式数据的每一行
	 * @return
	 */
	public static TagItem makeTagItem(String testLine){
		String[] splits = testLine.split("\t");
		TagItem newItem = new TagItem(testLine, splits.length);
		for( int i=0; i<splits.length; i++){
			newItem.subFeatures[i]=splits[i];
		}
		return newItem;
	}
	
	public String toString(){
		return this.raw+"\t"+this.bestTag;
	}
	
	
	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public String[] getSubFeatures() {
		return subFeatures;
	}

	public void setSubFeatures(String[] subFeatures) {
		this.subFeatures = subFeatures;
	}

	public String getFinalTag() {
		return finalTag;
	}

	public void setFinalTag(String finalTag) {
		this.finalTag = finalTag;
	}

	public double[] getValueForEachTag() {
		return valueForEachTag;
	}

	public void setValueForEachTag(double[] valueForEachTag) {
		this.valueForEachTag = valueForEachTag;
	}

	public String getBestTag() {
		return bestTag;
	}

	public void setBestTag(String bestTag) {
		this.bestTag = bestTag;
	}
}
