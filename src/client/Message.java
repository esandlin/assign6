package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.*;
import java.util.*;
import client.MessageLibrary;
import server.Student;

/**
 * Copyright (c) 2015 Tim Lindquist, Software Engineering, Arizona State
 * University at the Polytechnic campus
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful, but without
 * any warranty or fitness for a particular purpose.
 * <p/>
 * Please review the GNU General Public License at:
 * http://www.gnu.org/licenses/gpl-2.0.html see also:
 * https://www.gnu.org/licenses/gpl-faq.html so you are aware of the terms and
 * your rights with regard to this software. Or, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,USA
 * <p/>
 * Purpose: Sample Java Swing controller class. FolderBrowserGUI constructs the
 * view components for a sample GUI. This class is extends the GUI to provide
 * the control functionality. When the user does a tree node selection, this
 * valueChanged is called, but virtue of being a TreeSelectionListener and
 * adding itself as a listerner. FolderBrowser defines the call-backs for the
 * JButton as well. It contains sample control functions that respond to button
 * clicks and tree selects. This software is meant to run on Debian Wheezy Linux
 * <p/>
 * Ser321 Principles of Distributed Software Systems see
 * http://pooh.poly.asu.edu/Ser321
 * 
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering,
 *         IAFSE, ASU at the Polytechnic campus
 * @file FolderBrowserGUI.java
 * @date July, 2015
 **/

public class Message extends Object implements Runnable{

    static final boolean debugOn = false;
    static protected String host = "$localhost";
    static protected String port = "1099";

    @SuppressWarnings("unused")
    private static void debug(String message) {
        if (debugOn)
            System.out.println("debug: " + message);
    }

    private MessageGUI theView;

    /**
     * @param theView
     * @param theModel
     */
    public Message(MessageGUI theView) {
        this.theView = theView;
        theView.setDateTextField(theView.getDateTextField());

        /*
         * Tell the View that when ever the button is clicked to execute the
         * actionPerformed method in the Listener inner class
         */
        this.theView.addCypherListener(new Listener());
        this.theView.addDeleteListener(new Listener());
        this.theView.addSendListener(new Listener());
        this.theView.addReplyListener(new Listener());
    }
    
    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            // The OSName.java
            System.out.println(System.getProperty("os.name"));
            System.out.println(InetAddress.getLocalHost().getCanonicalHostName());

                String url = "http://" + host + ":" + port + "/";
                System.out.println("Opening connection to: " + url);
                MessageLibrary sc = (MessageLibrary) new MessageLibrary(host, Integer.parseInt(port));
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

                System.out.print("Enter end or {add|get|getNameById|getNames|remove} followed by args>");
                String inStr = stdin.readLine();
                StringTokenizer st = new StringTokenizer(inStr);
                String opn = st.nextToken();
                while (!opn.equalsIgnoreCase("end")) {
                    if (opn.equalsIgnoreCase("add")) {
                        String name = "";
                        while (st.hasMoreTokens()) {
                            name = name + st.nextToken();
                            if (st.hasMoreTokens())
                                name = name + " ";
                        }
                        Student aStud = new Student(name, 7, new String[] { "Ser423", "Ser321" });
                        boolean result = sc.add(aStud);
                        System.out.println("Add " + aStud.name + " result " + result);
                    } else if (opn.equalsIgnoreCase("get")) {
                        String name = "";
                        while (st.hasMoreTokens()) {
                            name = name + st.nextToken();
                            if (st.hasMoreTokens())
                                name = name + " ";
                        }
                        Student result = sc.get(name);
                        System.out.println("Got " + result.toString());
                    } else if (opn.equalsIgnoreCase("getNames")) {
                        String[] result = sc.getNames();
                        System.out.print("The collection has entries for: ");
                        for (int i = 0; i < result.length; i++) {
                            System.out.print(result[i] + ", ");
                        }
                        System.out.println();
                    } else if (opn.equalsIgnoreCase("remove")) {
                        String name = st.nextToken();
                        while (st.hasMoreTokens()) {
                            name = name + " " + st.nextToken();
                        }
                        boolean result = sc.remove(name);
                        System.out.println("remove " + name + " result " + result);
                    } else if (opn.equalsIgnoreCase("getNamebyid")) {
                        int idNo = Integer.parseInt(st.nextToken());
                        String result = sc.getNameById(idNo);
                        System.out.println(result + " has id number " + idNo);
                    }
                    System.out.print("Enter end or {add|get|getNameById|getNames|remove} followed by args>");
                    inStr = stdin.readLine();
                    st = new StringTokenizer(inStr);
                    opn = st.nextToken();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Oops, you didn't enter the right stuff");
            }
         
    }

    /**
     * The Main starts the program
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {

        // The MVC
        MessageGUI theView = new MessageGUI();

        theView.setVisible(true);
        if (args.length >= 2) {
            host = args[0];
            port = args[1];
        }
        
        Message m = new Message(theView);
        new Thread(m).start();
        
        
    }

    class Listener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // JTextField to, from, subject, date;

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
               @Override
                protected Void doInBackground() throws Exception {
                    /*
                     * Surround interactions with the view with a try block just in case
                     */
                    try {
                        theView.getToTextField();
                        theView.getFromTextField();
                        theView.getSubjectTextField();
                        theView.getDateTextField();
                        /*
                         * This is for Deleting nodes
                         */
                        if (e.getActionCommand().equals("Delete")) {

                            Thread.sleep(2000);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) theView.tree
                                            .getSelectionPath().getLastPathComponent();

                                    if (selectedNode != theView.tree.getModel().getRoot()) {
                                        DefaultTreeModel model = (DefaultTreeModel) theView.tree.getModel();

                                        model.removeNodeFromParent(selectedNode);
                                        model.reload();
                                    }
                                }
                            });
                            /*
                             * This is for creating new nodes
                             */
                        } else if (e.getActionCommand().equals("Reply")) {

                            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) theView.tree
                                    .getSelectionPath().getLastPathComponent();

                            // DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();

                            selectedNode.add(new DefaultMutableTreeNode(theView.getSubjectTextField().getText()));

                            String str = "RE:";

                            // theView.setFromTextField(str);

                            DefaultTreeModel model = (DefaultTreeModel) theView.tree.getModel();
                            model.reload();
                            /*
                             * todo
                             */
                        } else if (e.getActionCommand().equals("Send Text")) {

                            System.out.println("you pressed send text");
                            /*
                             * todo
                             */
                        } else if (e.getActionCommand().equals("Send Cipher")) {

                        }

                    } catch (NumberFormatException ex) {
                        System.out.println(ex);
                        theView.displayErrorMessage("Something went wrong.");
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    return null;
                }
            };
            worker.execute();
        }
    }
}