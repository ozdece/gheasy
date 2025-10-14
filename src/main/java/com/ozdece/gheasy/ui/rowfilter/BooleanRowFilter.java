package com.ozdece.gheasy.ui.rowfilter;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.Arrays;

public class BooleanRowFilter extends RowFilter<TableModel, Integer> {

    private final int[] columnIndices;
    private final boolean expectedBoolean;

    public BooleanRowFilter(boolean expectedBoolean, int... indices) {
        this.expectedBoolean = expectedBoolean;
        this.columnIndices = Arrays.stream(indices).toArray();
    }

    @Override
    public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {

        for (int index : columnIndices) {
            final Object value = entry.getValue(index);

            if (!(value instanceof Boolean bool)) {
                return false;
            }

            if (bool != expectedBoolean) {
                return false;
            }

        }

        return true;
    }
}
