package main.simple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class SequenceReaderUtil {
		
	// widget types, add more as required
	public static enum WidgetType{
		button,	
		label,
		radioButton
	}
	
	static List<SequenceAction> actionList;
	static List<String> scriptsList;
	
	/*
	 * 	read in widget actions from a sequence file
	 */
	static{
		try{
			 BufferedReader br = new BufferedReader(new FileReader("fullnames"));
			 actionList = br.lines()
			              .map(line -> {
			                  String[] parts = line.split(",");	// modify according to sequence file
			                	  return new SequenceAction(parts[0],parts[1],parts[2]);  
			                 })
		                  .collect(Collectors.toList());
			 br.close();
		 }
		 catch(Exception e){
		     e.printStackTrace();
	      } 
	}
	
	/*
	 * Generate testing scripts based on widget action list
	 */
	public List<String> generateAction(List<SequenceAction> actionList) {
		String widgetAction = "";
		//String widgetType = "";
		for(SequenceAction widgetAct: actionList) {
			switch(widgetAct.getEleAction().toLowerCase()){
				case "click":{
					//widgetType = "button";
					widgetAction = "bot.Button(" + widgetAct.getEleName() + ").click();"; // update according to targeted scripts
					scriptsList.add(widgetAction);
				}
			}
		}
		if(scriptsList != null) {
			scriptsList.forEach(System.out::println);
			}
		return scriptsList;
	}

}
