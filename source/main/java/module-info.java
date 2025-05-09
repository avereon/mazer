import com.avereon.xenon.Module;

module com.avereon.mazer {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.avereon.xenon;
	requires com.avereon.zenna;
	requires com.avereon.zerra;
	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.graphics;

	opens com.avereon.mazer.bundles;

	exports com.avereon.mazer to com.avereon.xenon, com.avereon.zerra;

	provides Module with com.avereon.mazer.Mazer;

}
