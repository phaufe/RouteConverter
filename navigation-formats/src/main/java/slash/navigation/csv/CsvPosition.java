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
import slash.navigation.base.BaseNavigationPosition;

import java.util.Map;

/**
 * Represents a CSV based position in a route.
 *
 * @author Christian Pesch
 */

public class CsvPosition extends BaseNavigationPosition {
    private BaseCsvFormat format;
    private Map<String, String> properties;

    public CsvPosition(BaseCsvFormat format, Map<String, String> properties) {
        this.format = format;
        this.properties = properties;
    }

    public CsvPosition(BaseCsvFormat format, Double longitude, Double latitude, Double elevation, Double speed, CompactCalendar time, String comment) {
        this.format = format;
        setLongitude(longitude);
        setLatitude(latitude);
        setElevation(elevation);
        setSpeed(speed);
        setTime(time);
        setComment(comment);
    }

    public Double getLongitude() {
        return format.getLongitude(properties);
    }

    public void setLongitude(Double longitude) {
        format.setLongitude(properties, longitude);
    }

    public Double getLatitude() {
        return format.getLatitude(properties);
    }

    public void setLatitude(Double latitude) {
        format.setLatitude(properties, latitude);
    }

    public String getComment() {
        return format.getComment(properties);
    }

    public void setComment(String comment) {
        format.setComment(properties, comment);
    }

    public Double getElevation() {
        return format.getElevation(properties);
    }

    public void setElevation(Double elevation) {
        format.setElevation(properties, elevation);
    }

    public CompactCalendar getTime() {
        return format.getTime(properties);
    }

    public void setTime(CompactCalendar time) {
        format.setTime(properties, time);
    }

    public Double getSpeed() {
        return format.getSpeed(properties);
    }

    public void setSpeed(Double speed) {
        format.setSpeed(properties, speed);
    }


    public String getProperty(String property) {
        return properties.get(property);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsvPosition that = (CsvPosition) o;

        return format.equals(that.format) &&
                properties.equals(that.properties);
    }

    public int hashCode() {
        int result = format.hashCode();
        result = 31 * result + properties.hashCode();
        return result;
    }
}