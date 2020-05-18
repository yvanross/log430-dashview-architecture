package dashview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param {string}   code the code that contains doc comments
     * @param {number}   headingsLevel the headings level to use as the base (1-6)
     * @returns {string} the Markdown documentation
     */
    public String fromDoc(String code, int headingsLevel) {

        // get all documentation sections from code
        List<String> sections = getSections(code);

        // initialize a string buffer
        List<String> out = new ArrayList<String>();

        out.add("#".repeat(headingsLevel) + " Documentation");

        for (int i = 0; i < sections.size(); i++) {
            out.add(fromSection(sections.get(i), headingsLevel));
        }

        // return the contents of the string buffer and add a trailing newline

        return String.join("", out) + "\n";
    }

    /**
     * Generates Markdown documentation from a statically typed language's doc
     * comments
     *
     * @param {string} code the code that contains doc comments
     * @param {number} headingsLevel the headings level to use as the base (1-6)
     * @returns {string} the Markdown documentation
     */
    public String fromStaticTypesDoc(String code, int headingsLevel) {
        return fromDoc(code, headingsLevel);
    }

    private void fnAddTagsMarkdown(Map tag, Map assocBuffer) {
        String tokens;
        
        switch (tag.key) {
            case "abstract": addToBuffer(assocBuffer, "Abstract", tag.value); break;
            case "access": addToBuffer(assocBuffer, "Access", tag.value); break;
            case "author": addToBuffer(assocBuffer, "Author", tag.value); break;
            case "constructor": addToBuffer(assocBuffer, "Constructor", null); break;
            case "copyright": addToBuffer(assocBuffer, "Copyright", tag.value); break;
            case "deprec":
            case "deprecated": addToBuffer(assocBuffer, "Deprecated", null); break;
            case "example": addToBuffer(assocBuffer, "Example", tag.value); break;
            case "exception":
            case "throws":
                tokens = tag.value.tokenize(/\s+/g, 2);
                addToBuffer(assocBuffer, "Exceptions", "`"+tokens[0]+"` — "+tokens[1]);
                break;
            case "exports": addToBuffer(assocBuffer, "Exports", tag.value); break;
            case "license": addToBuffer(assocBuffer, "License", tag.value); break;
            case "link": addToBuffer(assocBuffer, "Link", tag.value); break;
            case "name": addToBuffer(assocBuffer, "Alias", tag.value); break;
            case "package": addToBuffer(assocBuffer, "Package", tag.value); break;
            case "param":
                tokens = tag.value.tokenize(/\s+/g, 2);
                addToBuffer(assocBuffer, "Parameters", "`"+tokens[0]+"` — "+tokens[1]);
                break;
            case "private": addToBuffer(assocBuffer, "Private", null); break;
            case "return":
            case "returns": addToBuffer(assocBuffer, "Returns", tag.value); break;
            case "see": addToBuffer(assocBuffer, "See also", tag.value); break;
            case "since": addToBuffer(assocBuffer, "Since", tag.value); break;
            case "static": addToBuffer(assocBuffer, "Static", tag.value); break;
            case "subpackage": addToBuffer(assocBuffer, "Sub-package", tag.value); break;
            case "this": addToBuffer(assocBuffer, "This", "`"+tag.value+"`"); break;
            case "todo": addToBuffer(assocBuffer, "To-do", tag.value); break;
            case "version": addToBuffer(assocBuffer, "Version", tag.value); break;
            default: break;
        }
    }

    /**
	 * Generates Markdown documentation from Javadoc comments
	 *
	 * @param {string} code the code that contains doc comments
	 * @param {number} headingsLevel the headings level to use as the base (1-6)
	 * @returns {string} the Markdown documentation
	 */
	public String fromJavadoc(String code, int headingsLevel) {
		return fromStaticTypesDoc(String code, String headingsLevel);
    }

    /**
	 * Generates Markdown documentation from a given section
	 *
	 * The function processes units of documentation, a line of code with accompanying doc comment
	 *
	 * @param {object} section the section that consists of code line and doc comment
	 * @param {number} headingsLevel the headings level to use as the base (1-6)
	 * @param {function} fnAddTagsMarkdown the function that processes doc tags and generates the Markdown documentation
	 * @returns {string} the Markdown documentation
	 */
	private String fromSection(String section, int headingsLevel) {
		String assocBuffer;
		String description;
		String field;
		List<String>  out = new ArrayList<String>();
		String p;
		String t;
		String tags;


		// first get the field that we want to describe
		field = getFieldDeclaration(section.line);
		// if there is no field to describe
		if (!field) {
			// do not return any documentation
			return "";
		}

		out.add("\n\n");
		out.add("#".repeat(headingsLevel+1)+" `"+field+"`");

		// split the doc comment into main description and tag section
		var docCommentParts = section.doc.split("/^(?:\t| )*?\*(?:\t| )*?(?=@)/m");
		// get the main description (which may be an empty string)
		var rawMainDescription = docCommentParts.shift();
		// get the tag section (which may be an empty array)
		var rawTags = docCommentParts;

		description = getDocDescription(rawMainDescription);
		if (description.length) {
			out.add("\n\n");
			out.add(description);
		}

		tags = getDocTags(rawTags);
		if (tags.length) {
			out.add("\n");

			assocBuffer = {};
			for (t = 0; t < tags.length; t++) {
				fnAddTagsMarkdown(tags[t], assocBuffer);
			}

			for (p : assocBuffer) {
				if (assocBuffer.hasOwnProperty(p)) {
					out.add(fromTagGroup(p, assocBuffer[p]));
				}
			}
		}

		// return the contents of the string buffer
		return out.join("");
	}

    private String fromTagGroup(String name, String entries) {
		long i;
		List<String> out= new ArrayList<String>();

		// initialize a string buffer
		out = [];

		out.add("\n");
		if (entries.length == 1 && entries[0] == null) {
			out.add(" * **"+name+"**");
		}
		else {
			out.add(" * **"+name+":**");
			if (entries.length > 1) {
				for (i = 0; i < entries.length; i++) {
					out.add("\n");
					out.add("   * "+entries[i]);
				}
			}
			else if (entries.length === 1) {
				out.add(" "+entries[0]);
			}
		}

		// return the contents of the string buffer
		return String.join("",out);
	}

    private List<String>  getSections(String code) {
		String docLine;
		String fieldDeclaration;
		String m;
		List<String> out = new ArrayList<String>();
		// String regex;

		regex = "/\/\*\*([^]*?)\*\/([^{;/]+)/gm";

		while ((m = regex.exec(code)) != null) {
			if (m.index == regex.lastIndex) {
				regex.lastIndex++;
			}

			if ( m[1] instanceof String && m[1] != null) {
				if ( m[2] instanceof String && m[2] != null) {
					fieldDeclaration = m[2].trim();
					docLine = m[1];

					// if the source code line is an import statement
					if (/^import\s+/.test(fieldDeclaration)) {
						// ignore this piece
						continue;
					}

					// if this is a single line comment
					if (docLine.indexOf("*") === -1) {
						// prepend an asterisk to achieve the normal line structure
						docLine = "*"+docLine;
					}

					// interpret empty lines as if they contained a p-tag
					docLine = docLine.replace(/\*[ ]*$/gm, "* <p>");

					out.add({ "line": fieldDeclaration, "doc": docLine });
				}
			}
		}

		return out;
	}

    private String  getFieldDeclaration(String line) {
		String regex = "/^([^\{;]+)(.*?)$/gm";
		String m;

		while ((m = regex.exec(line)) !== null) {
			if (m.index == regex.lastIndex) {
				regex.lastIndex++;
			}

			if ( m[1] instanceof String && m[1] != null) {
				return cleanSingleLine(m[1]);
			}
		}

		return "";
	}

    private String replaceHTMLWithMarkdown(String html) {
		return html.replace(/<\s*?code\s*?>(.*?)<\s*?\/\s*?code\s*?>/g, "`$1`");
	}

    private String  getDocDescription(String docLines) {
		String regex = "/^(\t| )*?\*(\t| )+(.*?)$/gm";
		String m;
		List<String> out = new ArrayList<String>();

		while ((m = regex.exec(docLines)) != null) {
			if (m.index == regex.lastIndex) {
				regex.lastIndex++;
			}

			if (m[3] instanceof String && m[3] != null) {
				m[3] = cleanLine(m[3]);
				m[3] = replaceHTMLWithMarkdown(m[3]);
				out.add(m[3]);
			}
		}

		return cleanLine(String.join(" ",out).replace(/<(\/)?p>/gi, "\n\n"));
	}

    private List<String> getDocTags(String docLines) {
		String regex = "/^(?:\t| )*?@([a-zA-Z]+)([\s\S]*)/";
		String m;
		List<String> out = new ArrayList<String>();

		for (var i = 0; i < docLines.length; i++) {
			m = regex.exec(docLines[i]);

			if (m != null) {
				if ( m[1] instanceof String && m[1] != null) {
					if ( m[2] instanceof  && m[2] != null) {
						// trim leading and trailing space in the tag value
						m[2] = m[2].trim();
						// format multi-line tag values correctly
						m[2] = m[2].split(/[\r\n]{1,2}(?:\t| )*?\*(?:\t| )*/).join("\n\n     ");

						// add the key and value for this tag to the output
						out.add({ "key": cleanSingleLine(m[1]), "value": m[2] });
					}
				}
			}
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

    private void addToBuffer(ArrayList<List<String>> buffer, int key, String value) {
        if (buffer[key] == null) {
            buffer[key] =new List<String>();
        }
        buffer[key].push(value);
    }

    private List<String> tokenize(String splitByRegex, long limit) {
        long counter = 1;
        long	i;
		String m;
		long  start = 0;
		List<String> tokens = new ArrayList<String>();

		while ((m = splitByRegex.exec(this)) !== null) {
			if (m.index == splitByRegex.lastIndex) {
				splitByRegex.lastIndex++;
			}

			if (counter < limit) {
				tokens.push(this.substring(start, m.index));
				start = m.index + m[0].length;
			}

			counter++;
		}

		// add the remainder as a single part
		tokens.push(this.substring(start));

		// fill the array to match the limit if necessary
		for (i = tokens.length; i < limit; i++) {
			tokens.push("");
		}

		return tokens;
    }
    
     class Tag(String key, String value){
        public String key;
        public string value;
        Tag(String key, String value){
            this.key = key;
            this.value= value;
        }
    }
}