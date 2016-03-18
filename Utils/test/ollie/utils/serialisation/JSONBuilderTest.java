package ollie.utils.serialisation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JSONBuilderTest {

	@Test
	public void simpleJSON() {
		Assert.assertEquals("{'stringTest':'string'}", JSONBuilder.newSingleQuoteJSONBuilder().field("stringTest", "string").toString());
		Assert.assertEquals("{'booleanTest':true}", JSONBuilder.newSingleQuoteJSONBuilder().field("booleanTest", true).toString());
		Assert.assertEquals("{'primNumberTest':666}", JSONBuilder.newSingleQuoteJSONBuilder().field("primNumberTest", 666L).toString());
		Assert.assertEquals("{'objNumberTest':66.7}", JSONBuilder.newSingleQuoteJSONBuilder().field("objNumberTest", new Double(66.7)).toString());
	}

	@Test
	public void simpleJSONMultiFields() {
		Assert.assertEquals("{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void simpleJSONMultiFieldsWithError() {
		Assert.assertEquals("{'stringTest':'string','stringTest':true,'primNumberTest':666,'objNumberTest':66.7}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.field("stringTest", "string")
				.field("stringTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.toString());
	}
	
	@Test(expected=IllegalStateException.class)
	public void complexJSONWithError() {
		Assert.assertEquals("{'inner':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.beginObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.toString());
	}
	
	@Test
	public void complexJSON() {
		Assert.assertEquals("{'inner':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.beginObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void complexJSONNestedWithError() {
		Assert.assertEquals("{'inner':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7},'inner':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.beginObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.beginObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.toString());
	}
	
	@Test
	public void complexJSONNested() {
		Assert.assertEquals("{'inner':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7},'inner2':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.beginObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.beginObject("inner2")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.toString());
	}
	
	@Test
	public void arrayTest() {
		Assert.assertEquals("{'array':['one','two','three']}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.field("array", new String[]{"one","two","three"})
				.toString());
	}
	
	@Test
	public void collectionTest() {
		List<String> l = new ArrayList<>();
		l.add("one");
		l.add("two");
		l.add("three");
		Assert.assertEquals("{'collection':['one','two','three']}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.field("collection", l)
				.toString());
	}
	
	@Test
	public void mapTest() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("obj1", "one");
		map.put("obj2", "two");
		Assert.assertEquals("{'map':{'obj1':'one','obj2':'two'}}", 
				JSONBuilder.newSingleQuoteJSONBuilder()
				.field("map", map)
				.toString());
	}
	
	@Test
	public void print() {
		System.out.print(JSONBuilder.newDoubleQuoteJSONBuilder()
		.beginObject("inner")
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.beginObject("innerInner")
		.beginObject("innerInnerInner")
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.endObject()
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.beginObject("innerInnerInner2")
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.endObject()
		.endObject()
		.endObject()
		.beginObject("inner2")
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.endObject()
		.prettyPrint());
	}
}

