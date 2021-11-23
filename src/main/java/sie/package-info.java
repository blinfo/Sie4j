/**
 * This package contains the class Sie4j and the interface DataReader.
 * <h3>Sie4j</h3>
 * <p>
 * Sie4j is the class needed for parsing data to and from the SIE format.
 * <p>
 * <h4>Sample - SIE to JSON</h4>
 * <p>
 * To convert SIE data in a file to a JSON string:
 * </p>
 * <code><pre>
 * InputStream input = new FileInputStream( ... );
 * String jsonString = Sie4j.asJson(input);
 * </pre></code>
 * <p>
 * The result should be something like:
 * </p>
 * <code><pre>
 * {"metaData":{"read":false,"program":{"name":"Kassasystem","version":"1.0"},"generated":{"date":"2018-05-03","signature":"Lars ...
 * </pre></code>
 * <h3>DataReader</h3>
 * <p>
 * DataReader has two methods: read(), which returns the Document and
 * validate(), which returns the DocumentValidator.
 */
package sie;
