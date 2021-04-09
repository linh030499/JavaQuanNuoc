/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quanlynuoc.Main;

import Database.DB;
import Models.Billinfo;
import Models.Drink;
import Models.DrinkCategory;
import Models.bill;
import Models.tabledrink;
import java.awt.Button;
import java.awt.Color;
import java.awt.ItemSelectable;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import quanlynuoc.FormDangNhap;

/**
 *
 * @author PHUOC-PC
 */
public class Main extends javax.swing.JFrame{

    /**
     * Creates new form Main
     */
    public int STT=0;
    public static int curtable;
    DB db = new DB();
    String[] columns = {"Tên","Loại","Đơn Giá","Số lượng","Thành tiền"};
    DefaultTableModel tablemodel = new DefaultTableModel(columns,0);
    public void loadbantrong()
    {
        ArrayList<tabledrink> tables = db.gettabletrong();
        for(tabledrink table : tables)
        {
            cbbchuyenban.addItem(table.name);
        }
    }
    public Drink getdrinkbyname(String name)
    {
        ArrayList<Drink> drinks = db.getDrinks();
        for(Drink drink : drinks)
        {
            if(drink.name.equals(name))
            {
                return drink;
            }
        }
        return new Drink();
    }
    public void loadCbb()
    {
        final ArrayList<DrinkCategory> categories = db.getCategories();
        
        for(DrinkCategory category : categories)
        {
            
            CbbCate.addItem(category.name);
        }
        
        CbbCate.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent arg0){
                if(arg0.getStateChange()==ItemEvent.SELECTED)
                {
                    CbbName.removeAllItems();
                    ArrayList<Drink> drinks = new ArrayList<>();
                    for(DrinkCategory category : categories)
                    {
                        if(CbbCate.getSelectedItem().equals(category.name))
                        {
                            drinks=db.getDrinkbyCateId(String.valueOf(category.id));
                        }
                    }
                    for(Drink drink : drinks)
                    {
                        System.out.println(drink.name);
                        CbbName.addItem(drink.name);
                    }
                }
            }
        });
    }
    public void loadTableAfter()
    {
        tablemodel.setRowCount(0);
        ArrayList<tabledrink> tablebef= db.gettabledrink();
        ArrayList<Billinfo> billinfo = db.getbillinfobyTableId(String.valueOf(curtable));
         ArrayList<Drink> drinks= new ArrayList<>();
         int subtotalbef=0;
          for(int j =0; j<billinfo.size();j++)
            {
                drinks.add(db.getDrinkbyId(String.valueOf(billinfo.get(j).idDrink)));
            }
            for(int l=0;l<drinks.size();l++)
            {
                 String[] rowsdata = {drinks.get(l).name,
                       String.valueOf(drinks.get(l).CateId),
                       String.valueOf(drinks.get(l).price),
                       String.valueOf(billinfo.get(l).count),
                       String.valueOf(drinks.get(l).price*billinfo.get(l).count)};
                   tablemodel.addRow(rowsdata);
                subtotalbef+=drinks.get(l).price*billinfo.get(l).count;
            }
            
           txtsubtotal.setText(String.valueOf(subtotalbef));
    }
    public void loadTable()
    {
        
        int x=0;
        int y=0;
        String[] rows = {};
        final ArrayList<tabledrink> tablebef= db.gettabledrink();
        for(int i=0;i<tablebef.size();i++)
        {
            if(db.getuncheckbillbyTableId(String.valueOf(tablebef.get(i).id)).DateCheckIn!=null)
            {
                db.updatetableStatus(String.valueOf(1), String.valueOf(tablebef.get(i).id));
            }
            else{
                db.updatetableStatus(String.valueOf(0), String.valueOf(tablebef.get(i).id));
            }
        }
        
        final ArrayList<tabledrink> tables= db.gettabledrink();
        for(int i=0;i<tables.size();i++)
        {
            final int k=i;
            final int tableid=tables.get(i).id;
            final ArrayList<Billinfo> billinfo = db.getbillinfobyTableId(String.valueOf(tables.get(i).id));
            final ArrayList<Drink> drinks= new ArrayList<>();
            for(int j =0; j<billinfo.size();j++)
            {
                drinks.add(db.getDrinkbyId(String.valueOf(billinfo.get(j).idDrink)));
            }
           
            JButton btn = new JButton();
            Window window = SwingUtilities.windowForComponent( btn );
            
            if(tables.get(i).status)
            {
                btn.setBackground(Color.red);
                btn.setText("<html>"+tables.get(i).name+"<br />"+"Có người"+"</html>");
            }
            else
            {
                btn.setText("<html>"+tables.get(i).name+"<br />"+"Trống"+"</html>");
            }
            btn.setSize(80, 80);
            btn.setLocation(x, y);
         
            btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               curtable = tables.get(k).id;
               int subtotal=0;
               tablemodel.setRowCount(0);
               for(int l=0;l<drinks.size();l++)
               {
                   String[] rowsdata = {drinks.get(l).name,
                       String.valueOf(drinks.get(l).CateId),
                       String.valueOf(drinks.get(l).price),
                       String.valueOf(billinfo.get(l).count),
                       String.valueOf(drinks.get(l).price*billinfo.get(l).count)};
                   tablemodel.addRow(rowsdata);
                   subtotal+=drinks.get(l).price*billinfo.get(l).count;
               }
               txtsubtotal.setText(String.valueOf(subtotal));
            }
            });
            ListTable.add(btn);
            ListTable.revalidate();
            ListTable.repaint();
            if(x<ListTable.getWidth()-200)
            {
                x=x+100;
            }
            else{
                x=0;
                y=y+100;
            }
            
        }
        TableBillinfo.setModel(tablemodel);
    }
    public Main() {
        initComponents();
        loadTable();
        loadCbb();
        loadbantrong();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        BtnThemmon = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        ListTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableBillinfo = new javax.swing.JTable();
        CbbCate = new javax.swing.JComboBox<>();
        CbbName = new javax.swing.JComboBox<>();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        txtsubtotal = new javax.swing.JTextField();
        cbbchuyenban = new javax.swing.JComboBox<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 314, Short.MAX_VALUE)
        );

        BtnThemmon.setText("Thêm món");
        BtnThemmon.setToolTipText("");
        BtnThemmon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThemmonActionPerformed(evt);
            }
        });

        jButton2.setText("Thanh toán");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Chuyển bàn");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ListTableLayout = new javax.swing.GroupLayout(ListTable);
        ListTable.setLayout(ListTableLayout);
        ListTableLayout.setHorizontalGroup(
            ListTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 457, Short.MAX_VALUE)
        );
        ListTableLayout.setVerticalGroup(
            ListTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 314, Short.MAX_VALUE)
        );

        TableBillinfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(TableBillinfo);

        CbbName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbbNameActionPerformed(evt);
            }
        });

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));
        jSpinner1.setValue(1);

        jLabel1.setText("Tổng tiền");

        txtsubtotal.setEnabled(false);

        jMenu1.setText("File");

        jMenuItem2.setText("Thoát");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setText("jMenuItem1");
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ListTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbbchuyenban, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addGap(1098, 1098, 1098))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(CbbName, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CbbCate, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(BtnThemmon))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(491, 491, 491)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtsubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CbbName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnThemmon, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CbbCate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ListTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtsubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(31, 31, 31))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cbbchuyenban, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    @SuppressWarnings("empty-statement")
    private void BtnThemmonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThemmonActionPerformed
        // TODO add your handling code here:
        Random rand = new Random();
        boolean was_found=false;
// Obtain a number between [0 - 49].
        int n = rand.nextInt(10000);
        ArrayList<Billinfo> billinfos = db.getbillinfobyTableId(String.valueOf(curtable));
            if(db.getuncheckbillbyTableId(String.valueOf(curtable)).DateCheckIn!=null)
            {
                for(int i=0;i<billinfos.size();i++)
                {
                    if(getdrinkbyname(CbbName.getSelectedItem().toString()).id==billinfos.get(i).idDrink&&was_found==false)
                    {
                        int newquantity=billinfos.get(i).count+Integer.parseInt(jSpinner1.getValue().toString());
                        db.updatedrinkbillinfo(String.valueOf(newquantity), 
                                String.valueOf(billinfos.get(i).idDrink),
                                String.valueOf(db.getuncheckbillbyTableId(String.valueOf(curtable)).id));
                        was_found=true;
                    }
                }
                if(was_found==false)
                {
                    db.insertbillinfo(String.valueOf(db.getuncheckbillbyTableId(String.valueOf(curtable)).id),
                    String.valueOf(getdrinkbyname(CbbName.getSelectedItem().toString()).id), 
                    jSpinner1.getValue().toString());
                }
            }
            else{
                db.insertbill(String.valueOf(n),String.valueOf(curtable),String.valueOf(0));
                for(int i=0;i<billinfos.size();i++)
                {
                    if((getdrinkbyname(CbbName.getSelectedItem().toString()).id==billinfos.get(i).idDrink&&was_found==false))
                    {
                        int newquantity=billinfos.get(i).count+Integer.parseInt(jSpinner1.getValue().toString());
                        db.updatedrinkbillinfo(String.valueOf(newquantity), 
                                String.valueOf(billinfos.get(i).idDrink),
                                String.valueOf(n));
                        was_found=true;
                    }
                }
                if(was_found==false)
                {
                    db.insertbillinfo(String.valueOf(n),
                                String.valueOf(getdrinkbyname(CbbName.getSelectedItem().toString()).id), 
                                jSpinner1.getValue().toString());
                }
            }
        tablemodel.setRowCount(0);
        final ArrayList<Billinfo> billinfo = db.getbillinfobyTableId(String.valueOf(curtable));
        final ArrayList<Drink> drinks= new ArrayList<>();
        for(int j =0; j<billinfo.size();j++)
        {
            drinks.add(db.getDrinkbyId(String.valueOf(billinfo.get(j).idDrink)));
        }
        for(int i=0;i<drinks.size();i++)
               {
                   String[] rowsdata = {drinks.get(i).name,
                       String.valueOf(drinks.get(i).CateId),
                       String.valueOf(drinks.get(i).price),
                       String.valueOf(billinfo.get(i).count)};
                   tablemodel.addRow(rowsdata);
               }
        ListTable.removeAll();
        loadTable();
        loadTableAfter();
    }//GEN-LAST:event_BtnThemmonActionPerformed

    private void CbbNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbbNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CbbNameActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        boolean wasfound=false;
        ArrayList<tabledrink> tables = db.gettabledrink();
        int tableid=-1;
        for(tabledrink table : tables)
        {
            System.out.println(cbbchuyenban.getSelectedItem().toString());
            System.out.println(table.name);
            if(table.name.equals(cbbchuyenban.getSelectedItem().toString())&&wasfound==false)
            {
                tableid=table.id;
                wasfound=true;
            }
        }
        bill bill=db.getbillByTableId(String.valueOf(curtable));
        
        db.updateTable(String.valueOf(bill.id),String.valueOf(tableid));
        ListTable.removeAll();
        loadTable();
        loadTableAfter();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        bill bill = db.getbillByTableId(String.valueOf(curtable));
        db.checkoutbill(String.valueOf(bill.id));
        ListTable.removeAll();
        loadTable();
        loadTableAfter();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnThemmon;
    private javax.swing.JComboBox<String> CbbCate;
    private javax.swing.JComboBox<String> CbbName;
    private javax.swing.JPanel ListTable;
    private javax.swing.JTable TableBillinfo;
    private javax.swing.JComboBox<String> cbbchuyenban;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField txtsubtotal;
    // End of variables declaration//GEN-END:variables
}
