package JTableModels;// To manipulate data to and from the JavaSpace into a JTable I have used this example for a Table
// model class on Github: http://github.com/xrezut/Java-TableModel-Example---A-Contacts-App

import LotSpace.LotItem;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class BuyerTableModel extends AbstractTableModel {

    private List<LotItem> lotitem;

    public BuyerTableModel(List<LotItem> item) {
        this.lotitem = new ArrayList<>(item);
    }

    @Override
    public int getRowCount() {
        return lotitem.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        String name = "??";
        switch (column) {
            case 0:
                name = "Lot ID";
                break;
            case 1:
                name = "Name";
                break;
            case 2:
                name = "Seller Name";
                break;
            case 3:
                name = "Final Price";
                break;
        }
        return name;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class type = String.class;
        switch (columnIndex) {
            case 0:
            case 1:
                type = Integer.class;
                break;
        }
        return type;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LotItem item = lotitem.get(rowIndex);
        Object value = null;
        switch (columnIndex) {
            case 0:
                value = item.returnLotNo();
                break;
            case 1:
                value = item.returnLotName();
                break;
            case 2:
                value = item.returnSellerName();
                break;
            case 3:
                value = item.returnFinalPriceValue();
                break;
        }
        return value;
    }

    public LotItem getTask(int row)
    {
        return lotitem.get( row );
    }

    public void addItem(int lot_no, int buyNowValue, String lot_name, String lot_description, String lot_seller, ArrayList<Integer> lot_bid, boolean sold, String buyerName, Integer lotFinalPrice){
        lotitem.add(new LotItem(lot_no, buyNowValue, lot_name, lot_description, lot_seller, lot_bid, sold, buyerName, lotFinalPrice));
    }

    public void addNewLot(int lot_no, int buyNowValue, String lot_name, String lot_description, String lot_seller, ArrayList<Integer> lot_bid, boolean sold, String buyerName, Integer lotFinalPrice){
        addItem(lot_no, buyNowValue, lot_name, lot_description, lot_seller, lot_bid, sold, buyerName, lotFinalPrice);
        fireTableDataChanged();
    }


    public void alignTable(JTable table){
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        table.getColumnModel().getColumn(0).setCellRenderer(renderer);
        table.getColumnModel().getColumn(1).setCellRenderer(renderer);
        table.getColumnModel().getColumn(2).setCellRenderer(renderer);
    }
}