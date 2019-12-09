// To manipulate data to and from the JavaSpace into a JTable I have used this example for a Table
// model class on Github: http://github.com/xrezut/Java-TableModel-Example---A-Contacts-App

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class SellerTableModel extends AbstractTableModel {

    private List<LotItem> lotitem;

    public SellerTableModel(List<LotItem> item) {
        this.lotitem = new ArrayList<>(item);
    }

    @Override
    public int getRowCount() {
        return lotitem.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
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
                name = "Buy Now Price";
                break;
            case 3:
            	name = "Current Highest Bid";
            	break;
            case 4:
            	name = "Sold";
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
                value = item.returnBuyNowValue();
                break;
            case 3:
            	value = item.returnHighestBidValue();
            	break;
            case 4:
            	value = item.returnItemSoldStatus();
            	break;
        }
        return value;
    }

    public LotItem getTask(int row)
    {
        return lotitem.get( row );
    }

    public void addItem(int lot_no, int buyNowValue, String lot_name, String lot_description, String lot_seller, ArrayList<Integer> lot_bid, boolean sold){
        lotitem.add(new LotItem(lot_no, buyNowValue, lot_name, lot_description, lot_seller, lot_bid, sold));
    }

    public void addNewLot(int lot_no, int buyNowValue, String lot_name, String lot_description, String lot_seller, ArrayList<Integer> lot_bid, boolean sold){
        addItem(lot_no, buyNowValue, lot_name, lot_description, lot_seller, lot_bid, sold);
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