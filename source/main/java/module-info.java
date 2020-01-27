module com.avereon.mazer {

	requires com.avereon.xenon;
	requires com.avereon.rossa;
	requires com.avereon.venza;
	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.graphics;

	opens com.avereon.mazer.bundles;

	exports com.avereon.mazer to com.avereon.xenon, com.avereon.venza;

	provides com.avereon.xenon.Mod with com.avereon.mazer.Mazer;

}
