package com.ving.financial;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ExpandableList {
	private JPanel host;
	private ArrayList<JPanel> parents;
	private ArrayList<ArrayList <JPanel>> children;
	private ArrayList<Boolean> expanded;
	private Font textFont;

	public ExpandableList(JPanel host, ArrayList<JPanel> parents, ArrayList<ArrayList <JPanel>> children) {
		this.host = host;
		this.parents = parents;
		this.children = children;
		this.expanded = new ArrayList<Boolean>();
		this.textFont = new Font("Dialog", Font.BOLD, 10);
		for (int i=0;i<this.parents.size();i++) {
			this.expanded.add(false);
		}
		display();
	}
	
	private void display() {
		host.removeAll();
		int i = 0;
		for (JPanel parentRow : parents) {
			final int rn = i;
			JPanel dispParentRow = new JPanel();
			dispParentRow.setBackground(Color.WHITE);
			dispParentRow.setLayout(new BoxLayout(dispParentRow, BoxLayout.LINE_AXIS));
			JButton expand = new JButton();
			expand.setBackground(null);
			expand.setOpaque(false);
			expand.setBorder(new EmptyBorder(0, 0, 0, 0));
			if (expanded.get(i)) {
				expand.setText("V");
			} else {
				expand.setText(">");
			}
			expand.setFont(textFont);
			Dimension bd = expand.getPreferredSize();
			int bw = (int) Math.ceil( bd.getWidth()) + 20;
			int rh = parentRow.getHeight();
			bd.setSize(bw, rh);
			expand.setPreferredSize(bd);
			expand.setHorizontalAlignment(SwingConstants.CENTER);
			expand.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchRow(rn);
				}
			});
			dispParentRow.add(expand);
			dispParentRow.add(parentRow);
			host.add(dispParentRow);
			if (expanded.get(i)) {
				for (JPanel childRow : children.get(i)) {
					JPanel dispChildRow = new JPanel();
					dispChildRow.setBackground(Color.WHITE);
					dispChildRow.setLayout(new BoxLayout(dispChildRow, BoxLayout.LINE_AXIS));
					dispChildRow.add(Box.createRigidArea(bd));
					dispChildRow.add(childRow);
					host.add(dispChildRow);
				}
			}
			i++;
		}
		host.add(Box.createVerticalGlue());
		host.revalidate();
		host.repaint();
	}
	
	public void collapseAll() {
		for (int i=0;i<parents.size();i++) {
			expanded.set(i, false);
		}
		display();
	}
	
	public void collapse(int rownum) {
		if ((rownum >= 0) && (rownum < parents.size())) {
			expanded.set(rownum, false);
			display();
		}
	}
	
	public void expandAll() {
		for (int i=0;i<parents.size();i++) {
			expanded.set(i, true);
		}
		display();
	}
	
	public void expand(int rownum) {
		if ((rownum >= 0) && (rownum < parents.size())) {
			expanded.set(rownum, true);
			display();
		}
	}
	
	public void switchRow(int rownum) {
		int i = 0;
		for (JPanel parentRow : parents) {
			if (((expanded.get(i) && (rownum != i)) ||
					((! expanded.get(rownum) && (rownum == i))))) {
				expanded.set(i, true);
			} else {
				expanded.set(i, false);
			}
			i++;
		}
		display();
	}
}
