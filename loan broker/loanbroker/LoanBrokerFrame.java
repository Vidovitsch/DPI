package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import models.bank.*;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import services.GenericConsumer;
import services.GenericProducer;


public class LoanBrokerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	private LoanBrokerFrame() {
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
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
		
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);

		// Start consuming
		initConsumers();
	}
	
	 private JListLine getRequestReply(LoanRequest request){    
	     
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	   }

	private void add(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));		
	}


	private void add(LoanRequest loanRequest, BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}

	private void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}

	private LoanRequest findCorrelatedRequest(String correlationId) {
		for (int i = 0; i < listModel.getSize(); i++){
			JListLine rr =listModel.get(i);
			if (rr.getLoanRequest().getCorrelationId().equals(correlationId)){
				return rr.getLoanRequest();
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
				bankInterestRequest.setCorrelationId(loanRequest.getCorrelationId());
				add(loanRequest, bankInterestRequest);

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

				LoanReply loanReply = new LoanReply(bankReply.getInterest(), bankReply.getQuoteId());
				loanReply.setCorrelationId(bankReply.getCorrelationId());

				GenericProducer.getInstance().produce(loanReply, "loanReply");
			}
		});
	}
}
