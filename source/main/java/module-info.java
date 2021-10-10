module com.avereon.mazer {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.avereon.xenon;
	requires com.avereon.zenna;
	requires com.avereon.zarra;
	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.graphics;

	opens com.avereon.mazer.bundles;

	exports com.avereon.mazer to com.avereon.xenon, com.avereon.zarra;

	provides com.avereon.xenon.Mod with com.avereon.mazer.Mazer;

}
