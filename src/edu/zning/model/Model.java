package edu.zning.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Model {
	//final static 
	/** 所有特征模板 */ 
	public ArrayList<FeatureTemplate> feaTemplateList=new ArrayList<FeatureTemplate>();
	public HashMap<String, FeatureFunction> functionMap = new HashMap<String,FeatureFunction>();
	
	/** 标签和id的相互转换 */
	public Map<String, Integer> tag2idMap = new HashMap<String , Integer>();
	/** id转标签  */
	public String[] id2tagArray;
	
	/**  tag的二元转移矩阵，适用于bi-gram feature */
	public double[][] matrix;
	
	/** 所有输出标签数量 */
	public int tagCount=0;
	
	public static Model loadFromTxt(String modelPath){
		Model model = null;
		try{
			model = new Model();
			FileInputStream in = new FileInputStream(new File(modelPath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			
			String version = reader.readLine();
			String cost_factor = reader.readLine();
			int maxid = Integer.parseInt(reader.readLine().trim().split(":")[1].trim());
			int xsize = Integer.parseInt(reader.readLine().trim().split(":")[1].trim());
			String empty = reader.readLine();
			
			//标签
			String line="";
			int tagCount=0;
			while((line=reader.readLine()).trim().length()!=0){
				model.tag2idMap.put(line.trim(), tagCount);
				tagCount++;
			}
			model.tagCount=tagCount;
			model.id2tagArray= new String[tagCount];
			for(Map.Entry<String, Integer> entry : model.tag2idMap.entrySet()){
				model.id2tagArray[entry.getValue()] = entry.getKey();
			}
			
			//模板
			int templateCount = 0;
			while( (line=reader.readLine()).trim().length()!=0){
				if(!"B".equals(line)){
					FeatureTemplate template = FeatureTemplate.create((line));
					model.feaTemplateList.add((template));
				}else{
					model.matrix = new double[tagCount][tagCount];
				}
			}
			
			//保存所有特征函数
			empty=reader.readLine();//跳过0 B
			List<FeatureFunction> tmpFeatureFunctionList = new LinkedList<FeatureFunction>();
			while( (line=reader.readLine()).length()!=0){
				String[] splits = line.split(" ",2);
				FeatureFunction function = new FeatureFunction(splits[1], tagCount);
				model.functionMap.put(function.featureName, function);
				tmpFeatureFunctionList.add(function);
			}
			
			//tag转移概率
			if(model.matrix!=null){
				for(int i=0; i<tagCount; i++){
					for(int j=0; j<tagCount; j++){
						model.matrix[i][j]=Double.parseDouble(reader.readLine());
					}
				}
			}
			
			//填充每个特征函数对应每个tag的权值
			for(FeatureFunction function : tmpFeatureFunctionList){
				for(int i=0; i<tagCount; i++){
					function.weightForEachTag[i]=Double.parseDouble(reader.readLine());
				}
			}
			
			//讲道理，到这里就应该读完了的
			line=reader.readLine();
			if(line!=null){
				System.out.println("bad txt model file");
			}
			reader.close();
		}catch(Exception ex){
			model=null;
		}
		return model;
	}
	
	/**
	 * 根据tag id获取tag string
	 * @param tagIdx
	 * @return
	 */
	public String getTagString(int tagIdx){
		if(tagIdx>=this.tagCount)
			return null;
		return this.id2tagArray[tagIdx];
	}
	
	public FeatureFunction getFeatureFunction(String tagString){
		return this.functionMap.get(tagString);
	}
}
