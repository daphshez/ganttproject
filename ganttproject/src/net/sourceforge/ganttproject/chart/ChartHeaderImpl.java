/*
 * This code is provided under the terms of GPL version 3.
 * Please see LICENSE file for details
 * (C) Dmitry Barashev, GanttProject team, 2004-2008
 */
package net.sourceforge.ganttproject.chart;

import java.util.Date;
import java.util.List;

import biz.ganttproject.core.chart.canvas.Canvas;
import biz.ganttproject.core.chart.canvas.TextMetrics;
import biz.ganttproject.core.chart.canvas.TextSelector;
import biz.ganttproject.core.chart.canvas.Canvas.TextGroup;
import biz.ganttproject.core.chart.grid.Offset;

import net.sourceforge.ganttproject.chart.TimeUnitText;
import net.sourceforge.ganttproject.chart.timeline.TimeFormatters;
import net.sourceforge.ganttproject.chart.timeline.TimeFormatters.Position;

/**
 * Renders chart timeline.
 */
class ChartHeaderImpl extends ChartRendererBase implements ChartHeader {

  private Canvas myPrimitiveContainer;
  private Canvas myTimelineContainer;
  private Canvas myBackgroundContainer;

  ChartHeaderImpl(ChartModelBase model) {
    super(model);
    myPrimitiveContainer = getPrimitiveContainer();
    myBackgroundContainer = myPrimitiveContainer.newLayer();
    myPrimitiveContainer.newLayer();
    myPrimitiveContainer.newLayer();
    myTimelineContainer = myPrimitiveContainer.newLayer();
  }

  public void beforeProcessingTimeFrames() {
    myPrimitiveContainer.clear();
    createGreyRectangleWithNiceBorders();
  }

  public Canvas getTimelineContainer() {
    return myTimelineContainer;
  }

  /**
   * Draws the timeline box
   */
  private void createGreyRectangleWithNiceBorders() {
    int sizex = getWidth();
    final int spanningHeaderHeight = getChartModel().getChartUIConfiguration().getSpanningHeaderHeight();
    final int headerHeight = getChartModel().getChartUIConfiguration().getHeaderHeight();
    Canvas container = myTimelineContainer;
    Canvas.Rectangle headerRectangle = container.createRectangle(0, 0, sizex, headerHeight);
    headerRectangle.setBackgroundColor(getChartModel().getChartUIConfiguration().getSpanningHeaderBackgroundColor());

    Canvas.Rectangle timeunitHeaderBorder = container.createRectangle(0, spanningHeaderHeight,
        sizex - 1, spanningHeaderHeight);
    timeunitHeaderBorder.setForegroundColor(getChartModel().getChartUIConfiguration().getHeaderBorderColor());
    //
    // GraphicPrimitiveContainer.Line middleGutter1 = getTimelineContainer()
    // .createLine(1, spanningHeaderHeight - 1, sizex - 2, spanningHeaderHeight
    // - 1);
    // middleGutter1.setForegroundColor(getChartModel()
    // .getChartUIConfiguration().getHorizontalGutterColor1());
    //
    Canvas.Line bottomGutter = getTimelineContainer().createLine(0, headerHeight - 1, sizex - 2,
        headerHeight - 1);
    bottomGutter.setForegroundColor(getChartModel().getChartUIConfiguration().getHorizontalGutterColor1());
  }

  public Canvas paint() {
    return myPrimitiveContainer;
  }

  @Override
  public void render() {
    beforeProcessingTimeFrames();
    renderTopUnits();
    renderBottomUnits();
  }

  /**
   * Draws cells of the top line in the time line
   */
  private void renderTopUnits() {
    Date curDate = getChartModel().getStartDate();
    List<Offset> topOffsets = getChartModel().getTopUnitOffsets();
    int curX = topOffsets.get(0).getOffsetPixels();
    if (curX > 0) {
      curX = 0;
    }
    final int topUnitHeight = getChartModel().getChartUIConfiguration().getSpanningHeaderHeight();
    TextGroup textGroup = myTimelineContainer.createTextGroup(0, 0, topUnitHeight, "timeline.top");
    for (Offset nextOffset : topOffsets) {
      if (curX >= 0) {
        TimeUnitText[] texts = TimeFormatters.getFormatter(nextOffset.getOffsetUnit(), Position.UPPER_LINE).format(
            nextOffset.getOffsetUnit(), curDate);
        final int maxWidth = nextOffset.getOffsetPixels() - curX - 5;
        final TimeUnitText timeUnitText = texts[0];
        textGroup.addText(curX + 5, 0, new TextSelector() {
          @Override
          public Canvas.Label[] getLabels(TextMetrics textLengthCalculator) {
            return timeUnitText.getLabels(maxWidth, textLengthCalculator);
          }
        });
        getTimelineContainer().createLine(curX, topUnitHeight - 10, curX, topUnitHeight);
      }
      curX = nextOffset.getOffsetPixels();
      curDate = nextOffset.getOffsetEnd();
    }
  }

  /**
   * Draws cells of the bottom line in the time line
   */
  private void renderBottomUnits() {
    BottomUnitLineRendererImpl bottomUnitLineRenderer = new BottomUnitLineRendererImpl(getChartModel(),
        myTimelineContainer, getPrimitiveContainer());
    bottomUnitLineRenderer.setHeight(getHeight());
    bottomUnitLineRenderer.render();
  }
}