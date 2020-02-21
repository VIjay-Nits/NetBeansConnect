/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadmanager;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author Vijay
 */
public class DownloadsTableModel extends AbstractTableModel implements Observer{
    private static final String[] columnNames={"URL","Size","Progress","Status"};
    private static final Class[] columnClasses={String.class,String.class,JProgressBar.class,String.class};
    private ArrayList<Download> downloadList=new ArrayList<Download>();
    

    @Override
    public int getRowCount() {
        return downloadList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
         }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Download dld=downloadList.get(rowIndex);
        switch(columnIndex){
            case 0:return dld.getUrl();//URL
            case 1: return (dld.getSize()==-1)?"":Integer.toString(dld.getSize());
            case 2: return dld.getProgress();
            case 3: return Download.STATUSES[dld.getStatus()];
        }
        return "";
        }

    @Override
    public void update(Observable o, Object arg) {
        int index=downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);
        }
    
    public void addDownload(Download download){
        download.addObserver(this);
        downloadList.add(download);
        fireTableRowsInserted(getRowCount()-1,getRowCount()-1);
    }
    public Download getDownload(int row){
        return downloadList.get(row);
    }
    public void clearDownload(int row){
        downloadList.remove(row);
        fireTableRowsDeleted(row,row);
    }
    @Override
    public String getColumnName(int col){
        return columnNames[col];
    }
    
    
    
    
}
