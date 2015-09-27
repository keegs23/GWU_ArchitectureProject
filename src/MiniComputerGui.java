import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class MiniComputerGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8217933506339143771L;
	private static final int INSTRUCTION_SIZE = 16;
	private static final String[] INSTRUCTION_LIST_CHOICES = {"IR", "R0", "R1", "R2", "R3"};
	private Container mainPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel iplPanel;
	private JPanel instructionPanel;
	private JPanel pcPanel;
	private JPanel registerPanel;
	private JPanel memoryPanel;
	private JLabel[] indicators;
	private JToggleButton[] toggles;
	private JComboBox<String> instructionLoadList;
	private JButton instructionLoadButton;
	private JTextField pcInput;
	private JButton iplButton;
	private JButton singleStepButton;
	private DefaultTableModel registerModel;
	private DefaultTableModel memoryModel;
	private JTable registerTable;
	private JTable memoryTable;

	public MiniComputerGui() {

		mainPanel = getContentPane();
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		iplPanel = new JPanel();
		instructionPanel = new JPanel();
		pcPanel = new JPanel();
		registerPanel = new JPanel();
		memoryPanel = new JPanel();
		indicators = new JLabel[INSTRUCTION_SIZE];
		toggles = new JToggleButton[INSTRUCTION_SIZE];
		instructionLoadList = new JComboBox<String>(INSTRUCTION_LIST_CHOICES);
		instructionLoadButton = new JButton("Load Instruction");
		pcInput = new JTextField(10);
		iplButton = new JButton("IPL");
		singleStepButton = new JButton("Single Step");
		registerModel = new DefaultTableModel();
		memoryModel = new DefaultTableModel();
		registerTable = new JTable(registerModel);
		memoryTable = new JTable(memoryModel);
		
		initUI();
    }

    private void initUI() {

    	setTitle("The Mini Computer");
        setSize(1100, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    	setPanelLayouts();
        setPanelBorders();
        
        addToIplPanel();
        addToInstructionPanel();
        addToPcPanel();
        addToRegisterPanel();
        addToMemoryPanel();
        
        initRegisterTable();
        initMemoryTable();
        
        addToLeftPanel();
        addToRightPanel();
        addToMainPanel();
        
    }
    
    private void setPanelLayouts() {
    	
    	mainPanel.setLayout(new GridLayout(1, 2));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        iplPanel.setLayout(new FlowLayout());
        instructionPanel.setLayout(new GridLayout(0, 2));
        pcPanel.setLayout(new FlowLayout());
        registerPanel.setLayout(new FlowLayout());
        memoryPanel.setLayout(new FlowLayout());
    }
    
    private void setPanelBorders() {
    	
    	iplPanel.setBorder(new TitledBorder("Initial Program Load"));
        instructionPanel.setBorder(new TitledBorder("Instructions"));
        pcPanel.setBorder(new TitledBorder("Program Counter"));
        registerPanel.setBorder(new TitledBorder("Registers"));
        memoryPanel.setBorder(new TitledBorder("Memory in Use"));
    }
    
    private void addToIplPanel() {
    	
    	iplButton.addActionListener(this);
        iplPanel.add(iplButton);
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
        instructionPanel.add(instructionLoadList);
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
    
    private void initRegisterTable() {
    	
    	registerModel.setRowCount(0);
    	registerModel.addColumn("Register");
        registerModel.addColumn("Value (base-2)");
        registerModel.addRow(new Object[]{"R0", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"R1", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"R2", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"R3", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"X1", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"X2", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"X3", "0000 0000 0000 0000"});
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
    
    private void addToRightPanel() {
    	
    	rightPanel.add(registerPanel);
        rightPanel.add(memoryPanel);
    }
    
    private void addToMainPanel() {
    	
    	mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
    }
    
    /**** public methods ****/
    
    public String getPcInput() {
    	return pcInput.getText();
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
    
    public String getInstructionLoadChoice() {
    	return instructionLoadList.getSelectedItem().toString();
    }
    
    public void populateRegisterRow(String registerName, Register register) {
    	
    	for (int i = 0; i < registerModel.getRowCount(); i++) {
    		if (registerModel.getValueAt(i, 0).equals(registerName)) {
    			registerModel.setValueAt(register.getBitValue().getValue(), i, 1);
    			break;
    		}
    	}
    }
    
    public void populateRegisterTable(Register R0, Register R1, Register R2, Register R3, Register X1, Register X2, Register X3) {
    	
    	registerModel.setRowCount(0);
        registerModel.addRow(new Object[]{"R0", R0.getBitValue().getValue()});
        registerModel.addRow(new Object[]{"R1", R1.getBitValue().getValue()});
        registerModel.addRow(new Object[]{"R2", R2.getBitValue().getValue()});
        registerModel.addRow(new Object[]{"R3", R3.getBitValue().getValue()});
        registerModel.addRow(new Object[]{"X1", X1.getBitValue().getValue()});
        registerModel.addRow(new Object[]{"X2", X2.getBitValue().getValue()});
        registerModel.addRow(new Object[]{"X3", X3.getBitValue().getValue()});
    }
    
    public void populateMemoryTable(HashMap<String, MemoryLocation> memoryMap) {
    	
    	memoryModel.setRowCount(0);
        
    	for (Map.Entry<String, MemoryLocation> m : memoryMap.entrySet()) {
    		
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
        	} else if (src == instructionLoadButton) {
        		runInstructionLoad();
        	} else if (src == singleStepButton) {
        		runSingleStep();
        	} else {
        		System.out.println("SOMETHING WENT WRONG!");
        	}
    	}	
    }
    
    public void runIpl() {
    	
    	System.out.println("IPL BUTTON CLICKED!");
		// TODO
    }
    
    public void runInstructionLoad() {
    	
    	System.out.println("INSTRUCTION LOAD BUTTON CLICKED!");
		System.out.println(getInstructionWord());
		System.out.println(getInstructionLoadChoice());
		
		String selected = getInstructionLoadChoice();
		
		if (selected.equals("IR")) {
			// TODO
		} else if (selected.equals("R0")) {
			
		} else if (selected.equals("R1")) {
			
		} else if (selected.equals("R2")) {
			
		} else if (selected.equals("R3")) {
			
		} else {
			
		}
    }
    
    public void runSingleStep() {
    	
    	System.out.println("SINGLE STEP BUTTON CLICKED!");
		System.out.println(getPcInput());
		// TODO
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