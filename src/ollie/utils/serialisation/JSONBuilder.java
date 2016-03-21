package ollie.utils.serialisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

import ollie.utils.Strings;

/**
 * 
 * A simple builder class for building JSON strings.
 * 
 * @author Ollie
 *
 */
public class JSONBuilder {

	private JSONObject root;
	private JSONObject currentObject;
	private int openObjects;
	private String sd;
	
	private JSONBuilder(char stringDelim) {
		root = new JSONObject(null);
		currentObject = root;
		openObjects = 0;
		if (stringDelim == '"') {
			sd = "\"";
		} else {
			sd = String.valueOf(stringDelim); 
		}
	}
	
	public static JSONBuilder newSingleQuoteJSONBuilder() {
		return new JSONBuilder('\'');
	}
	
	public static JSONBuilder newDoubleQuoteJSONBuilder() {
		return new JSONBuilder('"');
	}
	
	public JSONBuilder beginObject(String name) {
		if (Strings.isBlank(name)) {
			throw new NullPointerException("Object must have a name.");
		}
		JSONObject newObj = new JSONObject(name);
		if (currentObject.nodes.contains(newObj)) {
			throw new IllegalArgumentException("Duplicate field name.");
		}
		newObj.parent = currentObject;
		currentObject.nodes.add(newObj);
		currentObject = newObj;
		openObjects ++;
		return this;
	}
	
	public JSONBuilder endObject() {
		if (currentObject != root) {
			openObjects --;
			currentObject = currentObject.parent;
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public JSONBuilder field(String name, Object value) {
		if (Strings.isBlank(name)) {
			throw new NullPointerException("Field must have a name.");
		}
		JSONNode node;
		if (value instanceof Number || value instanceof Boolean) {
			node = new JSONPrimativeField(name, String.valueOf(value));
		} else if (value instanceof Collection) {
			Collection<Object> col = (Collection<Object>) value;
			node = new JSONArrayField(name, col.toArray(new Object[col.size()]));
		} else if (value.getClass().isArray()) {
			node = new JSONArrayField(name, toObjectArray(value));
		} else if (value instanceof Map) {
			JSONObject obj = new JSONObject(name);
			Map<String, Object> map = (Map<String, Object>) value;
			for (Entry<String, Object> entry : map.entrySet()) {
				obj.nodes.add(new JSONStringField(entry.getKey(), String.valueOf(entry.getValue())));
			}
			node = obj;
		} else {
			node = new JSONStringField(name, String.valueOf(value));	
		}
		if (currentObject.nodes.contains(node)) {
			throw new IllegalArgumentException("Duplicate field name.");
		}
		currentObject.nodes.add(node);
		return this;
	}
	
	@Override
	public String toString() {
		if (openObjects > 0) {
			throw new IllegalStateException("One or more objects not closed.");
		}
		return root.toString();
	}
	
	public String prettyPrint() {
		if (openObjects > 0) {
			throw new IllegalStateException("One or more objects not closed.");
		}
		return root.pretty(0);
	}
	
	private class JSONNode {
		String name;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			return prime * result + ((name == null) ? 0 : name.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof JSONNode)) {
				return false;
			}
			JSONNode other = (JSONNode) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}
		
		public String pretty(int hierarchy) {
			return toString();
		}

	}
	
	private class JSONObject extends JSONNode {
		JSONObject parent;
		List<JSONNode> nodes = new ArrayList<>();
		
		JSONObject(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			StringBuilder result;
			if (Strings.isNotBlank(name)) {
				result = new StringBuilder("'"+name+"':{");
			} else {
				result = new StringBuilder("{");
			}
			for (Iterator<JSONNode> itr = nodes.iterator(); itr.hasNext();) {
				result.append(itr.next().toString());
				if (itr.hasNext()) {
					result.append(",");
				}
			}
			result.append("}");
			return result.toString();
		}
		
		@Override
		public String pretty(int hierarchyLevel) {
			StringBuilder result;
			if (Strings.isNotBlank(name)) {
				result = new StringBuilder();
				for (int i=0; i<hierarchyLevel; i++) {
					result.append("\t");
				}
				result.append(sd+name+sd+":{\n");
			} else {
				result = new StringBuilder("{\n");
			}
			
			for (Iterator<JSONNode> itr = nodes.iterator(); itr.hasNext();) {
				JSONNode node = itr.next();
				if (node instanceof JSONObject) {
					// increment the hierarchy by one for the object so that it gets indented.
					result.append(node.pretty(hierarchyLevel+1));
				} else {
					// add one to the hierarchy so that we indent all the fields of the object.
					for (int i=0; i<hierarchyLevel+1; i++) {
						result.append("\t");
					}
					result.append(node.pretty(hierarchyLevel));
				}
				if (itr.hasNext()) {
					result.append(",");
				}
				result.append("\n");
			}
			for (int i=0; i<hierarchyLevel; i++) {
				result.append("\t");
			}
			result.append("}");
			return result.toString();
		}

	}
	
	private class JSONStringField extends JSONNode {
		String value;
		
		JSONStringField(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return sd+name+sd+":"+sd+value+sd;
		}
		
	}
	
	private class JSONPrimativeField extends JSONNode {
		String value;
		
		JSONPrimativeField(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return sd+name+sd+":"+value;
		}
		
	}
	
	private class JSONArrayField extends JSONNode {
		Object[] value;
		
		JSONArrayField(String name, Object[] value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String toString() {
			StringBuilder array = new StringBuilder("[");
			if (value != null) {
				for (int i=0; i<value.length; i++) {
					Object val = value[i];
					if (val instanceof Number || val instanceof Boolean) {
						array.append(val);
					} else {
						array.append(sd+val+sd);						
					}
					if (i<value.length-1) {
						array.append(",");
					}
				}
			}
			array.append("]");
			return sd+name+sd+":"+array.toString();
		}
	}
	
	private Object[] toObjectArray(Object obj) {
		Object[] result = null;
		if (obj.getClass().isArray()) {
			if (obj instanceof int[]) {
				Integer[] arr = ArrayUtils.toObject((int[]) obj);
				result = arr;
			} else if (obj instanceof long[]) {
				Long[] arr = ArrayUtils.toObject((long[]) obj);
				result = arr;
			} else if (obj instanceof double[]) {
				Double[] arr = ArrayUtils.toObject((double[]) obj);
				result = arr;
			} else if (obj instanceof float[]) {
				Float[] arr = ArrayUtils.toObject((float[]) obj);
				result = arr;
			} else if (obj instanceof byte[]) {
				Byte[] arr = ArrayUtils.toObject((byte[]) obj);
				result = arr;
			} else if (obj instanceof short[]) {
				Short[] arr = ArrayUtils.toObject((short[]) obj);
				result = arr;
			} else if (obj instanceof char[]) {
				Character[] arr = ArrayUtils.toObject((char[]) obj);
				result = arr;
			} else if (obj instanceof boolean[]) {
				Boolean[] arr = ArrayUtils.toObject((boolean[]) obj);
				result = arr;
			} else {
				result = (Object[]) obj;
			}
		}
		return result;
	}
}
