/* 
     Copyright 2014 Manuel Deleo. 
     
     This file is part of Java Local Message Exchange.

     Serie A Database is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.

     Java Local Message Exchange is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.

     You should have received a copy of the GNU General Public License
     along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/

package qpid_messaging;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class Main extends JFrame {
	

	
	private static JLabel usernameLabel;
	private static JTextField usernameField;
	private static JLabel txtLabel;
	
	private static JPanel panel1;
	private static JPanel panel2;
	private static JPanel panel3;
	private static JPanel panel4;
	private static JTextField txtField;
	
	private static DefaultListModel<String> model;

	private final static Color foregroundColor = new Color(0, 0, 255);
	private static JButton logButton;
	private static JButton enterButton;


	public Main() {

		final JFrame frame = new JFrame();
		create(frame);
		frame.setTitle("Java Local Message Exchange");
		frame.setLocation(new Point(500, 500));
		frame.setSize(new Dimension(600, 300));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

	}
	
	public static void main(String[] args) {
		new Main();
	}

	private void create(JFrame frame) {
		frame.getContentPane().setLayout(new GridLayout());
		panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		
		usernameLabel = new JLabel();
		usernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		usernameLabel.setForeground(foregroundColor);
		usernameLabel.setText("Username:");
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(usernameLabel);
		panel1.add(p);
		
		usernameField = new JTextField();
		usernameField.setForeground(foregroundColor);
		usernameField.setSelectedTextColor(foregroundColor);
		usernameField.setToolTipText("Enter your username");
		usernameField.setColumns(10);
		p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(usernameField);
		panel1.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(usernameField);
		panel1.add(p);
		
		logButton = new JButton();
		logButton.setForeground(foregroundColor);
		logButton.setText("Login");
		logButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String us = new String(usernameField.getText());
				createChat(us);
			}
		});
		panel1.add(logButton);
		
		
		
		frame.getContentPane().add(panel1);
		
	}
	
	void createChat(final String user) {
		
		panel1.removeAll();
		
		 final WriteRunnable  writeMessage = 
                 new WriteRunnable();
		
		model = new DefaultListModel<String>();
		model.addElement("Hello " + user + "!");
		JList<String> list = new JList<String>(model);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setSize(new Dimension(200,200));
		scrollPane.setViewportView(list);

		GridLayout grid = new GridLayout();
		grid.setHgap(10);
		grid.setVgap(10);
		panel1.setLayout(grid);
		panel1.add(scrollPane);
		
		panel2 = new JPanel();
		panel2.setLayout(new GridLayout(2,1));
		
		panel3 = new JPanel();
		panel3.setLayout(new GridLayout(2,1));
		
		panel4 = new JPanel();
		panel4.setLayout(new FlowLayout());

		txtLabel = new JLabel();
		txtLabel.setHorizontalAlignment(SwingConstants.CENTER);
		txtLabel.setForeground(foregroundColor);
		txtLabel.setText("Enter text:");
		
		panel4.add(txtLabel);
		
		txtField = new JTextField();
		txtField.setForeground(foregroundColor);
		txtField.setSelectedTextColor(foregroundColor);
		txtField.setToolTipText("Enter text");
		txtField.setColumns(15);
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(txtField);
		panel4.add(p,"push, align center");
		
		
		enterButton = new JButton();
		enterButton.setForeground(foregroundColor);
		enterButton.setText("Send");
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = new String(txtField.getText());
				writeMessage.publishMessage(user, text);
			}
		});
		p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(enterButton);
		panel3.add(panel4);
		panel3.add(p);
		panel2.add(panel3);
		
	
		BufferedImage myPicture = null;
		Image pic = null;
		try {
			myPicture = ImageIO.read(new File("C:\\Users\\Delle91\\Pictures\\Qpid-logo2.png"));
			pic = myPicture.getScaledInstance(152, 82, Image.SCALE_SMOOTH);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JLabel picLabel = new JLabel(new ImageIcon(pic));
		panel2.add(picLabel);
		panel1.add(panel2);
		
		panel1.revalidate();
		
		ReadRunnable R1 = new ReadRunnable();
	      R1.start();

	}
	
	class ReadRunnable implements Runnable {
		   private Thread t;
		   private String threadName;
		   
		   ReadRunnable() 
		   {
		   }
		   
		   public void run() {
		      try {
		    	    TopicSubscriber topicSubscriber = null;
		    	    TextListener topicListener = null;

		    	    Properties properties = new Properties();
		    	    properties.load(this.getClass().getResourceAsStream("hello.properties"));
		    	    Context context = new InitialContext(properties);

		    	    TopicConnectionFactory connectionFactory = (TopicConnectionFactory) context.lookup("qpidConnectionfactory");
		    	    TopicConnection connection = connectionFactory.createTopicConnection();
		    	    TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		    	    Topic topic= (Topic) context.lookup("testQueue");

		    	    topicListener = new TextListener();
		    	    topicSubscriber = session.createSubscriber(topic);
		    	    topicSubscriber.setMessageListener(topicListener);
		    	    connection.start();

		    	    while(true) {
		    	    }
		      } catch (Exception e) {
		    	  System.out.println("Thread " +  threadName + " interrupted.");
		      }
		      System.out.println("Thread " +  threadName + " exiting.");
		   }

		   public void start ()
		   {
			   if (t == null)
			   {
				   t = new Thread (this);
				   t.start();
			   }
		   }

		   private class TextListener implements MessageListener {

			   public void onMessage(Message message) {
				   TextMessage  msg = (TextMessage) message;

				   try {
					   model.addElement(msg.getText());

				   } catch (JMSException e) {
					   System.err.println("Exception in " + 
							   "onMessage(): " + e.toString());
				   }

			   }
		   }


	}

	class WriteRunnable {
		TopicPublisher topicPublisher = null;
		TopicSession session = null;
		TopicConnection connection = null;
		Topic topic = null;

		WriteRunnable() 
		{
			TopicConnectionFactory connectionFactory = null;
			try {


				Properties properties = new Properties();
				properties.load(this.getClass().getResourceAsStream("hello.properties"));
				Context context = new InitialContext(properties);


				 connectionFactory = (TopicConnectionFactory) context.lookup("qpidConnectionfactory");
				 connection = connectionFactory.createTopicConnection();
				 session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
				 topic = (Topic) context.lookup("testQueue");

				topicPublisher = 
						session.createPublisher(topic);


			} catch (Exception e) {
                System.err.println("Connection problem: " + 
                        e.toString());
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (JMSException ee) {}
                    }
        	        System.exit(1);
                } 
		}

		public void publishMessage(String user, String text) {
			TextMessage  message = null;
			new String("Here is a message");

			try {
				message = session.createTextMessage();

				message.setText(user + " says: " + text);
				System.out.println("PUBLISHER: Publishing " +
						"message: " + message.getText());
				topicPublisher.publish(message);

			} catch (JMSException e) {
				System.err.println("Exception occurred: " + 
						e.toString());
			}
		}

	}



}
