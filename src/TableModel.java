import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class TableModel extends AbstractTableModel {

    private List<LotItem> lotitem;

    public TableModel(List<LotItem> item) {
        this.lotitem = new ArrayList<>(item);
    }

    @Override
    public int getRowCount() {
        return lotitem.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
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
        }
        return value;
    }

    public LotItem getTask(int row)
    {
        return lotitem.get( row );
    }

    public void addItem(int lot_no, int buyNowValue, String lot_name, String lot_description, String lot_seller, ArrayList<Integer> lot_bid){
        lotitem.add(new LotItem(lot_no, buyNowValue, lot_name, lot_description, lot_seller, lot_bid));
    }

    public void addNewLot(int lot_no, int buyNowValue, String lot_name, String lot_description, String lot_seller, ArrayList<Integer> lot_bid){
        addItem(lot_no, buyNowValue, lot_name, lot_description, lot_seller, lot_bid);
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