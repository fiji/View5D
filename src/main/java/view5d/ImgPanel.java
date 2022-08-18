/*-
 * #%L
 * View5D plugin for Fiji.
 * %%
 * Copyright (C) 2006 - 2022 Fiji developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
/****************************************************************************
 *   Copyright (C) 1996-2007 by Rainer Heintzmann                          *
 *   heintzmann@gmail.com                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************
*/
// By making the appropriate class "View5D" or "View5D_" public and renaming the file, this code can be toggled between Applet and ImageJ respectively

package view5d;

// import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.System.*;
// import ij.*;

// This class manages constructing the application
public class ImgPanel extends Panel implements MouseWheelListener { // the listener is for the scrollbar (slider)
    static final long serialVersionUID = 1;

    Container applet;
    ImgPanel    DataPanel=null; // just for the case, that this (histogram) window was generated by another owner
    My3DData  data3d;
    public ImageCanvas c1,c2,c3;
    public PositionLabel label;
    boolean     ROIstarted = false;   // this is stored for the sqare ROIs in the Canvasses
    APoint DraggedMarker = null;   // a ROI currently being dragged
    APoint SavedMarker = null;   // a ROI currently being dragged
    MenuBar  MyMenu;
    boolean ScrollbarPresent=false;
    double [] OldOffset;  // to remember the active offsets, when changing elements or time
   
    public void InitScaling() {
	c2.InitScaling();
	c3.InitScaling();
	c1.InitScaling();
	c3.CalcPrev();
	c3.UpdateAllNoCoord();
    }
    public void OwnerPanel(ImgPanel owner) {
       DataPanel=owner;
        }

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0)
			data3d.nextTime(1);
		else
			data3d.nextTime(-1);
		AdjustOffset();
		c1.UpdateAll();
	}

   public void CheckScrollBar() {
	   //System.out.println("CheckScrollBar: "+data3d.Times+" timesteps.\n");
	   //System.out.println("Objects: Applet: "+System.identityHashCode(applet)+" , Data: "+data3d+", this Panel: "+System.identityHashCode(this)+", DataPanel: "+System.identityHashCode(DataPanel)+"\n");
    	if (data3d.Times > 1)
                {
                	Scrollbar Slider=label.TimeScrollbar;
                	if (! ScrollbarPresent) { //
						// System.out.println("Adding a Scrollbar with "+data3d.Times+" timesteps.\n");
						Slider=new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, data3d.Times);
						Slider.setBackground(new Color(70,70,200)); // Color.BLUE
						// Slider.setForeground(new Color(255,255,0)); // does not set the foreground but only the border
                    	ScrollbarPresent=true;
						label.TimeScrollbar = Slider;  // that it will be moved when browsing though the data
						add("East", Slider);
						//if (DataPanel != null)
						//	DataPanel.CheckScrollBar();
						//data3d.ShowAllSlices=true;
						data3d.TrackDirection = 4;
						if (data3d.SizeZ > 1) {
							data3d.SearchZ = 1;
							data3d.COMZ = 1;
						}
                    Slider.setBlockIncrement(data3d.Times / 10 + 1);
                    Slider.addAdjustmentListener(c1);
                    Slider.addMouseWheelListener(this); // register this class for handling the events in it
                    Slider.setVisible(true);
                	}
                	else {
						Slider.setMaximum(data3d.Times);
						// System.out.println("Changed Scrollbar size to"+data3d.Times+" timesteps.\n");
						Slider.setBlockIncrement(data3d.Times / 10 + 1);
						Slider.setVisible(true);
					}
                	Slider.doLayout();
                	this.doLayout();
                }
//                if (data3d.Elements > 5)
//                	data3d.TrackDirection=3;
 }

    public void setPositions(APoint ap) {
    if (ap != null)
    	{
       	APoint p=data3d.LimitPoint(ap);
    	// System.out.println("Setting Position: (" + p.coord[0] +", " + p.coord[1] +", "+ p.coord[2] +", "+ p.coord[3] +", "+ p.coord[4] +")\n");
       	c2.PositionValue= (int) (p.coord[0]+0.5);
       	c3.PositionValue= (int) (p.coord[1]+0.5);
       	c1.PositionValue= (int) (p.coord[2]+0.5);
	if ((int) (p.coord[3]+0.5) >= 0 && (int) (p.coord[3]+0.5) < data3d.Elements)
       		data3d.setElement((int) (p.coord[3]+0.5));
	if ((int) (p.coord[4]+0.5) >= 0 && (int) (p.coord[4]+0.5) < data3d.Times)
	        data3d.setTime((int) (p.coord[4]+0.5));
       }
    }

    public float[] getPositions() {
           	float [] pos = new float[5];
           	pos[0]=(float) c2.PositionValue;
           	pos[1]=(float) c3.PositionValue;
           	pos[2]=(float) c1.PositionValue;
           	pos[3]=data3d.ActiveElement;
           	pos[4]=data3d.ActiveTime;
           return pos;
        }

    public void RememberOffset() {
    	OldOffset = data3d.ElementAt(data3d.ActiveElement).DisplayOffset;
    }
    
    public void LimitPositions() {
    	if (c1.PositionValue < 0) c1.PositionValue=0;
    	if (c2.PositionValue < 0) c2.PositionValue=0;
    	if (c3.PositionValue < 0) c3.PositionValue=0;
    	if (c1.PositionValue >= c1.getMaxPos()) c1.PositionValue=c1.getMaxPos()-1;
    	if (c2.PositionValue >= c2.getMaxPos()) c2.PositionValue=c2.getMaxPos()-1;
    	if (c3.PositionValue >= c3.getMaxPos()) c3.PositionValue=c3.getMaxPos()-1;
    }

    public void AdjustOffset() {
    	//return;
    	double [] NewOffset = data3d.ElementAt(data3d.ActiveElement).DisplayOffset;
    	if (OldOffset != null)
    	{
       	c2.PositionValue += (int) ((OldOffset[0]-NewOffset[0]));
       	c3.PositionValue += (int) ((OldOffset[1]-NewOffset[1]));
       	c1.PositionValue += (int) ((OldOffset[2]-NewOffset[2]));
       	LimitPositions();
    	}
    }

    public void AdvancePos() {
    	// System.out.println("Advancing direction " + data3d.TrackDirection);
    	switch (data3d.TrackDirection){
    	case 0:
           	c2.PositionValue += 1;
           	c2.PositionValue = (c2.PositionValue % c2.getMaxPos());
           	break;
    	case 1:
           	c3.PositionValue += 1;
           	c3.PositionValue = (c3.PositionValue % c3.getMaxPos());
           	break;
    	case 2:
           	c1.PositionValue += 1;
           	c1.PositionValue = (c1.PositionValue % c1.getMaxPos());
           	break;
    	case 3:
           	data3d.setElement((data3d.ActiveElement + 1) % data3d.Elements);
    	case 4:
           	data3d.setTime((data3d.ActiveTime + 1) % data3d.Times);
    	}
        }
    
    void AddPopupMenu(Menu aMenu, boolean doSubMenu) {
	if (doSubMenu)  // a real applet -> context menus are required
	{
            c1.MyPopupMenu.add(aMenu);
            //c2.MyPopupMenu.add(aMenu);  // can be debated whether all caveses should have these general menues
            //c3.MyPopupMenu.add(aMenu);  // can be debated whether all caveses should have these general menues
	}
	else
	{    
        MyMenu.add(aMenu);
	}
    }
    
    public ImgPanel(Container app,My3DData mydata) {
	applet = app;
	setLayout(new BorderLayout());
	Panel grid = new Panel();
	grid.setLayout(new GridLayout(2, 2));
	add("Center", grid);

	// Only one 3D Data class:
	data3d = mydata;
	// Image joe = Toolkit.getDefaultToolkit().getImage("./iap.gif");
	// Image joe = applet.getImage(applet.getDocumentBase(), "iap.gif");
	try {
	c1 = new ImageCanvas(applet,this,data3d,2,"XY");
        c1.AspectLocked.setState(true);
	c2 = new ImageCanvas(applet,this,data3d,0,"ZY");
	c3 = new ImageCanvas(applet,this,data3d,1,"XZ");
	} catch(Exception e)
	    {
		applet.add("South",new Label ("Caught Exception:"+e.getMessage()));
		applet.setVisible(true);
		e.printStackTrace();
	    }
	c2.TakeOtherCanvas1(c1);
	c2.TakeOtherCanvas2(c3);
	c3.TakeOtherCanvas1(c2);
	c3.TakeOtherCanvas2(c1);
	c1.TakeOtherCanvas1(c2);
	c1.TakeOtherCanvas2(c3);

	grid.add(c1);
	grid.add(c2);
	grid.add(c3);

        MyMenu =new MenuBar();  
        
        // MenuComponent AMenu = MyMenu;
        // All the events are actually processed by the c1 canvas
    boolean doSubMenu=(applet instanceof View5D);

	if (!(applet instanceof View5D))
		if (System.getProperty("os.name").startsWith("Mac"))
			doSubMenu=true;
		
        Menu SubMenu = new Menu("General",false);  // can eventually be dragged to the side
        AddPopupMenu(SubMenu,doSubMenu);

        MenuItem tmp;
	tmp = new MenuItem("Help [? or F1]");
	tmp.addActionListener(new MyMenuProcessor(c1,'?')); SubMenu.add(tmp);
	tmp = new MenuItem("Exit [$]");
	tmp.addActionListener(new MyMenuProcessor(c1,'$')); SubMenu.add(tmp);
	tmp = new MenuItem("Readmode (Complex only) [^ or F2]");
	tmp.addActionListener(new MyMenuProcessor(c1,'^')); SubMenu.add(tmp);

        SubMenu = new Menu("Import/Export",false);  // can eventually be dragged to the side
        AddPopupMenu(SubMenu,doSubMenu);
        
	tmp = new MenuItem("Reload data [l]");
	tmp.addActionListener(new MyMenuProcessor(c1,'l')); SubMenu.add(tmp);
        tmp = new MenuItem("Reload markers [L]");
	tmp.addActionListener(new MyMenuProcessor(c1,'L')); SubMenu.add(tmp);
        tmp = new MenuItem("Export this element [X] (only in ImageJ)");
        tmp.addActionListener(new MyMenuProcessor(c1,'X')); SubMenu.add(tmp);
        tmp = new MenuItem("Spawn Viewer [s]");
	tmp.addActionListener(new MyMenuProcessor(c1,'s')); SubMenu.add(tmp);

        SubMenu = new Menu("Measuring & Markers",false);  // can eventually be dragged to the side
        AddPopupMenu(SubMenu,doSubMenu);
        
	tmp = new MenuItem("Set Axes Units and Scalings [N]");
	tmp.addActionListener(new MyMenuProcessor(c1,'N')); SubMenu.add(tmp);
        tmp = new MenuItem("set marker [m]");
	tmp.addActionListener(new MyMenuProcessor(c1,'m')); SubMenu.add(tmp);
    tmp = new MenuItem("set split marker [\\]");
	tmp.addActionListener(new MyMenuProcessor(c1,'\\')); SubMenu.add(tmp);
        tmp = new MenuItem("delete active marker [M]");
	tmp.addActionListener(new MyMenuProcessor(c1,'M')); SubMenu.add(tmp);
        tmp = new MenuItem("delete trailing markers [Q]");
	tmp.addActionListener(new MyMenuProcessor(c1,'Q')); SubMenu.add(tmp);
        tmp = new MenuItem("tag/untag marker [&]");
	tmp.addActionListener(new MyMenuProcessor(c1,'&')); SubMenu.add(tmp);
        tmp = new MenuItem("new marker list [k]");
	tmp.addActionListener(new MyMenuProcessor(c1,'k')); SubMenu.add(tmp);
        tmp = new MenuItem("delete marker list [K]");
	tmp.addActionListener(new MyMenuProcessor(c1,'K')); SubMenu.add(tmp);
        tmp = new MenuItem("activate next marker [0]");
	tmp.addActionListener(new MyMenuProcessor(c1,'0')); SubMenu.add(tmp);
        tmp = new MenuItem("activate previous marker [9]");
	tmp.addActionListener(new MyMenuProcessor(c1,'9')); SubMenu.add(tmp);
        tmp = new MenuItem("activate next marker list [j]");
	tmp.addActionListener(new MyMenuProcessor(c1,'j')); SubMenu.add(tmp);
        tmp = new MenuItem("activate previous marker list [J]");
	tmp.addActionListener(new MyMenuProcessor(c1,'J')); SubMenu.add(tmp);
        tmp = new MenuItem("toggles the marker color of current list [w]");
	tmp.addActionListener(new MyMenuProcessor(c1,'w')); SubMenu.add(tmp);
        tmp = new MenuItem("auto-track marker [W]");
	tmp.addActionListener(new MyMenuProcessor(c1,'W')); SubMenu.add(tmp);
    	tmp = new MenuItem("align display to marker track [|]");
	tmp.addActionListener(new MyMenuProcessor(c1,'|')); SubMenu.add(tmp);
		tmp = new MenuItem("reset track alignments  [{]");
	tmp.addActionListener(new MyMenuProcessor(c1,'{')); SubMenu.add(tmp);
        tmp = new MenuItem("subtract track from data [#]");
	tmp.addActionListener(new MyMenuProcessor(c1,'#')); SubMenu.add(tmp);
        tmp = new MenuItem("Show detailed marker menu [n]");
	tmp.addActionListener(new MyMenuProcessor(c1,'n')); SubMenu.add(tmp);
		tmp = new MenuItem("Marker list property menu [}]");
	tmp.addActionListener(new MyMenuProcessor(c1,'}')); SubMenu.add(tmp);
        tmp = new MenuItem("Reload markers [L]");
	tmp.addActionListener(new MyMenuProcessor(c1,'L')); SubMenu.add(tmp);

        SubMenu = new Menu("Color",false);  // can eventually be dragged to the side
        AddPopupMenu(SubMenu,doSubMenu);
        
	tmp = new MenuItem("Toggle multicolor overlay [C]");
	tmp.addActionListener(new MyMenuProcessor(c1,'C')); SubMenu.add(tmp);
        tmp = new MenuItem("Next color map for element [c]");
	tmp.addActionListener(new MyMenuProcessor(c1,'c')); SubMenu.add(tmp);
        tmp = new MenuItem("Next inverse color map [d]");
	tmp.addActionListener(new MyMenuProcessor(c1,'d')); SubMenu.add(tmp);
        tmp = new MenuItem("Toggle over/underflow [o]");
	tmp.addActionListener(new MyMenuProcessor(c1,'o')); SubMenu.add(tmp);
        tmp = new MenuItem("Toggle logarithmic scale [O]");
	tmp.addActionListener(new MyMenuProcessor(c1,'O')); SubMenu.add(tmp);
        tmp = new MenuItem("Adjust threshold element [t]");
	tmp.addActionListener(new MyMenuProcessor(c1,'t')); SubMenu.add(tmp);
        tmp = new MenuItem("Adjust all thresholds [T]");
	tmp.addActionListener(new MyMenuProcessor(c1,'T')); SubMenu.add(tmp);
        tmp = new MenuItem("Coarse raise lower display threshold [1]");
	tmp.addActionListener(new MyMenuProcessor(c1,'1')); SubMenu.add(tmp);
        tmp = new MenuItem("Coarse decrease lower display threshold [2]");
	tmp.addActionListener(new MyMenuProcessor(c1,'2')); SubMenu.add(tmp);
        tmp = new MenuItem("Coarse raise upper display threshold [3]");
	tmp.addActionListener(new MyMenuProcessor(c1,'3')); SubMenu.add(tmp);
        tmp = new MenuItem("Coarse decrease upper display threshold [4], use 5-8 for fine tuning");
	tmp.addActionListener(new MyMenuProcessor(c1,'4')); SubMenu.add(tmp);
        tmp = new MenuItem("Transfer display thresholds to data thresholds [!]");
	tmp.addActionListener(new MyMenuProcessor(c1,'!')); SubMenu.add(tmp);
        tmp = new MenuItem("Advance element [e]");
	tmp.addActionListener(new MyMenuProcessor(c1,'e')); SubMenu.add(tmp);
        tmp = new MenuItem("Devance element [E]");
	tmp.addActionListener(new MyMenuProcessor(c1,'E')); SubMenu.add(tmp);
        tmp = new MenuItem("Define underflow gate [u]");
	tmp.addActionListener(new MyMenuProcessor(c1,'u')); SubMenu.add(tmp);
        tmp = new MenuItem("Toggle defined underflow gate [U]");
	tmp.addActionListener(new MyMenuProcessor(c1,'U')); SubMenu.add(tmp);
        tmp = new MenuItem("Red glow colormap [R]");
	tmp.addActionListener(new MyMenuProcessor(c1,'R')); SubMenu.add(tmp);
        tmp = new MenuItem("Gray colormap [G]");
	tmp.addActionListener(new MyMenuProcessor(c1,'G')); SubMenu.add(tmp);
        tmp = new MenuItem("Rainbow colormap [B]");
	tmp.addActionListener(new MyMenuProcessor(c1,'B')); SubMenu.add(tmp);
        tmp = new MenuItem("Red colomap [r]");
	tmp.addActionListener(new MyMenuProcessor(c1,'r')); SubMenu.add(tmp);
        tmp = new MenuItem("Green colormap [g]");
	tmp.addActionListener(new MyMenuProcessor(c1,'g')); SubMenu.add(tmp);
        tmp = new MenuItem("Blue colormap [b]");
	tmp.addActionListener(new MyMenuProcessor(c1,'b')); SubMenu.add(tmp);
        tmp = new MenuItem("Show in overlay [v]");
	tmp.addActionListener(new MyMenuProcessor(c1,'v')); SubMenu.add(tmp);
        tmp = new MenuItem("Show in multiplicative Overlay [V]");
	tmp.addActionListener(new MyMenuProcessor(c1,'V')); SubMenu.add(tmp);

	SubMenu = new Menu("ROIs",false);  // can eventually be dragged to the side
        AddPopupMenu(SubMenu,doSubMenu);
        tmp = new MenuItem("Toggle rectangular ROIs [S]");
	tmp.addActionListener(new MyMenuProcessor(c1,'S')); SubMenu.add(tmp);
        tmp = new MenuItem("Extract with ROI [Y]");
	tmp.addActionListener(new MyMenuProcessor(c1,'Y')); SubMenu.add(tmp);

        SubMenu = new Menu("Processing",false);  // can eventually be dragged to the side
        AddPopupMenu(SubMenu,doSubMenu);
	
        tmp = new MenuItem("Clone element with upcast to float [f]");
	tmp.addActionListener(new MyMenuProcessor(c1,'f')); SubMenu.add(tmp);
        tmp = new MenuItem("Clone displayed element as short [F]");
	tmp.addActionListener(new MyMenuProcessor(c1,'f')); SubMenu.add(tmp);
        tmp = new MenuItem("Mathematically add gate element [+]");
	tmp.addActionListener(new MyMenuProcessor(c1,'+')); SubMenu.add(tmp);
        tmp = new MenuItem("Mathematically subtract from gate element [-]");
	tmp.addActionListener(new MyMenuProcessor(c1,'-')); SubMenu.add(tmp);
        tmp = new MenuItem("Mathematically multiply with gate element [*]");
	tmp.addActionListener(new MyMenuProcessor(c1,'*')); SubMenu.add(tmp);
        tmp = new MenuItem("Ratio with gate element [/]");
	tmp.addActionListener(new MyMenuProcessor(c1,'/')); SubMenu.add(tmp);
        tmp = new MenuItem("Define this element offset from ROI mean [_]");
	tmp.addActionListener(new MyMenuProcessor(c1,'_')); SubMenu.add(tmp);
        tmp = new MenuItem("Delete active element [D]");
	tmp.addActionListener(new MyMenuProcessor(c1,'D')); SubMenu.add(tmp);

	SubMenu = new Menu("Histograms",false);  // can eventually be dragged to the side
        AddPopupMenu(SubMenu,doSubMenu);
        
	tmp = new MenuItem("Select for histogram X-axis [x]");
        tmp.addActionListener(new MyMenuProcessor(c1,'x')); SubMenu.add(tmp);
        tmp = new MenuItem("Select for histogram Y-axis [y]");
	tmp.addActionListener(new MyMenuProcessor(c1,'y')); SubMenu.add(tmp);
        tmp = new MenuItem("Select for histogram Z-axis [z]");
	tmp.addActionListener(new MyMenuProcessor(c1,'z')); SubMenu.add(tmp);
        tmp = new MenuItem("Histogram (<= 3 dimensional) / apply histogram ROI to data [h]");
	tmp.addActionListener(new MyMenuProcessor(c1,'h')); SubMenu.add(tmp);
        tmp = new MenuItem("Force new histogram window [H]");
	tmp.addActionListener(new MyMenuProcessor(c1,'H')); SubMenu.add(tmp);

        label = new PositionLabel("View5D initialization.",c1,c2,c3,data3d);
        grid.add(label);

	c1.ConnectLabel(label);
	c2.ConnectLabel(label);
	c3.ConnectLabel(label);
	setBounds(0, 0, 20, 20);
        //c1.InitScaling();
	c1.UpdateAllNoCoord();
    }
}
