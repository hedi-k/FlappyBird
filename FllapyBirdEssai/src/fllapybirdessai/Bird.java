package fllapybirdessai;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 *
 * @author hedi
 */
public class Bird extends JPanel implements ActionListener, KeyListener {

    //Paramètres:
    int boardWidth = 360;
    int boardHeight = 640;
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;
    int velocityY = 0; // négatif Y => monte dans l'image 
    int gravity = +1;   // positif donc déscend dans l'image
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    int velocityX = -4;
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    ParamBird paramBird;
    Timer gameLoop; //boucle d'affichage du jeux.
    Timer placePipesTimer; //boucle de génération des tuyaux.
    ArrayList<Pipe> tabPipes;
    Random random = new Random();
    boolean gameOver = false;
    double score = 0;

    Bird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //Ce panel est bien le focus pour les KeyEvent et écoute l'event.
        setFocusable(true);
        addKeyListener(this);
        //Valorise les images
        backgroundImg = new ImageIcon(getClass().getResource("/Images/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/Images/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/Images/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/Images/bottompipe.png")).getImage();
        //Valorise un "oiseau"
        paramBird = new ParamBird(birdImg);
        tabPipes = new ArrayList<Pipe>();
        //Boucle sur la génération d'un tuyaux tout les 1.5s appel placePipes()
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }

        });
        placePipesTimer.start();
        //Boucle du jeu 1000ms divisé par 60 =>60 images secondes
        //Le timer appel actionPerformed 60 fois par seconde d'où l'implements
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    /**
     * Cette méthode est responsable du dessin des composants graphiques dans
     * une fenêtre ou un panneau. Elle est appelée automatiquement par le
     * système lorsque le composant doit être redessiné, paintComponent est
     * appelé par fram.pack();
     *
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    //Dessine une image avec paramètres (coordonnées,longueur...)
    public void draw(Graphics g) {
        //Image de fond
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        //Image de l'oiseau
        g.drawImage(birdImg, paramBird.x, paramBird.y, birdWidth, birdHeight, null);
        //Bouble pour dessiner les tuyaux,
        for (int i = 0; i < tabPipes.size(); i++) {
            Pipe pipe = tabPipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("PERDU ! score : " + String.valueOf((int)score),10,35);
        }else{
            g.drawString(String.valueOf((int)score),10,35);
        }
    }

    //implements ActionListener
    //Va appeler ces méthodes 60 fois par secondes.
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    //Permet de faire déplacer "l'oiseau" et tuyaux (verticale)
    //Math.max retourne la plus grand valeur entre ces paramètres,
    //comme paramBird.y se réduit 0 sera la plus grande valeur et donc paramBird.Y sera 0 en MAX
    public void move() {
        velocityY += gravity;
        paramBird.y += velocityY;
        //Pour l'empecher de sortir de l'écran
        paramBird.y = Math.max(paramBird.y, 0);
        //paramBird.y = Math.min(paramBird.y, 630); // ça me rend triste de ne plus voir mon poulet dans l'écran.
        //Fait déplacer les tuyaux
        for (int i = 0; i < tabPipes.size(); i++) {
            Pipe pipe = tabPipes.get(i);
            pipe.x += velocityX;
            
            if (!pipe.passed && pipe.x > pipe.width){
                pipe.passed = true;
                score += 0.5;
            }
            //Si touche tuyaux c'est game over.
            if (collision(paramBird, pipe)){
                gameOver = true;
            }
        }
        //Si sort de l'écran par le bas c'est game over.
        if (paramBird.y > boardHeight) {
            gameOver = true;
        }
    }

    //Construit les tuyaux et les placent dans le tableau de tuyaux.
    public void placePipes() {
        //Donne un nombre aléatoire entre 1/4 et 3/4 du tuyaux.  
        //                       0     -     128     -   (entre 0 et 1) *    256  
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        //L'espace pour laisser passer l'oiseau entre deux tuyaux.
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        tabPipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        tabPipes.add(bottomPipe);
    }

    //Controle collision return true si contact.
    public boolean collision(ParamBird a, Pipe b) {
        return a.x < b.x + b.width
                && //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x
                && //a's top right corner passes b's top left corner
                a.y < b.y + b.height
                && //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    //Action quand je tape sur une touche mais ne prends pas en compte les touche "speciale" comme F5 ou espace.
    @Override
    public void keyTyped(KeyEvent e) {
    }

    //Action quand je tape sur une touche espace pris en compte.
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //Chaque barre d'espace me fera monter de 9.
            velocityY = -9;
            //Reinitialise le jeux
            if (gameOver){
                paramBird.y = birdY;
                velocityY =0;
                tabPipes.clear();
                score =0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    //Action quand je relache une touche pas necessaire ici.
    @Override
    public void keyReleased(KeyEvent e) {
    }

    //Classe qui contient les caractéristiques de l'oiseau afin de le créer.
    class ParamBird {

        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        ParamBird(Image img) {
            this.img = img;
        }
    }

    //Classe qui contient les caractéristiques des tuyaux.
    class Pipe {

        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }
}
