package br.com.silver;

import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ini4j.Ini;

import br.com.silver.dao.ClientDao;
import br.com.silver.dao.FinanceDao;
import br.com.silver.model.Client;
import br.com.silver.repository.ConnectionFactory;
import br.com.silver.utils.*;

import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
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
	private File file;	
	private ConnectionFactory repository;
	private CounterResult counter;
	private Ini ini;
	private ClientDao clientDao;
	private FinanceDao financeDao;
	
	public static String NOT_PAID = " ($)";
	public static String MISSING_DATA = " (!)";

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
		
		try {
			Config conf = new Config();
			this.ini = conf.getConfig();
			this.repository = ConnectionFactory.getInstance(this.ini);
			String statusConn = this.repository.isConnected() ? "connected" : "disconnected";
			log("Database status: " + statusConn);
			
			this.clientDao = new ClientDao(this.repository);
			this.financeDao = new FinanceDao(this.repository);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.counter = new CounterResult();
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
				startImport();
			}
		});
		panelTop.add(btnImport);
		
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
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv", "csv");
		fileChooser.setFileFilter(filter);
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
	 * Start import
	 */
	private void startImport() {
		if(this.file != null) {
			txtLog.setText(null);
			ReaderCSV rc = new ReaderCSV(this);
	    	rc.readCSV(this.file);
	    	
	    	log(this.counter.toString());
		}
	}
	
	/**
	 * Print log in terminal
	 * @param log
	 */
	private void log(final String log) {
		EventQueue.invokeLater(new Runnable() {
   		 	public void run() {
   		 		if(log != null)
     			    txtLog.append(String.format("%s\n", log));
   		 	}
		});
	}
	
	@Override
	public void lineReady(ArrayList<String> data) {
		// Count total
		this.counter.setTotal();

		String cpf = data.get(Integer.parseInt(this.ini.get("csv","cpf")));
		Client client = this.clientDao.getByCpf(cpf);

		
		if(client == null) {
			log("Client not found - CPF " + cpf);
			this.counter.setNotFound();
			return;
		}
		
		boolean paid = this.financeDao.isPaid(client);
		
		if(paid) {
			this.counter.setNotUpdated();
		} else {
			client.setName(client.getName() + NOT_PAID);
		}
		
		String vc = data.get(Integer.parseInt(this.ini.get("csv","vc")));
		String missing = data.get(Integer.parseInt(this.ini.get("csv","missing")));
		
		if(missing.contains("I")) {
			client.setName(client.getName() + MISSING_DATA);
		}
		
		client.setVc(vc);

		String error = clientDao.update(client);
		log(error);
		
		if(error == null) {
			this.counter.setClient();
		}
		
	}
	
}
