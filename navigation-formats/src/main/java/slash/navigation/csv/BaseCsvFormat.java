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
import slash.navigation.base.NavigationPosition;
import slash.navigation.base.ParserContext;
import slash.navigation.base.RouteCharacteristics;
import slash.navigation.base.SimpleFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static slash.common.io.Transfer.parseDouble;
import static slash.common.type.CompactCalendar.fromDate;
import static slash.navigation.base.RouteCharacteristics.Waypoints;
import static slash.navigation.csv.Header.Comment;
import static slash.navigation.csv.Header.Elevation;
import static slash.navigation.csv.Header.Latitude;
import static slash.navigation.csv.Header.Longitude;
import static slash.navigation.csv.Header.Speed;

/**
 * The base of all CSV formats.
 *
 * @author Christian Pesch
 */

public abstract class BaseCsvFormat extends SimpleFormat<CsvRoute> {
    protected static final Logger log = Logger.getLogger(BaseCsvFormat.class.getName());

    protected static final char SEPARATOR = ',';

    private Map<Header, String> headerToNames = new HashMap<Header, String>();

    protected BaseCsvFormat() {
        initializeHeaderToNames(headerToNames);
    }

    protected abstract void initializeHeaderToNames(Map<Header, String> headerToNames);

    public String getExtension() {
        return ".csv";
    }

    protected RouteCharacteristics getRouteCharacteristics() {
        return Waypoints;
    }

    public int getMaximumPositionCount() {
        return UNLIMITED_MAXIMUM_POSITION_COUNT;
    }

    @SuppressWarnings("unchecked")
    public <P extends NavigationPosition> CsvRoute createRoute(RouteCharacteristics characteristics, String name, List<P> positions) {
        return new CsvRoute(this, characteristics, (List<CsvPosition>) positions);
    }

    protected CsvPosition createPosition(Map<String, String> properties) {
        return new CsvPosition(this, properties);
    }

    public void read(BufferedReader reader, CompactCalendar startDate, String encoding, ParserContext<CsvRoute> context) throws IOException {
        List<CsvPosition> positions = new ArrayList<CsvPosition>();

        int lineCount = 0;
        List<String> headers = new ArrayList<String>(0);
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            if (line.length() == 0)
                continue;

            List<String> values = asList(line.split(Character.toString(SEPARATOR)));

            // try to read header definition
            if (headers.size() == 0) {
                if (isValidHeader(values))
                    headers.addAll(values);

                // a header has to be defined in the first five lines of a file
                if (lineCount++ > 5)
                    return;

                continue;
            }

            // read position from line
            Map<String, String> properties = new HashMap<String, String>();
            for (int i = 0; i < values.size(); i++) {
                properties.put(headers.get(i), values.get(i));
            }

            if (isValidPosition(properties)) {
                positions.add(createPosition(properties));
            }
        }

        if (positions.size() > 0) {
            CsvRoute route = createRoute(getRouteCharacteristics(), null, positions);
            route.setHeaders(headers);
            context.appendRoute(route);
        }
    }

    protected abstract boolean isValidHeader(List<String> headers);
    protected abstract boolean isValidPosition(Map<String, String> properties);

    protected String getString(Map<String, String> properties, Header header) {
        String key = headerToNames.get(header);
        return properties.get(key);
    }

    protected void putString(Map<String, String> properties, Header header, String value) {
        String key = headerToNames.get(header);
        properties.put(key, value);
    }

    protected Double getDouble(Map<String, String> properties, Header header) {
        return parseDouble(getString(properties, header));
    }

    protected void putObject(Map<String, String> properties, Header header, Object value) {
        putString(properties, header, value != null ? value.toString() : null);
    }


    protected Double getLongitude(Map<String, String> properties) {
        return getDouble(properties, Longitude);
    }

    protected void setLongitude(Map<String, String> properties, Double longitude) {
        putObject(properties, Longitude, longitude);
    }

    protected Double getLatitude(Map<String, String> properties) {
        return getDouble(properties, Latitude);
    }

    protected void setLatitude(Map<String, String> properties, Double latitude) {
        putObject(properties, Latitude, latitude);
    }

    protected String getComment(Map<String, String> properties) {
        return getString(properties, Comment);
    }

    protected void setComment(Map<String, String> properties, String comment) {
        putObject(properties, Comment, comment);
    }

    protected Double getElevation(Map<String, String> properties) {
        return getDouble(properties, Elevation);
    }

    protected void setElevation(Map<String, String> properties, Double elevation) {
        putObject(properties, Elevation, elevation);
    }

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    protected CompactCalendar getTime(Map<String, String> properties) {
        String time = properties.get("TIME"); // TODO fix me
        if (time != null)
            try {
                Date parsed = TIME_FORMAT.parse(time);
                return fromDate(parsed);
            } catch (ParseException e) {
                log.severe("Could not parse time '" + time + "'");
            }
        return null;
    }

    protected void setTime(Map<String, String> properties, CompactCalendar time) {
        properties.put("TIME", time != null ? TIME_FORMAT.format(time.getTime()) : null);  // TODO fix me
    }

    protected Double getSpeed(Map<String, String> properties) {
        return getDouble(properties, Speed);
    }

    protected void setSpeed(Map<String, String> properties, Double speed) {
        putObject(properties, Speed, speed);
    }

    @SuppressWarnings("unchecked")
    public void write(CsvRoute route, PrintWriter writer, int startIndex, int endIndex) throws IOException {
        List<CsvPosition> positions = route.getPositions();
        writeHeader(route, writer);
        for (int i = startIndex; i < endIndex; i++) {
            writePosition(positions.get(i), writer, route);
        }
    }

    protected void writeHeader(CsvRoute route, PrintWriter writer) {
        List<String> headers = route.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            writer.write(header);
            if (i < headers.size() - 1)
                writer.write(SEPARATOR);
        }
        writer.println();
    }

    protected void writePosition(CsvPosition position, PrintWriter writer, CsvRoute route) {
        List<String> headers = route.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            writer.write(position.getProperty(header));
            if (i < headers.size() - 1)
                writer.write(SEPARATOR);
        }
        writer.println();
    }
}
