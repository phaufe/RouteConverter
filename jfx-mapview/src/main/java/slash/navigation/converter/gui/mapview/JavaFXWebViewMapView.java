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

package slash.navigation.converter.gui.mapview;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import slash.common.io.TokenResolver;
import slash.navigation.base.NavigationPosition;

import java.awt.*;
import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;
import static javafx.concurrent.Worker.State;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import static slash.common.io.Externalization.extractFile;
import static slash.common.io.Transfer.parseDouble;

/**
 * Implementation for a component that displays the positions of a position list on a map
 * using the JavaFX WebView.
 *
 * @author Christian Pesch
 */

public class JavaFXWebViewMapView extends BaseMapView {
    private static final Logger log = Logger.getLogger(JavaFXWebViewMapView.class.getName());
    private static final String GOOGLE_MAPS_SERVER_PREFERENCE = "mapServer";  // TODO push up?
    private static final String DEBUG_PREFERENCE = "debug"; // TODO push up?

    private JFXPanel panel;
    private WebView webView;
    private boolean debug = preferences.getBoolean(DEBUG_PREFERENCE, false); // TODO push up?

    public boolean isSupportedPlatform() {
        return true;
    }

    public Component getComponent() {
        return panel;
    }

    // initialization

    private WebView createWebView() {
        try {
            Group group = new Group();
            panel.setScene(new Scene(group));
            WebView webView = new WebView();
            group.getChildren().add(webView);
            return webView;
        } catch (Throwable t) {
            log.severe("Cannot create WebView: " + t.getMessage());
            setInitializationCause(t);
            return null;
        }
    }

    private boolean loadWebPage() { // TODO unify with EclipseSWTMapView?
        try {
            final String language = Locale.getDefault().getLanguage().toLowerCase();
            final String country = Locale.getDefault().getCountry().toLowerCase();
            File html = extractFile("slash/navigation/converter/gui/mapview/routeconverter.html", country, new TokenResolver() {
                public String resolveToken(String tokenName) {
                    if (tokenName.equals("language"))
                        return language;
                    if (tokenName.equals("country"))
                        return country;
                    if (tokenName.equals("mapserver"))
                        return preferences.get(GOOGLE_MAPS_SERVER_PREFERENCE, "maps.google.com");
                    if (tokenName.equals("maptype"))
                        return preferences.get(MAP_TYPE_PREFERENCE, "roadmap");
                    return tokenName;
                }
            });
            if (html == null)
                throw new IllegalArgumentException("Cannot extract routeconverter.html");
            extractFile("slash/navigation/converter/gui/mapview/contextmenu.js");
            extractFile("slash/navigation/converter/gui/mapview/keydragzoom.js");
            extractFile("slash/navigation/converter/gui/mapview/label.js");
            extractFile("slash/navigation/converter/gui/mapview/latlngcontrol.js");

            final String url = html.toURI().toURL().toExternalForm();
            webView.getEngine().load(url);
            log.fine(currentTimeMillis() + " loadWebPage thread " + Thread.currentThread());
        } catch (Throwable t) {
            log.severe("Cannot create WebBrowser: " + t.getMessage());
            setInitializationCause(t);
            return false;
        }
        return true;
    }

    protected void initializeBrowser() {
        panel = new JFXPanel();

        runLater(new Runnable() {
            public void run() {
                webView = createWebView();
                if (webView == null)
                    return;

                log.info("Using JavaFX WebView to create map view");

                webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                    private int startCount = 0;

                    public void changed(ObservableValue<? extends State> observableValue, State oldState, State newState) {
                        // log.fine("WebView changed observableValue " + observableValue + " oldState " + oldState + " newState " + newState + " thread " + Thread.currentThread());
                        if (newState == SUCCEEDED) {
                            tryToInitialize(startCount++, currentTimeMillis());
                        }
                    }
                });

                if (!loadWebPage())
                    dispose();
            }
        });
    }

    private void tryToInitialize(int count, long start) { // TODO unify with EclipseSWTMapView?
        boolean initialized = getComponent() != null && isMapInitialized();
        synchronized (this) {
            this.initialized = initialized;
        }
        log.fine("Initialized map: " + initialized);

        if (isInitialized()) {
            runBrowserInteractionCallbacksAndTests(start);
        } else {
            long end = currentTimeMillis();
            int timeout = count++ * 100;
            if (timeout > 3000)
                timeout = 3000;
            log.info("Failed to initialize map since " + (end - start) + " ms, sleeping for " + timeout + " ms");

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                // intentionally left empty
            }
            tryToInitialize(count, start);
        }
    }

    private void runBrowserInteractionCallbacksAndTests(long start) { // TODO push up?
        long end = currentTimeMillis();
        log.fine("Starting browser interaction, callbacks and tests after " + (end - start) + " ms");
        initializeAfterLoading();
        initializeBrowserInteraction();
        initializeCallbackListener();
        checkLocalhostResolution();
        checkCallback();
        end = currentTimeMillis();
        log.fine("Browser interaction is running after " + (end - start) + " ms");
    }

    private boolean isMapInitialized() {
        String result = executeScriptWithResult("isInitialized();");
        return parseBoolean(result);
    }

    private void initializeAfterLoading() { // TODO push up?
        resize();
        update(true);
    }

    // resizing

    private boolean hasBeenResizedToInvisible = false;

    public void resize() { // TODO unify with EclipseSWTMapView?
        if (!isInitialized() || !getComponent().isShowing())
            return;

        synchronized (notificationMutex) {
            // if map is not visible remember to update and resize it again
            // once the map becomes visible again
            if (!isVisible()) {
                hasBeenResizedToInvisible = true;
            } else if (hasBeenResizedToInvisible) {
                hasBeenResizedToInvisible = false;
                update(true);
            }
            resizeMap();
        }
    }

    private int lastWidth = -1, lastHeight = -1;

    private void resizeMap() { // TODO push up?
        synchronized (notificationMutex) {
            int width = max(getComponent().getWidth(), 0);
            int height = max(getComponent().getHeight(), 0);
            if (width != lastWidth || height != lastHeight) {
                executeScript("resize(" + width + "," + height + ");");
            }
            lastWidth = width;
            lastHeight = height;
        }
    }

    // bounds and center

    protected NavigationPosition getNorthEastBounds() {
        return extractLatLng("getNorthEastBounds();");
    }

    protected NavigationPosition getSouthWestBounds() {
        return extractLatLng("getSouthWestBounds();");
    }

    protected NavigationPosition getCurrentMapCenter() {
        return extractLatLng("getCenter();");
    }

    protected Integer getCurrentZoom() {
        Double zoom = parseDouble(executeScriptWithResult("getZoom();"));
        return zoom != null ? zoom.intValue() : null;
    }

    protected String getCallbacks() {
        return executeScriptWithResult("getCallbacks();");
    }

    // script execution

    protected void executeScript(final String script) {
        if (webView == null || script.length() == 0)
            return;

        if (!isFxApplicationThread()) {
            runLater(new Runnable() {
                public void run() {
                    webView.getEngine().executeScript(script);
                    logJavaScript(script, null);
                }
            });
        } else {
            webView.getEngine().executeScript(script);
            logJavaScript(script, null);
        }
    }

    protected String executeScriptWithResult(final String script) {
        if (script.length() == 0)
            return null;

        final boolean pollingCallback = !script.contains("getCallbacks");
        final Object[] result = new Object[1];
        if (!isFxApplicationThread()) {
            runLater(new Runnable() {
                public void run() {
                    result[0] = webView.getEngine().executeScript(script);
                    if (debug && pollingCallback) {
                        log.info("After invokeLater, executeJavascriptWithResult " + result[0]);
                    }
                }
            });
        } else {
            result[0] = webView.getEngine().executeScript(script);
            if (debug && pollingCallback) {
                log.info("After executeJavascriptWithResult " + result[0]);
            }
        }

        if (pollingCallback) {
            logJavaScript(script, result[0]);
        }
        return result[0] != null ? result[0].toString() : null;
    }
}
