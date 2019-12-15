package com.avereon.mazer;

import com.avereon.venza.image.ProgramIcon;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MazerIcon extends ProgramIcon {

	private double mouthAngle = 30;

	private Random RANDOM = new Random( 8 );

	private Set<Point2D> points = new HashSet<>();

	public MazerIcon() {
		int chips = 30;
		double r = 13;
		double pie = (2 * Math.PI) / chips;

		double start = 1.5* mouthAngle  * RADIANS_PER_DEGREE;
		double end = (360 - 1.5 * mouthAngle)  * RADIANS_PER_DEGREE;
		for( double a = start; a < end; a += pie ) {
			double n = 0.33 + 0.67 * RANDOM.nextDouble();
			double x = r * Math.cos( a ) * n;
			double y = r * Math.sin( a ) * n;
			points.add( new Point2D( x, y ) );
		}
	}

	@Override
	protected void render() {
		startPath();
		moveTo( g( 16 ), g( 16 ) );
		addArc( g( 16 ), g( 16 ), g( 15 ), g( 15 ), mouthAngle, 360 - 2 * mouthAngle );
		closePath();

		fillAndDraw( getIconFillPaint( Color.TAN, GradientTone.MEDIUM ), Color.SADDLEBROWN.darker() );
		setDrawPaint( Color.SADDLEBROWN.darker() );

		points.forEach( ( p ) -> drawDot( g( 16 + p.getX() ), g( 16 + p.getY() ) ) );
	}

	public static void main( String[] args ) {
		save( new MazerIcon(), "target/icons/mazer.png" );
		proof( new MazerIcon() );
		wrapup();
	}

}
