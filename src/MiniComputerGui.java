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
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import javax.swing.table.DefaultTableModel;

public class MiniComputerGui extends JFrame implements ActionListener, Observer, KeyListener {

	protected static volatile boolean enterKeyClicked = false;
	private static final long serialVersionUID = -8217933506339143771L;
	private static final int INSTRUCTION_SIZE = 16;	
	private MiniComputer cpu;
	private Container mainPanel;
	private JPanel leftPanel;
	private JPanel centerPanel;
	private JPanel rightPanel;
	private JPanel iplPanel;
	private JPanel instructionPanel;
	private JPanel pcPanel;
	private JPanel registerPanel;
	private JPanel memoryPanel;
	private JPanel consolePrinterPanel;
	private JPanel consoleKeyboardPanel;
	private JLabel[] indicators;
	private JToggleButton[] toggles;
	private JButton instructionLoadButton;
	private JTextField pcInput;
	private JButton iplButton;
	private JButton haltButton;
	private JButton singleStepButton;
	private DefaultTableModel registerModel;
	private DefaultTableModel memoryModel;
	private JTable registerTable;
	private JTable memoryTable;
	private JTextArea consolePrinterOutput;
	private JTextField consoleKeyboardInput;
	private JButton clearConsolePrinterButton;
	private String consoleKeyboardInputHolder;

	public MiniComputerGui() throws FileNotFoundException {

		cpu = new MiniComputer();
		mainPanel = getContentPane();
		leftPanel = new JPanel();
		centerPanel = new JPanel();
		rightPanel = new JPanel();
		iplPanel = new JPanel();
		instructionPanel = new JPanel();
		pcPanel = new JPanel();
		registerPanel = new JPanel();
		memoryPanel = new JPanel();
		consolePrinterPanel = new JPanel();
		consoleKeyboardPanel = new JPanel();
		indicators = new JLabel[INSTRUCTION_SIZE];
		toggles = new JToggleButton[INSTRUCTION_SIZE];
		instructionLoadButton = new JButton("Load Instruction");
		pcInput = new JTextField(10);
		iplButton = new JButton("IPL");
		haltButton = new JButton("Halt");
		singleStepButton = new JButton("Single Step");
		registerModel = new DefaultTableModel();
		memoryModel = new DefaultTableModel();
		registerTable = new JTable(registerModel);
		memoryTable = new JTable(memoryModel);
		consolePrinterOutput = new JTextArea();
		consoleKeyboardInput = new JTextField(10);
		clearConsolePrinterButton = new JButton("Clear Output Screen");
		consoleKeyboardInputHolder = "";
		
		initUI();
    }

    private void initUI() {

    	final int WINDOW_WIDTH = 1000;
    	final int WINDOW_HEIGHT = 800;
    	
    	setTitle("The Mini Computer");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    	setPanelLayouts();
        setPanelBorders();
        
        addToIplPanel();
        addToInstructionPanel();
        addToPcPanel();
        addToRegisterPanel();
        addToMemoryPanel();
        addToConsolePrinterPanel();
        addToConsoleKeyboardPanel();
        
        initRegisterTable();
        initMemoryTable();
        
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
        iplPanel.setLayout(new FlowLayout());
        instructionPanel.setLayout(new GridLayout(0, 2));
        pcPanel.setLayout(new FlowLayout());
        registerPanel.setLayout(new BorderLayout());
        memoryPanel.setLayout(new BorderLayout());
        consolePrinterPanel.setLayout(new BorderLayout());
        consoleKeyboardPanel.setLayout(new FlowLayout());
    }
    
    private void setPanelBorders() {
    	
    	iplPanel.setBorder(new TitledBorder("Initial Program Load"));
        instructionPanel.setBorder(new TitledBorder("Instructions"));
        pcPanel.setBorder(new TitledBorder("Program Counter"));
        registerPanel.setBorder(new TitledBorder("Registers"));
        memoryPanel.setBorder(new TitledBorder("Memory in Use"));
        consolePrinterPanel.setBorder(new TitledBorder("Console Printer"));
        consoleKeyboardPanel.setBorder(new TitledBorder("Console Keyboard"));
    }
    
    private void addToIplPanel() {
    	
    	final int BUTTON_WIDTH = 150;
    	final int BUTTON_HEIGHT = 75;
    	final int FONT_SIZE = 30;
    	
    	iplButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    	iplButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
    	iplButton.addActionListener(this);
        iplPanel.add(iplButton);
        
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
        registerPanel.add(new JScrollPane(registerTable));
    }
    
    private void addToMemoryPanel() {
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
    	consoleKeyboardPanel.add(consoleKeyboardInput);
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
        memoryModel.addRow(new Object[]{"", ""});
    }
    
    private void addToLeftPanel() {
    	
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
    }
    
    private void addToMainPanel() {
    	
    	mainPanel.add(leftPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(rightPanel);
    }
    
    /**** public methods ****/
    
    public String getPcInput() {
    	return pcInput.getText();
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
    
    public void populateRegisterRow(String registerName, Register register) {
    	
    	for (int i = 0; i < registerModel.getRowCount(); i++) {
    		if (registerModel.getValueAt(i, 0).equals(registerName)) {
    			registerModel.setValueAt(register.getBitValue().getValue(), i, 1);
    			break;
    		}
    	}
    }
    
    public void populateRegisterTable() {
    	
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
    
    public void populateMemoryTable() {
    	
    	memoryModel.setRowCount(0);
        
    	for (Map.Entry<String, MemoryLocation> m : cpu.getMemory().entrySet()) {
    		
    		String address = m.getValue().getAddress().getValue();
    		String value = m.getValue().getValue().getValue();
    		
    		memoryModel.addRow(new Object[]{address, value});
    	}
    }
    
    /* Observer Methods */
    
    @Override
    public void update(Observable o, Object io) {
    	
    	IOObject ioObject = null;
    	
    	try {
    		ioObject = (IOObject) io;
    	}
    	catch (Exception e) {
    		System.err.println("Exception: " + e.getMessage());
    	}
    	
    	if (ioObject.getOpCode().equals(OpCode.IN) 
    			&& ioObject.getDevId().equals(DeviceId.CONSOLE_KEYBOARD)) {
    		runConsoleKeyboardInput();
    		
    	} else if (ioObject.getOpCode().equals(OpCode.OUT) 
    			&& ioObject.getDevId().equals(DeviceId.CONSOLE_PRINTER)) {
    		runConsolePrinterOutput(ioObject.getRegisterId());
    		
    	} else {
    		System.out.println("ERROR: UNKNOWN OBSERVABLE SOURCE!");
    	}
    }
    
    private void runConsoleKeyboardInput() {
    	
    	System.out.println("Inputting from console keyboard.");
    	
    	consoleKeyboardInput.setEnabled(true);
    	
    }
    
    private void runConsolePrinterOutput(int registerId) {
    	
    	System.out.println("Outputting to console printer.");
    	
    	String registerValue = cpu.getR(registerId).getBitValue().getValue();
    	
		consolePrinterOutput.append(DataConversion.binaryToText(registerValue));
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
    		} else if (src == haltButton) {
    			runHalt();
        	} else if (src == instructionLoadButton) {
        		runInstructionLoad();
        	} else if (src == singleStepButton) {
        		runSingleStep();
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
    	populateRegisterTable();
    	populateMemoryTable();
    }
    
    private void runHalt() {
    	
    	System.out.println("HALT BUTTON CLICKED!");
    	
    	//TODO
    }
    
    private void runInstructionLoad() {
    	
    	System.out.println("INSTRUCTION LOAD BUTTON CLICKED!");
		System.out.println(getInstructionWord());
		
		cpu.loadToggleInstruction(getInstructionWord());
		populateMemoryTable();
		//System.out.println(cpu.getMemory().get(MemoryLocation.RESERVED_ADDRESS_TOGGLE_INSTRUCTION).getValue().getValue());
    }
    
    private void runSingleStep() {
    	
    	System.out.println("SINGLE STEP BUTTON CLICKED!");
		System.out.println(getPcInput());
		
		cpu.getPC().setBitValue(getPcInput());
		cpu.singleStep();
		
		populateRegisterTable();
		populateMemoryTable();
		populatePcInput();
    }
    
    /* End ActionListener Methods */
    
    /* KeyListener Methods */
    
    @Override
    public void keyReleased(KeyEvent ke) {
    	
    	int keyCode = ke.getKeyCode();
    	
    	switch (keyCode) {
	    	case KeyEvent.VK_ENTER:
	    		System.out.println("Enter button pressed");
	    		
	    		if (!consoleKeyboardInput.equals("")) {
	    			enterKeyClicked = true;
		    		consoleKeyboardInput.setEnabled(false);
		    		clearConsoleKeyboard();
		    		consoleKeyboardInputHolder = "";
		    		populateRegisterTable();
		    		populateMemoryTable();
	    		} else {
	    			System.out.println("Keyboard Input is blank!");
	    		}
	    		
	    		break;
	    		
	    	case KeyEvent.VK_BACK_SPACE:
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		break;
	    		
	    	case KeyEvent.VK_0:
	    	case KeyEvent.VK_1:
	    	case KeyEvent.VK_2:
	    	case KeyEvent.VK_3:
	    	case KeyEvent.VK_4:
	    	case KeyEvent.VK_5:
	    	case KeyEvent.VK_6:
	    	case KeyEvent.VK_7:
	    	case KeyEvent.VK_8:
	    	case KeyEvent.VK_9:
	    		System.out.println("A number was pressed");
	    		consoleKeyboardInputHolder = consoleKeyboardInput.getText();
	    		
	    		try {
	    			int inputInt = Integer.parseInt(consoleKeyboardInputHolder);
	    			cpu.inProcessing(inputInt);
	    		} catch (NumberFormatException nfe) {
	    			System.err.println("NumberFormatException: " + nfe.getMessage());
	    		}
	    		break;
	    		
	    	default:
	    		consoleKeyboardInput.setText(consoleKeyboardInputHolder);
	    		break;
    	}
    	
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {}
    
    @Override
    public void keyPressed(KeyEvent ke) {}
    
    /* End KeyListener Methods */

    public static void main(String[] args) {

    	SwingUtilities.invokeLater(new Runnable() {
        
            @Override
            public void run() {
            	
                MiniComputerGui gui;
                
				try {
					gui = new MiniComputerGui();
					gui.setVisible(true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
            }
        });
    }
}