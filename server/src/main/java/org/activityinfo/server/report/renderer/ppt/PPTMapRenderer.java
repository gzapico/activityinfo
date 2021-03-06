package org.activityinfo.server.report.renderer.ppt;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.activityinfo.server.geo.AdminGeometryProvider;
import org.activityinfo.server.report.generator.MapIconPath;
import org.activityinfo.server.report.generator.map.IconRectCalculator;
import org.activityinfo.server.report.renderer.image.ImageMapRenderer;
import org.activityinfo.server.util.ColorUtil;
import org.activityinfo.shared.report.content.BubbleMapMarker;
import org.activityinfo.shared.report.content.IconMapMarker;
import org.activityinfo.shared.report.content.MapMarker;
import org.activityinfo.shared.report.model.MapReportElement;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.ShapeTypes;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

import com.google.code.appengine.awt.Dimension;
import com.google.code.appengine.awt.Rectangle;
import com.google.inject.Inject;

/**
 * @author Alex Bertram
 */
public class PPTMapRenderer extends ImageMapRenderer {

    @Inject
    public PPTMapRenderer(AdminGeometryProvider geometryProvider,
        @MapIconPath String mapIconPath) {
        super(geometryProvider, mapIconPath);
    }

    @Override
    public void render(MapReportElement element, OutputStream stream)
        throws IOException {
        // create a new empty slide show
        SlideShow ppt = new SlideShow();
        Dimension pageSize = computePageSize(element);
        ppt.setPageSize(pageSize);

        render(element, ppt);

        // write to stream
        ppt.write(stream);

    }

    public void render(MapReportElement element, SlideShow ppt)
        throws IOException {

        // add first slide
        Slide slide = ppt.createSlide();

        // calculate map offset
        Dimension pageSize = ppt.getPageSize();
        int offsetX = ((int) pageSize.getWidth() - element.getWidth()) / 2;
        int offsetY = ((int) pageSize.getHeight() - element.getHeight()) / 2;

        // add the map background image

        drawBasemap(element, new PPTTileHandler(ppt, slide));

        // keep a list of map icons
        Map<String, Integer> iconPictureIndex = new HashMap<String, Integer>();

        // Add the indicator markers to the slide as shapes
        for (MapMarker marker : element.getContent().getMarkers()) {

            if (inView(element, marker)) {

                if (marker instanceof IconMapMarker) {
                    addIconMarker(ppt, slide, offsetX, offsetY,
                        iconPictureIndex, (IconMapMarker) marker);
                } else if (marker instanceof BubbleMapMarker) {
                    addBubble(slide, offsetX, offsetY, (BubbleMapMarker) marker);
                }
            }
        }
    }

    private boolean inView(MapReportElement element, MapMarker marker) {
        return (marker.getX() + marker.getSize()) > 0 &&
            (marker.getY() + marker.getSize()) > 0 &&
            (marker.getX() - marker.getSize()) < element.getWidth() &&
            (marker.getY() - marker.getSize()) < element.getHeight();
    }

    private void addBubble(Slide slide, int offsetX, int offsetY,
        BubbleMapMarker marker) {
        AutoShape shape = new AutoShape(ShapeTypes.Ellipse);
        shape.setAnchor(new Rectangle(
            offsetX + marker.getX() - marker.getRadius(),
            offsetY + marker.getY() - marker.getRadius(),
            marker.getRadius() * 2,
            marker.getRadius() * 2));

        shape.setFillColor(ColorUtil.colorFromString(marker.getColor()));
        shape.setEscherProperty(EscherProperties.FILL__FILLOPACITY, 49087);
        shape.setLineColor(bubbleStrokeColor(ColorUtil.toInteger(marker
            .getColor())));
        slide.addShape(shape);
    }

    private void addIconMarker(SlideShow ppt, Slide slide, int offsetX,
        int offsetY,
        Map<String, Integer> iconPictureIndex, IconMapMarker marker) {
        Integer iconIndex = iconPictureIndex.get(marker.getIcon().getName());
        if (iconIndex == null) {
            try {
                iconIndex = ppt.addPicture(
                    new File(getMapIconRoot() + "/"
                        + marker.getIcon().getName() + ".png"),
                    Picture.PNG);
            } catch (IOException e) {
                iconIndex = -1;
            }
            iconPictureIndex.put(marker.getIcon().getName(), iconIndex);
        }
        if (iconIndex != -1) {
            IconRectCalculator rectCtor = new IconRectCalculator(
                marker.getIcon());
            Picture icon = new Picture(iconIndex);
            org.activityinfo.shared.geom.Rectangle iconRect = rectCtor
                .iconRect(
                    offsetX + marker.getX(),
                    offsetY + marker.getY());
            icon.setAnchor(new Rectangle(iconRect.getX(), iconRect.getY(),
                iconRect.getWidth(), iconRect.getHeight()));
            slide.addShape(icon);
        }
    }

    private Dimension computePageSize(MapReportElement element) {
        // standard sizes
        Dimension[] stdSizes = new Dimension[] {
            new Dimension(720, 540), // Onscreen Show (4:5)
            new Dimension(720, 405), // Onscreen Show (16:9)
            new Dimension(780, 540), // A4 Portrait
            new Dimension(540, 780)
        };

        for (int i = 0; i != stdSizes.length; ++i) {
            if (stdSizes[i].getWidth() > element.getWidth() &&
                stdSizes[i].getHeight() > element.getHeight()) {

                return stdSizes[i];

            }
        }

        return new Dimension(element.getWidth(), element.getHeight());
    }
}
