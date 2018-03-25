package applications.Loan_Broker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import correlation.CorrelationManager;
import models.bank.*;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import message_gateways.GenericConsumer;
import message_gateways.GenericProducer;

public class LoanBrokerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
	private JList<JListLine> list;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				LoanBrokerFrame frame = new LoanBrokerFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private LoanBrokerFrame() {
		setTitle("Loan Broker");
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
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		list = new JList<>(listModel);
		scrollPane.setViewportView(list);

		// Start consuming
		initConsumers();
	}
	
	 private JListLine getRequestReply(LoanRequest request) {
	     for (int i = 0; i < listModel.getSize(); i++) {
	    	 JListLine listLine =listModel.get(i);
	    	 if (listLine.getLoanRequest() == request) {
	    		 return listLine;
	    	 }
	     }
	     
	     return null;
	   }

	private void add(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));		
	}

	private void add(LoanRequest loanRequest, BankInterestRequest bankRequest) {
		JListLine listLine = getRequestReply(loanRequest);
		if (listLine != null && bankRequest != null) {
			listLine.setBankRequest(bankRequest);
            list.repaint();
		}		
	}

	private void add(LoanRequest loanRequest, BankInterestReply bankReply) {
		JListLine listLine = getRequestReply(loanRequest);
		if (listLine != null && bankReply != null) {
			listLine.setBankReply(bankReply);
            list.repaint();
		}		
	}

	private LoanRequest findCorrelatedRequest(String correlationId) {
		for (int i = 0; i < listModel.getSize(); i++) {
			JListLine listLine =listModel.get(i);
			if (listLine.getLoanRequest().getCorrelationId().equals(correlationId)) {
				return listLine.getLoanRequest();
			}
		}

		return null;
	}

	private void initConsumers() {
		GenericConsumer genericConsumer = GenericConsumer.getInstance();
		genericConsumer.consume("loanRequest", new DefaultConsumer(genericConsumer.getChannel()) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");

				Gson gson = new Gson();
				LoanRequest loanRequest = gson.fromJson(message, LoanRequest.class);

				add(loanRequest);

				BankInterestRequest bankInterestRequest = new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime());

				// Pass correlation id
				CorrelationManager.correlate(loanRequest, bankInterestRequest);

				add(loanRequest, bankInterestRequest);

				// Start producing
				GenericProducer.getInstance().produce(bankInterestRequest, "interestRequest");
			}
		});
		genericConsumer.consume("interestReply", new DefaultConsumer(genericConsumer.getChannel()) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");

				Gson gson = new Gson();
				BankInterestReply bankReply = gson.fromJson(message, BankInterestReply.class);
				LoanRequest loanRequest = findCorrelatedRequest(bankReply.getCorrelationId());
				add(loanRequest, bankReply);

				LoanReply loanReply = new LoanReply(bankReply.getInterest(), bankReply.getBankId());

				// Pass correlation id
				CorrelationManager.correlate(bankReply, loanReply);

				// Start producing
				GenericProducer.getInstance().produce(loanReply, String.valueOf(loanRequest.getSsn()));
			}
		});
	}
}
