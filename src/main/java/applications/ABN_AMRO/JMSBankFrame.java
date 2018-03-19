package applications.ABN_AMRO;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import correlation.CorrelationManager;
import models.bank.*;
import models.messaging.RequestReply;
import services.GenericConsumer;
import services.GenericProducer;

public class JMSBankFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField tfReply;
	private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				JMSBankFrame frame = new JMSBankFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private JMSBankFrame() {
		setTitle("ABN AMRO");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		JList<RequestReply<BankInterestRequest, BankInterestReply>> list = new JList<>(listModel);
		scrollPane.setViewportView(list);
		
		JLabel lblNewLabel = new JLabel("type reply");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		tfReply = new JTextField();
		GridBagConstraints gbc_tfReply = new GridBagConstraints();
		gbc_tfReply.gridwidth = 2;
		gbc_tfReply.insets = new Insets(0, 0, 0, 5);
		gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfReply.gridx = 1;
		gbc_tfReply.gridy = 1;
		contentPane.add(tfReply, gbc_tfReply);
		tfReply.setColumns(10);
		
		JButton btnSendReply = new JButton("send reply");
		btnSendReply.addActionListener((ActionEvent e) -> {
			RequestReply<BankInterestRequest, BankInterestReply> rr = list.getSelectedValue();
			double interest = Double.parseDouble((tfReply.getText()));
			BankInterestReply reply = new BankInterestReply(interest,"ABN AMRO");
			if (rr!= null) {
				rr.setReply(reply);
				list.repaint();

				// Pass correlation id
				CorrelationManager.correlate(rr.getRequest(), reply);

				// Start producing
				GenericProducer.getInstance().produce(reply, "interestReply");
			}
		});
		GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
		gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSendReply.gridx = 4;
		gbc_btnSendReply.gridy = 1;
		contentPane.add(btnSendReply, gbc_btnSendReply);

		// Start consuming
		initConsumers();
	}

	private void add(BankInterestRequest loanRequest){
		listModel.addElement(new RequestReply<>(loanRequest, null));
	}

	private void initConsumers() {
		GenericConsumer genericConsumer = GenericConsumer.getInstance();
		genericConsumer.consume("interestRequest", new DefaultConsumer(genericConsumer.getChannel()) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");

				Gson gson = new Gson();
				BankInterestRequest bankInterestRequest = gson.fromJson(message, BankInterestRequest.class);

				add(bankInterestRequest);
			}
		});
	}
}
