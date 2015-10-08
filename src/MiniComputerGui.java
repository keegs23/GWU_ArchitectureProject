import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

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

public class MiniComputerGui extends JFrame implements ActionListener {

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
	private JTextArea consoleKeyboardInput;

	public MiniComputerGui() {

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
		consoleKeyboardInput = new JTextArea();
		
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
        consoleKeyboardPanel.setLayout(new BorderLayout());
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
    	consolePrinterPanel.add(new JScrollPane(consolePrinterOutput));
    }
    
    private void addToConsoleKeyboardPanel() {
    	
    	consoleKeyboardInput.setLineWrap(true);
    	consoleKeyboardInput.setWrapStyleWord(true);
    	consoleKeyboardPanel.add(new JScrollPane(consoleKeyboardInput));
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
        registerModel.addRow(new Object[]{"IRR", SIXTEEN_ZEROS});
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
        registerModel.addRow(new Object[]{"IRR", cpu.getIRR().getBitValue().getValue()});
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
    	
    	//Do something
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

    public static void main(String[] args) {

    	SwingUtilities.invokeLater(new Runnable() {
        
            @Override
            public void run() {
                MiniComputerGui gui = new MiniComputerGui();
                gui.setVisible(true);
            }
        });
    }
}