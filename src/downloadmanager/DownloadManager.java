/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadmanager;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 *
 * @author Vijay
 */


public class DownloadManager extends JFrame implements Observer  {

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DownloadManager man=new DownloadManager();
                man.setVisible(true);
            }
        });
    }
    
    private JTextField addTextField;
    private JTable table;
    private JButton pauseButton,resumeButton,cancelButton,clearButton;
    
    private DownloadsTableModel tableModel;//download table's data model
    private Download selectedDownload;
    
    private boolean clearing;//table selection is being cleared or not
    
    
    public DownloadManager() {
        setTitle("Downlaod Manager");
        setSize(640,480);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                actionExit();
            }
        });
        
        JMenuBar menuBar=new JMenuBar();
        JMenu fileMenu=new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem=new JMenuItem("Exit",KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e){
                actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        JPanel addPanel=new JPanel();
        addTextField=new JTextField(30);
        addPanel.add(addTextField);
        JButton addButton =new JButton("Add Download");
        addButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e){
                actionAdd();
            }
        });
        
        addPanel.add(addButton);
        tableModel=new DownloadsTableModel();
        table=new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
        //one row at a time
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        ProgressRenderer renderer=new ProgressRenderer(0,100);
        renderer.setStringPainted(true);
        table.setDefaultRenderer(JProgressBar.class, renderer);
        
        table.setRowHeight((int)renderer.getPreferredSize().getHeight());
        
        JPanel downloadsPanel=new JPanel();
        downloadsPanel.setBorder(
                BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table),BorderLayout.CENTER);
        
        JPanel buttonsPanel=new JPanel();
        pauseButton=new JButton("Pause");
        pauseButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                actionPause();
            }
        });
        pauseButton.setEnabled(false);
        buttonsPanel.add(pauseButton);
        resumeButton=new JButton("Resume");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionResume();
            }
        });
        resumeButton.setEnabled(false);
        buttonsPanel.add(resumeButton);
        cancelButton=new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionClear();
            }
        });
        cancelButton.setEnabled(false);
        buttonsPanel.add(cancelButton);
        clearButton=new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionClear();
            }
        });
        
        clearButton.setEnabled(false);
        buttonsPanel.add(clearButton);
        
        //button display
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(addPanel,BorderLayout.NORTH);
        getContentPane().add(downloadsPanel,BorderLayout.CENTER);
        getContentPane().add(buttonsPanel,BorderLayout.SOUTH);
    }
    
    private void actionExit(){System.exit(0);}
    
    private void actionAdd(){
        URL verifiedUrl=verifyUrl(addTextField.getText());
        if(verifiedUrl!=null){
            tableModel.addDownload(new Download(verifiedUrl));
            addTextField.setText("");
        }
        else{
            JOptionPane.showMessageDialog(this, "Invalid Download URL","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private URL verifyUrl(String url){
        //only http
        if(!url.toLowerCase().startsWith("http://"))
            return null;
        URL verifiedUrl=null;
        try{
            verifiedUrl=new URL(url);
            
        }
        catch(Exception e){
            return null;
        }
        if(verifiedUrl.getFile().length()<2)
            return null;
        return verifiedUrl;
    }
    private void tableSelectionChanged(){
        if(selectedDownload!=null)
            selectedDownload.deleteObserver(DownloadManager.this);
        if(!clearing&&table.getSelectedRow()>-1){
            selectedDownload=tableModel.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(DownloadManager.this);
            updateButtons();
        }
    }
    
    private void actionPause(){
        selectedDownload.pause();
        updateButtons();
    }
    
    private void actionResume(){
        selectedDownload.resume();
        updateButtons();
    }
    
    private void actionCancel(){
        selectedDownload.cancel();
        updateButtons();
    }
    
    private void actionClear(){
        clearing=true;
        tableModel.clearDownload(table.getSelectedRow());
        clearing=false;
        selectedDownload=null;
        updateButtons();
    }
    
    private void updateButtons(){
        if(selectedDownload!=null){
            int status=selectedDownload.getStatus();
            switch(status){
                case Download.DOWNLOADING:
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.PAUSED:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.ERROR:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;
                default://cancel  complete
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    
                }
        }
        else{
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            clearButton.setEnabled(false);
                
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if(selectedDownload!=null&&selectedDownload.equals(o))
            updateButtons();
    }
    
    
    
    
    
    
}
