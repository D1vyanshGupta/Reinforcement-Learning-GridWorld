import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GridUIUtils {

    /**
     * Method to display the GridWord UI to show the optimal policy and utility values.
     * @param title
     * @param actionMap
     * @param gridWorld
     */
    public static void displayWorld(String title, HashMap<State, Action> actionMap, GridWorld gridWorld, int xCoord, int yCoord){
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(xCoord, yCoord, 500, 500);
        frame.setTitle("Grid World - " + title);

        int numRows = gridWorld.getNumRows();
        int numCols = gridWorld.getNumCols();

        State[][] stateGrid = gridWorld.getStateGrid();
        HashMap<State, Double> utilityMap = gridWorld.getUtilityMap();

        panel.setLayout(new GridLayout(numRows, numCols));

        for(int i = 0; i < numRows; ++i){
            for(int j = 0; j < numCols; ++j){
                JPanel cellPanel = new JPanel();
                cellPanel.setBorder(new LineBorder(Color.BLACK));
                cellPanel.setLayout(new BorderLayout());

                State cellState = stateGrid[i][j];

                if(cellState.isWall()){
                    cellPanel.setBackground(Color.GRAY);
                    JLabel wallLabel = new JLabel("Wall", JLabel.CENTER);
                    cellPanel.add(wallLabel, BorderLayout.CENTER);
                    panel.add(cellPanel);
                    continue;
                }


                Double stateReward = cellState.getReward();
                Double stateUtility = utilityMap.get(cellState);

                if(stateReward == 1){
                    cellPanel.setBackground(Color.GREEN);
                }

                if(stateReward == -1){
                    cellPanel.setBackground(new Color(255, 165, 0));
                }

                JLabel rewardLabel = new JLabel(Double.toString(stateReward));
                rewardLabel.setHorizontalAlignment(JLabel.RIGHT);
                cellPanel.add(rewardLabel,BorderLayout.NORTH);

                if(actionMap.size() != 0){
                    JLabel imageLabel = new JLabel();
                    ImageIcon actionIcon = getActionIcon(cellState, actionMap);
                    imageLabel.setIcon(actionIcon);
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                    cellPanel.add(imageLabel, BorderLayout.CENTER);
                }

                JLabel utilityLabel = new JLabel(Double.toString(stateUtility));
                cellPanel.add(utilityLabel, BorderLayout.SOUTH);

                panel.add(cellPanel);
            }
        }

        frame.setVisible(true);
    }

    /**
     * Method to display the graph plots.
     * @param title
     * @param dataMap
     */
    public static void displayLineChart(String title, LinkedHashMap<State, XYSeries> dataMap){
        ApplicationFrame applicationFrame = new ApplicationFrame(title);
        JFreeChart xyLineChart = ChartFactory.createXYLineChart(
                "Iterations v/s Estimated Utility",
                "No. of Iterations",
                "Estimated Utility",
                createDataSet(dataMap),
                PlotOrientation.VERTICAL,
                true, true, false);

        final XYPlot plot = xyLineChart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        Stroke dashedStroke = new BasicStroke(
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {10.0f, 6.0f}, 0.0f );
        renderer.setBaseStroke(dashedStroke);
        renderer.setAutoPopulateSeriesStroke(false);
        plot.setRenderer( renderer );

        plot.setBackgroundPaint(new Color(255,228,196));

        ChartPanel chartPanel = new ChartPanel(xyLineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 600));

        applicationFrame.setContentPane( chartPanel );
        applicationFrame.pack();
        RefineryUtilities.centerFrameOnScreen(applicationFrame);
        applicationFrame.setVisible(true);
    }

    /**
     * Helper method for plotting graphs.
     * @param dataMap
     * @return
     */
    private static XYDataset createDataSet(LinkedHashMap<State, XYSeries> dataMap){
        XYSeriesCollection dataSet = new XYSeriesCollection();
        for(XYSeries seriesIterator : dataMap.values()){
            dataSet.addSeries(seriesIterator);
        }
        return dataSet;
    }

    /**
     * Helper method for displaying the GridWorld UI.
     * @param state
     * @param actionMap
     * @return
     */
    private static ImageIcon getActionIcon(State state, HashMap<State, Action> actionMap) {
        Action stateAction = actionMap.get(state);

        switch (stateAction.getIntendedAction()){
            case "UP": {
                return new ImageIcon(new ImageIcon("images/up.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            }
            case "RIGHT": {
                return new ImageIcon(new ImageIcon("images/right.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            }
            case "DOWN": {
                return new ImageIcon(new ImageIcon("images/down.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            }
            default: {
                return new ImageIcon(new ImageIcon("images/left.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            }
        }
    }
}
