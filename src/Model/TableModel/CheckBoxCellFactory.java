package Model.TableModel;

import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * Created by vn130 on 11/17/2015.
 */
public class CheckBoxCellFactory implements Callback {
    @Override
    public Object call(Object param) {
        return new CheckBoxTableCell<MessageTableModel,Boolean>();
    }
}
