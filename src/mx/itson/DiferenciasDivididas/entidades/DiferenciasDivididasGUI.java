/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.itson.DiferenciasDivididas.entidades;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

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
        
        //Columnas
        tabla.addColumn("X");
        tabla.addColumn("Y");
        
        tb_tabulacion.setModel(tabla);
        
        //Filas
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
        
        String[] datos = new String[elementos];
        for (int i = 1; i <= elementos; i++) {
            tabla.addRow(datos);
        }
    }
    
    //metodo para llenar el arregloX a partir de los datos ingresados en la tabla
    public void llenarX(String f) {
        TableModel tabla = tb_tabulacion.getModel();
        x = new double[tabla.getRowCount()];
        
        for (int i = 0; i < x.length; i++) {
            x[i] = Double.parseDouble(tabla.getValueAt(i, 0).toString());
            //System.out.println(x[i]);
        }
        
        llenarY(f);
        actualizarTablaProcedimiento();
    }
    
    //metodo para llenar el arregloY
    public void llenarY(String f) {
        TableModel tabla = tb_tabulacion.getModel();
        y = new double[x.length][x.length];
        int contador = x.length-1; //contador para saber cuando guardar un 0
        int separacion = 0; //separador que guarda los limites entre las x
        
        for (int j = 0; j < x.length; j++) {
            for (int i = 0; i < x.length; i++) {
                if (j == 0) { //primer columna siempre guarda la y
                    if (checkb_ecuacion.isSelected()) {
                        y[i][j] = funcion(f, x[i]); //si existe una funcion se guardara la 'y' evaluada en la funcion
                    } else {
                        y[i][j] = Double.parseDouble(tabla.getValueAt(i, 1).toString()); //si no existe una funcion se guardara la 'y' introducida
                    }
                } else if (j > 0 && i > contador){ //evaluar si sobrepasa el contador para no seguir haciendo operaciones
                    y[i][j] = 0;
                } else {
                    y[i][j] = p(y[i+1][j-1], y[i][j-1], x[i+separacion], x[i]);
                }
                
            }
            contador--;
            separacion++;
        }
    }
    
    //metodo para completar la tabla procedimiento
    public void actualizarTablaProcedimiento() {
        TableModel tabla1 = tb_tabulacion.getModel();
        TableModel tabla2 = tb_procedimiento.getModel();
        
        for (int j = 0; j < x.length; j++) {
            for (int i = 0; i < x.length; i++) {
                if (j == 0) {
                    if (checkb_ecuacion.isSelected()) {
                        tabla1.setValueAt(y[i][j], i, 1);
                    }
                } else {
                    tabla2.setValueAt(y[i][j], i, j-1);
                }
            }
        }

    }
    
    //formula para calcular la funcion con exp4j
    private double funcion(String f, double x){
        Expression e = new ExpressionBuilder(f) //leer funciones
                .variables("x") //se establece la variable x
                .build() //se construye
                .setVariable("x", x); //establecer lo que vale x
        
        double resultado = e.evaluate(); //se evalua la funcion
        return resultado;
    }
    
    //formula que calcula el siguiente valor de la columna
    private double p(double p1, double p0, double x1, double x0){
        double resultado = (p1 - p0) / (x1 - x0);
        return resultado;
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
        txt_ecuacion = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_tabulacion = new javax.swing.JTable();
        txt_observaciones = new javax.swing.JTextField();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(null);

        checkb_ecuacion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        checkb_ecuacion.setSelected(true);
        checkb_ecuacion.setText("Ecuación:");
        checkb_ecuacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        checkb_ecuacion.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        checkb_ecuacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkb_ecuacionActionPerformed(evt);
            }
        });
        jPanel1.add(checkb_ecuacion);
        checkb_ecuacion.setBounds(100, 70, 90, 20);

        txt_ecuacion.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jPanel1.add(txt_ecuacion);
        txt_ecuacion.setBounds(190, 70, 220, 21);

        jLabel1.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Interpolación numérica");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(0, 10, 620, 40);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Observaciones:");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(100, 100, 90, 20);

        tb_tabulacion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tb_tabulacion);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(40, 170, 130, 140);

        txt_observaciones.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jPanel1.add(txt_observaciones);
        txt_observaciones.setBounds(190, 100, 220, 21);

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

        btn_aceptar.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_aceptar.setText("Aceptar");
        btn_aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_aceptarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_aceptar);
        btn_aceptar.setBounds(430, 70, 90, 50);

        btn_calcular.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_calcular.setText("<html><P ALIGN=center>Calcular<br>función</html>");
        btn_calcular.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_calcularActionPerformed(evt);
            }
        });
        jPanel1.add(btn_calcular);
        btn_calcular.setBounds(30, 340, 120, 50);

        txt_funcion.setColumns(20);
        txt_funcion.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_funcion.setLineWrap(true);
        txt_funcion.setRows(3);
        jScrollPane2.setViewportView(txt_funcion);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(170, 350, 420, 50);

        btn_evaluar.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 12)); // NOI18N
        btn_evaluar.setText("<html><P ALIGN=center>Evaluar<br>función</html>");
        btn_evaluar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(btn_evaluar);
        btn_evaluar.setBounds(430, 420, 100, 50);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Función resultante:");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(170, 330, 420, 15);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Valor:");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(90, 420, 40, 20);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Resultado:");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(90, 450, 70, 20);

        txt_resultado.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jPanel1.add(txt_resultado);
        txt_resultado.setBounds(160, 450, 250, 21);

        txt_valor.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jPanel1.add(txt_valor);
        txt_valor.setBounds(130, 420, 280, 21);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
                llenarCamposTabulacion(Integer.parseInt(txt_observaciones.getText()));
            }
        } else {
            if (txt_observaciones.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Favor de llenar los campos");
            } else {
                llenarCamposTabulacion(Integer.parseInt(txt_observaciones.getText()));
            }
        }
    }//GEN-LAST:event_btn_aceptarActionPerformed

    private void btn_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_calcularActionPerformed
        llenarX(txt_ecuacion.getText());
    }//GEN-LAST:event_btn_calcularActionPerformed

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
    private javax.swing.JButton btn_calcular;
    private javax.swing.JButton btn_evaluar;
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
