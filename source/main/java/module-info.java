module com.avereon.mazer {
	requires com.avereon.venza;
	requires com.avereon.xenon;
	requires com.avereon.zevra;
	requires javafx.graphics;

	exports com.avereon.mazer to com.avereon.xenon;

	provides com.avereon.xenon.Mod with com.avereon.mazer.Mazer;
}
