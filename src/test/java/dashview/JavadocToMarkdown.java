package dashview;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// reference: https://regexr.com/
/**
 * Generate Markdown from your Javadoc, PHPDoc or JSDoc comments
 *
 * Usage: Create a new instance of <code>JavadocToMarkdown</code> and then call
 * either <code>fromJavadoc()</code>, <code>fromPHPDoc()</code> or
 * <code>fromJSDoc()</code>
 *
 * @constructor
 */
public class JavadocToMarkdown {

	/**
	 * Generates Markdown documentation from code on a more abstract level
	 *
	 * @param {string} code the code that contains doc comments
	 * @param {number} headingsLevel the headings level to use as the base (1-6)
	 * @returns {string} the Markdown documentation
	 */
	public String fromJavadoc(final String code, final int headingsLevel) {

		// get all documentation sections from code
		final List<Section> sections = getSections(code);

		// initialize a string buffer
		final List<String> out = new ArrayList<String>();

		out.add("#".repeat(headingsLevel) + " Documentation");

		for (int i = 0; i < sections.size(); i++) {
			out.add(fromSection(sections.get(i), headingsLevel));
		}

		// return the contents of the string buffer and add a trailing newline

		return String.join("", out) + "\n";
	}

	

	private void fnAddTagsMarkdown(final Map.Entry<String, String> tag,
			final List<Map.Entry<String, String>> assocBuffer) {
		final List<String> tokens;

		switch (tag.getKey()) {
			case "abstract":
				addToBuffer(assocBuffer, "Abstract", tag.getValue());
				break;
			case "access":
				addToBuffer(assocBuffer, "Access", tag.getValue());
				break;
			case "author":
				addToBuffer(assocBuffer, "Author", tag.getValue());
				break;
			case "constructor":
				addToBuffer(assocBuffer, "Constructor", null);
				break;
			case "copyright":
				addToBuffer(assocBuffer, "Copyright", tag.getValue());
				break;
			case "deprec":
			case "deprecated":
				addToBuffer(assocBuffer, "Deprecated", null);
				break;
			case "example":
				addToBuffer(assocBuffer, "Example", tag.getValue());
				break;
			case "exception":
			case "throws":
				tokens = tokenize(tag.getValue(), "/\s+/g", 2);
				addToBuffer(assocBuffer, "Exceptions", "`" + tokens.get(0) + "` — " + tokens.get(1));
				break;
			case "exports":
				addToBuffer(assocBuffer, "Exports", tag.getValue());
				break;
			case "license":
				addToBuffer(assocBuffer, "License", tag.getValue());
				break;
			case "link":
				addToBuffer(assocBuffer, "Link", tag.getValue());
				break;
			case "name":
				addToBuffer(assocBuffer, "Alias", tag.getValue());
				break;
			case "package":
				addToBuffer(assocBuffer, "Package", tag.getValue());
				break;
			case "param":
				tokens = tokenize(tag.getValue(), "/\s+/g", 2);
				addToBuffer(assocBuffer, "Parameters", "`" + tokens.get(0) + "` — " + tokens.get(1));
				break;
			case "private":
				addToBuffer(assocBuffer, "Private", null);
				break;
			case "return":
			case "returns":
				addToBuffer(assocBuffer, "Returns", tag.getValue());
				break;
			case "see":
				addToBuffer(assocBuffer, "See also", tag.getValue());
				break;
			case "since":
				addToBuffer(assocBuffer, "Since", tag.getValue());
				break;
			case "static":
				addToBuffer(assocBuffer, "Static", tag.getValue());
				break;
			case "subpackage":
				addToBuffer(assocBuffer, "Sub-package", tag.getValue());
				break;
			case "this":
				addToBuffer(assocBuffer, "This", "`" + tag.getValue() + "`");
				break;
			case "todo":
				addToBuffer(assocBuffer, "To-do", tag.getValue());
				break;
			case "version":
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
		String field = getFieldDeclaration(section.line);

		// if there is no field to describe
		if (field == null) {
			// do not return any documentation
			return "";
		}

		out.add("\n\n");
		out.add("#".repeat(headingsLevel + 1) + " `" + field + "`");

		// split the doc comment int"/^(?:\t| )*?\*(?:\t| )*?(?=@)");o main description
		// and tag section

		// final String[] docCommentParts = section.doc.split("^(?:\t| )*?\\*(?:\t| )*?(?=@)");
		// get the main description (which may be an empty string)
		// final String[] rawMainDescription = docCommentParts;// .shift();
		// get the tag section (which may be an empty array)
		// final String[] rawTags = docCommentParts;

		description = getDocDescription(section.doc); //rawMainDescription.toString());
		if (description.length() > 0) {
			out.add("\n\n");
			out.add(description);
		}

		List<Map.Entry<String, String>> tags = getDocTags(section.doc); //rawTags);
		if (tags.size() > 0) {
			out.add("\n");

			List<Map.Entry<String, String>> assocBuffer = new ArrayList();
			for (int t = 0; t < tags.size(); t++) {
				fnAddTagsMarkdown(tags.get(t), assocBuffer);
			}

			// for (final Map.Entry<String,String> p : assocBuffer) {
			// if (assocBuffer.hasOwnProperty(p)) {
			// out.add(fromTagGroup(p, assocBuffer[p]));
			// }
			// }
		}

		// return the contents of the string buffer
		return String.join("", out);
	}

	private String fromTagGroup(final String name, final String[] entries) {
		List<String> out = new ArrayList<String>();

		out.add("\n");
		if (entries.length == 1 && entries[0] == null) {
			out.add(" * **" + name + "**");
		} else {
			out.add(" * **" + name + ":**");
			if (entries.length > 1) {
				for (int i = 0; i < entries.length; i++) {
					out.add("\n");
					out.add("   * " + entries[i]);
				}
			} else if (entries.length == 1) {
				out.add(" " + entries[0]);
			}
		}

		// return the contents of the string buffer
		return String.join("", out);
	}

	private List<Section> getSections(final String code) {
		List<Section> sections = new ArrayList<Section>();
		final List<String> out = new ArrayList<String>();

		final Pattern pattern = Pattern.compile("/\\*\\*([^$]*?)\\*/([^{;/]+)");
		Matcher m = pattern.matcher(code);

		while (m.find()) {
			
				System.out.println("---------------------------");
				System.out.println(m.group());
					String fieldDeclaration = m.group().trim();				
					final Pattern pattern2 = Pattern.compile("/^import\s+/");
					final Matcher m2 = pattern2.matcher(fieldDeclaration);
					// if the source code line is an import statement
					if (m2.matches()) {
						// ignore this piece
						continue;
					}

					String docLine = m.group();
					// if this is a single line comment
					if (docLine.indexOf("*") == -1) {
						// prepend an asterisk to achieve the normal line structure
						docLine = "*" + docLine;
					}

					// interpret empty lines as if they contained a p-tag
					docLine = docLine.replace("/\\*[ ]*$/gm", "* <p>");

					sections.add(new Section(fieldDeclaration, docLine));
				}
		return sections;
	}

	private String getFieldDeclaration(final String line) {
		Pattern regex = Pattern.compile("/^([^\\{;]+)(.*?)$/gm");
		Matcher m = regex.matcher(line);

		while (m.find()) {
			// if (m.index == regex.lastIndex) {
			// regex.lastIndex++;
			// }

			// if ( m[1] instanceof String && m[1] != null) {
			return cleanSingleLine(m.group());
			// }
		}

		return "";
	}

// 	private String replaceHTMLWithMarkdown(final String html) {
// 	return html.replace("/<\s*?code\s*?>(.*?)<\s*?\/\s*?code\s*?>/g", "`$1`");
//  }

	private String getDocDescription(final String docLines) {
		final Pattern regex = Pattern.compile("/^(\t| )*?\\*(\t| )+(.*?)$/gm");

		Matcher m = regex.matcher(docLines);
		final List<String> out = new ArrayList<String>();

		while (m.find()) {
			String result = cleanLine(m.group());
		//  result = replaceHTMLWithMarkdown(result);
			out.add(result);
			System.out.println(result);
		}
		return cleanLine(String.join(" ", out).replace("/<(/)?p>/gi", "\n\n"));
	}

	List<Map.Entry<String,String>> getDocTags(final String docLines) {
	// extract @param (param)
	
		final Pattern p = Pattern.compile("^(?:[ |\t|*])*(@[a-zA-Z]+)(?:[\\s\\S])(.*$)");
		
		final List<Map.Entry<String,String>> out = new ArrayList<Map.Entry<String,String>>();

		// for (var i = 0; i < docLines.length; i++) {
			Matcher m = p.matcher(docLines);

			if (m.find()) {
				if (m.groupCount() == 2) {
					Map.Entry<String,String> entry = new AbstractMap.SimpleEntry<String,String>(m.group(1),m.group(2));
					 out.add(entry);
					 System.out.println(m.group(1) + "=>" + m.group(2));
				}
				
				
				// if ( m[1] instanceof String && m[1] != null) {
				// 	if ( m[2] instanceof String && m[2] != null) {
				// 		// trim leading and trailing space in the tag value
				// 		m[2] = m[2].trim();
				// 		// format multi-line tag values correctly
				// 		Pattern multiline = Pattern.compile("/[\r\n]{1,2}(?:\t| )*?\\*(?:\t| )*/");
				// 		m[2] = m[2].split(multiline).join("\n\n     ");

				// 		// add the key and value for this tag to the output
				// 		Map<String,String> map = new HashMap<String,String>();
				// 		map.put(cleanSingleLine(m[1], m[2]));
				// 		out.add(map);
				// 		// out.add({ "key": , "value": });
				// 	}
				// }
			// }
		}

		return out;
	}

	private String cleanLine(String line) {
		// trim leading and trailing spaces
		line = line.trim();

		// clear spaces before and after line breaks and tabs
		line = line.replace("/ *([\n\r\t]) */gm", "$1");

		// make consecutive spaces one
		line = line.replace("/[ ]{2,}/g", " ");

		return line;
	}

	private String cleanSingleLine(String line) {
		// perform normal line cleaning
		line = cleanLine(line);

		// replace line breaks and tabs with spaces
		line = line.replace("/(\n|\r|\t)/g", " ");

		return line;
	}

	private void addToBuffer(final List<Map.Entry<String, String>> buffer, final String key, final String value) {
		// if (buffer.get(key) == null) {
		// buffer.put(key,new ArrayList<String>());
		// }
		// final List<String> ls = buffer.get(key)
		// ls.add(value);
		// buffer[key].push(value);
	}

	private List<String> tokenize(final String value, final String splitByRegex, final long limit) {
		long counter = 1;
		long i;
		String m;
		long start = 0;
		final List<String> tokens = new ArrayList<String>();

		// while ((m = value.split(splitByRegex)) != null) {
		// 	if (m.index == splitByRegex.lastIndex) {
		// 		splitByRegex.lastIndex++;
		// 	}

		// 	if (counter < limit) {
		// 		tokens.push(this.substring(start, m.index));
		// 		start = m.index + m[0].length;
		// 	}

		// 	counter++;
		// }

		// add the remainder as a single part
		// tokens.push(this.substring(start));

		// fill the array to match the limit if necessary
		// for (i = tokens.length; i < limit; i++) {
			// tokens.push("");
		// }

		return tokens;
	}

	class Section {
		public String line;
		public String doc;

		Section(String line, String doc) {
			this.line = line;
			this.doc = doc;
		}
	}

}