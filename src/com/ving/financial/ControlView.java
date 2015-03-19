package com.ving.financial;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.View;

public class ControlView extends JFrame {
	
	public static final int OVERVIEW = 0;
	public static final int CATAGORIES = 1;
	public static final int DETAIL = 2;
	public static final int FUTURE = 3;
	private Font textFont;
	private int padding;
	private JPanel headerRow;
	private JTabbedPane tabbedPane;
	private JPanel overViewPanel;
	private JPanel catagoryPanel;
	private JPanel detailTransPanel;
	private JPanel futureTransPanel;
	private Date reportDate;
	private Float startBalance;
	private StatementData statement;
	private TransactionList currentTrans;
	private TransactionList futureTrans;
	private TransactionCatagories catagories;
	private int currentDisplay;
	
	public ControlView () {
		textFont = new Font("Dialog", Font.BOLD, 15);
		padding = 10;
		reportDate = null;
		statement = null;
		currentTrans = null;
		futureTrans = null;
		catagories = null;
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		headerRow = new JPanel();
		headerRow.setBackground(Color.WHITE);
		headerRow.setForeground(Color.WHITE);
		headerRow.setLayout(new BoxLayout(headerRow, BoxLayout.LINE_AXIS));
		getContentPane().add(headerRow);
		tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(Color.WHITE);
//		tabbedPane.setForeground(Color.WHITE);
		getContentPane().add(tabbedPane);
		overViewPanel = new JPanel();
		overViewPanel.setLayout(new BoxLayout(overViewPanel, BoxLayout.LINE_AXIS));
		catagoryPanel = new JPanel();
		catagoryPanel.setLayout(new BoxLayout(catagoryPanel, BoxLayout.LINE_AXIS));
		detailTransPanel = new JPanel();
		detailTransPanel.setLayout(new BoxLayout(detailTransPanel, BoxLayout.PAGE_AXIS));
		futureTransPanel = new JPanel();
		futureTransPanel.setLayout(new BoxLayout(futureTransPanel, BoxLayout.PAGE_AXIS));
		tabbedPane.addTab("Overview", overViewPanel);
		tabbedPane.addTab("Catagories", catagoryPanel);
		tabbedPane.addTab("Current Transactions",detailTransPanel);
		tabbedPane.addTab("Future Transactions",futureTransPanel);
		currentDisplay = StatementData.UNDEF;
//		setMinimumSize(new Dimension(860,768));
//		setSize(860, 768);
		setVisible(true);
	}
	
	public void financialData(Date rd, StatementData sd, TransactionList ct, TransactionList ft, TransactionCatagories cat, Float sb) {
		reportDate = rd;
		statement = sd;
		currentTrans = ct;
		futureTrans = ft;
		catagories = cat;
		startBalance = sb;
	}
	
	public void refreshDisplay() {
		
		if (reportDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
			JLabel header = new JLabel("Statement Date: "+sdf.format(reportDate), SwingConstants.CENTER);
			header.setBackground(Color.WHITE);
			header.setFont(textFont);
			Dimension hr = header.getPreferredSize();
			Double hight = hr.getHeight() + 15;
			hr.setSize(hr.getWidth(), hight);
			header.setPreferredSize(hr);
			headerRow.add(header);
		} else {
			System.out.println("Report date is null");
		}
		if (statement != null) {
			overViewPanel.removeAll();
			setUpOverview();
		}
		if (catagoryPanel != null) {
			catagoryPanel.removeAll();
			setUpCatagories();
		}
		if (detailTransPanel != null) {
			detailTransPanel.removeAll();
			setUpDetail();
		}
		if (futureTransPanel != null) {
			futureTransPanel.removeAll();
			setUpFuture();
		}
		pack();
	}
	
	private void setUpOverview() {
		
		final JPanel right = new JPanel();
		right.setBackground(Color.WHITE);
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		JScrollPane rightScrollPane = new JScrollPane();
		rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightScrollPane.setWheelScrollingEnabled(true);
		rightScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		rightScrollPane.setViewportView(right);
		rightScrollPane.setMinimumSize(new Dimension(100,200));
		
		String[][] data = statement.tableData();
		String undef = Integer.toString(StatementData.UNDEF);
		Dimension[] colDim = getPreferredSize(data);
		JPanel left = new JPanel();
		left.setBackground(Color.WHITE);
		left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
		for (int i=0;i<data.length;i++) {
			JPanel row = buildRow(data[i], 2, colDim);
			if (! data[i][2].equals(undef)) {
				row.addMouseListener(new IndexMouseListener(new OverviewObject(data[i][2]), OVERVIEW, right, this));
			}
			left.add(row);
		}
		left.add(Box.createVerticalGlue());
		JScrollPane leftScrollPane = new JScrollPane();
		leftScrollPane.setBackground(Color.WHITE);
		leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftScrollPane.setWheelScrollingEnabled(true);
		leftScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		leftScrollPane.setViewportView(left);
		leftScrollPane.setMinimumSize(new Dimension(100,200));
		
		overViewPanel.add(leftScrollPane);
		overViewPanel.add(rightScrollPane);
	}
	
	private void setUpCatagories() {
		int numCat = catagories.catagories().size();
		if (numCat > 0) {
			final JPanel right = new JPanel();
			right.setBackground(Color.WHITE);
			right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
			JScrollPane rightScrollPane = new JScrollPane();
			rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			rightScrollPane.setWheelScrollingEnabled(true);
			rightScrollPane.getVerticalScrollBar().setUnitIncrement(20);
			rightScrollPane.setViewportView(right);
			rightScrollPane.setMinimumSize(new Dimension(100,200));
			
			ArrayList<JPanel> parentCatagories = new ArrayList<JPanel>();
			ArrayList<ArrayList <JPanel>> childCatagories = new ArrayList<ArrayList <JPanel>>();
			String[][] parentData = new String[numCat][2];
			int i = 0;
			for (CatagoryGroup catgrp : catagories.catagories()) {
				MoneyObject mo = new MoneyObject(catgrp.groupName(), catgrp.total());
				parentData[i] = mo.values();
				ArrayList<JPanel> child = new ArrayList<JPanel>();
				String[][] childData = new String[catgrp.groupList().size()][2];
				int j = 0;
				for (Catagory cat : catgrp.groupList()) {
					MoneyObject cmo = new MoneyObject(cat.toString(), catgrp.total(cat));
					childData[j] = cmo.values();
					j++;
				}
				Dimension[] childColDim = getPreferredSize(childData);
				for (j=0;j<childData.length;j++) {
					JPanel childRow = buildRow(childData[j], 0, childColDim);
					childRow.addMouseListener(new IndexMouseListener(new CatagoryObject(catgrp.transactions(catgrp.groupList().get(j))), CATAGORIES, right, this));
					child.add(childRow);
				}
				childCatagories.add(child);
				i++;
			}
			Dimension[] colDim = getPreferredSize(parentData);
			for (i=0;i<parentData.length;i++) {
				JPanel row = buildRow(parentData[i], 0, colDim);
				row.addMouseListener(new IndexMouseListener(new CatagoryObject(catagories.catagories().get(i).transactions()), CATAGORIES, right, this));
				parentCatagories.add(row);
			}
			
			
			
			JPanel left = new JPanel();
			left.setBackground(Color.WHITE);
			left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
			ExpandableList el = new ExpandableList(left, parentCatagories, childCatagories);
			JScrollPane leftScrollPane = new JScrollPane();
			leftScrollPane.setBackground(Color.WHITE);
			leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			leftScrollPane.setWheelScrollingEnabled(true);
			leftScrollPane.getVerticalScrollBar().setUnitIncrement(20);
			leftScrollPane.setViewportView(left);
			leftScrollPane.setMinimumSize(new Dimension(100,200));
			
			catagoryPanel.add(leftScrollPane);
			catagoryPanel.add(rightScrollPane);
		}
	}
	
	private void setUpDetail() {
		
		detailTransPanel.setBackground(Color.WHITE);
		String[][] data = currentTrans.tableData(startBalance);
		Dimension[] colDim = getPreferredSize(data);
		String columns[] = new String[7];
		columns[0] = "Num";
		columns[1] = "Date";
		columns[2] = "Payee";
		columns[3] = "Category";
		columns[4] = "";
		columns[5] = "";
		columns[6] = "Balance";
		JPanel header = buildRow(columns, 0, colDim);
		detailTransPanel.add(header);
		
		JPanel dataPanel = new JPanel();
		dataPanel.setBackground(Color.WHITE);
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
		JScrollPane vertical = new JScrollPane();
		vertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		vertical.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		vertical.setWheelScrollingEnabled(true);
		vertical.getVerticalScrollBar().setUnitIncrement(20);
		vertical.setViewportView(dataPanel);
		for (int i=0;i<data.length;i++) {
			JPanel row = buildRow(data[i], 0, colDim);
			dataPanel.add(row);
		}
		dataPanel.add(Box.createVerticalGlue());
		detailTransPanel.add(vertical);
	}
	
private void setUpFuture() {
		
		futureTransPanel.setBackground(Color.WHITE);
		String[][] data = futureTrans.tableData(statement.data(StatementData.BALANCE).amount());
		Dimension[] colDim = getPreferredSize(data);
		String columns[] = new String[7];
		columns[0] = "Num";
		columns[1] = "Date";
		columns[2] = "Payee";
		columns[3] = "Category";
		columns[4] = "";
		columns[5] = "";
		columns[6] = "Balance";
		JPanel header = buildRow(columns, 0, colDim);
		futureTransPanel.add(header);
		
		JPanel dataPanel = new JPanel();
		dataPanel.setBackground(Color.WHITE);
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
		JScrollPane vertical = new JScrollPane();
		vertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		vertical.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		vertical.setWheelScrollingEnabled(true);
		vertical.getVerticalScrollBar().setUnitIncrement(20);
		vertical.setViewportView(dataPanel);
		for (int i=0;i<data.length;i++) {
			JPanel row = buildRow(data[i], 0, colDim);
			dataPanel.add(row);
		}
		dataPanel.add(Box.createVerticalGlue());
		futureTransPanel.add(vertical);
	}
	
	public void displayDetail(int catagory, JPanel disp, Object obj) {
		switch (catagory) {
		case OVERVIEW:
			OverviewObject ovo = (OverviewObject) obj;
			if ((ovo.type() != StatementData.UNDEF) && (ovo.type() != currentDisplay)) {
				currentDisplay = ovo.type();
				String[][] data = statement.tableList(currentDisplay);
				if (data != null) {
					Dimension[] colDim = getPreferredSize(data);
					disp.removeAll();
					for (int i=0;i<data.length;i++) {
						JPanel row = buildRow(data[i], 0, colDim);
						disp.add(row);
					}
					disp.add(Box.createVerticalGlue());
					pack();
				}
			}
			break;
		case CATAGORIES:
			CatagoryObject catobj = (CatagoryObject) obj;
			String[][] data = catobj.transList().tableData();
			Dimension[] colDim = getPreferredSize(data);
			String columns[] = new String[6];
			columns[0] = "Num";
			columns[1] = "Date";
			columns[2] = "Payee";
			columns[3] = "Category";
			columns[4] = "";
			columns[5] = "";
			JPanel header = buildRow(columns, 0, colDim);
			disp.removeAll();
			disp.add(header);
			
			JPanel dataPanel = new JPanel();
			dataPanel.setBackground(Color.WHITE);
			dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
			JScrollPane vertical = new JScrollPane();
			vertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			vertical.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			vertical.setWheelScrollingEnabled(true);
			vertical.getVerticalScrollBar().setUnitIncrement(20);
			vertical.setViewportView(dataPanel);
			for (int i=0;i<data.length;i++) {
				JPanel row = buildRow(data[i], 0, colDim);
				dataPanel.add(row);
			}
			dataPanel.add(Box.createVerticalGlue());
			disp.add(vertical);
			pack();
			break;
		}
	}
	
	private void setTableSize(JTable table) {
		int rowHeight = table.getRowHeight();
		TableColumn tableColumn;
		int column;
		for (column = 0; column < table.getColumnCount(); column++)
		{
		    tableColumn = table.getColumnModel().getColumn(column);
//		    int preferredWidth = tableColumn.getMinWidth();
		    int preferredWidth = 200;
		    int maxWidth = tableColumn.getMaxWidth();

		    for (int row = 0; row < table.getRowCount(); row++)
		    {
		        TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
		        Component c = table.prepareRenderer(cellRenderer, row, column);
		        int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
		        preferredWidth = Math.max(preferredWidth, width);
		        rowHeight = Math.max(rowHeight, c.getPreferredSize().height);

		        //  We've exceeded the maximum width, no need to check other rows

		        if (preferredWidth >= maxWidth)
		        {
		            preferredWidth = maxWidth;
		            break;
		        }
		    }
		    tableColumn.setPreferredWidth( preferredWidth );
		}
		table.setRowHeight(rowHeight);
	}
	
	public Dimension[] getPreferredSize(String[][] text) {
		
		int numRows = text.length;
		int rowSize = text[0].length;
		Dimension[] rtn = new Dimension[rowSize];
		JLabel lbl;
		Dimension lblDim;
		
		for (int i=0;i<rowSize;i++) {
			rtn[i] = new Dimension(0, 0);
		}
		
		for (int i=0;i<numRows;i++) {
			for (int j=0;j<rowSize;j++) {
				lbl = new JLabel(text[i][j]);
				lbl.setFont(textFont);
				lblDim = lbl.getPreferredSize();
				if (lblDim.getWidth() > rtn[j].getWidth()) {
					rtn[j].setSize(lblDim.getWidth()+padding, lblDim.getHeight()+padding);
				}
			}
		}
    	
    	return rtn;
    }
	
	public JPanel buildRow(String[] data, int rowSize, Dimension[] colDim) {

		int rowSizeToUse = rowSize;
		if (rowSizeToUse == 0) {
			rowSizeToUse = data.length;
		}
		JPanel row = new JPanel();
		JLabel lbl;
		Matcher matcher;
		Pattern pattern = Pattern.compile("\\$\\s*\\d+");
		row.setBackground(Color.WHITE);
		row.setForeground(Color.WHITE);
		row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
		for (int i=0;i<rowSizeToUse;i++) {
			matcher = pattern.matcher(data[i]);
			if (matcher.find()) {
				lbl = new JLabel(data[i], SwingConstants.RIGHT);
			} else {
				lbl = new JLabel(data[i]);
			}
			lbl.setFont(textFont);
			lbl.setPreferredSize(colDim[i]);
			row.add(lbl);
		}
		row.add(Box.createHorizontalGlue());
		
		return row;
	}
	
	class OverviewObject {
		private int type;
		
		public OverviewObject(String type) {
			try {
				this.type = Integer.parseInt(type);
			} catch (NumberFormatException e) {
				this.type = StatementData.UNDEF;
			}
		}
		
		public int type() {
			return type;
		}
	}
	
	class CatagoryObject {
		private TransactionList transList;
		
		public CatagoryObject(TransactionList trans) {
			this.transList = trans;
		}
		
		public CatagoryObject(ArrayList<Transaction> trans) {
			this.transList = new TransactionList(trans);
		}
		
		public TransactionList transList() {
			return transList;
		}
	}

}
