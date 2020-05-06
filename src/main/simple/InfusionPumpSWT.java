package main.simple;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class InfusionPumpSWT {
	
	private Shell shell;
	private Button upBtn;
	private Button downBtn;
	private Button yesStartBtn;
	private Button noStopBtn;
	private Button onOffBtn;
	private Label displayLabel;
			
	private int TOTAL_VOLUME = 500;
	private int RATE_MAX = 25;
	private int VALUE_STEP = 1;
	
	private int pumpID;
	private int batteryPercent = 0;
	private int rate;	// volume per minute, ml/min
	private int duration;	// duration = TOTAL_VOLUME/rate
	private int curRate;
	private String displayContent = "";
	private boolean isPowerOn = false;
	
	enum Status{
		Initial,	// initial status after powered on
		SettingStart,	// start setting
		Setting,	// configuration is ongoing, + and - is enabled
		Paused,		// infusion is paused
		Infusing,	// infusion is ongoing
		SettingCancelled	// configuration is cancelled
	}
	private Status status;
	
	public InfusionPumpSWT(int id) {
		this.pumpID = id;
	}
		
	public void open(){
		Display display = Display.getDefault();
		initializeGUI();
		initializeValue();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
	}

	protected void initializeGUI(){
		shell = new Shell();
		shell.setText("Simple Infusion Pump");
		
		displayLabel = new Label (shell, SWT.COLOR_DARK_GRAY | SWT.WRAP);
		displayLabel.setText (displayContent);
		displayLabel.setBounds(50, 10, 400, 150);
	
		upBtn = new Button (shell, SWT.PUSH);
		upBtn.setText ("+");
		upBtn.setBounds(10, 170, 80, 80);
		upBtn.setEnabled(false);
		upBtn.addListener(SWT.Selection, event ->{
			executeUp();
			upBtn.setSelection(true);
		});
		
		yesStartBtn = new Button(shell, SWT.PUSH);
		yesStartBtn.setText("Yes/Start");
		yesStartBtn.setBounds(100, 170, 200, 80);
		yesStartBtn.setEnabled(false);
		yesStartBtn.addListener(SWT.Selection, event ->{
			executeYesStart();
			yesStartBtn.setSelection(true);
		});
		yesStartBtn.addListener(SWT.MouseDoubleClick, event ->{
			executeSetting();
			yesStartBtn.setSelection(true);
		});
		
		onOffBtn = new Button (shell, SWT.PUSH);
		onOffBtn.setText ("On/Off");
		onOffBtn.setBounds(310, 170, 120, 160);
//		onOffBtn.addListener(SWT.Selection, event ->{
//			executeOnOff();
//			onOffBtn.setSelection(true);
//		});

		onOffBtn.addListener(SWT.MouseDoubleClick, event ->{
			executeOnOff();
			onOffBtn.setSelection(true);
		});
		
		downBtn = new Button (shell, SWT.PUSH);
		downBtn.setText ("-");
		downBtn.setBounds(10, 250, 80, 80);
		downBtn.setEnabled(false);
		downBtn.addListener(SWT.Selection, event ->{
			executeDown();
			downBtn.setSelection(true);
		});
				
		noStopBtn = new Button (shell, SWT.PUSH);
		noStopBtn.setText ("No/Stop");	
		noStopBtn.setBounds(100, 250, 200, 80);
		noStopBtn.setEnabled(false);
		noStopBtn.addListener(SWT.Selection, event ->{
			executeNoStop();
			noStopBtn.setSelection(true);
		});
		
		shell.setDefaultButton (onOffBtn);
		shell.pack ();
		shell.open ();
	}
	
	/*
	 * Initialize the values when turns on	
	 */
		private void initializeValue() {
			
			batteryPercent = 100;
			rate = 5;
			curRate = rate;
			duration = TOTAL_VOLUME/rate;
			status = Status.Initial;
			displayInfo();
		}
		
		private void displayInfo() {
			if(isPowerOn) {
				displayContent =  "1. Double-Click [On/Off] to power on "
						+ "\n2. Double-Click [Yes/Start] to configure the infusion pump"
						+ "\n"
						+ "\nPumpId: " + pumpID
						+ "\nBatteryPercent: " + batteryPercent
						+ "\nTotal Volume: " + TOTAL_VOLUME
						+ "\nRate(ml/min): " + rate
						+ "\nDuration(mins): " + duration
						+ "\nStatus: " + status;
			}else {
				displayContent = " Powered off..."
						+ "\n"
						+ "\n1. Double-Click [On/Off] to power on "
						+ "\n"
						+ "\n2. Double-Click [Yes/Start] to configure the infusion pump";
			}	
			displayLabel.setText(displayContent);
		}
		
		/*
		 * Increase the value of rate, calculate the duration
		 */
		private void executeUp() {
			if(status == Status.SettingStart) {
				status = Status.Setting;
				curRate = rate;
			}
			if(isPowerOn && status == Status.Setting) {
				if(rate < RATE_MAX) {
					rate += VALUE_STEP;
					duration = (int) TOTAL_VOLUME/rate;
				}else {
					rate = RATE_MAX; 
					duration = (int) TOTAL_VOLUME/rate;
				}
				System.out.println("Increase rate to: " + rate);
			}	
			displayInfo();
		}
		
		/*
		 * Decrease the value of rate, calculate the duration
		 */
		private void executeDown() {
			if(status == Status.SettingStart) {
				status = Status.Setting;
				curRate = rate;
			}
			if(isPowerOn && status == Status.Setting) {
				if(rate > 1) {
					rate -= VALUE_STEP;
					duration = (int) TOTAL_VOLUME/rate;
				}else {
					rate = 1;
					duration = (int) TOTAL_VOLUME/rate;
				}
				System.out.println("Decrease rate to: " + rate);
			}
			displayInfo();
		}
		
		/*
		 * Start infusion	
		 */
		private void executeYesStart() {	
			if(isPowerOn) {
				if(status == Status.Initial) {
					System.out.println("Please configure the infusion pump first ");
				}else if(status == Status.Paused  || status == Status.SettingCancelled || status == Status.Setting) {
					status = Status.Infusing;
					System.out.println("**** Start infusion ****");
				}
				displayInfo();
			}		
		}
		
		/*
		 * Start configuration
		 */
		public void executeSetting() {
			if(isPowerOn && status != Status.SettingStart) {
					status = Status.SettingStart;
					upBtn.setEnabled(true);
					downBtn.setEnabled(true);
					noStopBtn.setEnabled(true);
					System.out.println("**** Start configuration ****");
				}
			displayInfo();
		}

		/*
		 * Cancel settings or pause infusion
		 */
		private void executeNoStop() {
			if(isPowerOn) {
				if(status == Status.Infusing) {
					status = Status.Paused;
					System.out.println("**** Status: infusting -> paused ****");
				}else if(status == Status.Setting || status == Status.SettingStart) {
					setRate(curRate);
					setDuration(curRate);
					status = Status.SettingCancelled;
					System.out.println("**** Cancel configuration ****");
				}
				displayInfo();
			}
		}
		
		/*
		 * Power on or power off
		 */
		private void executeOnOff() {
			if (isPowerOn == false) {
				isPowerOn = true;
				yesStartBtn.setEnabled(true);
				System.out.println("**** Power on ****");
			}else {
				isPowerOn = false;
				upBtn.setEnabled(false);
				downBtn.setEnabled(false);
				yesStartBtn.setEnabled(false);
				noStopBtn.setEnabled(false);
				System.out.println("**** Powered off ****");
			}
			displayInfo();
		}
		
	public void setRate(int rate) {this.rate = rate;}

	public int getRate() {return rate;}
		
	public int getDuration() {return duration;}
		
	public void setDuration(int rate) {
		if(rate != 0) {
			this.duration = (int)TOTAL_VOLUME/rate;
		}else {
			duration = 0;
		}
	}
	
	public static void main(String [] args) 
	 {
		InfusionPumpSWT pump = new InfusionPumpSWT(1);
		pump.open();
	}
}
