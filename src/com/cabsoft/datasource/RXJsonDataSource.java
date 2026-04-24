package com.cabsoft.datasource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cabsoft.characterdetctor.UniversalDetector;
import com.cabsoft.rx.engine.RXException;
import com.cabsoft.rx.engine.RXField;
import com.cabsoft.rx.engine.RXRewindableDataSource;
import com.cabsoft.rx.engine.RXReportsContext;
import com.cabsoft.rx.engine.RXRuntimeException;
import com.cabsoft.rx.engine.data.RXAbstractTextDataSource;
import com.cabsoft.rx.engine.util.JsonUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * JSON data source implementation
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 * @version $Id: RXJsonDataSource.java 5627 2012-08-31 09:59:29Z narcism $
 */
public class RXJsonDataSource extends RXAbstractTextDataSource implements RXRewindableDataSource {

	private static final Log log = LogFactory.getLog(RXJsonDataSource.class);
	// the JSON select expression that gives the nodes to iterate
	private String selectExpression;

	private Iterator<JsonNode> jsonNodesIterator;

	// the current node
	private JsonNode currentJsonNode;

	private final String PROPERTY_SEPARATOR = ".";

	private final String ARRAY_LEFT = "[";

	private final String ARRAY_RIGHT = "]";

	private final String ATTRIBUTE_LEFT = "(";

	private final String ATTRIBUTE_RIGHT = ")";

	// the JSON tree as it is obtained from the JSON source
	private JsonNode jsonTree;

	private ObjectMapper mapper;

	private InputStream jsonStream;
	private JsonNode dsjsonNode;

	private boolean toClose;
	private boolean saveJsonNode = false;

	public static RXJsonDataSource createDatasource() {

		try {
			String fileNM = "c:\\jar\\json.json";
			String quertStr = null;
			boolean yn_dataSourceUse = true;

			File file = new File(fileNM);
			if (file.exists()) {
				// return new RXJsonDataSource(file);
				byte[] data = null;
				int size = 0;
				FileInputStream fis = null;
				// file = new File(fileNM);
				fis = new FileInputStream(file);
				size = fis.available();
				data = new byte[size];
				fis.read(data);
				fis.close();
				file = null;
				String dataString = new String(data, detector(data));
				System.out.println(dataString);
				return new RXJsonDataSource(dataString, quertStr, yn_dataSourceUse);
			} else {
				return null;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (RXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public RXJsonDataSource(String json_str, String selectExpression) throws Exception {
		this(new ByteArrayInputStream(json_str.getBytes("utf-8")), selectExpression, false);
	}

	public RXJsonDataSource(String json_str, String charset, String selectExpression) throws Exception {
		this(new ByteArrayInputStream(json_str.getBytes(charset)), selectExpression, false);
	}

	public RXJsonDataSource(String json_str, String selectExpression, boolean savejsonnode) throws Exception {
		this(new ByteArrayInputStream(json_str.getBytes("utf-8")), selectExpression, savejsonnode);
	}

	public RXJsonDataSource(InputStream stream) throws RXException {
		this(stream, null, false);
	}

	public RXJsonDataSource(InputStream jsonStream, String selectExpression, boolean savejsonnode) throws RXException {
		try {
			this.jsonStream = jsonStream;
			this.saveJsonNode = savejsonnode;
			this.mapper = new ObjectMapper();

			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

			this.jsonTree = mapper.readTree(jsonStream);
			this.selectExpression = selectExpression;

			moveFirst();
		} catch (JsonProcessingException e) {
			throw new RXException(e);
		} catch (IOException e) {
			throw new RXException(e);
		}
	}

	public RXJsonDataSource(File file) throws FileNotFoundException, RXException {
		this(file, null);
	}

	public RXJsonDataSource(File file, String selectExpression) throws FileNotFoundException, RXException {
		this(new FileInputStream(file), selectExpression, false);

		toClose = true;
	}

	/**
	 * Creates a data source instance that reads JSON data from a given location
	 * 
	 * @param rxReportsContext
	 *            the RXReportsContext
	 * @param location
	 *            a String representing JSON data source
	 * @param selectExpression
	 *            a String representing the select expression
	 */
	/*
	 * public RXJsonDataSource(RXReportsContext rXReportsContext, String
	 * location, String selectExpression) throws RXException {
	 * this(RepositoryUtil
	 * .getInstance(rXReportsContext).getInputStreamFromLocation(location),
	 * selectExpression);
	 * 
	 * toClose = true; }
	 */
	/**
	 * @see #RXJsonDataSource(RXReportsContext, String, String)
	 */
	/*
	 * public RXJsonDataSource(String location, String selectExpression) throws
	 * RXException { this(DefaultRXReportsContext.getInstance(), location,
	 * selectExpression); }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cabsoft.rx.engine.RXRewindableDataSource#moveFirst()
	 */
	public void moveFirst() throws RXException {
		if (jsonTree == null || jsonTree.isMissingNode()) {
			throw new RXException("No JSON data to operate on!");
		}

		currentJsonNode = null;
		dsjsonNode = getJsonData(jsonTree, selectExpression);
		if (dsjsonNode != null && dsjsonNode.isObject()) {
			final List<JsonNode> list = new ArrayList<JsonNode>();
			list.add(dsjsonNode);
			jsonNodesIterator = new Iterator<JsonNode>() {
				private int count = -1;

				public void remove() {
					list.remove(count);
				}

				public JsonNode next() {
					count++;
					return list.get(count);
				}

				public boolean hasNext() {
					return count < list.size() - 1;
				}
			};
		} else if (dsjsonNode != null && dsjsonNode.isArray()) {
			jsonNodesIterator = dsjsonNode.elements();
		}
		if (!this.saveJsonNode)
			dsjsonNode = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cabsoft.rx.engine.RXDataSource#next()
	 */
	public boolean next() {
		if (jsonNodesIterator == null || !jsonNodesIterator.hasNext()) {
			return false;
		}
		currentJsonNode = jsonNodesIterator.next();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cabsoft.rx.engine.RXDataSource#getFieldValue(com.cabsoft.rx.engine
	 * .RXField)
	 */
	public Object getFieldValue(RXField rxField) throws RXException {
		if (currentJsonNode == null) {
			return null;
		}
		String expression = rxField.getDescription();
		if (expression == null || expression.length() == 0) {
			expression = rxField.getName();
			if (expression == null || expression.length() == 0) {
				return null;
			}
		}
		Object value = null;

		Class<?> valueClass = rxField.getValueClass();
		JsonNode selectedObject = getJsonData(currentJsonNode, expression);

		if (Object.class != valueClass) {
			if (selectedObject != null) {
				try {
					if (valueClass.equals(String.class)) {
						value = selectedObject.asText();

					} else if (valueClass.equals(Boolean.class)) {
						value = selectedObject.booleanValue();

					} else if (Number.class.isAssignableFrom(valueClass)) {
						value = convertStringValue(selectedObject.asText(), valueClass);

					} else if (Date.class.isAssignableFrom(valueClass)) {
						value = convertStringValue(selectedObject.asText(), valueClass);

					} else {
						throw new RXException("Field '" + rxField.getName() + "' is of class '" + valueClass.getName()
								+ "' and cannot be converted");
					}
				} catch (Exception e) {
					throw new RXException("Unable to get value for field '" + rxField.getName() + "' of class '"
							+ valueClass.getName() + "'", e);
				}
			}
		} else {
			value = selectedObject;
		}

		return value;
	}

	public Object getDataValue(String expression) throws RXException {
		return getDataValue(JsonNode.class, expression);
	}

	public Object getDataValue(Class<?> valueClass, String expression) throws RXException {
		if (jsonTree == null) {
			return null;
		}

		if (expression == null || expression.length() == 0) {
			return null;
		}
		Object value = null;

		JsonNode selectedObject = getJsonData(jsonTree, expression);

		if (Object.class != valueClass && valueClass != null) {
			if (selectedObject != null) {
				try {
					if (valueClass.equals(String.class)) {
						value = selectedObject.asText();

					} else if (valueClass.equals(Boolean.class)) {
						value = selectedObject.booleanValue();

					} else if (Number.class.isAssignableFrom(valueClass)) {
						value = convertStringValue(selectedObject.asText(), valueClass);

					} else if (Date.class.isAssignableFrom(valueClass)) {
						value = convertStringValue(selectedObject.asText(), valueClass);

					} else {
						value = selectedObject.toString();
					}
				} catch (Exception e) {
					throw new RXException("getDataValue", e);
				}
			}
		} else {
			value = selectedObject.toString();
		}

		return value;
	}

	public String getStringValue(String expression) throws RXException, JsonProcessingException {
		return getStringValue(jsonTree, expression);
	}

	public String getStringValue(String strjsonnode, String expression) throws RXException, JsonProcessingException,
			IOException {
		return getStringValue(mapper.readTree(strjsonnode), expression);
	}

	public String getStringValue(JsonNode jsonnode, String expression) throws RXException, JsonProcessingException {
		if (jsonnode == null) {
			return null;
		}

		if (expression == null || expression.length() == 0) {
			return null;
		}
		String value = null;

		JsonNode selectedObject = getJsonData(jsonnode, expression);

		if (selectedObject != null) {
			try {
				value = selectedObject.asText();
			} catch (Exception e) {
				throw new RXException("getStringValue Unable to get value for field '", e);
			}
		}

		return value;
	}

	/**
	 * Extracts the JSON nodes based on the query expression
	 * 
	 * @param rootNode
	 * @param jsonExpression
	 * @throws RXException
	 */
	protected JsonNode getJsonData(JsonNode rootNode, String jsonExpression) throws RXException {
		if (jsonExpression == null || jsonExpression.length() == 0) {
			return rootNode;
		}
		JsonNode tempNode = rootNode;
		StringTokenizer tokenizer = new StringTokenizer(jsonExpression, PROPERTY_SEPARATOR);

		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			int currentTokenLength = currentToken.length();
			int indexOfLeftSquareBracket = currentToken.indexOf(ARRAY_LEFT);

			// got Left Square Bracket - LSB
			if (indexOfLeftSquareBracket != -1) {
				// a Right Square Bracket must be the last character in the
				// current token
				if (currentToken.lastIndexOf(ARRAY_RIGHT) != (currentTokenLength - 1)) {
					throw new RXException("Invalid expression: " + jsonExpression + "; current token " + currentToken
							+ " not ended properly");
				}

				// LSB not first character
				if (indexOfLeftSquareBracket > 0) {
					// extract nodes at property
					String property = currentToken.substring(0, indexOfLeftSquareBracket);
					tempNode = goDownPathWithAttribute(tempNode, property);

					String arrayOperators = currentToken.substring(indexOfLeftSquareBracket);
					StringTokenizer arrayOpsTokenizer = new StringTokenizer(arrayOperators, ARRAY_RIGHT);
					while (arrayOpsTokenizer.hasMoreTokens()) {
						if (!tempNode.isMissingNode() && tempNode.isArray()) {
							String currentArrayOperator = arrayOpsTokenizer.nextToken();
							tempNode = tempNode.path(Integer.parseInt(currentArrayOperator.substring(1)));
						}
					}
				} else { // LSB first character
					String arrayOperators = currentToken.substring(indexOfLeftSquareBracket);
					StringTokenizer arrayOpsTokenizer = new StringTokenizer(arrayOperators, ARRAY_RIGHT);
					while (arrayOpsTokenizer.hasMoreTokens()) {
						if (!tempNode.isMissingNode() && tempNode.isArray()) {
							String currentArrayOperator = arrayOpsTokenizer.nextToken();
							tempNode = tempNode.path(Integer.parseInt(currentArrayOperator.substring(1)));
						}
					}
				}
			} else {
				tempNode = goDownPathWithAttribute(tempNode, currentToken);
			}
		}

		return tempNode;
	}

	/**
	 * Extracts the JSON nodes that match the attribute expression
	 * 
	 * @param rootNode
	 * @param pathWithAttributeExpression
	 *            : e.g. Orders(CustomerId == HILAA)
	 * @throws RXException
	 */
	protected JsonNode goDownPathWithAttribute(JsonNode rootNode, String pathWithAttributeExpression)
			throws RXException {
		// check if path has attribute selector
		int indexOfLeftRoundBracket = pathWithAttributeExpression.indexOf(ATTRIBUTE_LEFT);
		if (indexOfLeftRoundBracket != -1) {

			// a Right Round Bracket must be the last character in the current
			// pathWithAttribute
			if (pathWithAttributeExpression.indexOf(ATTRIBUTE_RIGHT) != (pathWithAttributeExpression.length() - 1)) {
				throw new RXException("Invalid attribute selection expression: " + pathWithAttributeExpression);
			}

			if (rootNode != null && !rootNode.isMissingNode()) {

				String path = pathWithAttributeExpression.substring(0, indexOfLeftRoundBracket);

				// an expression in a form like: attribute==value
				String attributeExpression = pathWithAttributeExpression.substring(indexOfLeftRoundBracket + 1,
						pathWithAttributeExpression.length() - 1);

				JsonNode result = null;
				if (rootNode.isObject()) {
					// select only those nodes for which the attribute
					// expression applies
					if (!rootNode.path(path).isMissingNode()) {
						if (rootNode.path(path).isObject()) {
							if (isValidExpression(rootNode.path(path), attributeExpression)) {
								result = rootNode.path(path);
							}
						} else if (rootNode.path(path).isArray()) {
							result = mapper.createArrayNode();
							for (JsonNode node : rootNode.path(path)) {
								if (isValidExpression(node, attributeExpression)) {
									((ArrayNode) result).add(node);
								}
							}
						}
					}
				} else if (rootNode.isArray()) {
					result = mapper.createArrayNode();
					for (JsonNode node : rootNode) {
						JsonNode deeperNode = node.path(path);
						if (!deeperNode.isMissingNode()) {
							if (deeperNode.isArray()) {
								for (JsonNode arrayNode : deeperNode) {
									if (isValidExpression(arrayNode, attributeExpression)) {
										((ArrayNode) result).add(arrayNode);
									}
								}
							} else if (isValidExpression(deeperNode, attributeExpression)) {
								((ArrayNode) result).add(deeperNode);
							}
						}
					}
				}
				return result;
			}

		} else { // path has no attribute selectors
			return goDownPath(rootNode, pathWithAttributeExpression);
		}
		return rootNode;
	}

	/**
	 * Extracts the JSON nodes under the simple path
	 * 
	 * @param rootNode
	 * @param simplePath
	 *            - a simple field name, with no selection by attribute
	 */
	protected JsonNode goDownPath(JsonNode rootNode, String simplePath) {
		if (rootNode != null && !rootNode.isMissingNode()) {
			JsonNode result = null;
			if (rootNode.isObject()) {
				result = rootNode.path(simplePath);
			} else if (rootNode.isArray()) {
				result = mapper.createArrayNode();
				for (JsonNode node : rootNode) {
					JsonNode deeperNode = node.path(simplePath);
					if (!deeperNode.isMissingNode()) {
						if (deeperNode.isArray()) {
							for (JsonNode arrayNode : deeperNode) {
								((ArrayNode) result).add(arrayNode);
							}
						} else {
							((ArrayNode) result).add(deeperNode);
						}
					}
				}
			}
			return result;
		}
		return rootNode;
	}

	/**
	 * Validates an attribute expression on a JsonNode
	 * 
	 * @param operand
	 * @param attributeExpression
	 * @throws RXException
	 */
	protected boolean isValidExpression(JsonNode operand, String attributeExpression) throws RXException {
		return JsonUtil.evaluateJsonExpression(operand, attributeExpression);
	}

	/**
	 * Creates a sub data source using the current node as the base for its
	 * input stream.
	 * 
	 * @return the JSON sub data source
	 * @throws RXException
	 */
	public RXJsonDataSource subDataSource() throws RXException {
		return subDataSource(null);
	}

	/**
	 * Creates a sub data source using the current node as the base for its
	 * input stream. An additional expression specifies the select criteria that
	 * will be applied to the JSON tree node.
	 * 
	 * @param selectExpression
	 * @return the JSON sub data source
	 * @throws RXException
	 */
	public RXJsonDataSource subDataSource(String selectExpression) throws RXException {
		if (currentJsonNode == null) {
			throw new RXException("No node available. Iterate or rewind the data source.");
		}

		try {
			return new RXJsonDataSource(new ByteArrayInputStream(currentJsonNode.toString().getBytes("UTF-8")),
					selectExpression, false);
		} catch (UnsupportedEncodingException e) {
			throw new RXRuntimeException(e);
		}
	}

	public RXJsonDataSource dataSource() throws Exception {
		return dataSource(null, null);
	}

	public RXJsonDataSource dataSource(String selectExpression) throws Exception {
		return dataSource(selectExpression, null);
	}

	/**
	 * Creates a sub data source using the current node as the base for its
	 * input stream. An additional expression specifies the select criteria that
	 * will be applied to the JSON tree node.
	 * 
	 * @param selectExpression
	 * @return the JSON sub data source
	 * @throws RXException
	 */
	public RXJsonDataSource dataSource(String selectExpression, String nodeName) throws RXException {
		if (dsjsonNode == null) {
			log.debug("dataSource, dsjsonNode == null ");
			return null;
			// throw new
			// RXException("No node available. Iterate or rewind the data source.");
		}

		try {
			if (nodeName == null) {
				return new RXJsonDataSource(new ByteArrayInputStream(dsjsonNode.toString().getBytes("UTF-8")),
						selectExpression, true);
			} else {
				String[] astr = nodeName.split("\\.");
				String strS = "{\"";
				String strE = "}";
				for (int i = 0, maxi = astr.length - 1; i < maxi; i++) {
					strS += (astr[i] + "\":{\"");
					strE += "}";
				}
				strS += (astr[astr.length - 1] + "\":");

				return new RXJsonDataSource(new ByteArrayInputStream(
						(strS + dsjsonNode.toString() + strE).getBytes("UTF-8")), selectExpression, true);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RXRuntimeException(e);
		}
	}

	private static String detector(byte[] b) {
		try {
			UniversalDetector detector = new UniversalDetector(null);
			detector.handleData(b, 0, b.length);
			detector.dataEnd();
			String charset = detector.getDetectedCharset();
			charset = (charset == null || "".equals(charset)) ? "utf-8" : charset;

			return charset;
		} catch (Exception e) {
			return "utf-8"; // default
		}
	}

	public static void main(String[] args) throws Exception {
		createDatasource();

	}

	public void close() {
		if (toClose) {
			try {
				jsonStream.close();
			} catch (Exception e) {
				// nothing to do
			}
		}
	}

}
