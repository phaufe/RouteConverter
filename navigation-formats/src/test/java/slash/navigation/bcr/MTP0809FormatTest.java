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

package slash.navigation.bcr;

import org.junit.Test;
import slash.navigation.base.ParserContext;
import slash.navigation.base.ParserContextImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static slash.navigation.base.BaseNavigationFormat.DEFAULT_ENCODING;

public class MTP0809FormatTest {
    private MTP0809Format format = new MTP0809Format();
    private BcrRoute route = new BcrRoute(format, "RouteName", Arrays.asList("Description1", "Description2"), Arrays.asList(new BcrPosition(1, 2, 3, "Start"), new BcrPosition(3, 4, 5, "WP,End,@,,0,")));

    @Test
    public void testReadComment() throws IOException {
        StringWriter writer = new StringWriter();
        format.write(route, new PrintWriter(writer), 0, 2);
        ParserContext<BcrRoute> context = new ParserContextImpl<BcrRoute>();
        format.read(new BufferedReader(new StringReader(writer.toString())), null, DEFAULT_ENCODING, context);
        List<BcrRoute> routes = context.getRoutes();
        assertEquals(1, routes.size());
        BcrRoute route = routes.get(0);
        List<BcrPosition> positions = route.getPositions();
        assertEquals(2, positions.size());
        BcrPosition position1 = positions.get(0);
        assertEquals("Start", position1.getComment());
        BcrPosition position2 = positions.get(1);
        assertEquals("End,@", position2.getComment());
    }

    @Test
    public void testWriteComment() {
        StringWriter writer = new StringWriter();
        format.write(route, new PrintWriter(writer), 0, 2);
        String string = writer.toString();
        assertTrue(string.contains("STATION1=WP,Start,,0,"));
        assertTrue(string.contains("STATION2=WP,End,@,,0,"));
    }
}