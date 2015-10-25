/*
    libVMR
    
    Copyright (C) 2014,  Yury V. Reshetov

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

/**
 * jPrediction
 *
 * @author Yury V. Reshetov
 * @version 1.00
 */

import libvmr.VMR;
import libvmr.parsers.Parser;
import libvmr.testers.tools.Conveyor;
import libvmr.testers.tools.SearchEngine;
import libvmr.testers.tools.Store;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class JPrediction extends Frame {

    /**
     *  jPrediction
     * @author Yury V. Reshetov
     * @version 3.01
     */
    private static final long serialVersionUID = 1L;

    /**
     * Vector machine by Reshetov
     */
    private VMR vmr = new VMR();

    /**
     * Constructor
     */
    public JPrediction() {
        Frame frame = this;
        // Create Frame
        this.setTitle("jPrediction");
        this.setLayout(new BorderLayout());
        Panel panel1 = new Panel();
        this.add(panel1, BorderLayout.EAST);
        Panel panel2 = new Panel();
        this.add(panel2, BorderLayout.WEST);
        Panel panel3 = new Panel();
        this.add(panel3, BorderLayout.SOUTH);
        Panel panel4 = new Panel();
        this.add(panel4, BorderLayout.NORTH);
        Label threadslabel = new Label("Threads:");
        panel3.add(threadslabel);
        Choice threadscount = new Choice();
        for (int i = 1; i < 128; i++) {
            threadscount.add(i + "");
        }
        threadscount.select(3);
        panel3.add(threadscount);
        TextArea ta = new TextArea();
        this.add(ta, BorderLayout.CENTER);
        MenuBar menubar = new MenuBar();
        Menu filemenu = new Menu("File");
        MenuItem openmenuitem = new MenuItem("Open ...");
        openmenuitem.setActionCommand("open");
        MenuItem savemenuitem = new MenuItem("Save model ...");
        openmenuitem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savemenuitem.setEnabled(false);
                openmenuitem.setEnabled(false);
                threadscount.setEnabled(false);

                FileDialog fd = new FileDialog(frame, "Load CSV file",
                        FileDialog.LOAD);
                fd.setFile("*.csv");
                fd.setVisible(true);
                if (fd.getFile() == null) {
                    openmenuitem.setEnabled(true);
                    threadscount.setEnabled(true);
                    return;
                }

                ta.setText("Please wait");
                // Create model
                Parser parser = new Parser();
                File file = new File(fd.getDirectory() + fd.getFile());
                double[][] samples = parser.parsing(file);
                if (parser.getError() != null) {
                    ta.setText(parser.getError());
                    openmenuitem.setEnabled(true);
                    threadscount.setEnabled(true);
                    return;
                }

                Conveyor conveyor = new Conveyor();
                int threads = threadscount.getSelectedIndex() + 1;
                for (int i = 0; i < threads; i++) {
                    new SearchEngine(conveyor, samples);
                }
                Store store = conveyor.getStore();
                for (int i = 0; i < 100; i++) {
                    conveyor.setNumber(i);
                    ta.setText("Progress: " + store.getCounter() + "%\n");
                    if (store.getVMR() != null) {
                        ta.append(store.getVMR().getFormula());
                    }
                }
                for (int i = 0; i < threads; i++) {
                    conveyor.setNumber(-1);
                }
                while (store.getCounter() < 100) {

                }
                vmr = store.getVMR();
                if (vmr.getGeneralizationAbility() > 0d) {
                    ta.setText("The quality of modeling:\n\n");
                    ta.append("Sensitivity of generalization abiliy: "
                            + vmr.getSensitivity() + "%\n");
                    ta.append("Specificity of generalization ability: "
                            + vmr.getSpecificity() + "%\n");
                    ta.append("Generalization abiliy: "
                            + vmr.getGeneralizationAbility() + "%\n");
                    ta.setSelectionStart(0);
                    ta.setSelectionEnd(0);
                    savemenuitem.setEnabled(true);
                } else {
                    savemenuitem.setEnabled(false);
                    ta.setText("Bad data");
                }

                openmenuitem.setEnabled(true);
                threadscount.setEnabled(true);
            }
        });
        savemenuitem.setActionCommand("save");
        savemenuitem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(frame, "Save Model",
                        FileDialog.SAVE);
                fd.setFile("*.txt");
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    String filename = fd.getFile();
                    if (!filename.endsWith(".txt")) {
                        filename = filename + ".txt";
                    }
                    try {
                        File file = new File(fd.getDirectory() + filename);
                        PrintWriter pw = new PrintWriter(new FileWriter(file));
                        String formula = vmr.getFormula();
                        StringTokenizer st = new StringTokenizer(formula, "\n");
                        while (st.hasMoreTokens()) {
                            pw.println(st.nextToken());
                        }
                        pw.flush();
                        pw.close();
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("vmr.ser"));
                        oos.writeObject(vmr);
                        oos.flush();
                        oos.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        savemenuitem.setEnabled(false);
        MenuItem exitmenuitem = new MenuItem("Exit");
        exitmenuitem.setActionCommand("exit");
        exitmenuitem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        filemenu.add(openmenuitem);
        filemenu.add(savemenuitem);
        filemenu.addSeparator();
        filemenu.add(exitmenuitem);
        Menu about = new Menu("About ...");
        MenuItem copyright = new MenuItem(
                "CopyRight (с) 2014, Yury V. Reshetov");
        MenuItem licencse = new MenuItem("General Public License version 3");
        MenuItem siteurl = new MenuItem("Help online ...");
        siteurl.setActionCommand("link");
        siteurl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop d = Desktop.getDesktop();

                    d.browse(new URI(String.format(
                            "https://sourceforge.net/p/libvmr/wiki/Home",
                            URLEncoder
                                    .encode("запрос с кучей пробелов", "UTF8"))));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (URISyntaxException use) {
                    use.printStackTrace();
                }
            }
        });
        MenuItem opensource = new MenuItem("Get the open source code ...");
        opensource.setActionCommand("opensource");
        opensource.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop d = Desktop.getDesktop();

                    d.browse(new URI(String.format(
                            "https://sourceforge.net/p/libvmr/code", URLEncoder
                                    .encode("запрос с кучей пробелов", "UTF8"))));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (URISyntaxException use) {
                    use.printStackTrace();
                }
            }
        });
        about.add(copyright);
        about.add(licencse);
        about.add(siteurl);
        about.add(opensource);
        menubar.add(filemenu);
        menubar.add(about);
        this.setMenuBar(menubar);
        this.pack();
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new JPrediction();
    }

}
