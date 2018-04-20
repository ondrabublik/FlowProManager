/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowpromanager;

import flowpro.api.Mat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author obublik
 */
public class SimulationSetup {

    public static final String ARG_FILE_NAME = "args.txt";
    public static final String PARAMETER_FILE_NAME = "parameters.txt";
    public static final String STATE_FILE_NAME = "state.txt";
    public static final String REF_VALUE_FILE_NAME = "referenceValues.txt";

    public String problemPath;
    public String meshPath;
    public String simulationPath;
    public String problemName;
    public String simulationName;
    public int nVertices;
    public int nElements;
    public int[] nElemTypes = new int[5];
    public String nElementsPrint;

    SimulationSetup() {
        refreshSimulationPath();
    }

    void refreshSimulationPath() {
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(ARG_FILE_NAME));

            String line;
            String[] args;
            if ((line = reader.readLine()) != null) {
                args = line.split(" ");
                if (args.length != 2) {
                    throw new IOException("file " + ARG_FILE_NAME
                            + " must contain one line with exactly two arguments");
                }
                problemName = args[0];
                simulationName = args[1];
            } else {
                throw new IOException("file " + ARG_FILE_NAME + " is empty");
            }

            problemPath = "simulations/" + problemName;
            meshPath = "simulations/" + problemName + "/mesh/";
            simulationPath = "simulations/" + problemName + "/" + simulationName + "/";

        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            double[][] PXY = Mat.loadDoubleMatrix(meshPath + "vertices.txt"); // mesh vertices coordinates
            nVertices = PXY.length;
            int[] elemsType = Mat.loadIntArray(meshPath + "elementType.txt");
            Arrays.fill(nElemTypes, 0);
            nElements = 0;
            for (int i = 0; i < elemsType.length; i++) {
                nElements++;
                switch (elemsType[i]) {
                    case 3:
                        nElemTypes[0]++;
                        break;
                    case 4:
                        nElemTypes[1]++;
                        break;
                    case 5:
                        nElemTypes[2]++;
                        break;
                    case 6:
                        nElemTypes[3]++;
                        break;
                    case 7:
                        nElemTypes[4]++;
                        break;
                }
            }
            boolean first = true;
            nElementsPrint = nElements + " (";
            if (nElemTypes[0] > 0) {
                if (first) {
                    nElementsPrint += "triangles " + nElemTypes[0];
                    first = false;
                } else {
                    nElementsPrint += ", triangles " + nElemTypes[0];
                }
            }
            if (nElemTypes[1] > 0) {
                if (first) {
                    nElementsPrint += "squares " + nElemTypes[1];
                    first = false;
                } else {
                    nElementsPrint += ", squares " + nElemTypes[1];
                }
            }
            if (nElemTypes[2] > 0) {
                if (first) {
                    nElementsPrint += "tetrahedrals " + nElemTypes[2];
                    first = false;
                } else {
                    nElementsPrint += ", tetrahedrals " + nElemTypes[2];
                }
            }
            if (nElemTypes[3] > 0) {
                if (first) {
                    nElementsPrint += "hexahedrals " + nElemTypes[3];
                    first = false;
                } else {
                    nElementsPrint += ", hexahedrals " + nElemTypes[3];
                }
            }
            if (nElemTypes[4] > 0) {
                if (first) {
                    nElementsPrint += "prismatics " + nElemTypes[4];
                    first = false;
                } else {
                    nElementsPrint += ", prismatics " + nElemTypes[4];
                }
            }
            nElementsPrint += ")";
        } catch (Exception e) {

        }
    }

    String getMeshName() {
        return problemName;
    }

    String getSimulationName() {
        return simulationName;
    }
}
