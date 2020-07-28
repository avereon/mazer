module com.avereon.mazer {

	requires com.avereon.xenon;
	requires com.avereon.zenna;
	requires com.avereon.zerra;
	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.graphics;

	opens com.avereon.mazer.bundles;

	exports com.avereon.mazer to com.avereon.xenon, com.avereon.zerra;

	provides com.avereon.xenon.Mod with com.avereon.mazer.Mazer;

}
