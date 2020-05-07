package main.simple;

public class SequenceAction {

	private String elementName;	// widget name
	private String elementType;	// widget type
	private String elementAction;	//widget action
		
	SequenceAction(String eleName, String eleType, String eleAct)
	{
		this.elementName = eleName;
		this.elementType = eleType;
		this.elementAction = eleAct;
	}
	public String getEleName(){
		return elementName;
	}
	
	public String getEleType(){
		return elementType;
	}
	
	public String getEleAction() {
		return elementAction;
	}
	
	public void setEleName(String eleName) {
		this.elementName = eleName;
	}
	
	public void setEleType(String eleType) {
		this.elementType = eleType;
	}
	
	public void setEleAction(String eleAct) {
		this.elementAction = eleAct;
	}

}
