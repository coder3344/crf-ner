package edu.zning.model;

/**
 * 输入语料会通过每个特征模板，生成一个特征（函数），并且对应每个tag都有一个全职，所以包含一个权值数组
 * @author FanstyCoder
 *
 */
public class FeatureFunction {
	public String featureName;
	public double[] weightForEachTag;
	
	public FeatureFunction(String name, int tagCount){
		this.featureName=name;
		this.weightForEachTag=new double[tagCount];		
	}
	
	public FeatureFunction(String name, double[] weights){
		this.featureName=name;
		this.weightForEachTag=weights;
	}
}
