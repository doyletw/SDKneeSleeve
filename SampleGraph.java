import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;

public class SampleGraph {

	static SerialPort chosenPort;
	static int x = 0;

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
		window.add(topPanel, BorderLayout.NORTH);
		
		
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
										series1.add(x++, number);
										window.repaint();
									} else if (line.equals("SS2")) {
										line = scanner.nextLine();
										int number = Integer.parseInt(line);
										series2.add(x++, number);
										window.repaint();
									} else if (line.equals("SS3")) {
										line = scanner.nextLine();
										int number = Integer.parseInt(line);
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
