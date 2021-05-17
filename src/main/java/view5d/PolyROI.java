/*-
 * #%L
 * View5D plugin for Fiji.
 * %%
 * Copyright (C) 2006 - 2021 Fiji developers.
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

// A ROI class based on three Polygons who's orthogonal overlap is the 3D ROI
class PolyROI extends ROI {
    Polygon ROIPolygons[];         // 3 polygons for sectioning

     PolyROI() {
        super();
        ROIPolygons = new Polygon[3];
        ROIPolygons[0] = null;ROIPolygons[1] = null;ROIPolygons[2] = null;
        }

     double GetROISize(int dim) {
        return 0;
     }
    
    void TakePolyROIs(Polygon PS[]) {
        ROIPolygons = PS;
    }
        
    boolean InROIRange(int x,int y, int z) {
        // The problem with the "contains" method is that the right and lower pixels are not inside, if equal to boundary of Polygon
            if (ROIPolygons[2] != null)
                if (! ROIPolygons[2].intersects(x-0.05,y-0.05,0.1,0.1)) // contains((double)x,(double)y))
                    return false;
            if (ROIPolygons[1] != null)
                if (! ROIPolygons[1].intersects(x-0.05,z-0.05,0.1,0.1)) // contains(x,z))
                    return false;
            if (ROIPolygons[0] != null)
                if (! ROIPolygons[0].intersects(z-0.05,y-0.05,0.1,0.1)) // contains(z,y))
                    return false;
            return true;
        }
}
