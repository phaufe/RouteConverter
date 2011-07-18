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

package slash.navigation.csv;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static slash.common.TestCase.assertDoubleEquals;

public class MicrosoftExcelFormatTest {
    private MicrosoftExcelFormat format = new MicrosoftExcelFormat();

    @Test
    public void testIsValidHeader() {
        assertTrue(format.isValidHeader(asList("longitude", "latitude", "comment")));
        assertTrue(format.isValidHeader(asList("Longitude", "Latitude", "Comment")));
        assertTrue(format.isValidHeader(asList("LONGITUDE", "LATITUDE", "COMMENT")));
        assertFalse(format.isValidHeader(asList("LONGITUDE", "A", "B", "C")));
    }

    @Test
    public void testIsValidPosition() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("LONgitude", "-1.0");
        properties.put("LATitude", "2.0");
        assertTrue(format.isValidPosition(properties));
        CsvPosition position = format.createPosition(properties);
        assertDoubleEquals(-1.0, position.getLongitude());
        assertDoubleEquals(2.0, position.getLatitude());
    }
}
