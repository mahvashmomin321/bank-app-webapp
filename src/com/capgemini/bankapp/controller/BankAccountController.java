package com.capgemini.bankapp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.banapp.exception.BankAccountNotFoundException;
import com.capgemini.banapp.exception.LowBalanceException;
import com.capgemini.bankapp.model.BankAccount;
import com.capgemini.bankapp.service.BankAccountService;
import com.capgemini.bankapp.service.impl.BankAccountServiceImpl;

@WebServlet(urlPatterns = { "*.do" }, loadOnStartup = 1)
public class BankAccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private BankAccountService bankService;

	public BankAccountController() {
		bankService = new BankAccountServiceImpl();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		String path = request.getServletPath();

		if (path.equals("/displayAllBankAccount.do")) {
			List<BankAccount> bankAccounts = bankService.findAllBankAccounts();
			RequestDispatcher dispatcher = request.getRequestDispatcher("show-details.jsp");
			request.setAttribute("accounts", bankAccounts);
			dispatcher.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String path = request.getServletPath();
		System.out.println(path);

		if (path.equals("/addNewAccount.do")) {
			String accountHolderName = request.getParameter("customer_name");
			String accountType = request.getParameter("account_type");
			double balance = Double.parseDouble(request.getParameter("account_balance"));

			BankAccount account = new BankAccount(accountHolderName, accountType, balance);

			if (bankService.addNewBankAccount(account)) {
				out.println("<h2>BankAccount is created Sucessfully..");
				out.close();
			}
		} else if (path.equals("/widthdraw.do")) {
			long accountId = Long.parseLong(request.getParameter("account_id"));
			double balance = Double.parseDouble(request.getParameter("account_balance"));
			try {
				double newBalance = bankService.withdraw(accountId, balance);
				out.println("<h2>Transaction successful..your Current Balance=" + newBalance);
				out.println("<h2><a href='index.html'>Home</a></h2>");
				out.close();
			} catch (LowBalanceException e) {
				System.out.println(e.getMessage());
				out.println("<h2>Low balance");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			} catch (BankAccountNotFoundException e) {
				out.println("<h2>account not found");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}

		} else if (path.equals("/deposit.do")) {
			long accountId = Long.parseLong(request.getParameter("account_id"));
			double balance = Double.parseDouble(request.getParameter("account_balance"));
			try {
				bankService.deposit(accountId, balance);
				out.println("<h2>Transaction Successful..Your Current Balance is:" + balance);
				out.println("<h2><a href='index.html'>Home</a></h2>");
				out.close();
			} catch (BankAccountNotFoundException e) {
				out.println("<h2>Account Not Found");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}
		}

		else if (path.equals("/fundtransfer.do")) {

			long senderAccount = Long.parseLong(request.getParameter("sender_account"));
			long recipentAccount = Long.parseLong(request.getParameter("recipent_account"));
			double amount = Double.parseDouble(request.getParameter("amount"));
			try {
				double balance = bankService.fundTransfer(senderAccount, recipentAccount, amount);
				out.println("<h2>Transaction Successful...Your Current Balance is:" + balance);
				out.println("<h2><a href='index.html'>Home</a></h2>");
				out.close();
			} catch (LowBalanceException e) {
				out.println("<h2>you Don't have sufficient Fund");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			} catch (BankAccountNotFoundException e) {
				out.println("<h2>Account Not Found");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}
		}

		else if (path.equals("/deleteAccount.do")) {
			long accountId = Long.parseLong(request.getParameter("account_id"));
			try {
				if (bankService.deleteBankAccount(accountId)) {
					out.println("<h2>Account deleted Successful...");
					out.println("<h2><a href='index.html'>Home</a></h2>");
				}
			} catch (BankAccountNotFoundException e) {
				out.println("<h2>Account Not Found");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}
		} else if (path.equals("/checkBalance.do")) {
			long accountId = Long.parseLong(request.getParameter("account_id"));
			try {
				double balance = bankService.checkBalance(accountId);
				out.println("<h2>your current Balance is:" + balance);
				out.println("<h2><a href='index.html'>Home</a></h2>");
			} catch (BankAccountNotFoundException e) {
				out.println("<h2>Account Not Found");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}
		} else if (path.equals("/searchAccount.do")) {
			long accountId = Long.parseLong(request.getParameter("account_id"));

			try {
				BankAccount bankAccount = bankService.searchAccount(accountId);
				RequestDispatcher dispatcher = request.getRequestDispatcher("accountDetails.jsp");
				request.setAttribute("account", bankAccount);
				dispatcher.forward(request, response);

			} catch (BankAccountNotFoundException e) {
				out.println("<h2>Account Not Found");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}

		} else if (path.equals("/updateAccount.do")) {
			long accountId = Long.parseLong(request.getParameter("account_id"));
			try {
				BankAccount bankAccount = bankService.searchAccount(accountId);
				RequestDispatcher dispatcher = request.getRequestDispatcher("accountInfo.jsp");
				request.setAttribute("account", bankAccount);
				dispatcher.forward(request, response);
			} catch (BankAccountNotFoundException e) {
				out.println("<h2>Account Not Found");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}

		} else if (path.equals("/accountInfo.do")) {
			long accountId = Long.parseLong(request.getParameter("account_id"));
			String customer_name= request.getParameter("customer_name");
			String accountType = request.getParameter("account_type");
			boolean result=bankService.updateAccount(accountId, customer_name, accountType);
			if(result == true) {
				out.println("<h2>Account Updated Successfully");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}
			else {
				out.println("<h2>Account Updation failed");
				out.println("<h2><a href='index.html'>Home</a></h2>");
			}

		}

	}
}
