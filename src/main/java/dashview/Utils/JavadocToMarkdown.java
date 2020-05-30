package dashview.Utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// reference: https://regexr.com/
/**
 * Generate Markdown from your Javadoc, PHPDoc or JSDoc comments
 * Language translation from https://github.com/delight-im/Javadoc-to-Markdown
 * @tranlator: Yvan Ross
 * Usage: Create a new instance of <code>JavadocToMarkdown</code> and then call
 * either <code>fromJavadoc()</code>, <code>fromPHPDoc()</code> or
 * <code>fromJSDoc()</code>
 *
 * @constructor
 */
public class JavadocToMarkdown {
	int indentation_level;
	/**
	 * Generates Markdown documentation from code on a more abstract level
	 *
	 * @param {string} code the code that contains doc comments
	 * @param {number} headingsLevel the headings level to use as the base (1-6)
	 * @returns {string} the Markdown documentation
	 */
	public String fromJavadoc(final String code, final int headingsLevel) {
		indentation_level = 0;
		// get all documentation sections from code
		final List<Section> sections = getSections(code);

		// initialize a string buffer
		final List<String> out = new ArrayList<String>();

		// out.add("#".repeat(headingsLevel) + " Documentation");

		for (int i = 0; i < sections.size(); i++) {
			out.add(fromSection(sections.get(i), headingsLevel));
		}

		// return the contents of the string buffer and add a trailing newline

		return String.join("", out) + "\n";
	}

	private void fnAddTagsMarkdown(final Map.Entry<String, String> tag, final ArrayList<Map.Entry<String,ArrayList<String>>> assocBuffer) {
		String[] tokens;
		switch (tag.getKey()) {
			case "@abstract":
				addToBuffer(assocBuffer, "Abstract", tag.getValue());
				break;
			case "@access":
				addToBuffer(assocBuffer, "Access", tag.getValue());
				break;
			case "@author":
				addToBuffer(assocBuffer, "Author", tag.getValue());
				break;
			case "@constructor":
				addToBuffer(assocBuffer, "Constructor", null);
				break;
			case "@copyright":
				addToBuffer(assocBuffer, "Copyright", tag.getValue());
				break;
			case "@deprec":
			case "@deprecated":
				addToBuffer(assocBuffer, "Deprecated", null);
				break;
			case "@example":
				addToBuffer(assocBuffer, "Example", tag.getValue());
				break;
			case "@exception":
			case "@throws":
				tokens = tag.getValue().split("\s+", 2);
				addToBuffer(assocBuffer, "Exceptions", "`" + tokens[0] + "` — " + tokens[1]);
				break;
			case "@exports":
				addToBuffer(assocBuffer, "Exports", tag.getValue());
				break;
			case "@license":
				addToBuffer(assocBuffer, "License", tag.getValue());
				break;
			case "@link":
				addToBuffer(assocBuffer, "Link", tag.getValue());
				break;
			case "@name":
				addToBuffer(assocBuffer, "Alias", tag.getValue());
				break;
			case "@package":
				addToBuffer(assocBuffer, "Package", tag.getValue());
				break;
			case "@param":
				tokens = tag.getValue().split("\s+", 2);
				addToBuffer(assocBuffer, "Parameters", "`" + tokens[0] + "` — " + tokens[1]);
				break;
			case "@private":
				addToBuffer(assocBuffer, "Private", null);
				break;
			case "@return":
			case "@returns":
				addToBuffer(assocBuffer, "Returns", tag.getValue());
				break;
			case "@see":
				addToBuffer(assocBuffer, "See also", tag.getValue());
				break;
			case "@since":
				addToBuffer(assocBuffer, "Since", tag.getValue());
				break;
			case "@static":
				addToBuffer(assocBuffer, "Static", tag.getValue());
				break;
			case "@subpackage":
				addToBuffer(assocBuffer, "Sub-package", tag.getValue());
				break;
			case "@this":
				addToBuffer(assocBuffer, "This", "`" + tag.getValue() + "`");
				break;
			case "@todo":
				addToBuffer(assocBuffer, "To-do", tag.getValue());
				break;
			case "@version":
				addToBuffer(assocBuffer, "Version", tag.getValue());
				break;
			default:
				break;
		}
	}

	/**
	 * Generates Markdown documentation from a given section
	 *
	 * The function processes units of documentation, a line of code with
	 * accompanying doc comment
	 *
	 * @param {object} section the section that consists of code line and doc
	 *                 comment
	 * @param {number} headingsLevel the headings level to use as the base (1-6)
	 * @returns {string} the Markdown documentation
	 */
	private String fromSection(final Section section, final int headingsLevel) {
		String description;
		final List<String> out = new ArrayList<String>();

		// first get the field that we want to describe
		final String field = getFieldDeclaration(section.line);

		// if there is no field to describe
		if (field.compareTo("") == 0) {
			// do not return any documentation
			return "";
		}

		out.add("\n\n");
		
		out.add(" ".repeat(headingsLevel +  indentation_level) +  field );
		indentation_level = 1;

		 // split the doc comment into main description and tag section
		//  String docCommentParts = section.doc.split("^(?:\t| )*?\\*(?:\t| )*?(?=@)",Pattern.MULTILINE);
		 String[] docCommentParts = section.doc.split("@",2);

		 // get the main description (which may be an empty string)
		 String rawMainDescription = docCommentParts[0]; //.shift();
		 // get the tag section (which may be an empty array)
		 String rawTags = docCommentParts.length==2 ? "* @"+docCommentParts[1] : "";

		description = getDocDescription(rawMainDescription.toString()); // rawMainDescription.toString());
		if (description.length() > 0) {
			out.add("\n\n");
			out.add(description);
		}

		final List<Map.Entry<String, String>> tags = getDocTags(rawTags.toString()); // rawTags);
		if (tags.size() > 0) {
			out.add("\n");
		}

			final ArrayList<Map.Entry<String,ArrayList<String>>> assocBuffer = new ArrayList<Map.Entry<String,ArrayList<String>>>();
			for (int t = 0; t < tags.size(); t++) {
				fnAddTagsMarkdown(tags.get(t), assocBuffer);
			}


			for (final Map.Entry<String,ArrayList<String>> p : assocBuffer) {
				 out.add(fromTagGroup(p.getKey(),p.getValue()));
			}

		// return the contents of the string buffer
		return String.join("", out);
	}

	private String fromTagGroup(final String name, final List<String> entries) {
		final List<String> out = new ArrayList<String>();

		out.add("\n");
		if (entries.size() == 1) {
			if(entries.get(0) == null)
				out.add(" * **" + name + "**");
			else
				out.add(" * **" + name + ":** " + entries.get(0));

		} else {
			out.add(" * **" + name + ":**");
				for (int i = 0; i < entries.size(); i++) {
					out.add("\n");
					out.add("   * " + entries.get(i));
				}
		}

		// return the contents of the string buffer
		return String.join("", out);
	}

	private List<Section> getSections(final String code) {
		String fieldDeclaration;
		String docLine;

		final List<Section> sections = new ArrayList<Section>();

		final Pattern pattern = Pattern.compile("/\\*\\*([^$]*?)\\*/([^{;/]+)");
		final Matcher m = pattern.matcher(code);

		while (m.find()) {
			docLine = m.group(1);
			fieldDeclaration = m.group(2).replace("\n", "").replaceAll("\\s+", " ");

			// if the source code line is an import statement
			// do not add it to the documentaion
			final Pattern importPattern = Pattern.compile("/^import\s+/");
			final Matcher matchImport = importPattern.matcher(fieldDeclaration);
			if (matchImport.matches()) {
				continue;
			}

			// if this is a single line comment
			// prepend an asterisk to achieve the normal line structure
			if (docLine.indexOf("*") == -1) {
				docLine = "*" + docLine;
			}

			// interpret empty lines as if they contained a p-tag
			docLine = docLine.replace("/\\*[ ]*$/gm", "* <p>");

			sections.add(new Section(fieldDeclaration, docLine));
		}
		return sections;
	}

	private String getFieldDeclaration(final String line) {
		final Pattern regex = Pattern.compile("^([^{;]+)(.*)");
		final Matcher m = regex.matcher(line);

		while (m.find()) {
			return cleanSingleLine(m.group(1));
		}

		return "";
	}

	private String getDocDescription(final String docLines) {
		final Pattern regex = Pattern.compile("^(\t| )*?\\*(\t| )+(.*?)$", Pattern.MULTILINE);

		final Matcher m = regex.matcher(docLines);
		final List<String> out = new ArrayList<String>();

		while (m.find()) {
			out.add(cleanLine(m.group(3)));
		}
		//return cleanLine(String.join("\n", out).replaceAll("<(/)?p>", "\n\n"));
		String result = String.join(" ", out);
		result = result.replaceAll("<(/)?p>", "\n\n");
		result = cleanLine(result);
		return (result);
	}

  private List<Map.Entry<String, String>> getDocTags(final String docLines) {
		// extract @param (param)

		final Pattern p = Pattern.compile("^(?:[ |\t|*])*(@[a-zA-Z]+)(?:[\\s\\S])(.*$)", Pattern.MULTILINE);

		final List<Map.Entry<String, String>> out = new ArrayList<Map.Entry<String, String>>();

		// for (var i = 0; i < docLines.length; i++) {
		final Matcher m = p.matcher(docLines);

		// TODO process multilines
		while (m.find()) {
			if (m.groupCount() == 2) {
				final Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(m.group(1),
						m.group(2).replaceAll("\\s+", " ").trim());
				out.add(entry);
			}

			// if ( m[1] instanceof String && m[1] != null) {
			// if ( m[2] instanceof String && m[2] != null) {
			// // trim leading and trailing space in the tag value
			// m[2] = m[2].trim();
			// // format multi-line tag values correctly
			// Pattern multiline = Pattern.compile("/[\r\n]{1,2}(?:\t| )*?\\*(?:\t| )*/");
			// m[2] = m[2].split(multiline).join("\n\n ");

			// // add the key and value for this tag to the output
			// Map<String,String> map = new HashMap<String,String>();
			// map.put(cleanSingleLine(m[1], m[2]));
			// out.add(map);
			// // out.add({ "key": , "value": });
			// }
			// }
			// }
		}

		return out;
	}

	private String cleanLine(String line) {
		// trim leading and trailing spaces
		line = line.trim();

		// clear spaces before and after line breaks and tabs
		line = line.replaceAll(" *([\n\r\t])","$1").replaceAll("([\n\r\t]) *","$1");

		// make consecutive spaces one
		//line = line.replaceAll("\\s+", " ");

		return line;
	}

	private String cleanSingleLine(String line) {
		// perform normal line cleaning
		line = cleanLine(line);

		// replace line breaks and tabs with spaces
		line = line.replace("/(\n|\r|\t)/g", " ");

		return line;
	}

	private void addToBuffer(final ArrayList<Map.Entry<String, ArrayList<String>>> buffer, final String key, final String value) {
	
		Iterator<Map.Entry<String,ArrayList<String>>> iterator = buffer.iterator();
	
		boolean inserted = false;
		while(iterator.hasNext()){
			Map.Entry<String,ArrayList<String>> tag = iterator.next();
			if(tag.getKey() == key){
				tag.getValue().add(value);
				inserted = true;
			}
		}
		if(!inserted){
				ArrayList<String> values = new ArrayList<String>();
				values.add(value);
				Map.Entry<String,ArrayList<String>> tag = new AbstractMap.SimpleEntry<String,ArrayList<String>>(key,values);
				buffer.add(tag);
		}

			
		// if (buffer.containsKey(key)) {
		// 	buffer.get(key).add(value);
		// } else {
		// 	final List<String> list = new ArrayList<String>();
		// 	list.add(value);
		// 	buffer.put(key, list);
		// }
	}

}