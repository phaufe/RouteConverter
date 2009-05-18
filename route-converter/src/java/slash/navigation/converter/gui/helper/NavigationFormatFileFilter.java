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
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Copyright (C) 2007 Christian Pesch. All Rights Reserved.
*/

package slash.navigation.converter.gui.helper;

import slash.navigation.BaseNavigationFormat;
import slash.navigation.NavigationFormat;
import slash.navigation.util.Files;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Filters files by the extension of a given {@link BaseNavigationFormat}.
 *
 * @author Christian Pesch
 */

public class NavigationFormatFileFilter extends FileFilter {
    private NavigationFormat format;

    public NavigationFormatFileFilter(NavigationFormat format) {
        this.format = format;
    }

    public boolean accept(File f) {
        return f.isDirectory() || format.getExtension().toLowerCase().equals(Files.getExtension(f.getName()).toLowerCase());
    }

    public String getDescription() {
        return format.getName();
    }

    public NavigationFormat getFormat() {
        return format;
    }
}