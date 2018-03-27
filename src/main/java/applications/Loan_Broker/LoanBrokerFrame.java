package applications.Loan_Broker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import app_gateways.BankAppGateway;
import app_gateways.LoanClientAppGateway;
import models.bank.*;
import models.loan.LoanReply;
import models.loan.LoanRequest;
import net.sourceforge.jeval.Evaluator;

public class LoanBrokerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
	private JList<JListLine> list;

	private LoanClientAppGateway loanClientApp = new LoanClientAppGateway();
	private BankAppGateway bankApp = new BankAppGateway();
	private RecipientList recipientList = new RecipientList();

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
		// Add recipients with rules
		recipientList.addRecipient("ING", "#{amount} <= 100000 && #{time} <= 10");
		recipientList.addRecipient("ABN_AMRO", "#{amount} >= 200000 && #{amount} <= 300000  && #{time} <= 20");
		recipientList.addRecipient("RaboBank", "#{amount} <= 250000 && #{time} <= 15");

		this.loanClientApp.setLoanRequestListener(request ->  {
			add(request);
			BankInterestRequest interestRequest = new BankInterestRequest(request.getAmount(), request.getTime());

			List<String> recipients = recipientList.evaluateRules(interestRequest.getAmount(), interestRequest.getTime());

			bankApp.sendBankRequest(interestRequest);
			add(request, interestRequest);
		});
		this.bankApp.setBankReplyListener((request, reply) -> {
			JListLine listLine = getRequestReply(request);
			if (listLine != null) {
				LoanRequest loanRequest = listLine.getLoanRequest();
				add(loanRequest, reply);
				LoanReply loanReply = new LoanReply(reply.getInterest(), reply.getBankId());
				loanClientApp.sendLoanReply(loanRequest, loanReply);
			}
		});

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

	 private JListLine getRequestReply(BankInterestRequest request) {
		 for (int i = 0; i < listModel.getSize(); i++) {
			 JListLine listLine = listModel.get(i);
			 if (listLine.getBankRequest() == request) {
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
		}		
	}

	private void add(LoanRequest loanRequest, BankInterestReply bankReply) {
		JListLine listLine = getRequestReply(loanRequest);
		if (listLine != null && bankReply != null) {
			listLine.setBankReply(bankReply);
            list.repaint();
		}		
	}
}
