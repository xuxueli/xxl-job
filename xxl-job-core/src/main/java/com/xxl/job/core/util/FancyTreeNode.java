package com.xxl.job.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FancyTreeNode implements Serializable{
	
	private String key; 
	private String parent;
	private String title;
	private boolean folder = false;
	private boolean selected;
	private boolean hasChildren = false;
	private int level = -1;
	private List<FancyTreeNode> children = new ArrayList<FancyTreeNode>();
	
	public FancyTreeNode()
	{
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<FancyTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<FancyTreeNode> children) {
		this.children = children;
	}
	
	
	

}
