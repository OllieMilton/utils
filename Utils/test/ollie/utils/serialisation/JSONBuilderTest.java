package ollie.utils.serialisation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JSONBuilderTest {

	@Test
	public void simpleJSON() {
		Assert.assertEquals("{'stringTest':'string'}", JSONBuilder.newJSONBuilder('\'').field("stringTest", "string").toString());
		Assert.assertEquals("{'booleanTest':true}", JSONBuilder.newJSONBuilder('\'').field("booleanTest", true).toString());
		Assert.assertEquals("{'primNumberTest':666}", JSONBuilder.newJSONBuilder('\'').field("primNumberTest", 666L).toString());
		Assert.assertEquals("{'objNumberTest':66.7}", JSONBuilder.newJSONBuilder('\'').field("objNumberTest", new Double(66.7)).toString());
	}

	@Test
	public void simpleJSONMultiFields() {
		Assert.assertEquals("{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}", 
				JSONBuilder.newJSONBuilder('\'')
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.toString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void simpleJSONMultiFieldsWithError() {
		Assert.assertEquals("{'stringTest':'string','stringTest':true,'primNumberTest':666,'objNumberTest':66.7}", 
				JSONBuilder.newJSONBuilder('\'')
				.field("stringTest", "string")
				.field("stringTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.toString());
	}
	
	@Test(expected=IllegalStateException.class)
	public void complexJSONWithError() {
		Assert.assertEquals("{'inner':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}}", 
				JSONBuilder.newJSONBuilder('\'')
				.startObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.toString());
	}
	
	@Test
	public void complexJSON() {
		Assert.assertEquals("{'inner':{'stringTest':'string','booleanTest':true,'primNumberTest':666,'objNumberTest':66.7}}", 
				JSONBuilder.newJSONBuilder('\'')
				.startObject("inner")
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
				JSONBuilder.newJSONBuilder('\'')
				.startObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.startObject("inner")
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
				JSONBuilder.newJSONBuilder('\'')
				.startObject("inner")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.startObject("inner2")
				.field("stringTest", "string")
				.field("booleanTest", true)
				.field("primNumberTest", 666L)
				.field("objNumberTest", new Double(66.7))
				.endObject()
				.toString());
	}
	
	@Test
	public void print() {
		System.out.print(JSONBuilder.newJSONBuilder('"')
		.startObject("inner")
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.startObject("innerInner")
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.endObject()
		.endObject()
		.startObject("inner2")
		.field("stringTest", "string")
		.field("booleanTest", true)
		.field("primNumberTest", 666L)
		.field("objNumberTest", new Double(66.7))
		.endObject()
		.prettyPrint());
	}
}

