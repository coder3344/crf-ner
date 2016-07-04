package edu.zning.tagging;


import java.awt.TexturePaint;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoublePredicate;

import javax.management.relation.RelationServiceNotRegisteredException;

import edu.zning.entity.ResultItem;
import edu.zning.entity.TagItem;
import edu.zning.model.FeatureFunction;
import edu.zning.model.FeatureTemplate;
import edu.zning.model.Model;

public class TaggerImpl {
	Model model = null;
	public TaggerImpl(Model m){
		this.model =m;
	}
	
	public void toTag(TagItem[] itemList){
		int tagSize = this.model.tagCount;
		int[][] preNodeIndexTable = new int[itemList.length][tagSize];
		double[][] preNodeScoreTable = new double[itemList.length][tagSize];
		
		int i=0, j=0;
		for(i=0; i<itemList.length; i++){
			double[] value =new double[tagSize];
			for(FeatureTemplate f : this.model.feaTemplateList){
				String featureString = f.featureRecover(itemList, i);
				FeatureFunction function=this.model.getFeatureFunction(featureString);
				if(function == null){
					continue;
				}
				double[] tmpValues = function.weightForEachTag;
				
				for(j=0; j<this.model.tagCount; j++){
					value[j]+=tmpValues[j];
				}
			}
			itemList[i].setValueForEachTag(value);
			
			if(i==0){
				for(j=0; j<tagSize; j++){
					String tag=this.model.id2tagArray[j];
					if(tag=="M" || tag=="E"){
						preNodeScoreTable[0][j]=-10000;
					}else{
						preNodeScoreTable[0][j]=value[j];
					}
					preNodeIndexTable[i][j]=j;
				}
			}
		}
		
		if(itemList.length==1){
			double max = Double.MIN_VALUE;
			int maxId=-1;
			double[] tmpValues = itemList[0].getValueForEachTag();
			for(i=0; i<tagSize; i++){
				if(tmpValues[i]>max){
					max=tmpValues[i];
					maxId=i;
				}
			}
			itemList[0].setBestTag(this.model.getTagString(maxId));
			return ;
		}
		
		for(i=0; i<itemList.length; i++){
			for(int cur=0; cur<tagSize; cur++){
				double maxScore=Double.MIN_VALUE;
				for(int pre=0; pre<tagSize; pre++){
					double tmpScore=preNodeScoreTable[i-1][pre]+this.model.matrix[pre][cur]+itemList[i].valueForEachTag[cur];
					if(tmpScore>maxScore){
						preNodeIndexTable[i][cur]=pre;
						preNodeScoreTable[i][cur]=tmpScore;
						maxScore=tmpScore;
					}
				}
			}
		}
		
		int tagIdx_E=this.model.tag2idMap.get("B");
		int tagIdx_M=this.model.tag2idMap.get("M");
		
		for(i=0; i<tagSize; i++){
			if(i==tagIdx_E || i== tagIdx_M){
				preNodeScoreTable[itemList.length-1][i]=-10000;
			}
		}
		
		//回溯
		double tailMaxScore = Double.MIN_VALUE;
		int maxTagId=-1;
		for(i=0; i<tagSize; i++){
			if(preNodeScoreTable[itemList.length-1][i]>tailMaxScore){
				tailMaxScore=preNodeScoreTable[itemList.length-1][i];
				maxTagId=i;
				itemList[itemList.length-1].bestTag=model.getTagString(i);
			}
		}
		
		for(i=itemList.length-2; i>=0; i--){
			maxTagId=preNodeIndexTable[i+1][maxTagId];
			itemList[i].bestTag = this.model.getTagString(maxTagId);
		}
	}
	
	/**
	 * 对inference输出的结果，提取实体
	 * @param items
	 * @return
	 */
	public List<ResultItem> fetchResult(TagItem[] items){
		List<ResultItem> found = new ArrayList<ResultItem>();
		
		boolean enterMark=false;
		int offe=0;
		StringBuilder sb = new StringBuilder();
		for(TagItem item : items){
			if("B".equals(item.bestTag)){
				sb.append(item.getRaw());
			}else if("E".equals(item.bestTag)){
				sb.append(item.getRaw());
				ResultItem resultItem =new ResultItem(sb.toString(), offe);
				found.add(resultItem);
				sb=new StringBuilder();
			}else if("M".equals(item.bestTag)){
				sb.append(item.getRaw());
			}else if("S".equals(item.bestTag)){
				ResultItem resultItem =new ResultItem(item.getRaw(), offe);
				found.add(resultItem);
				sb=new StringBuilder();
			}else{
				;
			}
			offe+=item.getRaw().length();
		}
		
		return found;
	}
}
