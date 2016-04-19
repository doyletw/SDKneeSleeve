import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;

public class SampleGraph {

	static SerialPort chosenPort;
	static int x = 0;
	static int maxVal1 = Integer.MIN_VALUE;
	static int maxVal2 = Integer.MIN_VALUE;
	static int maxVal3 = Integer.MIN_VALUE;
	static int minVal1 = Integer.MAX_VALUE;
	static int minVal2 = Integer.MAX_VALUE;
	static int minVal3 = Integer.MAX_VALUE;

	public static void main(String[] args) {

		// create a window
		JFrame window = new JFrame();
		window.setTitle("SampleGraph Window");
		window.setSize(600,400);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create a drop-down and connect button
		JComboBox<String> portList = new JComboBox<String>();
		JButton connect = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connect);
		
		// create max value fields
		JTextField maxField1 = new JTextField(10);
		JTextField maxField2 = new JTextField(10);
		JTextField maxField3 = new JTextField(10);
		maxField1.setSize(500, 60);
		maxField2.setSize(500, 60);
		maxField3.setSize(500, 60);
		maxField1.setBackground(Color.WHITE);
		maxField2.setBackground(Color.WHITE);
		maxField3.setBackground(Color.WHITE);
		maxField1.setEditable(false);
		maxField2.setEditable(false);
		maxField3.setEditable(false);
		
		// create max value labels
		JLabel maxLabel1 = new JLabel("Max Value (Sensor 1): ");
		JLabel maxLabel2 = new JLabel("Max Value (Sensor 2): ");
		JLabel maxLabel3 = new JLabel("Max Value (Sensor 3): ");
		
		// create min value fields
		JTextField minField1 = new JTextField(10);
		JTextField minField2 = new JTextField(10);
		JTextField minField3 = new JTextField(10);
		minField1.setSize(500, 60);
		minField2.setSize(500, 60);
		minField3.setSize(500, 60);
		minField1.setBackground(Color.WHITE);
		minField2.setBackground(Color.WHITE);
		minField3.setBackground(Color.WHITE);
		minField1.setEditable(false);
		minField2.setEditable(false);
		minField3.setEditable(false);
		
		// create min value labels
		JLabel minLabel1 = new JLabel("Min Value (Sensor 1): ");
		JLabel minLabel2 = new JLabel("Min Value (Sensor 2): ");
		JLabel minLabel3 = new JLabel("Min Value (Sensor 3): ");
		
		// create panels
		JPanel calcPanel = new JPanel(new GridLayout(2,1));
		JPanel maxPanel = new JPanel();
		JPanel minPanel = new JPanel();
		maxPanel.add(maxLabel1);
		maxPanel.add(maxField1);		
		maxPanel.add(maxLabel2);
		maxPanel.add(maxField2);
		maxPanel.add(maxLabel3);
		maxPanel.add(maxField3);
		
		minPanel.add(minLabel1);
		minPanel.add(minField1);		
		minPanel.add(minLabel2);
		minPanel.add(minField2);
		minPanel.add(minLabel3);
		minPanel.add(minField3);
		
		calcPanel.add(maxPanel);
		calcPanel.add(minPanel);

		
		// add panels to window
		window.add(topPanel, BorderLayout.NORTH);
		window.add(calcPanel, BorderLayout.SOUTH);
		
		// window.pack();
		
		
		//drop down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		for (int i = 0; i < portNames.length; i++) {
			portList.addItem(portNames[i].getSystemPortName());		
		}
		
		// create line graph
		XYSeries series1 = new XYSeries("Sensor1 Reading");
		XYSeries series2 = new XYSeries("Sensor2 Reading");
		XYSeries series3 = new XYSeries("Sensor3 Reading");
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series1);
		data.addSeries(series2);
		data.addSeries(series3);
		JFreeChart chart = ChartFactory.createXYLineChart("Sample Graph", "Time [samples]", "Volts [mV]", data);
		window.add(new ChartPanel(chart), BorderLayout.CENTER);
		
		
		// connect button
		connect.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				if (connect.getText().equals("Connect")) {
					// connect serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if (chosenPort.openPort()) {
						connect.setText("Disconnect");
						portList.setEnabled(false);
					}
					
					//create new thread that listens for data
					Thread thread = new Thread() {
						@Override public void run() {
							Scanner scanner = new Scanner(chosenPort.getInputStream());
							while (scanner.hasNextLine()) {
								try {
									String line = scanner.nextLine();
									if (line.equals("SS1")) {
										line = scanner.nextLine();
										int number = Integer.parseInt(line);
										if (number > maxVal1) {
											maxVal1 = number;
											maxField1.setText(Integer.toString(maxVal1));
										} 
										if (number < minVal1) {
											minVal1 = number;
											minField1.setText(Integer.toString(minVal1));
										}
										series1.add(x++, number);
										window.repaint();
									} else if (line.equals("SS2")) {
										line = scanner.nextLine();
										int number = Integer.parseInt(line);
										if (number > maxVal2) {
											maxVal2 = number;
											maxField2.setText(Integer.toString(maxVal2));
										}
										if (number < minVal2) {
											minVal2 = number;
											minField2.setText(Integer.toString(minVal2));
										}
										series2.add(x++, number);
										window.repaint();
									} else if (line.equals("SS3")) {
										line = scanner.nextLine();
										int number = Integer.parseInt(line);
										if (number > maxVal3) {
											maxVal3 = number;
											maxField3.setText(Integer.toString(maxVal3));
										}
										if (number < minVal3) {
											minVal3 = number;
											minField3.setText(Integer.toString(minVal3));
										}
										series3.add(x++, number);
										window.repaint();
									}
									
								} catch(Exception e) {
									// Potential error message will go here
								}
							}
							scanner.close();
						}
					};
					thread.start();
				} else {
					// disconnect
					chosenPort.closePort();
					portList.setEnabled(true);
					connect.setText("Connect");
					
					// if wishing to reset data
					// series.clear();
					// x = 0;
					
				}
				
			}
		});
		
		
		// show the window
		window.setVisible(true);
		
	}

}
