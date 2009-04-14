//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.02.17 at 01:40:15 PM MEZ
//


package slash.navigation.kml.binding20;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://earth.google.com/kml/2.0}extrude" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}tessellate" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}altitudeMode" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}MultiGeometry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}MultiLineString" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}MultiPoint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}MultiPolygon" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}LineString" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}Point" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}Polygon" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.google.com/kml/2.0}Placemark" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "extrudeOrTessellateOrAltitudeMode"
})
@XmlRootElement(name = "MultiGeometry")
public class MultiGeometry {

    @XmlElementRefs({
        @XmlElementRef(name = "Polygon", namespace = "http://earth.google.com/kml/2.0", type = Polygon.class),
        @XmlElementRef(name = "altitudeMode", namespace = "http://earth.google.com/kml/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "MultiLineString", namespace = "http://earth.google.com/kml/2.0", type = MultiLineString.class),
        @XmlElementRef(name = "extrude", namespace = "http://earth.google.com/kml/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "MultiGeometry", namespace = "http://earth.google.com/kml/2.0", type = MultiGeometry.class),
        @XmlElementRef(name = "MultiPoint", namespace = "http://earth.google.com/kml/2.0", type = MultiPoint.class),
        @XmlElementRef(name = "tessellate", namespace = "http://earth.google.com/kml/2.0", type = JAXBElement.class),
        @XmlElementRef(name = "Placemark", namespace = "http://earth.google.com/kml/2.0", type = Placemark.class),
        @XmlElementRef(name = "Point", namespace = "http://earth.google.com/kml/2.0", type = Point.class),
        @XmlElementRef(name = "MultiPolygon", namespace = "http://earth.google.com/kml/2.0", type = MultiPolygon.class),
        @XmlElementRef(name = "LineString", namespace = "http://earth.google.com/kml/2.0", type = LineString.class)
    })
    protected List<Object> extrudeOrTessellateOrAltitudeMode;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;

    /**
     * Gets the value of the extrudeOrTessellateOrAltitudeMode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extrudeOrTessellateOrAltitudeMode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtrudeOrTessellateOrAltitudeMode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Polygon }
     * {@link MultiLineString }
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * {@link MultiGeometry }
     * {@link MultiPoint }
     * {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     * {@link Point }
     * {@link Placemark }
     * {@link MultiPolygon }
     * {@link LineString }
     * 
     * 
     */
    public List<Object> getExtrudeOrTessellateOrAltitudeMode() {
        if (extrudeOrTessellateOrAltitudeMode == null) {
            extrudeOrTessellateOrAltitudeMode = new ArrayList<Object>();
        }
        return this.extrudeOrTessellateOrAltitudeMode;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
