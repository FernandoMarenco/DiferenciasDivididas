/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.itson.DiferenciasDivididas.entidades;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import mx.itson.DiferenciasDivididas.JfreeChart.FormularioChart;
import mx.itson.DiferenciasDivididas.JfreeChart.GraficasLineal;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.Node;

/**
 *
 * @author Fernando
 */
public class DiferenciasDivididasGUI extends javax.swing.JFrame {

    /**
     * Creates new form DiferenciasDivididasGUI
     */
    public DiferenciasDivididasGUI() {
        initComponents();
        this.setLocationRelativeTo(null);
    }
    
    //valores de entrada
    double[] x = null;
    double[][] y = null;
    double[][] grafica = null, tabulacion = null;

    //metodo para llenar la tabla tabulacion con campos vacios
    public void llenarCamposTabulacion(int elementos) {
        DefaultTableModel tabla = new DefaultTableModel();
        
        //bloquear columna 'y' si existe una ecuacion
        if (checkb_ecuacion.isSelected()) {
            tabla = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    if (columna == 1) { //no editar la columna 'y'
                        return false;
                    } else {
                        return true;
                    }
                }
            };
        }
        
        //columnas
        tabla.addColumn("X");
        tabla.addColumn("Y");
        
        tb_tabulacion.setModel(tabla);
        
        //filas
        String[] datos = new String[elementos];
        for (int i = 0; i < elementos; i++) {
            tabla.addRow(datos);
        }
        
        llenarCamposProcedimiento(elementos);
    }
    
    //metodo para llenar la tabla procedimiento con campos vacios
    public void llenarCamposProcedimiento(int elementos) {
        DefaultTableModel tabla = new DefaultTableModel() {
            //bloquear la edicion de todas las celdas
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        
        //columnas
        for (int i = 1; i < elementos; i++) {
            tabla.addColumn("X"+i);
        }
        
        tb_procedimiento.setModel(tabla);
        
        //filas
        String[] datos = new String[elementos];
        for (int i = 0; i < elementos; i++) {
            tabla.addRow(datos);
        }
    }
    
    //metodo para llenar el arregloX a partir de los datos ingresados en la tabla
    public void llenarX(String f) {
        TableModel tabla = tb_tabulacion.getModel();
        x = new double[tabla.getRowCount()]; //crear arregloX
        
        for (int i = 0; i < x.length; i++) {
            x[i] = Double.parseDouble(tabla.getValueAt(i, 0).toString()); //obtener el valor de la columna 'x'
            //System.out.println(x[i]);
        }
        
        llenarY(f);
        actualizarTablaProcedimiento();
        txt_funcion.append(generarFuncion());
    }
    
    /* Algotimo para aplicar el Método de Newton */
    //metodo para llenar el arregloY
    public void llenarY(String f) {
        TableModel tabla = tb_tabulacion.getModel();
        y = new double[x.length][x.length]; //crear arregloY
        int contador = x.length-1; //contador para saber cuando guardar un 0
        int separacion = 0; //separador que guarda los limites entre las x que se deben tomar
        
        for (int j = 0; j < x.length; j++) { //recorrer columnas
            for (int i = 0; i < x.length; i++) { //recorrer filas
                if (j == 0) { //primer columna siempre guarda la 'y'
                    if (checkb_ecuacion.isSelected()) {
                        y[i][j] = funcion(f, x[i]); //si existe una funcion se guardara la 'y' evaluada en esa funcion
                    } else {
                        y[i][j] = Double.parseDouble(tabla.getValueAt(i, 1).toString()); //si no existe una funcion se guardara la 'y' introducida
                    }
                } else if (j > 0 && i > contador){ //evaluar si sobrepasa el contador para no seguir haciendo operaciones
                    y[i][j] = 0;
                } else { //realizar la operacion
                    y[i][j] = p(y[i+1][j-1], y[i][j-1], x[i+separacion], x[i]); //en la parte de arriba: se obtiene la letra i de la columna j anterior
                } //en la parte de abajo: se coloca el valor de la x de tal limite a tal limite dependiendo su separacion
                
            }
            contador--;
            separacion++;
        }
    }
    
    //metodo para introducir los datos a la tabla procedimiento
    public void actualizarTablaProcedimiento() {
        TableModel tabla1 = tb_tabulacion.getModel();
        TableModel tabla2 = tb_procedimiento.getModel();
        
        //recorrer arregloY para establecer la matriz en la tabla
        for (int j = 0; j < x.length; j++) {
            for (int i = 0; i < x.length; i++) {
                if (j == 0) {
                    if (checkb_ecuacion.isSelected()) {
                        tabla1.setValueAt(y[i][j], i, 1); //tabla tabulacion segunda columna
                    }
                } else {
                    tabla2.setValueAt(y[i][j], i, j-1); //se le resta 1 a la j porque ya se habia agregado la primer columna en otra tabla, esto es para que empieze bien con su indice
                }
            }
        }
    }
    
    //metodo que genera la ecuacion resultante
    public String generarFuncion() {
        String ecuacion = "";
        for (int i = 0; i < x.length; i++) { //recorrer arregloY para obtener su primer fila
            if (y[0][i] >= 0) { //condicion por si el primer numero es mayor a 0 escribirle un signo +
                if (i == 0) { //pero si es el primer valor no escribe un signo
                    ecuacion += redondearDecimales(y[0][i], 5);
                } else {
                    ecuacion += "+" + redondearDecimales(y[0][i], 5);
                }
            } else {
                ecuacion += redondearDecimales(y[0][i], 5);
            }
            for (int j = 0; j < i; j++) {
                ecuacion += "(x-" + redondearDecimales(x[j], 5) + ")"; //escribir las veces que necesita ser multiplicado el numero en curso
            }
        }
        
        ecuacion = simplificarEcuacion(ecuacion);
        
        return ecuacion;
    }
    
    //metodo que simplifica una ecuacion
    private String simplificarEcuacion(String ecuacion) {
        try {
            XJep jep = new XJep();
            jep.addStandardFunctions();
            jep.addStandardConstants();
            jep.addComplex();
            jep.setImplicitMul(true);
            jep.setAllowUndeclared(true);
            jep.setAllowAssignment(true);
            
            Node n = jep.parse(ecuacion);
            Node sim = jep.simplify(n);
            
            ecuacion = jep.toString(sim);

            //System.out.println(jep.toString(sim));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ecuacion;
    }
    
    //formula para calcular la funcion con exp4j
    public double funcion(String f, double x) {
        Expression e = new ExpressionBuilder(f) //leer funciones
                .variables("x") //se establece la variable x
                .build() //se construye
                .setVariable("x", x); //establecer lo que vale x
        
        double resultado = e.evaluate(); //se evalua la funcion
        return resultado;
    }
    
    //formula que calcula el siguiente valor de la columna
    private double p(double p1, double p0, double x1, double x0) {
        double resultado = (p1 - p0) / (x1 - x0);
        return resultado;
    }
    
    //formula para redondear los decimales
    public double redondearDecimales(double valor, int decimales) {
        double resultado = valor;
        BigDecimal big = new BigDecimal(resultado);
        big = big.setScale(decimales, RoundingMode.HALF_UP);
        resultado = Double.parseDouble(big.toString());
        
        return resultado;
    }
    
    //metodo para generar los puntos de la grafica
    public void generarPuntosGrafica(String funcion) {
        grafica = new double[201][2]; //guardar tabulacion de la grafica desde -10 hasta 10
        double numerador = -10;
        for (int j = 0; j < 2; j++) { //recorrer la matriz grafica
            for (int i = 0; i < 201; i++) {
                if (j == 0) {
                    grafica[i][j] = redondearDecimales(numerador, 2); //redondear el numerador
                    numerador+=0.1;
                }
                else {
                    grafica[i][j] = funcion(funcion, redondearDecimales(numerador, 4)); //redondear el numerador y aplicar la funcion f(x)
                    numerador+=0.1;
                }
                //System.out.println("grafica: "+grafica[i][j]);
            }
            numerador = -10;
        }
        
        //generar la tabulacion principal
        tabulacion = new double[x.length][2];
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < x.length; i++) {
                if (j == 0) {
                    tabulacion[i][j] = x[i];
                } else {
                    tabulacion[i][j] = y[i][0];
                }
                System.out.println(tabulacion[i][j]);
            }
        }
        
    }
    
    //metodo para abrir el frame de la grafica y graficar
    public void llamarFormularioChart(double[][] tabulacion, int l1, double[][] grafica, int l2) {
        FormularioChart ventana = new FormularioChart();
        ventana.setVisible(true);

        GraficasLineal g = new GraficasLineal();
        g.Graficar(tabulacion, l1, grafica, l2);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        checkb_ecuacion = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_tabulacion = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tb_procedimiento = new javax.swing.JTable();
        btn_aceptar = new javax.swing.JButton();
        btn_calcular = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_funcion = new javax.swing.JTextArea();
        btn_evaluar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_resultado = new javax.swing.JTextField();
        txt_valor = new javax.swing.JTextField();
        btn_borrar = new javax.swing.JButton();
        txt_ecuacion = new javax.swing.JTextField();
        txt_observaciones = new javax.swing.JTextField();
        btn_graficar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Interpolación numérica");

        jPanel1.setBackground(new java.awt.Color(177, 237, 237));
        jPanel1.setLayout(null);

        checkb_ecuacion.setBackground(new java.awt.Color(177, 237, 237));
        checkb_ecuacion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        checkb_ecuacion.setSelected(true);
        checkb_ecuacion.setText("Ecuación:");
        checkb_ecuacion.setFocusable(false);
        checkb_ecuacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        checkb_ecuacion.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        checkb_ecuacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkb_ecuacionActionPerformed(evt);
            }
        });
        jPanel1.add(checkb_ecuacion);
        checkb_ecuacion.setBounds(50, 70, 90, 20);

        jLabel1.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Interpolación numérica");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(0, 10, 620, 40);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Observaciones:");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(50, 100, 90, 20);

        tb_tabulacion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tb_tabulacion);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(40, 170, 130, 140);

        jLabel3.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Tabulación:");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(40, 140, 130, 20);

        jLabel4.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Procedimiento:");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(190, 140, 390, 20);

        tb_procedimiento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tb_procedimiento);

        jPanel1.add(jScrollPane3);
        jScrollPane3.setBounds(190, 170, 390, 140);

        btn_aceptar.setBackground(new java.awt.Color(0, 204, 204));
        btn_aceptar.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_aceptar.setText("Aceptar");
        btn_aceptar.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 153, 153)));
        btn_aceptar.setContentAreaFilled(false);
        btn_aceptar.setOpaque(true);
        btn_aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_aceptarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_aceptar);
        btn_aceptar.setBounds(380, 70, 90, 50);

        btn_calcular.setBackground(new java.awt.Color(0, 204, 204));
        btn_calcular.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_calcular.setText("<html><P ALIGN=center>Calcular<br>función</html>");
        btn_calcular.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 153, 153)));
        btn_calcular.setContentAreaFilled(false);
        btn_calcular.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_calcular.setOpaque(true);
        btn_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_calcularActionPerformed(evt);
            }
        });
        jPanel1.add(btn_calcular);
        btn_calcular.setBounds(30, 330, 120, 70);

        txt_funcion.setEditable(false);
        txt_funcion.setColumns(20);
        txt_funcion.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_funcion.setLineWrap(true);
        txt_funcion.setRows(3);
        jScrollPane2.setViewportView(txt_funcion);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(170, 350, 420, 50);

        btn_evaluar.setBackground(new java.awt.Color(0, 204, 204));
        btn_evaluar.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_evaluar.setText("<html><P ALIGN=center>Evaluar<br>función</html>");
        btn_evaluar.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 153, 153)));
        btn_evaluar.setContentAreaFilled(false);
        btn_evaluar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_evaluar.setOpaque(true);
        btn_evaluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_evaluarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_evaluar);
        btn_evaluar.setBounds(370, 420, 100, 50);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Función resultante:");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(170, 330, 420, 15);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Valor:");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(40, 420, 40, 20);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Resultado:");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(40, 450, 70, 20);

        txt_resultado.setEditable(false);
        txt_resultado.setBackground(new java.awt.Color(255, 255, 255));
        txt_resultado.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jPanel1.add(txt_resultado);
        txt_resultado.setBounds(110, 450, 240, 21);

        txt_valor.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_valor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_valorKeyTyped(evt);
            }
        });
        jPanel1.add(txt_valor);
        txt_valor.setBounds(80, 420, 270, 21);

        btn_borrar.setBackground(new java.awt.Color(0, 204, 204));
        btn_borrar.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_borrar.setText("Borrar");
        btn_borrar.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 153, 153)));
        btn_borrar.setContentAreaFilled(false);
        btn_borrar.setOpaque(true);
        btn_borrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_borrarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_borrar);
        btn_borrar.setBounds(480, 70, 90, 50);

        txt_ecuacion.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_ecuacion.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jPanel1.add(txt_ecuacion);
        txt_ecuacion.setBounds(140, 70, 220, 21);

        txt_observaciones.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_observaciones.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        txt_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_observacionesKeyTyped(evt);
            }
        });
        jPanel1.add(txt_observaciones);
        txt_observaciones.setBounds(140, 100, 220, 21);

        btn_graficar.setBackground(new java.awt.Color(0, 204, 204));
        btn_graficar.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_graficar.setText("Graficar");
        btn_graficar.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 3, new java.awt.Color(0, 153, 153)));
        btn_graficar.setContentAreaFilled(false);
        btn_graficar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_graficar.setOpaque(true);
        btn_graficar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_graficarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_graficar);
        btn_graficar.setBounds(480, 420, 100, 50);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkb_ecuacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkb_ecuacionActionPerformed
        if (checkb_ecuacion.isSelected()) {
            txt_ecuacion.setEnabled(true);
        } else {
            txt_ecuacion.setText("");
            txt_ecuacion.setEnabled(false);
        }
    }//GEN-LAST:event_checkb_ecuacionActionPerformed

    private void btn_aceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_aceptarActionPerformed
        if (checkb_ecuacion.isSelected()) {
            if (txt_ecuacion.getText().equals("") || txt_observaciones.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Favor de llenar los campos");
            } else {
                checkb_ecuacion.setEnabled(false);
                txt_ecuacion.setEditable(false);
                txt_observaciones.setEditable(false);
                btn_aceptar.setEnabled(false);
                llenarCamposTabulacion(Integer.parseInt(txt_observaciones.getText()));
            }
        } else {
            if (txt_observaciones.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Favor de llenar los campos");
            } else {
                checkb_ecuacion.setEnabled(false);
                txt_ecuacion.setEditable(false);
                txt_observaciones.setEditable(false);
                btn_aceptar.setEnabled(false);
                llenarCamposTabulacion(Integer.parseInt(txt_observaciones.getText()));
            }
        }
    }//GEN-LAST:event_btn_aceptarActionPerformed

    private void btn_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_calcularActionPerformed
        try {
            if (tb_tabulacion.getColumnCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay datos registrados");
            } else {
                llenarX(txt_ecuacion.getText());
                btn_calcular.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos invalidos");
        }
    }//GEN-LAST:event_btn_calcularActionPerformed

    private void btn_borrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_borrarActionPerformed
        checkb_ecuacion.setEnabled(true);
        txt_ecuacion.setEditable(true);
        txt_ecuacion.setText("");
        txt_observaciones.setEditable(true);
        txt_observaciones.setText("");
        x = null; y = null;
        btn_aceptar.setEnabled(true);
        btn_calcular.setEnabled(true);
        DefaultTableModel t = new DefaultTableModel();
        tb_tabulacion.setModel(t);
        tb_procedimiento.setModel(t);
        txt_funcion.setText("");
        txt_valor.setText("");
        txt_resultado.setText("");
    }//GEN-LAST:event_btn_borrarActionPerformed

    private void btn_evaluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_evaluarActionPerformed
        if (txt_funcion.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "No hay función para graficar");
        } else if (txt_valor.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Favor de llenar el formulario");
        } else {
            double resultado = funcion(txt_funcion.getText(), Double.parseDouble(txt_valor.getText()));
            txt_resultado.setText(String.valueOf(resultado));
        }
    }//GEN-LAST:event_btn_evaluarActionPerformed

    private void txt_observacionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_observacionesKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) || c == KeyEvent.VK_BACKSPACE || c == KeyEvent.VK_SPACE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_observacionesKeyTyped

    private void txt_valorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_valorKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) || c == KeyEvent.VK_BACKSPACE || c == KeyEvent.VK_SPACE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_valorKeyTyped

    private void btn_graficarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_graficarActionPerformed
        if (!txt_funcion.getText().equals("")) {
            //generar matriz graficar
            generarPuntosGrafica(txt_funcion.getText());
            llamarFormularioChart(tabulacion, tabulacion.length, grafica, grafica.length);
        }
        else {
            JOptionPane.showMessageDialog(this, "No hay función para graficar");
        }
    }//GEN-LAST:event_btn_graficarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set look and feel system*/
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DiferenciasDivididasGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_aceptar;
    private javax.swing.JButton btn_borrar;
    private javax.swing.JButton btn_calcular;
    private javax.swing.JButton btn_evaluar;
    private javax.swing.JButton btn_graficar;
    private javax.swing.JCheckBox checkb_ecuacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tb_procedimiento;
    private javax.swing.JTable tb_tabulacion;
    private javax.swing.JTextField txt_ecuacion;
    private javax.swing.JTextArea txt_funcion;
    private javax.swing.JTextField txt_observaciones;
    private javax.swing.JTextField txt_resultado;
    private javax.swing.JTextField txt_valor;
    // End of variables declaration//GEN-END:variables
}
