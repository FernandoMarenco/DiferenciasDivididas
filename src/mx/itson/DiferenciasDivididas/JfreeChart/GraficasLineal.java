package mx.itson.DiferenciasDivididas.JfreeChart;

import java.awt.BorderLayout;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GraficasLineal {

    public void Graficar(double[][] tabulacion, int l1, double[][] grafica, int l2) {
        XYSeries serie1 = new XYSeries("Tipo A");
        XYSeries serie2 = new XYSeries("Tipo B");
        // Introduccion de datos
        
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < l1; i++) {
                serie1.add(tabulacion[i][0], tabulacion[i][1]);
            }
        }
        
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < l2; i++) {
                serie2.add(grafica[i][0], grafica[i][1]);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie1);
        dataset.addSeries(serie2);
        // Generamos la grafica     
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Graficar función",// Titulo           
                "X",// etiqueta coordenada X    
                "Y",// tiqueta coordenada Y   
                dataset,// los datos guardados     
                PlotOrientation.VERTICAL,// modo de grafica HORIZANTAL o VERTICAL    
                false,// motrar leyenda en la gráfica 
                true,// Use tooltips      
                true // Configurar chart   daniel ipanaque
                );

        //personalización del grafico 
        XYPlot xyplot = (XYPlot) chart.getPlot();
        xyplot.setBackgroundPaint(new Color(255, 255, 255));
        xyplot.setDomainGridlinePaint(new Color(36, 45, 54));
        xyplot.setRangeGridlinePaint(new Color(36, 45, 54));
        chart.setBackgroundPaint(new Color(255, 255, 255));
        chart.getTitle().setPaint(new Color(0, 0, 0));

        //Pinta los puntos guardados
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        configurarRendered(renderer); // cambiamos color a las lineas
        renderer.setBaseShapesVisible(true);
        // muestra los valores de cada punto XY
        XYItemLabelGenerator xy = new StandardXYItemLabelGenerator();
        renderer.setBaseItemLabelGenerator(xy);
        //renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseLinesVisible(true);
        
        final ChartPanel barPanel = new ChartPanel(chart);
        // barPanel.setMouseZoomable(false);  // esta linea es para bloquear acnes del mouse en la gráfica

        FormularioChart.PanelGraficar.removeAll();
        FormularioChart.PanelGraficar.add(barPanel, BorderLayout.CENTER);
        FormularioChart.PanelGraficar.validate();

    }

    private void configurarRendered(XYLineAndShapeRenderer renderer) {
        renderer.setSeriesPaint(0, new Color(238, 53, 17));
        renderer.setSeriesPaint(1, new Color(102, 154, 238));
        
    }
}
