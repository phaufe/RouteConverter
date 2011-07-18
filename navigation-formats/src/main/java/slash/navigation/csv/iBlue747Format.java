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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.Math.abs;
import static slash.navigation.csv.Header.Elevation;
import static slash.navigation.csv.Header.Latitude;
import static slash.navigation.csv.Header.Longitude;

/**
 * Reads and writes i-Blue 747 (.csv) files.
 * <p/>
 * Header: INDEX,RCR,DATE,TIME,VALID,LATITUDE,N/S,LONGITUDE,E/W,HEIGHT,SPEED,HEADING,DISTANCE,<br/>
 * Format: 3656,T,2010/12/09,10:59:05,SPS,28.649061,N,17.896196,W,513.863 M,15.862 km/h,178.240250,34.60 M,
 *
 * @author Christian Pesch
 */

public class iBlue747Format extends BaseCsvFormat {
    protected static final Logger log = Logger.getLogger(iBlue747Format.class.getName());

    public String getName() {
        return "i-Blue 747 (*" + getExtension() + ")";
    }

    protected void initializeHeaderToNames(Map<Header, String> headerToNames) {
        headerToNames.put(Longitude, "LONGITUDE");
        headerToNames.put(Latitude,  "LATITUDE");
        headerToNames.put(Elevation, "HEIGHT");
    }

    protected boolean isValidHeader(List<String> headers) {
        return headers.contains("LATITUDE") && headers.contains("N/S") &&       // TODO fix me
                headers.contains("LONGITUDE") && headers.contains("E/W");
    }

    protected boolean isValidPosition(Map<String, String> properties) {
        return properties.get("LATITUDE") != null && properties.get("N/S") != null &&            // TODO fix me
                properties.get("LONGITUDE") != null && properties.get("E/W") != null;
    }

    protected Double getLongitude(Map<String, String> properties) {
        Double longitude = getDouble(properties, Longitude);
        String eastOrWest = properties.get("E/W");
        if ("W".equals(eastOrWest) && longitude != null)
            longitude = -longitude;
       return longitude;
    }

    protected void setLongitude(Map<String, String> properties, Double longitude) {
        putObject(properties, Longitude, abs(longitude));
        properties.put("E/W", longitude < 0.0 ? "W" : "E");
    }

    protected Double getLatitude(Map<String, String> properties) {
        Double latitude = getDouble(properties, Latitude);
        String northOrSouth = properties.get("N/S");
        if ("S".equals(northOrSouth) && latitude != null)
            latitude = -latitude;
        return latitude;
    }

    protected void setLatitude(Map<String, String> properties, Double latitude) {
        putObject(properties, Latitude, abs(latitude));
        properties.put("N/S", latitude < 0.0 ? "S" : "N");
    }

    /* TODO

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    static {
        DATE_FORMAT.setTimeZone(CompactCalendar.UTC);
        TIME_FORMAT.setTimeZone(CompactCalendar.UTC);
    }

    private String formatTime(CompactCalendar time) {
        if (time == null)
            return "";
        return TIME_FORMAT.format(time.getTime());
    }

    private String formatDate(CompactCalendar date) {
        if (date == null)
            return "";
        return DATE_FORMAT.format(date.getTime());
    }

    */
}
