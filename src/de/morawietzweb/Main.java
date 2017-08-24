package de.morawietzweb;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    static File sourceFile, destFile;
    static JFileChooser fileChooser = new JFileChooser();

    static JFrame window = new JFrame("ResizeTool morambiente.de");

    static JLabel lblSource, lblDest;
    static JProgressBar progress = new JProgressBar();

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }

        addWindowListener();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2,5,5));
        JButton btnChooseSource = new JButton("Quellverzeichnis");
        JButton btnChooseDest = new JButton("Zielverzeichnis");
        JButton btnConvert = new JButton("Verkleinern");
        final JCheckBox cbxWatermark = new JCheckBox("'Morambiente' Label");
        cbxWatermark.setSelected(true);
        progress.setMinimum(0);
        progress.setStringPainted(true);
        lblSource = new JLabel();
        lblDest = new JLabel();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        btnChooseSource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileChooser.showDialog(window, "Verzeichnis wählen");
                sourceFile = fileChooser.getSelectedFile();
                lblSource.setText(sourceFile.getAbsolutePath());
            }
        });
        btnChooseDest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileChooser.showDialog(window, "Verzeichnis wählen");
                destFile = fileChooser.getSelectedFile();
                lblDest.setText(destFile.getAbsolutePath());
            }
        });
        btnConvert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (destFile == null || sourceFile == null)
                    return;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File[] files = sourceFile.listFiles();
                        progress.setMaximum(files.length);
                        BufferedImage img;
                        int progressCount = 0;
                        for (File file : sourceFile.listFiles()) {
                            progressCount++;
                            progress.setValue(progressCount);
                            if (file.isDirectory() || !(file.getName().endsWith(".jpg") || file.getName().endsWith(".JPG")))
                                continue;

                            try {
                                //Load img
                                img = ImageIO.read(file);

                                if (img == null) {
                                    System.out.println(file.getName() + " defekt");
                                    continue;
                                }
                                int h = img.getHeight();
                                int w = img.getWidth();

                                //Calc new size
                                int newh, neww;
                                if (h < w) {
                                    newh = 870;
                                    neww = w * 870 / h;
                                } else {
                                    neww = 870;
                                    newh = h * 870 / w;
                                }

                                //Scale
                                Image scaled = img.getScaledInstance(neww, newh, Image.SCALE_AREA_AVERAGING);

                                //Image to BufferedImage
                                BufferedImage scaledBuffered = new BufferedImage(neww, newh, BufferedImage.TYPE_INT_RGB);
                                Graphics2D g2 = scaledBuffered.createGraphics();
                                g2.drawImage(scaled, 0, 0, null);
                                //DrawWatermark
                                if(cbxWatermark.isSelected()) {
                                    g2.drawImage(ImageIO.read(getClass().getResource("/watermark.png")), neww - 870, newh - 100, null);
                                }
                                g2.dispose();

                                File newFile = new File(destFile.getAbsolutePath() + File.separator + file.getName());
                                System.out.println("Größe von " + file.getAbsolutePath() + " geändert. Gespeichert nach " + newFile.getAbsolutePath());

                                ImageIO.write(scaledBuffered, "jpg", newFile);

                            } catch (IOException e) {
                                continue;
                            }

                        }
                    }
                }).start();

            }
        });

        panel.add(btnChooseSource);
        panel.add(lblSource);
        panel.add(btnChooseDest);
        panel.add(lblDest);
        panel.add(btnConvert);
        panel.add(progress);
        panel.add(cbxWatermark);

        window.setContentPane(panel);

        window.pack();
        window.setBounds(10, 10, 800, window.getHeight() + 10);
        window.setVisible(true);
    }


    public static void addWindowListener() {
        window.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
    }


}
