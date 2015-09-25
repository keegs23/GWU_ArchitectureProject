import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class MiniComputerGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8217933506339143771L;
	private static final int INSTRUCTION_SIZE = 16;
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
    private JTextField pcInput;
    private JButton iplButton;
    private JButton singleStepButton;

	public MiniComputerGui() {

		mainPanel = 		getContentPane();
		leftPanel = 		new JPanel();
		rightPanel = 		new JPanel();
		iplPanel = 			new JPanel();
		instructionPanel = 	new JPanel();
		pcPanel = 			new JPanel();
		registerPanel = 	new JPanel();
		memoryPanel = 		new JPanel();
		indicators = 		new JLabel[INSTRUCTION_SIZE];
		toggles = 			new JToggleButton[INSTRUCTION_SIZE];
		pcInput = 			new JTextField(10);
		iplButton = 		new JButton("IPL");
		singleStepButton = 	new JButton("Single Step");
		
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
        	indicators[i] = new JLabel();
        	toggles[i] = new JToggleButton(i + "");
        	
        	indicators[i].setOpaque(true);
        	indicators[i].setBackground(Color.RED);
        	toggles[i].addActionListener(this);
        	
        	instructionPanel.add(indicators[i]);
        	instructionPanel.add(toggles[i]);
        }
    }
    
    private void addToPcPanel() {
    	
    	singleStepButton.addActionListener(this);
        pcPanel.add(new JLabel("PC:"));
        pcPanel.add(pcInput);
        pcPanel.add(singleStepButton);
    }
    
    private void addToRegisterPanel() {
    	
    	DefaultTableModel registerModel = new DefaultTableModel();
        
        registerModel.addColumn("Register");
        registerModel.addColumn("Value (base-2)");
        registerModel.addRow(new Object[]{"R0", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"R1", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"R2", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"R3", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"X1", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"X2", "0000 0000 0000 0000"});
        registerModel.addRow(new Object[]{"X3", "0000 0000 0000 0000"});
        
        JTable registerTable = new JTable(registerModel);
        registerPanel.add(new JScrollPane(registerTable));
    }
    
    private void addToMemoryPanel() {
    	
    	DefaultTableModel memoryModel = new DefaultTableModel();
    	final int MAX_MEMORY = 128;
        
        memoryModel.addColumn("Address (base-10)");
        memoryModel.addColumn("Value (base-2)");
        
        for (int i = 0; i < MAX_MEMORY; i++) {
        	memoryModel.addRow(new Object[]{i, "0000 0000 0000 0000"});
        }
        
        JTable memoryTable = new JTable(memoryModel);
        memoryPanel.add(new JScrollPane(memoryTable));
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
    
    public void setRegisterValue(String register, BitWord value) {
    	// TODO
    }
    
    public void setMemoryValue(String address, BitWord value) {
    	// TODO
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
    	
    	if (index > -1 ) { // if a toggle button was clicked, change the color
    		if (toggles[index].isSelected()) {
    			indicators[index].setBackground(Color.GREEN);
    		} else {
    			indicators[index].setBackground(Color.RED);
    		}
    	} else { // else check the other buttons
    		if (src == iplButton) {
        		System.out.println("IPL BUTTON CLICKED!");
        		// TODO
        	} else if (src == singleStepButton) {
        		System.out.println("SINGLE STEP BUTTON CLICKED!");
        		System.out.println(getPcInput());
        		// TODO
        	} else {
        		System.out.println("SOMETHING WENT WRONG!");
        	}
    	}	
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