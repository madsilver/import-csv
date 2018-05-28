package br.com.silver;

import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import br.com.silver.dao.ClientDao;
import br.com.silver.dao.FinanceDao;
import br.com.silver.model.Client;
import br.com.silver.model.Finance;
import br.com.silver.utils.*;

import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.Font;
import java.awt.SystemColor;

public class UI implements IReaderCSV{

	private JFrame frame;
	private JPanel panelTop;
	private JButton btnFile;
	private JButton btnImport;
	private JTextField txtFile;
	private JTextArea txtLog;
	private JFileChooser fileChooser;
	private JCheckBox chkCPF;
	
	private File file;
	
	private int totalRegister;
	private int totalUpdateClient;
	private int totalUpdateFinance;
	private int totalFail;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI window = new UI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Frame
		frame = new JFrame();
		frame.setTitle("Import Data Sheet");
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Panel Top
		panelTop = new JPanel();
		frame.getContentPane().add(panelTop, BorderLayout.NORTH);
				
		// Button File
		btnFile = new JButton("File");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openFile();
			}
		});
		panelTop.add(btnFile);
		
		// Field File
		txtFile = new JTextField();
		txtFile.setEditable(false);
		panelTop.add(txtFile);
		txtFile.setColumns(40);
		txtFile.setPreferredSize( new Dimension(0, 26) );
		
		// Button Import
		btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startUpdate();
			}
		});
		panelTop.add(btnImport);
		
		// Checkbox CPF
		chkCPF = new JCheckBox("CPF");
 		panelTop.add(chkCPF);
		
		// Field Log
		txtLog = new JTextArea();
		txtLog.setFont(new Font("Dialog", Font.PLAIN, 11));
		txtLog.setForeground(new Color(50, 205, 50));
		txtLog.setBackground(SystemColor.activeCaptionText);
		txtLog.setMargin( new Insets(10, 10, 10, 10) );
		JScrollPane scrollPane = new JScrollPane(txtLog);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		// File Chooser
		fileChooser = new JFileChooser(".");
	    fileChooser.setControlButtonsAreShown(false);
	}
	
	/**
	 * Open csv file
	 */
	private void openFile() {
	    if (fileChooser.showOpenDialog(btnFile) == JFileChooser.APPROVE_OPTION) {
	    	this.file = fileChooser.getSelectedFile();
	    	txtFile.setText(file.getAbsolutePath());
	    }
	}
	
	/**
	 * Start update
	 */
	private void startUpdate() {
		if(this.file != null) {
			txtLog.setText(null);
			resetCount();
			log("Opening file ...\n");
			ReaderCSV rc = new ReaderCSV(this);
	    	rc.readCSV(this.file);
	    	
	    	log("\nDone!\n");
	    	log("Total registers: " + totalRegister);
		    log("Total client updates: " + totalUpdateClient);
		    log("Total finance updates: " + totalUpdateFinance);
		    log("Total fails: " + totalFail);
		}
	}
	
	/**
	 * Print log in terminal
	 * @param log
	 */
	private void log(String log) {
		SwingUtilities.invokeLater(
          new Runnable() {
             public void run()
             {
            	 if(log != null) {
         			txtLog.append(String.format("%s \n", log));
         		}
             }
          });
	}
	
	@Override
	public void afterRead(ArrayList<String> data) {
		Client client = updateClient(data);
		totalRegister++;
		
		if(client != null) {
			totalUpdateClient++;
			if(updateFinance(client, data)) {
				totalUpdateFinance++;
			} else {
				totalFail++;
			}
		} else {
			totalFail++;
		}
		
	}
	
	/**
	 * Update client
	 * @param data
	 * @return Client
	 */
	private Client updateClient(ArrayList<String> data) {
		ClientDao clientDao = new ClientDao();
		Client client = null;
		int id = Integer.parseInt(data.get(0));
		String cpf = data.get(1);
		
		if(chkCPF.isSelected()) {
			client = clientDao.getByCpf(cpf);
		} else {
			client = clientDao.get(id);
		}
		
		if(client == null) {
			if(chkCPF.isSelected()) {
				log("Client not found - CPF " + cpf);
			} else {
				log("Client not found - ID " + id);
			}
			
			return null;
		}
		
		client.setVc(data.get(8));
		String error = clientDao.update(client);
		log(error);
		
		return client;
	}
	
	/**
	 * Update finance
	 * @param client
	 * @param data
	 * @return boolean
	 */
	private boolean updateFinance(Client client, ArrayList<String> data) {
		FinanceDao financeDao = new FinanceDao();
		Finance finance = new Finance();
		finance.setClient(client);
		finance.setStatus(data.get(10));
		
		String error = financeDao.update(finance);
		log(error);
		
		if(error == null) {	
			return false;
		}
		
		return true;
	}
	
	/**
	 * Reset count
	 */
	private void resetCount() {
		totalRegister = 0;
		totalUpdateClient = 0;
		totalUpdateFinance = 0;
		totalFail = 0;
	}
	
}
