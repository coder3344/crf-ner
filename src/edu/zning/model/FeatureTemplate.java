package edu.zning.model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.zning.entity.TagItem;

/**
 * 特征模板
 * @author FanstyCoder
 *
 */
public class FeatureTemplate {
	private String templateRawString=null;
	private ArrayList<String> delimiterList= null;
	private ArrayList<int[]> templateSubItem=null;
	static final Pattern pattern = Pattern.compile("%x\\[(-?\\d*),(\\d*)]");
	
	public FeatureTemplate() {
		this.delimiterList = new ArrayList<String>();
		this.templateSubItem = new ArrayList<int[]>();
	}
	
	/**
	 * 根据this特征模板，得到在index位置的词应该有的特征字符串
	 * @param tagItemList
	 * @param index
	 * @return
	 */
	public String featureRecover(TagItem[] tagItemList, int index){
		StringBuilder sb = new StringBuilder();
		int i=0;
		for(int[] positionPair : this.templateSubItem){
			sb.append(delimiterList.get(i++));
			
			int linepos=positionPair[0]+index;
			if(linepos<0){
				sb.append("_B"+linepos);
			}else if(linepos>=tagItemList.length){
				sb.append("_B"+(linepos-tagItemList.length+1));
			}else{
				sb.append(tagItemList[linepos].getSubFeature(positionPair[1]));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 创建一个模板实例
	 * @param lineTemplate
	 * @return
	 */
	public static FeatureTemplate create(String lineTemplate){
		FeatureTemplate newTemplate = new FeatureTemplate();
		newTemplate.templateRawString=lineTemplate;
		Matcher matcher = pattern.matcher(lineTemplate);
		int start = 0;
		while(matcher.find()){
			newTemplate.delimiterList.add(lineTemplate.substring(start,matcher.start()));
			start = matcher.end();
			newTemplate.templateSubItem.add(new int[]{Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2))});
		}
		return newTemplate;
	}
}
