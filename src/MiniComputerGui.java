import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class MiniComputerGui extends JFrame implements ActionListener, Observer, KeyListener {

	protected static volatile boolean validKeyClicked = false;
	protected static volatile boolean haltButtonClicked = false;
	private static final long serialVersionUID = -8217933506339143771L;
	private static final int INSTRUCTION_SIZE = 16;	
	private MiniComputer cpu;
	private Container mainPanel;
	private JPanel leftPanel;
	private JPanel centerPanel;
	private JPanel rightPanel;
	private JPanel statusPanel;
	private JPanel iplPanel;
	private JPanel instructionPanel;
	private JPanel pcPanel;
	private JPanel registerPanel;
	private JPanel memoryPanel;
	private JPanel consolePrinterPanel;
	private JPanel consoleKeyboardPanel;
	private JPanel cachePanel;
	private JLabel statusLabel;
	private JLabel[] indicators;
	private JToggleButton[] toggles;
	private JButton instructionLoadButton;
	private JTextField pcInput;
	private JButton iplButton;
	private JButton loadFileButton;
	private JButton runButton;
	private JButton haltButton;
	private JButton singleStepButton;
	private JFileChooser fileChooser;
	private DefaultTableModel registerModel;
	private DefaultTableModel memoryModel;
	private DefaultTableModel cacheModel;
	private JTable registerTable;
	private JTable memoryTable;
	private JTable cacheTable;
	private JButton refreshTablesButton;
	private JTextArea consolePrinterOutput;
	private JTextField consoleKeyboardInput;
	private JButton clearConsolePrinterButton;
	private String consoleKeyboardInputHolder;

	public MiniComputerGui() throws FileNotFoundException, IOException {

		cpu = new MiniComputer();
		mainPanel = getContentPane();
		leftPanel = new JPanel();
		centerPanel = new JPanel();
		rightPanel = new JPanel();
		statusPanel = new JPanel();
		iplPanel = new JPanel();
		instructionPanel = new JPanel();
		pcPanel = new JPanel();
		registerPanel = new JPanel();
		memoryPanel = new JPanel();
		consolePrinterPanel = new JPanel();
		consoleKeyboardPanel = new JPanel();
		cachePanel = new JPanel();
		statusLabel = new JLabel();
		indicators = new JLabel[INSTRUCTION_SIZE];
		toggles = new JToggleButton[INSTRUCTION_SIZE];
		instructionLoadButton = new JButton("Load Instruction");
		pcInput = new JTextField(10);
		iplButton = new JButton("IPL");
		loadFileButton = new JButton("Load Prgm");
		runButton = new JButton("Run");
		haltButton = new JButton("Halt");
		singleStepButton = new JButton("Single Step");
		fileChooser = new JFileChooser();
		registerModel = new DefaultTableModel();
		memoryModel = new DefaultTableModel();
		cacheModel = new DefaultTableModel();
		registerTable = new JTable(registerModel);
		memoryTable = new JTable(memoryModel);
		cacheTable = new JTable(cacheModel);
		refreshTablesButton = new JButton("Refresh Table Data");
		consolePrinterOutput = new JTextArea();
		consoleKeyboardInput = new JTextField(10);
		clearConsolePrinterButton = new JButton("Clear Output Screen");
		consoleKeyboardInputHolder = "";
		
		initUI();
    }

    private void initUI() {

    	final int WINDOW_WIDTH = 1000;
    	final int WINDOW_HEIGHT = 900;
    	
    	setTitle("The Mini Computer");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    	setPanelLayouts();
        setPanelBorders();
        
        addToStatusPanel();
        addToIplPanel();
        addToInstructionPanel();
        addToPcPanel();
        addToRegisterPanel();
        addToMemoryPanel();
        addToConsolePrinterPanel();
        addToConsoleKeyboardPanel();
        addToCachePanel();
        
        initRegisterTable();
        initMemoryTable();
        initCacheTable();
        
        addToLeftPanel();
        addToCenterPanel();
        addToRightPanel();
        addToMainPanel();
        
        cpu.addObserver(this);
        
    }
    
    private void setPanelLayouts() {
    	
    	mainPanel.setLayout(new GridLayout(1, 3));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        statusPanel.setLayout(new GridLayout(0, 2));
        iplPanel.setLayout(new FlowLayout());
        instructionPanel.setLayout(new GridLayout(0, 2));
        pcPanel.setLayout(new FlowLayout());
        registerPanel.setLayout(new BorderLayout());
        memoryPanel.setLayout(new BorderLayout());
        consolePrinterPanel.setLayout(new BorderLayout());
        consoleKeyboardPanel.setLayout(new FlowLayout());
        cachePanel.setLayout(new BorderLayout());
    }
    
    private void setPanelBorders() {
    	
    	statusPanel.setBorder(new TitledBorder("Machine Status"));
    	iplPanel.setBorder(new TitledBorder("Initial Program Load"));
        instructionPanel.setBorder(new TitledBorder("Instructions"));
        pcPanel.setBorder(new TitledBorder("Program Counter"));
        registerPanel.setBorder(new TitledBorder("Registers"));
        memoryPanel.setBorder(new TitledBorder("Memory in Use"));
        consolePrinterPanel.setBorder(new TitledBorder("Console Printer"));
        consoleKeyboardPanel.setBorder(new TitledBorder("Console Keyboard"));
        cachePanel.setBorder(new TitledBorder("Cache"));
    }
    
    private void addToStatusPanel() {
    	
    	statusLabel.setText("OK");
    	statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	statusLabel.setOpaque(true);
    	statusLabel.setBackground(Color.GREEN);
    	statusLabel.setPreferredSize(new Dimension(0, 0));
    	
    	statusPanel.add(statusLabel, BorderLayout.NORTH);
    }
    
    private void addToIplPanel() {
    	
    	final int BUTTON_WIDTH = 120;
    	final int BUTTON_HEIGHT = 25;
    	final int FONT_SIZE = 15;
    	
    	iplButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    	iplButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
    	iplButton.addActionListener(this);
        iplPanel.add(iplButton);
        
        loadFileButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    	loadFileButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
    	loadFileButton.addActionListener(this);
        iplPanel.add(loadFileButton);
        
        runButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    	runButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
    	runButton.addActionListener(this);
        iplPanel.add(runButton);
        
        haltButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    	haltButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
    	haltButton.addActionListener(this);
        iplPanel.add(haltButton);
    }
    
    private void addToInstructionPanel() {
    	
    	instructionPanel.add(new JLabel("Indicator"));
        instructionPanel.add(new JLabel("Position"));
        
        for (int i = 0; i < INSTRUCTION_SIZE; i++) {
        	
        	indicators[i] = new JLabel("0", SwingConstants.CENTER);
        	toggles[i] = new JToggleButton(i + "");
        	
        	indicators[i].setOpaque(true);
        	indicators[i].setBackground(Color.RED);
        	toggles[i].addActionListener(this);
        	
        	instructionPanel.add(indicators[i]);
        	instructionPanel.add(toggles[i]);
        }
        
        instructionLoadButton.addActionListener(this);
        instructionPanel.add(new JLabel(""));
        instructionPanel.add(new JLabel(""));
        instructionPanel.add(new JLabel(""));
        instructionPanel.add(instructionLoadButton);
    }
    
    private void addToPcPanel() {
    	
    	singleStepButton.addActionListener(this);
        pcPanel.add(new JLabel("PC:"));
        pcPanel.add(pcInput);
        pcPanel.add(singleStepButton);
    }
    
    private void addToRegisterPanel() {
    	
        registerPanel.add(new JScrollPane(registerTable), BorderLayout.CENTER);
        
        refreshTablesButton.addActionListener(this);
        registerPanel.add(refreshTablesButton, BorderLayout.SOUTH);
    }
    
    private void addToMemoryPanel() {
    	
    	memoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        memoryPanel.add(new JScrollPane(memoryTable));
    }
    
    private void addToConsolePrinterPanel() {
    	
    	consolePrinterOutput.setEditable(false);
    	consolePrinterOutput.setLineWrap(true);
    	consolePrinterOutput.setWrapStyleWord(true);
    	consolePrinterPanel.add(new JScrollPane(consolePrinterOutput), BorderLayout.CENTER);
    	
    	clearConsolePrinterButton.addActionListener(this);
    	consolePrinterPanel.add(clearConsolePrinterButton, BorderLayout.SOUTH);
    }
    
    private void addToConsoleKeyboardPanel() {
    	
    	consoleKeyboardInput.setEnabled(false);
    	consoleKeyboardInput.addKeyListener(this);
    	consoleKeyboardPanel.add(consoleKeyboardInput);
    }
    
    private void addToCachePanel() {
    	
    	cacheTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	cacheTable.setPreferredScrollableViewportSize(new Dimension(0, 0));
    	cachePanel.add(new JScrollPane(cacheTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }
    
    private void initRegisterTable() {
    	
    	final String SIXTEEN_ZEROS = "0000000000000000";
    	final String TWELVE_ZEROS = "000000000000";
    	final String FOUR_ZEROS = "0000";
    	
    	registerModel.setRowCount(0);
    	registerModel.addColumn("Register");
        registerModel.addColumn("Value (base-2)");
        registerModel.addRow(new Object[]{"PC", TWELVE_ZEROS});
        registerModel.addRow(new Object[]{"CC", FOUR_ZEROS});
        registerModel.addRow(new Object[]{"IR", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"MAR", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"MBR", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"MSR", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"MFR", FOUR_ZEROS});
        registerModel.addRow(new Object[]{"IAR", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"IRR [0]", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"IRR [1]", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"IRR [2]", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"IRR [3]", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"R0", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"R1", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"R2", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"R3", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"X1", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"X2", SIXTEEN_ZEROS});
        registerModel.addRow(new Object[]{"X3", SIXTEEN_ZEROS});
    }
    
    private void initMemoryTable() {
    	
    	memoryModel.setRowCount(0);
    	memoryModel.addColumn("Address (base-2)");
        memoryModel.addColumn("Value (base-2)");
        memoryModel.addColumn("Address (base-10");
        memoryModel.addColumn("Value (vase-10");
        memoryModel.addRow(new Object[]{"", "", "", ""});
    }
    
    private void initCacheTable() {
    	
    	cacheModel.setRowCount(0);
    	cacheModel.addColumn("Address Tag");
    	
    	String temp;
    	
    	for (int i = 0; i < Cache.MAX_CACHE_SIZE; i++) {
    		
			temp = DataConversion.intToFourBitString(i);
			cacheModel.addColumn(temp);
    	}
    	
    	cacheModel.addRow(new Object[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
    }
    
    private void addToLeftPanel() {
    	
    	leftPanel.add(statusPanel);
    	leftPanel.add(iplPanel);
        leftPanel.add(instructionPanel);
        leftPanel.add(pcPanel);
    }
    
    private void addToCenterPanel() {
    	
    	centerPanel.add(registerPanel);
        centerPanel.add(memoryPanel);
    }
    
    private void addToRightPanel() {
    	
    	rightPanel.add(consolePrinterPanel);
    	rightPanel.add(consoleKeyboardPanel);
    	rightPanel.add(cachePanel);
    }
    
    private void addToMainPanel() {
    	
    	mainPanel.add(leftPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(rightPanel);
    }
    
    /**** public methods ****/
    
    public String getPcInput() {
    	return pcInput.getText().trim();
    }
    
    public void populatePcInput() {
    	pcInput.setText(cpu.getPC().getBitValue().getValue());
    }
    
    public void clearConsoleKeyboard() {
    	consoleKeyboardInput.setText("");
    }
    
    public void clearConsolePrinter() {
    	consolePrinterOutput.setText("");
    }
    
    public String getInstructionWord() {
    	
    	StringBuilder sb = new StringBuilder();
    	
    	for (int i = 0; i < INSTRUCTION_SIZE; i++) {
    		if (toggles[i].isSelected()) {
    			sb.append("1");
    		} else {
    			sb.append("0");
    		}
    	}
    	
    	return sb.toString();
    }
    
    /* Populate Table Methods */
    
    public void populateAllTables() {
    	
    	populateRegisterTable();
    	populateMemoryTable();
    	populateCacheTable();
    }
    
    private void populateRegisterTable() {
    	
    	registerModel.setRowCount(0);
    	registerModel.addRow(new Object[]{"PC", cpu.getPC().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"CC", cpu.getCC().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"IR", cpu.getIR().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"MAR", cpu.getMAR().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"MBR", cpu.getMBR().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"MSR", cpu.getMSR().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"MFR", cpu.getMFR().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"IAR", cpu.getIAR().getBitValue().getValue()});
        registerModel.addRow(new Object[]{"IRR [0]", cpu.getIRR()[0].getBitValue().getValue()});
        registerModel.addRow(new Object[]{"IRR [1]", cpu.getIRR()[1].getBitValue().getValue()});
        registerModel.addRow(new Object[]{"IRR [2]", cpu.getIRR()[2].getBitValue().getValue()});
        registerModel.addRow(new Object[]{"IRR [3]", cpu.getIRR()[3].getBitValue().getValue()});
        registerModel.addRow(new Object[]{"R0", cpu.getR(0).getBitValue().getValue()});
        registerModel.addRow(new Object[]{"R1", cpu.getR(1).getBitValue().getValue()});
        registerModel.addRow(new Object[]{"R2", cpu.getR(2).getBitValue().getValue()});
        registerModel.addRow(new Object[]{"R3", cpu.getR(3).getBitValue().getValue()});
        registerModel.addRow(new Object[]{"X1", cpu.getX(1).getBitValue().getValue()});
        registerModel.addRow(new Object[]{"X2", cpu.getX(2).getBitValue().getValue()});
        registerModel.addRow(new Object[]{"X3", cpu.getX(3).getBitValue().getValue()});
    }
    
    private void populateMemoryTable() {
    	
    	memoryModel.setRowCount(0);
        
    	for (Map.Entry<String, MemoryLocation> m : cpu.getMemory().entrySet()) {
    		
    		String address2 = m.getValue().getAddress().getValue();
    		String value2 = m.getValue().getValue().getValue();
    		String address10 = String.valueOf(Integer.parseInt(address2, 2));
    		String value10 = String.valueOf(Integer.parseInt(value2, 2));
    		
    		memoryModel.addRow(new Object[]{address2, value2, address10, value10});
    	}
    }
    
    private void populateCacheTable() {
    	
    	cacheModel.setRowCount(0);
    	
    	for (CacheLine cl : cpu.getCache().getCache()) {
    		cacheModel.addRow(new Object[]{
    				cl.getAddressTag().getValue(), 
    				cl.getBlock()[0].getValue().getValue(),
    				cl.getBlock()[1].getValue().getValue(),
    				cl.getBlock()[2].getValue().getValue(),
    				cl.getBlock()[3].getValue().getValue(),
    				cl.getBlock()[4].getValue().getValue(),
    				cl.getBlock()[5].getValue().getValue(),
    				cl.getBlock()[6].getValue().getValue(),
    				cl.getBlock()[7].getValue().getValue(),
    				cl.getBlock()[8].getValue().getValue(),
    				cl.getBlock()[9].getValue().getValue(),
    				cl.getBlock()[10].getValue().getValue(),
    				cl.getBlock()[11].getValue().getValue(),
    				cl.getBlock()[12].getValue().getValue(),
    				cl.getBlock()[13].getValue().getValue(),
    				cl.getBlock()[14].getValue().getValue(),
    				cl.getBlock()[15].getValue().getValue()});
    	}
    }
    
    /* End Populate Table Methods */
    
    /* Observer Methods */
    
    @Override
    public void update(Observable o, Object obj) {
    	
    	if (obj instanceof IOObject) {
    		
    		IOObject ioObject = (IOObject) obj;
    		
    		if (ioObject.getOpCode().equals(OpCode.IN) 
        			&& ioObject.getDevId().equals(DeviceId.CONSOLE_KEYBOARD)) {
        		runConsoleKeyboardInput();
        		
        	} else if (ioObject.getOpCode().equals(OpCode.OUT)) {
        		runConsolePrinterOutput(ioObject.getRegisterId(), ioObject.getDevId());
        		
        	} else {
        		System.out.println("ERROR: UNKNOWN I/O CODE!");
        	}
    		
    	} else if (obj instanceof Register) {
    		
    		Register MFR = (Register) obj;
    		int machineFaultCode = Integer.parseInt(MFR.getBitValue().getValue(), 2);
    		
    		if (machineFaultCode == 15) {
    			statusLabel.setBackground(Color.GREEN);
    			statusLabel.setText("OK");
    		} else {
    			statusLabel.setBackground(Color.RED);
    			statusLabel.setText("FAULT: CODE " + machineFaultCode);
    		}
    		
    	} else {
    		System.out.println("ERROR: UNKNOWN OBSERVABLE SOURCE!");
    	}
    }
    
    private void runConsoleKeyboardInput() {
    	
    	System.out.println("Ready to receive user input.");
    	
    	consoleKeyboardInput.setEnabled(true);
    	consoleKeyboardInput.requestFocusInWindow();
    	
    }
    
    private void runConsolePrinterOutput(int registerId, String devId) {
    	
    	System.out.println("Outputting to console printer.");
    	
    	String registerValue = cpu.getR(registerId).getBitValue().getValue();
    	
    	if (devId.equals(DeviceId.CONSOLE_PRINTER)) {
    		
    		if (registerValue.equals(BitWord.VALUE_ENTER)) {
    			// Print nothing for Enter key
    		} else if (registerValue.equals(BitWord.VALUE_NEWLINE)) {
    			consolePrinterOutput.append("\n");
    		} else {
    			consolePrinterOutput.append(Integer.parseInt(registerValue, 2) + "");
    		}
    		
    	} else if (devId.equals(DeviceId.CONSOLE_PRINTER_ASCII)){
    		consolePrinterOutput.append(DataConversion.binaryToText(registerValue));
    	} else {
    		System.out.println("ERROR: UNKNOWN DEVICE ID");
    	}
    }
    
    /* End Observer Methods */
    
    /* ActionListener Methods */
    
    @Override
    public void actionPerformed(ActionEvent ae) {
    	
    	Object src = ae.getSource();
    	int index = -1;
    	
    	// check if a toggle button was clicked
    	for (int i = 0; i < INSTRUCTION_SIZE; i++) {
    		if (src == toggles[i]) {
    			index = i;
    			break;
    		}
    	}
    	
    	if (index > -1 ) { // if a toggle button was clicked
    		if (toggles[index].isSelected()) {
    			indicators[index].setBackground(Color.GREEN);
    			indicators[index].setText("1");
    		} else {
    			indicators[index].setBackground(Color.RED);
    			indicators[index].setText("0");
    		}
    	} 
    	else { // else check the other buttons
    		if (src == iplButton) {
        		runIpl();
    		} else if (src == loadFileButton) {
    			runLoadFile();
    		} else if (src == runButton) {
    			runRun();
    		} else if (src == haltButton) {
    			runHalt();
        	} else if (src == instructionLoadButton) {
        		runInstructionLoad();
        	} else if (src == singleStepButton) {
        		runSingleStep();
        	} else if (src == refreshTablesButton) {
        		populateAllTables();
        	} else if (src == clearConsolePrinterButton) {
        		clearConsolePrinter();
        	} else {
        		System.out.println("ERROR: UNKNOWN EVENT SOURCE!");
        	}
    	}	
    }
    
    private void runIpl() {
    	
    	System.out.println("IPL BUTTON CLICKED!");
		
    	cpu.loadROM();
    	sleep(1000);
    	populateAllTables();
    }
    
    private void runLoadFile() {
    	
    	System.out.println("LOAD FILE BUTTON CLICKED");
    	
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
    	fileChooser.setFileFilter(filter);
    	int returnVal = fileChooser.showOpenDialog(this);
    	
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		String fileName = fileChooser.getSelectedFile().getName();
    		cpu.loadFromFile(fileName);
    		populateAllTables();
    	}
    }
    
    private void runRun() {
    	
    	System.out.println("RUN BUTTON CLICKED");
    	cpu.runThroughMemory();
    	sleep(1000);
    	populateAllTables();
    }
    
    private void runHalt() {
    	
    	System.out.println("HALT BUTTON CLICKED!");
    	
    	haltButtonClicked = true;
    	consoleKeyboardInput.setEnabled(false);
    }
    
    private void runInstructionLoad() {
    	
    	System.out.println("INSTRUCTION LOAD BUTTON CLICKED!");
		System.out.println(getInstructionWord());
		
		cpu.loadToggleInstruction(getInstructionWord());
		populateAllTables();
		//System.out.println(cpu.getMemory().get(MemoryLocation.RESERVED_ADDRESS_TOGGLE_INSTRUCTION).getValue().getValue());
    }
    
    private void runSingleStep() {
    	
    	System.out.println("SINGLE STEP BUTTON CLICKED!");
		System.out.println(getPcInput());
		
		String address = getPcInput();
		
		try {
			Integer.parseInt(address, 2);
		}
		catch(NumberFormatException nfe) {
			System.out.println("Error: Enter valid digits only");
			return;
		}
		
		if (MemoryLocation.isAddressReserved(address)) {
			System.out.println("Error: Address is reserved");
		}
		else if (Integer.parseInt(address, 2) > 2047) {
			System.out.println("Error: Address is beyond 2048");
		}
		else {
			cpu.getPC().setBitValue(getPcInput());
			cpu.singleStep();
			sleep(200);
			
			populatePcInput();
		}
		
		populateAllTables();
    }
    
    private void sleep(int sleepTime) {    	
    	try{
    		Thread.sleep(sleepTime);
    	}
    	catch(InterruptedException ie) {
    		System.out.println("could not sleep");
    	}
    }
    
    /* End ActionListener Methods */
    
    /* KeyListener Methods */
    
    @Override
    public void keyReleased(KeyEvent ke) {
    	
    	int keyCode = ke.getKeyCode();
    	
    	switch (keyCode) {
    	
	    	case KeyEvent.VK_ENTER:
	    		System.out.println("Enter button pressed");
	    		
	    		if (!consoleKeyboardInput.getText().equals("")) {
	    			
	    			int inputInt = 0;
	    			
	    			if (MiniComputer.currentProgram == ProgramCode.PROGRAMONE) {
	    				inputInt = Integer.parseInt(BitWord.VALUE_ENTER, 2);
		    		}
	    			else if (MiniComputer.currentProgram == ProgramCode.PROGRAMTWO){
	    				inputInt = 13; // ASCII for Carriage Return
	    			}
	    			
	    			processKeyClick(inputInt);
		    		clearConsoleKeyboard();
		    		consoleKeyboardInputHolder = "";
		    		
	    		} else {
	    			System.out.println("Keyboard Input is blank!");
	    		}
	    		break;
	    		
	    	case KeyEvent.VK_0:
	    	case KeyEvent.VK_NUMPAD0:
	    		System.out.println("Input received: 0");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(0);
	    		break;
	    	case KeyEvent.VK_1:
	    	case KeyEvent.VK_NUMPAD1:
	    		System.out.println("Input received: 1");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(1);
	    		break;
	    	case KeyEvent.VK_2:
	    	case KeyEvent.VK_NUMPAD2:
	    		System.out.println("Input received: 2");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(2);
	    		break;
	    	case KeyEvent.VK_3:
	    	case KeyEvent.VK_NUMPAD3:
	    		System.out.println("Input received: 3");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(3);
	    		break;
	    	case KeyEvent.VK_4:
	    	case KeyEvent.VK_NUMPAD4:
	    		System.out.println("Input received: 4");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(4);
	    		break;
	    	case KeyEvent.VK_5:
	    	case KeyEvent.VK_NUMPAD5:
	    		System.out.println("Input received: 5");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(5);
	    		break;
	    	case KeyEvent.VK_6:
	    	case KeyEvent.VK_NUMPAD6:
	    		System.out.println("Input received: 6");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(6);
	    		break;
	    	case KeyEvent.VK_7:
	    	case KeyEvent.VK_NUMPAD7:
	    		System.out.println("Input received: 7");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(7);
	    		break;
	    	case KeyEvent.VK_8:
	    	case KeyEvent.VK_NUMPAD8:
	    		System.out.println("Input received: 8");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(8);
	    		break;
	    	case KeyEvent.VK_9:
	    	case KeyEvent.VK_NUMPAD9:
	    		System.out.println("Input received: 9");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		processKeyClick(9);
	    		break;
	    		
	    	default:
	    		if (MiniComputer.currentProgram == ProgramCode.PROGRAMONE) {
	    			consoleKeyboardInput.setText(consoleKeyboardInputHolder);
	    		}
	    		else if (MiniComputer.currentProgram == ProgramCode.PROGRAMTWO){
	    			System.out.println("A character was pressed");
		    		
		    		try {
		    			processKeyClick(keyCode);
		    			
		    		} catch (NumberFormatException nfe) {
		    			System.err.println("NumberFormatException: " + nfe.getMessage());
		    		}
	    		}
	    		break;
    	}
    }
    
    @Override
    public void keyPressed(KeyEvent ke) {}
    
    @Override
    public void keyTyped(KeyEvent ke) {}
    
    private void processKeyClick(int inputInt) {
    	
    	cpu.inProcessing(inputInt);
    	validKeyClicked = true;
    	consoleKeyboardInput.setEnabled(false);
    	populateAllTables();
    }
    
    /* End KeyListener Methods */

    public static void main(String[] args) {

    	SwingUtilities.invokeLater(new Runnable() {
        
            @Override
            public void run() {
            	
                MiniComputerGui gui;
                
				try {
					gui = new MiniComputerGui();
					gui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
    }
}