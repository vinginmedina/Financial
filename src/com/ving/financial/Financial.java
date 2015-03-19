package com.ving.financial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFrame;

public class Financial {
	
	public static final String dailyURL = "URL for Daily Transaction File";
	public static final String statementURL = "URL for Statement File";
	
	private static ControlView mainWindow;
	private static Date reportDate;
	private static Float startBalance;
	private static TransactionList actualTransactions;
	private static TransactionList futureTransactions;
	private static TransactionCatagories catagoryList;
	private static StatementData statement;

	public static void main(String[] args) {
		Boolean useFile = false;
		InputStream inp = null;
		URL url = null;
		URLConnection cnx = null;
		InputStreamReader ipsr = null;
		BufferedReader br = null;
		
		mainWindow = new ControlView();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String home = "";
		if (System.getProperty("os.name").equals("Linux")) {
			home = System.getenv("HOME");
		} else if ((System.getProperty("os.name").equals("Windows 7")) ||
				(System.getProperty("os.name").equals("Windows 8")) ||
				(System.getProperty("os.name").equals("Windows 8.1"))) {
			home = System.getProperty("user.home");
		}
		String copyLoc = home + "/Copy";
		File copyDir = new File(copyLoc);
		File daily = new File(copyDir, "DailyTrans.txt");
		if ((copyDir.exists()) && (daily.canRead())) {
			try {
				inp = new FileInputStream(daily);
				ipsr = new InputStreamReader(inp);
				br = new BufferedReader(ipsr);
				useFile = true;
			} catch (IOException e) {
				System.out.println("Error opening "+daily.getPath());
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			try {
				url = new URL(dailyURL);
				cnx = url.openConnection();
				cnx.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
				cnx.setDoInput(true);
	            cnx.setDoOutput(true);
				ipsr = new InputStreamReader(cnx.getInputStream());
				br = new BufferedReader(ipsr);
				useFile = false;
			} catch (IOException e) {
				System.out.println("Error opening "+url.toExternalForm());
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		actualTransactions = new TransactionList();
		futureTransactions = new TransactionList();
		String line;
		Boolean future = false;
		try {
			while ((line=br.readLine())!=null){
				if (line.startsWith("Date")) {
					String values[] = line.split("\\^");
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
					try {
						reportDate = sdf.parse(values[1]);
					} catch (ParseException e) {
						reportDate = null;
					}
				} else if (line.startsWith("^^^^^^")) {
					startBalance = Float.parseFloat(line.replaceAll("\\^","").replaceAll("\\$", ""));
				} else if (line.contains("Projected View")) {
					future = true;
				} else {
					if (future) {
						futureTransactions.add(line);
					} else {
						actualTransactions.add(line);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error in reading data");
			e.printStackTrace();
			System.exit(1);
		}
		try {
			br.close();
			ipsr.close();
			if (useFile) {
				inp.close();
			}
		} catch (IOException e) {
			System.out.println("Error closing input stream");
			e.printStackTrace();
			System.exit(1);
		}
//		String[][] data = actualTransactions.tableData();
//		for (int i=0;i<actualTransactions.size();i++) {
//			System.out.println(data[i][0]+data[i][1]+data[i][2]+data[i][3]+data[i][4]+data[i][5]);
//		}
		catagoryList = new TransactionCatagories(actualTransactions);
//		for (CatagoryGroup cg : catagoryList.catagories()) {
//			System.out.println(cg.groupName());
//			for (Catagory cat : cg.groupList()) {
//				System.out.println("   "+cat.toString());
//			}
//		}
		File statementFile = new File(copyDir, "Statement.txt");
		if ((copyDir.exists()) && (statementFile.canRead())) {
			try {
				inp = new FileInputStream(statementFile);
				ipsr = new InputStreamReader(inp);
				br = new BufferedReader(ipsr);
				useFile = true;
			} catch (IOException e) {
				System.out.println("Error opening "+daily.getPath());
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			try {
				url = new URL(statementURL);
				cnx = url.openConnection();
				cnx.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
				cnx.setDoInput(true);
	            cnx.setDoOutput(true);
				ipsr = new InputStreamReader(cnx.getInputStream());
				br = new BufferedReader(ipsr);
				useFile = false;
			} catch (IOException e) {
				System.out.println("Error opening "+url.toExternalForm());
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		statement = new StatementData(br);
		try {
			br.close();
			ipsr.close();
			if (useFile) {
				inp.close();
			}
		} catch (IOException e) {
			System.out.println("Error closing input stream");
			e.printStackTrace();
			System.exit(1);
		}
		mainWindow.financialData(reportDate, statement, actualTransactions, futureTransactions, catagoryList, startBalance);
		mainWindow.refreshDisplay();
	}

}
