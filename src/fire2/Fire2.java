/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fire2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author dam
 */
public class Fire2 {

    /**
     * @param args the command line arguments
     */
    static int xsize = 800;
    static int ysize = 400;
    static int[] n1;
    static int[] n2;
    static int[] n3;
    static int[] n4;
    static int[] c;
    static int p;

    static boolean first = true;

    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        BufferedImage image1 = new BufferedImage(xsize, ysize, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage image2 = new BufferedImage(xsize, ysize, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage imagec = ImageIO.read(new File("src\\images\\cooling.jpg"));

        byte[] buffer1 = ((DataBufferByte) image1.getRaster().getDataBuffer()).getData();
        byte[] buffer2 = ((DataBufferByte) image2.getRaster().getDataBuffer()).getData();
        byte[] bufferc = ((DataBufferByte) imagec.getRaster().getDataBuffer()).getData();

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(xsize + 15, ysize + 39);

        frame.setVisible(true);

        JPanel pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                while (first) {
                    super.paintComponent(g);
                    this.setBackground(Color.black);
                    startFire(buffer1);
                    first = false;
                }

                expandFire(ysize, xsize, buffer1, bufferc, buffer1);
                g.drawImage(image1, 0, 0, null);

            }

        };

        frame.add(pane);
////        Redibujamos infinitamente
        while (true) {
            frame.repaint();
//            Thread.sleep(50);
        }
    }

    private static void startFire(byte[] buffer) {

        Random rand = new Random();

        for (int x = 0; x < xsize; x++) {

            int pixel = (x + (ysize - 1) * xsize);

            int random = rand.nextInt(100);

            if (random < 3) {
                buffer[0 + 3 * pixel] = (byte) 0;
                buffer[1 + 3 * pixel] = (byte) 0;
                buffer[2 + 3 * pixel] = (byte) 255;
            }

        }
    }

    private static void expandFire(int ysize, int xsize, byte[] buffer1, byte[] bufferc, byte[] buffer2) {

        for (int y = 2; y < (ysize - 2); y++) {
            for (int x = 0; x < (xsize - 2); x++) {
                n1 = getBGR(buffer1, xsize, ysize, x + 1, y);
                n2 = getBGR(buffer1, xsize, ysize, x - 1, y);
                n3 = getBGR(buffer1, xsize, ysize, x, y + 1);
                n4 = getBGR(buffer1, xsize, ysize, x, y - 1);

                c = getBGR(bufferc, xsize, ysize, x, y);

                for (int i = 0; i < 3; i++) {
                    p = avgBGR(n1[i], n2[i], n3[i], n4[i], c[i]);
                    setBGR(buffer2, xsize, ysize, x, y - 1, i, p);
                }

                buffer1 = buffer2;
            }
        }

    }

    private static int[] getBGR(byte[] buffer, int xsize, int ysize, int x, int y) {
        int[] bgr = new int[3];
        int pixel = (x + (ysize - y) * xsize);

        for (int i = 0; i < 3; i++) {
            bgr[i] = buffer[i + 3 * pixel] & 0xff;
        }

        return bgr;
    }

    private static int avgBGR(int n1, int n2, int n3, int n4, int c) {

        int avg;
        avg = (n1 + n2 + n3 + n4) / 4;
        avg = avg - c;

        if (avg < 0) {
            avg = 0;
        }

        return avg;
    }

    private static void setBGR(byte[] buffer, int xsize, int ysize, int x, int y, int i, int p) {

        int pixel = (x + (ysize - y) * xsize);

        buffer[i + 3 * pixel] = (byte) p;

    }
}
