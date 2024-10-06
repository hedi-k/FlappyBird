package fllapybirdessai;

import javax.swing.*;

/**
 *  Flappy bird game !
 * @author hedi
 */
public class FllapyBirdEssai {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         //Paramètres:
        //Fenêtre du jeux largeur .
        int boardWidth = 360;
        //Fenêtre du jeux hauteur.
        int boardHeight = 640;
        
        //Fenêtre principale
        JFrame frame = new JFrame("Flappy pigeon !");
        //Dimensionne la fenêtre.
        frame.setSize(boardWidth, boardHeight);
        //Place la fenêtre au centre de l'écran.
        frame.setLocationRelativeTo(null);
        //Empêche de modifier la taille de la fenêtre du jeux.
        frame.setResizable(false);
        //Ferme la programme quand on ferme la fenêtre.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //On ajoute a la fenêtre principale l'instance de la classe Bird.
        Bird Bird = new Bird();
        frame.add(Bird);
        //Ajuste la taille de la fenêtre pour que tout s'affiche
        //correctement (vis a vis de la barre de titre en haut).
        //Appel paintComponent()
        frame.pack();
        //utilité relative du focus ?
        //Bird.requestFocus();
        frame.setVisible(true);
    }
    }
    

