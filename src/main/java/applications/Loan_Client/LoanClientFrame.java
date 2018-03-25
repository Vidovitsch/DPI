package applications.Loan_Client;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import app_gateways.LoanBrokerAppGateway;
import correlation.CorrelationManager;
import models.messaging.RequestReply;
import models.loan.*;
import message_gateways.GenericProducer;

public class LoanClientFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField tfSSN;
	private DefaultListModel<RequestReply<LoanRequest, LoanReply>> listModel = new DefaultListModel<>();
	private JList<RequestReply<LoanRequest, LoanReply>> requestReplyList;
	private JTextField tfAmount;
	private JTextField tfTime;

	private static String ssn;
	private LoanBrokerAppGateway loanBrokerAppGateway;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				LoanClientFrame frame = new LoanClientFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
//                try {
//                    if (ssn != null) {
//                        GenericConsumer.getInstance().getChannel().queueDelete(ssn);
//                    }
//                } catch (IOException e) {
//                    System.out.println(e.getMessage());
//                }
            }
        });
	}

	/**
	 * Create the frame.
	 */
	private LoanClientFrame() {
		setTitle("Loan Client");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 684, 619);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {0, 0, 30, 30, 30, 30, 0};
		gbl_contentPane.rowHeights = new int[] {30,  30, 30, 30, 30};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblBody = new JLabel("ssn");
		GridBagConstraints gbc_lblBody = new GridBagConstraints();
		gbc_lblBody.insets = new Insets(0, 0, 5, 5);
		gbc_lblBody.gridx = 0;
		gbc_lblBody.gridy = 0;
		contentPane.add(lblBody, gbc_lblBody);
		
		tfSSN = new JTextField();
		GridBagConstraints gbc_tfSSN = new GridBagConstraints();
		gbc_tfSSN.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSSN.insets = new Insets(0, 0, 5, 5);
		gbc_tfSSN.gridx = 1;
		gbc_tfSSN.gridy = 0;
		contentPane.add(tfSSN, gbc_tfSSN);
		tfSSN.setColumns(10);

		JLabel lblNewLabel = new JLabel("amount");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		tfAmount = new JTextField();
		GridBagConstraints gbc_tfAmount = new GridBagConstraints();
		gbc_tfAmount.anchor = GridBagConstraints.NORTH;
		gbc_tfAmount.insets = new Insets(0, 0, 5, 5);
		gbc_tfAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfAmount.gridx = 1;
		gbc_tfAmount.gridy = 1;
		contentPane.add(tfAmount, gbc_tfAmount);
		tfAmount.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("time");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		tfTime = new JTextField();
		GridBagConstraints gbc_tfTime = new GridBagConstraints();
		gbc_tfTime.insets = new Insets(0, 0, 5, 5);
		gbc_tfTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfTime.gridx = 1;
		gbc_tfTime.gridy = 2;
		contentPane.add(tfTime, gbc_tfTime);
		tfTime.setColumns(10);
		
		JButton btnQueue = new JButton("send models.loan request");
		btnQueue.addActionListener((ActionEvent arg0) -> {
			int ssn = Integer.parseInt(tfSSN.getText());
			int amount = Integer.parseInt(tfAmount.getText());
			int time = Integer.parseInt(tfTime.getText());

//			try {
//                initConsumers(String.valueOf(ssn));
//                if (LoanClientFrame.ssn != null && !LoanClientFrame.ssn.equals(String.valueOf(ssn))) {
//                    GenericConsumer.getInstance().getChannel().queueDelete(LoanClientFrame.ssn);
//                }
//                LoanClientFrame.ssn = String.valueOf(ssn);
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }

			LoanRequest request = new LoanRequest(ssn, amount, time);

			// Pass correlation id
			CorrelationManager.setUUID(request);

			listModel.addElement( new RequestReply<>(request, null));

			// Start producing
			GenericProducer.getInstance().produce(request, "loanRequest");
		});

		GridBagConstraints gbc_btnQueue = new GridBagConstraints();
		gbc_btnQueue.insets = new Insets(0, 0, 5, 5);
		gbc_btnQueue.gridx = 2;
		gbc_btnQueue.gridy = 2;
		contentPane.add(btnQueue, gbc_btnQueue);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 7;
		gbc_scrollPane.gridwidth = 6;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		requestReplyList = new JList<>(listModel);
		scrollPane.setViewportView(requestReplyList);
	}

   private RequestReply<LoanRequest,LoanReply> getRequestReply(LoanRequest request) {
	   for (int i = 0; i < listModel.getSize(); i++) {
		   RequestReply<LoanRequest,LoanReply> requestReply = listModel.get(i);
		   if (requestReply.getRequest() == request) {
			   return requestReply;
		   }
	   }

	   return null;
   }

	private LoanRequest findCorrelatedRequest(String correlationId) {
		for (int i = 0; i < listModel.getSize(); i++) {
			RequestReply<LoanRequest,LoanReply> requestReply =listModel.get(i);
			if (requestReply.getRequest().getCorrelationId().equals(correlationId)) {
				return requestReply.getRequest();
			}
		}

		return null;
	}

	private void add(LoanRequest loanRequest, LoanReply loanReply) {
		RequestReply<LoanRequest,LoanReply> requestReply = getRequestReply(loanRequest);
		if (requestReply != null && loanReply != null) {
			requestReply.setReply(loanReply);
			requestReplyList.repaint();
		}
	}

//	private void initConsumers(String ssn) {
//		GenericConsumer genericConsumer = GenericConsumer.getInstance();
//		genericConsumer.consume(ssn, new DefaultConsumer(genericConsumer.getChannel()) {
//			@Override
//			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//				String message = new String(body, "UTF-8");
//
//				Gson gson = new Gson();
//				LoanReply loanReply = gson.fromJson(message, LoanReply.class);
//				LoanRequest loanRequest = findCorrelatedRequest(loanReply.getCorrelationId());
//				add(loanRequest, loanReply);
//			}
//		});
//	}
}
