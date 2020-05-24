package dashview.Requirements;
/** Specification of a single requirement */
public class Requirement{
    public enum Type {
        FUNCTIONAL,
        QUALITY,
        CONSTRAINT
    }
  
    private String key;
    private String parent;
    private Type type;
    private String category;
    private String title;
    private String description;
    private int importance;
    private int difficulty;
/**
 * Constructure for a single requiement
 * @param key Key used a an index to found requirement
 * @param parent Key of a parent requirement
 * @param type FUNCTIONAL, QUALITY, CONSTRAINT
 * @param category To class requirements by category
 * @param title Short description of the requirement
 * @param description Full description of the requirement
 * @param importance for all stakeholder
 * @param difficulty of realisation by developper and architect
 */
    public Requirement(final String key, 
    final String parent, 
    final Type type, 
    final String category, 
    final String title, 
    final String description,
    final int importance,
    final int difficulty) {
        this.key = key;
        this.parent = parent;
        this.type = type;
        this.category = category;
        this.title = title;
        this.description = description;
        this.importance = importance;
        this.difficulty = difficulty;
    }
        
    /** Without a default constructor, Jackson will throw an exception */
    public Requirement(){}
    
    /** Key accessor
     * @return Requirement key as a string
     */
    public String key(){
        return this.key;
    }

    /** type accessor that return a string matching 
     * @return String corresponding to a type
    */
    public String typeStr(){
        switch(this.type) {
            case FUNCTIONAL:
              return "Functional";
            case QUALITY:
               return "Quality";
            case CONSTRAINT:
              return "Constraint";
            default:
              return "";
          }
    }

    /**
     * compute priority based on importance and difficulty based on 3 level, low(1), medium(2), high(3)
     * @return priority value between 1..9, 9 is the highest priority
     */
    public int priority(){
        return importance * difficulty;
    }

    /** Markdown header of a requirement matching the _toMarkdown() function 
     * @return Header of a requirement in markdown
    */
    public static String markdownHeader(){
        return "|Parent|Key|Category|Title|Priority|\n"+
        "|--|--|--|--|--|\n";
    }

    /** a single requirement in Markdown format 
     * @return Requirement in markdown format
     */
    public String _toMarkdown(){
        return "|" + this.parent + "|" + this.key + "|" + this.category + "|" + this.title + "|" + this.priority() +  "|\n||||" + this.description + "|\n";
    }

    /** Requirement in string format
     * @return Requirement in string format
     */
    @Override
    public String toString(){
        return "\nParent: " + parent + "\nKey: " + this.key + "\nType: " + this.type +  "\nCategory: " + this.category + "\nTitle: " + this.title + "\nDescription: " + this.description + "\nPriority: " + this.priority() + "\n";
    }

    /** Requirement type accessor 
     * @return Requirement.Type
    */
	public Type type() {
		return this.type;
	}

    /** get Requirement key and title for short list of requirements 
     * @return {String} key + title
    */
	public String keyTitle() {
		return this.key + ", " + this.title;
	}

  
}