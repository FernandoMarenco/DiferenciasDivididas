/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.itson.DiferenciasDivididas.entidades;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.lsmp.djep.djep.DJep;
import org.lsmp.djep.xjep.XJep;
import org.nfunk.jep.Node;


/**
 *
 * @author Fernando
 */
public class Main {
    
    public static void main(String[] args) {
        double[] x = {0,1,2,3};
        //double[] y = new double[x.length];
//        System.out.println(x.length);
//        for (int i = 0; i < x.length; i++) {
//            y[i] = funcion(x[i]);
//            System.out.println(y[i]);
//        }
        
        double[][] y = new double[x.length][x.length];
        int contador = x.length-1; //contador para saber cuando guardar un 0
        int separacion = 0;
        for (int j = 0; j < x.length; j++) {
            for (int i = 0; i < x.length; i++) {
                if (j == 0) { //primer columna siempre guarda y 'f(x)'
                    y[i][j] = funcion(x[i]);
                } else if (j > 0 && i > contador){ //evaluar si sobrepasa el contador para no seguir haciendo operaciones
                    y[i][j] = 0;
                } else {
                    y[i][j] = p(y[i+1][j-1], y[i][j-1], x[i+separacion], x[i]);
                    //y[i][j] = 1;
                }
                
            }
            contador--;
            separacion++;
        }
        
        
        for (int j = 0; j < x.length; j++) {
            for (int i = 0; i < x.length; i++) {
                System.out.println(y[i][j]);
            }
        }
        
        //generar ecuacion
        String ecuacion = "";
        for (int i = 0; i < x.length; i++) {
            if (y[0][i] >= 0) {
                if (i == 0) {
                    ecuacion += y[0][i];
                } else {
                    ecuacion += "+" + y[0][i];
                }
            } else {
                ecuacion += y[0][i];
            }
            for (int j = 0; j < i; j++) {
                ecuacion += "(x+" + x[j] + ")";
            }
        }
        System.out.println(ecuacion);
        
//        Expression e = new ExpressionBuilder(ecuacion)
//                //.variables("x")
//                .build();
//        System.out.println(e.evaluate());
        
        XJep jep = new XJep();
        jep.addStandardFunctions();
        jep.addStandardConstants();
        jep.addComplex();
        jep.setImplicitMul(true);
        jep.setAllowUndeclared(true);
        jep.setAllowAssignment(true);
        
        String ss = "x*x+2+0";
        try {
           
            Node n = jep.parse(ecuacion);
            Node p = jep.preprocess(n);
            Node sim = jep.simplify(p);

            System.out.println(jep.toString(sim));

            
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static double funcion(double x){
        double resultado = Math.pow(Math.E, x);
        return resultado;
    }
    
    public static double p(double p1, double p0, double x1, double x0){
        double resultado = (p1 - p0) / (x1 - x0);
        return resultado;
    }
    
}
