package ROBOSTONIGHT;

import robocode.*;
import java.awt.Color;
import java.io.IOException;

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
        setColors(Color.red, Color.yellow, Color.green); // Define as cores do corpo, arma e radar

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
        return 1;
        
    }

    /**
     * onScannedRobot: O que fazer quando um robô inimigo é detectado
     */
    
    public void onScannedRobot(ScannedRobotEvent e) {
        // Verifica se o robô detectado é o BorderGuard
        if (!e.isSentryRobot() && !isTeammate(e.getName())) {
			 try {
			 	sendMessage("SapoDeLantejoulas", new EnemyScannedMessage(e.getName(), e.getDistance(), e.getBearing()));
			 } catch(IOException exp) {
			 	//tratar de alguma forma
			 }
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

    public void onHitByBullet(HitByBulletEvent e) {
        double bearingToCenter = normalizeBearing(getHeading() - e.getBearingRadians());
        double deviation = 30;

        if (bearingToCenter > 0) {
            turnRight(bearingToCenter - deviation);
        } else {
            turnLeft(-bearingToCenter - deviation);
        }

        ahead(100);

    }

    private void evitarBorda() {
        double x = getX();
        double y = getY();
        
        if (x < MARGEM || x > TAMANHO_MAPA - MARGEM || y < MARGEM || y > TAMANHO_MAPA - MARGEM) {
            
            double angulo = Math.toDegrees(Math.atan2(500 - x, 500 - y));
            
            turnRight(angulo - getHeading());
            ahead(100);
        }
    }
}

class EnemyScannedMessage implements java.io.Serializable {
    private String enemyName;
    private double enemyDistance;
    private double enemyBearing;

    public EnemyScannedMessage(String enemyName, double enemyDistance, double enemyBearing) {
        this.enemyName = enemyName;
        this.enemyDistance = enemyDistance;
        this.enemyBearing = enemyBearing;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public double getEnemyDistance() {
        return enemyDistance;
    }

    public double getEnemyBearing() {
        return enemyBearing;
    }
}

// Classe para representar uma mensagem informando que o robô está pronto para a batalha
class RobotReadyMessage implements java.io.Serializable {
    // Pode ser vazia, pois não é necessário enviar informações adicionais
}