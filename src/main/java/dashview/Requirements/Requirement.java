package dashview.Requirements;

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

    public Requirement(final String key, final String parent, final Type type, final String category, final String title, final String description) {
        this.key = key;
        this.parent = parent;
        this.type = type;
        this.category = category;
        this.title = title;
        this.description = description;
    }
        
    // Without a default constructor, Jackson will throw an exception
    public Requirement(){}
    

    public String key(){
        return this.key;
    }

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
    public static String markdownHeader(){
        return "|Parent|Key|Category|Title|\n"+
        "|--|--|--|--|\n";
    }
    public String _toMarkdown(){
        return "|" + this.parent + "|" + this.key + "|" + this.category + "|" + this.title + "|\n||||" + this.description + "|\n";
    }
    @Override
    public String toString(){
        return "\nParent: " + parent + "\nKey: " + this.key + "\nType: " + this.type +  "\nCategory: " + this.category + "\nTitle: " + this.title + "\nDescription: " + this.description + "\n";
    }

	public Type type() {
		return this.type;
	}

  
}