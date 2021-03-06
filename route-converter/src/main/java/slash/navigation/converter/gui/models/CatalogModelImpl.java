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

package slash.navigation.converter.gui.models;

import slash.navigation.catalog.domain.Catalog;
import slash.navigation.catalog.domain.Category;
import slash.navigation.catalog.domain.Route;
import slash.navigation.catalog.model.CategoryTreeModel;
import slash.navigation.catalog.model.CategoryTreeNode;
import slash.navigation.catalog.model.CategoryTreeNodeImpl;
import slash.navigation.catalog.model.RouteComparator;
import slash.navigation.catalog.model.RouteModel;
import slash.navigation.catalog.model.RoutesTableModel;
import slash.navigation.converter.gui.helper.RouteServiceOperator;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.sort;
import static slash.navigation.converter.gui.helper.JTreeHelper.asNames;
import static slash.navigation.converter.gui.helper.JTreeHelper.asParents;

/**
 * Acts as a {@link TreeModel} for the categories and routes of a {@link Catalog}.
 *
 * @author Christian Pesch
 */

public class CatalogModelImpl implements CatalogModel {
    private static final RouteComparator routeComparator = new RouteComparator();
    private final CategoryTreeModel categoryTreeModel;
    private final RoutesTableModel routesTableModel = new RoutesTableModel();
    private final RouteServiceOperator operator;

    public CatalogModelImpl(CategoryTreeNode root, RouteServiceOperator operator) {
        categoryTreeModel = new CategoryTreeModel(root);
        this.operator = operator;
    }

    public CategoryTreeModel getCategoryTreeModel() {
        return categoryTreeModel;
    }

    public RoutesTableModel getRoutesTableModel() {
        return routesTableModel;
    }

    public void setCurrentCategory(CategoryTreeNode category) {
        List<Route> routes = category.getRoutes();
        List<RouteModel> routeModels = new ArrayList<RouteModel>();
        if (routes != null) {
            Route[] routesArray = routes.toArray(new Route[routes.size()]);
            sort(routesArray, routeComparator);
            for (Route route : routesArray)
                routeModels.add(new RouteModel(category, route));
        }
        getRoutesTableModel().setRoutes(routeModels);
    }

    public void addCategories(final List<CategoryTreeNode> parents, final List<String> names, final Runnable invokeLaterRunnable) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "AddCategories";
            }

            public void run() throws IOException {
                final List<CategoryTreeNode> categories = new ArrayList<CategoryTreeNode>();
                for (int i = 0; i < parents.size(); i++) {
                    CategoryTreeNode parent = parents.get(i);
                    Category category = parent.getCategory().create(names.get(i));
                    categories.add(new CategoryTreeNodeImpl(category));
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < parents.size(); i++) {
                            CategoryTreeNode parent = parents.get(i);
                            CategoryTreeNode category = categories.get(i);
                            categoryTreeModel.insertNodeInto(category, parent, 0);
                        }
                        if (invokeLaterRunnable != null)
                            invokeLaterRunnable.run();
                    }
                });
            }
        });
    }

    public void renameCategory(final CategoryTreeNode category, final String name) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "RenameCategory";
            }

            public void run() throws IOException {
                if (category.isLocalRoot() || category.isRemoteRoot())
                    throw new IOException("cannot rename category root");

                category.getCategory().update(null, name);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        categoryTreeModel.nodeChanged(category);
                    }
                });
            }
        });
    }

    public void moveCategories(List<CategoryTreeNode> categories, CategoryTreeNode parent, Runnable invokeLaterRunnable) {
        moveCategories(categories, asParents(parent, categories.size()), invokeLaterRunnable);
    }

    public void moveCategories(final List<CategoryTreeNode> categories, final List<CategoryTreeNode> parents, final Runnable invokeLaterRunnable) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "MoveCategories";
            }

            public void run() throws IOException {
                for (int i = 0; i < categories.size(); i++) {
                    CategoryTreeNode category = categories.get(i);
                    CategoryTreeNode parent = parents.get(i);

                    if (category.isLocal() && parent.isRemote())
                        throw new IOException("cannot move local category " + category.getName() + " to remote parent " + parent.getName());
                    if (category.isRemote() && parent.isLocal())
                        throw new IOException("cannot move remote category " + category.getName() + " to local parent " + parent.getName());

                    category.getCategory().update(parent.getCategory(), category.getCategory().getName());
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < categories.size(); i++) {
                            CategoryTreeNode category = categories.get(i);
                            CategoryTreeNode parent = parents.get(i);
                            categoryTreeModel.removeNodeFromParent(category);
                            categoryTreeModel.insertNodeInto(category, parent, 0);
                        }
                        if (invokeLaterRunnable != null)
                            invokeLaterRunnable.run();
                    }
                });
            }
        });
    }

    public void removeCategories(List<CategoryTreeNode> categories, Runnable invokeLaterRunnable) {
        removeCategories(asParents(categories), asNames(categories), invokeLaterRunnable);
    }

    public void removeCategories(final List<CategoryTreeNode> parents, final List<String> names, final Runnable invokeLaterRunnable) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "RemoveCategories";
            }

            public void run() throws IOException {
                for (int i = 0; i < parents.size(); i++) {
                    CategoryTreeNode category = categoryTreeModel.getChild(parents.get(i), names.get(i));

                    if (category.isLocalRoot() || category.isRemoteRoot())
                        throw new IOException("cannot remove category root");

                    category.getCategory().delete();
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < parents.size(); i++) {
                            CategoryTreeNode category = categoryTreeModel.getChild(parents.get(i), names.get(i));
                            categoryTreeModel.removeNodeFromParent(category);
                        }
                        if (invokeLaterRunnable != null)
                            invokeLaterRunnable.run();
                    }
                });
            }
        });
    }

    public void addRoute(final CategoryTreeNode category, final String description, final File file, final String url, final AddRouteCallback callback) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "AddRoute";
            }

            public void run() throws IOException {
                Route route = file != null ? category.getCategory().createRoute(description, file) : category.getCategory().createRoute(description, url);
                final RouteModel routeModel = new RouteModel(category, route);
                callback.setRoute(routeModel);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        routesTableModel.addRoute(routeModel);
                    }
                });
            }
        });
    }

    public void renameRoute(final RouteModel route, final String name) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "RenameRoute";
            }

            public void run() throws IOException {
                route.getRoute().update(route.getCategory().getCategory().getUrl(), name);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        routesTableModel.updateRoute(route);
                    }
                });
            }
        });
    }

    public void moveRoutes(List<RouteModel> routes, CategoryTreeNode parent, Runnable invokeLaterRunnable) {
        moveRoutes(routes, asParents(parent, routes.size()), invokeLaterRunnable);
    }

    public void moveRoutes(final List<RouteModel> routes, final List<CategoryTreeNode> parents, final Runnable invokeLaterRunnable) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "MoveRoutes";
            }

            public void run() throws IOException {
                for (int i = 0; i < routes.size(); i++) {
                    RouteModel route = routes.get(i);
                    CategoryTreeNode parent = parents.get(i);
                    CategoryTreeNode category = route.getCategory();

                    if (category.isLocal() && parent.isRemote())
                        throw new IOException("cannot move local route " + route.getName() + " to remote parent " + parent.getName());
                    if (category.isRemote() && parent.isLocal())
                        throw new IOException("cannot move remote route " + route.getName() + " to local parent " + parent.getName());

                    route.getRoute().update(parent.getCategory().getUrl(), route.getDescription() != null ? route.getDescription() : route.getName());
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (CategoryTreeNode parent : parents) {
                            setCurrentCategory(parent);
                        }
                        if (invokeLaterRunnable != null)
                            invokeLaterRunnable.run();
                    }
                });
            }
        });
    }

    public void removeRoutes(final List<RouteModel> routes) {
        operator.executeOperation(new RouteServiceOperator.Operation() {
            public String getName() {
                return "RemoveRoutes";
            }

            public void run() throws IOException {
                for (RouteModel route : routes) {
                    route.getRoute().delete();
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (RouteModel route : routes) {
                            routesTableModel.removeRoute(route);
                        }
                    }
                });
            }
        });
    }
}
