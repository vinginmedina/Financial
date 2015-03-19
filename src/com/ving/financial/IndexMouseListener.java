package com.ving.financial;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class IndexMouseListener extends MouseAdapter {
	private Object object;
	private int catagory;
	private JPanel panel;
	private ControlView cv;
	
	public IndexMouseListener(Object inp, int cat, JPanel p, ControlView cv) {
		object = inp;
		catagory = cat;
		panel = p;
		this.cv = cv;
    }
	
//	public IndexMouseListener(int index) {
//		this.index = index;
//	}
	
	@Override
    public void mouseClicked(MouseEvent e) {
        cv.displayDetail(catagory, panel, object);
    }

}
