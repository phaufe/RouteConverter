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

import slash.common.type.CompactCalendar;
import slash.navigation.base.RouteCharacteristics;
import slash.navigation.base.SimpleFormat;
import slash.navigation.base.SimpleRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a CSV based route.
 *
 * @author Christian Pesch
 */

public class CsvRoute extends SimpleRoute<CsvPosition, BaseCsvFormat> {
    private List<String> headers = new ArrayList<String>(0);

    public CsvRoute(BaseCsvFormat format, RouteCharacteristics characteristics, List<CsvPosition> positions) {
        super(format, characteristics, positions);
    }

    public CsvPosition createPosition(Double longitude, Double latitude, Double elevation, Double speed, CompactCalendar time, String comment) {
        return new CsvPosition(getFormat(), longitude, latitude, elevation, speed, time, comment);
    }

    protected SimpleRoute asSimpleFormat(SimpleFormat format) {
        List<CsvPosition> csvPositions = new ArrayList<CsvPosition>();
        for (CsvPosition position : positions) {
            csvPositions.add(position.asCsvPosition());
        }
        return new CsvRoute((BaseCsvFormat) format, getCharacteristics(), csvPositions);
    }

    protected List<String> getHeaders() {
        return headers;
    }

    protected void setHeaders(List<String> headers) {
        this.headers = headers;
    }
}
