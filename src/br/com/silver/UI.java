package br.com.silver;

import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
import java.awt.GridLayout;

public class UI implements IReaderCSV{

	private JFrame frame;
	private JPanel panelTop;
	private JButton btnFile;
	private JButton btnImport;
	private JTextField txtFile;
	private JFileChooser fileChooser;
	private File file;	
	private ConnectionFactory repository;
	private CounterResult counter;
	private Ini ini;
	private ClientDao clientDao;
	private FinanceDao financeDao;
	private JTextArea txtLog;
	private JTextArea txtResult;
	public static String NOT_PAID = " ($)";
	public static String MISSING_DATA = " (!)";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
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
		frame.setBounds(100, 100, 650, 300);
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
		txtFile.setColumns(40);
		txtFile.setPreferredSize( new Dimension(0, 26) );
		panelTop.add(txtFile);
		
		// Button Import
		btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startImport();
			}
		});
		panelTop.add(btnImport);

		frame.getContentPane().add(panelLog(), BorderLayout.CENTER);
		
		// File Chooser
		fileChooser = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv", "csv");
		fileChooser.setFileFilter(filter);
	    fileChooser.setControlButtonsAreShown(false);
	}
	
	/**
	 * Panel Log
	 * @return JPanel
	 */
	private JPanel panelLog() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		
		txtLog = new JTextArea();
		txtLog.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtLog.setForeground(new Color(50, 205, 50));
		txtLog.setBackground(new Color(30, 30, 30));
		txtLog.setMargin( new Insets(10, 10, 10, 10) );
		JScrollPane scroll1 = new JScrollPane(txtLog);
		
		txtResult = new JTextArea();
		txtResult.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtResult.setForeground(new Color(50, 205, 50));
		txtResult.setBackground(new Color(30, 30, 30));
		txtResult.setMargin( new Insets(10, 10, 10, 10) );
		JScrollPane scroll2 = new JScrollPane(txtResult);
		
		
		panel.add(scroll1);
		panel.add(scroll2);
		return panel;
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
			ReaderCSV.reader(file, this);
		}
	}
	
	/**
	 * Print log in terminal
	 * @param log
	 */
	public void log(String log) {
		SwingUtilities.invokeLater(new Runnable() {
   		 	public void run() {
   		 		if(log != null) {
   		 			txtLog.append(String.format("%s\n", log));
   		 		}
   		 	}
		});
	}
	
	/**
	 * Print result in terminal
	 * @param result
	 */
	public void logResult(String result) {
		SwingUtilities.invokeLater(new Runnable() {
   		 	public void run() {
   		 		if(result != null) {
   		 			txtResult.setText(String.format("%s\n", result));
   		 		}
   		 	}
		});
	}
	
	@Override
	public void lineReady(ArrayList<String> data) {

		this.counter.setTotal();

		String cpf = data.get(Integer.parseInt(this.ini.get("csv","cpf")));		
		Client client = this.clientDao.getByCpf(cpf);

		if(client == null) {
			log("Client not found - CPF " + cpf);
			this.counter.setNotFound();
		} else {
			updateClient(client, data);
		}
		
		logResult(this.counter.toString());
	}
	
	/**
	 * Update client
	 * @param client
	 * @param data
	 */
	public void updateClient(Client client, ArrayList<String> data) {
		String situacao = data.get(Integer.parseInt(this.ini.get("csv","situacao")));
		String missing = data.get(Integer.parseInt(this.ini.get("csv","missing")));
		String vc = data.get(Integer.parseInt(this.ini.get("csv","vc")));
		client.setVc(vc);
		
		if(this.financeDao.isPaid(client)) {
			this.counter.setNotUpdated();
		} else {
			if(situacao.contains("N")) {
				client.setName(client.getName() + NOT_PAID);
			}
		}
		
		if(missing.contains("I")) {
			client.setName(client.getName() + MISSING_DATA);
		}

		String error = clientDao.update(client);
		
		if(error == null) {
			this.counter.setClient();
		} 

		log(error);
	}
	
}
