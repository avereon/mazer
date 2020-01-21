module com.avereon.mazer {
	requires com.avereon.xenon;
	requires transitive com.avereon.venza;
	requires transitive com.avereon.zevra;
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires com.avereon.rossa;

	opens com.avereon.mazer.bundles;

	exports com.avereon.mazer to com.avereon.xenon;

	provides com.avereon.xenon.Mod with com.avereon.mazer.Mazer;
}
