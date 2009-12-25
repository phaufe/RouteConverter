/*
    This file is part of RouteConverter.

    RouteConverter is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    RouteConverter is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RouteConverter; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Copyright (C) 2007 Christian Pesch. All Rights Reserved.
*/

package slash.navigation.converter.gui.actions;

import slash.navigation.converter.gui.models.PositionsModel;
import slash.navigation.converter.gui.helper.JTableHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * {@link ActionListener} that deletes the selected rows of a {@link JTable}.
 *
 * @author Christian Pesch
 */

public class DeletePositions implements ActionListener {
    private final JTable table;
    private final PositionsModel positionsModel;

    public DeletePositions(JTable table, PositionsModel positionsModel) {
        this.table = table;
        this.positionsModel = positionsModel;
    }

    public void actionPerformed(ActionEvent e) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length > 0) {
            positionsModel.remove(selectedRows);
            final int removeRow = selectedRows[0] > 0 ? selectedRows[0] - 1 : 0;
            if (table.getRowCount() > 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JTableHelper.scrollToPosition(table, removeRow);
                        JTableHelper.selectPositions(table, removeRow, removeRow);
                    }
                });
            }
        }
    }
}