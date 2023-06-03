package ROBOSTONIGHT;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.Robot;

public class VugnaesSreo extends TeamRobot {

    private final double TAMANHO_MAPA = 1000;
    private final double MARGEM = 300;
    private boolean isMovingForward = true;

    public boolean getIntRandom(int max) {
        return Math.floor(Math.random() * max) == 0;
    }

    /** 
     * run: VugnaesSreo's default behavior
     */
    public void run() {
        // Configuração inicial do robô
        setColors(Color.red, Color.blue, Color.green); // Define as cores do corpo, arma e radar

        // Loop principal do robô
        while (true) {
            //Verifica se estamos perto do BorderGuard e toma a ação apropriada
            evitarBorda();
            
            if (getIntRandom(2)) {
                turnRight(Math.random() * 45);
                ahead(100);
            } else {
                turnLeft(Math.random() * 45);
                back(100);
            }
            
            // Gira a arma em um ângulo aleatório
            turnGunRight(Math.random() * 360);

            // Verifica se há um robô inimigo próximo
            turnRadarRight(360);
            scan();
        }
    }

    private double calculateFirePower(double distance) {
        double maxDistance = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        
        // Ajusta a potência com base na distância
        
        if(getEnergy() <= 10){
            return 1;
        }
        if(getEnergy() < 20){
            return 2;
        }
        if (distance < maxDistance / 4) {
            return 3;
        }
        if (distance < maxDistance / 2) {
            return 2;
        }
        if (distance < maxDistance ) {
            return 1;
        }
        

    }

    /**
     * onScannedRobot: O que fazer quando um robô inimigo é detectado
     */
    
    public void onScannedRobot(ScannedRobotEvent e) {
        // Verifica se o robô detectado é o BorderGuard
        if (!e.isSentryRobot()) {
            // Calcula a distância do robô inimigo
            double enemyDistance = e.getDistance();
            // Ajusta a mira para o robô inimigo
            double enemyBearing = e.getBearing();
            double gunTurn = getHeading() + enemyBearing - getGunHeading();
            turnGunRight(gunTurn);
            // Determina a potência do tiro com base na distância
            double firePower = calculateFirePower(enemyDistance);
            // Atira no robô inimigo com a potência calculada
            fire(firePower);
        }
        
    }

    private double normalizeBearing(double angle) {
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }
    /**
     * onHitByBullet: O que fazer quando o robô é atingido por uma bala
     */
    public void onHitByBullet(HitByBulletEvent e) {
        double bearingToCenter = normalizeBearing(getHeading() - (getBearing() + e.getBearing()));
        double deviation = 30; // Desvio padrão em graus

        // Escolher a direção para girar (esquerda ou direita) dependendo da rotação mais curta
        if (bearingToCenter > 0) {
            turnRight(bearingToCenter - deviation);
        } else {
            turnLeft(-bearingToCenter - deviation);
        }

        ahead(100); // Move-se em frente após a rotação
        // double x = getX();
        // double y = getY();
		
        // if (getIntRandom(2)) {
        //     double angulo = Math.toDegrees(Math.atan2(500 - x, 500 - y));
            
        //     // Girar em direção ao centro
        //     turnRight(angulo - getHeading());
            
        //     ahead(100);
        // } else {
        //     double angulo = Math.toDegrees(Math.atan2(500 - x, 500 - y));
            
        //     // Girar em direção ao centro
        //     turnLeft(angulo - getHeading());
        //     ahead(100);
        // }
        // Move-se para trás e gira em um ângulo aleatório para evitar ser atingido novamente
        

    }

    private void evitarBorda() {
        double x = getX();
        double y = getY();
        
        // Verificar se o robô está próximo à borda
        if (x < MARGEM || x > TAMANHO_MAPA - MARGEM || y < MARGEM || y > TAMANHO_MAPA - MARGEM) {
            // Calcular a angulação necessária para girar em direção ao centro
            double angulo = Math.toDegrees(Math.atan2(500 - x, 500 - y));
            
            // Girar em direção ao centro
            turnRight(angulo - getHeading());
            ahead(100);
        }
    }
}
