package com.avereon.mazer;

import com.avereon.zarra.image.RenderedIcon;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The MazerIcon subclasses {@link RenderedIcon} to provide a custom, scalable
 * icon for the Mazer mod. This icon should be registered with Xenon when the
 * {@link Mazer} mod is registered and unregistered when the {@link Mazer} mod
 * is unregistered.
 */
public class MazerIcon extends RenderedIcon {

	private double mouthAngle = 30;

	private Random RANDOM = new Random( 8 );

	private Set<Point2D> points = new HashSet<>();

	public MazerIcon() {
		int chips = 30;
		double r = 13;
		double pie = (2 * Math.PI) / chips;

		double start = Math.toRadians( 1.5 * mouthAngle );
		double end = Math.toRadians( 360 - 1.5 * mouthAngle );
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

		fill( Color.TAN.darker() );

		startPath();
		points.forEach( ( p ) -> addDot( g( 16 + p.getX() ), g( 16 + p.getY() ) ) );
		closePath();
		fill( Color.SADDLEBROWN.darker() );
	}

	public static void main( String[] args ) {
		//save( new MazerIcon(), "target/icons/mazer.png" );
		proof( new MazerIcon() );
	}

}
