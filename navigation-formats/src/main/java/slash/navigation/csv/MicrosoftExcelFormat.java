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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static slash.common.io.Transfer.parseDouble;
import static slash.navigation.csv.Header.Elevation;
import static slash.navigation.csv.Header.Latitude;
import static slash.navigation.csv.Header.Longitude;

/**
 * Reads and writes Microsoft Excel (.csv) files.
 *
 * @author Christian Pesch
 */

public class MicrosoftExcelFormat extends BaseCsvFormat {
    protected static final Logger log = Logger.getLogger(MicrosoftExcelFormat.class.getName());

    public String getName() {
        return "Microsoft Excel (*" + getExtension() + ")";
    }

    protected void initializeHeaderToNames(Map<Header, String> headerToNames) {
        headerToNames.put(Longitude, "Longitude");
        headerToNames.put(Latitude,  "Latitude");
        headerToNames.put(Elevation, "Elevation");
    }

    protected boolean isValidHeader(List<String> headers) {
        return containsHeader(headers, "Longitude") && containsHeader(headers, "Latitude");
    }

    protected boolean isValidPosition(Map<String, String> properties) {
        Set<String> headers = properties.keySet();
        return containsHeader(headers, "Longitude") && containsHeader(headers, "Latitude");   // TODO isValidHeader ?
    }

    private boolean containsHeader(Collection<String> headers, String header) {
        for (String aHeader : headers) {
            if (aHeader.equalsIgnoreCase(header))
                return true;
        }
        return false;
    }

    protected Double getLongitude(Map<String, String> properties) {
        for (String aHeader : new HashSet<String>(properties.keySet())) {
            if (aHeader.equalsIgnoreCase("Longitude"))
                return parseDouble(properties.get(aHeader));
        }
        return null;
    }

    protected void setLongitude(Map<String, String> properties, Double longitude) {
        for (String aHeader : new HashSet<String>(properties.keySet())) {
            if (aHeader.equalsIgnoreCase("Longitude"))
                properties.remove(aHeader);
        }
        super.setLongitude(properties, longitude);
    }

    protected Double getLatitude(Map<String, String> properties) {
        for (String aHeader : new HashSet<String>(properties.keySet())) {
            if (aHeader.equalsIgnoreCase("Latitude"))
                return parseDouble(properties.get(aHeader));
        }
        return null;
    }

    protected void setLatitude(Map<String, String> properties, Double latitude) {
        for (String aHeader : new HashSet<String>(properties.keySet())) {
            if (aHeader.equalsIgnoreCase("Latitude"))
                properties.remove(aHeader);
        }
        super.setLongitude(properties, latitude);
    }
}
