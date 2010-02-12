package ua.iasa.pathsim.app;

import javax.swing.table.AbstractTableModel;
import ua.iasa.pathsim.domain.testing.*;

public class TaskListModel extends AbstractTableModel {
    public static final String COL_NAME = "Short Name";

    protected final String[] columns = new String[]{COL_NAME};

    protected final TaskRepo taskRepo;

    public TaskListModel(TaskRepo newTaskRepo) {
        this.taskRepo = newTaskRepo;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getColumnCount() {
        return columns.length;
    }

    public int getRowCount() {
        return getTasks().length;
    }

    Task[] getTasks() {
        return taskRepo.findAll();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        final String colName = columns[columnIndex];


        if (COL_NAME.equals(colName)) {
            return getTasks()[rowIndex].getShortDesc();
        }

        return "";
    }
}
