package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

import java.io.PrintWriter;

public class VisualizeToHTML {
	public void writeTag(PrintWriter f, String name, String tagClass) {
		f.println("<" +name + " class=" + tagClass + ">");
	}
	
	public void writeTagLinkCSS(PrintWriter f, String link, String classTag) {
		f.println("<link rel=\"stylesheet\" type=\"text/css\" href=" + link+ ">");
	}
	
	public void writeTagTd(PrintWriter f, String name, String tagClass) {
		f.println("<td class=" + tagClass + ">" + name +"</td>");
	}
	
	public void writeTagTh(PrintWriter f, String name, String tagClass) {
		f.println("<td class=" + tagClass + ">" + name +"</td>");
	}
}
