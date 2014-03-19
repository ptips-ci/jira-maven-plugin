package com.george.plugins.jira.test.gherkin;

import java.util.LinkedList;
import java.util.List;

import com.george.plugins.jira.api.JiraIssue;

public abstract class Taggable {
	protected static final String EOL = System.getProperty("line.separator");
	protected List<String> tags;
	protected List<String> metaTags;
	protected List<String> componentAndLabelTags;
	
	public Taggable() {}
	
	/**
	 * Extracts useful tags from the issue: &#64;&lt;Key>, &#64;&lt;Status>, 
	 * 						&#64;type_&lt;issueType>, &#64;reporter_&lt;username>, &#64;assignee_&lt;username>, 
	 * 						&#64;&lt;Component>, &#64;&lt;Label>
	 * @param issue
	 */
	public Taggable( JiraIssue issue ) {
		tags = new LinkedList<String>();
		metaTags = new LinkedList<String>();
		componentAndLabelTags = new LinkedList<String>();
		
		tags.add( "@issue_" + issue.getKey() );
		tags.add( "@status_" + ensureValidTag( issue.getStatus() ) );	// Open/Resolved/Closed etc
		if( issue.getResolution() != null ) {							// Unresolved/Fixed/Wont_Fix etc
			tags.add( "@resolution_" + ensureValidTag( issue.getResolution() ) );
		}
		
		for( String component : issue.getComponents() ) {
			componentAndLabelTags.add( "@" + ensureValidTag( component ) );
		}
		
		for( String label : issue.getLabels() ) {
			componentAndLabelTags.add( "@" + label );
		}
		
//		issue.getFieldByName("issuelinks");
		
		metaTags.add( "@type_" + issue.getIssueType() );		// This might be useful for the reporter
		metaTags.add( "@reporter_" + issue.getReporter() );
		metaTags.add( "@assignee_" + issue.getAssignee() );
	}
	
	public abstract String formatForCucumber();
	
	protected String ensureValidTag( String tag ) {
		return tag.replaceAll("'", "").replaceAll("[^\\w\\d]+", "_") ;
	}
	
	protected StringBuilder getTags( String indent ) {
		StringBuilder str = new StringBuilder();
	
		// First write the tags for the Feature
		if( tags != null ) {
			if( indent != null ) { str.append(indent); }
			for( String tag : tags ) {
				str.append(tag).append(' ');
			}
			str.append(EOL);
			
			if( !componentAndLabelTags.isEmpty() ) {
				if( indent != null ) { str.append(indent); }
				for( String tag : componentAndLabelTags ) {
					str.append(tag).append(' ');
				}
				str.append(EOL);
			}
			
			if( indent != null ) { str.append(indent); }
			for( String tag : metaTags ) {
				str.append(tag).append(' ');
			}
			str.append(EOL);
		}
		
		return str;
	}
}
