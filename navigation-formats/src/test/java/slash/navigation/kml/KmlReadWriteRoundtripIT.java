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

package slash.navigation.kml;

import slash.navigation.base.NavigationFileParser;
import slash.navigation.base.ReadWriteBase;
import slash.navigation.base.RouteCharacteristics;
import slash.navigation.kml.binding22.AbstractGeometryType;
import slash.navigation.kml.binding22.KmlType;
import slash.navigation.kml.binding22.PlacemarkType;

import java.io.IOException;

public class KmlReadWriteRoundtripIT extends ReadWriteBase {

    private void checkUnprocessed(KmlType kml) {
        assertNotNull(kml);
    }

    public void testKml22Roundtrip() throws IOException {
        readWriteRoundtrip(TEST_PATH + "from22.kml", new NavigationFileParserCallback() {
            public void test(NavigationFileParser source, NavigationFileParser target) {
                KmlRoute sourceRoute = (KmlRoute) source.getAllRoutes().get(0);
                assertEquals(RouteCharacteristics.Route, sourceRoute.getCharacteristics());
                assertNotNull(sourceRoute.getOrigins());
                assertEquals(1, sourceRoute.getOrigins().size());
                checkUnprocessed(sourceRoute.getOrigin(KmlType.class));
                KmlPosition sourceRoutePoint = sourceRoute.getPosition(0);
                assertNotNull(sourceRoutePoint.getOrigin());
                // checkUnprocessed(sourceRoutePoint.getOrigin(PlacemarkType.class));

                KmlRoute sourceTrack = (KmlRoute) source.getAllRoutes().get(1);
                assertEquals(RouteCharacteristics.Track, sourceTrack.getCharacteristics());
                assertNotNull(sourceTrack.getOrigins());
                assertEquals(2, sourceTrack.getOrigins().size());
                checkUnprocessed(sourceTrack.getOrigin(KmlType.class));
                KmlPosition sourceTrackPoint = sourceTrack.getPosition(0);
                assertNotNull(sourceTrackPoint.getOrigin());
                // checkUnprocessed(sourceTrackPoint.getOrigin(AbstractGeometryType.class));

                KmlRoute sourceWaypoints = (KmlRoute) source.getAllRoutes().get(0);
                assertEquals(RouteCharacteristics.Route, sourceWaypoints.getCharacteristics());
                assertNotNull(sourceWaypoints.getOrigins());
                assertEquals(1, sourceWaypoints.getOrigins().size());
                checkUnprocessed(sourceWaypoints.getOrigin(KmlType.class));
                KmlPosition sourceWaypoint = sourceWaypoints.getPosition(0);
                assertNotNull(sourceWaypoint.getOrigin());
                // checkUnprocessed(sourceWaypoint.getOrigin(AbstractGeometryType.class));

                // TODO target wie source oben
            }
        });
    }
}
