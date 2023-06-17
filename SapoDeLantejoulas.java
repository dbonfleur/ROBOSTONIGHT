package ROBOSTONIGHT;

import robocode.*;
import java.awt.Color;

public class SapoDeLantejoulas extends TeamRobot
{
	 private final double TAMANHO_MAPA = 1000;
    private final double MARGEM = 300;
    private boolean isMovingForward = true;

    public boolean getIntRandom(int max) {
        return Math.floor(Math.random() * max) == 0;
    }
	
	public void run() {
		setColors(Color.red, Color.yellow, Color.green);
		
		while(true) {
		
			evitarBorda();
            
            if (getIntRandom(2)) {
                turnRight(Math.random() * 45);
                ahead(100);
            } else {
                turnLeft(Math.random() * 45);
                back(100);
            }
            
            turnGunRight(Math.random() * 360);

            turnRadarRight(360);
            scan();
		}
	}

	private double calculateFirePower(double distance) {
        double maxDistance = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        
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

    public void onScannedRobot(ScannedRobotEvent e) {
        // Verifica se o robô detectado é o BorderGuard
        if (!e.isSentryRobot() && !isTeammate(e.getName())) {
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
