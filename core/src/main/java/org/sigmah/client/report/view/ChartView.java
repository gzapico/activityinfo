/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.report.view;

import org.sigmah.shared.report.model.PivotChartReportElement;

/**
 * Interface to a view of a PivotChartElement
 *
 * @author Alex Bertram
 */
public interface ChartView extends ReportView<PivotChartReportElement> {
    void show(PivotChartReportElement element);
}