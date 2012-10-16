/**
 * @artifact.name@ Tag Library
 *
 * Description
 */
@artifact.package@class @artifact.name@ {
	// define the tag namespace (e.g.: <foo:action ... />
	static namespace = "foo"

	/**
	 * bar tag (e.g. <foo:bar ... />
	 * @param Map attributes
	 * @param Closure
	 */
	def bar = { attrs, body ->
		// render bar
		out << '--- this is the bar tag ---<br/>'
		out << render(view: "_someView") // renders views/@artifact.name@/_someView.gsp
		out << '--- and here it ends ---<br/>'
	}
}
