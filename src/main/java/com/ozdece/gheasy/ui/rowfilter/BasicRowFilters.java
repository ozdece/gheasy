package com.ozdece.gheasy.ui.rowfilter;

import javax.swing.*;
import javax.swing.table.TableModel;

public class BasicRowFilters {

    public static RowFilter<TableModel, Integer> caseInsensitiveRegexFilter(String text, int... indices) {
        return RowFilter.regexFilter("(?i)" + text, indices);
    }

}
