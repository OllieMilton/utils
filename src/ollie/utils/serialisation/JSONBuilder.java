package ollie.utils.serialisation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ollie.utils.Strings;

public class JSONBuilder {

	private JSONObject root;
	private JSONObject currentObject;
	private int openObjects;
	
	private JSONBuilder() {
		root = new JSONObject(null);
		currentObject = root;
		openObjects = 0;
	}
	
	public static JSONBuilder newJSONBuilder() {
		return new JSONBuilder();
	}
	
	public JSONBuilder startObject(String name) {
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
	
	public JSONBuilder field(String name, Object value) {
		if (Strings.isBlank(name)) {
			throw new NullPointerException("Field must have a name.");
		}
		JSONNode node;
		if (value instanceof Number || value instanceof Boolean) {
			node = new JSONPrimativeField(name, String.valueOf(value));
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

	}
	
	private class JSONStringField extends JSONNode {
		String value;
		
		JSONStringField(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "'"+name+"':'"+value+"'";
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
			return "'"+name+"':"+value;
		}
		
	}
}
