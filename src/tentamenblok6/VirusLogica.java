/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tentamenblok6;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
 * @author Rogier
 */
public class VirusLogica {

    //public static ArrayList<String> fileLines = new ArrayList<String>();
    public static HashMap<Integer, Virus> virusHM = new HashMap<>();
    public static ArrayList<String> uniqueHosts = new ArrayList<>();
    public static ArrayList<String> uniqueHostIDs = new ArrayList<>();
    private static DefaultListModel<String> dlm = new DefaultListModel<>();
    private static DefaultListModel<String> replicatesListModel = new DefaultListModel<>();
    public static ArrayList<Virus> dlmListCheck1 = new ArrayList<>();
    public static ArrayList<Virus> dlmListCheck2 = new ArrayList<>();
    private static DefaultListModel<String> dlmChecked1 = new DefaultListModel<>();
    private static DefaultListModel<String> dlmChecked2 = new DefaultListModel<>();
    public static ArrayList<Integer> dlmList = new ArrayList<>();
    public static ArrayList<Object> extractedViruses = new ArrayList<>();
    public static ArrayList<Object> extractedVirusesDump = new ArrayList<>();
    private static ListModel extractedListModel;
    public static int sortCase = 0;

    public static void openFile(String filename) {
        try {
            //Had some issues with file location, so getting the project's root and navigating to the package file
            filename = System.getProperty("user.dir") + "\\src\\tentamenblok6\\" + filename;
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String nextLine = br.readLine();
            String[] line = {};
            String previousID = "";
            nextLine = br.readLine(); // To skip the header
            while (nextLine != null) {
                try {
                    line = nextLine.split("\t");
                    if (!previousID.equals(line[0])) {
                        Virus virusObject = new Virus(line[0], line[1], line[2], line[7], line[8]);
                        virusHM.put(Integer.valueOf(line[0]), virusObject);
                        previousID = line[0];
                        if (!uniqueHosts.contains(line[7] + "  (" + line[8] + ")")) {
                            uniqueHosts.add(line[7] + "  (" + line[8] + ")");
                            uniqueHostIDs.add(line[7]);
                            System.out.println("asdsadsa" + line[7] + "\n" + Integer.parseInt(line[7]));
                        }
                    } else {
                        virusHM.get(Integer.valueOf(line[0])).addHost(line[7], line[8]);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("The virus with ID " + line[0] + " is incomplete; information missing.");
                } catch (NullPointerException e) {
                    System.out.println("NullPointerException occurred");
                }
                nextLine = br.readLine();
            }
            Collections.sort(uniqueHosts);
            Collections.sort(uniqueHostIDs);
            uniqueHosts.remove(0);
            uniqueHostIDs.remove(0);
            FillHostIDCombo();
            FillVirusLists();
            VirusGUI.fileLoaded = true;
        } catch (FileNotFoundException e) {
            System.out.println("The file was not found");
            VirusGUI.buttonFind.setEnabled(false);
            VirusGUI.labelFindButtonUnderscript.setVisible(true);
        } catch (IOException e) {
            System.out.println("IOException occurred");
            VirusGUI.buttonFind.setEnabled(false);
            VirusGUI.labelFindButtonUnderscript.setVisible(true);
        } catch (NumberFormatException e) {
            System.out.println("A NumberFormatException has been found " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("A NumberFormatException has occurred, please check the data of your file.\nNote that the file must contain at least 2 lines.");
        }

    }

    /**
     * Adds all host ID's to the combo boxes in the GUI
     */
    public static void FillHostIDCombo() {
        for (String uniqueHost : uniqueHosts) {
            VirusGUI.comboHostID1.addItem(uniqueHost);
            VirusGUI.comboHostID2.addItem(uniqueHost);
        }
    }

    /**
     * Adds all the virus id's to the lists in the GUI
     */
    public static void FillVirusLists() {
        for (Map.Entry<Integer, Virus> entry : virusHM.entrySet()) {
            dlmList.add(entry.getKey());
            dlm.addElement(String.valueOf(entry.getKey()) + "  (" + entry.getValue().getSoort() + ")");
        }
        //ArrayList<Integer> sortedVirusIDList = SortVirusIDList(dlmList);
        VirusGUI.listVirusID1.setModel(dlm);
        VirusGUI.listVirusID2.setModel(dlm);
    }
    /*
     public static ArrayList<Integer> SortVirusIDList(ArrayList<Integer> VirusList){
     for (int virusID : dlmList) {
     Virus currentVirus = virusHM.get(virusID);
     }
     return;
     }
     */

    public static void SortVirusLists(String viralClassificationState, String hostIDState_1, String hostIDState_2) {
        dlmChecked1.clear();
        dlmChecked2.clear();
        for (int virusID : dlmList) {
            Virus currentVirus = virusHM.get(virusID);
            if (!viralClassificationState.equals("none")) {
                if (currentVirus.SortVirusListCheckClassification(viralClassificationState)) {
                    SortVirusListsLogic(currentVirus, hostIDState_1, hostIDState_2);
                }
            } else {
                SortVirusListsLogic(currentVirus, hostIDState_1, hostIDState_2);
            }
        }
        //Harcode because getting the selected radiobutton from a group, which OBVIOUSLY is the main purpose of a radiobuttongroup, IS HARD AS HELL.
        if (VirusGUI.jRadioButton1.isSelected()) {
            sortCase = 0;
        } else if (VirusGUI.jRadioButton2.isSelected()) {
            sortCase = 1;
        } else {
            sortCase = 2;
        }
        Collections.sort(dlmListCheck1);
        Collections.sort(dlmListCheck2);
        for (Virus currentVirus : dlmListCheck1) {
            dlmChecked1.addElement(String.valueOf(currentVirus.getId()) + "  (" + currentVirus.getSoort() + ")");
        }
        for (Virus currentVirus : dlmListCheck2) {
            dlmChecked2.addElement(String.valueOf(currentVirus.getId()) + "  (" + currentVirus.getSoort() + ")");
        }
        
        VirusGUI.listVirusID1.setModel(dlmChecked1);
        VirusGUI.listVirusID2.setModel(dlmChecked2);
    }

    private static void SortVirusListsLogic(Virus currentVirus, String hostIDState_1, String hostIDState_2) {
        if (!hostIDState_1.equals("none")) {
            if (currentVirus.getHostList().contains(Integer.parseInt(uniqueHostIDs.get(uniqueHosts.indexOf(hostIDState_1))))) {
                dlmListCheck1.add(currentVirus);
                //dlmChecked1.addElement(String.valueOf(currentVirus.getId()) + "  (" + currentVirus.getSoort() + ")");
            }
        } else {
            dlmListCheck1.add(currentVirus);
            //dlmChecked1.addElement(String.valueOf(currentVirus.getId()) + "  (" + currentVirus.getSoort() + ")");
        }
        if (!hostIDState_2.equals("none")) {
            if (currentVirus.getHostList().contains(Integer.parseInt(uniqueHostIDs.get(uniqueHosts.indexOf(hostIDState_2))))) {
                dlmListCheck2.add(currentVirus);
                //dlmChecked2.addElement(String.valueOf(currentVirus.getId()) + "  (" + currentVirus.getSoort() + ")");
            }
        } else {
            dlmListCheck2.add(currentVirus);
            //dlmChecked2.addElement(String.valueOf(currentVirus.getId()) + "  (" + currentVirus.getSoort() + ")");
        }
    }

    public static void Compare() {
        extractedListModel = VirusGUI.listVirusID1.getModel();
        extractedVirusesDump.clear();
        replicatesListModel.clear();
        extractedViruses.clear();
        for (int i = 0; i < extractedListModel.getSize(); i++) {
            extractedViruses.add(extractedListModel.getElementAt(i));
        }
        extractedListModel = VirusGUI.listVirusID2.getModel();
        for (int i = 0; i < extractedListModel.getSize(); i++) {
            extractedViruses.add(extractedListModel.getElementAt(i));
        }
        for (Object vir : extractedViruses) {
            if (extractedVirusesDump.contains(vir)) {
                replicatesListModel.addElement(vir.toString());
            } else {
                extractedVirusesDump.add(vir);
            }
        }
        VirusGUI.listReplicateViruses.setModel(replicatesListModel);
    }
}
