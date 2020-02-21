/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadmanager;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;


/**
 *
 * @author Vijay
 */
class ProgressRenderer extends JProgressBar implements TableCellRenderer{

    public ProgressRenderer(int min, int max) {
        super(min, max);
    }
    @Override
    public Component getTableCellRendererComponent(
    JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
        setValue((int)((Float)value).floatValue());
        return this;
    }

}
