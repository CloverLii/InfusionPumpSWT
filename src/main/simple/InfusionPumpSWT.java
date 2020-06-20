package main.simple;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class InfusionPumpSWT {
	
	private static Shell shell;
	private Button upBtn;
	private Button downBtn;
	private Button yesStartBtn;
	private Button noStopBtn;
	private Button onOffBtn;
	private Label displayLabel;
			
	private final int VOLUME_MAX = 800; //maximum of volume, ml
	private final int VOLUME_MIN = 100; 
	private final int DURATION_MAX = 80; //maximum of duration, mins
	private final int DURATION_MIN = 10;
	private final int VOLUME_STEP = 100; //increment or decrement when change volume
	private final int DURATION_STEP = 10; //increment or decrement when change duration
	
	private int pumpID; //default value is 1
	private int batteryPercent = 0; // value is 100 when power on
	private int volume = 0; 
	private int duration = 0;	
	private int oldVolume = 0; //used when cancel settings
	private int oldDuration = 0; //used when cancel settings
	private String displayContent = " ";
	
	private final String hintStr = " Configuration Instruction"
			+ "\n\n -> Power On"
			+ "\n -> Set Volume"
			+ "\n -> Set Duration"
			+ "\n -> Confirm Settings"
			+ "\n -> Start Infusion?"
			+ "\n -> Start Infusion...";
	
	enum Status{
		Off, // power off
		Initial, // initial status after powered on or stop infusion
		SetVolume,
		SetDuration,
		SettingsConfirmed,
		QStartInfusion, //double check before start infusion
		Infusing, // infusion is ongoing
		Paused,	 // infusion is paused
		SettingsCancelled // configuration is cancelled
	}
	private Status status = Status.Off;
	
	public InfusionPumpSWT(int id) {
		this.pumpID = id;
		initializeGUI();
		displayInfo();
	}
	
	public static void main(String [] args) 
	 {
		InfusionPumpSWT pump = new InfusionPumpSWT(1);
		pump.open();
	}
	
	private void open(){
		Display display = Display.getDefault();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
	}		

	private void initializeGUI(){
		shell = new Shell();
		shell.setText("Simple Infusion Pump");
		shell.setBounds(100, 100, 460, 460 );
		
		Label hintLabel = new Label (shell, SWT.COLOR_DARK_GRAY | SWT.WRAP);		
		hintLabel.setBounds(15, 20, 180, 180);
		hintLabel.setText (hintStr);
		
		displayLabel = new Label (shell, SWT.WRAP);
		displayLabel.setText (displayContent);
		displayLabel.setBounds(190, 20, 180, 180);
	
		upBtn = new Button (shell, SWT.PUSH);
		upBtn.setText ("+");
		upBtn.setBounds(15, 180, 60, 60);
		upBtn.setEnabled(false);
		upBtn.addListener(SWT.Selection, event ->{
			executeUp();
			upBtn.setSelection(true);
		});
		
		downBtn = new Button (shell, SWT.PUSH);
		downBtn.setText ("-");
		downBtn.setBounds(15, 260, 60, 60);
		downBtn.setEnabled(false);
		downBtn.addListener(SWT.Selection, event ->{
			executeDown();
			downBtn.setSelection(true);
		});
		
		yesStartBtn = new Button(shell, SWT.PUSH);
		yesStartBtn.setText("Yes/Start");
		yesStartBtn.setBounds(90, 180, 140, 60);
		yesStartBtn.addListener(SWT.Selection, event ->{
			executeYesStart();
			yesStartBtn.setSelection(true);
		});
		
		noStopBtn = new Button (shell, SWT.PUSH);
		noStopBtn.setText ("No/Stop");	
		noStopBtn.setBounds(90, 260, 140, 60);
		noStopBtn.setEnabled(false);
		noStopBtn.addListener(SWT.Selection, event ->{
			executeNoStop();
			noStopBtn.setSelection(true);
		});
		
		onOffBtn = new Button (shell, SWT.PUSH);
		onOffBtn.setText ("On/Off");
		onOffBtn.setBounds(245, 180, 110, 140);
		onOffBtn.addListener(SWT.Selection, event ->{
			executeOnOff();
			onOffBtn.setSelection(true);
		});
		
	
		shell.setDefaultButton (onOffBtn);
		shell.pack ();
		shell.open ();
	}
	
	/*
	 * Initialize values	
	 */
		private void initializeValue() {
			
			batteryPercent = 100;
			duration = 10;
			volume = 100;
			duration = 10;
			oldVolume = volume;
			oldDuration = duration;
			
			status = Status.Initial;
			upBtn.setEnabled(true);
			downBtn.setEnabled(true);
			noStopBtn.setEnabled(true);
			displayInfo();
		}
		
		/*
		 * Display the settings and status
		 */
		private void displayInfo() {
			if(status == Status.Off) {
				displayContent = "Powered Off"
								+ " \n\nDisplay confusion status and settings";			
			}else {
				displayContent = " PumpId: " + pumpID
						+ " \n BatteryPercent: " + batteryPercent
						+ " \n\n Volume(ml): " + volume
						+ " \n Duration(mins): " + duration
						+ " \n Rate(ml/mins): " + calculateRate()
						+ " \n\n Status: " + status;
			}
			displayLabel.setText(displayContent);
		}

		/*
		 * Calculate infusion rate
		 */
		private String calculateRate() {
			return String.format("%.2f", (double)volume/duration);
		}
		
		/*
		 * Increase the value of volume of duration
		 */
		private void executeUp() {
			if(status == Status.SetVolume) {
				if(volume < VOLUME_MAX) {
					volume += VOLUME_STEP;				
				}else {
					volume = VOLUME_MAX; 
				}
				System.out.println("Increase volume to: " + volume);
			}else if(status == Status.SetDuration) {
				if(duration < DURATION_MAX) {
					duration += DURATION_STEP;
				}else {
					duration = DURATION_MAX; 
				}			
				System.out.println("Increase duration to: " + duration);
			}
			displayInfo();
		}
		
		
		/*
		 * Decrease the value of volume or duration
		 */
		private void executeDown() {
			if(status == Status.SetVolume) {
				if(volume > VOLUME_MIN) {
					volume -= VOLUME_STEP;				
				}else {
					volume = VOLUME_MIN; 
				}
				System.out.println("Decrease volume to: " + volume);
			}else if(status == Status.SetDuration) {
				if(duration > DURATION_MIN) {
					duration -= DURATION_STEP;
				}else {
					duration = DURATION_MIN; 
				}
				System.out.println("Decrease duration to: " + duration);
			}
			displayInfo();
		}
		
		/*
		 * Switch modes: SetVolume, SetDuration, ConfirmSettings, QInfusionStart, StartInfusion	
		 */
		private void executeYesStart() {
			switch(status) {
				case Initial:
				case Infusing:
				case SettingsCancelled:
					status = Status.SetVolume;
					System.out.println("**** Set volume ****");
					break;
				case SetVolume:
					status = Status.SetDuration;
					System.out.println("**** Set duration ****");
					break;
				case SetDuration:
					status = Status.SettingsConfirmed;
					oldVolume = volume;
					oldDuration = duration;
					System.out.println("**** Confirm settings ****");
					break;
				case SettingsConfirmed:
					status = Status.QStartInfusion;
					System.out.println("**** Start infusion ? ****");
					break;
				case QStartInfusion:
				case Paused:
					status = Status.Infusing;
					System.out.println("**** Start infusion ... ****");
					break;
				default:
					break;
			}
			displayInfo();	
		}

		/*
		 * Cancel settings, pause infusion or stop infusion
		 */
		private void executeNoStop() {
			switch (status){
				case SetVolume:
				case SetDuration:
					status = Status.SettingsCancelled;
					volume = oldVolume;
					duration = oldDuration;
					System.out.println("**** Cancel settings ****");
					break;
				case Paused:
				case SettingsCancelled:
					initializeValue();
					System.out.println("**** Status: paused/settingsCancelled -> Initial ****");
					break;
				default:
					status = Status.Paused;
					System.out.println("**** Status: infusing -> paused ****");
					break;
			}
			displayInfo();
		}
		
		/*
		 * Power on or power off
		 */
		private void executeOnOff() {
			if (status == Status.Off) {
				status = Status.Initial;
				initializeValue();
				System.out.println("**** Powered on ****");
			}else {
				status = Status.Off;
				upBtn.setEnabled(false);
				downBtn.setEnabled(false);
				noStopBtn.setEnabled(false);
				System.out.println("**** Powered off ****");
			}
			displayInfo();
		}
		
}
